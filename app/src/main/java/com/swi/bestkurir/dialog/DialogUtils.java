/*
 * ============================================================================
 * COPYRIGHT
 *              Pax CORPORATION PROPRIETARY INFORMATION
 *   This software is supplied under the terms of a license agreement or
 *   nondisclosure agreement with Pax Corporation and may not be copied
 *   or disclosed except in accordance with the terms in that agreement.
 *      Copyright (C) 2016 - ? Pax Corporation. All rights reserved.
 * Module Date: 2016-12-1
 * Module Author: Steven.W
 * Description:
 *
 * ============================================================================
 */
package com.swi.bestkurir.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.KeyEvent;

import com.swi.bestkurir.dialog.CustomAlertDialog.OnCustomClickListener;

public class DialogUtils {

    private DialogUtils() {
        //do nothing
    }

    /**
     * 提示错误信息
     *
     * @param msg
     * @param listener
     * @param timeout
     */
    public static void showErrMessage(final Context context, final String title, final String msg,
                                      final OnDismissListener listener, final int timeout) {
        if (context == null) {
            return;
        }
        CustomAlertDialog dialog = new CustomAlertDialog(context, CustomAlertDialog.ERROR_TYPE, true, timeout);
        dialog.setTitleText(title);
        dialog.setContentText(msg);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        dialog.setOnDismissListener(listener);
//        Device.beepErr();
    }

    public static void showPrintConfirmDialog(final Context context, final OnCustomClickListener listener) {
        final CustomAlertDialog dialog = new CustomAlertDialog(context, CustomAlertDialog.NORMAL_TYPE);
        String title = "Print";
        dialog.setTitleText(title);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setConfirmClickListener(listener);
        dialog.setContentText("Print ?");
        dialog.showCancelButton(false);
        dialog.showConfirmButton(true);
        dialog.show();
    }

    public static CustomAlertDialog showProcessingMessage(final Context context, final String title, final int timeout){
        if (context == null) {
            return null;
        }

        final CustomAlertDialog dialog = new CustomAlertDialog(context, CustomAlertDialog.PROGRESS_TYPE);
        dialog.showContentText(false);
        dialog.setTitleText(title);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTimeout(timeout);
        dialog.show();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        return dialog;

    }
    /**
     * 单行提示成功信息
     *
     * @param title
     * @param listener
     * @param timeout
     */
    public static void showSuccMessage(final Context context, final String title,
                                       final OnDismissListener listener, final int timeout) {
        if (context == null) {
            return;
        }
        CustomAlertDialog dialog = new CustomAlertDialog(context, CustomAlertDialog.SUCCESS_TYPE, true, timeout);
        dialog.showContentText(false);
        dialog.setTitleText("Success");
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        dialog.setOnDismissListener(listener);
//        Device.beepOk();
    }
    public static void showSuccActiveMessage(final Context context, final String title,
                                             final OnDismissListener listener, final int timeout) {
        if (context == null) {
            return;
        }
        CustomAlertDialog dialog = new CustomAlertDialog(context, CustomAlertDialog.SUCCESS_TYPE, true, timeout);
        dialog.showContentText(false);
        dialog.setTitleText("Success Activation");
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });
        dialog.setOnDismissListener(listener);
//        Device.beepOk();
    }

    /**
     * 退出当前应用
     */
    public static void showExitAppDialog(final Context context) {
        showConfirmDialog(context, "Exit Current APP", null, new OnCustomClickListener() {
            @Override
            public void onClick(CustomAlertDialog alertDialog) {
                alertDialog.dismiss();
                /*Intent intent = new Intent(context, PaymentActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                context.startActivity(intent);*/
                System.exit(0);
            }
        });
    }

    public static void showConfirmDialog(final Context context, final String content,
                                         final OnCustomClickListener cancelClickListener, final OnCustomClickListener confirmClickListener) {
        final CustomAlertDialog dialog = new CustomAlertDialog(context, CustomAlertDialog.NORMAL_TYPE);

        final OnCustomClickListener clickListener = new OnCustomClickListener() {
            @Override
            public void onClick(CustomAlertDialog alertDialog) {
                alertDialog.dismiss();
            }
        };
        dialog.setCancelText("NO");
        dialog.setConfirmText("YES");
        dialog.setCancelClickListener(cancelClickListener == null ? clickListener : cancelClickListener);
        dialog.setConfirmClickListener(confirmClickListener == null ? clickListener : confirmClickListener);
        dialog.show();
        dialog.setNormalText(content);
        dialog.showCancelButton(true);
        dialog.showConfirmButton(true);
    }
    public static void showConfirmDialog2(final Context context, final String content,
                                          final OnCustomClickListener cancelClickListener, final OnCustomClickListener confirmClickListener) {
        final CustomAlertDialog dialog = new CustomAlertDialog(context, CustomAlertDialog.NORMAL_TYPE);

        final OnCustomClickListener clickListener = new OnCustomClickListener() {
            @Override
            public void onClick(CustomAlertDialog alertDialog) {
                alertDialog.dismiss();
            }
        };
//        dialog.setCancelText("NO");
        dialog.setConfirmText("YES");
        dialog.setCancelClickListener(cancelClickListener == null ? clickListener : cancelClickListener);
        dialog.setConfirmClickListener(confirmClickListener == null ? clickListener : confirmClickListener);
        dialog.show();
        dialog.setNormalText(content);
//        dialog.showCancelButton(true);
        dialog.showConfirmButton(true);
    }

    /**
     * 应用更新或者参数更新提示，点击确定则进行直接结算
     */
    public static void showUpdateDialog(final Context context, final String prompt) {

        final CustomAlertDialog dialog = new CustomAlertDialog(context, CustomAlertDialog.NORMAL_TYPE);
        dialog.setCancelClickListener(new OnCustomClickListener() {
            @Override
            public void onClick(CustomAlertDialog alertDialog) {
                dialog.dismiss();
            }
        });
        dialog.setConfirmClickListener(new OnCustomClickListener() {
            @Override
            public void onClick(CustomAlertDialog alertDialog) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setNormalText(prompt);
        dialog.showCancelButton(true);
        dialog.showConfirmButton(true);
    }
}
