package com.tapc.platform.usb;

import android.app.Activity;
import android.os.SystemClock;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.FT_Device;
import com.tapc.android.uart.Commands;
import com.tapc.android.uart.CommunicationPacket;

import java.util.Observable;
import java.util.Observer;

public class UsbCtl implements Observer {
    private static UsbCtl mUsbCtl;
    private static D2xxManager ftD2xx = null;
    private Activity mActivity;
    private final String TT = "Trace";
    private FT_Device ftDev;
    private boolean uart_configured = false;
    private final byte XON = 0x11; /* Resume transmission */
    private final byte XOFF = 0x13; /* Pause transmission */
    private UsbRecvListerner mUsbRecvListerner;
    private int readStatus;
    private ReadThread mReadThread;

    enum DeviceStatus {
        DEV_NOT_CONNECT, DEV_NOT_CONFIG, DEV_CONFIG
    }

    public UsbCtl(Activity activity) {
        mUsbCtl = this;
        mActivity = activity;
        try {
            ftD2xx = D2xxManager.getInstance(mActivity);
        } catch (D2xxException e) {
            e.printStackTrace();
        }
    }

    public static UsbCtl getInstance() {
        return mUsbCtl;
    }

    private void closeFtDev() {
        if (ftDev != null) {
            ftDev.close();
            ftDev = null;
        }
    }

    public boolean init() {
        int tempDevCount = ftD2xx.createDeviceInfoList(mActivity);
        closeFtDev();
        if (tempDevCount > 0) {
            if (ftDev == null) {
                ftDev = ftD2xx.openByIndex(mActivity, 0);
                if (ftDev != null) {
                    setConfig(19200, (byte) 8, (byte) 1, (byte) 0, (byte) 0);
                    if (mReadThread != null) {
                        mReadThread.interrupt();
                        mReadThread = null;
                    }
                    mReadThread = new ReadThread();
                    mReadThread.start();
                    return true;
                }
            }
        }
        DLog.d("####usb####", "usb not connect");
        return false;
    }

    public boolean sendCommand(Commands commands, byte[] data) {
        CommunicationPacket com = new CommunicationPacket(commands);
        com.setData(data);
        if (DeviceStatus.DEV_CONFIG != checkDevice()) {
            return false;
        }
        byte[] sendDate = com.getPacketByteArray();
        sendData(sendDate.length, sendDate);
        return true;
    }

    public void close() {
        if (ftDev != null && ftDev.isOpen()) {
            ftDev.close();
        }
    }

    public void open() {
        if (ftDev != null) {
            if (!ftDev.isOpen()) {
                ftDev = ftD2xx.openByIndex(mActivity, 0);
                setConfig(19200, (byte) 8, (byte) 1, (byte) 0, (byte) 0);
                ReadThread readThread = new ReadThread();
                readThread.start();
            }
        }
    }

    public boolean isUsbConnect() {
        if (ftDev != null && ftDev.isOpen()) {
            return true;
        }
        return false;
    }

    private DeviceStatus checkDevice() {
        if (ftDev == null || false == ftDev.isOpen()) {
            return DeviceStatus.DEV_NOT_CONNECT;
        } else if (false == uart_configured) {
            return DeviceStatus.DEV_NOT_CONFIG;
        }

        return DeviceStatus.DEV_CONFIG;
    }

    void setConfig(int baud, byte dataBits, byte stopBits, byte parity, byte flowControl) {
        ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

        ftDev.setBaudRate(baud);

        switch (dataBits) {
            case 7:
                dataBits = D2xxManager.FT_DATA_BITS_7;
                break;
            case 8:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
            default:
                dataBits = D2xxManager.FT_DATA_BITS_8;
                break;
        }

        switch (stopBits) {
            case 1:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
            case 2:
                stopBits = D2xxManager.FT_STOP_BITS_2;
                break;
            default:
                stopBits = D2xxManager.FT_STOP_BITS_1;
                break;
        }

        switch (parity) {
            case 0:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
            case 1:
                parity = D2xxManager.FT_PARITY_ODD;
                break;
            case 2:
                parity = D2xxManager.FT_PARITY_EVEN;
                break;
            case 3:
                parity = D2xxManager.FT_PARITY_MARK;
                break;
            case 4:
                parity = D2xxManager.FT_PARITY_SPACE;
                break;
            default:
                parity = D2xxManager.FT_PARITY_NONE;
                break;
        }

        ftDev.setDataCharacteristics(dataBits, stopBits, parity);

        short flowCtrlSetting;
        switch (flowControl) {
            case 0:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
            case 1:
                flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
                break;
            case 2:
                flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
                break;
            case 3:
                flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
                break;
            default:
                flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
                break;
        }

        ftDev.setFlowControl(flowCtrlSetting, XON, XOFF);
        uart_configured = true;
    }

    public void sendData(int numBytes, byte[] buffer) {
        if (ftDev == null || ftDev.isOpen() == false) {
            DLog.e(TT, "SendData: device not open");
            return;
        }
        if (numBytes > 0) {
            ftDev.write(buffer, numBytes);
        }
        // notifyRecvData();
    }

    public void sendData(byte buffer) {
        if (ftDev == null || ftDev.isOpen() == false) {
            DLog.e(TT, "SendData: device not open");
            return;
        }
        byte tmpBuf[] = new byte[1];
        tmpBuf[0] = buffer;
        ftDev.write(tmpBuf, 1);
        // notifyRecvData();
    }

    private void notifyRecvData() {
        if (mReadThread != null) {
            mReadThread.notifyRecvData();
        }
    }

    // call this API to show message
    void midToast(String str, int showTime) {
    }

    class ReadThread extends Thread {

        ReadThread() {
            this.setPriority(MAX_PRIORITY);
        }

        public void notifyRecvData() {
            synchronized (this) {
                notifyAll();
            }
        }

        public void run() {
            while (isUsbConnect()) {
                int readcount = ftDev.getQueueStatus();
                if (readcount > 0) {
                    byte[] startByte = new byte[1];
                    ftDev.read(startByte, startByte.length);
                    int dataLength = startByte[0] & 0xff;
                    if (dataLength <= 1) {
                        continue;
                    }
                    if (readcount == 1 && dataLength == 0x06) {
                        DLog.e(TT, "set baud rate success");
                        continue;
                    }
                    byte[] pkgData = new byte[dataLength - 1];
                    int retCount = ftDev.read(pkgData, dataLength - 1, 30);

                    if (retCount == (dataLength - 1) && retCount > 0) {
                        byte[] usbdata = new byte[dataLength];
                        usbdata[0] = startByte[0];
                        System.arraycopy(pkgData, 0, usbdata, 1, retCount);

                        byte BCC = 0x00;
                        for (int i = 0; i < (usbdata[0] - 2); i++) {
                            BCC ^= usbdata[i];
                        }
                        BCC = (byte) ~BCC;
                        DLog.e(TT, "recv buf:" + bytesToHexString(usbdata));
                        if (BCC == usbdata[usbdata[0] - 2] && usbdata[usbdata[0] - 1] == 0x03) {
                            if (mUsbRecvListerner != null) {
                                mUsbRecvListerner.update(usbdata);
                            }
                        }
                    } else {
                        DLog.e(TT, "recv buf failed :" + bytesToHexString(pkgData));
                    }
                }
                SystemClock.sleep(10);
            }
            DLog.e(TT, "usb ReadThread close");
        }
    }

    @Override
    public void update(Observable arg0, Object data) {
        if (mUsbRecvListerner != null) {
            mUsbRecvListerner.update(data);
        }
    }

    public void setUsbRecvListerner(UsbRecvListerner listerner) {
        if (listerner != null) {
            mUsbRecvListerner = listerner;
        }
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }
}
