package com.shiyi.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 参数对象
 */
@Data
@Accessors(chain = true)
public class DataVO {

    /**
     * 恋爱天数
     */
    private PropertyVO love;
    /**
     * 生日天数
     */
    private PropertyVO birthday;
    /**
     * 城市
     */
    private PropertyVO city;
    /**
     * 日期
     */
    private PropertyVO date;
    /**
     * 天气
     */
    private PropertyVO weather;
    /**
     * 最低气温
     */
    private PropertyVO minTemperature;
    /**
     * 最高气温
     */
    private PropertyVO maxTemperature;
    /**
     * 湿度
     */
    private PropertyVO humidity;
    /**
     * 风向
     */
    private PropertyVO windDirection;
    /**
     * 风力等级
     */
    private PropertyVO windLevel;
    /**
     * 气压
     */
    private PropertyVO airPressure;
    /**
     * 空气质量
     */
    private PropertyVO airQuality;
    /**
     * 寄言
     */
    private PropertyVO presence;

}
