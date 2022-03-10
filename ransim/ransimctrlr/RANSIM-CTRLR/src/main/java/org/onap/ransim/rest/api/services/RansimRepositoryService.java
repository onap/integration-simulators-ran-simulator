/*
 * ============LICENSE_START=======================================================
 * Ran Simulator Controller
 * ================================================================================
 * Copyright (C) 2020-2021 Wipro Limited.
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

package org.onap.ransim.rest.api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.onap.ransim.rest.api.models.CellDetails;
import org.onap.ransim.rest.api.models.CellNeighbor;
import org.onap.ransim.rest.api.models.NeighborDetails;
import org.onap.ransim.rest.api.models.NetconfServers;
import org.onap.ransim.rest.api.models.NRCellCU;
import org.onap.ransim.rest.api.models.OperationLog;
import org.onap.ransim.rest.api.repository.CellDetailsRepo;
import org.onap.ransim.rest.api.repository.CellNeighborRepo;
import org.onap.ransim.rest.api.repository.NRCellCURepository;
import org.onap.ransim.rest.api.repository.NeighborDetailsRepo;
import org.onap.ransim.rest.api.repository.NetconfServersRepo;
import org.onap.ransim.rest.api.repository.OperationLogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RansimRepositoryService {

    static Logger log = Logger.getLogger(RansimRepositoryService.class.getName());

    @Autowired
    CellDetailsRepo cellDetailsRepo;

    @Autowired
    NRCellCURepository nRCellCURepo;

    @Autowired
    NetconfServersRepo netconfServersRepo;

    @Autowired
    CellNeighborRepo cellNeighborRepo;

    @Autowired
    NeighborDetailsRepo neighborDetailsRepo;

    @Autowired
    OperationLogRepo operationLogRepo;

    /**
     * Method to retrieve cell details
     * 
     * @param nodeId
     * @return
     */
    public CellDetails getCellDetail(String nodeId) {
        Optional<CellDetails> cd = cellDetailsRepo.findById(nodeId);
        CellDetails cellDetails = null;
        if (cd.isPresent()) {
            cellDetails = cd.get();
        }
        return cellDetails;
    }

     /**
      * Method to retrieve cellCU details
      * 
      * @param cellLocalId
      * @return
      */
    public NRCellCU getNRCellCUDetail(Integer cellLocalId) {
        Optional<NRCellCU> cd = nRCellCURepo.findById(cellLocalId);
	NRCellCU nrCellCU = null;
	if (cd.isPresent()) {
		nrCellCU = cd.get();
	}
	return nrCellCU;
    }

     /**
      * Method to retrieve cellCU neighbors
      *
      * @param cellLocalId
      * @return
      */
    public NRCellCU getCellRelation(Integer cellLocalId) {
        Optional<NRCellCU> cellNeighborDetails = nRCellCURepo.findById(cellLocalId);
	NRCellCU cellRelation = null;
	if (cellNeighborDetails.isPresent()) {
		cellRelation = cellNeighborDetails.get();
	}
	return cellRelation;
    }

    /**
     * Method to retrieve netconf server details
     * 
     * @param serverId
     * @return
     */
    public NetconfServers getNetconfServer(String serverId) {
        Optional<NetconfServers> serverDetails = netconfServersRepo.findById(serverId);
        NetconfServers server = null;
        if (serverDetails.isPresent()) {
            server = serverDetails.get();
        }
        return server;
    }

    /**
     * Method to retrieve cell neighbors
     * 
     * @param nodeId
     * @return
     */
    public CellNeighbor getCellNeighbor(String nodeId) {
        Optional<CellNeighbor> cellNeighborDetails = cellNeighborRepo.findById(nodeId);
        CellNeighbor cellNeighbor = null;
        if (cellNeighborDetails.isPresent()) {
            cellNeighbor = cellNeighborDetails.get();
        }
        return cellNeighbor;
    }

    /**
     * Method to retrieve all cell details
     * 
     * @return
     */
    public List<CellDetails> getCellDetailsList() {
        Iterable<CellDetails> cellsList = cellDetailsRepo.findAll();
        if (cellsList != null) {
            return (List<CellDetails>) cellsList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Method to retrieve cells with no server ids
     * 
     * @return
     */
    public List<CellDetails> getCellsWithNoServerIds() {
        List<CellDetails> cellsList = cellDetailsRepo.findCellsWithNoServerId();
        if (cellsList != null) {
            return cellsList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Method to retrieve cell with collision or confusion
     * 
     * @return
     */
    public List<CellDetails> getCellsWithCollisionOrConfusion() {
        List<CellDetails> cellsList = cellDetailsRepo.getCellsWithCollisionOrConfusion();
        if (cellsList != null) {
            return cellsList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Method to retrieve operation log
     * 
     * @return
     */
    public List<OperationLog> getOperationLogList() {
        Iterable<OperationLog> opLogList = operationLogRepo.findAll();
        if (opLogList != null) {
            return (List<OperationLog>) opLogList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Method to retrieve all netconf servers
     * 
     * @return
     */
    public List<NetconfServers> getNetconfServersList() {
        Iterable<NetconfServers> serversList = netconfServersRepo.findAll();
        if (serversList != null) {
            return (List<NetconfServers>) serversList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Method to retrieve all cell neighbors
     * 
     * @return
     */
    public List<CellNeighbor> getCellNeighborList() {
        Iterable<CellNeighbor> cellNeighborList = cellNeighborRepo.findAll();
        if (cellNeighborList != null) {
            return (List<CellNeighbor>) cellNeighborList;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Method to delete specific cells
     * 
     * @param deleteCelldetail
     */
    public void deleteCellDetails(CellDetails deleteCelldetail) {

        if (deleteCelldetail.getServerId() != null) {
            NetconfServers ns = getNetconfServer(deleteCelldetail.getServerId());
            if (ns != null) {
                ns.getCells().remove(deleteCelldetail);
                netconfServersRepo.save(ns);
            }
        }
        cellDetailsRepo.deleteById(deleteCelldetail.getNodeId());
    }

    /**
     * Method to delete cell neighbors
     * 
     * @param deleteCellNeighbor
     */
    public void deleteCellNeighbor(CellNeighbor deleteCellNeighbor) {
        cellNeighborRepo.deleteById(deleteCellNeighbor.getNodeId());
    }

    /**
     * Method to delete all netconf servers
     */
    public void deleteNetconfServers() {
        netconfServersRepo.deleteAll();
    }

    /**
     * Method to delete all cell neighbors
     */
    public void deleteCellNeighbors() {
        cellNeighborRepo.deleteAll();
    }

    /**
     * Method to delete all cells
     */
    public void deleteAllCellDetails() {
        cellDetailsRepo.deleteAll();
    }

    /**
     * Method to save cells
     * 
     * @param cellDetail
     */
    public void mergeCellDetails(CellDetails cellDetail) {
        cellDetailsRepo.save(cellDetail);
    }

    /**
     * Method to save neighbors
     * 
     * @param neighborDetails
     */
    public void mergeNeighborDetails(NeighborDetails neighborDetails) {
        neighborDetailsRepo.save(neighborDetails);
    }

    /**
     * Method to save netconf servers
     * 
     * @param netconfServers
     */
    public void mergeNetconfServers(NetconfServers netconfServers) {
        netconfServersRepo.save(netconfServers);
    }

    /**
     * Method to save cell neighbors
     * 
     * @param cellNeighbor
     */
    public void mergeCellNeighbor(CellNeighbor cellNeighbor) {
        cellNeighborRepo.save(cellNeighbor);
    }

    /**
     * Method to save operation log
     * 
     * @param opLog
     */
    public void mergeOperationLog(OperationLog opLog) {
        operationLogRepo.save(opLog);
    }
}
