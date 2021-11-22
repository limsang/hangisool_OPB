package com.hangisool.lcd_a_h.util;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Util {

  public static String getIpAdress(){
    try {
      for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
        NetworkInterface intf = en.nextElement();
        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
          InetAddress inetAddress = enumIpAddr.nextElement();
          if (!inetAddress.isLoopbackAddress()) {
            if(inetAddress instanceof Inet4Address) {
              return inetAddress.getHostAddress().toString();
            }
          }
        }
      }
    } catch (SocketException ex) {
      Log.e("ERROR", ex.toString());
    }
    return null;
  }

  public static boolean isActivityAvailable(Activity activity) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
      return !activity.isFinishing() && !activity.isDestroyed();
    } else {
      return !activity.isFinishing();
    }
  }
}
