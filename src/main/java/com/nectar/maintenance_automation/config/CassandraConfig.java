package com.nectar.maintenance_automation.config;

import com.datastax.driver.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CassandraConfig {

    @Value("${cassandra.contact-point}")
    private String[] contactPoints;

    @Value("${cassandra.contact-point.port}")
    private int port;

    @Value("${cassandra.keyspace}")
    private String keyspace;

    @Value("${cassandra.username}")
    private String username;

    @Value("${cassandra.password}")
    private String password;

    @Bean
    public Cluster getCluster() {
        return Cluster.builder()
                .addContactPoints(contactPoints)
                .withPort(port)
                .withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.QUORUM))
                .withCredentials(username, password)
                .withProtocolVersion(ProtocolVersion.V3)
                .build();
    }

    @Bean
    public Session session(Cluster cluster) {
        return cluster.connect(keyspace);
    }
}
