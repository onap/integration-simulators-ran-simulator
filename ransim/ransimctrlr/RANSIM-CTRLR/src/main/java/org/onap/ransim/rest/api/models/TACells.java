package org.onap.ransim.rest.api.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="TACELLS")
public class TACells implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="TRACKINGAREA")
	private String trackingArea;
	@Column(name="CELLS")
	private String cellsList;
	public String getTrackingArea() {
		return trackingArea;
	}
	public void setTrackingArea(String trackingArea) {
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