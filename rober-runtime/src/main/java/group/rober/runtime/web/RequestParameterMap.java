package group.rober.runtime.web;

import group.rober.runtime.lang.MapData;
import group.rober.runtime.lang.ValueObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 请求参数再次封装
 * Created by tisir yangsong158@qq.com on 2017-05-31
 */
public class RequestParameterMap extends MapData {


	private static final long serialVersionUID = -3167615295990846332L;

	public RequestParameterMap(){

    }
	public RequestParameterMap(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        this.merge(parameterMap);
    }

    public <T> RequestParameterMap merge(Map<String,T> map){
        Iterator<Map.Entry<String, T>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Entry<String, T> entry = iterator.next();
            String name = entry.getKey();
            T value = entry.getValue();
            Object[] valueArray;
            if(value!=null&&value.getClass().isArray()){
                valueArray = (Object[])value;
                if(valueArray.length>1){
                    this.put(name,new ValueObject(valueArray));
                }else if(valueArray.length==1){
                    this.put(name,new ValueObject(valueArray[0]));
                }else{
                    this.put(name,new ValueObject(null));
                }
            }else{
                this.put(name,new ValueObject(value));
            }
        }
        return this;
    }

}
