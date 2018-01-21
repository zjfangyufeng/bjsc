package com.ff.common.red_package;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.ff.common.DisplayMetricsTool;
import com.ff.common.ToolUtils;
import com.ff.common.application.ApplicationProxy;
import com.ff.common_tools.R;

/**
 * Created by Ace on 2017/12/22.
 */

public class RedPackageDialog extends AlertDialog{

    String title;
    View.OnClickListener onClickListener;

    public RedPackageDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();
        View view = View.inflate(ApplicationProxy.getInstance().getContext(), R.layout.dialog_daily_sign_red_package,null);
        View iv_red_package = view.findViewById(R.id.iv_red_package);
        ObjectAnimator red_package_animX = ObjectAnimator.ofFloat(iv_red_package, "ScaleX", 0, 1);
        red_package_animX.setDuration(800);
        ObjectAnimator red_package_animY = ObjectAnimator.ofFloat(iv_red_package, "ScaleY", 0, 1);
        red_package_animY.setDuration(800);

        final View iv_top = view.findViewById(R.id.iv_top);
        iv_top.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        ObjectAnimator iv_top_anim = ObjectAnimator.ofFloat(iv_top, "translationY", iv_top.getMeasuredHeight(), 0);
        iv_top_anim.setDuration(800);
        iv_top_anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                iv_top.setVisibility(View.VISIBLE);
            }
        });

        TextView tv = (TextView) view.findViewById(R.id.tv);
        if(!ToolUtils.isNull(title))
            tv.setText(title);
        ObjectAnimator alpha_anim = ObjectAnimator.ofFloat(tv, "Alpha", 0f, 1f);
        alpha_anim.setDuration(800);

        final View iv_halo = view.findViewById(R.id.iv_halo);
        ObjectAnimator halo_anim = ObjectAnimator.ofFloat(iv_halo, "Rotation", 0, 360);
        halo_anim.setInterpolator(new LinearInterpolator());
        halo_anim.setRepeatCount(ValueAnimator.INFINITE);
        halo_anim.setDuration(5000);
        halo_anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                iv_halo.setVisibility(View.VISIBLE);
            }
            public void onAnimationEnd(Animator animation) {
            }
            public void onAnimationCancel(Animator animation) {
            }
            public void onAnimationRepeat(Animator animation) {
            }
        });

        final View btn_open = view.findViewById(R.id.btn_open);
        ObjectAnimator btn_anim = ObjectAnimator.ofFloat(btn_open,"translationY",0, DisplayMetricsTool.getInstance().getDPsize(2),0,-DisplayMetricsTool.getInstance().getDPsize(2),0);
        btn_anim.setDuration(200);
        btn_anim.setInterpolator(new LinearInterpolator());
        btn_anim.setRepeatCount(ValueAnimator.INFINITE);
        btn_anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                btn_open.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        btn_open.setOnClickListener(onClickListener);
        setContentView(view);

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(red_package_animX).with(red_package_animY).before(iv_top_anim).before(alpha_anim);
        animatorSet.play(iv_top_anim).before(halo_anim).before(btn_anim);
        animatorSet.start();

        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                animatorSet.cancel();
            }
        });
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
