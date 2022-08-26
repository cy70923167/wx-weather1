package com.shiyi.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 请求对象
 */
@Data
@Accessors(chain = true)
public class RequestVO {
    /**
     * 接收者openId
     */
    private String touser;
    /**
     * 模板id
     */
    private String template_id = "4QMHlo8Sb1cds8gEf5VgfvM8PcKleYaKCw_AR2pnbeM";
    /**
     * 微信接口默认地址
     */
    private String url = "http://weixin.qq.com/download";
    /**
     * 背景颜色
     */
    private String topcolor = "#FF0000";
    /**
     * 参数对象
     */
    private DataVO data;

}
