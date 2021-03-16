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
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "GNBDUFUNCTION")
public class GNBDUFunction implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "GNBDUID")
	private Integer gNBDUId;
	@Column(name = "GNBID")
	private Integer gNBId;
	@Column(name = "GNBIDLENGTH")
	private Integer gNBIdLength;
	@Column(name = "GNBDUNAME")
	private String gNBDUName;
	@Column(name = "PLMNID")
	private String pLMNId;
	@Column(name = "NFTYPE")
	private String nFType;
	@Column(name = "CELLDULIST")
	@OneToMany(mappedBy = "gNBDUFunction", cascade = CascadeType.ALL)
	private List<NRCellDU> cellDUList;
	@ManyToOne
	@JoinColumn(name = "nearrtricid")
	private NearRTRIC nearRTRIC;

	public Integer getgNBDUId() {
		return gNBDUId;
	}

	public void setgNBDUId(Integer gNBDUId) {
		this.gNBDUId = gNBDUId;
	}

	public Integer getgNBId() {
		return gNBId;
	}

	public void setgNBId(Integer gNBId) {
		this.gNBId = gNBId;
	}

	public Integer getgNBIdLength() {
		return gNBIdLength;
	}

	public void setgNBIdLength(Integer gNBIdLength) {
		this.gNBIdLength = gNBIdLength;
	}

	public String getgNBDUName() {
		return gNBDUName;
	}

	public void setgNBDUName(String gNBDUName) {
		this.gNBDUName = gNBDUName;
	}

	public String getpLMNId() {
		return pLMNId;
	}

	public void setpLMNId(String pLMNId) {
		this.pLMNId = pLMNId;
	}

	public List<NRCellDU> getCellDUList() {
		return cellDUList;
	}

	public void setCellDUList(List<NRCellDU> cellDUList) {
		this.cellDUList = cellDUList;
	}

	public NearRTRIC getNearRTRIC() {
		return nearRTRIC;
	}

	public void setNearRTRIC(NearRTRIC nearRTRIC) {
		this.nearRTRIC = nearRTRIC;
	}

	public String getnFType() {
		return nFType;
	}

	public void setnFType(String nFType) {
		this.nFType = nFType;
	}

}
