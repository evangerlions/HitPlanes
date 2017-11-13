package com.zk.zju.hitplanes.GameView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.zk.zju.hitplanes.R;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class GameView extends View {

    private static final int MAX_YELLOW_BULLET = 150; //黄色子弹最大射出数量
    public int currentX;
    public int currentY;
    MyPlane myPlane;

    //moveItem数组存放除了战机之外的其他物品，比如敌机，奖励,用于随机生成
    MoveItem[] moveItems;
    //这个数组辅助存放上个数组有数据的位置，再次生成随机序列的时候加快清空上个数组的速度
    int[] moveItemsIndex;
    //这个布尔类型用来记录是否是第一次初始化随机数组moveItem
    boolean isFirstRandom;
    //这个布尔类型用来记录有没有黄色子弹
    boolean haveYellowBullet;

    //定义并创建画笔
    Paint p = new Paint();

    //获取屏幕分辨率，用于初始化战机位置
    Screen screen;

    {
        DisplayMetrics dm;
        dm = getResources().getDisplayMetrics();
        screen = Screen.getInstance(dm);
    }

    //OnTouchEvent里调用：isMove 用来记录拖动战机移动的动作,isClick用来判断是不是单击事件
    boolean isMove;
    boolean isClick;

    //记录当前是第几帧
    long frame;

    //此链表记录已经生成的MoveItem，以便于它们按规定速度向下移动
    LinkedList<MoveItem> existMoveItems;

    //此链表存储所有的子弹，设想是先创建能够铺满整个屏幕的子弹，要被显示的子弹在链表前部，不需要被显示的子弹在链表后部
    LinkedList<Bullet> totalBullets;
    //黄色子弹游标，辅助判断是否发射数超过上限300发
    int cursorYellowButtets;

    //爆炸效果，此列表存储所有需要爆炸的地点，和已经绘制的帧数
    LinkedList<Explosion> totalExplosion;
    Bitmap explosionBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.explosion);

    //游标，记录当前遍历moveItems数组的位置,从0到99循环
    int cursorMoveItem;

    //打飞机分数
    long totalScore = 0;
    long aMAXScore;

    //建立数据库，用于存储分数
    private MAXScoreDBManager db = MAXScoreDBManager.getInstance(this.getContext());
    //目前游戏运行状态
    private static final short GAME_STATE_RUNING = 1;
    private static final short GAME_STATE_OVER = 0;
    private short game_state = GAME_STATE_RUNING;

    //UI方面绘制
    //设置textPaint，设置为抗锯齿，且是粗体
    private Paint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
    private float density = getResources().getDisplayMetrics().density;//屏幕密度
    float fontSize = 15;//默认的字体大小，用于绘制左上角的文本
    float fontSize2 = 20;//用于在Game Over的时候绘制Dialog中的文本
    float borderSize = 2;//Game Over的Dialog的边框
    Rect continueRect = new Rect();//"重新开始"按钮的Rect

    {
        textPaint.setColor(0xff000000);
        fontSize *= density;
        fontSize2 *= density;
        textPaint.setTextSize(fontSize);
        borderSize *= density;
    }

    int temp;//调试用

    public GameView(Context context) {
        super(context);
        //    Log.d("zkk","myPlaneMap已初始化");
        init(null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        Log.d("zkk","myPlaneMap没有初始化1");
        init(attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //      Log.d("zkk","myPlaneMap没有初始化2");
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

        currentX = 0;
        currentY = 0;

        cursorMoveItem = -1;

        isMove = false;
        isClick = false;
        isFirstRandom = true;
        haveYellowBullet = false;
        cursorYellowButtets = 0 ;

        frame = 0;

        moveItems = null;
        moveItemsIndex = null;
        existMoveItems = null;
        totalBullets = null;
        totalExplosion = null;

        System.gc();

        moveItems = new MoveItem[100];
        moveItemsIndex = new int[10];
        existMoveItems = new LinkedList<>();
        totalBullets = new LinkedList<>();
        totalExplosion = new LinkedList<>();

        executeRandom(true);
        initBullet(screen);
        totalScore = 0;
        aMAXScore = db.query(1).getMAXScore();


        //此战机实际像素点：60*76 Bitmap宽和高是将此数据乘3，实际和屏幕分辨率有关，如果分辨率高的话会进行放大，所以Bitmap矩形会大一点
        if (myPlane != null) {  //重新开始时会调用，所以讲已经有的myPlane销毁
            myPlane = null;
        }
        myPlane = new MyPlane(BitmapFactory.decodeResource(this.getResources(), R.drawable.plane), screen);


        //      thread.start();
//        Log.d("zkk","dm"+dm.widthPixels);
//        Log.d("zkk","dm"+dm.heightPixels);
    }

    @Override
    public void onDraw(Canvas canvas) {
//      Log.d("zkk", "Prepare onDraw");
        super.onDraw(canvas);

        if (game_state == GAME_STATE_RUNING) {
            drawGameRunning(canvas);

            //延迟渲染可以用于控制游戏速度 1000/120  表示120帧
            postInvalidateDelayed(1000 / 120);
            frame++;
        } else if (game_state == GAME_STATE_OVER) {
            //判断是否刷新纪录
            if (totalScore > aMAXScore) {
                db.update(1, totalScore);
                aMAXScore = totalScore;
            }
            drawGameOver(canvas);
            init(null, 0);
        }


//        Paint p2 = new Paint();
//        p2.setColor(getResources().getColor(R.color.colorAccent));
//       canvas.drawCircle(myPlane.myPlaneX, myPlane.myPlaneY, 10, p2);
//        Log.d("zkk", "onDraw done!");

    }

    //初始化子弹链表
    private void initBullet(Screen screen) {
        Bullet temp = new Bullet(BitmapFactory.decodeResource(this.getResources(), R.drawable.blue_bullet));
        int bulletHeight = temp.height;
        for (int i = 0; i <= screen.getHeight() / bulletHeight; i++) {
            Bullet bullet = new Bullet(BitmapFactory.decodeResource(this.getResources(), R.drawable.blue_bullet));
            bullet.positionX = -1;
            bullet.positionY = -1;
            totalBullets.add(bullet);
        }
    }

    private void drawGameRunning(Canvas canvas) {

        //MovingItem描绘（不包含子弹）
        drawMoveItems(canvas);

        //战机描绘,canvas是以屏幕左上角为原点
        canvas.drawBitmap(myPlane.bitmap, myPlane.getCenterPlaneX(), myPlane.getCenterPlaneY(), p);

        //子弹描绘,顺便进行碰撞检测
        drawBullet(canvas);

        //爆炸描绘
        drawExplosion(canvas);
        //  Log.d("zkk", "子弹链表大小" + totalBullets.size()); //链表长度没有变过

        //UI描绘，也就是显示分数
        drawUI(canvas);
    }

    //UI描绘，目前只有分数
    private void drawUI(Canvas canvas) {
        canvas.drawText("当前得分: "+totalScore, fontSize * density / 2, fontSize * density / 2, textPaint);
        canvas.drawText("最高得分: "+aMAXScore, fontSize * density / 2, fontSize * density, textPaint);
    }

    //爆炸效果描绘
    private void drawExplosion(Canvas canvas) {
        Iterator<Explosion> it = totalExplosion.iterator();
        while (it.hasNext()) {
            Explosion tempExplosion = it.next();
            if (tempExplosion.drawThisExplosion(canvas)) {
                it.remove();
            }
        }
        //   Log.d("zkk", "Size of Explosin:"+ totalExplosion.size());
    }

    //碰撞检测，判断子弹是否击中敌机和战机是否撞上敌机，和是否获得奖励
    private short collision(Bullet bullet, MyPlane myPlane) {

        Iterator<MoveItem> it = existMoveItems.iterator();

        while (it.hasNext()) {
            MoveItem moveItem = it.next();
            //  Log.d("zkk","碰撞检测");

            //表明不是奖励（是一个敌机），进行子弹碰撞检测（子弹打不到奖励）
            if (moveItem.type != 2) {
                EnemyPlane enemyPlane = (EnemyPlane) moveItem;

                //子弹是否撞上敌机检测
                if (bullet.positionX > enemyPlane.positionX - bullet.width && bullet.positionX < enemyPlane.positionX + enemyPlane.width + bullet.width) { //X轴方向判断范围
                    if (bullet.positionY > enemyPlane.positionY - bullet.height && bullet.positionY < enemyPlane.positionY + enemyPlane.height + bullet.height) { //Y轴方向判断范围

                        //   if (bullet.positionX > enemyPlane.positionX && bullet.positionX < enemyPlane.positionX + enemyPlane.width) { //X轴方向判断范围
                        //       Log.d("zkk","碰撞X通过");
                        //     if (bullet.positionY < enemyPlane.positionY + enemyPlane.height && bullet.positionY > enemyPlane.positionY) {  //Y轴方向判断范围
                        //发生了碰撞，检测敌机类型
                        switch (enemyPlane.enemyPlaneType) {
                            case 0:
                                SmallEnemyPlane tempPlane1 = (SmallEnemyPlane) enemyPlane;
                                tempPlane1.health -= bullet.power;
                                // Log.d("zkk","碰撞！");
                                break;
                            case 1:
                                MiddleEnemyPlane tempPlane2 = (MiddleEnemyPlane) enemyPlane;
                                tempPlane2.health -= bullet.power;
                                // Log.d("zkk","碰撞！");
                                break;
                            case 2:
                                BigEnemyPlane tempPlane3 = (BigEnemyPlane) enemyPlane;
                                tempPlane3.health -= bullet.power;
                                // Log.d("zkk","碰撞！");
                                break;
                            default:
                                break;

                        }
                        return 1;
                    }
                }
            }
            //不管是敌机还是奖励，战机都要对其进行碰撞检测
            if (myPlane.positionX - myPlane.width / 2 > moveItem.positionX - myPlane.width && myPlane.positionX + myPlane.width / 2 < moveItem.positionX + moveItem.width + myPlane.width) { //X轴方向判断范围
                if (myPlane.positionY - myPlane.height / 2 > moveItem.positionY - myPlane.height && myPlane.positionY + myPlane.height / 2 < moveItem.positionY + moveItem.height + myPlane.height) { //Y轴方向判断范围

                    if(moveItem.type == 1){ //撞到敌机了，GG
                        Explosion explosion = new Explosion(explosionBitmap);
                        explosion.positionX = myPlane.positionX - myPlane.width / 2;
                        explosion.positionY = myPlane.positionY - myPlane.height / 2;
                        totalExplosion.add(explosion);

                        return 2;
                    }else {  //不是撞到敌机就是撞到了奖励，加强子弹
                        Award tempAward = (Award) moveItem;
                        tempAward.health -= 2;
                        haveYellowBullet = true;
                        cursorYellowButtets = 0; //初始化游标
                        return 3;
                    }
                }
            }

        }
        return 0;
    }

    //游戏结束界面描绘
    private void drawGameOver(Canvas canvas) {
        Paint paint = new Paint();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        //存储原始值
        float originalFontSize = textPaint.getTextSize();
        Paint.Align originalFontAlign = textPaint.getTextAlign();
        int originalColor = paint.getColor();
        Paint.Style originalStyle = paint.getStyle();
        /*
        W = 360
        w1 = 20
        w2 = 320
        buttonWidth = 140
        buttonHeight = 42
        H = 558
        h1 = 150
        h2 = 60
        h3 = 124
        h4 = 76
        */
        int w1 = (int) (20.0 / 360.0 * canvasWidth);
        int w2 = canvasWidth - 2 * w1;
        int buttonWidth = (int) (140.0 / 360.0 * canvasWidth);

        int h1 = (int) (150.0 / 558.0 * canvasHeight);
        int h2 = (int) (60.0 / 558.0 * canvasHeight);
        int h3 = (int) (124.0 / 558.0 * canvasHeight);
        int h4 = (int) (76.0 / 558.0 * canvasHeight);
        int buttonHeight = (int) (42.0 / 558.0 * canvasHeight);

        canvas.translate(w1, h1);


        //绘制背景色
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFD7DDDE);
        Rect rect1 = new Rect(0, 0, w2, canvasHeight - 2 * h1);
        canvas.drawRect(rect1, paint);
        //绘制边框
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFF515151);
        paint.setStrokeWidth(borderSize);
        //paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        canvas.drawRect(rect1, paint);
        //绘制文本"您的分数"
        textPaint.setTextSize(fontSize2);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("您的分数", w2 / 2, (h2 - fontSize2) / 2 + fontSize2, textPaint);
        //绘制"飞机大战分数"下面的横线
        canvas.translate(0, h2);
        canvas.drawLine(0, 0, w2, 0, paint);
        //绘制实际的分数
        String allScore = String.valueOf(totalScore);
        canvas.drawText(allScore, w2 / 2, (h3 - fontSize2) / 2 + fontSize2, textPaint);

        //绘制分数下面的横线
        canvas.translate(0, h3);
        canvas.drawLine(0, 0, w2, 0, paint);
        //绘制按钮边框
        Rect rect2 = new Rect();
        rect2.left = (w2 - buttonWidth) / 2;
        rect2.right = w2 - rect2.left;
        rect2.top = (h4 - buttonHeight) / 2;
        rect2.bottom = h4 - rect2.top;
        canvas.drawRect(rect2, paint);
        //绘制文本"重新开始"
        canvas.translate(0, rect2.top);
        canvas.drawText("重新开始", w2 / 2, (buttonHeight - fontSize2) / 2 + fontSize2, textPaint);
        continueRect = new Rect(rect2);
        continueRect.left = w1 + rect2.left;
        continueRect.right = continueRect.left + buttonWidth;
        continueRect.top = h1 + h2 + h3 + rect2.top;
        continueRect.bottom = continueRect.top + buttonHeight;
        //重置
        textPaint.setTextSize(originalFontSize);
        textPaint.setTextAlign(originalFontAlign);
        paint.setColor(originalColor);
        paint.setStyle(originalStyle);
    }

    //子弹描绘，因为需要进行子弹碰撞判定很多次，就顺便判断战机是否撞上敌机
    private void drawBullet(Canvas canvas) {
        //子弹发射的思路是每过一定帧数发射一枚子弹
        //游标，记录当前遍历totalBullets链表的位置
        Iterator<Bullet> cursorBullet = totalBullets.iterator();
        LinkedList<Bullet> tempList = new LinkedList<>(); //此临时链表记录①处移除的数组，遍历结束后统一加入totalBullet尾部，避免引发ConcurrentModificationException错误
        Bullet value = null;
        while (cursorBullet.hasNext()) {

            value = cursorBullet.next();

            //已经发射的子弹，将其位置按速度移动
            if (value.positionX != -1) {
                //        Log.d("zkk", "positionY : "+ value.positionY);
                //         Log.d("zkk","screen Height : "+ screen.getHeight());
                if (value.positionY < 0) { //如果子弹越界，就将子弹移除（回归初始状态）
                    value.setPositionX(-1);
                    value.setPositionY(-1);
                    Bullet tempBullet = (Bullet) value.clone();
                    cursorBullet.remove();//①
                    tempList.add(tempBullet);
                    //        Log.d("zkk", "子弹初始化 ");

                } else { //正常状态的子弹，在canvas中描绘
                    value.setPositionY(value.speed + value.positionY);
                    //发生了碰撞，将此子弹移除
                    int tag = collision(value, myPlane); //tag为0没有发生碰撞，为1子弹撞上敌机，为2战机撞上敌机
                    if (tag == 1) {
                        value.setPositionX(-1);
                        value.setPositionY(-1);
                        Bullet tempBullet = (Bullet) value.clone();
                        cursorBullet.remove();//①
                        tempList.add(tempBullet);
                    } else if (tag == 2) {
                        game_state = GAME_STATE_OVER;

                    }else if(tag == 3){
                        haveYellowBullet = true;
                    } else {
                        canvas.drawBitmap(value.bitmap, value.positionX, value.positionY, p);
                    }
                }
            } else {
                if(!haveYellowBullet){
                    if (frame % 5 == 0) {
                        //每隔一定时间生成一个子弹
                        Bullet tempBullet = new YellowBullet(BitmapFactory.decodeResource(this.getResources(), R.drawable.blue_bullet));
                        tempBullet.setPositionX(myPlane.positionX);
                        tempBullet.setPositionY(myPlane.positionY - myPlane.height / 2);
                        //把发射出去的子弹放到链表头部
                        totalBullets.remove(value);
                        totalBullets.addFirst(tempBullet);
                        //       log++;
                        //       Log.d("zkk", "子弹生成 "+log);
                    }
                    break;
                }else {  //有黄色子弹

                    if (frame % 2 == 0) { //黄色子弹发射得更快
                        //每隔一定时间生成一个子弹
                        YellowBullet tempYB = new YellowBullet(BitmapFactory.decodeResource(this.getResources(), R.drawable.yellow_bullet));
                        tempYB.setPositionX(myPlane.positionX);
                        tempYB.setPositionY(myPlane.positionY - myPlane.height / 2);
                        //把发射出去的子弹放到链表头部
                        totalBullets.remove(value);
                        totalBullets.addFirst(tempYB);
                        //       log++;
                        //       Log.d("zkk", "子弹生成 "+log);
                        cursorYellowButtets++;
                        if (cursorYellowButtets >= MAX_YELLOW_BULLET){
                            cursorYellowButtets = 0;
                            haveYellowBullet = false;
                        }
                    }
                    break;
                }

            }

        }
        cursorBullet = tempList.iterator();
        while (cursorBullet.hasNext()) {
            totalBullets.addLast(cursorBullet.next());
        }
    }

    //敌机，奖励描绘,同时移除血量为0的敌机，被撞到的奖励
    private void drawMoveItems(Canvas canvas) {

        //游标最大为99，避免越界
        if (cursorMoveItem == 99) {
            executeRandom(isFirstRandom);
            isFirstRandom = false;
            cursorMoveItem = 0;
            //每隔6帧移动一次游标，避免敌机生成过快
        } else if (frame % 4 == 0) {
            cursorMoveItem++;

            if (moveItems[cursorMoveItem] != null) {
                //X坐标设置随机
                Random random = new Random();
                int randomX = random.nextInt(screen.getWidth() - moveItems[cursorMoveItem].width);
//                Log.d("zkk", "" +randomX);
                moveItems[cursorMoveItem].setPositionX(randomX);

                MoveItem temp = (MoveItem) moveItems[cursorMoveItem].clone();
                existMoveItems.add(temp);


            }
        }
        //    Log.d("zkk", "existMoveItems.size: "+existMoveItems.size());

        //     if(existMoveItems.getFirst()!= null) {
        //MoveItem下降到超过屏幕或者HP <= 0，就删除它
        Iterator<MoveItem> it = existMoveItems.iterator();
        while (it.hasNext()) {
            MoveItem value = it.next();
            if (value.positionY > screen.getHeight()) {
                it.remove();  // ok
                //   Log.d("zkk", "因为position移除 "+temp);
                continue;
            }
            if (value.type == 1) {
                EnemyPlane tempPlane = (EnemyPlane) value;
                if (tempPlane.health <= 0) {
                    //敌机爆炸了，添加一个爆炸
                    Explosion explosion = new Explosion(explosionBitmap);
                    explosion.positionX = tempPlane.positionX + tempPlane.width/2 - explosion.width/2/14;
                    explosion.positionY = tempPlane.positionY + tempPlane.height/2 - explosion.height/2;
                    totalExplosion.add(explosion);

                    //记录分数
                    totalScore += ((EnemyPlane) value).score;
                    it.remove();
                    //          temp++;
                    //    Log.d("zkk", "因为health移除 "+temp);
                    continue;

                }
            }else if(value.type == 2){
                Award tempAward = (Award) value;
                if(tempAward.health < 0){
                    it.remove();
                    continue;
                }
            }
            value.setPositionY(value.positionY + value.speed);
            canvas.drawBitmap(value.bitmap, value.positionX, value.positionY, p);
        }
    }

    //这个random函数随机生成敌机和奖励,初始循环10次
    private void executeRandom(boolean isFirst) {
        if (!isFirst) {
            for (int j = 0; j < 10; j++) {
                moveItems[moveItemsIndex[j]] = null;
            }
        }

        int i;
        for (int j = 0; j < 10; j++) {  // 循环10次

            i = (int) (Math.random() * 100);
//            Log.d("zkk", "j = " + j + "; i = " + i);
            if (moveItems[i] == null) {
                moveItemsIndex[j] = i;
                MoveItem temp;
                //按比例生成敌机，小型机0-2，中型机3-6，大型机7-8，奖励9
                switch (j) {
                    case 0:
                        temp = new SmallEnemyPlane(BitmapFactory.decodeResource(this.getResources(), R.drawable.small));
                        moveItems[i] = temp;
                        break;
                    case 1:
                        temp = new SmallEnemyPlane(BitmapFactory.decodeResource(this.getResources(), R.drawable.small));
                        moveItems[i] = temp;
                        break;
                    case 2:
                        temp = new SmallEnemyPlane(BitmapFactory.decodeResource(this.getResources(), R.drawable.small));
                        moveItems[i] = temp;
                        break;
                    case 3:
                        temp = new MiddleEnemyPlane(BitmapFactory.decodeResource(this.getResources(), R.drawable.middle));
                        moveItems[i] = temp;
                        break;
                    case 4:
                        temp = new MiddleEnemyPlane(BitmapFactory.decodeResource(this.getResources(), R.drawable.middle));
                        moveItems[i] = temp;
                        break;
                    case 5:
                        temp = new MiddleEnemyPlane(BitmapFactory.decodeResource(this.getResources(), R.drawable.middle));
                        moveItems[i] = temp;
                        break;
                    case 6:
                        temp = new MiddleEnemyPlane(BitmapFactory.decodeResource(this.getResources(), R.drawable.middle));
                        moveItems[i] = temp;
                        break;
                    case 7:
                        temp = new BigEnemyPlane(BitmapFactory.decodeResource(this.getResources(), R.drawable.big));
                        moveItems[i] = temp;
                        break;
                    case 8:
                        temp = new BigEnemyPlane(BitmapFactory.decodeResource(this.getResources(), R.drawable.big));
                        moveItems[i] = temp;
                        break;
                    case 9:
                        temp = new Award(BitmapFactory.decodeResource(this.getResources(), R.drawable.bullet_award));
                        moveItems[i] = temp;
                        break;
                    default:
                        break;
                }


            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currentX = (int) event.getX();
        currentY = (int) event.getY();

        //游戏运行时进行相应的触摸响应
        if (game_state == GAME_STATE_RUNING) {
            if (isMove) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                Log.d("zkk", "Prepare invalidate!");
                    myPlane.setCurrentXY(currentX, currentY);
//                Log.d("zkk", "invalidate done!");

                }
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                //    Log.d("zkk", "down!");
                //如果触摸的是这个战机的话，就移动它
                if (myPlane.isMyPlane(currentX, currentY)) {
                    //         Log.d("zkk", "isMyPlane!");
                    isMove = true;
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                //初始化MyPlane中的偏移量
                myPlane.isMyPlane(-1, -1);
                isMove = false;
            }

        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                isClick = true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP && isClick) {

                if (continueRect.contains(currentX, currentY)) {
                    game_state = GAME_STATE_RUNING;
                    postInvalidate();
                }
                isClick = false;
            }
        }
        return true;
    }
}

//by Zhoukai version 0.4, 2017.11.12