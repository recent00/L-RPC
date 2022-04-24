package com.scut.framework.protocol.dubbo.vo;


import com.scut.framework.protocol.Invocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消息实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyMessage implements Serializable {
    private MsgHeader msgHeader;
    private Invocation body;
}
