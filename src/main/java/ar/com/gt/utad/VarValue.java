package ar.com.gt.utad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VarValue {

    String stringValue;
    Long integerValue;
    Double decimalValue;
    Boolean boolValue;
    byte[] byteArrayValue;
    Date dateValue;

    @Getter(AccessLevel.NONE)
    List<String> tags;

    Integer index;

    @Getter(AccessLevel.NONE)
    List<VarValue> arrayValue;

    public List<String> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }

    public List<VarValue> getArrayValue() {
        if (arrayValue == null) {
            arrayValue = new ArrayList<>();
        }
        return arrayValue;
    }

    public void setValue(Object value, String[] tags) {
        setValue(value);
        if (tags != null) {
            setTags(Arrays.asList(tags));
        }
    }

    public void setValue(Object value, String[] tags, Integer[] index) {
        var varValue = findIndexed(index[0]);

        for (int i = 1; i < index.length; i++) {
            varValue = varValue.findIndexed(index[i]);
        }

        varValue.setValue(value, tags);

    }

    private VarValue findIndexed(final Integer index) {
        var varValue = this.getArrayValue().stream().filter(vv -> vv.getIndex() == index).findFirst().orElse(null);
        if (varValue == null) {
            varValue = new VarValue();
            varValue.setIndex(index);
            this.getArrayValue().add(varValue);
        }
        return varValue;
    }

    public <T> void setValue(T value) {
        if (value == null) {
            this.stringValue = null;
            this.arrayValue = null;
            this.byteArrayValue = null;
            this.boolValue = null;
            this.dateValue = null;
            this.decimalValue = null;
            this.integerValue = null;
        } else {
            switch (value.getClass().getName()) {
                case "java.lang.String":
                    this.stringValue = (String) value;
                    break;
                case "java.lang.Boolean":
                case "java.lang.boolean":
                    this.boolValue = (Boolean) value;
                    break;
                case "java.lang.Byte":
                case "java.lang.byte":
                case "java.lang.Short":
                case "java.lang.short":
                case "java.lang.Integer":
                case "java.lang.int":
                case "java.lang.Long":
                case "java.lang.long":
                    this.integerValue = Long.valueOf(value.toString());
                    break;
                case "java.lang.Float":
                case "java.lang.float":
                case "java.lang.Double":
                case "java.lang.double":
                    this.decimalValue = Double.valueOf(value.toString());
                    break;
                case "java.lang.byte[]":
                case "[B":
                    this.byteArrayValue = (byte[]) value;
                    break;
                case "java.lang.Byte[]":
                    this.byteArrayValue = ArrayUtils.toPrimitive((Byte[]) value);
                    break;
                case "java.util.Date":
                    this.dateValue = (Date) value;
                    break;
            }
        }
    }
}
