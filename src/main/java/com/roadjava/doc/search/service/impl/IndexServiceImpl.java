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
        // ????????????
        ResultDTO<DocumentDTO> parseResult = parserContext.parse(fileDO);
        if (!parseResult.getSuccess()) {
            // ????????????
            return ResultDTO.buildFailure(parseResult.getErrMsg());
        }
        DocumentDTO dto = parseResult.getData();
        Document document = buildDoc(dto);
        try {
            indexWriter.addDocument(document);
            // ???????????????????????????????????????reader????????????,???searchManager??????
//            indexWriter.commit();
            log.info("??????????????????,?????????:{}", dto.getFileName());
            return ResultDTO.buildSuccess("??????????????????");
        } catch (IOException e) {
//            try {
//                indexWriter.rollback();
//            } catch (IOException ex) {
//                log.error("addDocument????????????????????????",e);
//            }
            log.error("??????????????????,????????????:{}", fileDO,e);
        }
        return ResultDTO.buildFailure("??????????????????");
    }

    @Override
    public void delete(List<String> pathsToDelete) {
        for (String fileId : pathsToDelete) {
            try {
                indexWriter.deleteDocuments(new Term(FIELD_PATH,fileId));
//            indexWriter.forceMergeDeletes();
//            indexWriter.commit();
                log.info("??????????????????,????????????:{}",fileId);
            }catch (Exception e) {
//            try {
//                indexWriter.rollback();
//            } catch (IOException ex) {
//                log.error("deleteDocuments????????????????????????",e);
//            }
                log.error("??????????????????,????????????:{}",fileId,e);
            }
        }

    }

    @Override
    public ResultDTO<List<FileVO>> search(SearchRequest request) {
        QueryParser queryParser = buildQueryParser(request);
        IndexSearcher indexSearcher = indexSearchManager.acquire();
        if (indexSearcher == null) {
            return ResultDTO.buildFailure("????????????");
        }
        try {
            Query query = queryParser.parse(request.getSearchWord());
            TopDocs topDocs = indexSearcher.search(query, luceneProperties.getLimit());
            log.info("query??????:{},????????????:{}?????????",query.toString(),topDocs.totalHits);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            // ??????
            QueryScorer queryScorer = new QueryScorer(query);
            // ?????????
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
                // ??????????????????
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
            log.error("????????????",e);
            return ResultDTO.buildFailure("????????????");
        } finally {
           indexSearchManager.release(indexSearcher);
        }
    }

    /**
     * ?????????????????????
     * @param request
     * @return
     */
    private QueryParser buildQueryParser(SearchRequest request) {
        String searchField = request.getSearchField();
        QueryParser queryParser = new QueryParser(searchField, analyzer);
        // ????????????and?????????
        queryParser.setDefaultOperator(QueryParser.Operator.AND);
        // ?????????????????????????????????
        queryParser.setAllowLeadingWildcard(true);
        return queryParser;
    }

    /**
     * dto??????lucene???document??????
     * @param dto
     * @return
     */
    private Document buildDoc(DocumentDTO dto) {
        Document document = new Document();
        // ???????????????,??????????????????,??????????????????,???ha3?????????,?????????????????????
        // ????????????StringField
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
