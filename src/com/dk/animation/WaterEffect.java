package com.dk.animation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.dk.animation.water.DrawWaterWaveWithSina;

public class WaterEffect extends BaseEffect {
    private Bitmap mBitmap = null;
    private DrawWaterWaveWithSina mTopImage;
    private int top;

    public void prepareAnimation(final Activity destActivity) {
        mTopImage = createImageView(destActivity, mBitmap);
    }

    public void prepare(Activity currActivity) {
        View root = currActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        top = root.getTop();
        root.setDrawingCacheEnabled(true);
        mBitmap = root.getDrawingCache();
    }

    public void cancel() {
    }

    public void clean(Activity activity) {
        if (mTopImage != null) {
            mTopImage.setLayerType(View.LAYER_TYPE_NONE, null);
            try {
                // If we use the regular removeView() we'll get a small UI glitch
                activity.getWindowManager().removeViewImmediate(mTopImage);
            } catch (Exception ignored) {
            }
        }
        mBitmap = null;
    }

    private DrawWaterWaveWithSina createImageView(Activity destActivity, Bitmap bmp) {
        DrawWaterWaveWithSina imageView = new DrawWaterWaveWithSina(destActivity, bmp);
        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.TOP;
        windowParams.x = 0;
        windowParams.y = top;
        windowParams.height = bmp.getHeight();
        windowParams.width = bmp.getWidth();
        windowParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
        destActivity.getWindowManager().addView(imageView, windowParams);

        return imageView;
    }

    @SuppressLint("HandlerLeak")
    public void animate(final Activity destActivity, final int duration) {

        new Handler().post(new Runnable() {

            @Override
            public void run() {

                final Handler callBack = new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 101:
                                mTopImage.start(mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
                                break;
                            case 102:
                                clean(destActivity);
                                break;
                        }
                    }
                };

                Thread animationThread = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        callBack.sendEmptyMessage(101);
                        //
                        // callBack.sendEmptyMessage(102);
                    }
                });

                animationThread.start();

            }
        });
    }

}
