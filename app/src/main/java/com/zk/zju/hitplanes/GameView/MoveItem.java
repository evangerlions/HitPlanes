package com.zk.zju.hitplanes.GameView;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/11/10.
 */

//这个类是移动的物体，比如敌机，奖励
public abstract class MoveItem extends Item implements Cloneable{
    //此属性是移动物品下落的速度
    int speed;

    public MoveItem(Bitmap bitmap) {
        super(bitmap);
    }

    int getCenterX() {
        return positionX - width / 2;
    }

    int getCenterY() {
        return positionY - height / 2;
    }

    @Override
    public Object clone(){
        MoveItem moveItem = null;
        try{
            moveItem = (MoveItem)super.clone();
        }catch (CloneNotSupportedException e ){
            e.printStackTrace();
        }
        return moveItem;
    }
}
