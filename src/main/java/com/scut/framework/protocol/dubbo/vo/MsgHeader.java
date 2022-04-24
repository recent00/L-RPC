package com.scut.framework.protocol.dubbo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息头
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MsgHeader {
    /*消息体的MD5摘要*/
    private String md5;

    /*消息类型*/
    private byte type;
}
