package com.eseasky.submarine.core.starters.dictionary.module.repository;


import com.eseasky.submarine.core.starters.dictionary.module.model.DictItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DictItemRepository extends JpaRepository<DictItem, Long> {
}
