package com.mapofmemory.global.util;

import lombok.experimental.UtilityClass;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@UtilityClass
public class ConverterUtils {

    public <T, R> R map(T source, Function<T, R> mapper) {
        if (source == null || mapper == null) {
            return null;
        }

        return mapper.apply(source);
    }

    public <T, R> List<R> mapList(List<T> sourceList, Function<T, R> mapper) {
        if (sourceList == null || mapper == null) {
            return Collections.emptyList();
        }

        return sourceList.stream()
                .filter(Objects::nonNull)
                .map(mapper)
                .toList();
    }
}