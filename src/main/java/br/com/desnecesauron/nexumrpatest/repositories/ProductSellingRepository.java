package br.com.desnecesauron.nexumrpatest.repositories;

import br.com.desnecesauron.nexumrpatest.entities.ProductSellingEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSellingRepository extends CrudRepository<ProductSellingEntity, Long> {

}
