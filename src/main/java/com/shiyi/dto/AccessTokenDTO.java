package com.shiyi.dto;

import lombok.Data;

/**
 * 公众号获取token
 */
@Data
public class AccessTokenDTO {

    /**
     * token
     */
    private String access_token;

    /**
     * 过期时间
     */
    private String expires_in;

}
