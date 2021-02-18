package com.eraykalkan.kafkaconnector.manager;

import java.util.List;

public interface TopicManager {
    int checkPartitionSizeForTopic(String topicName);
    void deleteTopic(String topicName);
    void createTopic(String topicName,int partitionSize,int replicationFactor);
    List<String> listAvailableTopics();
    void increasePartitionsOnTopic(String topicName, int partitionSize);
    int getClusterSize();
}
