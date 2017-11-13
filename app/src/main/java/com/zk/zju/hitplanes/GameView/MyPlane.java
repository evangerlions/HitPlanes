package com.zk.zju.hitplanes.GameView;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Administrator on 2017/11/9.
 */
//此类为玩家战机的模型，主要用于判断触摸区域是否为此战机，以达到触摸移动此战机的效果；
public class MyPlane extends Item{

    //获取屏幕长宽信息
    private Screen screen;

    //设置战机每次移动的偏移量
    private int offsetX = -1;
    private int offsetY = -1;

    //构造函数
    MyPlane(Bitmap bitmap, Screen screen) {
        super(bitmap);

        this.screen = screen;

        //初始化战机位置，在屏幕中间靠下出现
        this.positionX = screen.getWidth() / 2;
        this.positionY = screen.getHeight() - height;

        this.type = 0;
    }

    //返回触摸的是否为此战机
    boolean isMyPlane(int currentX, int currentY) {
        if ( (currentX > positionX - width / 2 && currentX < positionX + width / 2) && (currentY > positionY - height / 2 && currentY < positionY + height / 2)) {
            return true;
        } else {
            offsetX = -1;
            offsetY = -1;
            return false;
        }
    }

    //使战机矩阵中心点位置与触摸点重合
    int getCenterPlaneX() {
        return positionX - width / 2;
    }

    int getCenterPlaneY() {
        return positionY - height / 2;
    }

    //移动战机位置
    void setCurrentXY(int currentX, int currentY) {
        //确保触摸点在画布内时才作出响应（防止触摸点移动到状态栏等引发战机偏移）
        if(currentY > 0 && currentY <=  screen.getHeight() && currentX > 0 &&currentX <= screen.getWidth() ) {


            if (offsetX == -1) {
                offsetX = currentX;
                offsetY = currentY;
            } else {
                offsetX = currentX - offsetX;
                this.positionX = positionX + offsetX;
                //   Log.d("zkk","currentX :" + currentX);

                //防止战机越出屏幕
                if (this.positionX < width / 2) {
                    this.positionX = width / 2;
                } else if (this.positionX > screen.getWidth() - width / 2) {
                    this.positionX = screen.getWidth() - width / 2;
                }
                offsetX = currentX;


                offsetY = currentY - offsetY;
                this.positionY = positionY + offsetY;

                //防止战机越出屏幕
                if (this.positionY < height / 2) {
                    this.positionY = height / 2;
                } else if (this.positionY > screen.getHeight() - height / 2) {
                    this.positionY = screen.getHeight() - height / 2;
                }
                offsetY = currentY;
          //      Log.d("zkk", "currentY :" + currentY);

//                Log.d("zkk","X: "+positionX);
//                Log.d("zkk","Y: "+positionY);
            }
        }
    }
}
