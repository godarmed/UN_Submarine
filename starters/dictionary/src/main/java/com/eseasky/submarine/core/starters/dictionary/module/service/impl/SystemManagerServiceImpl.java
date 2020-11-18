package com.eseasky.submarine.core.starters.dictionary.module.service.impl;


import com.alibaba.fastjson.JSON;
import com.eseasky.submarine.core.starters.dictionary.exception.ExceptionType;
import com.eseasky.submarine.core.starters.dictionary.exception.GeneralException;
import com.eseasky.submarine.core.starters.dictionary.module.model.DictItem;
import com.eseasky.submarine.core.starters.dictionary.module.model.Dictionary;
import com.eseasky.submarine.core.starters.dictionary.module.model.enums.DictionaryStatus;
import com.eseasky.submarine.core.starters.dictionary.module.model.local.DictionaryConditions;
import com.eseasky.submarine.core.starters.dictionary.module.repository.DictItemRepository;
import com.eseasky.submarine.core.starters.dictionary.module.repository.DictionaryRepository;
import com.eseasky.submarine.core.starters.dictionary.module.service.SystemManagerService;
import com.eseasky.submarine.core.starters.global.utils.CheckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SystemManagerServiceImpl implements SystemManagerService {
    @Autowired
    DictionaryRepository dictionaryRepository;
    @Autowired
    DictItemRepository dictItemRepository;

    public SystemManagerServiceImpl() {
    }

    @Override
    public Dictionary insertDictionary(Dictionary dictionary) throws GeneralException {
        if (dictionary.getDesc().length() <= 255 && dictionary.getGroup().length() <= 255 && dictionary.getName().length() <= 255 && dictionary.getType().length() <= 255) {
            Dictionary dictionaryRet = this.dictionaryRepository.findByTypeAndGroup(dictionary.getType(), dictionary.getGroup());
            if ((dictionary.getId() != null || dictionaryRet == null) && (dictionary.getId() == null || dictionary.getId().equals(dictionaryRet.getId()))) {
                if (!CheckUtils.isEmpty(dictionary.getDictItems())) {
                    Set<String> duplicateKeys = this.checkDictItemsDuplicate(dictionary.getDictItems());
                    if (!CheckUtils.isEmpty(duplicateKeys)) {
                        throw new GeneralException(ExceptionType.SYSTEMHASSAMEDICTITEM, "重复枚举值为：" + duplicateKeys);
                    }

                    Iterator var4 = dictionary.getDictItems().iterator();

                    while (var4.hasNext()) {
                        DictItem dItem = (DictItem) var4.next();
                        if (dItem.getKey().length() > 255 || dItem.getName().length() > 255 || dItem.getValue().length() > 4096) {
                            throw new GeneralException(ExceptionType.SYSTEFieldTooLong);
                        }
                    }
                }

                dictionaryRet = (Dictionary) this.dictionaryRepository.save(dictionary);
                if (!CheckUtils.isEmpty(dictionary.getDictItems())) {
                    Dictionary finalDictionaryRet = dictionaryRet;
                    List<DictItem> dictItems = (List) dictionary.getDictItems().stream().map((item) -> {
                        item.setDictionary(finalDictionaryRet);
                        return item;
                    }).collect(Collectors.toList());
                    dictionaryRet.setDictItems(this.dictItemRepository.saveAll(dictItems));
                }

                return dictionaryRet;
            } else {
                throw new GeneralException(ExceptionType.SYSTEEXISTSDICT);
            }
        } else {
            throw new GeneralException(ExceptionType.SYSTEFieldTooLong);
        }
    }

    private Set<String> checkDictItemsDuplicate(List<DictItem> dictItems) {
        Set<String> existKeys = new HashSet();
        Set<String> duplicateKeys = new HashSet();
        Iterator var4 = dictItems.iterator();

        while (var4.hasNext()) {
            DictItem dictItem = (DictItem) var4.next();
            if (existKeys.contains(dictItem.getKey())) {
                duplicateKeys.add(dictItem.getKey());
            } else {
                existKeys.add(dictItem.getKey());
            }
        }

        return duplicateKeys;
    }

    @Override
    public Dictionary addItemToGroup(String type, String group, List<DictItem> dictItems) throws GeneralException {
        Dictionary dictionary = this.dictionaryRepository.findByTypeAndGroup(type, group);
        return this.addItemToGroup(dictionary, dictItems);
    }

    @Override
    @CacheEvict(
            value = {"sys_dictionary"},
            allEntries = true
    )
    public Dictionary addItemToGroup(Long dict_id, List<DictItem> dictItems) throws GeneralException {
        Optional<Dictionary> dictionary = this.dictionaryRepository.findById(dict_id);
        return dictionary.isPresent() ? this.addItemToGroup((Dictionary) dictionary.get(), dictItems) : null;
    }

    @Override
    public Dictionary addItemToGroup(Dictionary dictionary, List<DictItem> dictItems) throws GeneralException {
        if (dictionary == null) {
            throw new GeneralException(ExceptionType.SYSTEMNOEXISTSDICT);
        } else if (!dictionary.getStatus().equals("valid")) {
            throw new GeneralException(ExceptionType.SYSTEMDICTISINVALID);
        } else if (dictItems != null && dictItems.size() != 0) {
            List<DictItem> dItems = dictionary.getDictItems();
            if (dItems == null) {
                dItems = new ArrayList();
            }

            Iterator var4 = dictItems.iterator();

            while (var4.hasNext()) {
                DictItem dictItem = (DictItem) var4.next();
                Iterator var6 = ((List) dItems).iterator();

                while (var6.hasNext()) {
                    DictItem dItem = (DictItem) var6.next();
                    if (dItem != null) {
                        if (dItem.getKey().length() > 255 || dItem.getName().length() > 255 || dItem.getValue().length() > 4096) {
                            throw new GeneralException(ExceptionType.SYSTEFieldTooLong);
                        }

                        if (dictItem.getKey().equals(dItem.getKey())) {
                            throw new GeneralException(ExceptionType.SYSTEMHASSAMEDICTITEM);
                        }
                    }
                }

                dictItem.setDictionary(dictionary);
                ((List) dItems).add(dictItem);
            }

            dictionary.setDictItems((List) dItems);
            return this.insertDictionary(dictionary);
        } else {
            return dictionary;
        }
    }

    @Override
    public Dictionary findValidDictByTypeAndGroup(String type, String group) {
        return this.findDictByTypeAndGroupAndStatus(type, group, DictionaryStatus.VALID.getValue());
    }

    @Override
    public List<Dictionary> findValidDictByType(String type) {
        return this.findDictByTypeAndStatus(type, DictionaryStatus.VALID.getValue());
    }

    @Override
    public List<Dictionary> findDictByTypeAndStatus(String type, String status) {
        List<Dictionary> dictionarys = this.dictionaryRepository.findByTypeAndStatus(type, status);

        for (int i = 0; i < dictionarys.size(); ++i) {
            if (((Dictionary) dictionarys.get(i)).getDictItems() != null && ((Dictionary) dictionarys.get(i)).getDictItems().size() > 0) {
                List<DictItem> dictItems = (List) ((Dictionary) dictionarys.get(i)).getDictItems().stream().filter((item) -> {
                    return status.equals(item.getStatus());
                }).collect(Collectors.toList());
                ((Dictionary) dictionarys.get(i)).setDictItems(dictItems);
            }
        }

        return dictionarys;
    }

    @Override
    public Dictionary findDictByTypeAndGroupAndStatus(String type, String group, String status) {
        Dictionary dictionary = this.dictionaryRepository.findByTypeAndGroupAndStatus(type, group, status);
        if (dictionary != null && dictionary.getDictItems() != null && dictionary.getDictItems().size() > 0) {
            List<DictItem> dictItems = (List) dictionary.getDictItems().stream().filter((item) -> {
                return status.equals(item.getStatus());
            }).collect(Collectors.toList());
            dictionary.setDictItems(dictItems);
        }

        return dictionary;
    }

    @Override
    public Dictionary findDictByTypeAndGroup(String type, String group) {
        return this.dictionaryRepository.findByTypeAndGroup(type, group);
    }

    @Override
    @CacheEvict(
            value = {"sys_dictionary"},
            allEntries = true
    )
    public Dictionary updateDictionary(Dictionary updates) throws GeneralException {
        if (updates.getId() == null) {
            throw new GeneralException(ExceptionType.SYSTEMNOEXISTSDICT);
        } else {
            Dictionary oldDictionary = (Dictionary) this.dictionaryRepository.getOne(updates.getId());
            if (oldDictionary == null) {
                throw new GeneralException(ExceptionType.SYSTEMNOEXISTSDICT);
            } else {
                oldDictionary.setDesc(updates.getDesc() != null ? updates.getDesc() : oldDictionary.getDesc());
                oldDictionary.setName(updates.getName() != null ? updates.getName() : oldDictionary.getName());
                oldDictionary.setStatus(updates.getStatus() != null ? updates.getStatus() : oldDictionary.getStatus());
                if (oldDictionary.getDesc().length() <= 255 && oldDictionary.getGroup().length() <= 255 && oldDictionary.getType().length() <= 255 && oldDictionary.getName().length() <= 255) {
                    List<DictItem> dictItems = this.getUpdateDictItem(oldDictionary.getDictItems(), updates.getDictItems());
                    if (updates.getStatus() != null && updates.getStatus().equals(oldDictionary.getStatus())) {
                        for (int i = 0; i < dictItems.size(); ++i) {
                            if (((DictItem) dictItems.get(i)).getKey().length() > 255 || ((DictItem) dictItems.get(i)).getName().length() > 255 || ((DictItem) dictItems.get(i)).getValue().length() > 4096) {
                                throw new GeneralException(ExceptionType.SYSTEFieldTooLong);
                            }

                            ((DictItem) dictItems.get(i)).setStatus(updates.getStatus());
                        }
                    }

                    if (dictItems != null && dictItems.size() > 0) {
                        oldDictionary.setDictItems(dictItems);
                    }

                    return this.insertDictionary(oldDictionary);
                } else {
                    throw new GeneralException(ExceptionType.SYSTEFieldTooLong);
                }
            }
        }
    }

    private List<DictItem> getUpdateDictItem(List<DictItem> old, List<DictItem> newItems) {
        if (newItems != null && newItems.size() != 0) {
            if (old != null && old.size() != 0) {
                List<DictItem> newDictItems = new ArrayList();
                newDictItems.addAll(newItems);
                List<DictItem> oldDictItems = new ArrayList();
                oldDictItems.addAll(old);
                List<DictItem> items = new ArrayList();

                for (int i = 0; i < oldDictItems.size(); ++i) {
                    for (int m = 0; m < newDictItems.size(); ++m) {
                        if (((DictItem) oldDictItems.get(i)).getKey().equals(((DictItem) newDictItems.get(m)).getKey())) {
                            oldDictItems.set(i, this.getRepeatDict((DictItem) oldDictItems.get(i), (DictItem) newDictItems.get(m)));
                            newDictItems.remove(m);
                            break;
                        }
                    }
                }

                items.addAll(oldDictItems);
                if (newDictItems != null && newDictItems.size() > 0) {
                    items.addAll(newDictItems);
                }

                return items;
            } else {
                return newItems;
            }
        } else {
            return old;
        }
    }

    private DictItem getRepeatDict(DictItem old, DictItem repeat) {
        old.setName(repeat.getName() != null ? repeat.getName() : old.getName());
        old.setStatus(repeat.getStatus() != null ? repeat.getStatus() : old.getStatus());
        old.setValue(repeat.getValue() != null ? repeat.getValue() : old.getValue());
        return old;
    }

    @Override
    @CacheEvict(
            value = {"sys_dictionary"},
            allEntries = true
    )
    public Boolean deleteDictionary(Dictionary dictionary) throws GeneralException {
        if (dictionary != null && dictionary.getId() != null) {
            try {
                if (dictionary.getDictItems() != null) {
                    this.dictItemRepository.deleteAll(dictionary.getDictItems());
                }

                this.dictionaryRepository.deleteById(dictionary.getId());
                return true;
            } catch (Exception var3) {
                var3.printStackTrace();
                throw new GeneralException(ExceptionType.SYSTEMDICTDELETEERROR);
            }
        } else {
            throw new GeneralException(ExceptionType.SYSTEMNOEXISTSDICT);
        }
    }

    @Override
    @CacheEvict(
            value = {"sys_dictionary"},
            allEntries = true
    )
    public Boolean deleteDictionary(Long dict_id) throws GeneralException {
        return this.deleteDictionary((Dictionary) this.dictionaryRepository.getOne(dict_id));
    }

    @Override
    @CacheEvict(
            value = {"sys_dictionary"},
            allEntries = true
    )
    public Dictionary removeItemFromDict(Dictionary dictionary, List<Long> items) throws GeneralException {
        try {
            List<DictItem> dictItems = new ArrayList();
            dictItems.addAll(dictionary.getDictItems());
            List<DictItem> removes = new ArrayList();

            for (int i = 0; i < dictItems.size(); ++i) {
                if (items.contains(((DictItem) dictItems.get(i)).getId())) {
                    this.dictItemRepository.delete(dictItems.get(i));
                    removes.add(dictItems.get(i));
                }
            }

            dictItems.removeAll(removes);
            if (dictItems.size() > 0) {
                dictionary.setDictItems(new ArrayList(dictItems));
            } else {
                dictionary.setDictItems((List) null);
            }

            return this.insertDictionary(dictionary);
        } catch (Exception var6) {
            var6.printStackTrace();
            throw new GeneralException(ExceptionType.SYSTEMDICTDREMOVEITEMERROR);
        }
    }

    @Override
    public Page<Dictionary> queryDictionaries(final DictionaryConditions dictionaryConditions) {
        int page = dictionaryConditions != null ? dictionaryConditions.getPage() : 0;
        int pageSize = dictionaryConditions != null ? dictionaryConditions.getPageSize() : 10;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, new String[]{"id"}));
        if (dictionaryConditions != null) {
            Specification<Dictionary> querySpecifi = new Specification<Dictionary>() {
                private static final long serialVersionUID = 1L;

                public javax.persistence.criteria.Predicate toPredicate(Root<Dictionary> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    List<javax.persistence.criteria.Predicate> predicates = new ArrayList();
                    if (dictionaryConditions.getIds() != null && dictionaryConditions.getIds().size() > 0) {
                        CriteriaBuilder.In<Long> idIn = criteriaBuilder.in(root.get("id"));
                        List<Long> idsList = dictionaryConditions.getIds();

                        for (int i = 0; i < idsList.size(); ++i) {
                            idIn.value(idsList.get(i));
                        }

                        predicates.add(idIn);
                    }

                    if (null != dictionaryConditions.getType() && !"".equals(dictionaryConditions.getType().trim())) {
                        predicates.add(criteriaBuilder.like(root.get("type"), "%" + dictionaryConditions.getType().trim() + "%"));
                    }

                    if (null != dictionaryConditions.getName() && !"".equals(dictionaryConditions.getName().trim())) {
                        predicates.add(criteriaBuilder.like(root.get("name"), "%" + dictionaryConditions.getName().trim() + "%"));
                    }

                    if (null != dictionaryConditions.getGroup() && !"".equals(dictionaryConditions.getGroup().trim())) {
                        predicates.add(criteriaBuilder.like(root.get("group"), "%" + dictionaryConditions.getGroup().trim() + "%"));
                    }

                    if (null != dictionaryConditions.getStatus()) {
                        predicates.add(criteriaBuilder.equal(root.get("status"), dictionaryConditions.getStatus()));
                    }

                    if (dictionaryConditions.getItemKey() != null || dictionaryConditions.getItemName() != null || dictionaryConditions.getItemStatus() != null) {
                        Join<DictItem, Dictionary> dictItem = root.join("dictItems", JoinType.LEFT);
                        if (dictionaryConditions.getItemKey() != null && !"".equals(dictionaryConditions.getItemKey().trim())) {
                            predicates.add(criteriaBuilder.like(dictItem.get("key"), "%" + dictionaryConditions.getItemKey().trim() + "%"));
                        }

                        if (dictionaryConditions.getItemName() != null && !"".equals(dictionaryConditions.getItemName().trim())) {
                            predicates.add(criteriaBuilder.like(dictItem.get("name"), "%" + dictionaryConditions.getItemName().trim() + "%"));
                        }

                        if (dictionaryConditions.getItemStatus() != null) {
                            predicates.add(criteriaBuilder.equal(dictItem.get("status"), dictionaryConditions.getItemStatus()));
                        }

                        criteriaQuery.groupBy(new Expression[]{root.get("id")});
                    }

                    return criteriaBuilder.and((javax.persistence.criteria.Predicate[]) predicates.toArray(new javax.persistence.criteria.Predicate[predicates.size()]));
                }
            };
            return this.dictionaryRepository.findAll(querySpecifi, pageable);
        } else {
            return this.dictionaryRepository.findAll(pageable);
        }
    }

    @Override
    @CacheEvict(
            value = {"sys_dictionary"},
            allEntries = true
    )
    public Boolean deleteDictionary(List<Long> dicts) throws GeneralException {
        try {
            Iterator var2 = dicts.iterator();

            while (var2.hasNext()) {
                Long id = (Long) var2.next();
                this.deleteDictionary(this.queryOneDict(id));
            }

            return true;
        } catch (Exception var4) {
            var4.printStackTrace();
            throw new GeneralException(ExceptionType.SYSTEMDICTDELETEERROR);
        }
    }

    @Override
    @CacheEvict(
            value = {"sys_dictionary"},
            allEntries = true
    )
    public Boolean deleteDictionaryByObjects(List<Dictionary> dictionaries) throws GeneralException {
        try {
            Iterator var2 = dictionaries.iterator();

            while (var2.hasNext()) {
                Dictionary dictionary = (Dictionary) var2.next();
                this.deleteDictionary(dictionary);
            }

            return true;
        } catch (Exception var4) {
            var4.printStackTrace();
            throw new GeneralException(ExceptionType.SYSTEMDICTDELETEERROR);
        }
    }

    @Override
    public Dictionary queryOneDict(Long id) {
        return (Dictionary) this.dictionaryRepository.getOne(id);
    }

    @Override
    @CacheEvict(
            value = {"sys_dictionary"},
            allEntries = true
    )
    public boolean refreshDict() {
        return false;
    }

    @Override
    public DictItem getDictItem(String type, String group, String key) {
        Dictionary dictionary = this.findValidDictByTypeAndGroup(type, group);
        if (dictionary != null) {
            List<DictItem> dictItems = dictionary.getDictItems();
            if (dictItems != null) {
                Optional<DictItem> dictItem = dictItems.stream().filter((item) -> {
                    return item.getKey().equals(key);
                }).findFirst();
                if (dictItem.isPresent()) {
                    return (DictItem) dictItem.get();
                }
            }
        }

        return null;
    }

    @Override
    public List<Map> getDictItemValue2ListMap(String type, String group, String key) {
        DictItem dictItem = this.getDictItem(type, group, key);
        if (dictItem != null) {
            String value = dictItem.getValue();
            if (value != null) {
                List<Map> map = this.getDictItemValue2List(dictItem, Map.class);
                if (map != null) {
                    return map;
                }
            }
        }

        return null;
    }

    @Override
    public <T> List<T> getDictItemValue2List(DictItem dictItem, Class<T> clazz) {
        return dictItem != null && dictItem.getValue() != null ? this.transJSONStr2List(dictItem.getValue(), clazz) : null;
    }

    private <T> List<T> transJSONStr2List(String str, Class<T> clazz) {
        str = str.trim();
        Object result = null;

        try {
            if (str.startsWith("[")) {
                result = JSON.parseArray(str, clazz);
            } else if (str.startsWith("{")) {
                T obj = JSON.parseObject(str, clazz);
                result = new ArrayList();
                ((List) result).add(obj);
            }

            return (List) result;
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    @Override
    public List<String> getDictTypes() {
        return this.dictionaryRepository.getDictTypes();
    }

    @Override
    public List<String> getDictGroups() {
        return this.dictionaryRepository.getDictGroups();
    }

    @Override
    public Map<String, Map<String, Object>> getAllDictItemValueByType(String type) {
        Map<String, Map<String, Object>> map = new HashMap();
        List<Dictionary> listDictionary = this.dictionaryRepository.findByTypeAndStatus(type, DictionaryStatus.VALID.getValue());
        if (CheckUtils.isEmpty(listDictionary)) {
            return null;
        } else {
            List<DictItem> items = null;
            Iterator var5 = listDictionary.iterator();

            while (true) {
                Dictionary item;
                do {
                    if (!var5.hasNext()) {
                        return map;
                    }

                    item = (Dictionary) var5.next();
                    items = item.getDictItems();
                } while (CheckUtils.isEmpty(items));

                Map<String, Object> dictItemMap = new HashMap();
                Iterator var8 = items.iterator();

                while (var8.hasNext()) {
                    DictItem dictItem = (DictItem) var8.next();
                    dictItemMap.put(dictItem.getKey().trim(), dictItem.getValue());
                }

                map.put(item.getGroup(), dictItemMap);
            }
        }
    }

    @Override
    public List<Dictionary> findValidDictByTypeAndGroups(String type, Collection<String> groups) {
        if (groups == null) {
            return null;
        } else {
            List<String> groupList = new ArrayList();
            Iterator var4 = groups.iterator();

            while (var4.hasNext()) {
                String group = (String) var4.next();
                groupList.add(group);
            }

            return this.dictionaryRepository.findByTypeAndGroupInAndStatus(type, groupList, DictionaryStatus.VALID.getValue());
        }
    }

    @Override
    public Map<String, Map<String, String>> findValidDictByTypeAndGroupsForMap(String type, Collection<String> groups) {
        List<Dictionary> listDictionary = this.findValidDictByTypeAndGroups(type, groups);
        if (CheckUtils.isEmpty(listDictionary)) {
            return null;
        } else {
            Map<String, Map<String, String>> map = new HashMap();
            List<DictItem> items = null;
            Iterator var6 = listDictionary.iterator();

            while (true) {
                Dictionary item;
                do {
                    if (!var6.hasNext()) {
                        return map;
                    }

                    item = (Dictionary) var6.next();
                    items = item.getDictItems();
                } while (CheckUtils.isEmpty(items));

                Map<String, String> dictItemMap = new HashMap();
                Iterator var9 = items.iterator();

                while (var9.hasNext()) {
                    DictItem dictItem = (DictItem) var9.next();
                    dictItemMap.put(dictItem.getKey().trim(), dictItem.getValue());
                }

                map.put(item.getGroup(), dictItemMap);
            }
        }
    }
}
