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

package org.onap.ransim.rest.api.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TACELLS")
public class TACells implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "TRACKINGAREA")
    private int trackingArea;
    @Column(name = "CELLS")
    private String cellsList;

    public int getTrackingArea() {
        return trackingArea;
    }

    public void setTrackingArea(int trackingArea) {
        this.trackingArea = trackingArea;
    }

    public String getCellsList() {
        return cellsList;
    }

    public void setCellsList(String cellsList) {
        this.cellsList = cellsList;
    }

    @Override
    public String toString() {
        return "TACells [trackingArea=" + trackingArea + ", cellsList=" + cellsList + "]";
    }
}
