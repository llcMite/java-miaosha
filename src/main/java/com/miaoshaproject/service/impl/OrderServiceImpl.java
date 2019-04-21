package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.ItemDOMapper;
import com.miaoshaproject.dao.OrderDOMapper;
import com.miaoshaproject.dao.SequenceDOMapper;
import com.miaoshaproject.dao.UserDOMapper;
import com.miaoshaproject.dataobject.ItemDO;
import com.miaoshaproject.dataobject.OrderDO;
import com.miaoshaproject.dataobject.SequenceDO;
import com.miaoshaproject.dataobject.UserDO;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.OrderService;
import com.miaoshaproject.service.model.ItemModel;
import com.miaoshaproject.service.model.OrderModel;
import com.miaoshaproject.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Override
    @Transactional //保证订单是在同一事务当中？
    public OrderModel createModel(Integer userId, Integer itemId,Integer promoId, Integer amount) throws BusinessException {
       //1校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确
       ItemModel itemModel = itemService.getItemById(itemId);
       if(itemModel == null){
           throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR,"商品信息不存在");
       }
       UserModel userModel =userService.getUserById(userId);
       if(userModel == null){
           throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR,"用户不存在");
       }
       if(amount<=0 || amount>99){
           throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR,"商品数量不正确");
       }
       //校验活动信息
        if(promoId != null){
            //校验对应的活动是否存在这个商品
            if(promoId.intValue()!= itemModel.getPromoModel().getId()){
                throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR,"活动信息不正确");
            }else if(itemModel.getPromoModel().getStatus().intValue()!=2){//判断活动是否进行中
                throw new BusinessException(EmBusinessError.PARAMTER_VALIDATION_ERROR,"不在活动时间");
            }
        }

       //2.落单减库存
       boolean result =itemService.decreaseStock(itemId,amount);
       if(!result){
           throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
       }


       //3.订单入库
       OrderModel orderModel = new OrderModel();
       orderModel.setUserId(userId);
       orderModel.setItemId(itemId);
       orderModel.setPromoId(promoId);
       orderModel.setAmount(amount);
       if(promoId !=null){
           orderModel.setItemPrice(itemModel.getPromoModel().getPromo_item_price());
       }else{
           orderModel.setItemPrice(itemModel.getPrice());
       }

       orderModel.setOrderAmount(itemModel.getPrice().multiply(new BigDecimal(amount)));
       //生成订单号
       orderModel.setId(generatorOrderNo());
       OrderDO orderDO=this.convertOrderDOFromOrderModel(orderModel);
       orderDOMapper.insertSelective(orderDO);

       //加上销售额
        itemService.increaseSales(itemId,amount);
        //返回前端
        return orderModel;
    }

    public OrderDO convertOrderDOFromOrderModel(OrderModel orderModel){
        OrderDO orderDO = new OrderDO();
        if(orderModel == null){
            return null;
        }
        BeanUtils.copyProperties(orderModel,orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderAmount(orderModel.getOrderAmount().doubleValue());
        return orderDO;
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generatorOrderNo(){
        //订单号有16
        StringBuilder stringBuilder = new StringBuilder();
        // 前8位是时间年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);
        //中间6位是自增序列,
        // 创建sequence_info表有字段name current_value step
        //每次加step，然后更新表，不够6位用0补上
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue()+sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKey(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for(int i=0;i<6 - sequenceStr.length(); i++){
            //这里需要考虑的一点是如果大于6的
            // 时候怎么处理
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);
        //最后两位是分库分表位,这里而写死
        stringBuilder.append("00");

       return stringBuilder.toString();
    }

}
