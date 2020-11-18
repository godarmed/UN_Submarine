package com.eseasky.submarine.core.starters.dictionary.module.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(
        name = "sys_dict_item",
        indexes = {@Index(
                name = "uni_index_dict_item_status",
                columnList = "status"
        ), @Index(
                name = "uni_index_dict_item_key",
                columnList = "`key`"
        )}
)
@ApiModel(
        value = "DictItem",
        description = "字典项表定义"
)
@Data
public class DictItem implements Serializable {
    private static final long serialVersionUID = 8050660443876463036L;
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @ApiModelProperty("字典项id")
    private Long id;
    @Column(
            name = "`key`"
    )
    @ApiModelProperty("字典项key")
    private String key;
    @Column(
            name = "value",
            length = 4096
    )
    @ApiModelProperty("字典项value")
    private String value;
    @ApiModelProperty("字典项名称")
    private String name;
    @ApiModelProperty("字典项状态")
    private String status;
    @JsonIgnore
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "dict_id"
    )
    @ApiModelProperty("关联到字典表")
    private Dictionary dictionary;

    public DictItem() {
    }

}

