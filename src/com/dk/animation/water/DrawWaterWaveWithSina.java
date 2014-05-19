package com.dk.animation.water;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.widget.ImageView;

public class DrawWaterWaveWithSina extends ImageView implements Runnable {
    boolean isRunning = false;

    private final static double TWO_PI = Math.PI * 2;
    private int width;

    private int height;

    private int[] mBitmap2;

    private int[] mBitmap1;

    private float wavelength = 36;
    private float amplitude = 10;
    private float phase = 0;
    private int radius = 5;

    private int radius2 = 0;
    private int icentreX;
    private int icentreY;

    private int alpha = 255;
    private Paint mPaint;
    private boolean flag = true;
    private Activity mActivity;

    private int SCALE = 3;

    public DrawWaterWaveWithSina(Activity context, Bitmap bmp) {
        super(context);
        mPaint = new Paint();
        Bitmap image = bmp;
        width = image.getWidth() / SCALE;
        height = image.getHeight() / SCALE;
        mActivity = context;
        mBitmap2 = new int[width * height];
        mBitmap1 = new int[width * height];

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(image, width, height, false);

        scaledBitmap.getPixels(mBitmap1, 0, width, 0, 0, width, height);
        for (int i = 0; i < width * height; i++) {
            mBitmap2[i] = mBitmap1[i];
        }
        setImageBitmap(Bitmap.createBitmap(mBitmap2, 0, width, width, height, Bitmap.Config.ARGB_8888));

    }

    // protected void onDraw(Canvas canvas) {
    // canvas.drawBitmap(mBitmap2, 0, width, 0, 0, width, height, true, mPaint);
    // super.onDraw(canvas);
    // }

    // @Override
    // public boolean onTouchEvent(MotionEvent event) {
    //
    // if (event.getAction() == MotionEvent.ACTION_DOWN && !isRunning) {
    // start((int)event.getX(), (int)event.getY());
    // }
    //
    // return super.onTouchEvent(event);
    // }

    private boolean transformInverse(int x, int y, int[] out) {
        int dx = x - icentreX;
        int dy = y - icentreY;
        int distance2 = dx * dx + dy * dy;

        if (distance2 > radius2) {
            out[0] = x;
            out[1] = y;
            out[2] = 0;
            return false;
        } else {
            float distance = (float)Math.sqrt(distance2);
            float amount = amplitude * (float)Math.sin(distance / wavelength * TWO_PI - phase / TWO_PI);
            // float amount = amplitude * (float)Math.sin(distance / WT - PW);
            amount *= (radius - distance) / radius;
            // if (distance != 0)
            // amount *= wavelength / distance;
            out[0] = (int)(x + dx * amount);
            out[1] = (int)(y + dy * amount);
            out[2] = (int)distance;
            return true;
        }
    }

    private void createNextBitmap() {
        int[] temp = new int[3];
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                if (transformInverse(i, j, temp)) {
                    if (temp[0] >= width || temp[1] >= height || temp[0] < 0 || temp[1] < 0) {
                        mBitmap2[j * width + i] = 0x00000000;
                    } else {
//                        mBitmap2[j * width + i] = (mBitmap1[temp[1] * width + temp[0]] & 0x00ffffff)
//                                + (int)((1 - temp[2] / (float)radius) * alpha) << 24;
                        mBitmap2[j * width + i] = (mBitmap1[temp[1] * width + temp[0]] & 0x00ffffff)
                                +  (alpha << 24);
                    }
                } else {
                    if (temp[0] >= width || temp[1] >= height || temp[0] < 0 || temp[1] < 0) {
                         mBitmap2[j * width + i] = 0x00000000;
                    } else {
                         mBitmap2[j * width + i] = mBitmap1[temp[1] * width + temp[0]];
                    }
                }

            }
    }

    @Override
    public void run() {
        isRunning = true;
        while (flag) {
            try {
                Thread.sleep(30);
            } catch (Exception e) {
            }
            // filter.radius2++;
            phase += 5;
            radius += 5;
            amplitude /= 1.12;
            if (amplitude < 0.01) {
                stop();
                return;
            }
            if (alpha > 0) {
                alpha -= 5;
            } else {
                alpha = 0;
            }
            radius2 = radius * radius;

            long start = System.currentTimeMillis();
            createNextBitmap();
            post(new Runnable() {

                @Override
                public void run() {
                    setImageBitmap(Bitmap.createBitmap(mBitmap2, 0, width, width, height, Bitmap.Config.ARGB_8888));

                }
            });

            System.out.println("duration:" + (System.currentTimeMillis() - start));
            postInvalidate();

        }
    }

    private void stop() {
        flag = false;
        if (this.getParent() != null) {
            this.post(new Runnable() {

                @Override
                public void run() {
                    mActivity.getWindowManager().removeView(DrawWaterWaveWithSina.this);
                }
            });
        }

    }

    public void start(int x, int y) {
        icentreX = x / SCALE;
        icentreY = y / SCALE;

        Thread t = new Thread(this);
        t.start();
    }

}
