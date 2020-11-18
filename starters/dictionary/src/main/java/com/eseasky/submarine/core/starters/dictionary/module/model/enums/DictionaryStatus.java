package com.eseasky.submarine.core.starters.dictionary.module.model.enums;


public enum DictionaryStatus {
    VALID("valid"),
    INVALID("invalid");

    private String value;

    private DictionaryStatus(String value) {
        this.setValue(value);
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
