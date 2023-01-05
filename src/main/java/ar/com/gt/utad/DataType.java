package ar.com.gt.utad;

import java.util.Date;

import lombok.Getter;

public enum DataType {
    STRING(String.class),
    BOOLEAN(String.class),
    INTEGER(Long.class),
    DECIMAL(Double.class),
    DATE(Date.class),
    BYTEA(Byte[].class);

    @Getter
    Class<?> clazz;

    private DataType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public static DataType parse(Class<? extends Object> valueClass) {
        switch (valueClass.getName()) {
            case "java.lang.String":
                return STRING;
            case "java.lang.Byte":
            case "java.lang.byte":
            case "java.lang.Short":
            case "java.lang.short":
            case "java.lang.Integer":
            case "java.lang.int":
                return INTEGER;
            case "java.lang.Float":
            case "java.lang.float":
            case "java.lang.Double":
            case "java.lang.double":
                return DECIMAL;
            case "java.lang.Boolean":
            case "java.lang.boolean":
                return BOOLEAN;
            case "java.util.Date":
                return DATE;
            case "java.util.Byte[]":
            case "java.util.byte[]":
            case "[B":
                return BYTEA;
        }

        return null;
    }
}
