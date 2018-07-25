package group.rober.runtime.kit;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class BsonKit {

    public static <T> Document bean2Document(T object){
        Map<String,Object> map = BeanKit.bean2Map(object);
        Document document = new Document(map);
        return document;
    }

    public static <T> List<Document> bean2DocumentList(List<T> objects){
        ValidateKit.notEmpty(objects);

        List<Document> documents = new ArrayList<Document>(objects.size());
        objects.forEach(o -> {
            documents.add(bean2Document(o));
        });
        return documents;
    }
}
