package com.roadjava.doc.search.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.ControlledRealTimeReopenThread;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.FSDirectory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
@Configuration
@EnableConfigurationProperties(LuceneProperties.class)
@Slf4j
public class LuceneConfiguration {
    @Resource
    private LuceneProperties luceneProperties;

    private FSDirectory fsDirectory;

    @PostConstruct
    public void init() {
        String dir = luceneProperties.getDir();
        try {
            fsDirectory = FSDirectory.open(Paths.get(dir));
        } catch (IOException e) {
            log.error("打开索引存放目录{}失败", dir, e);
            throw new RuntimeException("打开索引存放目录失败");
        }
    }

    @Bean
    public Analyzer analyzer() {
        return new IKAnalyzer(false);
    }

    @Bean
    public IndexWriter indexWriter(Analyzer analyzer) {
        IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
        // 刷新策略FlushPolicy用到
        writerConfig.setRAMBufferSizeMB(IndexWriterConfig.DEFAULT_RAM_BUFFER_SIZE_MB);
        try {
            return new IndexWriter(fsDirectory,writerConfig);
        } catch (IOException e) {
            log.error("创建IndexWriter失败", e);
            throw new RuntimeException("创建IndexWriter失败");
        }
    }

    /**
     * indexWriter在addDocument/update/delete
     * [这些方法会放入DocumentsWriterFlushControl,该类会根据刷新策略定时把
     * 新的文档放入到硬盘中]后,带来的结果是:
     *    文档在硬盘中的索引目录有了,这没问题。
     * 但因没显式调用close(close()前会调用commit)或commit,
     * [commit的作用是同步磁盘索引到内存,让reader可以查询到最新的]
     * 那么IndexSearcher怎么从内存中获取最新的与硬盘上同步的最新的索引呢？
     * 一、原始写法:
     * // 每次查询时重新获取,从索引目录加载全部的文档,耗费资源
     * DirectoryReader ir = DirectoryReader.open(fsDirectory);
     * IndexSearcher is = new IndexSearcher(ir);
     * 二、优化写法:
     * DirectoryReader directoryReader = DirectoryReader.open(fsDirectory);
     * IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
     * //只是重新加载被更新过的文档
     * IndexReader newReader = DirectoryReader.openIfChanged(directoryReader);
     * if (directoryReader != newReader) {
     *   indexSearcher = new IndexSearcher(newReader);
     *   directoryReader.close();
     * }
     * 三、SearcherManager统一管理searcher写法:
     * // 底层调用DirectoryReader.openIfChanged(directoryReader);
     * searcherManager.maybeRefresh();
     * indexSearcher = searcherManager.acquire();
     * +
     * searcherManager.release(indexSearcher);
     * 四、后台守护线程
     * 尽管在每次获取indexsearcher进行search前调用maybeRefresh只是加载被更新过的文档,
     * 但真正要更新时还是会影响到查询的性能，因此建议使用后台守护线程来定时调用maybeRefresh
     */
    @Bean
    public SearcherManager searcherManager(IndexWriter indexWriter) throws IOException {
        /*
        applyAllDeletes:true IndexSearcher/DirectoryReader从硬盘同步的索引后，删除的默认
                        不应用[还能搜索到]
        writeAllDeletes: 在DocumentsWriterFlushControl中控制,这里不控制,故写为false
         */
        SearcherManager searcherManager = new SearcherManager(indexWriter,
        true, false, new SearcherFactory());

        useNRT(indexWriter,searcherManager);
        return searcherManager;
    }

    /**
     * 采用近实时(near real time)搜索:
     *  相比在每次获取searcher前调用maybeRefresh,这种方式更高效，但可能存在配置时间上的延迟
     *  ,如下配置表示：最大延迟5s
     * @param indexWriter
     * @param searcherManager
     */
    private void useNRT(IndexWriter indexWriter, SearcherManager searcherManager) {
        if (luceneProperties.getUseNRT()) {
            ControlledRealTimeReopenThread cRTReopenThead = new
                    ControlledRealTimeReopenThread(indexWriter, searcherManager
                    , 5.0, 0.025);
            cRTReopenThead.setDaemon(true);
            //线程名称
            cRTReopenThead.setName("更新IndexReader线程");
            // 开启线程
            cRTReopenThead.start();
        }
    }
}
