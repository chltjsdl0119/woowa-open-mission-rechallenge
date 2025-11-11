package com.mapofmemory.global.dto;

import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    public static <S, T> PageResponse<T> of(Page<S> page, Function<S, T> converter) {
        Objects.requireNonNull(page, "Page 객체는 null일 수 없습니다.");
        Objects.requireNonNull(converter, "Converter 함수는 null일 수 없습니다.");

        List<T> content = page.getContent().stream()
                .map(converter)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    public static <T> PageResponse<T> of(Page<T> page) {
        Objects.requireNonNull(page, "Page 객체는 null일 수 없습니다.");
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    public static <T> PageResponse<T> empty() {
        return new PageResponse<>(Collections.emptyList(), 0, 0, 0, 1, true, true);
    }
}
