package com.zk.zju.hitplanes.GameView;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/**
 * Created by Administrator on 2017/11/11.
 */
//用于显示爆炸效果
class Explosion extends Item {

    int frame = 14; // 这个动画时由14帧组成
    private int i = 0; //当前帧数

    Explosion(Bitmap bitmap) {
        super(bitmap);
    }

    boolean drawThisExplosion(Canvas canvas) {

      //  Log.d("zkk","width " +(this.width) / 14);
        Bitmap framBitmap = bitmap.createBitmap(bitmap, i * this.width / 14, 0, (this.width) / 14, (this.height),null,false);
        Paint p = new Paint();
        canvas.drawBitmap(framBitmap, positionX, positionY, p);
        i++;
        if (i == 14) {
            i = 0;
            return true;
        } else {
            return false;
        }
    }

}
