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

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.onap.ransim.websocket.model.*;
import org.onap.ransim.rest.api.models.CellData;
import org.onap.ransim.rest.api.models.CellDetails;
import org.onap.ransim.rest.api.models.CellNeighbor;
import org.onap.ransim.rest.api.models.FmAlarmInfo;
import org.onap.ransim.rest.api.models.GetNeighborList;
import org.onap.ransim.rest.api.models.NbrDump;
import org.onap.ransim.rest.api.models.NeighborDetails;
import org.onap.ransim.rest.api.models.NeighborPmDetails;
import org.onap.ransim.rest.api.models.NeihborId;
import org.onap.ransim.rest.api.models.NetconfServers;
import org.onap.ransim.rest.api.models.OperationLog;
import org.onap.ransim.rest.api.models.PmDataDump;
import org.onap.ransim.rest.api.models.PmParameters;
import org.onap.ransim.rest.api.models.TopologyDump;
import org.onap.ransim.rest.client.RestClient;
import org.onap.ransim.websocket.model.FmMessage;
import org.onap.ransim.websocket.model.ModifyNeighbor;
import org.onap.ransim.websocket.model.ModifyPci;
import org.onap.ransim.websocket.model.Neighbor;
import org.onap.ransim.websocket.model.PmMessage;
import org.onap.ransim.websocket.model.SetConfigTopology;
import org.onap.ransim.websocket.model.Topology;
import org.onap.ransim.websocket.model.UpdateCell;
import org.onap.ransim.websocket.server.RansimWebSocketServer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RansimController {

	static Logger log = Logger.getLogger(RansimController.class
			.getName());

	private static RansimController rsController = null;
	Properties netconfConstants = new Properties();
	int gridSize = 10;
	boolean collision = false;
	String serverIdPrefix = "";
	static int numberOfCellsPerNcServer = 15;
	int numberOfMachines = 1;
	int numberOfProcessPerMc = 5;
	boolean strictValidateRansimAgentsAvailability = false;
	static public Map<String, Session> webSocketSessions = new ConcurrentHashMap<String, Session>();
	static Map<String, String> serverIdIpPortMapping = new ConcurrentHashMap<String, String>();

	static Map<String, String> globalNcServerUuidMap = new ConcurrentHashMap<String, String>();
	static List<String> unassignedServerIds = Collections
			.synchronizedList(new ArrayList<String>());
	static Map<String, List<String>> serverIdIpNodeMapping = new ConcurrentHashMap<String, List<String>>();
	int nextServerIdNumber = 1001;
	String sdnrServerIp = "";
	int sdnrServerPort = 0;
	static String sdnrServerUserid = "";
	static String sdnrServerPassword = "";
	static String dumpFileName = "";
	static long maxPciValueAllowed = 503;

	static RansimPciHandler rsPciHdlr = RansimPciHandler.getRansimPciHandler();

	private RansimController() {

	}

	/**
	 * To accesss variable of this class from another class.
	 *
	 * @return returns rscontroller constructor
	 */
	public static synchronized RansimController getRansimController() {
		if (rsController == null) {
			rsController = new RansimController();
			new KeepWebsockAliveThread(rsController).start();
		}
		return rsController;
	}

	private String checkIpPortAlreadyExists(String ipPort,
			Map<String, String> serverIdIpPortMapping) {
		String serverId = null;
		for (String key : serverIdIpPortMapping.keySet()) {
			String value = serverIdIpPortMapping.get(key);
			if (value.equals(ipPort)) {
				serverId = key;
				break;
			}
		}
		return serverId;
	}

	/**
	 * Add web socket sessions.
	 *
	 * @param ipPort
	 *            ip address for the session
	 * @param wsSession
	 *            session details
	 */
	public synchronized String addWebSocketSessions(String ipPort,
			Session wsSession) { 
		loadProperties();
		if (webSocketSessions.containsKey(ipPort)) {
			log.info("addWebSocketSessions: Client session "
					+ wsSession.getId() + " for " + ipPort
					+ " already exist. Removing old session.");
			webSocketSessions.remove(ipPort);
		}

		log.info("addWebSocketSessions: Adding Client session "
				+ wsSession.getId() + " for " + ipPort);
		webSocketSessions.put(ipPort, wsSession);
		String serverId = null;
		if (!serverIdIpPortMapping.containsValue(ipPort)) {
			if (unassignedServerIds.size() > 0) {
				log.info("addWebSocketSessions: No serverIds pending to assign for "
						+ ipPort);
				serverId = checkIpPortAlreadyExists(ipPort,
						serverIdIpPortMapping);
				if (serverId == null) {
					serverId = unassignedServerIds.remove(0);
				} else {
					if (unassignedServerIds.contains(serverId)) {
						unassignedServerIds.remove(serverId);
					}
				}
				log.info("RansCtrller = Available unassigned ServerIds :"
						+ unassignedServerIds);
				log.info("RansCtrller = addWebSocketSessions: Adding serverId "
						+ serverId + " for " + ipPort);
				serverIdIpPortMapping.put(serverId, ipPort);
				log.debug("RansCtrller = serverIdIpPortMapping >>>> :"
						+ serverIdIpPortMapping);
				mapServerIdToNodes(serverId);
				RansimControllerDatabase rsDb = new RansimControllerDatabase();
				try {

					NetconfServers server = rsDb.getNetconfServer(serverId);
					if (server != null) {
						server.setIp(ipPort.split(":")[0]);
						server.setNetconfPort(ipPort.split(":")[1]);
						rsDb.mergeNetconfServers(server);
					}

				} catch (Exception e1) {
					log.info("Exception mapServerIdToNodes :", e1);
				}
			} else {
				log.info("addWebSocketSessions: No serverIds pending to assign for "
						+ ipPort);
			}
		} else {
			for (String key : serverIdIpPortMapping.keySet()) {
				if (serverIdIpPortMapping.get(key).equals(ipPort)) {
					log.info("addWebSocketSessions: ServerId " + key + " for "
							+ ipPort + " is exist already");
					serverId = key;
					break;
				}
			}
		}
		return serverId;
	}

	/**
	 * Map server ID to the cells
	 * 
	 * @param serverId
	 *            Server ID
	 */
	private void mapServerIdToNodes(String serverId) {
		dumpSessionDetails();
		if (serverIdIpNodeMapping.containsKey(serverId)) {
			// already mapped.RansimController Do nothing.
		} else {
			List<String> nodeIds = new ArrayList<String>();
			RansimControllerDatabase rsDb = new RansimControllerDatabase();
			try {
				List<CellDetails> nodes = rsDb.getCellsWithNoServerIds();
				for (CellDetails cell : nodes) {
					cell.setServerId(serverId);
					nodeIds.add(cell.getNodeId());
					rsDb.mergeCellDetails(cell);
				}
				serverIdIpNodeMapping.put(serverId, nodeIds);
			} catch (Exception e1) {
				log.info("Exception mapServerIdToNodes :", e1);

			}
		}
	}

	/**
	 * It removes the web socket sessions.
	 *
	 * @param ipPort
	 *            ip address of the netconf server
	 */
	public synchronized void removeWebSocketSessions(String ipPort) {
		RansimControllerDatabase rsDb = new RansimControllerDatabase();
		log.info("remove websocket session request received for: " + ipPort);
		try {
			if (webSocketSessions.containsKey(ipPort)) {
				String removedServerId = null;
				for (String serverId : serverIdIpPortMapping.keySet()) {
					String ipPortVal = serverIdIpPortMapping.get(serverId);
					if (ipPortVal.equals(ipPort)) {
						if (!unassignedServerIds.contains(serverId)) {
							unassignedServerIds.add(serverId);
							log.info(serverId + "added in unassignedServerIds");
						}
						NetconfServers ns = rsDb.getNetconfServer(serverId);
						ns.setIp(null);
						ns.setNetconfPort(null);
						log.info(serverId + " ip and Port set as null ");
						rsDb.mergeNetconfServers(ns);
						removedServerId = serverId;
						break;
					}
				}
				serverIdIpPortMapping.remove(removedServerId);

				Session wsSession = webSocketSessions.remove(ipPort);
				log.info("removeWebSocketSessions: Client session "
						+ wsSession.getId() + " for " + ipPort
						+ " is removed. Server Id : " + removedServerId);
			} else {
				log.info("addWebSocketSessions: Client session for " + ipPort
						+ " not exist");
			}
		} catch (Exception e) {
			log.info("Exception in removeWebSocketSessions. e: " + e);
		}

	}

	/**
	 * Checks the number of ransim agents running.
	 *
	 * @param cellsToBeSimulated
	 *            number of cells to be simulated
	 * @return returns true if there are enough ransim agents running
	 */
	public boolean hasEnoughRansimAgentsRunning(int cellsToBeSimulated) {

		log.info("hasEnoughRansimAgentsRunning: numberOfCellsPerNCServer "
				+ numberOfCellsPerNcServer + " , webSocketSessions.size:"
				+ webSocketSessions.size() + " , cellsToBeSimulated:"
				+ cellsToBeSimulated);
		log.info(strictValidateRansimAgentsAvailability);

		if (strictValidateRansimAgentsAvailability) {
			if (numberOfCellsPerNcServer * webSocketSessions.size() < cellsToBeSimulated) {
				return false;
			}
		}
		return true;
	}

	/**
	 * It updates the constant values in the properties file.
	 */
	public void loadProperties() {
		InputStream input = null;
		try {
			input = new FileInputStream(
					"/tmp/ransim-install/config/ransim.properties");
			netconfConstants.load(input);
			serverIdPrefix = netconfConstants.getProperty("serverIdPrefix");
			numberOfCellsPerNcServer = Integer.parseInt(netconfConstants
					.getProperty("numberOfCellsPerNCServer"));
			numberOfMachines = Integer.parseInt(netconfConstants
					.getProperty("numberOfMachines"));
			numberOfProcessPerMc = Integer.parseInt(netconfConstants
					.getProperty("numberOfProcessPerMc"));
			strictValidateRansimAgentsAvailability = Boolean
					.parseBoolean(netconfConstants
							.getProperty("strictValidateRansimAgentsAvailability"));
			sdnrServerIp = netconfConstants.getProperty("sdnrServerIp");
			sdnrServerPort = Integer.parseInt(netconfConstants
					.getProperty("sdnrServerPort"));
			sdnrServerUserid = netconfConstants.getProperty("sdnrServerUserid");
			sdnrServerPassword = netconfConstants
					.getProperty("sdnrServerPassword");
			dumpFileName = netconfConstants.getProperty("dumpFileName");
			maxPciValueAllowed = Long.parseLong(netconfConstants
					.getProperty("maxPciValueAllowed"));

		} catch (Exception e) {
			log.info("Properties file error", e);
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (Exception ex) {
				log.info("Properties file error", ex);
			}
		}
	}

	/**
	 * The function adds the cell(with nodeId passed as an argument) to its
	 * netconf server list if the netconf server already exists. Else it will
	 * create a new netconf server in the NetconfServers Table and the cell into
	 * its list.
	 *
	 * @param nodeId
	 *            node Id of the cell
	 */
	static void setNetconfServers(String nodeId) {

		RansimControllerDatabase rsDb = new RansimControllerDatabase();
		CellDetails currentCell = rsDb.getCellDetail(nodeId);

		Set<CellDetails> newList = new HashSet<CellDetails>();
		try {
			if (currentCell != null) {
				NetconfServers server = rsDb.getNetconfServer(currentCell
						.getServerId());

				if (server == null) {

					server = new NetconfServers();
					server.setServerId(currentCell.getServerId());
				} else {
					newList.addAll(server.getCells());
				}

				newList.add(currentCell);
				server.setCells(newList);
				log.info("setNetconfServers: nodeId: " + nodeId + ", X:"
						+ currentCell.getGridX() + ", Y:"
						+ currentCell.getGridY() + ", ip: " + server.getIp()
						+ ", portNum: " + server.getNetconfPort()
						+ ", serverId:" + currentCell.getServerId());

				rsDb.mergeNetconfServers(server);

			}

		} catch (Exception eu) {
			log.info("/setNetconfServers Function Error", eu);

		}
	}

	private static double degToRadians(double angle) {
		double radians = 57.2957795;
		return (angle / radians);
	}

	private static double metersDeglon(double angle) {

		double d2r = degToRadians(angle);
		return ((111415.13 * Math.cos(d2r)) - (94.55 * Math.cos(3.0 * d2r)) + (0.12 * Math
				.cos(5.0 * d2r)));

	}

	private static double metersDeglat(double angle) {

		double d2r = degToRadians(angle);
		return (111132.09 - (566.05 * Math.cos(2.0 * d2r))
				+ (1.20 * Math.cos(4.0 * d2r)) - (0.002 * Math.cos(6.0 * d2r)));

	}

	/**
	 * generateClusterFromFile()
	 * 
	 * @throws IOException
	 */
	static void generateClusterFromFile() throws IOException {

		EntityManagerFactory emfactory = Persistence
				.createEntityManagerFactory("ransimctrlrdb");
		EntityManager entitymanager = emfactory.createEntityManager();
		log.info("Inside generateClusterFromFile");
		File dumpFile = null;
		String cellDetailsString = "";

		dumpFile = new File(dumpFileName);

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
			cellDetailsString = sb.toString();

			TopologyDump dumpTopo = new Gson().fromJson(cellDetailsString,
					TopologyDump.class);
			CellDetails cellsDb = new CellDetails();

			log.info("dumpTopo.getCellList().size():"
					+ dumpTopo.getCellList().size());
			for (int i = 0; i < dumpTopo.getCellList().size(); i++) {
				Gson g = new Gson();
				String pnt = g.toJson(dumpTopo.getCellList().get(i));
				log.info("Creating Cell:" + pnt);
				log.info("Creating Cell:"
						+ dumpTopo.getCellList().get(i).getCell().getNodeId());

				cellsDb = new CellDetails();
				entitymanager.getTransaction().begin();
				cellsDb.setNodeId(dumpTopo.getCellList().get(i).getCell()
						.getNodeId());
				cellsDb.setPhysicalCellId(dumpTopo.getCellList().get(i)
						.getCell().getPhysicalCellId());
				cellsDb.setLongitude(dumpTopo.getCellList().get(i).getCell()
						.getLongitude());
				cellsDb.setLatitude(dumpTopo.getCellList().get(i).getCell()
						.getLatitude());
				cellsDb.setServerId(dumpTopo.getCellList().get(i).getCell()
						.getPnfName());
				if (!unassignedServerIds.contains(cellsDb.getServerId())) {
					unassignedServerIds.add(cellsDb.getServerId());
				}
				cellsDb.setNetworkId(dumpTopo.getCellList().get(i).getCell()
						.getNetworkId());

				double lon = Float.valueOf(dumpTopo.getCellList().get(i)
						.getCell().getLongitude());
				double lat = Float.valueOf(dumpTopo.getCellList().get(i)
						.getCell().getLatitude());

				double xx = (lon - 0) * metersDeglon(0);
				double yy = (lat - 0) * metersDeglat(0);

				double rad = Math.sqrt(xx * xx + yy * yy);

				if (rad > 0) {
					double ct = xx / rad;
					double st = yy / rad;
					xx = rad * ((ct * Math.cos(0)) + (st * Math.sin(0)));
					yy = rad * ((st * Math.cos(0)) - (ct * Math.sin(0)));
				}

				cellsDb.setScreenX((float) (xx));
				cellsDb.setScreenY((float) (yy));

				List<String> attachedNoeds = serverIdIpNodeMapping.get(cellsDb
						.getServerId());
				log.info("Attaching Cell:"
						+ dumpTopo.getCellList().get(i).getCell().getNodeId()
						+ " to " + cellsDb.getServerId());
				if (attachedNoeds == null) {
					attachedNoeds = new ArrayList<String>();
				}
				attachedNoeds.add(cellsDb.getNodeId());
				serverIdIpNodeMapping.put(cellsDb.getServerId(), attachedNoeds);
				if (attachedNoeds.size() > numberOfCellsPerNcServer) {
					log.warn("Attaching Cell:"
							+ dumpTopo.getCellList().get(i).getCell()
									.getNodeId() + " to "
							+ cellsDb.getServerId()
							+ ", But it is exceeding numberOfCellsPerNcServer "
							+ numberOfCellsPerNcServer);
				}

				entitymanager.merge(cellsDb);
				entitymanager.flush();
				entitymanager.getTransaction().commit();

				setNetconfServers(cellsDb.getNodeId());
			}

			dumpSessionDetails();

			try {

				for (int i = 0; i < dumpTopo.getCellList().size(); i++) {

					String cellNodeId = dumpTopo.getCellList().get(i).getCell()
							.getNodeId();
					entitymanager.getTransaction().begin();

					// neighbor list with the corresponding node id
					CellNeighbor neighborList = entitymanager.find(
							CellNeighbor.class, cellNodeId);
					// cell with the corresponding nodeId
					CellDetails currentCell = entitymanager.find(
							CellDetails.class, cellNodeId);

					Set<NeighborDetails> newCell = new HashSet<NeighborDetails>();

					if (currentCell != null) {
						if (neighborList == null) {
							neighborList = new CellNeighbor();
							neighborList.setNodeId(cellNodeId);
						}
						List<NbrDump> neighboursFromFile = dumpTopo
								.getCellList().get(i).getNeighbor();
						log.info("Creating Neighbor for Cell :" + cellNodeId);
						for (NbrDump a : neighboursFromFile) {
							String id = a.getNodeId().trim();
							boolean noHo = Boolean.parseBoolean(a
									.getBlacklisted().trim());
							CellDetails neighborCell = entitymanager.find(
									CellDetails.class, id);
							NeighborDetails neighborDetails = new NeighborDetails(
									new NeihborId(currentCell.getNodeId(),
											neighborCell.getNodeId()), noHo);

							newCell.add(neighborDetails);
						}

						neighborList.setNeighborList(newCell);
						entitymanager.merge(neighborList);
						entitymanager.flush();

						entitymanager.getTransaction().commit();

						rsPciHdlr.setCollisionConfusionFromFile(cellNodeId);

					}

				}

			} catch (Exception e1) {
				log.info("Exception generateClusterFromFile :", e1);
				if (entitymanager.getTransaction().isActive()) {
					entitymanager.getTransaction().rollback();
				}
			}

			try {

				long startTimeSectorNumber = System.currentTimeMillis();
				for (int i = 0; i < dumpTopo.getCellList().size(); i++) {

					CellData icellData = dumpTopo.getCellList().get(i);
					CellDetails icell = entitymanager.find(CellDetails.class,
							icellData.getCell().getNodeId());
					int icount = icell.getSectorNumber();
					if (icount == 0) {
						entitymanager.getTransaction().begin();
						log.info("Setting sectorNumber for Cell(i) :"
								+ icell.getNodeId());
						int jcount = 0;
						for (int j = (i + 1); j < dumpTopo.getCellList().size(); j++) {

							CellData jcellData = dumpTopo.getCellList().get(j);
							if (icellData.getCell().getLatitude()
									.equals(jcellData.getCell().getLatitude())) {
								if (icellData
										.getCell()
										.getLongitude()
										.equals(jcellData.getCell()
												.getLongitude())) {

									if (icount == 0) {
										icount++;
										jcount = icount + 1;
									}

									CellDetails jcell = entitymanager.find(
											CellDetails.class, dumpTopo
													.getCellList().get(j)
													.getCell().getNodeId());

									jcell.setSectorNumber(jcount);
									log.info("Setting sectorNumber for Cell(j) :"
											+ jcell.getNodeId()
											+ " icell: "
											+ icell.getNodeId()
											+ " Sector number: " + jcount);
									entitymanager.merge(jcell);
									jcount++;
									if (jcount > 3) {
										break;
									}
								}
							}
						}
						icell.setSectorNumber(icount);
						entitymanager.merge(icell);
						entitymanager.flush();
						entitymanager.getTransaction().commit();
					}

				}

				long endTimeSectorNumber = System.currentTimeMillis();
				log.info("Time taken for setting sector number: "
						+ (endTimeSectorNumber - startTimeSectorNumber));

			} catch (Exception e3) {
				log.info("Exception generateClusterFromFile :", e3);
				if (entitymanager.getTransaction().isActive()) {
					entitymanager.getTransaction().rollback();
				}
			}

		} catch (Exception e) {
			log.info("Exception generateClusterFromFile :", e);
			if (entitymanager.getTransaction().isActive()) {
				entitymanager.getTransaction().rollback();
			}
		} finally {
			br.close();
			entitymanager.close();
			emfactory.close();
		}
	}

	/**
	 * The function deletes the cell from the database with the nodeId passed in
	 * the arguments. It removes the cell from its neighbor's neighbor list and
	 * the netconf server list.
	 *
	 * @param nodeId
	 *            node Id of the cell to be deleted.
	 * @return returns success or failure message
	 */
	public static String deleteCellFunction(String nodeId) {
		String result = "failure node dosent exist";
		log.info("deleteCellFunction called with nodeId :" + nodeId);
		RansimControllerDatabase rsDb = new RansimControllerDatabase();

		try {
			CellDetails deleteCelldetail = rsDb.getCellDetail(nodeId);

			CellNeighbor deleteCellNeighbor = rsDb.getCellNeighbor(nodeId);

			if (deleteCelldetail != null) {
				if (deleteCellNeighbor != null) {
					List<CellNeighbor> cellNeighborList = rsDb
							.getCellNeighborList();
					for (CellNeighbor cellNeighbors : cellNeighborList) {
						Set<NeighborDetails> currentCellNeighbors = new HashSet<NeighborDetails>(
								cellNeighbors.getNeighborList());

						NeihborId deleteNeighborDetail = new NeihborId(
								cellNeighbors.getNodeId(),
								deleteCelldetail.getNodeId());

						if (currentCellNeighbors.contains(deleteNeighborDetail)) {
							log.info("Deleted Cell is Neighbor of NodeId : "
									+ cellNeighbors.getNodeId());
							currentCellNeighbors.remove(deleteNeighborDetail);
							cellNeighbors.setNeighborList(currentCellNeighbors);
							rsDb.mergeCellNeighbor(cellNeighbors);
						}
					}

					deleteCellNeighbor.getNeighborList().clear();
					rsDb.deleteCellNeighbor(deleteCellNeighbor);
				}

				rsDb.deleteCellDetails(deleteCelldetail);
				result = "cell has been deleted from the database";
			} else {
				log.info("cell id does not exist");
				result = "failure nodeId dosent exist";
				return result;
			}
		} catch (Exception eu) {
			log.info("Exception deleteCellFunction :", eu);

			result = "exception in function";
		}
		return result;
	}

	/**
	 * Send configuration details to all the netconf server.
	 */
	public void sendInitialConfigAll() {
		RansimControllerDatabase rsDb = new RansimControllerDatabase();
		try {
			dumpSessionDetails();
			List<NetconfServers> ncServers = rsDb.getNetconfServersList();
			for (NetconfServers netconfServers : ncServers) {
				String ipPortKey = serverIdIpPortMapping.get(netconfServers
						.getServerId());
				if (ipPortKey == null || ipPortKey.trim().equals("")) {
					log.info("No client for " + netconfServers.getServerId());
					for (String ipPortKeyStr : webSocketSessions.keySet()) {
						if (!serverIdIpPortMapping.containsValue(ipPortKeyStr)) {
							serverIdIpPortMapping.put(
									netconfServers.getServerId(), ipPortKeyStr);
							ipPortKey = ipPortKeyStr;
							break;
						}
					}
				}
				if (ipPortKey != null && !ipPortKey.trim().equals("")) {
					Session clSess = webSocketSessions.get(ipPortKey);
					if (clSess != null) {
						sendInitialConfig(netconfServers.getServerId());
						try {
							String[] agentDetails = ipPortKey.split(":");
							new RestClient().sendMountRequestToSdnr(
									netconfServers.getServerId(), sdnrServerIp,
									sdnrServerPort, agentDetails[0],
									agentDetails[1], sdnrServerUserid,
									sdnrServerPassword);
						} catch (Exception ex1) {
							log.info("Ignoring exception", ex1);
						}

					} else {
						log.info("No session for " + ipPortKey);
					}
				}
			}
		} catch (Exception eu) {
			log.info("Exception:", eu);
		}
	}

	/**
	 * Sends initial configuration details of the cells for a new netconf server
	 * that has been started.
	 *
	 * @param ipPortKey
	 *            ip address details of the netconf server
	 */
	public void sendInitialConfigForNewAgent(String ipPortKey, String serverId) {
		try {
			dumpSessionDetails();
			if (ipPortKey != null && !ipPortKey.trim().equals("")) {
				if (serverId != null && !serverId.trim().equals("")) {
					Session clSess = webSocketSessions.get(ipPortKey);
					if (clSess != null) {
						String[] agentDetails = ipPortKey.split(":");
						sendInitialConfig(serverId);
						new RestClient().sendMountRequestToSdnr(serverId,
								sdnrServerIp, sdnrServerPort, agentDetails[0],
								agentDetails[1], sdnrServerUserid,
								sdnrServerPassword);
					} else {
						log.info("No session for " + ipPortKey);
					}
				} else {
					log.info("No serverid for " + ipPortKey);
				}
			} else {
				log.info("Invalid ipPortKey " + ipPortKey);
			}
		} catch (Exception eu) {
			log.info("Exception:", eu);
		}
	}

	/**
	 * To send the initial configration to the netconf server.
	 *
	 * @param serverId
	 *            ip address details of the netconf server
	 */
	public void sendInitialConfig(String serverId) {

		RansimControllerDatabase rsDb = new RansimControllerDatabase();
		try {
			NetconfServers server = rsDb.getNetconfServer(serverId);
			log.info("sendInitialConfig: serverId:" + serverId + ", server:"
					+ server);
			if (server == null) {
				return;
			}

			String ipPortKey = serverIdIpPortMapping.get(serverId);

			log.info("sendInitialConfig: ipPortKey:" + ipPortKey);

			List<CellDetails> cellList = new ArrayList<CellDetails>(
					server.getCells());

			List<Topology> config = new ArrayList<Topology>();

			for (int i = 0; i < server.getCells().size(); i++) {
				Topology cell = new Topology();
				CellDetails currentCell = rsDb.getCellDetail(cellList.get(i)
						.getNodeId());
				CellNeighbor neighbor = rsDb.getCellNeighbor(cellList.get(i)
						.getNodeId());

				cell.setCellId("" + currentCell.getNodeId());
				cell.setPciId(currentCell.getPhysicalCellId());
				cell.setPnfName(serverId);

				List<Neighbor> nbrList = new ArrayList<Neighbor>();
				Set<NeighborDetails> nbrsDet = neighbor.getNeighborList();
				for (NeighborDetails cellDetails : nbrsDet) {
					Neighbor nbr = new Neighbor();
					CellDetails nbrCell = rsDb.getCellDetail(cellDetails
							.getNeigbor().getNeighborCell());
					nbr.setNodeId(nbrCell.getNodeId());
					nbr.setPhysicalCellId(nbrCell.getPhysicalCellId());
					nbr.setPnfName(nbrCell.getServerId());
					nbr.setServerId(nbrCell.getServerId());
					nbr.setPlmnId(nbrCell.getNetworkId());
					nbr.setBlacklisted(cellDetails.isBlacklisted());
					nbrList.add(nbr);
				}
				cell.setNeighborList(nbrList);
				config.add(i, cell);
			}

			SetConfigTopology topo = new SetConfigTopology();

			topo.setServerId(server.getServerId());
			String uuid = globalNcServerUuidMap.get(server.getServerId());
			if (uuid == null) {
				uuid = getUuid();
				globalNcServerUuidMap.put(server.getServerId(), uuid);
			}
			topo.setUuid(uuid);

			topo.setTopology(config);

			Gson gson = new Gson();
			String jsonStr = gson.toJson(topo);
			log.info("ConfigTopologyMessage: " + jsonStr);
			Session clSess = webSocketSessions.get(ipPortKey);
			RansimWebSocketServer.sendSetConfigTopologyMessage(jsonStr, clSess);

		} catch (Exception eu) {
			log.info("Exception:", eu);
		}

	}

	private static String getUuid() {
		return UUID.randomUUID().toString();
	}

	/**
	 * The function alters the database information based on the modifications
	 * made in the SDNR.
	 *
	 * @param message
	 *            message received from the SDNR
	 * @param session
	 *            sends the session details
	 * @param ipPort
	 *            ip address of the netconf server
	 */
	public void handleModifyPciFromSdnr(String message, Session session,
			String ipPort) {
		log.info("handleModifyPciFromSDNR: message:" + message + " session:"
				+ session + " ipPort:" + ipPort);
		RansimControllerDatabase rcDb = new RansimControllerDatabase();
		ModifyPci modifyPci = new Gson().fromJson(message, ModifyPci.class);
		log.info("handleModifyPciFromSDNR: modifyPci:" + modifyPci.getCellId()
				+ "; pci: " + modifyPci.getPciId());
		String source = "Netconf";

		CellDetails cd = rcDb.getCellDetail(modifyPci.getCellId());
		long pci = cd.getPhysicalCellId();
		cd.setPhysicalCellId(modifyPci.getPciId());
		rcDb.mergeCellDetails(cd);
		rsPciHdlr.updatePciOperationsTable(modifyPci.getCellId(), source, pci,
				modifyPci.getPciId());
	}

	/**
	 * The function alters the database information based on the modifications
	 * made in the SDNR.
	 *
	 * @param message
	 *            message received from the SDNR
	 * @param session
	 *            sends the session details
	 * @param ipPort
	 *            ip address of the netconf server
	 */
	public void handleModifyNeighborFromSdnr(String message, Session session,
			String ipPort) {
		log.info("handleModifyAnrFromSDNR: message:" + message + " session:"
				+ session + " ipPort:" + ipPort);
		RansimControllerDatabase rsDb = new RansimControllerDatabase();
		ModifyNeighbor modifyNeighbor = new Gson().fromJson(message,
				ModifyNeighbor.class);
		log.info("handleModifyAnrFromSDNR: modifyPci:"
				+ modifyNeighbor.getCellId());
		CellDetails currentCell = rsDb
				.getCellDetail(modifyNeighbor.getCellId());
		List<NeighborDetails> neighborList = new ArrayList<NeighborDetails>();
		List<String> cellList = new ArrayList<String>();
		cellList.add(modifyNeighbor.getCellId());
		String nbrsAdd = "";
		String nbrsDel = "";
		String source = "Netconf";

		for (int i = 0; i < modifyNeighbor.getNeighborList().size(); i++) {
			if (modifyNeighbor.getNeighborList().get(i).isBlacklisted()) {
				NeighborDetails nd = new NeighborDetails(new NeihborId(
						modifyNeighbor.getCellId(), modifyNeighbor
								.getNeighborList().get(i).getNodeId()), true);
				rsDb.mergeNeighborDetails(nd);
				cellList.add(modifyNeighbor.getNeighborList().get(i)
						.getNodeId());
				if (nbrsAdd.equals("")) {
					nbrsDel = modifyNeighbor.getNeighborList().get(i)
							.getNodeId();
				} else {
					nbrsDel += ","
							+ modifyNeighbor.getNeighborList().get(i)
									.getNodeId();
				}
			} else {
				NeighborDetails nd = new NeighborDetails(new NeihborId(
						modifyNeighbor.getCellId(), modifyNeighbor
								.getNeighborList().get(i).getNodeId()), false);
				rsDb.mergeNeighborDetails(nd);
				cellList.add(modifyNeighbor.getNeighborList().get(i)
						.getNodeId());
				if (nbrsDel.equals("")) {
					nbrsAdd = modifyNeighbor.getNeighborList().get(i)
							.getNodeId();
				} else {
					nbrsAdd += ","
							+ modifyNeighbor.getNeighborList().get(i)
									.getNodeId();
				}
			}

		}

		for (String cl : cellList) {
			RansimPciHandler.setCollisionConfusionFromFile(cl);
		}

		log.info("neighbor list: " + neighborList);

		rsPciHdlr.updateNbrsOperationsTable(modifyNeighbor.getCellId(), source,
				nbrsAdd, nbrsDel);
	}

	/**
	 * The function sends the modification made in the GUI to the netconf
	 * server.
	 *
	 * @param cellId
	 *            node Id of the cell which was modified
	 * @param pciId
	 *            PCI number of the cell which was modified
	 */
	public void handleModifyPciFromGui(String cellId, long pciId) {
		log.info("handleModifyPciFromGUI: cellId:" + cellId + " pciId:" + pciId);
		RansimControllerDatabase rsDb = new RansimControllerDatabase();

		try {
			CellDetails currentCell = rsDb.getCellDetail(cellId);
			CellNeighbor neighborList = rsDb.getCellNeighbor(cellId);
			List<Neighbor> nbrList = new ArrayList<Neighbor>();
			Iterator<NeighborDetails> iter = neighborList.getNeighborList()
					.iterator();
			while (iter.hasNext()) {
				NeighborDetails nbCell = iter.next();
				Neighbor nbr = new Neighbor();
				CellDetails nbrCell = rsDb.getCellDetail(nbCell.getNeigbor()
						.getNeighborCell());

				nbr.setNodeId(nbrCell.getNodeId());
				nbr.setPhysicalCellId(nbrCell.getPhysicalCellId());
				nbr.setPnfName(nbrCell.getNodeName());
				nbr.setServerId(nbrCell.getServerId());
				nbr.setPlmnId(nbrCell.getNetworkId());
				nbrList.add(nbr);
			}

			String pnfName = currentCell.getServerId();
			String ipPort = serverIdIpPortMapping.get(pnfName);
			log.info("handleModifyPciFromGui:ipPort >>>>>>> " + ipPort);

			if (ipPort != null && !ipPort.trim().equals("")) {

				String[] ipPortArr = ipPort.split(":");
				Topology oneCell = new Topology(pnfName, pciId, cellId, nbrList);
				UpdateCell updatedPci = new UpdateCell(
						currentCell.getServerId(), ipPortArr[0], ipPortArr[1],
						oneCell);
				Gson gson = new Gson();
				String jsonStr = gson.toJson(updatedPci);
				if (ipPort != null && !ipPort.trim().equals("")) {
					Session clSess = webSocketSessions.get(ipPort);
					if (clSess != null) {
						RansimWebSocketServer.sendUpdateCellMessage(jsonStr,
								clSess);
						log.info("handleModifyPciFromGui, message: " + jsonStr);
					} else {
						log.info("No client session for " + ipPort);
					}
				} else {
					log.info("No client for " + currentCell.getServerId());
				}
			}

		} catch (Exception eu) {

			log.info("Exception:", eu);
		}
	}

	/**
	 * The function unmounts the connection with SDNR.
	 *
	 * @return returns null value
	 */
	public String stopAllCells() {
		RansimControllerDatabase rcDb = new RansimControllerDatabase();
		try {
			List<NetconfServers> ncServers = rcDb.getNetconfServersList();
			for (NetconfServers netconfServers : ncServers) {
				try {
					log.info("Unmount " + netconfServers.getServerId());
					new RestClient().sendUnmountRequestToSdnr(
							netconfServers.getServerId(), sdnrServerIp,
							sdnrServerPort, sdnrServerUserid,
							sdnrServerPassword);
				} catch (Exception e) {
					log.info("Ignore Exception:", e);
				}
				serverIdIpNodeMapping.clear();
			}
			return "Netconf servers unmounted.";
		} catch (Exception eu) {

			log.info("Exception:", eu);
			return "Error";
		}

	}

	/**
	 * Used to dump session details.
	 */
	synchronized public static void dumpSessionDetails() {

		try {

			log.info("serverIdIpPortMapping.size:"
					+ serverIdIpPortMapping.size() + "webSocketSessions.size"
					+ webSocketSessions.size());
			for (String key : serverIdIpPortMapping.keySet()) {
				String val = serverIdIpPortMapping.get(key);
				Session sess = webSocketSessions.get(val);
				log.info("ServerId:" + key + " IpPort:" + val + " Session:"
						+ sess);
			}
			for (String serverId : unassignedServerIds) {
				log.info("Unassigned ServerId:" + serverId);
			}
			for (String serverId : serverIdIpPortMapping.keySet()) {
				List<String> attachedNoeds = serverIdIpNodeMapping
						.get(serverId);
				if (attachedNoeds != null) {
					log.info("ServerId:" + serverId + " attachedNoeds.size:"
							+ attachedNoeds.size() + " nodes:"
							+ attachedNoeds.toArray());
				} else {
					log.info("ServerId:" + serverId + " attachedNoeds:" + null);
				}
			}
		} catch (Exception e) {
			log.info("Exception:", e);
		}
	}

}

class KeepWebsockAliveThread extends Thread {
	static Logger log = Logger
			.getLogger(KeepWebsockAliveThread.class.getName());
	RansimController rsCtrlr = null;

	KeepWebsockAliveThread(RansimController ctrlr) {
		rsCtrlr = ctrlr;
	}

	@Override
	public void run() {
		log.info("Inside KeepWebsockAliveThread run method");
		while (true) {
			for (String ipPort : rsCtrlr.webSocketSessions.keySet()) {
				try {
					Session sess = rsCtrlr.webSocketSessions.get(ipPort);
					RansimWebSocketServer.sendPingMessage(sess);
					log.debug("Sent ping message to Client ipPort:" + ipPort);
				} catch (Exception ex1) {
				}
			}
			try {
				Thread.sleep(10000);
			} catch (Exception ex) {
			}
		}
	}
}
