package group.rober.sql.serialno.model;

import java.util.Date;

/**
 * Created by zhulifeng on 17-12-6.
 */
public class BatchIncreaseBean {

    /**
     * 编号
     */
    private String generatorId;

    /**
     * 批量值
     */
    private Integer count;

    /**
     * 更新时间
     */
    private Date updatedTime;

    public BatchIncreaseBean(String generatorId, Integer count, Date updatedTime) {
        this.generatorId = generatorId;
        this.count = count;
        this.updatedTime = updatedTime;
    }

    public BatchIncreaseBean() {

    }

    public String getGeneratorId() {
        return generatorId;
    }

    public void setGeneratorId(String generatorId) {
        this.generatorId = generatorId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
