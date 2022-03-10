/*
 ** ============LICENSE_START=======================================================
 ** Ran Simulator Controller
 ** ================================================================================
 ** Copyright (C) 2022 Wipro Limited.
 ** ================================================================================
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **      http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 ** ============LICENSE_END=========================================================
 **/

package org.onap.ransim.rest.api.models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "NRCELLRELATION")
public class NRCellRelation implements Serializable {
     private static final long serialVersionUID = 1L;

     @Id
     @Column(name = "NRCELLRELATIONID")
     private Integer idNRCellRelation;
     @Column(name = "nRTCI")
     private Integer nRTCI;
     @Column(name = "ISHOALLOWED")
     private boolean isHOAllowed;
     @ManyToOne
     @JoinColumn(name = "cellLocalId")
     private NRCellCU cellLocalId;


     public NRCellCU getCellLocalId() { return cellLocalId; }

     public void setCellLocalId(NRCellCU cellLocalId) { this.cellLocalId = cellLocalId; }

     public Integer getIdNRCellRelation() { return idNRCellRelation; }

     public void setIdNRCellRelation(Integer idNRCellRelation) { this.idNRCellRelation = idNRCellRelation; }

     public Integer getnRTCI() { return nRTCI; }

     public void setnRTCI(Integer nRTCI) { this.nRTCI = nRTCI; }

     public boolean getisHOAllowed() { return isHOAllowed; }

     public void setHOAllowed(boolean HOAllowed) { isHOAllowed = HOAllowed; }							        
}
