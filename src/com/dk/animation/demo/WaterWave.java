/**
 * fengsheng.yang@live.cn
 * 本程序可以随便使用，可以随便转载，但是希望尊重我的成果，请挂上本人博客
 * http://huojv.blog.hexun.com(有时间去看看，也许会有你要的东西)
 * 我老婆马艳，我会对你很好的！谢谢理解!
 * 2008-12-25
 */
package com.dk.animation.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class WaterWave extends Activity 
{
	DrawWaterWave	m_DrawWaterWave;
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        m_DrawWaterWave = new DrawWaterWave(this);
        setContentView(m_DrawWaterWave);
    }
    
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		m_DrawWaterWave.key();
		return super.onKeyDown(keyCode, event);
	}
}