package ar.com.gt.utad;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.ALWAYS)
public class VarDefinition {

    String name;
    DataType dataType;
    Boolean isRequired;
    Integer dimensions;

    @Getter(AccessLevel.NONE)
    List<String> tags;

    @Getter(AccessLevel.NONE)
    List<String> aliases;

    VarValue value;

    public boolean haveValue() {
        return this.value != null;
    }

    public List<String> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }

    public List<String> getAliases() {
        if (aliases == null) {
            aliases = new ArrayList<>();
        }
        return aliases;
    }

    public <T> void setValue(T value, String[] tags, Integer[] index) {
        if (this.value == null) {
            this.value = new VarValue();
        }

        this.value.setValue(value, tags, index);
    }

    public <T> void setValue(T value, String[] tags) {
        if (this.value == null) {
            this.value = new VarValue();
        }
        this.value.setValue(value, tags);
    }

}
