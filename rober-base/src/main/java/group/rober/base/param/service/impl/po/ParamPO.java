package group.rober.base.param.service.impl.po;

import group.rober.base.param.model.ParamItemEntry;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Table(name = "FOWK_PARAM")
public class ParamPO implements Serializable {

    @Id
    private String code;
    private String name;
    @Column(name = "NAME_I18N_CODE")
    private String nameI18nCode;
    private String sortCode;
    private String intro;

    protected Integer revision;
    protected Long createdBy;
    protected Date createdTime;
    protected Long updatedBy;
    protected Date updatedTime;
    @Transient
    private Map<String, ParamItemEntry> itemMap = new LinkedHashMap<String, ParamItemEntry>();

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

    public ParamItemEntry getItem(String itemCode) {
        return itemMap.get(itemCode);
    }

    public void setItemMap(Map<String, ParamItemEntry> itemMap) {
        this.itemMap = itemMap;
    }

    public List<ParamItemEntry> getItems() {
        List<ParamItemEntry> itemList = new ArrayList<ParamItemEntry>();
        itemList.addAll(itemMap.values());
        return itemList;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
