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

package org.onap.ransim.rest.api.handler;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.onap.ransim.rest.api.models.CellDetails;
import org.onap.ransim.rest.api.models.CellNeighbor;
import org.onap.ransim.rest.api.models.FmAlarmInfo;
import org.onap.ransim.rest.api.models.GetNeighborList;
import org.onap.ransim.rest.api.models.NeighborDetails;
import org.onap.ransim.rest.api.models.NetconfServers;
import org.onap.ransim.rest.api.models.NRCellCU;
import org.onap.ransim.rest.api.models.NRCellDU;
import org.onap.ransim.rest.api.models.NRCellRelation;
import org.onap.ransim.rest.api.models.OperationLog;
import org.onap.ransim.rest.api.models.PmDataDump;
import org.onap.ransim.rest.api.models.PmParameters;
import org.onap.ransim.rest.api.services.RANSliceConfigService;
import org.onap.ransim.rest.api.services.RansimControllerServices;
import org.onap.ransim.rest.api.services.RansimRepositoryService;
import org.onap.ransim.websocket.model.AdditionalMeasurements;
import org.onap.ransim.websocket.model.CommonEventHeaderFm;
import org.onap.ransim.websocket.model.CommonEventHeaderPm;
import org.onap.ransim.websocket.model.EventFm;
import org.onap.ransim.websocket.model.EventPm;
import org.onap.ransim.websocket.model.FaultFields;
import org.onap.ransim.websocket.model.FmMessage;
import org.onap.ransim.websocket.model.Measurement;
import org.onap.ransim.websocket.model.PmMessage;
import org.onap.ransim.websocket.server.RansimWebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RansimPciHandler {

    static Logger log = Logger.getLogger(RansimPciHandler.class.getName());

    @Autowired
    RansimRepositoryService ransimRepo;

    @Autowired
    RansimControllerServices rscServices;

    @Autowired
    RANSliceConfigService ranSliceConfigService;

    static Map<String, String> globalFmCellIdUuidMap = new ConcurrentHashMap<String, String>();
    static Map<String, String> globalPmCellIdUuidMap = new ConcurrentHashMap<String, String>();

    Set<String> cellsWithIssues = new HashSet<>();

    List<PmParameters> pmParameters = new ArrayList<PmParameters>();
    int next = 0;

    public FmAlarmInfo setCollisionConfusionFromFile(Integer cellNodeId) {

        FmAlarmInfo result = new FmAlarmInfo();

        try {

            boolean collisionDetected = false;
            boolean confusionDetected = false;
            List<Integer> nbrPcis = new ArrayList<Integer>();
            String collisions = "";
            HashMap<Integer, Integer> confusions = new HashMap<Integer, Integer>();
            NRCellCU currentCell = ransimRepo.getNRCellCUDetail(cellNodeId);
            Integer currentCellNRPCI = ransimRepo.getNRPCI(cellNodeId);
            log.info("Setting confusion/collision for Cell :" + cellNodeId);

            GetNeighborList cellNbrDetails = generateNeighborList(cellNodeId.toString());

            for (NRCellCU firstLevelNbr : cellNbrDetails.getCUCellsWithHo()) {
                 Integer cellId = firstLevelNbr.getCellLocalId();
                 Integer nRPCI = ransimRepo.getNRPCI(cellId);
                 if (nbrPcis.contains(nRPCI)) {
                    confusionDetected = true;
                    if(confusions.containsKey((nRPCI))) {
                        confusions.put(nRPCI,confusions.get(nRPCI+","+firstLevelNbr.getCellLocalId()));
                    } else {
                         confusions.put(nRPCI,firstLevelNbr.getCellLocalId());
  		    }

                  } else {
                     nbrPcis.add(nRPCI);
                  }

                  if (currentCellNRPCI == nRPCI) {
                      collisionDetected = true;
                      collisions += collisions.isEmpty()?firstLevelNbr.getCellLocalId():","+firstLevelNbr.getCellLocalId();
                  }
            }

            result.setCollisions(collisions);
            result.setConfusions(confusions);

            currentCell.setPciCollisionDetected(collisionDetected);
            currentCell.setPciConfusionDetected(confusionDetected);

            if (!currentCell.isPciCollisionDetected() && !currentCell.isPciConfusionDetected()) {
                currentCell.setColor("#BFBFBF"); // GREY - No Issues
                result.setProblem("No Issues");
            } else if (currentCell.isPciCollisionDetected() && currentCell.isPciConfusionDetected()) {
                currentCell.setColor("#C30000"); // BROWN - Cell has both
                                                 // collision & confusion
                result.setProblem("CollisionAndConfusion");

            } else if (currentCell.isPciCollisionDetected()) {
                currentCell.setColor("#FF0000"); // RED - Cell has collision
                result.setProblem("Collision");

            } else if (currentCell.isPciConfusionDetected()) {
                currentCell.setColor("#E88B00"); // ORANGE - Cell has confusion
                result.setProblem("Confusion");

            } else {
                currentCell.setColor("#BFBFBF"); // GREY - No Issues
                result.setProblem("No Issues");
            }

            ransimRepo.mergeNRCellCU(currentCell);

            return result;

        } catch (Exception e2) {
            log.info("setCollisionConfusionFromFile :", e2);

            return null;
        }

    }

    /**
     * Generates separate list of neighbors with and without hand-off for a
     * cell.
     *
     * @param nodeId
     *        Node Id of cell for which the neighbor list is generated
     * @return Returns GetNeighborList object
     */
    public GetNeighborList generateNeighborList(String nodeId) {
	
	try {
		log.info("inside generateNeighborList for: " + nodeId);
		NRCellCU neighborList = ransimRepo.getCellRelation(Integer.valueOf(nodeId));
		GetNeighborList result = new GetNeighborList();

		List<NRCellCU> cellsWithNoHO = new ArrayList<>();
		List<NRCellCU> cellsWithHO = new ArrayList<>();

		List<NRCellRelation> nbrList = new ArrayList<>();
		if (neighborList != null) {
			nbrList.addAll(ransimRepo.getNRCellRelationList(neighborList));
		}

		for (int i = 0; i < nbrList.size(); i++) {
			NRCellCU nbr = ransimRepo.getNRCellCUDetail(nbrList.get(i).getIdNRCellRelation());
			if (nbrList.get(i).getisHOAllowed()) {
				cellsWithHO.add(nbr);
				log.info("cellswithHO is : " + cellsWithHO);
			} else {
				cellsWithNoHO.add(nbr);
				log.info("cellswithNoHO is : " + cellsWithNoHO);
			}
		}
		result.setNodeId(nodeId);
		result.setCUCellsWithHo(cellsWithHO);
		result.setCUCellsWithNoHo(cellsWithNoHO);
		log.info(" Generate neighbour result is : " + result);
		return result;
	} catch (Exception eu) {
		log.info("/getCUNeighborList", eu);
		return null;
	}
    }

    public void checkCollisionAfterModify() {
        try {
            List<NRCellCU> checkCollisionConfusion = ransimRepo.getCellsWithCollisionOrConfusion();

            for (int i = 0; i < checkCollisionConfusion.size(); i++) {
                log.info(checkCollisionConfusion.get(i).getCellLocalId());
                setCollisionConfusionFromFile(Integer.valueOf(checkCollisionConfusion.get(i).getCellLocalId()));
            }
        } catch (Exception eu) {
            log.info("checkCollisionAfterModify", eu);
        }
    }

    /**
     * It updates the cell with its new neighbor list and PCI and updates the
     * change in a database.
     *
     * @param nodeId
     *        node Id of the cell
     * @param physicalCellId
     *        PCI number of the cell
     * @param newNbrs
     *        List of new neighbors for the cell
     * @param source
     *        The source from which cell modification has been triggered
     * @return returns success or failure message
     */
    public int modifyCellFunction(String nodeId, Integer physicalCellId, List<NRCellRelation> newNbrs, String source) {

        int result = 111;

        log.info("modifyCellFunction nodeId:" + nodeId + ", physicalCellId:" + physicalCellId);
        NRCellCU modifyCell = ransimRepo.getNRCellCUDetail(Integer.valueOf(nodeId));

        if (modifyCell != null) {
            if (physicalCellId < 0 || physicalCellId > RansimControllerServices.maxPciValueAllowed) {
                log.info("NewPhysicalCellId is empty or invalid");
                result = 400;
            } else {
                long oldPciId = ransimRepo.getNRPCI(modifyCell.getCellLocalId());
                if (physicalCellId != oldPciId) {
                    updatePciOperationsTable(nodeId, source, physicalCellId, oldPciId);
                    NRCellDU nrCellDU = ransimRepo.getNRCellDUDetail(modifyCell.getCellLocalId());
		    nrCellDU.setnRPCI(physicalCellId);
		    ransimRepo.mergeNRCellDU(nrCellDU);
                }

                NRCellCU neighbors = ransimRepo.getNRCellCUDetail(Integer.valueOf(nodeId));
		List<NRCellRelation> oldNbrList = new ArrayList<NRCellRelation>(neighbors.getNrCellRelationsList());
		List<NRCellRelation> oldNbrListWithHo = new ArrayList<NRCellRelation>();

		for (NRCellRelation cell : oldNbrList) {
                   if (cell.getisHOAllowed()) {
                      oldNbrListWithHo.add(cell);
		   }
		}

                boolean flag = false;

                List<NRCellRelation> addedNbrs = new ArrayList<NRCellRelation>();
		List<NRCellRelation> deletedNbrs = new ArrayList<NRCellRelation>();

		String nbrsDel = "";

		List<String> oldNbrsArr = new ArrayList<String>();
		for (NRCellRelation cell : oldNbrListWithHo) {
		   oldNbrsArr.add(cell.getIdNRCellRelation().toString());
		}

		List<String> newNbrsArr = new ArrayList<String>();
		for (NRCellRelation cell : newNbrs) {
		   newNbrsArr.add(cell.getIdNRCellRelation().toString());											
	    	}
		
	        for (NRCellRelation cell : oldNbrListWithHo) {
                    if (!newNbrsArr.contains(cell.getIdNRCellRelation())) {
                        if (!flag) {
                            flag = true;
                        }
                        deletedNbrs.add(cell);
                        if (nbrsDel == "") {
                            nbrsDel = String.valueOf(cell.getIdNRCellRelation());
                        } else {
                            nbrsDel += "," + cell.getIdNRCellRelation();
                        }
                        log.info("deleted cell: " + cell.getIdNRCellRelation() + cell.getisHOAllowed());

                    }
                }

                String nbrsAdd = "";

                for (NRCellRelation cell : newNbrs) {
                    if (!cell.getisHOAllowed()) {
                        addedNbrs.add(cell);
                    } else {
                        if (!oldNbrsArr.contains(cell.getIdNRCellRelation())) {
                            addedNbrs.add(cell);
                            if (nbrsAdd == "") {
                                nbrsAdd = String.valueOf(cell.getIdNRCellRelation());
                            } else {
                                nbrsAdd += "," + cell.getIdNRCellRelation();
                            }
                            log.info("added cell: " + cell.getIdNRCellRelation() + cell.getisHOAllowed());
                        }
                    }
                }
                
		List<NRCellRelation> newNeighborList = new ArrayList<NRCellRelation>(oldNbrList);
		for (NRCellRelation cell : deletedNbrs) {
                    NRCellRelation removeHo = new NRCellRelation(cell.getIdNRCellRelation(), cell.getnRTCI(), false, cell.getCellLocalId());
		    ransimRepo.mergeNRCellRel(removeHo);
		    newNeighborList.add(removeHo);
		}

		for (NRCellRelation cell : addedNbrs) {
                    ransimRepo.mergeNRCellRel(cell);
		    newNeighborList.add(cell);
		}

                if (!flag) {
                    if (newNbrs.size() != oldNbrList.size()) {
                        flag = true;
                    }
                }

                if (flag) {
                    updateNbrsOperationsTable(nodeId, source, nbrsAdd, nbrsDel);
                }

                if (newNbrs != null) {
                    neighbors.getNrCellRelationsList().clear();
		    List<NRCellRelation> updatedNbrList = newNeighborList;
                    neighbors.setNrCellRelationsList(updatedNbrList);
		    ransimRepo.mergeNRCellCU(neighbors);
                }

                generateFmData(source, modifyCell, newNeighborList);

                result = 200;
            }

        } else {
            result = 400;
        }

        return result;
    }

    public void checkCellsWithIssue() {

        try {

            for (String id : cellsWithIssues) {
                CellDetails currentCell = ransimRepo.getCellDetail(id);
                FmMessage fmDataMessage = new FmMessage();
                List<EventFm> data = new ArrayList<EventFm>();

                if (!currentCell.isPciCollisionDetected()) {
                    if (!currentCell.isPciConfusionDetected()) {

                        cellsWithIssues.remove(id);
                        CommonEventHeaderFm commonEventHeader = new CommonEventHeaderFm();
                        FaultFields faultFields = new FaultFields();

                        commonEventHeader.setStartEpochMicrosec(System.currentTimeMillis() * 1000);
                        commonEventHeader.setSourceName(currentCell.getNodeId());
                        commonEventHeader.setReportingEntityName(currentCell.getServerId());
                        String uuid = globalFmCellIdUuidMap.get(currentCell.getNodeId());
                        commonEventHeader.setSourceUuid(uuid);

                        faultFields.setAlarmCondition("RanPciCollisionConfusionOccurred");
                        faultFields.setEventSeverity("NORMAL");
                        faultFields.setEventSourceType("other");
                        faultFields.setSpecificProblem("Problem Solved");

                        commonEventHeader.setLastEpochMicrosec(System.currentTimeMillis() * 1000);

                        EventFm event = new EventFm();
                        event.setCommonEventHeader(commonEventHeader);
                        event.setFaultFields(faultFields);

                        data.add(event);
                    }
                }

                fmDataMessage.setFmEventList(data);

                if (!data.isEmpty()) {
                    sendFmData(currentCell.getServerId(), fmDataMessage);
                }

            }

        } catch (Exception eu) {
            log.error("Exception:", eu);
        }
    }

    public void updatePciOperationsTable(String nodeId, String source, long physicalCellId, long oldPciId) {

        OperationLog operationLog = new OperationLog();

        operationLog.setNodeId(nodeId);
        operationLog.setFieldName("nRPCI");
        operationLog.setOperation("Modify");
        operationLog.setSource(source);
        operationLog.setTime(System.currentTimeMillis());
        operationLog.setMessage("nRPCI value changed from " + oldPciId + " to " + physicalCellId);
        ransimRepo.mergeOperationLog(operationLog);
    }

    public void updateNbrsOperationsTable(String nodeId, String source, String addedNbrs, String deletedNbrs) {

        OperationLog operationLogNbrChng = new OperationLog();
        operationLogNbrChng.setNodeId(nodeId);
        operationLogNbrChng.setFieldName("Neighbors");
        operationLogNbrChng.setOperation("Modify");
        operationLogNbrChng.setSource(source);

        log.info(" Neighbors added " + addedNbrs + ".");
        log.info(" Neighbors removed " + deletedNbrs + ".");
        String message = "";
        if (!addedNbrs.equals("")) {
            message += " Neighbors added " + addedNbrs + ".";
        }

        if (!deletedNbrs.equals("")) {
            message += " Neighbors removed " + deletedNbrs + ".";
        }

        operationLogNbrChng.setMessage(message);
        operationLogNbrChng.setTime(System.currentTimeMillis());
        ransimRepo.mergeOperationLog(operationLogNbrChng);
    }

    /**
     * Sends PM message to the netconf agent through the websocket server.
     *
     * @param serverId
     *        Netconf agent - Server ID to which the message is sent.
     * @param pmMessage
     *        PM message to be sent.
     */
    void sendPmdata(String serverId, String pmMessage) {

        log.info("Sending PM message to netconf agent");

        String ipPort = RansimControllerServices.serverIdIpPortMapping.get(serverId);

        if (ipPort != null && !ipPort.trim().equals("")) {

            if (ipPort != null && !ipPort.trim().equals("")) {

                Session clSess = RansimControllerServices.webSocketSessions.get(ipPort);
                log.info("PM message. Netconf agent IP:" + ipPort);
                if (clSess != null) {
                    RansimWebSocketServer.sendPmMessage(pmMessage, clSess);
                    log.info("Pm Data jsonStr: " + pmMessage);
                    log.info("PM message sent to netconf agent");
                } else {
                    log.info("No client session for " + ipPort);
                }
            } else {
                log.info("Pm message not sent, ipPort is null");
            }
        } else {

            log.info("Pm message not sent, ipPort is null. Server Id: " + serverId);
        }

    }

    /**
     *
     * Reads the values PM parameter values from a dump file.
     *
     */
    public void readPmParameters() {

        File dumpFile = null;
        PmDataDump pmDump = null;
        String jsonString = "";
        dumpFile = new File("PM_Kpi_Data.json");

        BufferedReader br = null;

        try {
            log.info("Reading dump file");
            br = new BufferedReader(new FileReader(dumpFile));

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            jsonString = sb.toString();

            pmDump = new Gson().fromJson(jsonString, PmDataDump.class);
            log.info("Dump value: " + pmDump.getKpiDump().get(0).getParameter1());
            pmParameters = new ArrayList<PmParameters>(pmDump.getKpiDump());

        } catch (Exception eu) {
            log.info("Exception: ", eu);
        }
    }

    private static String getUuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * Sets values for all the parameters in the PM message
     *
     * @param nodeIdBad
     *        List of node Ids with bad performance values
     * @param nodeIdPoor
     *        List of node Ids with poor performance values
     * @return It returns the pm message
     */
    @Transactional
    public List<String> generatePmData(String nodeIdBad, String nodeIdPoor) {

	log.info(" nodeIdBad in generatePmData is" + nodeIdBad);
	log.info(" nodeIdPoor in generatePmData is" + nodeIdPoor);
        List<String> result = new ArrayList<>();

        String parameter1 = "";
        String successValue1 = "";
        String badValue1 = "";
        String poorValue1 = "";
        String parameter2 = "";
        String successValue2 = "";
        String badValue2 = "";
        String poorValue2 = "";

        try {

            if (next >= pmParameters.size()) {
                next = 0;
                log.info("next : " + next);
            }
            try {
                log.info("next : " + next);
                parameter1 = pmParameters.get(next).getParameter1();
		log.info("parameter1 is : " + parameter1);
                successValue1 = pmParameters.get(next).getSuccessValue1();
		log.info("successValue1 is : " + successValue1);
                badValue1 = pmParameters.get(next).getBadValue1();
		log.info("badValue1 is : " + badValue1);
                poorValue1 = pmParameters.get(next).getPoorValue1();
		log.info("poorValue1 is : " + poorValue1);
                parameter2 = pmParameters.get(next).getParameter2();
		log.info("parameter2 is : " + parameter2);
                successValue2 = pmParameters.get(next).getSuccessValue2();
		log.info("successValue2 is : " + successValue2);
                badValue2 = pmParameters.get(next).getBadValue2();
		log.info("badValue2 is : " + badValue2);
                poorValue2 = pmParameters.get(next).getPoorValue2();
		log.info("poorValue2 is : " + poorValue2);
                next++;
            } catch (Exception e) {
                log.info("Exception: ", e);
            }

            List<NetconfServers> cnl = ransimRepo.getNetconfServersList();
            log.debug("obtained data from db");
            String[] cellIdsBad = null;
            String[] cellIdsPoor = null;
            Set<String> nodeIdsBad = new HashSet<String>();
            Set<String> nodeIdsPoor = new HashSet<String>();

            if (nodeIdBad != null) {
                cellIdsBad = nodeIdBad.split(",");
                for (int a = 0; a < cellIdsBad.length; a++) {
                    nodeIdsBad.add(cellIdsBad[a].trim());
		    log.info("nodeIds bad is : " + nodeIdsBad);
		    
                }
            }
            if (nodeIdPoor != null) {
                cellIdsPoor = nodeIdPoor.split(",");
                for (int a = 0; a < cellIdsPoor.length; a++) {
                    nodeIdsPoor.add(cellIdsPoor[a].trim());
		    log.info("nodeIds poor is : " + nodeIdsPoor);
                }
            }

            for (int i = 0; i < cnl.size(); i++) {

                 long startTime = System.currentTimeMillis();
		 List<NRCellCU> cellList = new ArrayList<NRCellCU>(cnl.get(i).getCellList());
                 log.info(" CellList in generatePmData is : " + cellList);
		 List<EventPm> data = new ArrayList<EventPm>();
		 for (int j = 0; j < cellList.size(); j++) {

			 long startTimeCell = System.currentTimeMillis();
			 String nodeId = cellList.get(j).getCellLocalId().toString();
			 log.info(" nodeId in cellList in generatePmData is : " + nodeId);
			 String reportingEntityName = cellList.get(j).getgNBCUCPFunction().getgNBCUName();
			 EventPm event = new EventPm();
			 CommonEventHeaderPm commonEventHeader = new CommonEventHeaderPm();
			 commonEventHeader.setSourceName(nodeId);
			 commonEventHeader.setReportingEntityName(reportingEntityName);
			 commonEventHeader.setStartEpochMicrosec(System.currentTimeMillis() * 1000);
			 String uuid = globalPmCellIdUuidMap.get(nodeId);
			 if (uuid == null) {
				 uuid = getUuid();
				 globalPmCellIdUuidMap.put(nodeId, uuid);
			 }
			 commonEventHeader.setSourceUuid(uuid);
			 Measurement measurement = new Measurement();
			 measurement.setMeasurementInterval(180);
			 
			 GetNeighborList cellNbrList = generateNeighborList(nodeId);
			 log.info("Get CellCU neighbourList with HO is : " + cellNbrList.getCUCellsWithHo());

			 long startTimeCheckBadPoor = System.currentTimeMillis();
			 boolean checkBad = false;
			 boolean checkPoor = false;
			 int countBad = 0;
			 int countPoor = 0;
			 log.info( "nodeIdsBad in for loop is" + nodeIdsBad);
			 log.info( "nodeIdsPoor in for loop is" + nodeIdsPoor);
			 
			 if (nodeIdsBad.contains(nodeId)) {
				 log.info( " inside nodeIdsBad if condition " + nodeId);
				 checkBad = true;
				 countBad = (int) (cellNbrList.getCUCellsWithHo().size() * 0.2);
				 log.info( " check bad is : " + checkBad);
				 log.info( " count bad is : " + countBad);
			 }
			 
			 if (nodeIdsPoor.contains(nodeId)) {
				 log.info( " inside nodeIdsPoor if condition " + nodeId);
				 checkPoor = true;
				 countPoor = (int) (cellNbrList.getCUCellsWithHo().size() * 0.2);
				 log.info( " check poor is : " + checkPoor);
				 log.info( " count bad is : " + countPoor);
			 }

			 long endTimeCheckBadPoor = System.currentTimeMillis();
			 log.debug("Time taken CheckBadPoor : " + (endTimeCheckBadPoor - startTimeCheckBadPoor));
			 List<AdditionalMeasurements> additionalMeasurements = new ArrayList<AdditionalMeasurements>();
			 if (checkPoor || checkBad) {
				 Collections.sort(cellNbrList.getCUCellsWithHo());
				 for (int k = 0; k < cellNbrList.getCUCellsWithHo().size(); k++) {
					 AdditionalMeasurements addMsmnt = new AdditionalMeasurements();
					 addMsmnt.setName(cellNbrList.getCUCellsWithHo().get(k).getCellLocalId().toString());
					 Map<String, String> hashMap = new HashMap<String, String>();
					 hashMap.put("networkId", "RAN001");
					 hashMap.put(parameter1, successValue1);
					 if (checkBad == true) {
						 if (countBad > 0) {
							 log.info("countBad: " + countBad);
							 hashMap.put(parameter2, badValue2);
							 countBad--;
						 } else {
							 hashMap.put(parameter2, successValue2);
						 }
					 } else if (checkPoor == true) {
						 if (countPoor > 0) {
							 log.info("countPoor: " + countPoor);
							 hashMap.put(parameter2, poorValue2);
							 countPoor--;
						 } else {
							 hashMap.put(parameter2, successValue2);
						 }
					 }
					 addMsmnt.setHashMap(hashMap);
					 additionalMeasurements.add(addMsmnt);
				 }
			 } else {
				 for (int k = 0; k < cellNbrList.getCUCellsWithHo().size(); k++) {
					 AdditionalMeasurements addMsmnt = new AdditionalMeasurements();
					 addMsmnt.setName(cellNbrList.getCUCellsWithHo().get(k).getCellLocalId().toString());
					 Map<String, String> hashMap = new HashMap<String, String>();
					 hashMap.put("networkId", "RAN001");
					 hashMap.put(parameter1, successValue1);
					 hashMap.put(parameter2, successValue2);
					 addMsmnt.setHashMap(hashMap);
					 additionalMeasurements.add(addMsmnt);
				 }
			 }
			 commonEventHeader.setLastEpochMicrosec(System.currentTimeMillis() * 1000);
			 measurement.setAdditionalMeasurements(additionalMeasurements);
			 event.setCommonEventHeader(commonEventHeader);
			 event.setMeasurement(measurement);
			 data.add(event);
			 long endTimeCell = System.currentTimeMillis();
			 log.debug("Time taken to Process Cell list : " + (endTimeCell - startTimeCell));
		 }
		 long endTime = System.currentTimeMillis();
		 log.info("Time taken for generating PM data for " + cnl.get(i).getServerId() + " : "
				 + (endTime - startTime));
		 PmMessage msg = new PmMessage();
		 if (data.size() > 0) {
			 msg.setEventPmList(data);
			 Gson gson = new Gson();
			 String jsonStr = gson.toJson(msg);
			 sendPmdata(cnl.get(i).getServerId(), jsonStr);
			 result.add(jsonStr);
		 }
	    }
	} catch (Exception e) {
		log.info("Exception in string builder", e);
	}
	return result;
    }
		  
    /**	  
     * Sets the value for all fields in the FM data for individual cell.
     *
     * @param networkId
     *        Network Id of the cell
     * @param ncServer
     *        Server Id of the cell
     * @param cellId
     *        Node Id of the cell
     * @param issue
     *        Contains the collision/confusion details of the cess
     * @return returns List of EventFm objects, with all the necessary parameters.
     */
    public static List<EventFm> setEventFm(String networkId, String ncServer, String cellId, FmAlarmInfo issue) {

        log.info("Inside generate FmData");
        List<EventFm> eventList = new ArrayList<EventFm>();
        EventFm event = new EventFm();

        try {

            CommonEventHeaderFm commonEventHeader = new CommonEventHeaderFm();
            FaultFields faultFields = new FaultFields();
            String uuid = globalFmCellIdUuidMap.get(cellId);
            if (uuid == null) {
                uuid = getUuid();
                globalFmCellIdUuidMap.put(cellId, uuid);
            }
            commonEventHeader.setSourceUuid(uuid);
            faultFields.setAlarmCondition("RanPciCollisionConfusionOccurred");
            faultFields.setEventSeverity("CRITICAL");
            faultFields.setEventSourceType("other");
            Map<String, String> alarmAdditionalInformation = new HashMap<String, String>();
            alarmAdditionalInformation.put("networkId", "10000000");
            faultFields.setAlarmAdditionalInformation(alarmAdditionalInformation);

            if (!issue.getCollisions().isEmpty()) {
                commonEventHeader.setStartEpochMicrosec(System.currentTimeMillis() * 1000);
                commonEventHeader.setSourceName(cellId);
                commonEventHeader.setReportingEntityName(ncServer);
                faultFields.setSpecificProblem(issue.getCollisions());
                faultFields.setEventCategory("PCICollision");
                commonEventHeader.setLastEpochMicrosec(System.currentTimeMillis() * 1000);
                event.setCommonEventHeader(commonEventHeader);
                event.setFaultFields(faultFields);
                eventList.add(event);
            }

            for (Map.Entry<Integer, Integer> set : issue.getConfusions().entrySet()) {
                commonEventHeader.setStartEpochMicrosec(System.currentTimeMillis() * 1000);
                commonEventHeader.setSourceName(cellId);
                commonEventHeader.setReportingEntityName(ncServer);
                faultFields.setSpecificProblem(String.valueOf(set.getValue()));
                faultFields.setEventCategory("PCIConfusion");
                commonEventHeader.setLastEpochMicrosec(System.currentTimeMillis() * 1000);
                event.setCommonEventHeader(commonEventHeader);
                event.setFaultFields(faultFields);
                eventList.add(event);
            }

        } catch (Exception e) {
            log.info("Exception: ", e);
        }

        return eventList;

    }

    /**
     * It checks if the cell or any of its neighbors have collision/confusion
     * issue. If there are any issues it generates the FM data for the entire
     * cluster
     *
     * @param source
     *        The source from which the cell modification has been
     *        triggered.
     * @param cell
     *        Details of the given cell.
     * @param newNeighborList
     *        Neighbor list of the given cell.
     */
    public void generateFmData(String source, NRCellCU cell, List<NRCellRelation> nRcellRelList) {

        List<EventFm> listCellIssue = new ArrayList<EventFm>();
        Set<String> ncs = new HashSet<>();
        log.info("Generating Fm data");
        FmAlarmInfo op1 = setCollisionConfusionFromFile(cell.getCellLocalId());
	String networkId = "ran-1";
	log.info("networkId is : " + networkId);

        if (source.equals("GUI")) {
            if (op1.getProblem().equals("CollisionAndConfusion") || op1.getProblem().equals("Collision")
                    || op1.getProblem().equals("Confusion")) {
                log.info("op1: " + op1);
                List<EventFm> lci = setEventFm(networkId, cell.getgNBCUCPFunction().getgNBCUName(), cell.getCellLocalId().toString(), op1);
                listCellIssue.addAll(lci);
                ncs.add(cell.getgNBCUCPFunction().getgNBCUName());
		log.info("Generating Fm data for: " + cell.getCellLocalId());
            }
        }

        for (NRCellRelation cd : nRcellRelList) {
            FmAlarmInfo op2 = setCollisionConfusionFromFile(cd.getIdNRCellRelation());
            NRCellCU nbrCell = ransimRepo.getNRCellCUDetail(cd.getIdNRCellRelation());

            if (source.equals("GUI")) {
                if (op2.getProblem().equals("CollisionAndConfusion") || op2.getProblem().equals("Collision")
                        || op2.getProblem().equals("Confusion")) {
                    List<EventFm> lci = setEventFm(networkId, nbrCell.getgNBCUCPFunction().getgNBCUName(), 
				    String.valueOf(nbrCell.getCellLocalId()), op2);
		    log.info("FmData added:" + nbrCell.getCellLocalId());
		    listCellIssue.addAll(lci);
		    ncs.add(nbrCell.getgNBCUCPFunction().getgNBCUName());
		    log.info("Generating Fm data for: " + nbrCell.getCellLocalId());
                }
            }

        }

        if (source.equals("GUI")) {
            for (String nc : ncs) {

                FmMessage fmDataMessage = new FmMessage();
                List<EventFm> data = new ArrayList<EventFm>();
                log.info("listCellIssue.size(): " + listCellIssue.size());
                for (EventFm cellIssue : listCellIssue) {
                    if (cellIssue.getCommonEventHeader().getReportingEntityName().equals(nc)) {
                        data.add(cellIssue);
                        if (!cellsWithIssues.contains(cellIssue.getCommonEventHeader().getSourceName())) {
                            cellsWithIssues.add(cellIssue.getCommonEventHeader().getSourceName());
                        }

                    }
                }
                log.info("data.size(): " + data.size());

                if (data.size() > 0) {
                    fmDataMessage.setFmEventList(data);
                    log.info("Sending FM message: ");
                    sendFmData(nc, fmDataMessage);
                }

            }
        }

    }

    /**
     * Sends the FM data message to the netconf agent through the ransim
     * websocket server.
     *
     * @param serverId
     *        server id of the netconf agent
     * @param fmDataMessage
     *        FM message to be sent
     */
    public void sendFmData(String serverId, FmMessage fmDataMessage) {

        log.info("inside sendFmData");
        Gson gson = new Gson();
        String jsonStr = gson.toJson(fmDataMessage);

        log.info("Fm Data jsonStr: " + jsonStr);

        String ipPort = RansimControllerServices.serverIdIpPortMapping.get(serverId);

        if (ipPort != null && !ipPort.trim().equals("")) {

            log.info("Connection estabilished with ip: " + ipPort);
            if (ipPort != null && !ipPort.trim().equals("")) {
                Session clSess = RansimControllerServices.webSocketSessions.get(ipPort);
                if (clSess != null) {
                    log.info("FM message sent.");
                    RansimWebSocketServer.sendFmMessage(jsonStr, clSess);
                } else {
                    log.info("No client session for " + ipPort);
                }
            } else {
                log.info("No client for " + serverId);
            }
        } else {
            log.info("No client for ");
        }

    }

}
