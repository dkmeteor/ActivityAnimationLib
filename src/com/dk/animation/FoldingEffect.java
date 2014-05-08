package com.dk.animation;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.dk.animation.folding.FoldingLayout;

public class FoldingEffect extends BaseEffect {

    public Bitmap mBitmap = null;
    private ImageView mTopImage;
    private FoldingLayout mLayout;
    private int top;
    private float t = 0;

    public void prepareAnimation(final Activity destActivity) {
        mTopImage = createImageView(destActivity, mBitmap);
    }

    public void prepare(Activity currActivity) {
        // Get the content of the activity and put in a bitmap
        View root = currActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        root.setDrawingCacheEnabled(true);
        mBitmap = root.getDrawingCache();
        top = root.getTop();

    }

    public void cancel() {
    }

    public void clean(Activity activity) {

        if (mLayout != null) {
            mLayout.setLayerType(View.LAYER_TYPE_NONE, null);
            try {
                activity.getWindowManager().removeViewImmediate(mLayout);
            } catch (Exception ignored) {
            }
        }

        mBitmap = null;
    }

    private ImageView createImageView(Activity destActivity, Bitmap bmp) {
        mLayout = new FoldingLayout(destActivity);
        ImageView imageView = new ImageView(destActivity);

        mLayout.addView(imageView);
        imageView.setImageBitmap(bmp);

        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP;
        windowParams.x = 0;
        windowParams.y = top;
        windowParams.height = bmp.getHeight();
        windowParams.width = bmp.getWidth();
        windowParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
        destActivity.getWindowManager().addView(mLayout, windowParams);

        return imageView;
    }

    public void animate(final Activity destActivity, final int duration) {

        new Handler().post(new Runnable() {

            @Override
            public void run() {
                mLayout.setNumberOfFolds(8);
                float foldFactor = mLayout.getFoldFactor();

                ObjectAnimator animator = ObjectAnimator.ofFloat(mLayout, "foldFactor", foldFactor, 1);
                animator.setRepeatMode(ValueAnimator.RESTART);
                animator.setRepeatCount(0);
                animator.setDuration(500);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.start();

                animator.addListener(new AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        clean(destActivity);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        // TODO Auto-generated method stub

                    }
                });
            }
        });
    }
}
