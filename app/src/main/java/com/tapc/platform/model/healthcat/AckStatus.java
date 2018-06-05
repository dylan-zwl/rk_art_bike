package com.tapc.platform.model.healthcat;

/**
 * Created by Administrator on 2018/4/13.
 */

public class AckStatus {
    public static final byte NULL = (byte) 0xFF;
    public static final byte STOP = 0x01;
    public static final byte START = 0x02;
    public static final byte LOCK = 0x01;
    public static final byte UNLOCK = 0x02;
    public static final byte SUCCESS = (byte) 0xE0;
    public static final byte FAIL = (byte) 0xE1;

}
