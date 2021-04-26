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

package org.onap.ransim.rest.api.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.onap.ransim.rest.api.models.NSSAIConfig;
import org.onap.ransim.rest.api.services.RANSliceConfigService;
import org.onap.ransim.rest.web.mapper.GNBCUCPModel;
import org.onap.ransim.rest.web.mapper.GNBCUUPModel;
import org.onap.ransim.rest.web.mapper.GNBDUModel;
import org.onap.ransim.rest.web.mapper.NRCellCUModel;
import org.onap.ransim.rest.web.mapper.NRCellDUModel;
import org.onap.ransim.rest.web.mapper.NearRTRICModel;
import org.onap.ransim.rest.web.mapper.RANSliceInfoModel;
import org.onap.ransim.rest.web.mapper.RRMPolicyRatioModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/ransim-db/v4")
public class RANSliceConfigController {
    private static final Logger logger = LoggerFactory.getLogger(RANSliceConfigController.class);

    @Autowired
    private RANSliceConfigService ranSliceConfigService;

    // SDN-R APIs
    /**
     * This method updates the slice details, config details of CUCP
     *
     * @param GNBCUCPModel
     * @return ResponseEntity<GNBCUCPModel>
     */
    @PutMapping(path = "/gNBCUCP")
    public ResponseEntity<GNBCUCPModel> updateGNBCUCPFunction(@RequestBody GNBCUCPModel gNBCUCPModel) {
        logger.info("Request Received");
        try {
            return new ResponseEntity<GNBCUCPModel>(ranSliceConfigService.saveGNBCUCP(gNBCUCPModel), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while updating GNBCUCP:" + e.getMessage());
            return new ResponseEntity<GNBCUCPModel>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To fetch the CUCP details
     *
     * @param gNBCUCPName
     * @return ResponseEntity<GNBCUCPModel>
     */
    @GetMapping(path = "/gNBCUCP/{gNBCUCPName}")
    public ResponseEntity<GNBCUCPModel> findGNBCUCPFunction(@PathVariable String gNBCUCPName) {
        logger.info("Request Received");
        try {
            if (ranSliceConfigService.fetchGNBCUCPData(gNBCUCPName) != null) {
                return new ResponseEntity<GNBCUCPModel>(ranSliceConfigService.fetchGNBCUCPData(gNBCUCPName),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<GNBCUCPModel>(ranSliceConfigService.fetchGNBCUCPData(gNBCUCPName),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error while fetching GNBCUCP:" + e.getMessage());
            return new ResponseEntity<GNBCUCPModel>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This method updates the slice details, config details of CUUP
     *
     * @param GNBCUUPModel
     * @return ResponseEntity<GNBCUUPModel>
     */
    @PutMapping(path = "/gNBCUUP")
    public ResponseEntity<GNBCUUPModel> updateGNBCUUPFunction(@RequestBody GNBCUUPModel gNBCUUPModel) {
        logger.info("Request Received");
        try {
            return new ResponseEntity<GNBCUUPModel>(ranSliceConfigService.saveGNBCUUP(gNBCUUPModel), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while updating GNBCUUP:" + e.getMessage());
            return new ResponseEntity<GNBCUUPModel>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To fetch the CUCP details
     *
     * @param gNBCUCPName
     * @return ResponseEntity<GNBCUCPModel>
     */
    @GetMapping(path = "/gNBCUUP/{gNBCUUPId}")
    public ResponseEntity<GNBCUUPModel> findGNBCUUPFunction(@PathVariable Integer gNBCUUPId) {
        logger.info("Request Received");
        try {
            if (ranSliceConfigService.fetchGNBCUUPData(gNBCUUPId) != null) {
                return new ResponseEntity<GNBCUUPModel>(ranSliceConfigService.fetchGNBCUUPData(gNBCUUPId),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<GNBCUUPModel>(ranSliceConfigService.fetchGNBCUUPData(gNBCUUPId),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error while fetching GNBCUCP:" + e.getMessage());
            return new ResponseEntity<GNBCUUPModel>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This method updates the slice details, config details of gNBDU
     *
     * @param GNBDUModel
     * @return ResponseEntity<GNBDUModel>
     */
    @PutMapping(path = "/gNBDU")
    public ResponseEntity<GNBDUModel> updateGNBDUFunction(@RequestBody GNBDUModel gNBDUModel) {
        logger.info("Request Received");
        try {
            return new ResponseEntity<GNBDUModel>(ranSliceConfigService.saveGNBDU(gNBDUModel), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while updating GNBDU:" + e.getMessage());
            return new ResponseEntity<GNBDUModel>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To fetch the gNBDU details
     *
     * @param gNBDUId
     * @return ResponseEntity<GNBDUModel>
     */
    @GetMapping(path = "/gNBDU/{gNBDUId}")
    public ResponseEntity<GNBDUModel> findGNBDUFunction(@PathVariable Integer gNBDUId) {
        logger.info("Request Received");
        try {
            if (ranSliceConfigService.fetchGNBDUData(gNBDUId) != null) {
                return new ResponseEntity<GNBDUModel>(ranSliceConfigService.fetchGNBDUData(gNBDUId), HttpStatus.OK);
            } else {
                return new ResponseEntity<GNBDUModel>(ranSliceConfigService.fetchGNBDUData(gNBDUId),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error while fetching GNBDU:" + e.getMessage());
            return new ResponseEntity<GNBDUModel>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This method updates the NearRTRIC details
     *
     * @param nearRTRICModel
     * @return ResponseEntity<NearRTRICModel>
     */
    @PutMapping(path = "/nearRTRIC")
    public ResponseEntity<NearRTRICModel> updateNearRTRIC(@RequestBody NearRTRICModel nearRTRICModel) {
        logger.info("Request Received");
        try {
            return new ResponseEntity<NearRTRICModel>(ranSliceConfigService.saveNearRTRIC(nearRTRICModel),
                    HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while updating nearRTRIC:" + e.getMessage());
            return new ResponseEntity<NearRTRICModel>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To fetch the nearRTRIC details
     *
     * @param nearRTRICId
     * @return ResponseEntity<GNBDUModel>
     */
    @GetMapping(path = "/nearRTRIC/{nearRTRICId}")
    public ResponseEntity<NearRTRICModel> findNearRTRICFunction(@PathVariable Integer nearRTRICId) {
        logger.info("Request Received");
        try {
            if (ranSliceConfigService.fetchNearRTRICData(nearRTRICId) != null) {
                return new ResponseEntity<NearRTRICModel>(ranSliceConfigService.fetchNearRTRICData(nearRTRICId),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<NearRTRICModel>(ranSliceConfigService.fetchNearRTRICData(nearRTRICId),
                        HttpStatus.NO_CONTENT);
            }

        } catch (Exception e) {
            logger.error("Error while fetching nearRTRIC:" + e.getMessage());
            return new ResponseEntity<NearRTRICModel>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To fetch the RRMPolicy of CU/DU
     *
     * @param resourceType
     * @param resourceId
     * @return
     */
    @GetMapping(path = "/rrmPolicy/{resourceType}/{resourceId}")
    public ResponseEntity<RRMPolicyRatioModel> findRRMPolicyOfNE(@PathVariable String resourceType,
            @PathVariable String resourceId) {
        logger.debug("Request Received");
        try {
            if (ranSliceConfigService.fetchRRMPolicyOfNE(resourceType, resourceId) != null) {
                return new ResponseEntity<RRMPolicyRatioModel>(
                        ranSliceConfigService.fetchRRMPolicyOfNE(resourceType, resourceId), HttpStatus.OK);
            } else {
                return new ResponseEntity<RRMPolicyRatioModel>(
                        ranSliceConfigService.fetchRRMPolicyOfNE(resourceType, resourceId), HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error while fetching RRMPolicy:" + e.getMessage());
            return new ResponseEntity<RRMPolicyRatioModel>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This method updates the RRM policy of a network function
     *
     * @param nearRTRICModel
     * @return ResponseEntity<NearRTRICModel>
     */
    @PostMapping(path = "/rrmPolicy")
    public ResponseEntity<RRMPolicyRatioModel> updateRRMPolicy(@RequestBody RRMPolicyRatioModel rrmPolicy) {
        logger.info("Request Received");
        try {
            return new ResponseEntity<RRMPolicyRatioModel>(ranSliceConfigService.updateRRMPolicy(rrmPolicy),
                    HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while updating RRM Policy:" + e.getMessage());
            return new ResponseEntity<RRMPolicyRatioModel>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To find the list of RICs from tracking area
     * 1. Find Cells from TA
     * 2. find List of RICs of the cells
     *
     * @param trackingArea
     * @return
     */
    @GetMapping(path = "/nearrtric-list/{trackingArea}")
    public ResponseEntity<List<NearRTRICModel>> findNearRTRICofCellsFromTA(@PathVariable int trackingArea) {
        logger.info("Request Received");
        try {
            List<String> cellIds = this.findListOfCells(trackingArea).getBody();
            List<Integer> cellIdList = cellIds.stream().map(Integer::parseInt).collect(Collectors.toList());
            if (ranSliceConfigService.findNearRTRICofCells(cellIdList).size() > 0) {
                return new ResponseEntity<List<NearRTRICModel>>(ranSliceConfigService.findNearRTRICofCells(cellIdList),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<List<NearRTRICModel>>(ranSliceConfigService.findNearRTRICofCells(cellIdList),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error while fetching the RICs:" + e.getMessage());
            return new ResponseEntity<List<NearRTRICModel>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To find the list of CUs in a tracking area
     *
     * @param trackingArea
     * @return
     */
    @GetMapping(path = "/cell-list/{trackingArea}")
    public ResponseEntity<List<String>> findListOfCells(@PathVariable int trackingArea) {
        logger.info("Request Received");
        try {
            if (ranSliceConfigService.fetchCellsofTA(trackingArea).size() > 0) {
                return new ResponseEntity<List<String>>(ranSliceConfigService.fetchCellsofTA(trackingArea),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<List<String>>(ranSliceConfigService.fetchCellsofTA(trackingArea),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error while fetching the Cells:" + e.getMessage());
            return new ResponseEntity<List<String>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To find the list of CU-Cells
     *
     * @param nearRTRICId
     * @return
     */
    @GetMapping(path = "/cu-cell-list/{nearRTRICId}")
    public ResponseEntity<List<NRCellCUModel>> findCUCellsofRIC(@PathVariable Integer nearRTRICId) {
        logger.info("Request Received");
        try {
            if (ranSliceConfigService.fetchCUCellsofRIC(nearRTRICId).size() > 0) {
                return new ResponseEntity<List<NRCellCUModel>>(ranSliceConfigService.fetchCUCellsofRIC(nearRTRICId),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<List<NRCellCUModel>>(ranSliceConfigService.fetchCUCellsofRIC(nearRTRICId),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error while fetching the Cells-CU:" + e.getMessage());
            return new ResponseEntity<List<NRCellCUModel>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To find the nearRTRIC of NSSI
     *
     * This API can be used in Terminate/activate/deactivate to find the RIC from ranNFNSSIId in SO request
     *
     * @param ranNFNSSIId
     * @return List<NearRTRICModel>
     */
    @GetMapping(path = "/nearrtric/{ranNFNSSIId}")
    public ResponseEntity<List<NearRTRICModel>> findNearRTRICByNSSI(@PathVariable String ranNFNSSIId) {
        logger.info("Request Received");
        try {
            if (ranSliceConfigService.findNearRTRICByNSSI(ranNFNSSIId).size() > 0) {
                return new ResponseEntity<List<NearRTRICModel>>(ranSliceConfigService.findNearRTRICByNSSI(ranNFNSSIId),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<List<NearRTRICModel>>(ranSliceConfigService.findNearRTRICByNSSI(ranNFNSSIId),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error while fetching the nearRTRIC by RANNFNSSI:" + e.getMessage());
            return new ResponseEntity<List<NearRTRICModel>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To find the list of DU-Cells
     *
     * @param nearRTRICId
     * @return
     */
    @GetMapping(path = "/du-cell-list/{sNSSAI}")
    public ResponseEntity<Map<Integer, List<NRCellDUModel>>> findDUCellsofRIC(@PathVariable String sNSSAI) {
        logger.info("Request Received");
        try {
            if (ranSliceConfigService.fetchDUCellsofRIC(sNSSAI).size() > 0) {
                return new ResponseEntity<Map<Integer, List<NRCellDUModel>>>(
                        ranSliceConfigService.fetchDUCellsofRIC(sNSSAI), HttpStatus.OK);
            } else {
                return new ResponseEntity<Map<Integer, List<NRCellDUModel>>>(
                        ranSliceConfigService.fetchDUCellsofRIC(sNSSAI), HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error while fetching the Cells-DU:" + e.getMessage());
            return new ResponseEntity<Map<Integer, List<NRCellDUModel>>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Inventory APIs
    /**
     * This method updates the RAN slice details
     *
     * @param ranSliceInfoModel
     * @return ResponseEntity<RANSliceInfoModel>
     */
    @PutMapping(path = "/ranslice-details")
    public ResponseEntity<RANSliceInfoModel> updateRANInventory(@RequestBody RANSliceInfoModel ranSliceInfoModel) {
        logger.info("Request Received");
        try {
            return new ResponseEntity<RANSliceInfoModel>(ranSliceConfigService.updateRANInventory(ranSliceInfoModel),
                    HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while updating RAN Inventory:" + e.getMessage());
            return new ResponseEntity<RANSliceInfoModel>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To fetch the RAN slice Details
     *
     * @param ranNFNSSIId
     * @return RANSliceInfoModel
     */
    @GetMapping(path = "/ranslice-details/{ranNFNSSIId}")
    public ResponseEntity<RANSliceInfoModel> findRANSlice(@PathVariable String ranNFNSSIId) {
        logger.info("Request Received");
        try {
            if (ranSliceConfigService.fetchRANSlice(ranNFNSSIId) != null) {
                return new ResponseEntity<RANSliceInfoModel>(ranSliceConfigService.fetchRANSlice(ranNFNSSIId),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<RANSliceInfoModel>(ranSliceConfigService.fetchRANSlice(ranNFNSSIId),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error while fetching the RAN slice Details:" + e.getMessage());
            return new ResponseEntity<RANSliceInfoModel>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Slice Analysis MS APIs
    /**
     * To fetch The NearRTRICs serving the sNSSAI
     *
     * @param sNSSAI
     * @return List<NearRTRICModel>
     */
    @GetMapping(path = "/nearrtric/snssai/{sNSSAI}")
    public ResponseEntity<List<NearRTRICModel>> findRICsofNSSAI(@PathVariable String sNSSAI) {
        logger.info("Request Received");
        try {
            if (ranSliceConfigService.findRICsByNSSAI(sNSSAI).size() > 0) {
                return new ResponseEntity<List<NearRTRICModel>>(ranSliceConfigService.findRICsByNSSAI(sNSSAI),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<List<NearRTRICModel>>(ranSliceConfigService.findRICsByNSSAI(sNSSAI),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error while fetching the nearRTRIC by sNSSAI:" + e.getMessage());
            return new ResponseEntity<List<NearRTRICModel>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To fetch the configuration requested for a slice
     *
     * @param sNSSAI
     * @return
     */
    @GetMapping(path = "/profile-config/{sNSSAI}")
    public ResponseEntity<Map<String, Integer>> fetchSliceProfileConfiguration(@PathVariable String sNSSAI) {
        logger.info("Request Received");
        try {
            if (ranSliceConfigService.findSliceProfileconfig(sNSSAI).size() > 0) {
                return new ResponseEntity<Map<String, Integer>>(ranSliceConfigService.findSliceProfileconfig(sNSSAI),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<Map<String, Integer>>(ranSliceConfigService.findSliceProfileconfig(sNSSAI),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error while fetching the Requested Configuration:" + e.getMessage());
            return new ResponseEntity<Map<String, Integer>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To fetch the configuration of a slice in RIC
     *
     * @param sNSSAI
     * @return
     */
    @GetMapping(path = "/slice-config/{sNSSAI}")
    public ResponseEntity<Map<Integer, NSSAIConfig>> fetchSliceConfiguration(@PathVariable String sNSSAI) {
        logger.info("Request Received");
        try {
            if (ranSliceConfigService.findSliceConfig(sNSSAI).size() > 0) {
                return new ResponseEntity<Map<Integer, NSSAIConfig>>(ranSliceConfigService.findSliceConfig(sNSSAI),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<Map<Integer, NSSAIConfig>>(ranSliceConfigService.findSliceConfig(sNSSAI),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error while fetching the Configuration of a Slice at RIC:" + e.getMessage());
            return new ResponseEntity<Map<Integer, NSSAIConfig>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To fetch the DU details
     *
     * @param
     * @return List<GNBDUModel>
     */
    @GetMapping(path = "/du-list/{sNSSAI}")
    public ResponseEntity<List<GNBDUModel>> fetchDUFunctionsOfNSSAI(@PathVariable String sNSSAI) {
        logger.info("Request Received::" + sNSSAI);
        try {
            if (ranSliceConfigService.findDUsofSNssai(sNSSAI).size() > 0) {
                return new ResponseEntity<List<GNBDUModel>>(ranSliceConfigService.findDUsofSNssai(sNSSAI),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<List<GNBDUModel>>(ranSliceConfigService.findDUsofSNssai(sNSSAI),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error while fetching the DU details of NSSAI:" + e.getMessage());
            return new ResponseEntity<List<GNBDUModel>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To fetch the DU details
     *
     * @param
     * @return List<GNBDUModel>
     */
    @GetMapping(path = "/cucp-list/{sNSSAI}")
    public ResponseEntity<List<GNBCUCPModel>> fetchCUFunctionsOfNSSAI(@PathVariable String sNSSAI) {
        logger.info("Request Received::" + sNSSAI);
        try {
            if (ranSliceConfigService.findDUsofSNssai(sNSSAI).size() > 0) {
                return new ResponseEntity<List<GNBCUCPModel>>(ranSliceConfigService.findCUsofSNssai(sNSSAI),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<List<GNBCUCPModel>>(ranSliceConfigService.findCUsofSNssai(sNSSAI),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error while fetching the CU details of NSSAI:" + e.getMessage());
            return new ResponseEntity<List<GNBCUCPModel>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To fetch the Customer Details
     *
     * @param
     * @return Map<String, String>
     */
    @GetMapping(path = "/subscriber-details/{sNSSAI}")
    public ResponseEntity<Map<String, String>> fetchSubsciberDetailsOfNSSAI(@PathVariable String sNSSAI) {
        logger.info("Request Received::" + sNSSAI);
        try {
            if (ranSliceConfigService.getSubscriberDetails(sNSSAI).size() > 0) {
                return new ResponseEntity<Map<String, String>>(ranSliceConfigService.getSubscriberDetails(sNSSAI),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<Map<String, String>>(ranSliceConfigService.getSubscriberDetails(sNSSAI),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error while fetching the Customer details of NSSAI:" + e.getMessage());
            return new ResponseEntity<Map<String, String>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To fetch the cu details
     *
     * @param
     * @return List<GNBCUCPModel>
     */
    @GetMapping(path = "/cucp-list")
    public ResponseEntity<List<GNBCUCPModel>> fetchCUCPFunctions() {
        logger.info("Request Received");
        try {
            if (ranSliceConfigService.findAllCUCPFunctions().size() > 0) {
                return new ResponseEntity<List<GNBCUCPModel>>(ranSliceConfigService.findAllCUCPFunctions(),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<List<GNBCUCPModel>>(ranSliceConfigService.findAllCUCPFunctions(),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error while fetching the CU details:" + e.getMessage());
            return new ResponseEntity<List<GNBCUCPModel>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * To fetch the DU details
     *
     * @param
     * @return List<GNBDUModel>
     */
    @GetMapping(path = "/du-list")
    public ResponseEntity<List<GNBDUModel>> fetchDUFunctions() {
        logger.info("Request Received");
        try {
            if (ranSliceConfigService.findAllDUFunctions().size() > 0) {
                return new ResponseEntity<List<GNBDUModel>>(ranSliceConfigService.findAllDUFunctions(), HttpStatus.OK);
            } else {
                return new ResponseEntity<List<GNBDUModel>>(ranSliceConfigService.findAllDUFunctions(),
                        HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("Error while fetching the DU details:" + e.getMessage());
            return new ResponseEntity<List<GNBDUModel>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
