package com.eraykalkan.kafkaconnector.config;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;

import static java.lang.Boolean.FALSE;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.springframework.kafka.support.serializer.JsonDeserializer.TRUSTED_PACKAGES;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafkaconnector.kafka.trusted-packages}")
    private String trustedPackages;

    @Bean
    public <T> ConsumerFactory<String, T> consumerFactory() {
        var configs = new HashMap<String, Object>();
        configs.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(GROUP_ID_CONFIG, "all");
        configs.put(AUTO_OFFSET_RESET_CONFIG, "latest");

        var deserializationConfigs = new HashMap<String, Object>();
        deserializationConfigs.put(TRUSTED_PACKAGES, trustedPackages);

        var jsonDeserializer = new JsonDeserializer<T>(new KafkaObjectMapper());
        jsonDeserializer.configure(deserializationConfigs, FALSE);

        var keyErrorHandlingDeserializer = new ErrorHandlingDeserializer<>(new StringDeserializer());
        var valueErrorHandlingDeserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);

        return new DefaultKafkaConsumerFactory<>(configs, keyErrorHandlingDeserializer, valueErrorHandlingDeserializer);
    }

    @Bean
    public <T> ConcurrentKafkaListenerContainerFactory<String, T> listenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, T>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }

}
