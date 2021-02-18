package com.eraykalkan.kafkaconnector.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.*;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Component
public class KafkaTopicManager implements TopicManager {

    private final KafkaTemplate<String, ?> kafkaTemplate;
    private final AdminClient kafkaAdminClient;

    @Override
    public int checkPartitionSizeForTopic(String topicName) {
        return kafkaTemplate.partitionsFor(topicName).size();
    }

    @Override
    public void deleteTopic(String topicName) {
        DeleteTopicsResult result = kafkaAdminClient.deleteTopics(Arrays.asList(topicName));
    }

    @Override
    public void createTopic(String topicName, int partitionSize, int replicationFactor) {
        if (replicationFactor > getClusterSize()) {
            return;
        }
        List<NewTopic> topics = new ArrayList<>();
        NewTopic topic = TopicBuilder
                .name(topicName)
                .partitions(partitionSize)
                .replicas(replicationFactor)
                .build();
        topics.add(topic);
        try {
            kafkaAdminClient.createTopics(topics).all().get();
        } catch (InterruptedException e) {
            log.error(e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            log.error(e);
        }
    }

    @Override
    public List<String> listAvailableTopics() {
        try {
            return kafkaAdminClient.listTopics()
                    .listings().get()
                    .stream()
                    .map(TopicListing::name)
                    .collect(Collectors.toList());
        } catch (InterruptedException e) {
            log.error(e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            log.error(e);
        }
        return new ArrayList<>();
    }

    @Override
    public void increasePartitionsOnTopic(String topicName, int partitionSize) {
        Map<String, NewPartitions> newPartitionSet = new HashMap<>();
        newPartitionSet.put(topicName, NewPartitions.increaseTo(partitionSize));
        try {
            kafkaAdminClient.createPartitions(newPartitionSet).all().get();
        } catch (InterruptedException e) {
            log.error(e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            log.error(e);
        }
    }

    @Override
    public int getClusterSize() {
        int numberOfClusters = 0;
        DescribeClusterResult describeClusterResult = kafkaAdminClient.describeCluster();
        try {
            numberOfClusters = describeClusterResult.nodes().get().size();
        } catch (InterruptedException e) {
            log.error(e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            log.error(e);
        }
        return numberOfClusters;
    }

}
