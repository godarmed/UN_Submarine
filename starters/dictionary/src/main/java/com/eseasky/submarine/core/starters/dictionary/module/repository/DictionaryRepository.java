package com.eseasky.submarine.core.starters.dictionary.module.repository;


import com.eseasky.submarine.core.starters.dictionary.module.model.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long>, JpaSpecificationExecutor<Dictionary> {
    List<Dictionary> findByType(String var1);

    List<Dictionary> findByTypeAndStatus(String var1, String var2);

    Dictionary findByTypeAndGroup(String var1, String var2);

    List<Dictionary> findByTypeAndGroupInAndStatus(String var1, List<String> var2, String var3);

    Dictionary findByTypeAndGroupAndStatus(String var1, String var2, String var3);

    List<Dictionary> findByStatus(String var1);

    List<Dictionary> findByIdIn(List<Long> var1);

    @Query(
            value = "SELECT distinct(type) FROM sys_dictionary",
            nativeQuery = true
    )
    List<String> getDictTypes();

    @Query(
            value = "SELECT distinct(`group`) FROM sys_dictionary",
            nativeQuery = true
    )
    List<String> getDictGroups();
}

