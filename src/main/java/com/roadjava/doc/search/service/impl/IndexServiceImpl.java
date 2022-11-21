package com.roadjava.doc.search.service.impl;

import com.roadjava.doc.search.bean.dto.DocumentDTO;
import com.roadjava.doc.search.bean.entity.FileDO;
import com.roadjava.doc.search.bean.enums.FileDocFieldEnum;
import com.roadjava.doc.search.bean.req.SearchRequest;
import com.roadjava.doc.search.bean.res.ResultDTO;
import com.roadjava.doc.search.bean.vo.FileVO;
import com.roadjava.doc.search.config.LuceneProperties;
import com.roadjava.doc.search.manager.IndexSearchManager;
import com.roadjava.doc.search.parser.ctx.ParserContext;
import com.roadjava.doc.search.service.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class IndexServiceImpl implements IndexService {
    @Resource
    private IndexWriter indexWriter;
    @Resource
    private Analyzer analyzer;
    @Resource
    private LuceneProperties luceneProperties;
    @Resource
    private IndexSearchManager indexSearchManager;
    @Resource
    private ParserContext parserContext;


    private static final String FIELD_NAME = FileDocFieldEnum.NAME.getName();
    private static final String FIELD_CONT = FileDocFieldEnum.CONT.getName();
    private static final String FIELD_PATH = FileDocFieldEnum.RELATIVE_PATH.getName();
    private static final String FIELD_TITL = FileDocFieldEnum.TITL.getName();
    private static final String FIELD_DATE = FileDocFieldEnum.DATE.getName();
    private static final String FIELD_WRIT = FileDocFieldEnum.WRIT.getName();
    private static final String FIELD_AFFI = FileDocFieldEnum.AFFI.getName();
    private static final String FIELD_ADDR = FileDocFieldEnum.ADDR.getName();

    @Override
    public ResultDTO<String> write(FileDO fileDO) {
        // 更新索引
        ResultDTO<DocumentDTO> parseResult = parserContext.parse(fileDO);
        if (!parseResult.getSuccess()) {
            // 解析出错
            return ResultDTO.buildFailure(parseResult.getErrMsg());
        }
        DocumentDTO dto = parseResult.getData();
        Document document = buildDoc(dto);
        try {
            indexWriter.addDocument(document);
            // 不必每次手动同步磁盘索引到reader的内存中,由searchManager控制
//            indexWriter.commit();
            log.info("写入索引成功,文献名:{}", dto.getFileName());
            return ResultDTO.buildSuccess("写入索引成功");
        } catch (IOException e) {
//            try {
//                indexWriter.rollback();
//            } catch (IOException ex) {
//                log.error("addDocument回滚内存索引出错",e);
//            }
            log.error("写入索引出错,写入内容:{}", fileDO,e);
        }
        return ResultDTO.buildFailure("写入索引出错");
    }

    @Override
    public void delete(List<String> pathsToDelete) {
        for (String fileId : pathsToDelete) {
            try {
                indexWriter.deleteDocuments(new Term(FIELD_PATH,fileId));
//            indexWriter.forceMergeDeletes();
//            indexWriter.commit();
                log.info("删除文献成功,文献标识:{}",fileId);
            }catch (Exception e) {
//            try {
//                indexWriter.rollback();
//            } catch (IOException ex) {
//                log.error("deleteDocuments回滚内存索引出错",e);
//            }
                log.error("删除索引出错,文件标识:{}",fileId,e);
            }
        }

    }

    @Override
    public ResultDTO<List<FileVO>> search(SearchRequest request) {
        QueryParser queryParser = buildQueryParser(request);
        IndexSearcher indexSearcher = indexSearchManager.acquire();
        if (indexSearcher == null) {
            return ResultDTO.buildFailure("搜索出错");
        }
        try {
            Query query = queryParser.parse(request.getSearchWord());
            TopDocs topDocs = indexSearcher.search(query, luceneProperties.getLimit());
            log.info("query语句:{},共搜索到:{}条文档",query.toString(),topDocs.totalHits);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            // 高亮
            QueryScorer queryScorer = new QueryScorer(query);
            // 获取段
            SimpleSpanFragmenter fragmenter = new SimpleSpanFragmenter(queryScorer,luceneProperties.getSummaryLength());
            SimpleHTMLFormatter formatter = new
                    SimpleHTMLFormatter(luceneProperties.getPreTag(),luceneProperties.getPostTag());
            Highlighter highlighter = new Highlighter(formatter,queryScorer);
            highlighter.setTextFragmenter(fragmenter);
            List<FileVO> list = new ArrayList<>();
            for (ScoreDoc scoreDoc :scoreDocs) {
                int docId = scoreDoc.doc;
                Document document = indexSearcher.doc(docId);
                String fileName = document.get(FIELD_NAME);
                String fileContent = document.get(FIELD_CONT);
                String path = document.get(FIELD_PATH);
                String fileWriter = document.get(FIELD_WRIT);
                String fileDate = document.get(FIELD_DATE);
                String fileAffiliation = document.get(FIELD_AFFI);
                String fileAddress = document.get(FIELD_ADDR);
                String fileTitle = document.get(FIELD_TITL);
                // 获取最佳摘要
                String bestFragment;
                String searchField = request.getSearchField();
                if (FileDocFieldEnum.NAME.getName().equals(searchField)) {
                    bestFragment = highlighter.getBestFragment(analyzer, searchField, fileName);
                }
                else if(FileDocFieldEnum.CONT.getName().equals(searchField)) {
                    bestFragment = highlighter.getBestFragment(analyzer, searchField, fileContent);
                }
                else if(FileDocFieldEnum.TITL.getName().equals(searchField)){
                    bestFragment = highlighter.getBestFragment(analyzer, searchField, fileTitle);
                }
                else if(FileDocFieldEnum.DATE.getName().equals(searchField)){
                    bestFragment = highlighter.getBestFragment(analyzer, searchField, fileDate);
                }
                else if(FileDocFieldEnum.WRIT.getName().equals(searchField)){
                    bestFragment = highlighter.getBestFragment(analyzer, searchField, fileWriter);
                }
                else if(FileDocFieldEnum.ADDR.getName().equals(searchField)){
                    bestFragment = highlighter.getBestFragment(analyzer, searchField, fileAddress);
                }
                else {
                    bestFragment = highlighter.getBestFragment(analyzer, searchField, fileAffiliation);
                }
                FileVO vo = new FileVO();
                vo.setOriginalFileName(fileName);
                vo.setSummary(bestFragment);
                vo.setStoreRelativePath(path);
                list.add(vo);
            }
            return ResultDTO.buildSuccess(list, (long) topDocs.totalHits);
        } catch (Exception e) {
            log.error("搜索出错",e);
            return ResultDTO.buildFailure("搜索出错");
        } finally {
           indexSearchManager.release(indexSearcher);
        }
    }

    /**
     * 获取查询解析器
     * @param request
     * @return
     */
    private QueryParser buildQueryParser(SearchRequest request) {
        String searchField = request.getSearchField();
        QueryParser queryParser = new QueryParser(searchField, analyzer);
        // 空格当做and来处理
        queryParser.setDefaultOperator(QueryParser.Operator.AND);
        // 通配符允许出现在第一位
        queryParser.setAllowLeadingWildcard(true);
        return queryParser;
    }

    /**
     * dto转为lucene的document对象
     * @param dto
     * @return
     */
    private Document buildDoc(DocumentDTO dto) {
        Document document = new Document();
        // 因为存储了,即便有停用词,也可完全还原,跟ha3不一样,不需要再建一个
        // 不分词的StringField
        document.add(new TextField(FIELD_NAME,dto.getFileName(), Field.Store.YES));
        document.add(new TextField(FIELD_CONT,dto.getFileContent(), Field.Store.YES));
        document.add(new StringField(FIELD_PATH,dto.getStoreRelativePath(), Field.Store.YES));
        document.add(new TextField(FIELD_TITL,dto.getFileTitle(), Field.Store.YES));
        document.add(new TextField(FIELD_DATE,dto.getFileDate(), Field.Store.YES));
        document.add(new TextField(FIELD_WRIT,dto.getFileWriter(), Field.Store.YES));
        document.add(new TextField(FIELD_AFFI,dto.getFileAffiliation(), Field.Store.YES));
        document.add(new TextField(FIELD_ADDR,dto.getFileAddress(), Field.Store.YES));
        return document;
    }
}
