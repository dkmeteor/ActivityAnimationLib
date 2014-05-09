package com.dk.animation.water;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.widget.ImageView;

public class DrawWaterWaveWithSina extends ImageView implements Runnable {
    boolean isRunning = false;

    private final static double TWO_PI = Math.PI;
    private int width;

    private int height;

    private int[] mBitmap2;

    private int[] mBitmap1;

    private float wavelength = 16;
    private float amplitude = 10;
    private float phase = 0;
    private float radius = 5;

    private float radius2 = 0;
    private float icentreX;
    private float icentreY;

    private int alpha = 255;
    private int alpha2 = 0x000000ff;
    private int alpha3 = 0xff;
    private int alphaR = alpha << 24;
    private int alphaRR = 0xff000000;
    private Paint mPaint;

    public DrawWaterWaveWithSina(Context context, Bitmap bmp) {
        super(context);
        mPaint = new Paint();
        Bitmap image = bmp;
        width = image.getWidth();
        height = image.getHeight();

        mBitmap2 = new int[width * height];
        mBitmap1 = new int[width * height];
        Bitmap.Config config = image.getConfig();
        image.getPixels(mBitmap1, 0, width, 0, 0, width, height);
        for (int i = 0; i < width * height; i++) {
            mBitmap2[i] = mBitmap1[i];
        }
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap2, 0, width, 0, 0, width, height, true, mPaint);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN && !isRunning) {
            start((int)event.getX(), (int)event.getY());
        }

        return super.onTouchEvent(event);
    }

    private boolean transformInverse(int x, int y, int[] out) {
        float dx = x - icentreX;
        float dy = y - icentreY;
        float distance2 = dx * dx + dy * dy;

        if (distance2 > radius2) {
            out[0] = x;
            out[1] = y;
            out[2] = 0;
            return false;
        } else {
            float distance = (float)Math.sqrt(distance2);
            float amount = amplitude * (float)Math.sin(distance / wavelength * TWO_PI - phase / TWO_PI);
            amount *= (radius - distance) / radius;
            if (distance != 0)
                amount *= wavelength / distance;
            out[0] = (int)(x + dx * amount);
            out[1] = (int)(y + dy * amount);
            out[2]= (int)Math.sqrt(distance);
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
                        mBitmap2[j * width + i] = (mBitmap1[temp[1] * width + temp[0]] & 0x00ffffff) +(int)((1-temp[2]/radius) *alpha) << 24;
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
        while (true) {
            try {
                Thread.sleep(30);
            } catch (Exception e) {
            }
            // filter.radius2++;
            phase += 20;
            radius += 20;
            amplitude /= 1.03;
            if (alpha > 0) {
                alpha -= 5;
            }
            alphaR = alpha << 24;

            radius2 = radius * radius;
            createNextBitmap();
            postInvalidate();
        }
    }

    public void start(int x, int y) {
        icentreX = x;
        icentreY = y;

        Thread t = new Thread(this);
        t.start();

    }

}
