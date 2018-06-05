package com.tapc.platform.witget;

import com.tapc.platform.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;



public class ProgressDialog extends Dialog {

    private Context context;
    private TextView tv_message;
    private String message;

    public ProgressDialog(Context context) {
        super(context, R.style.ProgressDialogStyle);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_dialog_progress);

        setCanceledOnTouchOutside(false);// Touch Other Dismiss

        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_message.setText(message);

        // Set Dialog Gravity
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);

        // Set Dialog Width
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        // p.width = WindowManager.LayoutParams.MATCH_PARENT;

        dialogWindow.setAttributes(p);
    }

    public ProgressDialog setMessage(String message) {
        this.message = message;
        return this;
    }


}
