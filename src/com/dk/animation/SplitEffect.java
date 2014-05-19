package com.dk.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

public class SplitEffect extends BaseEffect {

    public Bitmap mBitmap = null;
    private int[] mLoc1;
    private int[] mLoc2;
    private ImageView mTopImage;
    private ImageView mBottomImage;
    private AnimatorSet mSetAnim;

    public void prepareAnimation(final Activity destActivity) {
        mTopImage = createImageView(destActivity, mBitmap, mLoc1);
        mBottomImage = createImageView(destActivity, mBitmap, mLoc2);
    }

    public void prepare(Activity currActivity) {
        int splitYCoord = -1;
        // Get the content of the activity and put in a bitmap
        View root = currActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        root.setDrawingCacheEnabled(true);
        mBitmap = root.getDrawingCache();

        // If the split Y coordinate is -1 - We'll split the activity equally
        splitYCoord = (splitYCoord != -1 ? splitYCoord : mBitmap.getHeight() / 2);

        if (splitYCoord > mBitmap.getHeight())
            throw new IllegalArgumentException("Split Y coordinate [" + splitYCoord + "] exceeds the activity's height ["
                    + mBitmap.getHeight() + "]");

        // Set the location to put the 2 bitmaps on the destination activity
        mLoc1 = new int[] { 0, splitYCoord, root.getTop() };
        mLoc2 = new int[] { splitYCoord, mBitmap.getHeight(), root.getTop() };
    }

    public void cancel() {
        if (mSetAnim != null)
            mSetAnim.cancel();
    }

    public void clean(Activity activity) {
        if (mTopImage != null) {
            mTopImage.setLayerType(View.LAYER_TYPE_NONE, null);
            try {
                // If we use the regular removeView() we'll get a small UI glitch
                activity.getWindowManager().removeViewImmediate(mBottomImage);
            } catch (Exception ignored) {
            }
        }
        if (mBottomImage != null) {
            mBottomImage.setLayerType(View.LAYER_TYPE_NONE, null);
            try {
                activity.getWindowManager().removeViewImmediate(mTopImage);
            } catch (Exception ignored) {
            }
        }

        mBitmap = null;
    }

    private ImageView createImageView(Activity destActivity, Bitmap bmp, int loc[]) {
        MyImageView imageView = new MyImageView(destActivity);
        imageView.setImageBitmap(bmp);
        imageView.setImageOffsets(bmp.getWidth(), loc[0], loc[1]);

        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP;
        windowParams.x = 0;
        windowParams.y = loc[2] + loc[0];
        windowParams.height = loc[1] - loc[0];
        windowParams.width = bmp.getWidth();
        windowParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
        destActivity.getWindowManager().addView(imageView, windowParams);

        return imageView;
    }

    private class MyImageView extends ImageView {
        private Rect mSrcRect;
        private Rect mDstRect;
        private Paint mPaint;

        public MyImageView(Context context) {
            super(context);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }

        public void setImageOffsets(int width, int startY, int endY) {
            mSrcRect = new Rect(0, startY, width, endY);
            mDstRect = new Rect(0, 0, width, endY - startY);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Bitmap bm = null;
            Drawable drawable = getDrawable();
            if (null != drawable && drawable instanceof BitmapDrawable) {
                bm = ((BitmapDrawable)drawable).getBitmap();
            }

            if (null == bm) {
                super.onDraw(canvas);
            } else {
                canvas.drawBitmap(bm, mSrcRect, mDstRect, mPaint);
            }
        }
    }

    public void animate(final Activity destActivity, final int duration) {
        final Interpolator interpolator = new DecelerateInterpolator();
        destActivity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        
        
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                mSetAnim = new AnimatorSet();
                mTopImage.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                mBottomImage.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                mSetAnim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        clean(destActivity);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        clean(destActivity);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                Animator anim1 = ObjectAnimator.ofFloat(mTopImage, "translationY", mTopImage.getHeight() * -1);
                Animator anim2 = ObjectAnimator.ofFloat(mBottomImage, "translationY", mBottomImage.getHeight());

                if (interpolator != null) {
                    anim1.setInterpolator(interpolator);
                    anim2.setInterpolator(interpolator);
                }

                mSetAnim.setDuration(duration);
                mSetAnim.playTogether(anim1, anim2);
                mSetAnim.start();

            }
        });
    }
}
