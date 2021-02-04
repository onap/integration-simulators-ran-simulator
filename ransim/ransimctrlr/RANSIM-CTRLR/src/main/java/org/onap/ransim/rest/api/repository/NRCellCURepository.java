package org.onap.ransim.rest.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.onap.ransim.rest.api.models.NRCellCU;

@Repository
public interface NRCellCURepository extends CrudRepository<NRCellCU,Integer>{
	
}
