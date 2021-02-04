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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.onap.ransim.rest.api.models.CellDetails;
import org.onap.ransim.rest.api.models.CellNeighbor;
import org.onap.ransim.rest.api.models.DeleteACellReq;
import org.onap.ransim.rest.api.models.GetACellDetailReq;
import org.onap.ransim.rest.api.models.GetNeighborList;
import org.onap.ransim.rest.api.models.GetNeighborListReq;
import org.onap.ransim.rest.api.models.GetNetconfServerDetailsReq;
import org.onap.ransim.rest.api.models.GetPmDataReq;
import org.onap.ransim.rest.api.models.ModifyACellReq;
import org.onap.ransim.rest.api.models.NeighborDetails;
import org.onap.ransim.rest.api.models.NeihborId;
import org.onap.ransim.rest.api.models.NetconfServers;
import org.onap.ransim.rest.api.models.OperationLog;
import org.onap.ransim.rest.api.models.Topology;
import org.onap.ransim.rest.api.models.TACells;
import org.onap.ransim.rest.api.services.RansimControllerServices;
import org.onap.ransim.rest.api.services.RANSliceConfigService;
import org.onap.ransim.rest.api.services.RansimRepositoryService;
import org.onap.ransim.rest.api.controller.RANSliceConfigController;
import org.onap.ransim.rest.api.handler.RansimPciHandler;
import org.onap.ransim.rest.api.handler.RansimSlicingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.onap.ransim.rest.api.services.SlicingPMDataGenerator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "Ran Simulator Controller Services")
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RansimController {

	static Logger log = Logger.getLogger(RansimController.class.getName());

	private static boolean isPmDataGenerating = false;
    private static boolean isIntelligentSlicingPmDataGenerating = false;    

	ScheduledExecutorService execService = Executors.newScheduledThreadPool(5);
    ScheduledExecutorService execServiceForIntelligentSlicing = Executors.newScheduledThreadPool(5);  
	ScheduledExecutorService closedLoopExecService;

	@Autowired
	RansimRepositoryService ransimRepo;
	@Autowired
	RansimControllerServices rscServices;
	@Autowired
	RansimPciHandler rsPciHdlr;
	@Autowired
	RANSliceConfigController ranSliceConfigController;
	@Autowired
	RANSliceConfigService ranSliceConfigService;
	@Autowired
	RansimSlicingHandler ranSliceHandler;
	@Autowired
        SlicingPMDataGenerator pmDataGenerator;

	/**
	 * Start the RAN network simulation.
	 *
	 * @param req gets the necessary details as a request of class type
	 *            StartSimulationReq
	 * @return returns Http status
	 * @throws Exception throws exceptions in the functions
	 */
	@ApiOperation("Starts the RAN network simulation")
	@RequestMapping(value = "/StartSimulation", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
			@ApiResponse(code = 500, message = "Cannot start the simulation") })
	public ResponseEntity<String> startSimulation(@RequestBody Map<String,String> dumpfile) throws Exception {

		List<CellDetails> cellList = ransimRepo.getCellDetailsList();
		if (!cellList.isEmpty()) {
			return new ResponseEntity<>("Already simulation is running.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		try {
			rscServices.loadProperties();
			RansimControllerServices.dumpFileName = dumpfile.containsKey("dumpfile") ? dumpfile.get("dumpfile"):null;
			long startTimeStartSimulation = System.currentTimeMillis();
			rscServices.generateClusterFromFile();
			rscServices.sendInitialConfigAll();
			long endTimeStartSimulation = System.currentTimeMillis();
			log.info("Time taken for start simulation : " + (endTimeStartSimulation - startTimeStartSimulation));

			return new ResponseEntity<String>(HttpStatus.OK);

		} catch (Exception eu) {
			log.info("/StartSimulation ", eu);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@ApiOperation("Starts the RAN network slice simulation")
	@RequestMapping(value = "/StartRanSliceSimulation", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
			@ApiResponse(code = 500, message = "Cannot start the simulation") })
	public ResponseEntity<String> startRanSliceSimulation() throws Exception {
		try {
			rscServices.loadGNBFunctionProperties();
			long startTimeStartSimulation = System.currentTimeMillis();
			rscServices.sendRanInitialConfigAll();
			long endTimeStartSimulation = System.currentTimeMillis();
			log.info("Time taken for start ran slice simulation : " + (endTimeStartSimulation - startTimeStartSimulation));
			return new ResponseEntity<>("Simulation started.", HttpStatus.OK);

		} catch (Exception eu) {
			log.info("/StartRanSliceSimulation ", eu);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * The performance Management Data of each cell will be sent to its netconf
	 * agent at a regular interval.
	 * 
	 * @param req Contains the details of node ids which will have bad and poor pm
	 *            values
	 * @return return HTTP status.
	 * 
	 */
	@ApiOperation("Generate PM data")
	@RequestMapping(value = "/GeneratePmData", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
			@ApiResponse(code = 500, message = "Cannot start the simulation") })
	public ResponseEntity<String> generatePmData(@RequestBody GetPmDataReq req) throws Exception {

		log.info("Generate PM Data - nodeId_bad: " + req.getNodeIdBad() + ", nodeId_poor: " + req.getNodeIdPoor());
		try {
			rsPciHdlr.readPmParameters();
			execService = Executors.newScheduledThreadPool(5);
			execService.scheduleAtFixedRate(() -> {

				rsPciHdlr.generatePmData(req.getNodeIdBad(), req.getNodeIdPoor());
				log.info("execService.isTerminated(): " + execService.isTerminated());

			}, 0, 300, TimeUnit.SECONDS);

			isPmDataGenerating = true;

			return new ResponseEntity<>("Request generated.", HttpStatus.OK);

		} catch (Exception eu) {
			log.error("Exception: ", eu);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	/**
	 * Terminates the ScheduledExecutorService which sends the PM data at regular
	 * interval.
	 * 
	 * @return returns HTTP status
	 * 
	 */
	@ApiOperation("stop PM data")
	@RequestMapping(value = "/stopPmData", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
			@ApiResponse(code = 500, message = "Cannot start the simulation") })
	public ResponseEntity<String> stopPmData() throws Exception {

		try {
			log.info("1. execService.isTerminated(): " + execService.isTerminated());
			if (!execService.isTerminated()) {
				execService.shutdown();
				log.info("2. execService.isTerminated(): " + execService.isTerminated());

			}
			isPmDataGenerating = false;
			return new ResponseEntity<>("PM data generated.", HttpStatus.OK);

		} catch (Exception eu) {
			log.error("Exception: ", eu);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Get the status of ScheduledExecutorService, whether active or terminated.
	 * 
	 * @return return the status
	 * 
	 */
	@ApiOperation("get PM data status")
	@RequestMapping(value = "/GetPmDataStatus", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
			@ApiResponse(code = 500, message = "Cannot get information") })
	public boolean getPmDataStatus() throws Exception {

		try {
			return isPmDataGenerating;
		} catch (Exception eu) {
			log.error("Exception: ", eu);
			return false;
		}

	}

	/**
	 * The function retrieves RAN simulation network topology.
	 *
	 * @return returns Http status
	 * @throws Exception throws exceptions in the functions
	 *
	 */
	@ApiOperation("Retrieves RAN simulation network topology")
	@RequestMapping(value = "/GetTopology", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
			@ApiResponse(code = 500, message = "Cannot retrieve the RAN simulation network topology details") })
	public ResponseEntity<String> getTopology() throws Exception {
		log.debug("Inside getTopology...");
		try {
			rsPciHdlr.checkCollisionAfterModify();
			List<CellDetails> cds = ransimRepo.getCellDetailsList();

			Topology top = new Topology();

			if (cds != null && cds.size() > 0) {
				top.setMinScreenX(cds.get(0).getScreenX());
				top.setMaxScreenX(cds.get(0).getScreenX());
				top.setMinScreenY(cds.get(0).getScreenY());
				top.setMaxScreenY(cds.get(0).getScreenY());

				for (int i = 0; i < cds.size(); i++) {
					if (cds.get(i).getScreenX() < top.getMinScreenX()) {
						top.setMinScreenX(cds.get(i).getScreenX());
					}
					if (cds.get(i).getScreenY() < top.getMinScreenY()) {
						top.setMinScreenY(cds.get(i).getScreenY());
					}

					if (cds.get(i).getScreenX() > top.getMaxScreenX()) {
						top.setMaxScreenX(cds.get(i).getScreenX());
					}
					if (cds.get(i).getScreenY() > top.getMaxScreenY()) {
						top.setMaxScreenY(cds.get(i).getScreenY());
					}
				}
				top.setCellTopology(cds);
			}
			top.setGridSize(rscServices.gridSize);

			Gson gson = new Gson();
			String jsonStr = gson.toJson(top);

			return new ResponseEntity<>(jsonStr, HttpStatus.OK);

		} catch (Exception eu) {
			log.error("GetTopology", eu);
			return new ResponseEntity<>("Failure", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * The function retrieves the neighbor list details for the cell with the
	 * mentioned nodeId.
	 *
	 * @param req gets the necessary details as a request of class type
	 *            GetNeighborListReq
	 * @return returns Http status
	 * @throws Exception throws exceptions in the functions
	 */
	@ApiOperation("Retrieves the neighbor list details for the cell with the mentioned nodeId")
	@RequestMapping(value = "/GetNeighborList", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
			@ApiResponse(code = 500, message = "Cannot Insert the given parameters") })
	public ResponseEntity<String> getNeighborList(@RequestBody GetNeighborListReq req) throws Exception {
		log.debug("Inside getNeighborList...");

		try {
			String jsonStr = "";

			GetNeighborList message = rsPciHdlr.generateNeighborList(req.getNodeId());

			if (message != null) {

				log.info("message.getNodeId(): " + message.getNodeId());

				Gson gson = new Gson();
				jsonStr = gson.toJson(message);
			}
			return new ResponseEntity<>(jsonStr, HttpStatus.OK);

		} catch (Exception eu) {
			log.info("/getNeighborList", eu);
			return new ResponseEntity<>("Failure", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * The function retrieves the neighbor list for the cell with the mentioned
	 * nodeId.
	 *
	 * @param req gets the necessary details as a request of class type
	 *            GetNeighborListReq
	 * @return returns Http status
	 * @throws Exception throws exceptions in the functions
	 */
	@ApiOperation("Retrieves the neighbor list details for the cell with the mentioned nodeId")
	@RequestMapping(value = "/GetNeighborBlacklistDetails", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
			@ApiResponse(code = 500, message = "Cannot Insert the given parameters") })
	public ResponseEntity<String> getNeighborBlacklistDetails(@RequestBody GetNeighborListReq req) throws Exception {
		log.debug("Inside getNeighborList...");

		try {
			String jsonStr = "";

			CellNeighbor neighborList = ransimRepo.getCellNeighbor(req.getNodeId());

			Map<String, String> result = new ConcurrentHashMap<String, String>();

			for (NeighborDetails nd : neighborList.getNeighborList()) {

				result.put(nd.getNeigbor().getNeighborCell(), "" + nd.isBlacklisted());
			}

			if (result != null) {
				Gson gson = new Gson();
				jsonStr = gson.toJson(result);
			}
			return new ResponseEntity<>(jsonStr, HttpStatus.OK);

		} catch (Exception eu) {
			log.error("/getNeighborList", eu);

			return new ResponseEntity<>("Failure", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Changes the pci number or nbr list for the given cell.
	 *
	 * @param req gets the necessary details as a request of class type
	 *            ModifyACellReq
	 * @return returns Http status
	 * @throws Exception throws exceptions in the functions
	 */
	@ApiOperation("Changes the pci number or nbr list for the given cell")
	@RequestMapping(value = "/ModifyACell", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
			@ApiResponse(code = 500, message = "Cannot update the PCI for the given cell") })
	public ResponseEntity<String> modifyACell(@RequestBody ModifyACellReq req) throws Exception {
		log.debug("Inside ModifyCell...");

		try {
			long startTimemodifyCell = System.currentTimeMillis();

			String nbrsStr = req.getNewNbrs();
			if (req.getNewNbrs() == null) {
				nbrsStr = "";
			}
			String source = "GUI";
			List<NeighborDetails> nbrsList = new ArrayList<NeighborDetails>();
			String[] newNbrsArr = nbrsStr.split(",");

			for (int i = 0; i < newNbrsArr.length; i++) {
				NeighborDetails cell = new NeighborDetails(new NeihborId(req.getNodeId(), newNbrsArr[i].trim()), false);
				nbrsList.add(cell);
			}

			int result = rsPciHdlr.modifyCellFunction(req.getNodeId(), req.getNewPhysicalCellId(), nbrsList, source);
			rscServices.handleModifyPciFromGui(req.getNodeId(), req.getNewPhysicalCellId());
			long endTimemodifyCell = System.currentTimeMillis();
			log.info("Time taken to modify cell : " + (endTimemodifyCell - startTimemodifyCell));

			if (result == 200) {
				return new ResponseEntity<String>(HttpStatus.OK);
			} else if (result == 400) {
				return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			} else {
				return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} catch (Exception eu) {
			log.error("Exception in modifyACell", eu);
			return new ResponseEntity<>("Cannot update the PCI for the given cell", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * The function changes the PCI number of the cell for the the mentioned nodeId.
	 *
	 * @param req gets the necessary details as a request of class type
	 *            GetACellDetailReq
	 * @return returns Http status
	 * @throws Exception throws exceptions in the functions
	 */
	@ApiOperation("Changes the pci number of the cell for the the mentioned nodeId")
	@PostMapping(value = "/GetACellDetail")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
			@ApiResponse(code = 500, message = "Cannot retrive the cell details for the given cell") })
	public ResponseEntity<String> getACellDetail(@RequestBody GetACellDetailReq req) throws Exception {

		log.debug("Inside GetACellDetailReq...");
		try {
			String jsonStr = null;

			CellDetails cd = ransimRepo.getCellDetail(req.getNodeId());
			if (cd != null) {
				Gson gson = new Gson();
				jsonStr = gson.toJson(cd);
			}
			return new ResponseEntity<>(jsonStr, HttpStatus.OK);

		} catch (Exception eu) {
			log.error("Exception in getACellDetail : {} ", eu);
			return new ResponseEntity<>("Cannot update the PCI for the given cell", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * The function deletes a cell with the mentioned nodeId.
	 *
	 * @param req gets the necessary details as a request of class type
	 *            DeleteACellReq
	 * @return returns Http status
	 * @throws Exception throws exceptions in the functions
	 */
	@ApiOperation("Deletes a cell with the mentioned nodeId")
	@RequestMapping(value = "/DeleteACell", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
			@ApiResponse(code = 500, message = "Cannot delete the given cell") })
	public ResponseEntity<String> deleteACell(@RequestBody DeleteACellReq req) throws Exception {
		log.debug("Inside delete cell...");

		try {
			long startTimeDeleteCell = System.currentTimeMillis();
			String result = rscServices.deleteCellFunction(req.getNodeId());
			long endTimeDeleteCell = System.currentTimeMillis();
			log.info("Time taken to delete cell : " + (endTimeDeleteCell - startTimeDeleteCell));
			return new ResponseEntity<String>(HttpStatus.OK);

		} catch (Exception eu) {
			log.error("Exception in deleteACell", eu);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * The function stops RAN network simulation and deletes all the cell data from
	 * the database.
	 *
	 * @return returns Http status
	 * @throws Exception throws exceptions in the functions
	 */
	@ApiOperation("Stops RAN network simulation and deletes all the cell data from the database")
	@RequestMapping(value = "/StopSimulation", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
			@ApiResponse(code = 500, message = "Cannot stop simulation") })
	public ResponseEntity<String> stopSimulation() throws Exception {
		log.debug("Inside stopSimulation...");

		if (ransimRepo.getCellDetailsList().isEmpty()) {
			return new ResponseEntity<>("No simulation is running.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {
			long startTimStopSimulation = System.currentTimeMillis();
			ransimRepo.deleteNetconfServers();
			ransimRepo.deleteCellNeighbors();
			log.info("Stop simulation : " + (startTimStopSimulation));
			ransimRepo.deleteAllCellDetails();
			String result = rscServices.stopAllSimulation();
			log.info("All cell simulation are stopped...." + result);
			long endTimStopSimulation = System.currentTimeMillis();
			log.info("Time taken for stopping simulation : " + (endTimStopSimulation - startTimStopSimulation));
			return new ResponseEntity<>("Success", HttpStatus.OK);

		} catch (Exception eu) {
			log.error("Exception in stopSimulation", eu);
			return new ResponseEntity<>("Failure", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@ApiOperation("Stops RAN Slicing network simulation and deletes all the data from the database")
	@RequestMapping(value = "/StopRanSliceSimulation", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
	@ApiResponse(code = 500, message = "Cannot stop simulation") })
	public ResponseEntity<String> stopRanSliceSimulation() throws Exception {
		log.debug("Inside stopSimulation...");
		if (ransimRepo.getCellDetailsList().isEmpty()) {
			return new ResponseEntity<>("No simulation is running.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		try {
			long startTimStopSimulation = System.currentTimeMillis();
			ransimRepo.deleteNetconfServers();
			log.info("Stop simulation : " + (startTimStopSimulation));
			String result = rscServices.stopAllSimulation();
			log.info("All cell simulation are stopped...." + result);
			long endTimStopSimulation = System.currentTimeMillis();
			log.info("Time taken for stopping simulation : " + (endTimStopSimulation - startTimStopSimulation));
			return new ResponseEntity<>("Simulation stopped", HttpStatus.OK);

		} catch (Exception eu) {
			log.error("Exception in stopRanSliceSimulation", eu);
			return new ResponseEntity<>("Failure", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * The function returns the details of a Netconf server for the mentioned server
	 * id.
	 *
	 * @param req gets the necessary details as a request of class type
	 *            GetNetconfServerDetailsReq
	 * @return returns Http status
	 * @throws Exception throws exceptions in the functions
	 */
	@ApiOperation("Returns the details of a Netconf server for the mentioned server id")
	@RequestMapping(value = "/GetNetconfServerDetails", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
			@ApiResponse(code = 500, message = "Failure in GetNetconfServerDetails API") })
	public ResponseEntity<String> getNetconfServerDetails(@RequestBody GetNetconfServerDetailsReq req)
			throws Exception {

		try {
			log.debug("Inside GetNetconfServerDetails API...");
			String result = "";
			String input = req.getServerId();
			if (input.startsWith("Chn")) {
				CellDetails cds = ransimRepo.getCellDetail(input);
				if (cds != null) {
					Gson gson = new Gson();
					String jsonStr = gson.toJson(cds);
					result = "{\"serverId\":\"any\",\"cells\":[" + jsonStr + "]}";
				} else {
					result = ("Cell Id does not exist");
				}
			} else {
				NetconfServers ns = ransimRepo.getNetconfServer(input);
				if (ns != null) {
					Gson gson = new Gson();
					String jsonStr = gson.toJson(ns);
					result = jsonStr;
				} else {
					result = ("Server Id does not exist");
				}
			}
			return new ResponseEntity<>(result, HttpStatus.OK);

		} catch (Exception eu) {
			log.error("/GetNetconfServers", eu);
			return new ResponseEntity<>("Failure in GetNetconfServerDetails API", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation("Returns the connection status of all netconf servers")
	@RequestMapping(value = "/GetNetconfStatus", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
			@ApiResponse(code = 500, message = "Failure in GetNetconfServerDetails API") })
	public ResponseEntity<String> GetNetconfStatus() throws Exception {

		try {
			log.debug("Inside GetNetconfServerDetails API...");
			String result = "";

			List<NetconfServers> ns = ransimRepo.getNetconfServersList();
			if (ns != null) {
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.serializeNulls();
				Gson gson = gsonBuilder.create();
				String jsonStr = gson.toJson(ns);
				result = jsonStr;
			} else {
				result = ("Server Id does not exist");
			}

			return new ResponseEntity<>(result, HttpStatus.OK);

		} catch (Exception eu) {
			log.error("/GetNetconfServers", eu);
			return new ResponseEntity<>("Failure in GetNetconfServerDetails API", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * The function retrieves RAN simulation network topology.
	 *
	 * @return returns Http status
	 * @throws Exception throws exceptions in the functions
	 *
	 */
	@ApiOperation("Retrieves operations log - Modify/Delete operations performed")
	@GetMapping(value = "/GetOperationLog")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
			@ApiResponse(code = 500, message = "Cannot retrieve the Operation Logs") })
	public ResponseEntity<String> getOperationLog() throws Exception {
		log.debug("Inside getOperationLog...");
		try {
			List<OperationLog> ols = ransimRepo.getOperationLogList();
			if (ols != null && ols.size() > 0) {
				Gson gson = new Gson();
				String jsonStr = gson.toJson(ols);
				return new ResponseEntity<>(jsonStr, HttpStatus.OK);
			} else {
				return new ResponseEntity<>("", HttpStatus.OK);
			}
		} catch (Exception eu) {
			log.error("/GetOperationLog", eu);
			return new ResponseEntity<>("Failure", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


  @ApiOperation("Generate IntelligentSlicing PM data")
    @RequestMapping(value = "/GenerateIntelligentSlicingPmData", method = RequestMethod.POST)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 500, message = "Cannot t the simulation") })
    public ResponseEntity<String> generateIntelligentSlicingPmData(){
       log.info("******************Request to generate***************************");
                       
        try {
            long startTime = (System.currentTimeMillis());
            Iterable<TACells> tacellList = ranSliceConfigService.fetchAllTA();
            HashMap<String,List<String>> taCells = new HashMap<>();
           for(TACells ta : tacellList)
           {
            String[] cells = ta.getCellsList().split(",");
            List<String> cellList = new ArrayList<String>(Arrays.asList(cells));
            taCells.put(ta.getTrackingArea(),cellList);
            }
            execServiceForIntelligentSlicing = Executors.newScheduledThreadPool(5);
            execServiceForIntelligentSlicing.scheduleAtFixedRate(
                    () -> {
                        
                        ranSliceHandler.generateIntelligentSlicingPmData(startTime,taCells);
                        log.info("execServiceforIntelligentSlicing.isTerminated(): " + execServiceForIntelligentSlicing.isTerminated());
                       
                    }, 0, 10, TimeUnit.SECONDS);
           
            isIntelligentSlicingPmDataGenerating = true;
           

           
        } catch (Exception eu) {
            log.info("Exception: ", eu);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
           
        }
        return new ResponseEntity<>("IntelligentSlicing PM data generated.", HttpStatus.OK);
    }
   
    @ApiOperation("Stop IntelligentSlicing PM data")
    @RequestMapping(value = "/stopIntelligentSlicingPmData", method = RequestMethod.POST)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
            @ApiResponse(code = 500, message = "Cannot start the simulation") })
    public ResponseEntity<String> stopIntelligentSlicingPmData() throws Exception {
       
        try {
            log.info("1. execServiceForIntelligentSlicing.isTerminated(): " + execServiceForIntelligentSlicing.isTerminated());
            if (!execServiceForIntelligentSlicing.isTerminated()) {
            execServiceForIntelligentSlicing.shutdown();
                log.info("2. execServiceForIntelligentSlicing.isTerminated(): " + execServiceForIntelligentSlicing.isTerminated());
               
            }
            isIntelligentSlicingPmDataGenerating = false;
            return new ResponseEntity<>("Stopped PM data generation.", HttpStatus.OK);
           
        } catch (Exception eu) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
       
    }


        /*
        *Generate closed loopPM data for DUs
        *
        */
        @ApiOperation("Generate Closed loop PM data")
        @RequestMapping(value = "/generateClosedLoopPmData", method = RequestMethod.POST)
        @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
        @ApiResponse(code = 500, message = "Problem generating closed loop PM data") })
        public ResponseEntity<String> generateClosedLoopPmData() {
                        long startTime = (System.currentTimeMillis());
                        log.info("Closed loop PM Data generation started at " + startTime);
                        closedLoopExecService = Executors.newScheduledThreadPool(5);
                        closedLoopExecService.scheduleAtFixedRate(() -> {
                                pmDataGenerator.generateClosedLoopPmData(startTime);
                                log.info("closedLoopexecService.isTerminated(): " + execServiceForIntelligentSlicing.isTerminated());

                        }, 0, 10, TimeUnit.SECONDS);

                return new ResponseEntity<>("Generating Closed loop PM data.", HttpStatus.OK);
        }

            @ApiOperation("Stop Closed loop PM data")
            @RequestMapping(value = "/stopClosedLoopPmData", method = RequestMethod.GET)
            @ApiResponses(value = { @ApiResponse(code = 200, message = "Successful"),
                    @ApiResponse(code = 500, message = "Cannot start the simulation") })
            public ResponseEntity<String> stopClosedLoopPmData() throws Exception {

                try {
                    log.info("1. closedLoopexecService.isTerminated(): " + closedLoopExecService.isTerminated());
                    if (!closedLoopExecService.isTerminated()) {
                        closedLoopExecService.shutdown();
                        log.info("2. closedLoopexecService.isTerminated(): " + closedLoopExecService.isTerminated());

                    }
                    return new ResponseEntity<>("Closed loop PM data generated.", HttpStatus.OK);

                } catch (Exception eu) {
                    return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
                }

            }
}
