package com.eraykalkan.kafkaconnector.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.MapperFeature.DEFAULT_VIEW_INCLUSION;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.lang.Boolean.FALSE;


public final class KafkaObjectMapper extends ObjectMapper {

    private static final long serialVersionUID = -6180661847346548203L;

    public KafkaObjectMapper() {
        registerModule(new ParameterNamesModule());
        registerModule(new JavaTimeModule());
        registerModule(new Jdk8Module());

        configure(WRITE_DATES_AS_TIMESTAMPS, FALSE);
        configure(FAIL_ON_UNKNOWN_PROPERTIES, FALSE);
        configure(DEFAULT_VIEW_INCLUSION, FALSE);
    }

}
