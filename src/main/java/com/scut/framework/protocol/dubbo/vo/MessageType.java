package com.scut.framework.protocol.dubbo.vo;

/**
 * 消息类型
 */
public enum MessageType {

    SERVICE_REQ((byte) 0),/*业务请求消息*/
    HEARTBEAT_REQ((byte) 1), /*心跳请求消息*/
    HEARTBEAT_RESP((byte) 2);/*心跳应答消息*/

    private byte value;
    private MessageType(byte value) {
        this.value = value;
    }
    public byte value() {
        return this.value;
    }
}
