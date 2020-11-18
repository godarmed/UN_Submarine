package com.eseasky.submarine.core.starters.dictionary;

import com.eseasky.submarine.core.starters.dictionary.DictConfigHelper;
import com.eseasky.submarine.core.starters.dictionary.module.model.Dictionary;
import com.eseasky.submarine.core.starters.dictionary.module.model.enums.DictionaryStatus;
import com.eseasky.submarine.core.starters.dictionary.module.service.SystemManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;

@ConditionalOnClass({SystemManagerService.class})
@Service
public class DictConfigHelperImpl implements DictConfigHelper {
    @Autowired
    SystemManagerService systemManagerService;

    public DictConfigHelperImpl() {
    }

    @Override
    public Dictionary getDictConfig(String type, String group) {
        try {
            return this.systemManagerService.findDictByTypeAndGroupAndStatus(type, group, DictionaryStatus.VALID.name().toLowerCase());
        } catch (Exception var4) {
            return null;
        }
    }
}
