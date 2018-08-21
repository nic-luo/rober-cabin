package group.rober.base.detector;

import group.rober.runtime.lang.MapData;
import group.rober.runtime.lang.ValueObject;

import java.io.Serializable;
import java.util.Map;

/**
 * 检查器上下文对象
 */
public class DetectorContext implements Serializable{
    protected MapData params = new MapData();

    /**
     * 设置参数对象
     *
     * @param xpath
     * @param value
     * @return
     */
    protected DetectorContext setParam(String xpath,Object value){
        this.params.putValue(xpath,value);
        return this;
    }

    protected DetectorContext setParam(Map<String,Object> param){
        params.putAll(param);
        return this;
    }

    public ValueObject getParam(String xpath){
        return this.params.getValue(xpath);
    }
    public <T> T getParam(String xpath,Class<T> classType){
        return this.params.getValue(xpath).objectVal(classType);
    }
}
