package com.example.game2048;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GameView extends LinearLayout {

    private static final String TAG="GameView";

    private  int number;//面板数组
    private String mNumberVal;
    private Paint mPaint;
    /**
     * 绘制文字的区域
     */
    private Rect mBound;

    public GameView(Context context) {
       this(context,null);
    }

    public GameView(Context context, AttributeSet attrs) {
       this(context,attrs,0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint=new Paint();


        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();   //屏幕信息的对象
        int cardWidth=displayMetrics.widthPixels;
        int cardHeight=displayMetrics.heightPixels;
        Config.CARD_WIDTH = (Math.min(cardWidth, cardHeight)-10)/Config.LINES;
        addCards( Config.CARD_WIDTH,Config.CARD_WIDTH);
        initGameView();
    }

    private  Card[][] cardsMap=new Card[Config.LINES][Config.LINES];

    private List<Point> emptyPoints=new ArrayList<>();

    private void initGameView(){
        setOrientation(LinearLayout.VERTICAL);
        setBackgroundColor(0xffbbada0);

        //检测手势方向
        setOnTouchListener(new OnTouchListener() {
            private float startX,startY,offsetX,offsetY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = event.getX()-startX;
                        offsetY = event.getY()-startY;
                        //先判断是左右还是上下移动，避免左上左下右上右下的干扰
                        if(Math.abs(offsetX)>Math.abs(offsetY)){
                            if(offsetX < -5){
                                Log.d(TAG,"手势向左滑动");
                                swipLeft();
                                game_music();
                            }else if(offsetX > 5){
                                Log.d(TAG,"手势向右滑动");
                                swipRight();
                                game_music();
                            }
                        }else{
                            if(offsetY < -5){
                                Log.d(TAG,"手势向上滑动");
                                swipUp();
                                game_music();
                            }else if(offsetY > 5){
                                Log.d(TAG,"手势向下滑动");
                                swipDown();
                                game_music();
                            }
                        }
                        break;
                }
                return true;
            }
        });

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //Config.CARD_WIDTH = (Math.min(w, h)-10)/Config.LINES;
        //addCards( Config.CARD_WIDTH,Config.CARD_WIDTH);
        startGame();//开始游戏
    }

    //4*4的卡片
    private void addCards(int cardWidth,int cardHeight) {
        Card c;
        LinearLayout line;
        LinearLayout.LayoutParams lineLp;
        for (int y = 0; y < Config.LINES; y++) {
            line=new LinearLayout(getContext());
            lineLp=new LinearLayout.LayoutParams(-1,cardHeight);
            addView(line,lineLp);
            for (int x = 0; x < Config.LINES; x++) {
                c = new Card(getContext());
               line.addView(c,cardWidth,cardHeight);
                cardsMap[x][y] = c;
            }
        }
    }

    void startGame(){
        MainActivity.getMainActivity().clearScore();
        //清理阶段
        for(int y = 0; y < Config.LINES; y++){
            for(int x = 0; x < Config.LINES; x++){
                cardsMap[x][y].setNum(0);
            }
        }
        //随机添加两个数
        addRandomNum();
        addRandomNum();
    }

    //添加随机数
    private void addRandomNum(){
        emptyPoints.clear();
        for (int y = 0; y < Config.LINES; y++) {
            for (int x = 0; x < Config.LINES; x++) {
                if(cardsMap[x][y].getNum()<=0){
                    emptyPoints.add(new Point(x,y));
                }
            }
        }
        if (emptyPoints.size()>0){
            Point p = emptyPoints.remove((int)(Math.random()*emptyPoints.size()));//随机移去一个点，和用get一样 random是0到1之间
            cardsMap[p.x][p.y].setNum(Math.random()>0.1?2:4);//出现2与4的比例为9:1
            //MainActivity.getMainActivity().getAnimLayer().createScaleTo1(cardsMap[p.x][p.y]);
        }
    }

    private void swipLeft(){
        boolean merge = false;//检测有没有合并，初始值为没有
        for(int y = 0;y < Config.LINES;y++){
            for(int x = 0; x < Config.LINES;x++){
                for(int x1 = x + 1; x1 < Config.LINES; x1++){//从当前的位置向右遍历获取值
                    if(cardsMap[x1][y].getNum() > 0){//如果获取的值不是空的
                        if(cardsMap[x][y].getNum() <= 0 ){//如果当前的位置是空
                            //MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x1][y],cardsMap[x][y], x1, x, y, y);
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());//将当前位置设为获取的值
                            cardsMap[x1][y].setNum(0);//获取的卡片设为0
                            x--;//再遍历一边，有相同的可以合并
                            merge = true;
                        }else if(cardsMap[x][y].equals(cardsMap[x1][y])){//如果当前的值与获取的值相同
                            //MainActivity.getMainActivity().getAnimLayer().createMoveAnim(cardsMap[x1][y], cardsMap[x][y],x1, x, y, y);
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x1][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());//有合并就加分
                            merge = true;
                        }
                        break;

                    }

                }
            }

        }
        if (merge){
            addRandomNum();
            checkComplete();
        }

    }
    private void swipRight(){
        boolean merge = false;//检测有没有合并，初始值为没有
        for(int y = 0;y < 4;y++){
            for(int x = 3; x >= 0;x--){
                for(int x1 = x - 1; x1 >= 0; x1--){//从当前的位置向左遍历获取值
                    if(cardsMap[x1][y].getNum() > 0){//如果获取的值不是空的
                        if(cardsMap[x][y].getNum() <= 0 ){//如果当前的位置是空
                            cardsMap[x][y].setNum(cardsMap[x1][y].getNum());//将当前位置设为获取的值
                            cardsMap[x1][y].setNum(0);//获取的卡片设为0
                            x++;//再遍历一次，有相同的可以合并
                            merge = true;
                        }else if(cardsMap[x][y].equals(cardsMap[x1][y])){//如果当前的值与获取的值相同
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x1][y].setNum(0);
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());//有合并就加分
                            merge = true;
                        }
                        break;
                    }

                }
            }

        }
        if (merge){
            addRandomNum();
            checkComplete();
        }

    }
    private void swipUp(){
        boolean merge = false;//检测有没有合并，初始值为没有
        for(int x = 0;x < 4;x++){
            for(int y = 0; y < 4;y++){
                for(int y1 = y + 1; y1 < 4; y1++){//从当前的位置向下遍历获取值
                    if(cardsMap[x][y1].getNum() > 0){//如果获取的值不是空的
                        if(cardsMap[x][y].getNum() <= 0 ){//如果当前的位置是空
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());//将当前位置设为获取的值
                            cardsMap[x][y1].setNum(0);//获取的卡片设为0
                            y--;//再遍历一边，有相同的可以合并
                            merge = true;
                        }else if(cardsMap[x][y].equals(cardsMap[x][y1])){//如果当前的值与获取的值相同
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());//有合并就加分
                            merge = true;
                        }
                        break;
                    }

                }
            }

        }
        if (merge){
            addRandomNum();
            checkComplete();
        }

    }
    private void swipDown(){
        boolean merge = false;//检测有没有合并，初始值为没有
        for(int x = 0;x < 4;x++){
            for(int y = 3; y >= 0;y--){
                for(int y1 = y - 1; y1 >= 0; y1--){//从当前的位置向上遍历获取值
                    if(cardsMap[x][y1].getNum() > 0){//如果获取的值不是空的
                        if(cardsMap[x][y].getNum() <= 0 ){//如果当前的位置是空
                            cardsMap[x][y].setNum(cardsMap[x][y1].getNum());//将当前位置设为获取的值
                            cardsMap[x][y1].setNum(0);//获取的卡片设为0
                            y++;//再遍历一边，有相同的可以合并
                            merge = true;
                        }else if(cardsMap[x][y].equals(cardsMap[x][y1])){//如果当前的值与获取的值相同
                            cardsMap[x][y].setNum(cardsMap[x][y].getNum()*2);
                            cardsMap[x][y1].setNum(0);
                            MainActivity.getMainActivity().addScore(cardsMap[x][y].getNum());//有合并就加分
                            merge = true;
                        }
                        break;
                    }

                }
            }

        }
        if (merge){
            addRandomNum();
            checkComplete();
        }


    }



    //检查游戏结束，只要有0(即空卡片)或者4个方向有相当有数值相同的卡片，游戏没有结束
    private void checkComplete(){
        boolean complete = true;
        ALL:
        for (int y = 0 ; y < 4 ; y++){
            for (int x = 0; x < 4; x++) {

                if(cardsMap[x][y].getNum() == 0 || //<span style="white-space:pre"></span>
                        (x>0 && cardsMap[x][y].equals(cardsMap[x-1][y]))||  //往左判断 数值相同，游戏没结束
                        (x<3 && cardsMap[x][y].equals(cardsMap[x+1][y]))||  //往右判断 
                        (y>0 && cardsMap[x][y].equals(cardsMap[x][y-1]))||
                        (y>0 && cardsMap[x][y].equals(cardsMap[x][y-1]))||
                        (y<3 && cardsMap[x][y].equals(cardsMap[x][y+1]))){
                    complete = false;
                    break ALL;
                }

            }
        }


        if(complete){

            new AlertDialog.Builder(getContext()).setTitle("你好").setMessage("游戏结束").setPositiveButton("重来", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    startGame();
                }
            }).show();
        }
    }


    //获取音效
    private void game_music(){
        if (MainActivity.getMainActivity().getSoundMap()!=null){
            MainActivity.getMainActivity().getSoundPool().play(MainActivity.getMainActivity().getSoundMap().get(1),1, 1, 0, 0, 1);
        }else{
            Toast.makeText(getContext(),"正在加载音效.......",Toast.LENGTH_SHORT);
        }

    }
 }
