package com.example.linyuming.pintu;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity{
    /**
     * 当前移动动画是否在执行
     * */
    private boolean isAnimRun = false;
    /*判断游戏是否开始*/
    private boolean isGameStart = false;
    /**
     * 利用二维数组创建若干个小方块
     **/
    private ImageView[][] iv_game_arr = new ImageView[3][5];
    /*游戏主界面*/
    private GridLayout gl_main_game;
  /*当前方块的实例保存*/
    private ImageView iv_null_ImageView;
    //当前手势
    private GestureDetector mDetector;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
               int type = getDirByGes(e1.getX(),e1.getY(),e2.getX(),e2.getY());
               // Toast.makeText(MainActivity.this," "+ type,Toast.LENGTH_SHORT).show();
                changeByDir(type);
                return false;
            }
        });
        setContentView(R.layout.activity_main);
        /*初始化若干个小方块*/
        Bitmap bigBm = ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_game_tu)).getBitmap(); //获取一张大图
        int tuWanH = bigBm.getWidth() / 5;//每个游戏小方块的宽和高
        int ivWandH = getWindowManager().getDefaultDisplay().getWidth()/5;
        for (int i = 0; i < iv_game_arr.length; i++) {
            for (int j = 0; j < iv_game_arr[0].length; j++) {
                //根据行和列来切成若干个小方块
                Bitmap bm = Bitmap.createBitmap(bigBm, j * tuWanH, i * tuWanH, tuWanH, tuWanH);
                iv_game_arr[i][j] = new ImageView(this);
                iv_game_arr[i][j].setImageBitmap(bm);
                iv_game_arr[i][j].setLayoutParams(new RelativeLayout.LayoutParams(ivWandH,ivWandH));
                iv_game_arr[i][j].setPadding(2, 2, 2, 2);//设置方块之间的距离
                iv_game_arr[i][j].setTag(new GameData(i,j,bm));//绑定自定义的数据
                iv_game_arr[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       boolean flag = isHasByNullImageView((ImageView)v);
                     // Toast.makeText(MainActivity.this,"位置关系是否存在："+flag,Toast.LENGTH_SHORT).show();
                        if (flag){
                            changeDataByImageView((ImageView) v);
                        }
                    }
                });
            }

        }
        /*初始化游戏主界面，并添加若干个小方块*/
        gl_main_game = (GridLayout) findViewById(R.id.gl_main_game);
        for (int i = 0; i < iv_game_arr.length; i++) {
            for (int j = 0; j < iv_game_arr[0].length; j++) {
                gl_main_game.addView(iv_game_arr[i][j]);
            }
        }
        setNullImageView(iv_game_arr[2][4]);
        //初始化随机打乱顺序
        randomMove();
        isGameStart =true;//开始状态
}
    public void changeByDir(int type){
        changeByDir( type ,true);
    }
    //根据手势的方向，获取空方块相应的相邻位置如果存在方块，那么进行数据交换
    public void changeByDir(int type,boolean isAnim){
       //获取当前空方块的位置
       GameData mNullGameData = (GameData) iv_null_ImageView.getTag();
        //根据方向，设置相应的相邻的位置的坐标
        int new_x = mNullGameData.x;
        int new_y = mNullGameData.y;
        if (type==1){//要移动的方块在当前空方块的下边
            new_x++;
        }else if (type==2){
            new_x--;
        }else if (type==3){
           new_y++;
        }else if (type==4){
        new_y--;
    }
        //判断这个新坐标，是否存在
   if (new_x >= 0 &&new_x<iv_game_arr.length&&new_y>=0&&new_y<iv_game_arr[0].length){
      //存在的话，开始移动
       if (isAnim){
           changeDataByImageView(iv_game_arr[new_x][new_y]);
       }else{
           changeDataByImageView(iv_game_arr[new_x][new_y],isAnim);
       }
   }else{
//什么也不做
   }
    }
    //判断游戏结束的方法
    public void isGameOver(){
        boolean isGameOver= true;
        //要遍历每个游戏小方块
      for(int i = 0 ; i<iv_game_arr.length;i++){
          for (int j= 0;j<iv_game_arr[0].length;j++){
           //为的空的方块数据不判断跳过
              if (iv_game_arr[i][j]==iv_null_ImageView){
                  continue;
              }
              GameData  mGameData = (GameData) iv_game_arr[i][j].getTag();
              if (!mGameData.isTrue()){
              isGameOver = false;
                  break;
              }
          }
        }


        //根据一个开关变量决定游戏是否结束，结束时给提示
        if (isGameOver){
            Toast.makeText(this,"厉害了，游戏结束",Toast.LENGTH_SHORT).show();
        }
    }
    /*
    *手势判断，是向左滑，还是向右滑
    * start_x 手势的起始点x
    * start_Y  手势的起始点y
    * end_x  手势的终止点x
    * end_y  手势的终止点y
    * return 1：上 2：下 3：左 4：右
    */
    public int getDirByGes(float start_x,float start_y,float end_x,float end_y){
    boolean isLeftOrRight = (Math.abs(start_x - end_x )>Math.abs(start_y - end_y))?true:false;//是否左右
        if (isLeftOrRight){//左右
            boolean isLeft = start_x - end_x >0 ? true:false;
            if (isLeft){
                return 3;
            }else {
                return 4;
            }
        }else{
            boolean isUp = start_y - end_y > 0 ? true :false;
            if (isUp){
                return 1;
            }else{
                return 2;
            }
        }
    }
    //随机打乱顺序
    public void randomMove(){
        //打乱的次数
        for (int i = 0;i <100;i++){
            //根据手势开始交换，无动画
            int type =(int)(Math.random()*4)+1;
            changeByDir(type,false);
        }
    }
    public void changeDataByImageView(final ImageView mImageView){
        changeDataByImageView(mImageView,true);
    }
    /**
     * 利用动画结束之后，交换两个方块的数据
     * */
    public void changeDataByImageView(final ImageView mImageView, final boolean isAnim){
            if (isAnimRun){
                return;
            }
        if (!isAnim){
            mImageView.clearAnimation();
            GameData mGameData = (GameData) mImageView.getTag();
            iv_null_ImageView.setImageBitmap(mGameData.bm);
            GameData mNullGameData = (GameData) iv_null_ImageView.getTag();
            mNullGameData.bm = mGameData.bm;
            mNullGameData.p_x = mGameData.p_x;
            mNullGameData.p_y =mGameData.p_y;
            setNullImageView(mImageView); //设置当前点击的是空方块
            if(isGameStart){
                isGameOver(); //成功时，弹出一个Toast
            }
            return;
        }
   //创建一个动画，设置好方向，移动的距离
        TranslateAnimation translateAnimation = null;
        if (mImageView.getX()>iv_null_ImageView.getX()){
            //往上移
            translateAnimation = new TranslateAnimation(0.1f,-mImageView.getWidth(),0.1f,0.1f);
        }else if (mImageView.getX()<iv_null_ImageView.getX()){
            //往下移
            translateAnimation = new TranslateAnimation(0.1f,mImageView.getWidth(),0.1f,0.1f);
        }else if (mImageView.getY()>iv_null_ImageView.getY()) {
            //往左移
            translateAnimation = new TranslateAnimation(0.1f, 0.1f , 0.1f, -mImageView.getWidth());
        } else if (mImageView.getY()<iv_null_ImageView.getY()) {
            //往右移
            translateAnimation = new TranslateAnimation(0.1f, 0.1f, 0.1f, mImageView.getWidth());
        }
        //设置动画时长
        translateAnimation.setDuration(70);
        //设置动画结束之后是否停留
        translateAnimation.setFillAfter(true);
        //设置动画结束之后要真正的把数据交换了
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                   isAnimRun = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimRun = false;
                mImageView.clearAnimation();
                GameData mGameData = (GameData) mImageView.getTag();
                iv_null_ImageView.setImageBitmap(mGameData.bm);
                GameData mNullGameData = (GameData) iv_null_ImageView.getTag();
                mNullGameData.bm = mGameData.bm;
                mNullGameData.p_x = mGameData.p_x;
                mNullGameData.p_y =mGameData.p_y;
                setNullImageView(mImageView); //设置当前点击的是空方块
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //执行动画
        mImageView.startAnimation(translateAnimation);
    }
    //设置某个方块为空方块
    public void setNullImageView(ImageView mImageView) {
           mImageView.setImageBitmap(null);
           iv_null_ImageView = mImageView;
    }
    //判断当前点击的方块，是否与空方块的位置关系是相邻关系
    public boolean isHasByNullImageView(ImageView mImageView){
        //分别获取当前空方块的位置与点击方块的位置，通过想，y两边都差1
       GameData mNullGameData  = (GameData) iv_null_ImageView .getTag();
        GameData mGameData = (GameData) mImageView.getTag();
        if(mNullGameData.y==mGameData.y&&mGameData.x+1==mNullGameData.x) {//当前点击的方块在空方块的上方
           return  true;
        }else if(mNullGameData.y==mGameData.y&&mGameData.x-1==mNullGameData.x){//当前点击的方块在空方块的下方
            return  true;
        }else if(mNullGameData.y==mGameData.y+1&&mGameData.x==mNullGameData.x){//当前点击的方块在空方块的左方
            return  true;
        }else if(mNullGameData.y==mGameData.y-1&&mGameData.x==mNullGameData.x){//当前点击的方块在空方块的右方
            return  true;
        }
        return false;
    }
    /**每个游戏小方块上要绑定的数据*/
    class GameData{
        /**每个小方块的实际位置x*/
        public int x = 0;
        /**每个小方块的实际位置x*/
        public int y = 0;
        /**每个小方块的图片*/
        public Bitmap bm;
        /**每个小方块的图片的位置*/
        public int p_x = 0;
        /**每个小方块的位置图片*/
        public int p_y = 0;

        public GameData(int x, int y, Bitmap bm) {
            this.x = x;
            this.y = y;
            this.bm = bm;
            this.p_x = x;
            this.p_y = y;
        }

        public boolean isTrue() {
            if (x == p_x&& y == p_y){
                return  true;
            }
            return  false;
        }
    }
}

