package com.tapc.platform.model.healthcat;

import android.text.TextUtils;

/**
 * Created by Administrator on 2018/4/11.
 */

public class BaseDataPack {
    protected static byte[] start = new byte[]{(byte) 0xFF, (byte) 0xFE};
    protected static byte sid;
    protected static byte end = 0x16;

    public BaseDataPack(byte sid) {
        this.sid = sid;
    }

    public byte[] getDataStream(byte cmd, byte[] data) {
        if (data == null) {
            data = new byte[0];
        }
        byte length = (byte) data.length;
        int packLength = length + 7;

        byte[] packData = new byte[packLength];
        packData[0] = start[0];
        packData[1] = start[1];
        packData[2] = sid;
        packData[3] = cmd;
        packData[4] = length;
        System.arraycopy(data, 0, packData, 5, length);

        byte crc = getCrc(packData);
        packData[packData.length - 2] = crc;

        packData[packData.length - 1] = end;
        return packData;
    }

    private byte getCrc(byte data[]) {
        byte crc = 0;
        if (data != null && data.length > 7) {
            int star = 2;
            int end = data.length - 3;
            for (int i = star; i <= end; i++) {
                crc = (byte) (crc + data[i]);
            }
        }
        return crc;
    }

    public byte getComman(byte[] dataPack) {
        if (dataPack != null && dataPack.length > 4) {
            return dataPack[3];
        }
        return 0;
    }

    public byte getCommanData(byte[] data, int index) {
        if (data.length >= (index + 1)) {
            return data[index];
        }
        return AckStatus.NULL;
    }

    public byte[] getCommanDataPack(byte[] dataPack) {
        int length = dataPack.length;
        if (dataPack != null && length > 5) {
            int dataLength = dataPack[4] & 0xff;
            if (dataLength >= 0 && dataLength == (length - 7)) {
                byte crc = getCrc(dataPack);
                if (crc == dataPack[length - 2]) {
                    byte[] data = new byte[dataLength];
                    System.arraycopy(dataPack, 5, data, 0, data.length);
                    return data;
                }
            }
        }
        return new byte[0];
    }

    public byte[] login(String deviceId) {
        if (TextUtils.isEmpty(deviceId)) {
            return null;
        }
        return getDataStream(Command.D_LOGIN, deviceId.getBytes());
    }

    public byte[] ackStatus(byte command, byte status) {
        byte[] data = new byte[]{status};
        return getDataStream(command, data);
    }

    /**
     * int转byte[]
     */
    public static byte[] intToBytes(int i) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (i & 0xff);
        bytes[1] = (byte) ((i >> 8) & 0xff);
        bytes[2] = (byte) ((i >> 16) & 0xff);
        bytes[3] = (byte) ((i >> 24) & 0xff);
        return bytes;
    }


    public static byte[] getStart() {
        return start;
    }

    public static void setStart(byte[] start) {
        BaseDataPack.start = start;
    }

    public static byte getEnd() {
        return end;
    }

    public static void setEnd(byte end) {
        BaseDataPack.end = end;
    }

    public byte getSid() {
        return sid;
    }

    public void setSid(byte sid) {
        this.sid = sid;
    }

}
