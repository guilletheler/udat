package ar.com.gt.utad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Log
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferibleApplicationData {

    @Getter(AccessLevel.NONE)
    private List<VarDefinition> varDefinitions;

    private Boolean autoCreateVars = true;

    public List<VarDefinition> getVarDefinitions() {
        if (varDefinitions == null) {
            varDefinitions = new ArrayList<>();
        }

        return varDefinitions;
    }

    public VarDefinition findVarDefinition(String varName) {
        VarDefinition ret = this.getVarDefinitions().stream().filter(var -> {
            var match = var.getName().equalsIgnoreCase(varName);

            if (!match) {
                match = var.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(varName));
            }

            return match;
        })
                .findAny()
                .orElse(null);

        return ret;
    }

    public List<VarDefinition> filterVarDefinitionsByAnyTag(String[] tags) {
        return this.getVarDefinitions().stream()
                .filter(vd -> {
                    return Arrays.asList(tags).stream()
                            .anyMatch(tag -> vd.tags.stream().anyMatch(vdt -> vdt.equalsIgnoreCase(tag)));
                }).collect(Collectors.toList());
    }

    public List<VarDefinition> filterVarDefinitionsByAllTag(String[] tags) {
        return this.getVarDefinitions().stream()
                .filter(vd -> {
                    return Arrays.asList(tags).stream()
                            .allMatch(tag -> vd.tags.stream().anyMatch(vdt -> vdt.equalsIgnoreCase(tag)));
                })
                .collect(Collectors.toList());
    }

    private VarDefinition defineVariable(String varName, DataType dataType, Integer dimensions) {
        return defineVariable(varName, dataType, false, dimensions, null, null);
    }

    private VarDefinition defineVariable(String varName, DataType dataType) {
        return defineVariable(varName, dataType, false, null, null, null);
    }

    public VarDefinition defineVariable(String varName, DataType dataType, Boolean isRequired, Integer dimensions,
            String[] aliases, String[] tags) {
        VarDefinition ret = new VarDefinition(varName, dataType, isRequired, dimensions, null, null, null);
        if (aliases != null) {
            ret.setAliases(Arrays.asList(aliases));
        }

        this.getVarDefinitions().add(ret);

        return ret;
    }

    public <T> void setValue(String varName, T value, Integer... index) {
        setValue(varName, value, null, index);
    }

    public <T> void setValue(String varName, T value, String[] tags, Integer... index) {

        var varDefinition = this.findVarDefinition(varName);

        if (this.autoCreateVars && varDefinition == null) {
            if (value != null) {
                if (index != null && index.length > 0) {
                    varDefinition = this.defineVariable(varName, DataType.parse(value.getClass()), index[0]);
                } else {
                    varDefinition = this.defineVariable(varName, DataType.parse(value.getClass()));
                }
            }
        }

        if (index != null && index.length > 0) {
            varDefinition.setValue(value, tags, index);
        } else {
            varDefinition.setValue(value, tags);
        }
    }

    public void setValueFromString(String s) {
        if (!s.isEmpty() && !s.startsWith("#")) {

            int pos = s.indexOf(" ");

            if (pos < 0) {
                pos = s.indexOf("=");
            }

            String key;
            String value;

            if (pos < 0) {
                key = s;
                value = "true";
            } else {
                key = s.substring(0, pos).trim();
                value = s.substring(pos).trim();
            }

            if (value.startsWith("=")) {
                value = value.substring(1).trim();
            }

            log.fine("Cargando parametro " + key + "=" + value);

            if (key.toLowerCase().endsWith("[class]")) {
                key = key.substring(0, key.length() - 8);
                try {
                    Class<?> clazz = Class.forName(value);
                    defineVariable(key, DataType.parse(clazz));

                } catch (Exception ex) {
                    log.warning("No se puede setear la clase de la variable " + key);
                }
            } else if (key.contains("[") && key.endsWith("]")) {
                String name = key.substring(0, key.indexOf("["));
                String strIndex = key.substring(key.indexOf("[") + 1, key.length() - 1);
                Integer[] index = Arrays.asList(strIndex.split("\\]\\[")).stream().map(str -> Integer.valueOf(str))
                        .collect(Collectors.toList()).toArray(new Integer[] {});
                setValue(name, value, index);
            } else {
                setValue(key, value);
            }
            // System.out.println("cargando valor " + k + " = " + v);
        }

    }

    public static String getJson(TransferibleApplicationData data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
            mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            log.log(Level.SEVERE, "No se puede convertir json a string", e);
            return null;
        }
    
    }
    public static TransferibleApplicationData fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
            mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            return mapper.readValue(json, TransferibleApplicationData.class);
        } catch (JsonProcessingException e) {
            log.log(Level.SEVERE, "No se puede convertir json a string", e);
            return null;
        }
    }
}