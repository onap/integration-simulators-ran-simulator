/*-
 * ============LICENSE_START=======================================================
 * Ran Simulator Controller
 * ================================================================================
 * Copyright (C) 2020 Wipro Limited.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.ransim.rest.api.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.onap.ransim.rest.api.controller.RansimController;

@Entity
@Table(name = "CellNeighbor")
public class CellNeighbor {

	@Id
	@Column(name = "nodeId", unique = true, nullable = false, length = 52)
	private String nodeId;

	@OneToMany(targetEntity = NeighborDetails.class, cascade = CascadeType.ALL)
	private Set<NeighborDetails> neighborList;

	public CellNeighbor() {
		super();
	}

	public CellNeighbor(String nodeId, Set<NeighborDetails> neighborList) {
		super();
		this.nodeId = nodeId;
		this.neighborList = neighborList;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public Set<NeighborDetails> getNeighborList() {
		return neighborList;
	}

	public void setNeighborList(Set<NeighborDetails> neighborList) {
		this.neighborList = neighborList;
	}

	static Logger log = Logger.getLogger(RansimController.class.getName());

	public void display() {

		List<NeighborDetails> iterator = new ArrayList<>(neighborList);
		for (int ii = 0; ii < iterator.size(); ii++) {
			log.info("neighbors NeighborList: " + iterator.get(ii).getNeigbor().getSourceCellNodeId() + " "
					+ iterator.get(ii).getNeigbor().getNeighborCell() + " " + iterator.get(ii).isBlacklisted());
		}

	}
}
