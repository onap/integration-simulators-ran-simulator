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

package org.onap.ransim.rest.api.models;

import java.util.Set;
import java.util.Collection;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "NetconfServers")
public class NetconfServers {

    @Id
    @Column(name = "serverId", unique = true, nullable = false, length = 20)
    private String serverId;

    private String ip;
    private String netconfPort;

    @OneToMany(targetEntity = CellDetails.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<CellDetails> cells;

    @OneToMany(targetEntity = NRCellCU.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<NRCellCU> cellCUList;

    @OneToMany(targetEntity = NRCellDU.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<NRCellDU> cellDUList;   

    public NetconfServers() {

    }

    /**
     * A constructor for Netconf server table class.
     *
     * @param serverId server Id of the netconf server
     * @param ip ip address of the netconf server
     * @param netconfPort port number of the netconf server
     * @param cells List of cells belonging to the netconf server
     */
    public NetconfServers(String serverId, String ip, String netconfPort, Set<CellDetails> cells) {
        super();
        this.serverId = serverId;
        this.ip = ip;
        this.netconfPort = netconfPort;
        this.cells = cells;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getNetconfPort() {
        return netconfPort;
    }

    public void setNetconfPort(String netconfPort) {
        this.netconfPort = netconfPort;
    }

    public Set<CellDetails> getCells() {
        return cells;
    }

    public void setCells(Set<CellDetails> cells) {
        this.cells = cells;
    }

    public Set<NRCellCU> getCellList() {
        return cellCUList;
    }

    public void setCellList(Set<NRCellCU> cellCUList) {
        this.cellCUList = cellCUList;
    }

    public Set<NRCellDU> getDUList() {
        return cellDUList;
    }

    public void setDUList(Set<NRCellDU> cellDUList) {
        this.cellDUList = cellDUList;
    }
}
