package com.example.gesturenavigator.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class DrawingCanvasView extends View {
    public Paint paint;
    public Path path;
    public Bitmap mBitmap;
    public Canvas mCanvas;
    private int height;

    private int width;

    public DrawingCanvasView(Context context){
        super(context);
    }

    public DrawingCanvasView(Context context, AttributeSet attrs){
        super(context,attrs);
        //mBitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        height = h;
        width = w;
        init();
    }

    private void init(){
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        path = new Path();
        paint = new Paint();
        updatePaint();
    }

    private void updatePaint(){
        paint.setStrokeWidth(15f);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }


    @Override
    protected void onDraw(Canvas canvas){
        //canvas.drawBitmap(mBitmap, 0, 0, paint);
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        canvas.drawPath(path,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX,eventY);
                return true;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(eventX,eventY);
                break;
            case MotionEvent.ACTION_UP:
                mCanvas.drawPath(path, paint);
                path.reset();
                break;
               default: return false;
        }

        invalidate();
        return true;
    }


    public void clearScreen() {
        if (mCanvas != null) {
            Paint backPaint = new Paint();
            backPaint.setColor(Color.WHITE);
            mCanvas.drawRect(new Rect
                    (0, 0, width, height), backPaint);
        }
        invalidate();
    }

    /*public float[] getPixelData() {
        if (mBitmap == null) {
            return null;
        }

        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();

        // Get 28x28 pixel data from bitmap
        int[] pixels = new int[width * height];
        mBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        float[] retPixels = new float[pixels.length];
        for (int i = 0; i < pixels.length; ++i) {
            // Set 0 for white and 255 for black pixel
            int pix = pixels[i];
            int b = pix & 0xff;
            retPixels[i] = (float)((0xff - b)/255.0);
        }
        return retPixels;
    }*/
}
