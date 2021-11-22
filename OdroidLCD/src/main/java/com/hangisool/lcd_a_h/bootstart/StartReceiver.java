package com.hangisool.lcd_a_h.bootstart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hangisool.lcd_a_h.LcdActivity;
import com.hangisool.lcd_a_h.service.LcdService;

import java.io.IOException;

public class StartReceiver extends BroadcastReceiver {

    public StartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("intent.getAction()",intent.getAction());
        String action = intent.getAction();
        if(action.equals("android.intent.action.BOOT_COMPLETED")){
            Log.d("reboot", "true");
            try {
                Thread.sleep(20000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent i = new Intent(context, LcdActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        if(action.equals("ACTION_REBOOT_BROAD_CAST")){
            try{
                Log.d("!!!REBOOT START!!!", "!!!!!!!!!!");
                Process sh = Runtime.getRuntime().exec("su", null,null);
                Runtime.getRuntime().exec("su -c reboot");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(action.equals("android.hardware.usb.action.USB_DEVICE_ATTACHED")){
            LcdActivity.finishFlag = true;
            //System.exit(0);
        }

        if(action.equals("ACTION_RESTART_PERSISTENTSERVICE")){
            Log.e("StartReceiver","ACTION_RESTART_PERSISTENTSERVICE");
            Intent i = new Intent(context,LcdService.class);
            context.startService(i);
        }
    }
}
