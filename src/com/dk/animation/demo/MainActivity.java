package com.dk.animation.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.dk.animation.ActivityAnimationTool;
import com.dk.animation.BlurEffect;
import com.dk.animation.CloseEffect;
import com.dk.animation.FoldingEffect;
import com.dk.animation.R;
import com.dk.animation.SkewEffect;
import com.dk.animation.SplitEffect;
import com.dk.animation.TwisterEffect;
import com.dk.animation.WaterEffect;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityAnimationTool.init(new WaterEffect());
        findViewById(R.id.iv).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityAnimationTool.startActivity(MainActivity.this, new Intent(MainActivity.this, NextActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_blur:
                ActivityAnimationTool.init(new BlurEffect());
                break;
            case R.id.action_close:
                ActivityAnimationTool.init(new CloseEffect());
                break;
            case R.id.action_split:
                ActivityAnimationTool.init(new SplitEffect());
                break;
//            case R.id.action_twister:
//                ActivityAnimationTool.init(new TwisterEffect());
//                break;
            case R.id.action_folding:
                ActivityAnimationTool.init(new FoldingEffect());
                break;
            case R.id.action_skew:
                ActivityAnimationTool.init(new SkewEffect());
                break;
            case R.id.action_water:
                ActivityAnimationTool.init(new WaterEffect());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
