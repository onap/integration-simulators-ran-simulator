package org.onap.ransim.rest.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import org.onap.ransim.rest.api.models.GNBCUCPFunction;

@Repository
public interface GNBCUCPRepository extends CrudRepository<GNBCUCPFunction,String>{
	@Query(nativeQuery=true, value="select distinct(cucp.gnbcuname) from gnbcucpfunction cucp join nrcellcu cellcu where cellcu.celllocalid in (:cellIdList)")
	public List<String> findCUCPByCellIds(List<Integer> cellIdList);
	@Query(nativeQuery = true, value = "select * from gnbcucpfunction cucp join plmninfo plmn where plmn.snssai=?1 and plmn.nrcellcu_celllocalid IS NOT NULL")
	public List<GNBCUCPFunction> findCUCPsByNSSAI(String nSSAI);
}
