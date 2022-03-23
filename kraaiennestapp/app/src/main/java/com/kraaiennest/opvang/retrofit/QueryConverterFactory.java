package com.kraaiennest.opvang.retrofit;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import retrofit2.Converter;
import retrofit2.Retrofit;

public class QueryConverterFactory extends Converter.Factory {
    public static QueryConverterFactory create() {
        return new QueryConverterFactory();
    }

    @Override
    public Converter<?, String> stringConverter(@NotNull Type type, Annotation @NotNull [] annotations, @NotNull Retrofit retrofit) {
        if (type == LocalDate.class) {
            return DateQueryConverter.INSTANCE;
        }
        return null;
    }

    private static final class DateQueryConverter implements Converter<LocalDate, String> {
        static final DateQueryConverter INSTANCE = new DateQueryConverter();

        private static final ThreadLocal<DateFormat> DF = ThreadLocal.withInitial(SimpleDateFormat::getDateInstance);

        @Override
        public String convert(@NotNull LocalDate date) {
            return DF.get().format(date);
        }
    }
}
