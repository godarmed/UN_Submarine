package com.eseasky.submarine.core.starters.dictionary.module.service;

import com.eseasky.submarine.core.starters.dictionary.exception.GeneralException;
import com.eseasky.submarine.core.starters.dictionary.module.model.DictItem;
import com.eseasky.submarine.core.starters.dictionary.module.model.Dictionary;
import com.eseasky.submarine.core.starters.dictionary.module.model.local.DictionaryConditions;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public interface SystemManagerService {
    Dictionary insertDictionary(Dictionary var1) throws GeneralException;

    Dictionary addItemToGroup(String var1, String var2, List<DictItem> var3) throws GeneralException;

    Dictionary addItemToGroup(Long var1, List<DictItem> var2) throws GeneralException;

    Dictionary addItemToGroup(Dictionary var1, List<DictItem> var2) throws GeneralException;

    Dictionary findValidDictByTypeAndGroup(String var1, String var2);

    List<Dictionary> findValidDictByTypeAndGroups(String var1, Collection<String> var2);

    Map<String, Map<String, String>> findValidDictByTypeAndGroupsForMap(String var1, Collection<String> var2);

    List<Dictionary> findValidDictByType(String var1);

    List<Dictionary> findDictByTypeAndStatus(String var1, String var2);

    Dictionary findDictByTypeAndGroupAndStatus(String var1, String var2, String var3);

    Dictionary findDictByTypeAndGroup(String var1, String var2);

    Dictionary updateDictionary(Dictionary var1) throws GeneralException;

    Boolean deleteDictionary(Dictionary var1) throws GeneralException;

    Boolean deleteDictionary(Long var1) throws GeneralException;

    Boolean deleteDictionary(List<Long> var1) throws GeneralException;

    Boolean deleteDictionaryByObjects(List<Dictionary> var1) throws GeneralException;

    Dictionary removeItemFromDict(Dictionary var1, List<Long> var2) throws GeneralException;

    Page<Dictionary> queryDictionaries(DictionaryConditions var1);

    Dictionary queryOneDict(Long var1);

    boolean refreshDict();

    DictItem getDictItem(String var1, String var2, String var3);

    List<Map> getDictItemValue2ListMap(String var1, String var2, String var3);

    <T> List<T> getDictItemValue2List(DictItem var1, Class<T> var2);

    List<String> getDictTypes();

    List<String> getDictGroups();

    Map<String, Map<String, Object>> getAllDictItemValueByType(String var1);
}

