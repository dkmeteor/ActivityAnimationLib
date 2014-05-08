package com.dk.animation;

import android.app.Activity;

public abstract class BaseEffect {
    public abstract void prepareAnimation(final Activity destActivity);

    public abstract void prepare(Activity currActivity);

    public abstract void clean(Activity activity);
    
    public abstract void cancel();

    public abstract void animate(final Activity destActivity, final int duration);
}
