package com.tapc.platform.model.healthcat;

/**
 * Created by Administrator on 2018/4/11.
 */

public class Command {

    public static final byte D_LOGIN = 0x01;
    public static final byte S_LOGIN = 0x02;

    public static class Bike {
        //设备端发送命令
        public static final byte D_READ_INFO = 0x52;
        public static final byte D_READ_STATUS = 0x54;
        public static final byte D_SET_RESISTANCE = 0x56;
        public static final byte D_START_STOP = 0x5D;
        public static final byte D_LOCK = 0x5F;
        public static final byte D_UPLOAD_INFO = 0x5A;
        public static final byte D_IC_START = (byte) 0x58;

        //服务器发送命令
        public static final byte S_READ_INFO = 0x51;
        public static final byte S_READ_STATUS = 0x53;
        public static final byte S_SET_RESISTANCE = 0x55;
        public static final byte S_START_STOP = 0x5C;
        public static final byte S_LOCK = 0x5E;
        public static final byte S_UPLOAD_INFO = 0x5B;
        public static final byte S_IC_START = (byte) 0x57;
    }

    public static class Power {
        //设备端发送命令
        public static final byte D_SET_HEART_TIME = 0x28;
        public static final byte D_START_STOP = (byte) 0xCB;
        public static final byte D_LOCK = (byte) 0xCF;
        public static final byte D_UPLOAD_INFO = (byte) 0xCD;
        public static final byte D_READ_INFO = (byte) 0xC2;
        public static final byte D_IC_START = (byte) 0xC4;

        //服务器发送命令
        public static final byte S_SET_HEART_TIME = 0x27;
        public static final byte S_START_STOP = (byte) 0xCA;
        public static final byte S_LOCK = (byte) 0xCE;
        public static final byte S_UPLOAD_INFO = (byte) 0xCC;
        public static final byte S_READ_INFO = (byte) 0xC1;
        public static final byte S_IC_START = (byte) 0xC3;
    }
}
