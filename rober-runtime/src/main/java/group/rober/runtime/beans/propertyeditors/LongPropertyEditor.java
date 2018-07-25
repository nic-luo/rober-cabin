package group.rober.runtime.beans.propertyeditors;

import group.rober.runtime.kit.StringKit;
import group.rober.runtime.lang.ValueObject;

import java.beans.PropertyEditorSupport;

public class LongPropertyEditor extends PropertyEditorSupport {
    private Long defaultValue = null;

    public LongPropertyEditor(){

    }
    public LongPropertyEditor(Long defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        Long value = defaultValue;
        if(StringKit.isNotBlank(text)){
            value = ValueObject.valueOf(text).longValue();
        }
        setValue(value);
    }
}
