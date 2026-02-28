package com.decisiontool.util;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class TypeInferrer {

    private static final Pattern NUMBER_PATTERN  = Pattern.compile("^-?\\d+(\\.\\d+)?$");
    private static final Pattern DATE_PATTERN    = Pattern.compile(
            "^\\d{4}-\\d{2}-\\d{2}(T\\d{2}:\\d{2}(:\\d{2})?)?$");
    private static final Pattern BOOLEAN_PATTERN = Pattern.compile(
            "^(true|false|yes|no|1|0|sí|si)$", Pattern.CASE_INSENSITIVE);

    /** Inferir el tipo de una columna a partir de una muestra de sus valores */
    public String inferType(List<String> sampleValues) {
        if (sampleValues == null || sampleValues.isEmpty()) return "STRING";

        long nonNull = sampleValues.stream().filter(v -> v != null && !v.isBlank()).count();
        if (nonNull == 0) return "STRING";

        long numbers  = sampleValues.stream().filter(this::isNumber).count();
        long dates    = sampleValues.stream().filter(this::isDate).count();
        long booleans = sampleValues.stream().filter(this::isBoolean).count();

        double ratio = (double) nonNull / sampleValues.size();
        if (numbers  / (double) nonNull >= 0.9) return "NUMBER";
        if (dates    / (double) nonNull >= 0.9) return "DATE";
        if (booleans / (double) nonNull >= 0.9) return "BOOLEAN";
        return "STRING";
    }

    public boolean isNumber(String value) {
        return value != null && NUMBER_PATTERN.matcher(value.trim()).matches();
    }

    public boolean isDate(String value) {
        return value != null && DATE_PATTERN.matcher(value.trim()).matches();
    }

    public boolean isBoolean(String value) {
        return value != null && BOOLEAN_PATTERN.matcher(value.trim()).matches();
    }
}
