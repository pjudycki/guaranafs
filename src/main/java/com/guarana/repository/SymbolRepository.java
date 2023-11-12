package com.guarana.repository;

import com.guarana.entity.SymbolEntity;
import org.springframework.data.repository.CrudRepository;

public interface SymbolRepository extends CrudRepository<SymbolEntity, Long> {
}
