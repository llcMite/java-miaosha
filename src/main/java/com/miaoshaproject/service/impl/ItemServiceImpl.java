package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.ItemDOMapper;
import com.miaoshaproject.dao.StockDOMapper;
import com.miaoshaproject.dataobject.ItemDO;
import com.miaoshaproject.dataobject.StockDO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.model.ItemModel;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ValidatorImpl validator;
    @Autowired
    private ItemDOMapper itemDOMapper;
    @Autowired
    private StockDOMapper stockDOMapper;
    @Override
    @Transactional //?不懂
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        ValidationResult result=validator.validate(itemModel);
        if(result.isHasError()){
            throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR,result.getErrMsg());
        }
        ItemDO itemDO=this.convertItemDOFromItemModel(itemModel);
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());
        StockDO stockDO=this.convertItemStockFromItemModel(itemModel);
        stockDOMapper.insertSelective(stockDO);
        return this.getItemById(itemModel.getId());
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOList =itemDOMapper.listItem();
        List<ItemModel> itemModelList= itemDOList.stream().map(itemDO ->{
            StockDO stockDO = stockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel=this.converItemModelFromItemDO(itemDO,stockDO);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO=itemDOMapper.selectByPrimaryKey(id);
        if(itemDO == null){
            return null;
        }
        StockDO stockDO=stockDOMapper.selectByItemId(itemDO.getId());
        ItemModel itemModel=this.converItemModelFromItemDO(itemDO,stockDO);
        return itemModel;
    }

    public ItemDO convertItemDOFromItemModel(ItemModel itemModel){
        if(itemModel==null){
            return null;
        };
        ItemDO itemDO=new ItemDO();
        BeanUtils.copyProperties(itemModel,itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }
    public StockDO convertItemStockFromItemModel(ItemModel itemModel){
        StockDO stockDO=new StockDO();
        stockDO.setItemId(itemModel.getId());
        stockDO.setStock(itemModel.getStock());

        return stockDO;
    }
    public ItemModel converItemModelFromItemDO(ItemDO itemDO,StockDO stockDO){
        ItemModel itemModel=new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(stockDO.getStock());
        return itemModel;
    }

}
