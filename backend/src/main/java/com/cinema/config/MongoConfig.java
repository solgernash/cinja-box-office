package com.cinema.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * Show.showTime is a java.sql.Time. Spring Data MongoDB has no built-in
 * converter for that type: it can write it (java.sql.Time extends
 * java.util.Date), but reading it back throws
 * ConverterNotFoundException("No converter found capable of converting from
 * type [java.util.Date] to type [java.sql.Time]"). Register explicit
 * converters so java.sql.Time fields survive a save/read round trip.
 */
@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new DateToSqlTimeConverter());
        converters.add(new SqlTimeToDateConverter());
        return new MongoCustomConversions(converters);
    }

    @ReadingConverter
    static class DateToSqlTimeConverter implements Converter<Date, Time> {
        @Override
        public Time convert(Date source) {
            return new Time(source.getTime());
        }
    }

    @WritingConverter
    static class SqlTimeToDateConverter implements Converter<Time, Date> {
        @Override
        public Date convert(Time source) {
            return new Date(source.getTime());
        }
    }
}
