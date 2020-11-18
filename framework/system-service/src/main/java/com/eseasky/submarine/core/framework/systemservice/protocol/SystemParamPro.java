package com.eseasky.submarine.core.framework.systemservice.protocol;

import com.eseasky.submarine.core.framework.systemservice.protocol.dto.DictiCondiDto;
import com.eseasky.submarine.core.framework.systemservice.protocol.dto.DictionaryDto;
import com.eseasky.submarine.core.framework.systemservice.protocol.vo.DictItemVo;
import com.eseasky.submarine.core.framework.systemservice.protocol.vo.DictionaryVo;
import com.eseasky.submarine.core.starters.global.entity.MsgReturn;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Api(value = "API - 系统参数模块", tags = {"系统参数模块接口"})
public interface SystemParamPro {

    @ApiOperation(value = "根据类型查询有效的字典参数", notes = "根据类型查询有效的字典参数")
    @PostMapping(value = "/dict/queryByType")
    public ResponseEntity<MsgReturn<List<DictionaryVo>>> queryDict(@RequestBody DictionaryDto dictionaryDTO);

    @ApiOperation(value = "分页查询字典参数", notes = "分页查询字典参数")
    @PostMapping(value = "/dict/query")
    public ResponseEntity<MsgReturn<List<DictionaryVo>>> queryDictAdvance(@RequestBody DictiCondiDto dictiCondiDTO);

    @ApiOperation(value = "添加字典参数", notes = "添加字典参数")
    @PostMapping(value = "/dict/add")
    public ResponseEntity<MsgReturn<DictionaryVo>> addDict(@RequestBody DictionaryDto dictionaryDTO);

    @ApiOperation(value = "更新字典参数", notes = "更新字典参数")
    @PostMapping(value = "/dict/update")
    public ResponseEntity<MsgReturn<DictionaryVo>> updateDict(@RequestBody DictionaryDto dictionaryDTO);

    @ApiOperation(value = "删除字典", notes = "删除字典")
    @PostMapping(value = "/dict/delete")
    public ResponseEntity<MsgReturn<List<Long>>> deleteDict(@RequestBody @ApiParam(value = "字典ID列表") List<Long> ids);

    @ApiOperation(value = "添加字典项", notes = "添加字典项")
    @PostMapping(value = "/dict/addItem")
    public ResponseEntity<MsgReturn<DictionaryVo>> addDictItem(@RequestBody DictionaryDto dictionaryDTO);

    @ApiOperation(value = "根据条件删除字典项", notes = "根据条件删除字典项")
    @PostMapping(value = "/dict/removeItem")
    public ResponseEntity<MsgReturn<DictionaryVo>> removeItemByIds(@RequestBody DictionaryDto dictionaryDTO);

    @ApiOperation(value = "获取字典类型", notes = "获取字典类型")
    @PostMapping(value = "/dict/getDictTypes")
    public ResponseEntity<MsgReturn<Map<String, List<String>>>> getDictTypes();

    @ApiOperation(value = "根据类型和分组查询有效的字典参数", notes = "根据类型查询有效的字典参数")
    @PostMapping(value = "/dict/queryByTypeAndGroup")
    public ResponseEntity<MsgReturn<DictionaryVo>> queryByTypeAndGroup(@RequestBody DictiCondiDto dictiCondiDTO);

    @ApiOperation(value = "上传获取excel文件更新字典项", notes = "上传获取excel文件更新字典项")
    @PostMapping(value = "/dict/updateDictByUploadSingleFile")
    public ResponseEntity<MsgReturn<DictionaryVo>> updateDictByUploadSingleFile(DictionaryDto dictionaryDTO);

    @ApiOperation(value = "根据类型和分组查询有效的字典参数", notes = "根据类型查询有效的字典参数")
    @PostMapping(value = "/dict/queryByKeyAndDictId")
    public ResponseEntity<MsgReturn<DictItemVo>> queryByKeyAndDictId(@RequestBody DictiCondiDto dictiCondiDTO);

    @ApiOperation(value = "根据类型和分组查询有效的字典参数List", notes = "根据类型和分组查询有效的字典参数List")
    @PostMapping(value = "/dict/queryListByTypeAndGroup")
    public ResponseEntity<MsgReturn<Map<String, String>>> queryListByTypeAndGroup(@RequestBody Set<DictiCondiDto> dictiCondiDTOS);

    @ApiOperation(value = "更新字典参数(不用鉴权)", notes = "更新字典参数")
    @PostMapping(value = "/dict/updateForService")
    public ResponseEntity<MsgReturn<DictionaryVo>> updateForService(@RequestBody DictionaryDto dictionaryDTO);
}
