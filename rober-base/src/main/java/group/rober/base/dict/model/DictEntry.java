package group.rober.base.dict.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.*;

public class DictEntry implements Serializable {
    @Id
    protected String code;
    protected String name;
    protected String nameI18nCode;
    protected String categoryCode;
    protected String sortCode;
    protected String intro;
    protected List<DictItemEntry> items = new ArrayList<>();

    @Transient
    private Map<String, DictItemEntry> itemMap = new LinkedHashMap<String, DictItemEntry>();

    public void setItems(List<DictItemEntry> items) {
        this.items = items;
    }

    @JsonIgnore
    public List<DictItemEntry> getDictItemEntrys() {
        return items;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameI18nCode() {
        return nameI18nCode;
    }

    public void setNameI18nCode(String nameI18nCode) {
        this.nameI18nCode = nameI18nCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public DictItemEntry getItem(String itemCode) {
        return itemMap.get(itemCode);
    }

    public void setItemMap(Map<String, DictItemEntry> itemMap) {
        this.itemMap = itemMap;
    }

    public List<DictItemEntry> getItems() {
        List<DictItemEntry> itemList = new ArrayList<DictItemEntry>();
        itemList.addAll(itemMap.values());
        return itemList;
    }

}
