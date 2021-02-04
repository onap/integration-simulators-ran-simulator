package org.onap.ransim.rest.api.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import org.onap.ransim.rest.api.models.RRMPolicyRatio;

@Repository
public interface RRMPolicyRepository extends CrudRepository<RRMPolicyRatio,Integer>{
	
	@Query(nativeQuery = true, value="select * from rrmpolicy where resourcetype=?1 and resourceid=?2")
	public RRMPolicyRatio findByResourceTypeAndId(String resourceType, String resourceId);

}
