package com.nectar.maintenance_automation.serviceImpl;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.nectar.maintenance_automation.config.CassandraConfig;
import com.nectar.maintenance_automation.dtos.CassandraNodeStatus;
import com.nectar.maintenance_automation.service.CassandraNodeStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CassandraNodeStatusServiceImpl implements CassandraNodeStatusService {

    private static final Logger logger = LoggerFactory.getLogger(CassandraNodeStatusServiceImpl.class);
    private final CassandraConfig cassandraConfig;

    @Autowired
    public CassandraNodeStatusServiceImpl(CassandraConfig cassandraConfig) {
        this.cassandraConfig = cassandraConfig;
    }

    @Override
    public List<CassandraNodeStatus> getNodesStatus() {
        List<CassandraNodeStatus> nodeStatuses = new ArrayList<>();
        try {
            Cluster cluster = cassandraConfig.getCluster();
            for (Host host : cluster.getMetadata().getAllHosts()) {
                nodeStatuses.add(createNodeStatus(host));
            }
        } catch (NoHostAvailableException e) {
            logger.error("Failed to connect to any Cassandra nodes", e);
            throw new RuntimeException("Failed to connect to Cassandra cluster", e);
        } catch (Exception e) {
            logger.error("Error while fetching node statuses", e);
            throw new RuntimeException("Error while fetching node statuses", e);
        }
        return nodeStatuses;
    }

    @Override
    public CassandraNodeStatus getNodeStatus(String address) {
        try {
            Cluster cluster = cassandraConfig.getCluster();
            for (Host host : cluster.getMetadata().getAllHosts()) {
                if (host.getBroadcastAddress().getHostAddress().equals(address)) {
                    return createNodeStatus(host);
                }
            }
            logger.warn("No node found with address: {}", address);
            return null;
        } catch (NoHostAvailableException e) {
            logger.error("Failed to connect to Cassandra node: {}", address, e);
            throw new RuntimeException("Failed to connect to Cassandra node: " + address, e);
        } catch (Exception e) {
            logger.error("Error while fetching node status for address: {}", address, e);
            throw new RuntimeException("Error while fetching node status for address: " + address, e);
        }
    }

    private CassandraNodeStatus createNodeStatus(Host host) {
        return CassandraNodeStatus.builder()
                .address(host.getBroadcastAddress().getHostAddress())
                .status(host.isUp() ? "UP" : "DOWN")
                .state(host.getState().toString())
                .load(String.valueOf(host.getDatacenter()))
                .tokens(String.valueOf(host.getTokens().size()))
                .owns(calculateOwnership(host))
                .hostId(host.getHostId() != null ? host.getHostId().toString() : "N/A")
                .rack(host.getRack())
                .build();
    }

    private String calculateOwnership(Host host) {
        try {
            Cluster cluster = cassandraConfig.getCluster();
            double ownership = (double) host.getTokens().size() / 
                             cluster.getMetadata().getAllHosts().stream()
                                   .mapToInt(h -> h.getTokens().size())
                                   .sum() * 100;
            return String.format("%.2f%%", ownership);
        } catch (Exception e) {
            logger.warn("Failed to calculate ownership for host: {}", host.getBroadcastAddress(), e);
            return "N/A";
        }
    }
}