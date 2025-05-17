package com.group_three.food_ordering.configs;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class StringToEnumConverterFactory implements ConverterFactory<String, Enum<?>> {

    @Override
    @NonNull
    public <T extends Enum<?>> Converter<String, T> getConverter(@NonNull Class<T> targetType) {
        return new StringToEnumConverter<>(targetType);
    }

    private record StringToEnumConverter<T extends Enum<?>>(Class<T> enumType) implements Converter<String, T> {

        @Override
        @Nullable
        public T convert(@Nullable String source) {
            if (source == null || source.isEmpty()) {
                return null;
            }
            for (T constant : enumType.getEnumConstants()) {
                if (constant.name().equalsIgnoreCase(source)) {
                    return constant;
                }
            }
            throw new IllegalArgumentException("No enum constant " + enumType.getSimpleName() + " for value: " + source);
        }
    }
}