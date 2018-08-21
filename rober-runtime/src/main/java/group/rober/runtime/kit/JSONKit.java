package group.rober.runtime.kit;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by tisir yangsong158@qq.com on 2017-02-18
 */
public abstract class JSONKit {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static{
        SimpleDateFormat sdf = new SimpleDateFormat(DateKit.DATE_TIME_MS_FORMAT);
        OBJECT_MAPPER.setDateFormat(sdf);
    }

    static {
        // configure feature

    }


    public static synchronized String toJsonString(Object object,boolean prettify){
        if(prettify){
            OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("convert object to json string error", ex);
        } finally {
            if(prettify){
                OBJECT_MAPPER.disable(SerializationFeature.INDENT_OUTPUT);
            }
        }

    }
    public static String toJsonString(Object object){
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("convert object to json string error", ex);
        }
    }


    public static <T>T jsonToBean(String text,Class<T> classType){
        try {
            return OBJECT_MAPPER.readValue(text, classType);
        }catch (IOException ex) {
            throw new RuntimeException("convert json string to object error", ex);
        }
    }

    public static <T> List<T> jsonToBeanList(String text, Class<T> classType){
        try {
            return OBJECT_MAPPER.readValue(text, OBJECT_MAPPER.getTypeFactory()
                    .constructCollectionType(List.class, classType));
        } catch (IOException ex) {
            throw new RuntimeException("convert json string to list object error", ex);
        }
    }
}
