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

import org.onap.ransim.rest.api.models.GNBCUCPFunction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GNBCUCPRepository extends CrudRepository<GNBCUCPFunction, String> {
    @Query(
            nativeQuery = true,
            value = "select distinct(cucp.gnbcuname) from gnbcucpfunction cucp join nrcellcu cellcu where cellcu.celllocalid in (:cellIdList)")
    public List<String> findCUCPByCellIds(List<Integer> cellIdList);

    @Query(
            nativeQuery = true,
            value = "select * from gnbcucpfunction cucp join plmninfo plmn where plmn.snssai=?1 and plmn.nrcellcu_celllocalid IS NOT NULL")
    public List<GNBCUCPFunction> findCUCPsByNSSAI(String nSSAI);
}
