package group.rober.runtime.kit;

import group.rober.runtime.lang.MapData;

import java.util.List;
import java.util.Map;

/**
 * Created by tisir yangsong158@qq.com on 2017-06-10
 */
public abstract class DataMapKit {
    public static List<MapData> convertToDataObjectList(List<Map<String,Object>> dataList){
        if(dataList==null) return null;
        List<MapData> dataBoxList = ListKit.newArrayList();

        for(int i=0;i<dataList.size();i++){
            dataBoxList.add(convertToDataObject(dataList.get(i)));
        }

        return dataBoxList;
    }

    public static MapData convertToDataObject(Map<String,Object> data){
        if(data==null) return null;
        MapData dataBox = new MapData();
        dataBox.putAll(data);
        return dataBox;
    }
}
