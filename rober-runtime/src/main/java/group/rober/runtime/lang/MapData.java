package group.rober.runtime.lang;

import group.rober.runtime.kit.BeanKit;
import group.rober.runtime.kit.JSONKit;
import group.rober.runtime.kit.MapKit;
import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathNotFoundException;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.lang3.Validate;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.Locale;
import java.util.Map;

/**
 * Created by tisir yangsong158@qq.com on 2017-05-19
 */
public class MapData extends LinkedCaseInsensitiveMap<Object> {

    private static final long serialVersionUID = -8545051570671246562L;

    JXPathContext context = null;

    public MapData() {
    }

    public static <V> MapData valueOf(Map<String,?> value){
        MapData object = new MapData();
        object.putAll(value);
        return object;
    }

    public static <V> MapData valueOf(String k1,V v1){
        MapData object = new MapData();
        object.putAll(MapKit.mapOf(k1,v1));
        return object;
    }

    public static <V> MapData valueOf(String k1,V v1,String k2,V v2){
        MapData object = new MapData();
        object.putAll(MapKit.mapOf(k1,v1,k2,v2));
        return object;
    }
    public static <V> MapData valueOf(String k1,V v1,String k2,V v2,String k3,V v3){
        MapData object = new MapData();
        object.putAll(MapKit.mapOf(k1,v1,k2,v2,k3,v3));
        return object;
    }

    public MapData(Locale locale) {
        super(locale);
    }

    public MapData(int initialCapacity) {
        super(initialCapacity);
    }

    public MapData(int initialCapacity, Locale locale) {
        super(initialCapacity, locale);
    }

    public static MapData build(Map<String,?> mapData){
        MapData dataObject = new MapData();
        dataObject.putAll(mapData);
        return dataObject;
    }
    public static MapData buildFromBean(Object bean){
        MapData dataObject = new MapData();
        dataObject.putFromBean(bean);
        return dataObject;
    }

    protected void toggleInit(){
        if(context==null){
            context = JXPathContext.newContext(this);
            //xpath路径中,自动创建对象使用的factory
            context.setFactory(getCreateObjectFactory());
        }
    }

    /**
     * 把JavaBean转为DataBox
     *
     * @param bean  bean
     * @return map
     */
    public MapData putFromBean(Object bean) {
        Map<String, Object> data = BeanKit.bean2Map(bean);
        this.putAll(data);
        return this;
    }

    /**
     * 将DataBox转换为JavaBean
     *
     * @param classType classType
     * @param <T> T
     * @return toBean
     */
    public <T> T toBean(Class<T> classType){
        return BeanKit.map2Bean(this,classType);
    }

    public Map<String,Object> toMap(){
        return (Map<String,Object>)this;
    }

    protected AbstractFactory getCreateObjectFactory(){
        return new AbstractFactory(){
            public boolean createObject(JXPathContext context, Pointer pointer,
                                        Object parent, String name, int index){
                if (parent instanceof MapData){
                    MapData dtx = (MapData)parent;
                    dtx.put(name,new MapData());
                    return true;
                }else{
                    return false;
                }
            }
        };
    }

    protected String xpath(String p){
        if(p==null)return null;
        return p.replaceAll("\\.","/");
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(String expression, boolean strict, Class<T> classType){
        Object object = getObject(expression,strict);
        if(object==null)return null;
        if(classType.isAssignableFrom(object.getClass())){
            T ret = (T)object;
            //如果获取一个结果是Map类型,但是目标类型是DataBox,则封装转换一下
            if(classType.isAssignableFrom(MapData.class)&&!(ret instanceof MapData)&&ret instanceof Map){
                Map<String,Object> mapObject = (Map<String, Object>)ret;
                MapData dataBox = new MapData();
                dataBox.putAll(mapObject);
                ret = (T)dataBox;
            }

            return ret;
        }else{
            throw new ClassCastException(object.getClass()+" can not cast to "+classType);
        }
    }

    public <T> T getObject(String expression, Class<T> classType){
        return this.getObject(expression,false,classType);
    }
    /**
     * 取一个子对象
     *
     * @param expression  expression
     * @param strict strict
     * @return object
     */
    public Object getObject(String expression, boolean strict){
        Object value = null;
        if(expression.indexOf(".")<0||expression.indexOf("/")<0){
            value = get(expression);
        }else{
            toggleInit();
            try{
                value = context.getValue(xpath(expression));
            }catch(JXPathNotFoundException e){  //如果表达式不存在,在严格模式下,也抛出错
                if(strict){
                    throw new NullPointerException(e.getMessage());
                }
            }
        }
        if(strict) Validate.notNull(value,"expression:"+expression,",not exits in object:"+toJsonString());
        return value;
    }

    /**
     * 获取一个值对象
     *
     * @param expression xpath表达式
     * @param strict 是否使用严格模式
     * @return valueObject
     */
    public ValueObject get(String expression, boolean strict){
        Object value = getObject(expression,strict);
        return new ValueObject(value);
    }

    public MapData getSubDataObject(String expression, boolean strict){
        Object value = getObject(expression,strict);
        if(value instanceof MapData){
            return ((MapData)value);
        }else{
            return null;
        }
    }

    public MapData getSubDataObject(String expression){
        return getSubDataObject(expression,false);
    }

    public ValueObject getValue(String expression){
        return get(expression,false);
    }

    public void putValue(String expression, Object value){
        toggleInit();
        context.createPathAndSetValue(xpath(expression),value);
    }



    public String toJsonString(){
        return JSONKit.toJsonString(this);
    }

}
