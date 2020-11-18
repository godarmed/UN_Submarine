package com.eseasky.submarine.core.starters.dictionary.module.model.local;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(
        value = "DictionaryConditions",
        description = "字典分页查询条件"
)
@Data
public class DictionaryConditions {
    @ApiModelProperty("字典id列表")
    private List<Long> ids;
    @ApiModelProperty("字典名称")
    private String name;
    @ApiModelProperty("字典分组")
    private String group;
    @ApiModelProperty("字典状态")
    private String status;
    @ApiModelProperty("字典类型")
    private String type;
    @ApiModelProperty("字典项key")
    private String itemKey;
    @ApiModelProperty("字典项名称")
    private String itemName;
    @ApiModelProperty("字典项状态")
    private String itemStatus;
    @ApiModelProperty("分页页码")
    private int page = 0;
    @ApiModelProperty("每页条数")
    private int pageSize = 10;

    public DictionaryConditions() {
    }
}
