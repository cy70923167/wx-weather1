package com.shiyi.dto;

import lombok.Data;

/**
 * 天气信息对象
 */
@Data
public class WeatherDTO {

    /**
     * 城市
     */
    private String city;
    /**
     * 日期
     */
    private String date;
    /**
     * 星期数
     */
    private String week;
    /**
     * 天气情况
     */
    private String wea;
    /**
     * 白天温度（最高）
     */
    private String tem_day;
    /**
     * 晚上温度（最低）
     */
    private String tem_night;
    /**
     * 风向
     */
    private String win;
    /**
     * 	风力等级
     */
    private String win_speed;
    /**
     * 风速
     */
    private String win_meter;
    /**
     * 空气质量
     */
    private String air;
    /**
     * 气压
     */
    private String pressure;
    /**
     * 湿度
     */
    private String humidity;

}
