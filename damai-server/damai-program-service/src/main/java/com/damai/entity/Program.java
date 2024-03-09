package com.damai.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.damai.data.BaseTableData;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: 极度真实还原大麦网高并发实战项目。 添加 阿宽不是程序员 微信，添加时备注 damai 来获取项目的完整资料 
 * @description: 节目 实体
 * @author: 阿宽不是程序员
 **/
@Data
@TableName("d_program")
public class Program extends BaseTableData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 所在区域id
     */
    private Long areaId;

    /**
     * 节目类型表id
     */
    private Long programCategoryId;
    
    /**
     * 父节目类型表id
     * */
    private Long parentProgramCategoryId;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 艺人
     * */
    private String actor;
    
    /**
     * 地点
     * */
    private String place;
    
    /**
     * 项目图片
     * */
    private String itemPicture;

    /**
     * 项目详情
     */
    private String detail;

    /**
     * 限购规则
     */
    private String purchaseLimitRule;

    /**
     * 退票/换票规则
     */
    private String refundTicketRule;

    /**
     * 配送信息说明
     */
    private String deliveryInstruction;

    /**
     * 入场规则
     */
    private String entryRule;

    /**
     * 儿童购票
     */
    private String childPurchase;

    /**
     * 发票说明
     */
    private String invoiceSpecification;

    /**
     * 实名购票规则
     */
    private String realTicketPurchaseRule;

    /**
     * 异常排单说明
     */
    private String abnormalOrderDescription;

    /**
     * 温馨提示
     */
    private String kindReminder;

    /**
     * 演出时长
     */
    private String performanceDuration;

    /**
     * 入场时间
     */
    private String entryTime;
    
    /**
     * 最低演出曲目
     * */
    private Integer minPerformanceCount;
    
    /**
     * 主要演员
     * */
    private String mainActor;
    
    /**
     * 最低演出时长
     * */
    private String minPerformanceDuration;

    /**
     * 禁止携带物品
     */
    private String prohibitedItem;

    /**
     * 寄存说明
     */
    private String depositSpecification;

    /**
     * 大麦网初始开售时全场可售门票总张数
     */
    private Long totalCount;
    
    /**
     * 是否允许退款 1:允许 0:不允许
     */
    private Integer permitRefund;
    
    /**
     * 是否允许选座 1:允许选座 0:不允许选座
     */
    private Integer permitChooseSeat;
    
    /**
     * 电子票/快递票 1:是 0:不是
     */
    private Integer electronicDeliveryTicket;
    
    /**
     * 电子发票 1:是 0:不是
     */
    private Integer electronicInvoice;
    
    /**
     * 高热度节目 0:否 1:是
     * */
    private Integer highHeat;
    
    /**
     * 节目状态 1:上架 0:下架
     */
    private Integer programStatus;
}
