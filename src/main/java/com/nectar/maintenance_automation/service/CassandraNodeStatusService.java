package com.nectar.maintenance_automation.service;

import com.nectar.maintenance_automation.dtos.CassandraNodeStatus;
import java.util.List;

public interface CassandraNodeStatusService {
	List<CassandraNodeStatus> getNodesStatus();

	CassandraNodeStatus getNodeStatus(String address);
}