package com.eseasky.submarine.core.starters.dictionary;


import com.eseasky.submarine.core.starters.dictionary.module.model.Dictionary;
import org.springframework.stereotype.Service;

@Service
public interface DictConfigHelper {
    Dictionary getDictConfig(String var1, String var2);
}

