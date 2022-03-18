package com.winky.srb.core.pojo.BO;
import com.winky.srb.core.enums.TransTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
/**
 * @auther Li Wenjie
 * @create 2022-03-18 15:12
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransFlowBO {

    private String agentBillNo;
    private String bindCode;
    private BigDecimal amount;  //当前操作金额
    private TransTypeEnum transTypeEnum;  //当前操作类型（充值，体现……）
    private String memo;
}