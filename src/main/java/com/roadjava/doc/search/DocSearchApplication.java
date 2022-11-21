package com.roadjava.doc.search;

import org.apache.lucene.index.IndexWriter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;

/**
 *
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableTransactionManagement
@MapperScan(basePackages = {"com.roadjava.doc.search.mapper"})
public class DocSearchApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(DocSearchApplication.class, args);
        Runtime.getRuntime().addShutdownHook(new Thread(() ->{
            IndexWriter indexWriter = run.getBean(IndexWriter.class);
            try {
                // 程序关闭时把内存buffer中的改变写到disk中,避免关闭程序时磁盘索引与内存不一致
                indexWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            run.close();
        }));
    }
}
