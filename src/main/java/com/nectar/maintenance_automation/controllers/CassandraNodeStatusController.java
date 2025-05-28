package com.nectar.maintenance_automation.controllers;

import com.nectar.maintenance_automation.dtos.CassandraNodeStatus;
import com.nectar.maintenance_automation.service.CassandraNodeStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cassandra")
public class CassandraNodeStatusController {

    private static final Logger logger = LoggerFactory.getLogger(CassandraNodeStatusController.class);
    private final CassandraNodeStatusService cassandraNodeStatusService;

    @Autowired
    public CassandraNodeStatusController(CassandraNodeStatusService cassandraNodeStatusService) {
        this.cassandraNodeStatusService = cassandraNodeStatusService;
    }

    @GetMapping("/nodes")
    public ResponseEntity<?> getAllNodesStatus() {
        try {
            List<CassandraNodeStatus> nodes = cassandraNodeStatusService.getNodesStatus();
            if (nodes.isEmpty()) {
                logger.warn("No Cassandra nodes found in the cluster");
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(nodes);
        } catch (Exception e) {
            logger.error("Error while fetching all nodes status", e);
            return ResponseEntity.internalServerError()
                    .body("Failed to fetch Cassandra nodes status: " + e.getMessage());
        }
    }

    @GetMapping("/nodes/{address}")
    public ResponseEntity<?> getNodeStatus(@PathVariable String address) {
        try {
            CassandraNodeStatus nodeStatus = cassandraNodeStatusService.getNodeStatus(address);
            if (nodeStatus == null) {
                logger.warn("No node found with address: {}", address);
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(nodeStatus);
        } catch (Exception e) {
            logger.error("Error while fetching node status for address: {}", address, e);
            return ResponseEntity.internalServerError()
                    .body("Failed to fetch node status: " + e.getMessage());
        }
    }
} 