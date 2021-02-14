package com.example.pong;
import android.graphics.RectF;

public class Ball {
    private RectF mRect;
    private float mXVelocity;
    private float mYVelocity;
    private float mBallWidth;
    private float mBallHeight;

    Ball(int screenX){
    // Make the ball square and 1% of screen width of the screen width
        mBallWidth = screenX / 100;
        mBallHeight = screenX / 100;

    // Initialize the RectF with 0, 0, 0, 0
        mRect = new RectF();
    }
    RectF getRect(){
        return mRect;
    }
    void update(long fps){
// Move the top left corner
        mRect.left = mRect.left + (mXVelocity / fps);
        mRect.top = mRect.top + (mYVelocity / fps);

// Match up the bottom right corner based on the size of the ball
        mRect.right = mRect.left + mBallWidth;
        mRect.bottom = mRect.top + mBallHeight;
    }
    // Reverse the vertical direction of travel
    void reverseYVelocity(){

        mYVelocity = -mYVelocity;

    }

    // Reverse the horizontal direction of travel
    void reverseXVelocity(){

        mXVelocity = -mXVelocity;

    }
    void reset(int x, int y){
// Initialise the four points of the rectangle which defines the ball
        mRect.left = x / 2;
        mRect.top = 0;
        mRect.right = x / 2 + mBallWidth;
        mRect.bottom = mBallHeight;

// How fast will the ball travel
// You could even increase it as the game progresses to make it harder
        mYVelocity = -(y / 3);
        mXVelocity = (y / 3);
    }
    void increaseVelocity(){

// increase the speed by 10%
        mXVelocity = mXVelocity * 1.1f;
        mYVelocity = mYVelocity * 1.1f;

    }
    void batBounce(RectF batPosition){
// Detect centre of bat
        float batCenter = batPosition.left + (batPosition.width() / 2);
// Detect the centre of the ball
        float ballCenter = mRect.left + (mBallWidth / 2);
// Where on the bat did the ball hit?
        float relativeIntersect = (batCenter - ballCenter);
// Pick a bounce direction
        if(relativeIntersect < 0){
// Go right
            mXVelocity = Math.abs(mXVelocity);
        }else{
// Go left
            mXVelocity = -Math.abs(mXVelocity);
        }
// Simply reverse the vertical direction to go back up the screen
        reverseYVelocity();
    }
}

