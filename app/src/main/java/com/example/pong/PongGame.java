package com.example.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PongGame extends SurfaceView implements Runnable {

    private int mScreenX;
    private int mScreenY;

    private int mFontSize;
    private int mFontMargin;

    private int mScore;
    private int mLives;

    public Bat mBat;
    public Ball mBall;

    private SurfaceHolder mOurHolder;
    private Canvas mCanvas;
    private Paint mPaint;

    private final boolean DEBUGGING = true;
    private long mFPS;
    private final int MILLIS_IN_SECOND = 1000;

    private Thread mGameThread = null;
    private volatile boolean mPlaying;
    private boolean mPaused = true;


    public PongGame(Context context, int x, int y) {
        super(context);

        mScreenX = x;
        mScreenY = y;
        mFontSize = mScreenX / 20;
        mFontMargin = mScreenX / 75;
        mOurHolder = getHolder();
        mPaint = new Paint();
        mBall = new Ball(mScreenX);
        mBat = new Bat(mScreenX, mScreenY);
        startNewGame();
    }

    private void startNewGame() {
        mBall.reset(mScreenX, mScreenY);
        mScore = 0;
        mLives = 3;
    }
    private void draw() {
        if (mOurHolder.getSurface().isValid()) {
            mCanvas = mOurHolder.lockCanvas(); // Lock the canvas (graphics memory)
            mCanvas.drawColor(Color.argb(255, 26, 128, 182));
            mPaint.setColor(Color.argb(255, 255, 255, 255));
            mPaint.setTextSize(mFontSize);
            mCanvas.drawText("Score: " + mScore + " Lives: " + mLives,

                    mFontMargin, mFontSize, mPaint);
            mCanvas.drawRect(mBall.getRect(), mPaint);
            mCanvas.drawRect(mBat.getRect(), mPaint);
            if (DEBUGGING) {
                printDebuggingText();
            }
            mOurHolder.unlockCanvasAndPost(mCanvas);
        }
    }
    private void printDebuggingText() {
        int debugSize = mFontSize / 2;
        int debugStart = 150;
        mPaint.setTextSize(debugSize);
        mCanvas.drawText("FPS: " + mFPS , 10, debugStart + debugSize, mPaint);
    }

    @Override
    public void run() {
        while (mPlaying) {
            long frameStartTime = System.currentTimeMillis();
            if(!mPaused){
                update();
                detectCollisions();
            }
            draw();
            long timeThisFrame = System.currentTimeMillis() - frameStartTime;
            if (timeThisFrame > 0) {
                mFPS = MILLIS_IN_SECOND / timeThisFrame;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mPaused = false;
                if(motionEvent.getX() > mScreenX / 2){
                    mBat.setMovementState(mBat.RIGHT);
                }
                else{
                    mBat.setMovementState(mBat.LEFT);
                }
                break;
            case MotionEvent.ACTION_UP:
                mBat.setMovementState(mBat.STOPPED);
                break;
        }
        return true;
    }
    private void detectCollisions() {
        if(RectF.intersects(mBat.getRect(),mBall.getRect())) {
            mBall.batBounce(mBat.getRect());
//            mBall.increaseVelocity();
            float a = mBat.getRect().left;
            float b = mBat.getRect().right;
            float c = (b-a)/4;
            float x1 = a+c;
            float x2 = a+(3*c);
            float d = mBall.getRect().left;
            float e =mBall.getRect().left;
            float y =(d-e)/2;
            if(y < x2 && y >x1)
            {
                mScore+=50;
            }
            else {
                mScore += 10;
            }
        }
        if(mBall.getRect().bottom > mScreenY){
            mBall.reverseYVelocity();
            mLives--;
            if(mLives == 0){
                mPaused = true;
                startNewGame();
            }
        }
        if(mBall.getRect().top < 0){
            mBall.reverseYVelocity();
        }
        if(mBall.getRect().left < 0){
            mBall.reverseXVelocity();
        }

        if(mBall.getRect().right > mScreenX){
            mBall.reverseXVelocity();
//            mBall.increaseVelocity();
            mScore-=5;
        }

    }

    private void update() {
        mBall.update(mFPS);
        mBat.update(mFPS);
    }

    public void pause() {
        mPlaying = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }
    public void resume() {
        mPlaying = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }
}