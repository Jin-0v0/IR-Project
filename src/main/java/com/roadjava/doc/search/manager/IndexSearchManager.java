package com.roadjava.doc.search.manager;

import com.roadjava.doc.search.config.LuceneProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 封装searcher管理
 * @author zhaodaowen
 */
@Component
@Slf4j
public class IndexSearchManager {
    @Resource
    private SearcherManager searcherManager;
    @Resource
    private LuceneProperties luceneProperties;

    public IndexSearcher acquire() {
        IndexSearcher indexSearcher = null;
        try {
            // 采用nrt搜索时不再需要每次调用maybeRefresh,损耗性能
            if (!luceneProperties.getUseNRT()) {
                searcherManager.maybeRefresh();
            }
            indexSearcher = searcherManager.acquire();
        }catch (Exception e){
            log.error("获取IndexSearcher出错",e);
        }
        return indexSearcher;
    }
    public void release(IndexSearcher indexSearcher) {
        try {
            searcherManager.release(indexSearcher);
        } catch (IOException e) {
            log.error("释放indexSearcher出错",e);
        }
    }
}
