/*-
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

package org.onap.ransim.rest.api.models;

import java.util.HashMap;

public class FmAlarmInfo {
    
    private String problem;
    private String collisions;
    private HashMap<Integer, Integer> confusions;


    public FmAlarmInfo() {
        super();
    }
    
    /**
     * Constructor with all fields.
     * 
     * @param problem
     *            Collision or Confusion
     * @param collisions
     *            Cell ids that causes collision.
     * @param confusions
     *            Cell ids that causes confusion.
     */
    public FmAlarmInfo(String problem, String collisions, HashMap<Integer, Integer> confusions) {
        super();
        this.problem = problem;
        this.collisions = collisions;
        this.confusions = confusions;
    }
    
    public String getProblem() {
        return problem;
    }
    
    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getCollisions() {
        return collisions;
    }

    public void setCollisions(String collisions) {
        this.collisions = collisions;
    }

    public HashMap<Integer, Integer> getConfusions(){
        return confusions;
    }

    public void setConfusions(HashMap<Integer, Integer> confusions) {
        this.confusions = confusions;
    }

    
}
