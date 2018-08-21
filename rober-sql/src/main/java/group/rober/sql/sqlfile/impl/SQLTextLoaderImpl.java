package group.rober.sql.sqlfile.impl;

import group.rober.runtime.kit.IOKit;
import group.rober.runtime.kit.StringKit;
import group.rober.runtime.lang.RoberException;
import group.rober.sql.sqlfile.SQLCollecter;
import group.rober.sql.sqlfile.SQLTextLoader;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

public class SQLTextLoaderImpl implements SQLTextLoader {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public SQLCollecter parse(String... resource){
        Map<String,String> textMap = new HashMap<String,String>();

        for(String res : resource){
//            if(!res.startsWith("classpath:")
//                    &&!res.startsWith("/")
//                    &&!res.startsWith("file:")
//                    ){
//                res = "classpath:"+res;
//            }
            String text = null;
            InputStream inputStream = null;
            try {
                URL url = ResourceUtils.getURL(res);
                inputStream = url.openStream();
                text = IOKit.toString(inputStream, Charset.defaultCharset());
            } catch (FileNotFoundException e) {
                logger.warn("SQL FILE LOAD ERROR",e);
            } catch (IOException e) {
                logger.warn("OPEN SQL FILE ERROR",e);
            } finally {
                IOKit.close(inputStream);
            }
            if(StringKit.isBlank(text))continue;

            List<SQLTextItem> textList = parseText(text);
            textList.forEach(item->{
                if(textMap.containsKey(item.name)){
                    throw new RoberException("SQL查询项["+item.name+"]在加载的SQL资源文件中重复");
                }
                textMap.put(item.name,item.sql);
            });
        }

        //构建SQL文本集合器
        SQLCollecter collecter = new SQLCollecter(textMap);
        return collecter;
    }
    private List<SQLTextItem> parseText(String text){
        List<SQLTextItem> textList = new ArrayList<SQLTextItem>();

        Parser parser = Parser.builder().build();
        Node node = parser.parse(text);

        Node curNode = node.getFirstChild();
        SQLTextItem sqlTextItem = null;
        while(curNode != null){
            if(sqlTextItem == null) sqlTextItem = new SQLTextItem();

            if(curNode instanceof Heading){                 //标题头部-->SQL查询KEY
                Heading heading = (Heading)curNode;
                SQLTextItem tmpItem = new SQLTextItem();
                heading.accept(new AbstractVisitor() {
                    public void visit(Text text) {
                        tmpItem.name = text.getLiteral();
                    }
                });
                if(StringKit.isBlank(sqlTextItem.name)){
                    sqlTextItem.name = tmpItem.name;
                }
            }else if(curNode instanceof FencedCodeBlock){   //SQL文本内容部
                FencedCodeBlock codeBlock = (FencedCodeBlock)curNode;
                if(StringKit.isBlank(sqlTextItem.sql)){
                    sqlTextItem.sql = codeBlock.getLiteral();
                }
            }else{                                          //其他的说明文字
                List<String> texts = new ArrayList<String>();
                curNode.accept(new AbstractVisitor() {
                    public void visit(Text text) {
                        texts.add(text.getLiteral());
                    }
                });
                sqlTextItem.intro = StringKit.join(texts,",");
            }

            //凑齐了，加到列表中，并且就新创建一个实例
            if(StringKit.isNoneBlank(sqlTextItem.name)&&StringKit.isNoneBlank(sqlTextItem.sql)){
                textList.add(sqlTextItem);
                sqlTextItem = new SQLTextItem();
            }

            curNode = curNode.getNext();
        }



        return textList;
    }
    private class SQLTextItem{
        private String name;
        private String intro;
        private String sql;
    }
}
