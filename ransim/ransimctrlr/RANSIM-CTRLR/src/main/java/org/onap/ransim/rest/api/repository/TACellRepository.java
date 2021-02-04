package org.onap.ransim.rest.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import org.onap.ransim.rest.api.models.TACells;
/***
 * 
 * @author onapadmin
 *
 */
@Repository
public interface TACellRepository extends CrudRepository <TACells,String> {
	
}
