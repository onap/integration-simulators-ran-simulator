package org.onap.ransim.rest.api.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.onap.ransim.rest.api.models.NearRTRIC;

@Repository
public interface NearRTRICRepository extends CrudRepository<NearRTRIC,Integer>{
	
	@Query(nativeQuery = true, value = "select * from nearrtric ric join trackingarea ta where tracking_area=?1")
	public List<NearRTRIC> getListOfRICsInTrackingArea(String trackingArea);
	
	@Query(nativeQuery=true, value="select * from nearrtric ric join gnbcucpfunction cucp where cucp.gnbcuname=?1")
	public List<NearRTRIC> findNearRTRICByCUCPName(String cucpNames);
	
	@Query(nativeQuery = true, value = "select * from nearrtric ric join rannfnssi nssi where nssi.rannfnssilist=?1")
	public List<NearRTRIC> findNearRTRICByNSSI(String ranNFNSSIId);
	
	@Query(nativeQuery = true, value = "select * from nearrtric ric join plmninfo plmn where plmn.snssai=?1 and plmn.nearrtricid IS NOT NULL")
	public Set<NearRTRIC> findNearRTRICByNSSAI(String nSSAI);
}