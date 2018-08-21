package group.rober.base.detector.model;

import group.rober.base.detector.entity.DetectorPO;
import group.rober.runtime.kit.ListKit;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.List;

public class Detector extends DetectorPO implements Serializable {

    @Transient
    List<DetectorItem> items = ListKit.newArrayList();

    public DetectorItem lookupItem(String itemCode){
        for(DetectorItem item : items){
            if(itemCode.equals(item.getItemCode())){
                return item;
            }
        }
        return null;
    }

    public List<DetectorItem> getItems() {
        return items;
    }

    public void setItems(List<DetectorItem> items) {
        this.items = items;
    }
}
