package com.eseasky.submarine.core.starters.dictionary.module.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(
        name = "sys_dictionary",
        indexes = {@Index(
                name = "sys_dictionary_uni_index_type_group",
                columnList = "type,group",
                unique = true
        ), @Index(
                name = "sys_dictionary_index_STATUS",
                columnList = "status"
        )}
)
@ApiModel(
        value = "Dictionary",
        description = "字典表定义"
)
@Data
public class Dictionary implements Serializable {
    private static final long serialVersionUID = -711111343823177305L;
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @ApiModelProperty("字典id")
    private Long id;
    @ApiModelProperty("字典类型")
    private String type;
    @ApiModelProperty("字典名称")
    private String name;
    @ApiModelProperty("字典状态")
    private String status;
    @ApiModelProperty("是否可编辑")
    private String editable;
    @Column(
            name = "`desc`"
    )
    @ApiModelProperty("描述")
    private String desc;
    @Column(
            name = "`group`"
    )
    @ApiModelProperty("字典分组")
    private String group;
    @OneToMany(
            mappedBy = "dictionary",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.MERGE}
    )
    @JsonIgnoreProperties({"dictionary"})
    @ApiModelProperty("字典项定义")
    private List<DictItem> dictItems;

    public Dictionary() {
    }

}
