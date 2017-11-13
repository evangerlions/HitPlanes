package com.zk.zju.hitplanes.GameView;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/11/10.
 */

//这个类是所有物体父类
public abstract class Item {
    //物体数据矩阵大小及矩阵本身
    int width;
    int height;
    Bitmap bitmap;

    //记录物体位置信息，此坐标位于数据矩阵左上角
    int positionX;
    int positionY;

    //此属性为Item的种类,0-玩家战机，1-敌机，2-奖励，3-子弹
    short type;

    void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    Item(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
    }
}
