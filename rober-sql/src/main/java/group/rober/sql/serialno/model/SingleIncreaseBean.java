package group.rober.sql.serialno.model;

import java.util.Date;

/**
 * Created by zhulifeng on 17-12-6.
 */
public class SingleIncreaseBean {

    /**
     * 编号
     */
    private String generatorId;

    /**
     * 更新时间
     */
    private Date updatedTime;

    public SingleIncreaseBean(String generatorId, Date updatedTime) {
        this.generatorId = generatorId;
        this.updatedTime = updatedTime;
    }

    public SingleIncreaseBean() {

    }

    public String getGeneratorId() {
        return generatorId;
    }

    public void setGeneratorId(String generatorId) {
        this.generatorId = generatorId;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
