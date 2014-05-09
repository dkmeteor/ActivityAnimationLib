package com.dk.animation.water;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public class DrawWaterWave extends View implements Runnable {
    boolean isRunning = false;

    int BACKWIDTH;

    int BACKHEIGHT;

    short[] buf2;

    short[] buf1;

    int[] Bitmap2;

    int[] Bitmap1;

    public DrawWaterWave(Context context, Bitmap bmp) {
        super(context);

        Bitmap image = bmp;
        BACKWIDTH = image.getWidth();
        BACKHEIGHT = image.getHeight();

        buf2 = new short[BACKWIDTH * BACKHEIGHT];
        buf1 = new short[BACKWIDTH * BACKHEIGHT];
        Bitmap2 = new int[BACKWIDTH * BACKHEIGHT];
        Bitmap1 = new int[BACKWIDTH * BACKHEIGHT];

        image.getPixels(Bitmap1, 0, BACKWIDTH, 0, 0, BACKWIDTH, BACKHEIGHT);

        start();
    }

    void dropStone(int x, int y, int stonesize, int stoneweight) {
        if ((x + stonesize) > BACKWIDTH || (y + stonesize) > BACKHEIGHT || (x - stonesize) < 0 || (y - stonesize) < 0)
            return;

        for (int posx = x - stonesize; posx < x + stonesize; posx++)
            for (int posy = y - stonesize; posy < y + stonesize; posy++)
                if ((posx - x) * (posx - x) + (posy - y) * (posy - y) < stonesize * stonesize)
                    buf1[BACKWIDTH * posy + posx] = (short)-stoneweight;
    }

    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0x00000000);
        canvas.drawBitmap(Bitmap2, 0, BACKWIDTH, 0, 0, BACKWIDTH, BACKHEIGHT, false, null);
    }

    public void start() {
        isRunning = true;
        Thread t = new Thread(this);
        t.start();
    }

    public void key() {
        dropStone(BACKWIDTH / 2, BACKHEIGHT / 2, 10, 50);
    }

    public void stop() {
        isRunning = false;
    }

    public void run() {

        while (isRunning) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ;
            RippleSpread();
            render();
            postInvalidate();
        }
    }

    void RippleSpread() {
        for (int i = BACKWIDTH; i < BACKWIDTH * BACKHEIGHT - BACKWIDTH; i++) {

            buf2[i] = (short)(((buf1[i - 1] + buf1[i + 1] + buf1[i - BACKWIDTH] + buf1[i + BACKWIDTH]) >> 1) - buf2[i]);

            buf2[i] -= buf2[i] >> 5;
        }

        short[] ptmp = buf1;
        buf1 = buf2;
        buf2 = ptmp;
    }

    void render() {
        int xoff, yoff;
        int k = BACKWIDTH;
        for (int i = 1; i < BACKHEIGHT - 1; i++) {
            for (int j = 0; j < BACKWIDTH; j++) {
                xoff = buf1[k - 1] - buf1[k + 1];
                yoff = buf1[k - BACKWIDTH] - buf1[k + BACKWIDTH];

                if ((i + yoff) < 0) {
                    k++;
                    continue;
                }
                if ((i + yoff) > BACKHEIGHT) {
                    k++;
                    continue;
                }
                if ((j + xoff) < 0) {
                    k++;
                    continue;
                }
                if ((j + xoff) > BACKWIDTH) {
                    k++;
                    continue;
                }

                int pos1, pos2;
                pos1 = BACKWIDTH * (i + yoff) + (j + xoff);
                pos2 = BACKWIDTH * i + j;
                Bitmap2[pos2++] = Bitmap1[pos1++];
                k++;
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN && !isRunning) {
            dropStone((int)event.getX(), (int)event.getY(), 10, 50);
        }

        return super.onTouchEvent(event);
    }
}
