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

package org.onap.ransim.rest.api.repository;

import java.util.List;
import java.util.Set;

import org.onap.ransim.rest.api.models.NearRTRIC;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NearRTRICRepository extends CrudRepository<NearRTRIC, Integer> {

    @Query(nativeQuery = true, value = "select * from nearrtric ric join trackingarea ta where tracking_area=?1")
    public List<NearRTRIC> getListOfRICsInTrackingArea(int trackingArea);

    @Query(nativeQuery = true, value = "select * from nearrtric ric join gnbcucpfunction cucp where cucp.gnbcuname=?1")
    public List<NearRTRIC> findNearRTRICByCUCPName(String cucpNames);

    @Query(nativeQuery = true, value = "select * from nearrtric ric join rannfnssi nssi where nssi.rannfnssilist=?1")
    public List<NearRTRIC> findNearRTRICByNSSI(String ranNFNSSIId);

    @Query(
            nativeQuery = true,
            value = "select * from nearrtric ric join plmninfo plmn where plmn.snssai=?1 and plmn.nearrtricid IS NOT NULL")
    public Set<NearRTRIC> findNearRTRICByNSSAI(String nSSAI);
}
