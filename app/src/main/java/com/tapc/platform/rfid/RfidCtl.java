package com.tapc.platform.rfid;

import android.os.SystemClock;
import android.util.Log;

import com.tapc.platform.usb.UsbCtl;
import com.tapc.platform.usb.UsbRecvListerner;

public class RfidCtl {
    private UsbCtl mUsbCtl;
    private static final int ACK_SUCCESS = 0;
    private static final int ACK_FAIL = 1;
    private byte[] mCardUid;
    private byte nowCmdCode = 0;
    private RfidListener mRfidListener;
    private boolean isConnectDevice = false;

    public RfidCtl(UsbCtl usbCtl) {
        mUsbCtl = usbCtl;
        mUsbCtl.setUsbRecvListerner(usbRecvListerner);
    }

    public void init() {
        mUsbCtl.sendData((byte) 0x20);
        SystemClock.sleep(5);
        mUsbCtl.sendData((byte) 0x20);
    }

    public void getDvcInfo() {
        sendCmd(RfidCmdClass.DEVICE_CTL, RfidCtlCmdCode.GET_DVC_INFO, null);
    }

    public void PCDSetTX(byte ucSelTX) {
        byte[] infor = new byte[]{ucSelTX};
        sendCmd(RfidCmdClass.DEVICE_CTL, RfidCardCmdCode.PCD_SET_TX, infor);
    }

    public void activationCard() {
        byte[] infor = new byte[]{(byte) 0x00, (byte) 0x26};
        sendCmd(RfidCmdClass.MIFARE_S50_S70, RfidCardCmdCode.MF_ACTIVATE, infor);
    }

    public void authent(byte[] uid, int block) {
        if (uid != null) {
            byte[] passward = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
            byte[] infor = new byte[12];
            infor[0] = 0x60;
            System.arraycopy(uid, 0, infor, 1, uid.length);
            System.arraycopy(passward, 0, infor, 5, passward.length);
            infor[infor.length - 1] = (byte) (block & 0xff);
            sendCmd(RfidCmdClass.MIFARE_S50_S70, RfidCardCmdCode.MF_AUTHENT, infor);
        }
    }

    public boolean piceAutoDetect(byte ADMode, byte txMode, byte reqCode, byte authMode, byte keyType, byte[] key,
                                  int block) {
        int inforLength = 0;
        if (authMode == 'F') {
            inforLength = 12;
        } else {
            return false;
        }
        byte[] infor = new byte[inforLength];
        infor[0] = ADMode;
        infor[1] = txMode;
        infor[2] = reqCode;
        infor[3] = authMode;
        infor[4] = keyType;
        byte[] passward = new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff};
        System.arraycopy(passward, 0, infor, 5, passward.length);
        infor[inforLength - 1] = (byte) (block & 0xff);
        sendCmd(RfidCmdClass.MIFARE_S50_S70, RfidCardCmdCode.PICE_AUTO_DETECT, infor);
        return true;
    }

    public void mfGetValue(int block) {
        byte[] infor = new byte[]{(byte) (block & 0xff)};
        sendCmd(RfidCmdClass.MIFARE_S50_S70, RfidCardCmdCode.MF_READ, infor);
    }

    public boolean mfSetValue(int block, byte[] value) {
        if (block == 1 || block % 4 == 3 || value == null) {
            return false;
        }

        int length = value.length;
        if (length > 16) {
            return false;
        }
        byte[] infor = new byte[length + 1];
        infor[0] = (byte) block;
        System.arraycopy(value, 0, infor, 1, length);
        sendCmd(RfidCmdClass.MIFARE_S50_S70, RfidCardCmdCode.MF_WRITE, infor);
        return true;
    }

    public boolean mfSetAuthent(int block, byte[] passward) {
        if (block % 4 != 3) {
            return false;
        }
        if (passward == null) {
            return false;
        } else if (passward.length != 16) {
            return false;
        }
        int length = passward.length;
        byte[] infor = new byte[length + 1];
        infor[0] = (byte) block;
        System.arraycopy(passward, 0, infor, 1, length);
        sendCmd(RfidCmdClass.MIFARE_S50_S70, RfidCardCmdCode.MF_WRITE, infor);
        return true;
    }

    public int checkCmdStatus(byte[] data) {
        if (data[2] == ACK_SUCCESS) {
            return ACK_SUCCESS;
        } else {
            return ACK_FAIL;
        }
    }

    private UsbRecvListerner usbRecvListerner = new UsbRecvListerner() {

        @Override
        public void update(Object data) {
            byte[] datas = (byte[]) data;
            int length = datas.length;
            byte cmdClass = datas[1];
            if (checkCmdStatus(datas) == ACK_SUCCESS) {
                Log.d("rfid ack", "success");
            } else {
                Log.d("rfid ack", "failed");
                return;
            }
            switch (cmdClass) {
                case RfidCmdClass.DEVICE_CTL:
                    deviceCtlCmd(datas);
                    break;
                case RfidCmdClass.MIFARE_S50_S70:
                    mifareCmd(datas);
                    break;
                default:
                    break;
            }
            nowCmdCode = -1;
        }
    };

    private void mifareCmd(byte[] datas) {
        byte cmd = nowCmdCode;
        switch (cmd) {
            case RfidCardCmdCode.MF_ACTIVATE:
                int uidLength1 = datas[7];
                byte[] uid1 = new byte[uidLength1];
                System.arraycopy(datas, 8, uid1, 0, uidLength1);
                sendUidToUi(uid1);
                break;
            case RfidCardCmdCode.MF_AUTHENT:

                break;
            case RfidCardCmdCode.MF_READ:
                Log.d("mf read", "success");
                break;
            case RfidCardCmdCode.MF_WRITE:
                Log.d("mf write", "success");
                break;
            case RfidCardCmdCode.PICE_AUTO_DETECT:
                if (datas[0] == 0x1f) {
                    sendCardUid(datas);
                } else {
                    Log.d("pice auto detect", "success");
                }
                break;

            default:
                if (datas[0] == 0x1f) {
                    sendCardUid(datas);
                }
                break;
        }
    }

    private void sendCardUid(byte[] datas) {
        if (datas[3] > 0) {
            int uidLength2 = datas[8];
            byte[] uid2 = new byte[uidLength2];
            System.arraycopy(datas, 9, uid2, 0, uidLength2);
            sendUidToUi(uid2);
        }
    }

    private void sendUidToUi(byte[] uid) {
        if (uid != null) {
            mCardUid = uid;
            if (mRfidListener != null) {
                mRfidListener.detectCard(uid);
            }
        }
    }

    private void deviceCtlCmd(byte[] datas) {
        byte cmd = nowCmdCode;
        switch (cmd) {
            case RfidCtlCmdCode.GET_DVC_INFO:
                if (datas != null) {
                    Log.d("recv data", UsbCtl.bytesToHexString(datas));
                    byte[] infoBytes = new byte[(datas[3] & 0xff)];
                    System.arraycopy(datas, 4, infoBytes, 0, infoBytes.length);
                    String version = new String(infoBytes);
                    if (version != null && !version.isEmpty()) {
                        setConnectDevice(true);
                        Log.d("version", version);
                    }
                }
                break;
            default:
                break;
        }
    }

    public byte[] sendCmd(byte cmdClass, byte cmdCode, byte[] info) {
        int infoLength = 0;
        if (info != null && info.length > 0) {
            infoLength = info.length;
        }
        byte[] datas = new byte[infoLength + 6];
        datas[0] = (byte) datas.length;
        datas[1] = (byte) cmdClass;
        datas[2] = (byte) cmdCode;
        datas[3] = (byte) infoLength;
        if (infoLength > 0) {
            for (int index = 0; index < infoLength; index++) {
                datas[index + 4] = info[index];
            }
        }
        byte BCC = 0x00;
        for (int i = 0; i < (datas[0] - 2); i++) {
            BCC ^= datas[i];
        }
        datas[datas[0] - 2] = (byte) ~BCC;
        datas[datas[0] - 1] = (byte) 0x03;
        nowCmdCode = cmdCode;
        mUsbCtl.sendData(datas.length, datas);
        mUsbCtl.setReadStatus(1);
        Log.d("send data", UsbCtl.bytesToHexString(datas));
        return datas;
    }

    public byte[] getCardUid() {
        return mCardUid;
    }

    private void setCardUid(byte[] cardUid) {
        if (cardUid != null) {
            mCardUid = cardUid;
        }
    }

    public RfidListener getRfidListener() {
        return mRfidListener;
    }

    public void setRfidListener(RfidListener rfidListener) {
        this.mRfidListener = rfidListener;
    }

    public boolean isConnectDevice() {
        return isConnectDevice;
    }

    public void setConnectDevice(boolean isConnectDevice) {
        this.isConnectDevice = isConnectDevice;
    }
}
