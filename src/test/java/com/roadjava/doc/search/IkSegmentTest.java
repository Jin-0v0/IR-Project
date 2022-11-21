package com.roadjava.doc.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 *
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
public class IkSegmentTest {
    /**
     * 测试ik分词器分出来的结果
     */
    @Test
    public void testIkSegment() throws Exception{
        String str = "好的去哪儿啊";
        Analyzer analyzer = new IKAnalyzer(false);
        // token(令牌)==term
        TokenStream tokenStream = analyzer.tokenStream("xxx", str);
        CharTermAttribute termAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()) {
            System.out.println(termAttribute);
        }
        tokenStream.end();
        tokenStream.close();
    }

}
