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

package org.onap.ransim.rest.api.controller;

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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.onap.ransim.rest.api.models.CellDetails;
import org.onap.ransim.rest.api.models.CellNeighbor;
import org.onap.ransim.rest.api.models.FmAlarmInfo;
import org.onap.ransim.rest.api.models.GetNeighborList;
import org.onap.ransim.rest.api.models.NeighborDetails;
import org.onap.ransim.rest.api.models.NetconfServers;
import org.onap.ransim.rest.api.models.OperationLog;
import org.onap.ransim.rest.api.models.PmDataDump;
import org.onap.ransim.rest.api.models.PmParameters;
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

import com.google.gson.Gson;

public class RansimPciHandler {

	private static RansimPciHandler rsPciHandler = null;
	
	private RansimPciHandler() {

	}

	/**
	 * To accesss variable of this class from another class.
	 *
	 * @return returns rscontroller constructor
	 */
	public static synchronized RansimPciHandler getRansimPciHandler() {
		if (rsPciHandler == null) {
			rsPciHandler = new RansimPciHandler();
		}
		return rsPciHandler;
	}

	static Logger log = Logger.getLogger(RansimPciHandler.class
			.getName());

	RansimController rsCtrlr = RansimController.getRansimController();

	static Map<String, String> globalFmCellIdUuidMap = new ConcurrentHashMap<String, String>();
	static Map<String, String> globalPmCellIdUuidMap = new ConcurrentHashMap<String, String>();

	Set<String> cellsWithIssues = new HashSet<>();

	List<PmParameters> pmParameters = new ArrayList<PmParameters>();
	int next = 0;

	static FmAlarmInfo setCollisionConfusionFromFile(String cellNodeId) {

		FmAlarmInfo result = new FmAlarmInfo();
		RansimControllerDatabase rsDb = new RansimControllerDatabase();

		try {

			boolean collisionDetected = false;
			boolean confusionDetected = false;
			List<Long> nbrPcis = new ArrayList<Long>();
			List<Long> ConfusionPcis = new ArrayList<Long>();
			int collisionCount = 0;
			int confusionCount = 0;
			CellDetails currentCell = rsDb.getCellDetail(cellNodeId);

			log.info("Setting confusion/collision for Cell :" + cellNodeId);

			GetNeighborList cellNbrDetails = generateNeighborList(cellNodeId);

			for (CellDetails firstLevelNbr : cellNbrDetails.getCellsWithHo()) {
				if (nbrPcis
						.contains(new Long(firstLevelNbr.getPhysicalCellId()))) {
					confusionDetected = true;
					if (ConfusionPcis.contains(firstLevelNbr
							.getPhysicalCellId())) {
						confusionCount++;
					} else {
						ConfusionPcis.add(firstLevelNbr.getPhysicalCellId());
						confusionCount = confusionCount + 2;
					}

				} else {
					nbrPcis.add(new Long(firstLevelNbr.getPhysicalCellId()));
				}

				if (currentCell.getPhysicalCellId() == firstLevelNbr
						.getPhysicalCellId()) {
					collisionDetected = true;
					collisionCount++;
				}
			}

			currentCell.setPciCollisionDetected(collisionDetected);
			currentCell.setPciConfusionDetected(confusionDetected);

			if (!currentCell.isPciCollisionDetected()
					&& !currentCell.isPciConfusionDetected()) {
				currentCell.setColor("#BFBFBF"); // GREY - No Issues
				result.setProblem("No Issues");
			} else if (currentCell.isPciCollisionDetected()
					&& currentCell.isPciConfusionDetected()) {
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

			result.setCollisionCount("" + collisionCount);
			result.setConfusionCount("" + confusionCount);

			rsDb.mergeCellDetails(currentCell);

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
	 *            Node Id of cell for which the neighbor list is generated
	 * @return Returns GetNeighborList object
	 */
	static GetNeighborList generateNeighborList(String nodeId) {

		EntityManagerFactory emfactory = Persistence
				.createEntityManagerFactory("ransimctrlrdb");
		EntityManager entitymanager = emfactory.createEntityManager();
		try {
			log.info("inside generateNeighborList for: " + nodeId);
			RansimControllerDatabase rsDb = new RansimControllerDatabase();
			CellNeighbor neighborList = entitymanager.find(CellNeighbor.class,
					nodeId);
			GetNeighborList result = new GetNeighborList();

			List<CellDetails> cellsWithNoHO = new ArrayList<CellDetails>();
			List<CellDetails> cellsWithHO = new ArrayList<CellDetails>();

			List<NeighborDetails> nbrList = new ArrayList<NeighborDetails>(
					neighborList.getNeighborList());
			long readCellDetail = 0;
			long checkBlacklisted = 0;

			for (int i = 0; i < nbrList.size(); i++) {

				CellDetails nbr = entitymanager.find(CellDetails.class, nbrList
						.get(i).getNeigbor().getNeighborCell());

				if (nbrList.get(i).isBlacklisted()) {
					cellsWithNoHO.add(nbr);
				} else {
					cellsWithHO.add(nbr);
				}

			}

			result.setNodeId(nodeId);
			result.setCellsWithHo(cellsWithHO);
			result.setCellsWithNoHo(cellsWithNoHO);

			return result;

		} catch (Exception eu) {
			log.info("/getNeighborList", eu);
			if (entitymanager.getTransaction().isActive()) {
				entitymanager.getTransaction().rollback();
			}
			return null;
		} finally {
			entitymanager.close();
			emfactory.close();
		}
	}

	static void checkCollisionAfterModify() {
		RansimControllerDatabase rsDb = new RansimControllerDatabase();
		try {
			List<CellDetails> checkCollisionConfusion = rsDb
					.getCellsWithCollisionOrConfusion();

			for (int i = 0; i < checkCollisionConfusion.size(); i++) {
				log.info(checkCollisionConfusion.get(i).getNodeId());
				setCollisionConfusionFromFile(checkCollisionConfusion.get(i)
						.getNodeId());
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
	 *            node Id of the cell
	 * @param physicalCellId
	 *            PCI number of the cell
	 * @param newNbrs
	 *            List of new neighbors for the cell
	 * @param source
	 *            The source from which cell modification has been triggered
	 * @return returns success or failure message
	 */
	public int modifyCellFunction(String nodeId, long physicalCellId,
			List<NeighborDetails> newNbrs, String source) {

		int result = 111;

		RansimControllerDatabase rsDb = new RansimControllerDatabase();
		log.info("modifyCellFunction nodeId:" + nodeId + ", physicalCellId:"
				+ physicalCellId);
		CellDetails modifyCell = rsDb.getCellDetail(nodeId);

		if (modifyCell != null) {
			if (physicalCellId < 0
					|| physicalCellId > rsCtrlr.maxPciValueAllowed) {
				log.info("NewPhysicalCellId is empty or invalid");
				result = 400;
			} else {
				long oldPciId = modifyCell.getPhysicalCellId();
				if (physicalCellId != oldPciId) {
					updatePciOperationsTable(nodeId, source, physicalCellId,
							oldPciId);

					modifyCell.setPhysicalCellId(physicalCellId);
					rsDb.mergeCellDetails(modifyCell);
				}

				CellNeighbor neighbors = rsDb.getCellNeighbor(nodeId);
				List<NeighborDetails> oldNbrList = new ArrayList<NeighborDetails>(
						neighbors.getNeighborList());
				List<NeighborDetails> oldNbrListWithHo = new ArrayList<NeighborDetails>();

				for (NeighborDetails cell : oldNbrList) {
					if (!cell.isBlacklisted()) {
						oldNbrListWithHo.add(cell);
					}
				}

				boolean flag = false;

				List<NeighborDetails> addedNbrs = new ArrayList<NeighborDetails>();
				List<NeighborDetails> deletedNbrs = new ArrayList<NeighborDetails>();

				String nbrsDel = "";

				List<String> oldNbrsArr = new ArrayList<String>();
				for (NeighborDetails cell : oldNbrListWithHo) {
					oldNbrsArr.add(cell.getNeigbor().getNeighborCell());
				}

				List<String> newNbrsArr = new ArrayList<String>();
				for (NeighborDetails cell : newNbrs) {
					newNbrsArr.add(cell.getNeigbor().getNeighborCell());
				}

				for (NeighborDetails cell : oldNbrListWithHo) {

					if (!newNbrsArr.contains(cell.getNeigbor()
							.getNeighborCell())) {
						if (!flag) {
							flag = true;
						}
						deletedNbrs.add(cell);
						if (nbrsDel == "") {
							nbrsDel = cell.getNeigbor().getNeighborCell();
						} else {
							nbrsDel += ","
									+ cell.getNeigbor().getNeighborCell();
						}
						log.info("deleted cell: "
								+ cell.getNeigbor().getNeighborCell()
								+ cell.isBlacklisted());

					}
				}

				String nbrsAdd = "";

				for (NeighborDetails cell : newNbrs) {
					if (cell.isBlacklisted()) {
						addedNbrs.add(cell);
					} else {
						if (!oldNbrsArr.contains(cell.getNeigbor()
								.getNeighborCell())) {
							addedNbrs.add(cell);
							if (nbrsAdd == "") {
								nbrsAdd = cell.getNeigbor().getNeighborCell();
							} else {
								nbrsAdd += ","
										+ cell.getNeigbor().getNeighborCell();
							}
							log.info("added cell: "
									+ cell.getNeigbor().getNeighborCell()
									+ cell.isBlacklisted());
						}
					}

				}
				List<NeighborDetails> newNeighborList = new ArrayList<NeighborDetails>(
						oldNbrList);
				for (NeighborDetails cell : deletedNbrs) {
					NeighborDetails removeHo = new NeighborDetails(
							cell.getNeigbor(), true);
					rsDb.mergeNeighborDetails(removeHo);
					newNeighborList.add(removeHo);
				}

				for (NeighborDetails cell : addedNbrs) {
					rsDb.mergeNeighborDetails(cell);
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
					neighbors.getNeighborList().clear();
					Set<NeighborDetails> updatedNbrList = new HashSet<NeighborDetails>(
							newNeighborList);
					neighbors.setNeighborList(updatedNbrList);
					rsDb.mergeCellNeighbor(neighbors);
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

		EntityManagerFactory emfactory = Persistence
				.createEntityManagerFactory("ransimctrlrdb");
		EntityManager entitymanager = emfactory.createEntityManager();
		try {

			for (String id : cellsWithIssues) {
				CellDetails currentCell = entitymanager.find(CellDetails.class,
						id);
				FmMessage fmDataMessage = new FmMessage();
				List<EventFm> data = new ArrayList<EventFm>();

				if (!currentCell.isPciCollisionDetected()) {
					if (!currentCell.isPciConfusionDetected()) {

						cellsWithIssues.remove(id);
						CommonEventHeaderFm commonEventHeader = new CommonEventHeaderFm();
						FaultFields faultFields = new FaultFields();

						commonEventHeader.setStartEpochMicrosec(System
								.currentTimeMillis() * 1000);
						commonEventHeader
								.setSourceName(currentCell.getNodeId());
						commonEventHeader.setReportingEntityName(currentCell
								.getServerId());
						String uuid = globalFmCellIdUuidMap.get(currentCell
								.getNodeId());
						commonEventHeader.setSourceUuid(uuid);

						faultFields
								.setAlarmCondition("RanPciCollisionConfusionOccurred");
						faultFields.setEventSeverity("NORMAL");
						faultFields.setEventSourceType("other");
						faultFields.setSpecificProblem("Problem Solved");

						commonEventHeader.setLastEpochMicrosec(System
								.currentTimeMillis() * 1000);

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
			if (entitymanager.getTransaction().isActive()) {
				entitymanager.getTransaction().rollback();
			}
			log.info("Exception:", eu);
		} finally {
			entitymanager.close();
			emfactory.close();
		}

	}

	void updatePciOperationsTable(String nodeId, String source, long physicalCellId, long oldPciId) {
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        OperationLog operationLog = new OperationLog();
        
        entitymanager.getTransaction().begin();
        operationLog.setNodeId(nodeId);
        operationLog.setFieldName("PCID");
        operationLog.setOperation("Modify");
        operationLog.setSource(source);
        operationLog.setTime(System.currentTimeMillis());
        operationLog.setMessage("PCID value changed from " + oldPciId + " to " + physicalCellId);
        entitymanager.merge(operationLog);
        entitymanager.flush();
        entitymanager.getTransaction().commit();
    }
    
    void updateNbrsOperationsTable(String nodeId, String source, String addedNbrs, String deletedNbrs) {
        
        EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("ransimctrlrdb");
        EntityManager entitymanager = emfactory.createEntityManager();
        entitymanager.getTransaction().begin();
        OperationLog operationLogNbrChng = new OperationLog();
        operationLogNbrChng.setNodeId(nodeId);
        operationLogNbrChng.setFieldName("Neighbors");
        operationLogNbrChng.setOperation("Modify");
        operationLogNbrChng.setSource(source);
        
        log.info(" Neighbors added " + addedNbrs + ".");
        log.info(" Neighbors removed " + deletedNbrs + ".");
        String message = "";
        if(!addedNbrs.equals("")){
        	message += " Neighbors added " + addedNbrs + "." ;
        }
        
        if(!deletedNbrs.equals("")){
        	message += " Neighbors removed " + deletedNbrs + "." ;
        }
        
        operationLogNbrChng.setMessage(message);
        operationLogNbrChng.setTime(System.currentTimeMillis());
        entitymanager.merge(operationLogNbrChng);
        entitymanager.flush();
        entitymanager.getTransaction().commit();
        
    }
	
	/**
	 * Sends PM message to the netconf agent through the websocket server.
	 * 
	 * @param serverId
	 *            Netconf agent - Server ID to which the message is sent.
	 * @param pmMessage
	 *            PM message to be sent.
	 */
	void sendPmdata(String serverId, String pmMessage) {

		log.info("Sending PM message to netconf agent");

		String ipPort = rsCtrlr.serverIdIpPortMapping.get(serverId);

		if (ipPort != null && !ipPort.trim().equals("")) {

			String[] ipPortArr = ipPort.split(":");
			if (ipPort != null && !ipPort.trim().equals("")) {

				Session clSess = rsCtrlr.webSocketSessions.get(ipPort);
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

			log.info("Pm message not sent, ipPort is null. Server Id: "
					+ serverId);
		}

	}

	/**
	 * 
	 * Reads the values PM parameter values from a dump file.
	 * 
	 */
	public void readPmParameters() {

		File dumpFile = null;
		String kpiName = "";
		PmDataDump pmDump = null;
		String jsonString = "";
		int next = 0;
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
			log.info("Dump value: "
					+ pmDump.getKpiDump().get(0).getParameter1());
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
	 *            List of node Ids with bad performance values
	 * @param nodeIdPoor
	 *            List of node Ids with poor performance values
	 * @return It returns the pm message
	 */
	public List<String> generatePmData(String nodeIdBad, String nodeIdPoor) {

		List<String> result = new ArrayList<>();
		RansimControllerDatabase rcDb = new RansimControllerDatabase();

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
				successValue1 = pmParameters.get(next).getSuccessValue1();
				badValue1 = pmParameters.get(next).getBadValue1();
				poorValue1 = pmParameters.get(next).getPoorValue1();
				parameter2 = pmParameters.get(next).getParameter2();
				successValue2 = pmParameters.get(next).getSuccessValue2();
				badValue2 = pmParameters.get(next).getBadValue2();
				poorValue2 = pmParameters.get(next).getPoorValue2();
				next++;
			} catch (Exception e) {
				log.info("Exception: ", e);
			}

			List<NetconfServers> cnl = rcDb.getNetconfServersList();
			log.info("obtained data from db");
			String[] cellIdsBad = null;
			String[] cellIdsPoor = null;
			Set<String> nodeIdsBad = new HashSet<String>();
			Set<String> nodeIdsPoor = new HashSet<String>();

			if (nodeIdBad != null) {
				cellIdsBad = nodeIdBad.split(",");
				for (int a = 0; a < cellIdsBad.length; a++) {
					nodeIdsBad.add(cellIdsBad[a].trim());
				}
			}
			if (nodeIdPoor != null) {
				cellIdsPoor = nodeIdPoor.split(",");
				for (int a = 0; a < cellIdsPoor.length; a++) {
					nodeIdsPoor.add(cellIdsPoor[a].trim());
				}
			}

			for (int i = 0; i < cnl.size(); i++) {

				long startTime = System.currentTimeMillis();
				List<CellDetails> cellList = new ArrayList<CellDetails>(cnl
						.get(i).getCells());
				List<EventPm> data = new ArrayList<EventPm>();

				for (int j = 0; j < cellList.size(); j++) {

					long startTimeCell = System.currentTimeMillis();
					String nodeId = cellList.get(j).getNodeId();
					EventPm event = new EventPm();
					CommonEventHeaderPm commonEventHeader = new CommonEventHeaderPm();
					commonEventHeader.setSourceName(nodeId);
					commonEventHeader.setStartEpochMicrosec(System
							.currentTimeMillis() * 1000);
					String uuid = globalPmCellIdUuidMap.get(nodeId);
					if (uuid == null) {
						uuid = getUuid();
						globalPmCellIdUuidMap.put(nodeId, uuid);
					}
					commonEventHeader.setSourceUuid(uuid);

					Measurement measurement = new Measurement();
					measurement.setMeasurementInterval(180);

					GetNeighborList cellNbrList = generateNeighborList(nodeId);

					long startTimeCheckBadPoor = System.currentTimeMillis();

					boolean checkBad = false;
					boolean checkPoor = false;
					int countBad = 0;
					int countPoor = 0;

					if (nodeIdsBad.contains(nodeId)) {
						checkBad = true;
						countBad = (int) (cellNbrList.getCellsWithHo().size() * 0.2);
					}
					if (nodeIdsPoor.contains(nodeId)) {
						checkPoor = true;
						countPoor = (int) (cellNbrList.getCellsWithHo().size() * 0.2);
					}

					long endTimeCheckBadPoor = System.currentTimeMillis();

					List<AdditionalMeasurements> additionalMeasurements = new ArrayList<AdditionalMeasurements>();
					if (checkPoor || checkBad) {

						Collections.sort(cellNbrList.getCellsWithHo());

						for (int k = 0; k < cellNbrList.getCellsWithHo().size(); k++) {
							AdditionalMeasurements addMsmnt = new AdditionalMeasurements();
							addMsmnt.setName(cellNbrList.getCellsWithHo()
									.get(k).getNodeId());
							Map<String, String> hashMap = new HashMap<String, String>();
							hashMap.put("networkId", cellNbrList
									.getCellsWithHo().get(k).getNetworkId());

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
									log.info("countBad: " + countPoor);
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
						for (int k = 0; k < cellNbrList.getCellsWithHo().size(); k++) {
							AdditionalMeasurements addMsmnt = new AdditionalMeasurements();
							addMsmnt.setName(cellNbrList.getCellsWithHo()
									.get(k).getNodeId());
							Map<String, String> hashMap = new HashMap<String, String>();

							hashMap.put("networkId", cellNbrList
									.getCellsWithHo().get(k).getNetworkId());

							hashMap.put(parameter1, successValue1);

							hashMap.put(parameter2, successValue2);

							addMsmnt.setHashMap(hashMap);
							additionalMeasurements.add(addMsmnt);
						}
					}

					commonEventHeader.setLastEpochMicrosec(System
							.currentTimeMillis() * 1000);
					measurement
							.setAdditionalMeasurements(additionalMeasurements);

					event.setCommonEventHeader(commonEventHeader);
					event.setMeasurement(measurement);

					data.add(event);
					long endTimeCell = System.currentTimeMillis();
				}

				long endTime = System.currentTimeMillis();
				log.info("Time taken for generating PM data for "
						+ cnl.get(i).getServerId() + " : "
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
	 *            Network Id of the cell
	 * @param ncServer
	 *            Server Id of the cell
	 * @param cellId
	 *            Node Id of the cell
	 * @param issue
	 *            Contains the collision/confusion details of the cess
	 * @return returns EventFm object, with all the necessary parameters.
	 */
	public static EventFm setEventFm(String networkId, String ncServer,
			String cellId, FmAlarmInfo issue) {

		log.info("Inside generate FmData");
		EventFm event = new EventFm();

		try {

			CommonEventHeaderFm commonEventHeader = new CommonEventHeaderFm();
			FaultFields faultFields = new FaultFields();

			commonEventHeader
					.setStartEpochMicrosec(System.currentTimeMillis() * 1000);
			commonEventHeader.setSourceName(cellId);
			commonEventHeader.setReportingEntityName(ncServer);

			String uuid = globalFmCellIdUuidMap.get(cellId);
			if (uuid == null) {
				uuid = getUuid();
				globalFmCellIdUuidMap.put(cellId, uuid);
			}
			commonEventHeader.setSourceUuid(uuid);

			if (issue.getProblem().equals("Collision")
					|| issue.getProblem().equals("Confusion")
					|| issue.getProblem().equals("CollisionAndConfusion")) {
				faultFields
						.setAlarmCondition("RanPciCollisionConfusionOccurred");
				faultFields.setEventSeverity("CRITICAL");
				faultFields.setEventSourceType("other");
				faultFields.setSpecificProblem(issue.getProblem());

				Map<String, String> alarmAdditionalInformation = new HashMap<String, String>();
				alarmAdditionalInformation.put("networkId", networkId);
				alarmAdditionalInformation.put("collisions",
						issue.getCollisionCount());
				alarmAdditionalInformation.put("confusions",
						issue.getConfusionCount());

				faultFields
						.setAlarmAdditionalInformation(alarmAdditionalInformation);

			}
			commonEventHeader
					.setLastEpochMicrosec(System.currentTimeMillis() * 1000);

			event.setCommonEventHeader(commonEventHeader);
			event.setFaultFields(faultFields);

		} catch (Exception e) {
			log.info("Exception: ", e);
		}

		return event;

	}

	/**
	 * It checks if the cell or any of its neighbors have collision/confusion
	 * issue. If there are any issues it generates the FM data for the entire
	 * cluster
	 * 
	 * @param source
	 *            The source from which the cell modification has been
	 *            triggered.
	 * @param cell
	 *            Details of the given cell.
	 * @param newNeighborList
	 *            Neighbor list of the given cell.
	 */
	public void generateFmData(String source, CellDetails cell,
			List<NeighborDetails> newNeighborList) {

		List<EventFm> listCellIssue = new ArrayList<EventFm>();
		Set<String> ncs = new HashSet<>();
		log.info("Generating Fm data");

		RansimControllerDatabase rsDb = new RansimControllerDatabase();

		FmAlarmInfo op1 = setCollisionConfusionFromFile(cell.getNodeId());

		if (source.equals("GUI")) {
			if (op1.getProblem().equals("CollisionAndConfusion")
					|| op1.getProblem().equals("Collision")
					|| op1.getProblem().equals("Confusion")) {
				log.info("op1: " + op1);
				EventFm lci = setEventFm(cell.getNetworkId(),
						cell.getServerId(), cell.getNodeId(), op1);
				listCellIssue.add(lci);
				ncs.add(cell.getServerId());
				log.info("Generating Fm data for: " + cell.getNodeId());
			}
		}

		for (NeighborDetails cd : newNeighborList) {
			FmAlarmInfo op2 = setCollisionConfusionFromFile(cd.getNeigbor()
					.getNeighborCell());
			CellDetails nbrCell = rsDb.getCellDetail(cd.getNeigbor()
					.getNeighborCell());

			if (source.equals("GUI")) {
				if (op2.getProblem().equals("CollisionAndConfusion")
						|| op2.getProblem().equals("Collision")
						|| op2.getProblem().equals("Confusion")) {
					EventFm lci = setEventFm(nbrCell.getNetworkId(),
							nbrCell.getServerId(), nbrCell.getNodeId(), op2);
					log.info("FmData added:" + nbrCell.getNodeId());
					listCellIssue.add(lci);
					ncs.add(nbrCell.getServerId());
					log.info("Generating Fm data for: " + nbrCell.getNodeId());
				}
			}

		}

		if (source.equals("GUI")) {
			for (String nc : ncs) {

				FmMessage fmDataMessage = new FmMessage();
				List<EventFm> data = new ArrayList<EventFm>();
				log.info("listCellIssue.size(): " + listCellIssue.size());
				for (EventFm cellIssue : listCellIssue) {
					if (cellIssue.getCommonEventHeader()
							.getReportingEntityName().equals(nc)) {
						data.add(cellIssue);
						if (!cellsWithIssues.contains(cellIssue
								.getCommonEventHeader().getSourceName())) {
							cellsWithIssues.add(cellIssue
									.getCommonEventHeader().getSourceName());
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
	 *            server id of the netconf agent
	 * @param fmDataMessage
	 *            FM message to be sent
	 */
	public void sendFmData(String serverId, FmMessage fmDataMessage) {

		log.info("inside sendFmData");
		Gson gson = new Gson();
		String jsonStr = gson.toJson(fmDataMessage);

		log.info("Fm Data jsonStr: " + jsonStr);

		String ipPort = rsCtrlr.serverIdIpPortMapping.get(serverId);

		if (ipPort != null && !ipPort.trim().equals("")) {

			String[] ipPortArr = ipPort.split(":");
			log.info("Connection estabilished with ip: " + ipPort);
			if (ipPort != null && !ipPort.trim().equals("")) {
				Session clSess = rsCtrlr.webSocketSessions.get(ipPort);
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
