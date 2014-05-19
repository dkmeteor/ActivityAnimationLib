package com.dk.animation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class SkewEffect extends BaseEffect {
    public Bitmap mBitmap = null;
    private ImageView mTopImage;
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

        if (mTopImage != null) {
            mTopImage.setLayerType(View.LAYER_TYPE_NONE, null);
            try {
                activity.getWindowManager().removeViewImmediate(mTopImage);
            } catch (Exception ignored) {
            }
        }

        mBitmap = null;
    }

    private ImageView createImageView(Activity destActivity, Bitmap bmp) {
        MyImageView imageView = new MyImageView(destActivity);
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
        destActivity.getWindowManager().addView(imageView, windowParams);

        return imageView;
    }

    public void animate(final Activity destActivity, final int duration) {

        new Handler().post(new Runnable() {

            @Override
            public void run() {

                final Handler callBack = new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 101:
                                mTopImage.invalidate();
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
                        t = 0;
                        while (t < 1) {
                            t += 0.08;
                            callBack.sendEmptyMessage(101);

                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        callBack.sendEmptyMessage(102);

                    }
                });

                animationThread.start();

            }
        });
    }

    private class MyImageView extends ImageView {
        private Matrix mMatrix;
        private Camera mCamera = new Camera();
        private Paint mPaint = new Paint();

        public MyImageView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (mMatrix != null)
                mMatrix.reset();
            mMatrix = SkewEffect.this.getMatrix(canvas.getMatrix(), t, getWidth(), getHeight());
            mPaint.setAntiAlias(true);
            canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        }
    }

    /**
     * t should be 0~1;
     * 
     * @param t
     * @param w
     * @param h
     * @return
     */
    private Matrix getMatrix(Matrix matrix, float t, int w, int h) {
        matrix.setSkew(t, t);
        return matrix;

    }
}
