package org.onap.ransim.rest.api.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.onap.ransim.rest.api.models.GNBDUFunction;

@Repository
public interface GNBDURepository extends CrudRepository<GNBDUFunction,Integer>{
	@Query(nativeQuery = true, value = "select * from gnbdufunction du join plmninfo plmn where plmn.snssai=?1 and plmn.nrcelldu_celllocalid IS NOT NULL")
	public List<GNBDUFunction> findDUsByNSSAI(String nSSAI);
}

