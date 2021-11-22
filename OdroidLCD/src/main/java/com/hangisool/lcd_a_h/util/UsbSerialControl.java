package com.hangisool.lcd_a_h.util;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

public class UsbSerialControl {
    private UsbDevice usbDevice = null;
    private UsbManager usbManager;

    public UsbSerialControl(UsbDevice usbDevice, UsbManager usbManager){
        this.usbDevice = usbDevice;
        this.usbManager = usbManager;
    }

    public boolean setSerial(int baudrate, int dataBit,int parity, int stopBit, int flowControl){
        int dataBaudrate = 0;
        int dataOption = 0;
        UsbDeviceConnection conn = usbManager.openDevice(usbDevice);
        conn.controlTransfer(0x40, 0, 0, 0, null, 0, 0);// reset
        // mConnection.controlTransfer(0×40,
        // 0, 1, 0, null, 0,
        // 0);//clear Rx
        conn.controlTransfer(0x40, 0, 2, 0, null, 0, 0);// clear Tx
        conn.controlTransfer(0x40, 0x02, 0x0000, 0, null, 0, 0);// flow
        // control
        // control
        // none
        /*
        Value Baud Rate speed
        * 0×2710 300
        * 0×1388 600
        * 0x09C4 1200
        * 0x04E2 2400
        * 0×0271 4800
        * 0×4138 9600
        * 0x80D0 14400
        * 0x809C 19200
        * 0xC04E 38400
        * 0×0034 57600
        * 0x001A 115200
        * 0x000D 230400
        * 0×4006 460800
        * 0×8003 921600
        */
        switch(baudrate){
            case 300:
                dataBaudrate = 0x2710;
                break;
            case 600:
                dataBaudrate = 0x1388;
                break;
            case 1200:
                dataBaudrate = 0x09C4;
                break;
            case 2400:
                dataBaudrate = 0x04E2;
                break;
            case 4800:
                dataBaudrate = 0x0271;
                break;
            case 9600:
                dataBaudrate = 0x4138;
                break;
            case 14400:
                dataBaudrate = 0x80D0;
                break;
            case 19200:
                dataBaudrate = 0x809C;
                break;
            case 38400:
                dataBaudrate = 0xC04E;
                break;
            case 57600:
                dataBaudrate = 0x0034;
                break;
            case 115200:
                dataBaudrate = 0x001A;
                break;
            case 230400:
                dataBaudrate = 0x000D;
                break;
            case 460800:
                dataBaudrate = 0x4006;
                break;
            case 921600:
                dataBaudrate = 0x8003;
                break;
            default:
                return false;
        }
        conn.controlTransfer(0x40, 0x03, dataBaudrate, 0, null, 0, 0);// baudrate //
        /*
        Bits 0 to 7   -- Number of data bits
        Bits 8 to 10  -- Parity
                  0 = None
                  1 = Odd
                  2 = Even
                  3 = Mark
                  4 = Space
        Bits 11 to 13 -- Stop Bits
                  0 = 1
                  1 = 1.5
                  2 = 2
        Bit 14
                  1 = TX ON (break)
                  0 = TX OFF (normal state)
        Bit15 -- Reserved
        */
        dataOption |= dataBit;
        dataOption |= parity<<8;
        dataOption |= stopBit<<11;
        dataOption |= flowControl<<14;
        conn.controlTransfer(0x40, 0x04, dataOption, 0, null, 0, 0);// data bit
        return true;
    }
}
