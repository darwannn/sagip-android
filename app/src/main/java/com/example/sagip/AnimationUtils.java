package com.example.sagip;

import android.view.View;
import android.view.animation.AlphaAnimation;

public class AnimationUtils {

    public static void applyFadeAnimation(final View view, final float visibility) {
        AlphaAnimation animation = new AlphaAnimation(1 - visibility, visibility);
        animation.setDuration(200);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }
}

