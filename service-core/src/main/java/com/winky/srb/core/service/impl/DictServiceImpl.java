package com.winky.srb.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.winky.srb.core.listener.ExcelDictDTOListener;
import com.winky.srb.core.pojo.dto.ExcelDictDTO;
import com.winky.srb.core.pojo.entity.Dict;
import com.winky.srb.core.mapper.DictMapper;
import com.winky.srb.core.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author Winky
 * @since 2022-02-20
 */
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void importData(InputStream inputStream) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(inputStream, ExcelDictDTO.class, new ExcelDictDTOListener(baseMapper)).sheet().doRead();
        log.info("importData finished");
    }

    @Override
    public List<ExcelDictDTO> listDictData() {
        List<Dict> dictList = baseMapper.selectList(null);
        //创建ExcelDictDTO列表，将Dict列表转换成ExcelDictDTO列表
        ArrayList<ExcelDictDTO> excelDictDTOList = new ArrayList<>(dictList.size());
        dictList.forEach(dict -> {

            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(dict, excelDictDTO);
            excelDictDTOList.add(excelDictDTO);
        });
        return excelDictDTOList;
    }

    @Override
    public List<Dict> listByParentId(Long parentId) {

        try {
            //首先查询redis中是否存在数据列表
            List<Dict> dictList = (List<Dict>)redisTemplate.opsForValue().get("srb:core:dictList:" + parentId);
            //如果存在，直接从redis中返回数据列表
            if(dictList != null){
                log.info("从redis中取出列表");
                return dictList;
            }
        } catch (Exception e) {
            log.error("redis服务器异常：" + ExceptionUtils.getStackTrace(e));//此处不抛出异常，继续执行后面的代码
        }

        //如果不存在则查询数据库
        log.info("从数据库中取值");
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id", parentId);
        List<Dict> dicts = baseMapper.selectList(dictQueryWrapper);
        dicts.forEach(dict -> {
            dict.setHasChildren(hasChild(dict.getId()));
        });

        try{
            log.info("数据存入redis中");
            //将数据存入redis
            redisTemplate.opsForValue().set("srb:core:dictList:" + parentId,dicts,5, TimeUnit.MINUTES);
        }catch(Exception e){
            log.error("redis服务器异常：" + ExceptionUtils.getStackTrace(e));//此处不抛出异常，继续执行后面的代码
        }
        return dicts;
    }

    /**
     * 判断当前id所在节点下是否有子节点
     * @param id
     * @return
     */
    private boolean hasChild(Long id){
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id", id);
        Integer integer = baseMapper.selectCount(dictQueryWrapper);
        if(integer > 0){
            return true;
        }return false;
    }
}

































