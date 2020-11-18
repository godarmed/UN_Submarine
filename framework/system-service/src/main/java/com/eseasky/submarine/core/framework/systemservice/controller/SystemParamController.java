package com.eseasky.submarine.core.framework.systemservice.controller;


import com.alibaba.fastjson.JSON;
import com.eseasky.submarine.core.framework.systemservice.protocol.SystemParamPro;
import com.eseasky.submarine.core.framework.systemservice.protocol.dto.DictiCondiDto;
import com.eseasky.submarine.core.framework.systemservice.protocol.dto.DictionaryDto;
import com.eseasky.submarine.core.framework.systemservice.protocol.vo.DictItemVo;
import com.eseasky.submarine.core.framework.systemservice.protocol.vo.DictionaryVo;
import com.eseasky.submarine.core.framework.systemservice.service.SystemDictService;
import com.eseasky.submarine.core.starters.dictionary.exception.GeneralException;
import com.eseasky.submarine.core.starters.dictionary.module.model.DictItem;
import com.eseasky.submarine.core.starters.dictionary.module.model.Dictionary;
import com.eseasky.submarine.core.starters.global.entity.MsgPageInfo;
import com.eseasky.submarine.core.starters.global.entity.MsgReturn;
import com.eseasky.submarine.core.starters.global.utils.CheckUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@Api(value = "API - 系统参数模块", tags = {"系统参数模块接口"})
@RestController
public class SystemParamController implements SystemParamPro {
    @Autowired
    SystemDictService systemDictService;

    @Override
    @ApiOperation(value = "根据类型查询有效的字典参数", notes = "根据类型查询有效的字典参数")
    public ResponseEntity<MsgReturn<List<DictionaryVo>>> queryDict(@RequestBody DictionaryDto dictionaryDTO) {
        MsgReturn<List<DictionaryVo>> msgReturn = new MsgReturn<List<DictionaryVo>>();
        if (dictionaryDTO != null && dictionaryDTO.getType() != null && !"".equals(dictionaryDTO.getType())) {
            msgReturn.success(systemDictService.findValidDictByType(dictionaryDTO.getType()));
        } else {
            msgReturn.fail("缺少入参type");
        }
        return new ResponseEntity<MsgReturn<List<DictionaryVo>>>(msgReturn, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "分页查询字典参数", notes = "分页查询字典参数")
    public ResponseEntity<MsgReturn<List<DictionaryVo>>> queryDictAdvance(@RequestBody DictiCondiDto dictiCondiDTO) {
        MsgReturn<List<DictionaryVo>> msgReturn = new MsgReturn<List<DictionaryVo>>();
        Page<Dictionary> dictionaries = systemDictService.queryDictionaries(dictiCondiDTO);
        List<DictionaryVo> listDictionaryVo = dictionaries.getContent().stream().map(item -> {
            DictionaryVo dictionaryVO = new DictionaryVo();
            if (item != null) {
                BeanUtils.copyProperties(item, dictionaryVO);
                if ((dictiCondiDTO.getItemKey() != null && !CheckUtils.isEmpty(dictiCondiDTO.getItemKey().trim()))
                        || (dictiCondiDTO.getItemName() != null && !CheckUtils.isEmpty(dictiCondiDTO.getItemName().trim()))
                        || !CheckUtils.isEmpty(dictiCondiDTO.getItemStatus())) {
                    List<DictItemVo> newDictItems = new ArrayList<>();
                    for (DictItem dictItem : item.getDictItems()) {
                        Boolean[] needFlag = {true, true, true};
                        if (dictiCondiDTO.getItemKey() != null && !"".equals(dictiCondiDTO.getItemKey().trim())) {
                            if (dictItem.getKey().contains(dictiCondiDTO.getItemKey().trim())) {
                                needFlag[0] = true;
                            } else {
                                needFlag[0] = false;
                            }
                        }
                        if (dictiCondiDTO.getItemName() != null && !"".equals(dictiCondiDTO.getItemName().trim())) {
                            if (dictItem.getName().contains(dictiCondiDTO.getItemName().trim())) {
                                needFlag[1] = true;
                            } else {
                                needFlag[1] = false;
                            }
                        }
                        if (dictiCondiDTO.getItemStatus() != null && !"".equals(dictiCondiDTO.getItemStatus().trim())) {
                            if (dictItem.getStatus().equals(dictiCondiDTO.getItemStatus())) {
                                needFlag[2] = true;
                            } else {
                                needFlag[2] = false;
                            }
                        }
                        if (needFlag[0] && needFlag[1] && needFlag[2]) {
                            DictItemVo dictItemVO = new DictItemVo();
                            BeanUtils.copyProperties(dictItem, dictItemVO);
                            newDictItems.add(dictItemVO);
                        }
                    }
                    dictionaryVO.setDictItems(newDictItems);
                }
            }
            return dictionaryVO;
        }).collect(Collectors.toList());
        msgReturn.success().setData(listDictionaryVo, MsgPageInfo.loadFromPageable(dictionaries));
        return new ResponseEntity<MsgReturn<List<DictionaryVo>>>(msgReturn, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "添加字典参数", notes = "添加字典参数")
    public ResponseEntity<MsgReturn<DictionaryVo>> addDict(@RequestBody DictionaryDto dictionaryDTO) {
        MsgReturn<DictionaryVo> msgReturn = new MsgReturn<DictionaryVo>();
        try {
            msgReturn.success(systemDictService.insertDictionary(dictionaryDTO));
        } catch (GeneralException e) {
            e.printStackTrace();
            msgReturn.fail(e.getMessage());
        }
        return new ResponseEntity<MsgReturn<DictionaryVo>>(msgReturn, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "更新字典参数", notes = "更新字典参数")
    public ResponseEntity<MsgReturn<DictionaryVo>> updateDict(@RequestBody DictionaryDto dictionaryDTO) {
        MsgReturn<DictionaryVo> msgReturn = new MsgReturn<DictionaryVo>();
        try {
            msgReturn.success(systemDictService.updateDictionary(dictionaryDTO));
        } catch (GeneralException e) {
            e.printStackTrace();
            msgReturn.fail(e.getMessage());
        }
        return new ResponseEntity<MsgReturn<DictionaryVo>>(msgReturn, HttpStatus.OK);

    }

    @Override
    @ApiOperation(value = "删除字典参数", notes = "删除字典参数")
    public ResponseEntity<MsgReturn<List<Long>>> deleteDict(@RequestBody @ApiParam(value = "字典ID列表") List<Long> ids) {
        MsgReturn<List<Long>> msgReturn = new MsgReturn<List<Long>>();
        try {
            if (systemDictService.deleteDictionary(ids)) {
                msgReturn.success(ids);
            } else {
                msgReturn.fail("删除字典参数失败");
            }
        } catch (GeneralException e) {
            e.printStackTrace();
            msgReturn.fail(e.getMessage());
        }
        return new ResponseEntity<MsgReturn<List<Long>>>(msgReturn, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "添加字典项", notes = "添加字典项")
    public ResponseEntity<MsgReturn<DictionaryVo>> addDictItem(@RequestBody DictionaryDto dictionaryDTO) {
        MsgReturn<DictionaryVo> msgReturn = new MsgReturn<DictionaryVo>();
        try {
            msgReturn.success(systemDictService.addItemToGroup(dictionaryDTO.getId(), dictionaryDTO.getDictItems()));
        } catch (GeneralException e) {
            e.printStackTrace();
            msgReturn.fail(e.getMessage());
        }
        return new ResponseEntity<MsgReturn<DictionaryVo>>(msgReturn, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "根据条件删除字典项", notes = "根据条件删除字典项")
    public ResponseEntity<MsgReturn<DictionaryVo>> removeItemByIds(@RequestBody DictionaryDto dictionaryDTO) {
        MsgReturn<DictionaryVo> msgReturn = new MsgReturn<DictionaryVo>();
        try {
            msgReturn.success(systemDictService.removeItemByIds(dictionaryDTO));
        } catch (GeneralException e) {
            e.printStackTrace();
            msgReturn.fail(e.getMessage());
        }
        return new ResponseEntity<MsgReturn<DictionaryVo>>(msgReturn, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "获取字典类型", notes = "获取字典类型")
    public ResponseEntity<MsgReturn<Map<String, List<String>>>> getDictTypes() {
        MsgReturn<Map<String, List<String>>> msgReturn = new MsgReturn<Map<String, List<String>>>();
        msgReturn.success(systemDictService.getDictTypes());
        return new ResponseEntity<MsgReturn<Map<String, List<String>>>>(msgReturn, HttpStatus.OK);
    }


    @Override
    @ApiOperation(value = "上传获取excel文件更新字典项", notes = "上传获取excel文件更新字典项")
    public ResponseEntity<MsgReturn<DictionaryVo>> updateDictByUploadSingleFile(DictionaryDto dictionaryDTO) {
        MsgReturn<DictionaryVo> msgReturn = new MsgReturn<DictionaryVo>();
        try {
            if (CheckUtils.isEmpty(dictionaryDTO.getFiles())) {
                msgReturn.fail("上传文件为空");
            } else {
                msgReturn.success(systemDictService.updateDictByUploadSingleFile(dictionaryDTO));
            }
        } catch (Exception e) {
            e.printStackTrace();
            msgReturn.fail(e.getMessage());
        }
        return new ResponseEntity<MsgReturn<DictionaryVo>>(msgReturn, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "上传获取excel文件更新字典项", notes = "上传获取excel文件更新字典项")
    public ResponseEntity<MsgReturn<DictItemVo>> queryByKeyAndDictId(@RequestBody DictiCondiDto dictiCondiDTO) {
        MsgReturn<DictItemVo> msgReturn = new MsgReturn<DictItemVo>();
        try {
            DictiCondiDto dictiCondiDto = new DictiCondiDto();
            BeanUtils.copyProperties(dictiCondiDTO, dictiCondiDto);
            msgReturn.success(systemDictService.queryByKeyAndDictId(dictiCondiDto));
        } catch (Exception e) {
            e.printStackTrace();
            msgReturn.fail(e.getMessage());
        }
        return new ResponseEntity<MsgReturn<DictItemVo>>(msgReturn, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MsgReturn<DictionaryVo>> queryByTypeAndGroup(@RequestBody DictiCondiDto dictionaryDTO) {
        MsgReturn<DictionaryVo> msgReturn = new MsgReturn<DictionaryVo>();
//		if (AuthContextHelper.currentUser() == null) {
//			if (!TokenUtils.check(SystemServiceConfig.NO_LOGIN_KEY, dictionaryDTO.getAuthKey())) {
//				msgReturn.setStatus(403);
//				msgReturn.setMsg("没有权限");
//				return new ResponseEntity<MsgReturn<DictionaryVo>>(msgReturn, HttpStatus.FORBIDDEN);
//			}
//		}
        if (!StringUtils.isBlank(dictionaryDTO.getGroup()) && !StringUtils.isBlank(dictionaryDTO.getType())) {
            Dictionary dictionary = systemDictService.findValidDictByTypeAndGroup(dictionaryDTO.getType(), dictionaryDTO.getGroup());
            if (dictionary != null) {
                DictionaryVo dictionaryVO = new DictionaryVo();
                BeanUtils.copyProperties(dictionary, dictionaryVO, "dictItems");
                if (dictionary.getDictItems() != null && dictionary.getDictItems().size() > 0) {
                    List<DictItem> dictItems = dictionary.getDictItems();
                    ArrayList<DictItemVo> list = new ArrayList<DictItemVo>();
                    for (DictItem dictItem : dictItems) {
                        DictItemVo dictItemVO = new DictItemVo();
                        BeanUtils.copyProperties(dictItem, dictItemVO);
                        list.add(dictItemVO);
                    }
                    dictionaryVO.setDictItems(list);
                }
                msgReturn.success(dictionaryVO);
            } else {
                msgReturn.fail("暂无数据");
            }
        } else {
            msgReturn.fail("缺少入参type或者group");
        }
        return new ResponseEntity<MsgReturn<DictionaryVo>>(msgReturn, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<MsgReturn<Map<String, String>>> queryListByTypeAndGroup(@RequestBody Set<DictiCondiDto> dictiCondiDTOS) {
        MsgReturn<Map<String, String>> msgReturn = new MsgReturn<Map<String, String>>();
        if (dictiCondiDTOS != null && dictiCondiDTOS.size() > 0) {
            Map<String, String> map = new HashMap<>();
            for (DictiCondiDto dictiCondiDTO : dictiCondiDTOS) {
                try {
                    DictiCondiDto dictiCondiDto = new DictiCondiDto();
                    BeanUtils.copyProperties(dictiCondiDTO, dictiCondiDto);
                    DictItemVo dictItemVO = systemDictService.queryByKeyAndDictId(dictiCondiDto);
                    map.put(JSON.toJSONString(dictiCondiDTO), dictItemVO != null ? dictItemVO.getValue() : null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            msgReturn.setData(map);
        }
        return new ResponseEntity<MsgReturn<Map<String, String>>>(msgReturn, HttpStatus.OK);
    }

    @ApiOperation(value = "系统参数模块表Model", notes = "此接口不使用，只做输出Model数据结构")
    @PostMapping(value = "/dict/testSwaggerApi", consumes = "application/json")
    public void testSwaggerApi(@RequestBody Dictionary Dictionary
            , @RequestBody DictItem DictItem) {

    }

    @Override
    public ResponseEntity<MsgReturn<DictionaryVo>> updateForService(DictionaryDto dictionaryDTO) {
        // TODO Auto-generated method stub
        MsgReturn<DictionaryVo> msgReturn = new MsgReturn<DictionaryVo>();
        try {
            DictionaryVo dictionaryVO = null;
            DictionaryVo dictionaryVo = systemDictService.updateDictionary(dictionaryDTO);
            if (dictionaryVo != null) {
                dictionaryVO = new DictionaryVo();
                BeanUtils.copyProperties(dictionaryVo, dictionaryVO);
            }
            msgReturn.success(dictionaryVO);
        } catch (GeneralException e) {
            e.printStackTrace();
            msgReturn.fail(e.getMessage());
        }
        return new ResponseEntity<MsgReturn<DictionaryVo>>(msgReturn, HttpStatus.OK);
    }
}
