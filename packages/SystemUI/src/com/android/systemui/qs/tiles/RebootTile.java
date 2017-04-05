/*
 * Copyright (C) 2013 Slimroms
 * Copyright (C) 2017 Xperia Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.qs.tiles;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.ServiceManager;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import com.android.systemui.R;
import com.android.systemui.qs.QSTile;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.internal.util.benzo.Helpers;

import android.util.Log;

public class RebootTile extends QSTile<QSTile.BooleanState> {

    //1 Normal Reboot
    //2 Reboot to Recovery
    //3 Soft Reboot
    //4 Reboot SystemUI
    private int mChoiceNumber = 1;

    public RebootTile(Host host) {
        super(host);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.XOSP;
    }

    @Override
    public BooleanState newTileState() {
        return new BooleanState();
    }

    @Override
    protected void handleClick() {
        if(mChoiceNumber == 4){
            mChoiceNumber = 1;
            refreshState();
        }
        else{
            mChoiceNumber++;
            refreshState();
        }
    }

    private static void doSystemUIReboot() {
        Helpers.restartSystemUI();
    }

    private void doSoftReboot() {
        try {
            final IActivityManager am = ActivityManagerNative.asInterface(ServiceManager.checkService("activity"));
            
            if (am != null)
                am.restart();

        } catch (RemoteException e) {
            //Don't need anything from here
        }
    }

    @Override
    protected void handleLongClick() {
        Handler handler = new Handler();
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        String mReason;
        mHost.collapsePanels();
        
        switch(mChoiceNumber){
            case 1:
                mReason="REBOOT";
                handler.postDelayed(new Runnable() {
                    public void run(){
                        pm.reboot(mReason);
                    }
                }, 500);
            break;
            case 2:
                mReason="recovery";
                handler.postDelayed(new Runnable() {
                    public void run(){
                        pm.reboot(mReason);
                    }
                    }, 500);
            break;
            case 3:
                handler.postDelayed(new Runnable() {
                    public void run(){
                        doSoftReboot();
                    }
                }, 500);
            break;
            case 4:
                handler.postDelayed(new Runnable() {
                    public void run(){
                        doSystemUIReboot();
                    }
                }, 500);
        }
    }

    @Override
    public Intent getLongClickIntent() {
        return null;
    }

    @Override
    public CharSequence getTileLabel() {
        return mContext.getString(R.string.quick_settings_reboot_label);
    }

    @Override
    protected void handleUpdateState(BooleanState state, Object arg) {
       
        switch(mChoiceNumber){
            case 1:
                state.label = mContext.getString(R.string.quick_settings_reboot_label);
                state.icon = ResourceIcon.get(R.drawable.ic_qs_reboot);
                state.contentDescription =  mContext.getString(R.string.quick_settings_reboot_label);
            break;
            case 2:
                state.label = mContext.getString(R.string.quick_settings_reboot_recovery_label);
                state.icon = ResourceIcon.get(R.drawable.ic_qs_reboot_recovery);
                state.contentDescription =  mContext.getString(R.string.quick_settings_reboot_recovery_label);
            break;
            case 3:
                state.label = mContext.getString(R.string.quick_settings_soft_reboot_label);
                state.icon = ResourceIcon.get(R.drawable.ic_qs_reboot);
                state.contentDescription =  mContext.getString(R.string.quick_settings_soft_reboot_label);
            break;
            case 4:
                state.label = mContext.getString(R.string.quick_settings_systemui_reboot);
                state.icon = ResourceIcon.get(R.drawable.ic_qs_reboot_systemui);
                state.contentDescription =  mContext.getString(R.string.quick_settings_systemui_reboot);
            break;
        }
    }

    @Override
    public void setListening(boolean listening) {
    }

}
