package org.onap.ransim.rest.api.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import org.onap.ransim.rest.api.models.RANSliceInfo;

@Repository
public interface RANInventoryRepository extends CrudRepository<RANSliceInfo,String>{
	@Query(nativeQuery = true, value = "select distinct(raninfo.rannfnssiid) from raninventory raninfo join nssai where nssai.nssailist=?1")
	public String findRANNFNSSIofNSSAI(String sNSSAI);
}