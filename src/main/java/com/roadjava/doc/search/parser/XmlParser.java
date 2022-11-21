package com.roadjava.doc.search.parser;

import com.roadjava.doc.search.bean.enums.FileTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentHelper;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.io.FileInputStream;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class XmlParser extends Parser{

    public static String getXmlAttrValue(String xml,String attrName) throws Exception {
        if (null==xml||xml.equals("")||null==attrName||attrName.equals("")){
            return null;
        }
        String[] attrs = attrName.split("\\.");
        int length = attrs.length;
        String result = null;
        // 将xml格式字符串转化为DOM对象
        org.dom4j.Document document = DocumentHelper.parseText(xml);
        // 获取根结点对象
        org.dom4j.Element element = document.getRootElement();
        List<org.dom4j.Element> elements = Arrays.asList(element);
        for (int i = 0; i < length; i++) {
            Map<String, org.dom4j.Element> elementMap = elements.stream().collect(Collectors.toMap(e->e.getName(), e->e));
            if (elementMap.containsKey(attrs[i])){
                if (i==length-1){
                    result = elementMap.get(attrs[i]).asXML();
                    break;
                }else {
                    elements = elementMap.get(attrs[i]).elements();
                }
            }else {
                throw new Exception("Node does not exist:"+attrName);
            }
        }
        return result;
    }

    @Override
    protected String getFileCont(File file) throws Exception{
//        //创建解析器对象
//        SAXReader saxReader=new SAXReader();
//        //根据user.xml文档生成Document对象
//        File f = new File("src/main/resources/demo.xml");
        FileInputStream in = new FileInputStream(file);

        byte[] b = new byte[(int)file.length()];
        in.read(b);
        String xml = new String(b);
        String result = getXmlAttrValue(xml,"TEI");
        return result;
    }

    @Override
    protected String getFileDate(File file) throws Exception{
        FileInputStream in = new FileInputStream(file);

        byte[] b = new byte[(int)file.length()];
        in.read(b);
        String xml = new String(b);
        String result = getXmlAttrValue(xml,"TEI.teiHeader.fileDesc.publicationStmt.date");
        return result;
    }

    @Override
    protected String getFileTitl(File file) throws Exception{
        FileInputStream in = new FileInputStream(file);

        byte[] b = new byte[(int)file.length()];
        in.read(b);
        String xml = new String(b);
        String result = getXmlAttrValue(xml,"TEI.teiHeader.fileDesc.titleStmt.title");
        return result;
    }

    @Override
    protected String getFileWrit(File file) throws Exception{
        FileInputStream in = new FileInputStream(file);

        byte[] b = new byte[(int)file.length()];
        in.read(b);
        String xml = new String(b);
        String result = getXmlAttrValue(xml,"TEI.teiHeader.fileDesc.sourceDesc.biblStruct.analytic");
        return result;
    }

    @Override
    protected String getFileAffi(File file) throws Exception{
        FileInputStream in = new FileInputStream(file);

        byte[] b = new byte[(int)file.length()];
        in.read(b);
        String xml = new String(b);
        String result = getXmlAttrValue(xml,"TEI.teiHeader.fileDesc.sourceDesc.biblStruct.analytic");
        return result;
    }

    @Override
    protected String getFileAddr(File file) throws Exception{
        FileInputStream in = new FileInputStream(file);

        byte[] b = new byte[(int)file.length()];
        in.read(b);
        String xml = new String(b);
        String result = getXmlAttrValue(xml,"TEI.teiHeader.fileDesc.sourceDesc.biblStruct.analytic.idno");
        return result;
    }

    @Override
    public String getHandleType() {
        return FileTypeEnum.XML.getTypeName();
    }
}

