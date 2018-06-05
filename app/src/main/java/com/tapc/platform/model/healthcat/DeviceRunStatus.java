package com.tapc.platform.model.healthcat;

/**
 * Created by Administrator on 2018/4/17.
 */

public class DeviceRunStatus {
    public static final byte LOCK_SCREEN = 0x03;
    public static final byte UNSTART = 0x08;
    public static final byte STARTED = 0x09;
    public static final byte RUNNING = 0x04;
    public static final byte STOPED = 0x10;
    public static final byte TIMEOUT = 0x11;
    public static final byte ERROR = 0x0A;
}
