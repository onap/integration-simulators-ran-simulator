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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.onap.ransim.rest.api.models.GNBCUCPFunction;
import org.onap.ransim.rest.api.models.GNBCUUPFunction;
import org.onap.ransim.rest.api.models.GNBDUFunction;
import org.onap.ransim.rest.api.models.NRCellCU;
import org.onap.ransim.rest.api.models.NRCellDU;
import org.onap.ransim.rest.api.models.NSSAIConfig;
import org.onap.ransim.rest.api.models.NearRTRIC;
import org.onap.ransim.rest.api.models.PLMNInfo;
import org.onap.ransim.rest.api.models.RANSliceInfo;
import org.onap.ransim.rest.api.models.RRMPolicyRatio;
import org.onap.ransim.rest.api.models.SliceProfile;
import org.onap.ransim.rest.api.models.TACells;
import org.onap.ransim.rest.api.repository.GNBCUCPRepository;
import org.onap.ransim.rest.api.repository.GNBCUUPRepository;
import org.onap.ransim.rest.api.repository.GNBDURepository;
import org.onap.ransim.rest.api.repository.NRCellCURepository;
import org.onap.ransim.rest.api.repository.NearRTRICRepository;
import org.onap.ransim.rest.api.repository.RANInventoryRepository;
import org.onap.ransim.rest.api.repository.RRMPolicyRepository;
import org.onap.ransim.rest.api.repository.TACellRepository;
import org.onap.ransim.rest.web.mapper.GNBCUCPModel;
import org.onap.ransim.rest.web.mapper.GNBCUUPModel;
import org.onap.ransim.rest.web.mapper.GNBDUModel;
import org.onap.ransim.rest.web.mapper.NRCellCUModel;
import org.onap.ransim.rest.web.mapper.NRCellDUModel;
import org.onap.ransim.rest.web.mapper.NearRTRICModel;
import org.onap.ransim.rest.web.mapper.RANSliceInfoModel;
import org.onap.ransim.rest.web.mapper.RRMPolicyRatioModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RANSliceConfigService {
    private static Logger logger = Logger.getLogger(RANSliceConfigService.class.getName());
    @Autowired
    private NRCellCURepository nRCellCURepository;

    @Autowired
    private GNBCUCPRepository gNBCUCPRepository;

    @Autowired
    private GNBCUUPRepository gNBCUUPRepository;

    @Autowired
    private GNBDURepository gNBDURepository;

    @Autowired
    private NearRTRICRepository nearRTRICRepository;

    @Autowired
    private RRMPolicyRepository rRMPolicyRepository;

    @Autowired
    private RANInventoryRepository ranInventoryRepo;

    @Autowired
    private TACellRepository tACellRepository;

    private ModelMapper modelMapper = new ModelMapper();

    /**
     * To store/update the NRCEllCU details
     * 
     * @param nRCellCUModel
     * @return NRCellCUModel
     */
    public NRCellCUModel saveNRcellCU(NRCellCUModel nRCellCUModel) {
        logger.debug("Request received to save NRcellCU: id::" + nRCellCUModel.getCellLocalId());
        NRCellCU cellCUEntity = new NRCellCU();
        modelMapper.map(nRCellCUModel, cellCUEntity);
        if (!cellCUEntity.getpLMNInfoList().isEmpty()) {
            for (PLMNInfo plmn : cellCUEntity.getpLMNInfoList()) {
                // plmn.setnRCellCU(cellCUEntity);
            }
        }
        NRCellCU cellCUEntityResponse = nRCellCURepository.save(cellCUEntity);
        modelMapper.map(cellCUEntityResponse, nRCellCUModel);
        return nRCellCUModel;
    }

    /**
     * To fetch the NRCellCU details
     * 
     * @param cellLocalId
     * @return NRCellCUModel
     */
    public NRCellCUModel fetchNRCellCUDetails(Integer cellLocalId) {
        logger.debug("Request received to fetchNRCellCUDetails: id::" + cellLocalId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        NRCellCU cellCUEntity =
                nRCellCURepository.findById(cellLocalId).isPresent() ? nRCellCURepository.findById(cellLocalId).get()
                        : null;
        NRCellCUModel nRCellCUModel = new NRCellCUModel();
        modelMapper.map(cellCUEntity, nRCellCUModel);
        return nRCellCUModel;
    }

    /**
     * To store the slice / config details of GNBCUCPFunction
     * 
     * @param gNBCUCPModel
     * @return
     */
    public GNBCUCPModel saveGNBCUCP(GNBCUCPModel gNBCUCPModel) {
        logger.debug("Request received to save GNBCUCPModel: id::" + gNBCUCPModel.getgNBCUName());
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        GNBCUCPFunction gNBCUCPFunction = new GNBCUCPFunction();
        NearRTRIC nearRTRICRef = new NearRTRIC();
        try {
            modelMapper.map(gNBCUCPModel, gNBCUCPFunction);
            if (gNBCUCPModel.getNearRTRICId() != null
                    && nearRTRICRepository.findById(gNBCUCPModel.getNearRTRICId()).isPresent()) {
                gNBCUCPFunction.setNearRTRIC(nearRTRICRepository.findById(gNBCUCPModel.getNearRTRICId()).get());
            }
            if (gNBCUCPFunction != null && !gNBCUCPFunction.getCellCUList().isEmpty()) {
                gNBCUCPFunction.getCellCUList().forEach(cellCU -> {
                    cellCU.setgNBCUCPFunction(gNBCUCPFunction);
                });
            }
            GNBCUCPFunction gNBCUCPEntity = gNBCUCPRepository.save(gNBCUCPFunction);
            modelMapper.map(gNBCUCPEntity, gNBCUCPModel);
        } catch (Exception e) {
            logger.debug("Error occured during saveGNBCUCP" + e.getMessage());
            return null;
        }
        return gNBCUCPModel;
    }

    /**
     * To fetch the gNBCUCP details
     * 
     * @param cucpName
     * @return GNBCUCPModel
     */
    public GNBCUCPModel fetchGNBCUCPData(String cucpName) {
        logger.debug("Request received to fetch GNBCUCPFunction: name::" + cucpName);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        GNBCUCPFunction gNBCUCPEntity =
                gNBCUCPRepository.findById(cucpName).isPresent() ? gNBCUCPRepository.findById(cucpName).get() : null;
        GNBCUCPModel gNBCUCPModel = new GNBCUCPModel();
        modelMapper.map(gNBCUCPEntity, gNBCUCPModel);
        return gNBCUCPModel;
    }

    /**
     * To store the slice / config details of GNBCUCPFunction
     * 
     * @param gNBCUCPModel
     * @return
     */
    public GNBCUUPModel saveGNBCUUP(GNBCUUPModel gNBCUUPModel) {
        logger.debug("Request received to save GNBCUUPModel: id::" + gNBCUUPModel.getgNBCUUPId());
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        GNBCUUPFunction gNBCUUPFunction = new GNBCUUPFunction();
        modelMapper.map(gNBCUUPModel, gNBCUUPFunction);
        if (gNBCUUPModel.getNearRTRICId() != null
                && nearRTRICRepository.findById(gNBCUUPModel.getNearRTRICId()).isPresent()) {
            gNBCUUPFunction.setNearRTRIC(nearRTRICRepository.findById(gNBCUUPModel.getNearRTRICId()).get());
        }
        if (gNBCUUPFunction != null && !gNBCUUPFunction.getpLMNInfoList().isEmpty()) {
            gNBCUUPFunction.getpLMNInfoList().forEach(plmn -> {
                // plmn.setgNBCUUPFunction(gNBCUUPFunction);
            });
        }
        GNBCUUPFunction gNBCUUPEntity = gNBCUUPRepository.save(gNBCUUPFunction);
        modelMapper.map(gNBCUUPEntity, gNBCUUPModel);
        return gNBCUUPModel;
    }

    /**
     * To fetch the gNBCUUP details
     * 
     * @param cucpName
     * @return GNBCUUPModel
     */
    public GNBCUUPModel fetchGNBCUUPData(Integer gNBCUUPId) {
        logger.debug("Request received to fetch GNBCUUPFunction: id::" + gNBCUUPId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        GNBCUUPFunction gNBCUUPEntity =
                gNBCUUPRepository.findById(gNBCUUPId).isPresent() ? gNBCUUPRepository.findById(gNBCUUPId).get() : null;
        GNBCUUPModel gNBCUUPModel = new GNBCUUPModel();
        modelMapper.map(gNBCUUPEntity, gNBCUUPModel);
        return gNBCUUPModel;
    }

    /**
     * To store the slice / config details of GNBDUFunction
     * 
     * @param GNBDUModel
     * @return
     */
    public GNBDUModel saveGNBDU(GNBDUModel gNBDUModel) {
        logger.debug("Request received to save GNBDUModel: id::" + gNBDUModel.getgNBDUId());
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        GNBDUFunction gNBDUFunction = new GNBDUFunction();
        NearRTRIC nearRTRICRef = new NearRTRIC();
        modelMapper.map(gNBDUModel, gNBDUFunction);
        if (gNBDUModel.getNearRTRICId() != null
                && nearRTRICRepository.findById(gNBDUModel.getNearRTRICId()).isPresent()) {
            nearRTRICRef = nearRTRICRepository.findById(gNBDUModel.getNearRTRICId()).get();
            gNBDUFunction.setNearRTRIC(nearRTRICRef);
        }
        NearRTRIC nearRTRICRefNew = nearRTRICRef;
        nearRTRICRefNew.setNearRTRICId(gNBDUModel.getNearRTRICId());
        if (gNBDUFunction != null && !gNBDUFunction.getCellDUList().isEmpty()) {
            gNBDUFunction.getCellDUList().forEach(cellDU -> {
                cellDU.setgNBDUFunction(gNBDUFunction);
            });
        }
        GNBDUFunction gNBDUEntity = gNBDURepository.save(gNBDUFunction);
        modelMapper.map(gNBDUEntity, gNBDUModel);
        return gNBDUModel;
    }

    /**
     * To fetch the gNBDU details
     * 
     * @param gNBDUId
     * @return GNBDUModel
     */
    public GNBDUModel fetchGNBDUData(Integer gNBDUId) {
        logger.debug("Request received to fetch GNBDUFunction: id::" + gNBDUId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        GNBDUFunction gNBDUEntity =
                gNBDURepository.findById(gNBDUId).isPresent() ? gNBDURepository.findById(gNBDUId).get() : null;
        GNBDUModel gNBDUModel = new GNBDUModel();
        modelMapper.map(gNBDUEntity, gNBDUModel);
        return gNBDUModel;
    }

    /**
     * Stored NearRTRIC
     * 
     * @param nearRTRIC
     * @return
     */
    public NearRTRICModel saveNearRTRIC(NearRTRICModel nearRTRIC) {
        logger.debug("Request received to store NearRTRIC: id::" + nearRTRIC.getNearRTRICId());
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        NearRTRIC nearRTRICEntity = new NearRTRIC();
        modelMapper.map(nearRTRIC, nearRTRICEntity);
        NearRTRIC nearRTRICEntityEResponse = nearRTRICRepository.save(nearRTRICEntity);
        modelMapper.map(nearRTRICEntityEResponse, nearRTRIC);
        return nearRTRIC;
    }

    /**
     * To fetch the nearRTRIC details
     * 
     * @param nearRTRICId
     * @return NearRTRICModel
     */
    public NearRTRICModel fetchNearRTRICData(Integer nearRTRICId) {
        logger.debug("Request received to fetch GNBDUFunction: id::" + nearRTRICId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        NearRTRIC nearRTRICEntity =
                nearRTRICRepository.findById(nearRTRICId).isPresent() ? nearRTRICRepository.findById(nearRTRICId).get()
                        : null;
        NearRTRICModel nearRTRICModel = new NearRTRICModel();
        modelMapper.map(nearRTRICEntity, nearRTRICModel);
        return nearRTRICModel;
    }

    /**
     * To retrieve the RRMPolicy of a network function
     * 
     * @param resourceType
     * @param resourceID
     * @return
     */
    public RRMPolicyRatioModel fetchRRMPolicyOfNE(String resourceType, String resourceID) {
        logger.debug("Request received to fetch RRMPolicy:" + resourceType + "--" + resourceID);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        RRMPolicyRatioModel rrmPolicyResponse = new RRMPolicyRatioModel();
        RRMPolicyRatio rrmPolicyEntity = rRMPolicyRepository.findByResourceTypeAndId(resourceType, resourceID);
        modelMapper.map(rrmPolicyEntity, rrmPolicyResponse);
        return rrmPolicyResponse;
    }

    /**
     * To update RRM policy of NF
     * 
     * @param rrmPolicy
     * @return
     */
    public RRMPolicyRatioModel updateRRMPolicy(RRMPolicyRatioModel rrmPolicy) {
        logger.debug("Request received to update RRMPolicy:" + rrmPolicy.getRrmPolicyID());
        RRMPolicyRatio rrmPolicyEntity = new RRMPolicyRatio();
        modelMapper.map(rrmPolicy, rrmPolicyEntity);
        rrmPolicyEntity = rRMPolicyRepository.save(rrmPolicyEntity);
        modelMapper.map(rrmPolicyEntity, rrmPolicy);
        return rrmPolicy;
    }

    /**
     * @param trackingArea
     * @return List<NearRTRICModel>
     */
    public List<NearRTRICModel> findRICsInTA(String trackingArea) {
        logger.debug("Request received to find the NearRTRICs in Tracking Area::" + trackingArea);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<NearRTRIC> nearRTRICsList = nearRTRICRepository.getListOfRICsInTrackingArea(trackingArea);
        List<NearRTRICModel> ricModelList = nearRTRICsList.stream()
                .map(ricEntity -> modelMapper.map(ricEntity, NearRTRICModel.class)).collect(Collectors.toList());
        return ricModelList;
    }

    /**
     * @param cellsList
     * @return
     */
    public List<NearRTRICModel> findNearRTRICofCells(List<Integer> cellsList) {
        List<NearRTRIC> ricEntitiesList = new ArrayList<>();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<String> cucpNames = gNBCUCPRepository.findCUCPByCellIds(cellsList);
        cucpNames.forEach(cucpName -> {
            List<NearRTRIC> ricEntities = new ArrayList<>();
            ricEntities = nearRTRICRepository.findNearRTRICByCUCPName(cucpName);
            ricEntitiesList.addAll(ricEntities);
        });
        List<NearRTRICModel> ricModelList = ricEntitiesList.stream()
                .map(ricEntity -> modelMapper.map(ricEntity, NearRTRICModel.class)).collect(Collectors.toList());
        return ricModelList;
    }

    /**
     * To update the RAN Slice details
     * 
     * @param rANSliceInfoModel
     * @return RANSliceInfoModel
     */
    public RANSliceInfoModel updateRANInventory(RANSliceInfoModel rANSliceInfoModel) {
        logger.debug("Request received to update inventory for id::" + rANSliceInfoModel.getRanNFNSSIId());
        RANSliceInfo rANSliceInfoEntity = new RANSliceInfo();
        modelMapper.map(rANSliceInfoModel, rANSliceInfoEntity);
        if (ranInventoryRepo.findById(rANSliceInfoModel.getRanNFNSSIId()).isPresent()) {
            RANSliceInfo ranInfo = ranInventoryRepo.findById(rANSliceInfoModel.getRanNFNSSIId()).get();
            List<String> ranNSSIList = ranInfo.getRanNSSIList();
            ranNSSIList.addAll(rANSliceInfoEntity.getRanNSSIList());
            rANSliceInfoEntity.setRanNSSIList(ranNSSIList);

            List<String> nSSAIList = ranInfo.getnSSAIList();
            nSSAIList.addAll(rANSliceInfoEntity.getnSSAIList());
            rANSliceInfoEntity.setnSSAIList(nSSAIList);

            List<SliceProfile> sliceProfilesList = ranInfo.getSliceProfilesList();
            sliceProfilesList.addAll(rANSliceInfoEntity.getSliceProfilesList());
            rANSliceInfoEntity.setSliceProfilesList(sliceProfilesList);
        } else {
            if (!rANSliceInfoEntity.getSliceProfilesList().isEmpty()) {
                for (SliceProfile profile : rANSliceInfoEntity.getSliceProfilesList()) {
                    profile.setrANSliceInventory(rANSliceInfoEntity);
                }
            }
        }
        rANSliceInfoEntity = ranInventoryRepo.save(rANSliceInfoEntity);
        modelMapper.map(rANSliceInfoEntity, rANSliceInfoModel);
        return rANSliceInfoModel;
    }

    /**
     * @param ranNFNSSIId
     * @return RANSliceInfoModel
     */
    public RANSliceInfoModel fetchRANSlice(String ranNFNSSIId) {
        logger.debug("Request received to read inventory details for id::" + ranNFNSSIId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        RANSliceInfo ranSliceInfo =
                ranInventoryRepo.findById(ranNFNSSIId).isPresent() ? ranInventoryRepo.findById(ranNFNSSIId).get()
                        : null;
        RANSliceInfoModel rANSliceInfoModel = new RANSliceInfoModel();
        modelMapper.map(ranSliceInfo, rANSliceInfoModel);
        return rANSliceInfoModel;
    }

    /**
     * @param trackingArea
     * @return List<String>
     */
    public List<String> fetchCellsofTA(String trackingArea) {
        logger.debug("Request recieved to fetch the cell details of TA:" + trackingArea);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        String cells = tACellRepository.findById(trackingArea).isPresent()
                ? tACellRepository.findById(trackingArea).get().getCellsList()
                : null;
        return cells != null ? Arrays.asList(cells.split(",")) : null;

    }

    public Iterable<TACells> fetchAllTA() {
        logger.info("Request recieved to fetch all TA:");
        return tACellRepository.findAll();
    }

    /**
     * @param nearRTRICId
     * @return List<NRCellCUModel>
     */
    public List<NRCellCUModel> fetchCUCellsofRIC(Integer nearRTRICId) {
        logger.debug("Request recieved to fetch the cell (CU) details of nearRTRICId:" + nearRTRICId);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<NRCellCU> cellCUEntities = new ArrayList<NRCellCU>();
        NearRTRIC nearRTRIC =
                nearRTRICRepository.findById(nearRTRICId).isPresent() ? nearRTRICRepository.findById(nearRTRICId).get()
                        : null;
        if (nearRTRIC != null) {
            List<GNBCUCPFunction> cucpFunctions = nearRTRIC.getgNBCUCPList();
            cucpFunctions.forEach(cucpFunction -> {
                List<NRCellCU> cellCUList = new ArrayList<NRCellCU>();
                cellCUList.addAll(cucpFunction.getCellCUList());
                cellCUEntities.addAll(cellCUList);
            });
            List<NRCellCUModel> cuCellModels =
                    cellCUEntities.stream().map(cellCUEntity -> modelMapper.map(cellCUEntity, NRCellCUModel.class))
                            .collect(Collectors.toList());
            return cuCellModels;
        }
        return new ArrayList<NRCellCUModel>();
    }

    /**
     * @param trackingArea
     * @return List<NRCellDUModel>
     */
    public Map<Integer, List<NRCellDUModel>> fetchDUCellsofRIC(String sNSSAI) {
        logger.debug("Request recieved to fetch the cell (DU) details of sNSSAI:" + sNSSAI);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Set<NRCellDU> cellDUEntities = new HashSet<NRCellDU>();
        Set<NRCellDU> cellDUs = new HashSet<NRCellDU>();
        List<NearRTRICModel> ricModels = findRICsByNSSAI(sNSSAI);
        Map<Integer, List<NRCellDUModel>> cellsMap = new HashMap<Integer, List<NRCellDUModel>>();
        List<NearRTRIC> ricEntities =
                ricModels.stream().map(ric -> modelMapper.map(ric, NearRTRIC.class)).collect(Collectors.toList());
        ricEntities.forEach(ricEntity -> {
            List<GNBDUFunction> duFunctions = ricEntity.getgNBDUList();
            duFunctions.forEach(duFunction -> {
                List<NRCellDU> cellDUList = duFunction.getCellDUList();
                cellDUList.forEach(cellDU -> {
                    List<PLMNInfo> plmnList = cellDU.getpLMNInfoList();
                    plmnList.forEach(plmn -> {
                        if (sNSSAI.equalsIgnoreCase(plmn.getsNSSAI().getsNSSAI())) {
                            cellDUs.add(cellDU);
                        }
                    });
                });
                // cellDUList.addAll(duFunction.getCellDUList());
                cellDUEntities.addAll(cellDUs);
            });
            List<NRCellDUModel> duCellModels =
                    cellDUEntities.stream().map(cellDUEntity -> modelMapper.map(cellDUEntity, NRCellDUModel.class))
                            .collect(Collectors.toList());
            cellsMap.put(ricEntity.getNearRTRICId(), duCellModels);
        });

        return cellsMap;
    }

    /**
     * @param ranNFNSSIID
     * @return List<NearRTRICModel>
     */
    public List<NearRTRICModel> findNearRTRICByNSSI(String ranNFNSSIID) {
        logger.debug("Request recieved to fetch nearRTRIC of NSSI:" + ranNFNSSIID);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<NearRTRIC> nearRTRICEntities = nearRTRICRepository.findNearRTRICByNSSI(ranNFNSSIID);
        List<NearRTRICModel> nearRTRICModels = nearRTRICEntities.stream()
                .map(nearRTRICEntity -> modelMapper.map(nearRTRICEntity, NearRTRICModel.class))
                .collect(Collectors.toList());
        return nearRTRICModels;
    }

    /**
     * @param sNSSAI
     * @return List<NearRTRICModel>
     */
    public List<NearRTRICModel> findRICsByNSSAI(String sNSSAI) {
        logger.debug("Request recieved to fetch nearRTRIC of NSSAI:" + sNSSAI);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Set<NearRTRIC> nearRTRICEntities = nearRTRICRepository.findNearRTRICByNSSAI(sNSSAI);
        List<NearRTRICModel> nearRTRICModels = nearRTRICEntities.stream()
                .map(nearRTRICEntity -> modelMapper.map(nearRTRICEntity, NearRTRICModel.class))
                .collect(Collectors.toList());
        return nearRTRICModels;
    }

    /**
     * @param sNSSAI
     * @return config Details
     */
    public Map<String, Integer> findSliceProfileconfig(String sNSSAI) {
        logger.debug("Request recieved to fetch Config requested for a slice:" + sNSSAI);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Map<String, Integer> configDetails = new HashMap<String, Integer>();
        String ranNFNSSIId = ranInventoryRepo.findRANNFNSSIofNSSAI(sNSSAI);
        RANSliceInfo rANSliceInfo =
                ranInventoryRepo.findById(ranNFNSSIId).isPresent() ? ranInventoryRepo.findById(ranNFNSSIId).get()
                        : null;
        if (rANSliceInfo.getSliceProfilesList().size() > 0) {
            rANSliceInfo.getSliceProfilesList().forEach(sliceProfile -> {
                if (sNSSAI.equalsIgnoreCase(sliceProfile.getsNSSAI())) {
                    // configDetails.put("maxNoOfConns",sliceProfile.getMaxNumberofConns());
                    configDetails.put("dLThptPerSlice", sliceProfile.getdLThptPerSlice());
                    configDetails.put("uLThptPerSlice", sliceProfile.getuLThptPerSlice());
                }
            });
        }
        return configDetails;
    }

    /**
     * @param sNSSAI
     * @return RIC Config for a slice
     */
    public Map<Integer, NSSAIConfig> findSliceConfig(String sNSSAI) {
        logger.debug("Request recieved to fetch Slice config Details at RICs:" + sNSSAI);
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<NearRTRICModel> nearRTRICModels = findRICsByNSSAI(sNSSAI);
        Map<Integer, NSSAIConfig> configMap = new HashMap<Integer, NSSAIConfig>();
        nearRTRICModels.forEach(nearRTRIC -> {
            nearRTRIC.getpLMNInfoList().forEach(plmn -> {
                if (sNSSAI.equalsIgnoreCase(plmn.getsNSSAI().getsNSSAI())) {
                    configMap.put(nearRTRIC.getNearRTRICId(), plmn.getsNSSAI().getConfigData());
                }
            });
        });
        return configMap;
    }

    /**
     * @param sNSSAI
     * @return List<GNBDUModel>
     */
    public List<GNBDUModel> findDUsofSNssai(String sNSSAI) {
        logger.debug("Request recieved to fetch all DUs of NSSAI");
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<GNBDUFunction> duList = (List<GNBDUFunction>) gNBDURepository.findDUsByNSSAI(sNSSAI);
        List<GNBDUModel> duModels = duList.stream().map(duEntity -> modelMapper.map(duEntity, GNBDUModel.class))
                .collect(Collectors.toList());
        return duModels;
    }

    /**
     * @param sNSSAI
     * @return List<GNBCUCPModel>
     */
    public List<GNBCUCPModel> findCUsofSNssai(String sNSSAI) {
        logger.debug("Request recieved to fetch all CUs of NSSAI");
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<GNBCUCPFunction> cuList = (List<GNBCUCPFunction>) gNBCUCPRepository.findCUCPsByNSSAI(sNSSAI);
        List<GNBCUCPModel> cuModels = cuList.stream().map(cuEntity -> modelMapper.map(cuEntity, GNBCUCPModel.class))
                .collect(Collectors.toList());
        return cuModels;
    }

    /**
     * @param sNSSAI
     * @return Map<String, String>
     */
    public Map<String, String> getSubscriberDetails(String sNSSAI) {
        logger.debug("Request recieved to fetch SubscriberDetails");
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Map<String, String> details = new HashMap<String, String>();
        String ranNFNSSIId = ranInventoryRepo.findRANNFNSSIofNSSAI(sNSSAI);
        details.put("ranNFNSSIId", ranNFNSSIId);
        details.put("sNSSAI", sNSSAI);
        RANSliceInfo rANSliceInfo =
                ranInventoryRepo.findById(ranNFNSSIId).isPresent() ? ranInventoryRepo.findById(ranNFNSSIId).get()
                        : null;
        if (rANSliceInfo.getSliceProfilesList().size() > 0) {
            rANSliceInfo.getSliceProfilesList().forEach(sliceProfile -> {
                if (sNSSAI.equalsIgnoreCase(sliceProfile.getsNSSAI())) {
                    details.put("sliceProfileId", sliceProfile.getSliceProfileId());
                }
            });
        }
        List<NearRTRICModel> nearRTRICModels = findRICsByNSSAI(sNSSAI);
        nearRTRICModels.forEach(nearRTRIC -> {
            nearRTRIC.getpLMNInfoList().forEach(plmn -> {
                if (sNSSAI.equalsIgnoreCase(plmn.getsNSSAI().getsNSSAI())) {
                    details.put("subscriptionServiceType", plmn.getsNSSAI().getSubscriptionServiceType());
                    details.put("globalSubscriberId", plmn.getsNSSAI().getGlobalSubscriberId());
                }
            });
        });

        return details;
    }

    // Data required for PM data Simulation
    /**
     * @return List<GNBCUCPModel>
     */
    public List<GNBCUCPModel> findAllCUCPFunctions() {
        logger.debug("Request recieved to fetch all CUCPs");
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<GNBCUCPFunction> cucpsList = (List<GNBCUCPFunction>) gNBCUCPRepository.findAll();
        List<GNBCUCPModel> cucpModels = cucpsList.stream()
                .map(cucpEntity -> modelMapper.map(cucpEntity, GNBCUCPModel.class)).collect(Collectors.toList());
        return cucpModels;
    }

    /**
     * @return List<GNBDUModel>
     */
    public List<GNBDUModel> findAllDUFunctions() {
        logger.debug("Request recieved to fetch all DUs");
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<GNBDUFunction> duList = (List<GNBDUFunction>) gNBDURepository.findAll();
        List<GNBDUModel> duModels = duList.stream().map(duEntity -> {
            GNBDUModel gnbDuModel = modelMapper.map(duEntity, GNBDUModel.class);
            gnbDuModel.setNearRTRICId(duEntity.getNearRTRIC().getNearRTRICId());
            return gnbDuModel;
        }).collect(Collectors.toList());
        return duModels;
    }

    public List<NearRTRICModel> findAllNearRTRIC() {
        logger.debug("Request received to fetch all NearRTRICModel");
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<NearRTRIC> rtricList = (List<NearRTRIC>) nearRTRICRepository.findAll();
        List<NearRTRICModel> rtricmodels = rtricList.stream()
                .map(rtricEntity -> modelMapper.map(rtricEntity, NearRTRICModel.class)).collect(Collectors.toList());
        return rtricmodels;
    }

}
