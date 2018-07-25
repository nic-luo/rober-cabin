package group.rober.sql.serialno.model;

import java.io.Serializable;
import java.util.Date;

public class SerialNoCursor implements Serializable {

    private String id;

    private String template;

    private Long cursorValue;

    private Date updatedTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Long getCursorValue() {
        return cursorValue;
    }

    public void setCursorValue(Long cursorValue) {
        this.cursorValue = cursorValue;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
