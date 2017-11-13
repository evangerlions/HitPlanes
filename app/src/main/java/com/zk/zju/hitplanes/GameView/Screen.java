package com.zk.zju.hitplanes.GameView;

import android.util.DisplayMetrics;

/**
 * Created by Administrator on 2017/11/10.
 */

//记录屏幕大小信息,使用单例模式
public class Screen {
    //使用绝对布局，下面为屏幕长和宽（含状态栏和底部虚拟按钮）
    private int width;
    private int height;

    //屏幕实际分辨率（不含状态栏和底部虚拟按钮）
    private int realWidth;
    private int realHeight;

    private  Screen (){};

    private static final Screen screen = new Screen();

    public static Screen getInstance(DisplayMetrics dm){

        screen.width = dm.widthPixels;
        screen.height = dm.heightPixels;
       // screen.realHeight =
        return  screen;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
