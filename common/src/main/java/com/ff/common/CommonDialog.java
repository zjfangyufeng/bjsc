package com.ff.common;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.ff.common_tools.R;

/**
 * Created by fangyufeng on 2015/12/18.
 */
public class CommonDialog extends AlertDialog {

    View view;
    TextView tv_title,tv_sub_title, btn_cancel, btn_confirm;

    public CommonDialog(Context context){
        super(context);
        view = View.inflate(context, R.layout.common_dialog, null);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_sub_title = (TextView) view.findViewById(R.id.tv_sub_title);
        btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
        btn_cancel.setVisibility(View.GONE);
        btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public CommonDialog setTitle(String t){
        tv_title.setText(t);
        return this;
    }

    public CommonDialog setSubTitle(String t){
        tv_sub_title.setText(t);
        return this;
    }

    public CommonDialog setCancelBtn(String text, View.OnClickListener onCancel){
        if(!ToolUtils.isNull(text))
            btn_cancel.setText(text);
        if(onCancel!=null){
            btn_cancel.setOnClickListener(onCancel);
        }
        showCancelBtn();
        return this;
    }

    public CommonDialog setConfirmBtn(String text, View.OnClickListener onConfirm){
        if(!ToolUtils.isNull(text))
            btn_confirm.setText(text);
        if(onConfirm!=null){
            btn_confirm.setOnClickListener(onConfirm);
        }
        showConfirmBtn();
        return this;
    }

    public CommonDialog showCancelBtn(){
        btn_cancel.setVisibility(View.VISIBLE);
        return this;
    }

    public CommonDialog showConfirmBtn(){
        btn_confirm.setVisibility(View.VISIBLE);
        return this;
    }

    @Override
    public void show() {
        super.show();
        setContentView(view);
    }
}
