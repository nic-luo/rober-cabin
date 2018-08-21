package group.rober.runtime.beans.propertyeditors;

import group.rober.runtime.kit.StringKit;
import group.rober.runtime.lang.ValueObject;

import java.beans.PropertyEditorSupport;

public class DoublePropertyEditor extends PropertyEditorSupport {
    private Double defaultValue = null;

    public DoublePropertyEditor(){

    }
    public DoublePropertyEditor(Double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        Double value = defaultValue;
        if(StringKit.isNotBlank(text)){
            value = ValueObject.valueOf(text).doubleValue();
        }
        setValue(value);
    }
}
