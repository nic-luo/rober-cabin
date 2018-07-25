package group.rober.runtime.beans.propertyeditors;

import group.rober.runtime.kit.StringKit;
import group.rober.runtime.lang.ValueObject;

import java.beans.PropertyEditorSupport;

public class IntegerPropertyEditor extends PropertyEditorSupport {
    private Integer defaultValue = null;

    public IntegerPropertyEditor(){

    }
    public IntegerPropertyEditor(Integer defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        Integer value = defaultValue;
        if(StringKit.isNotBlank(text)){
            value = ValueObject.valueOf(text).intValue();
        }
        setValue(value);
    }
}
