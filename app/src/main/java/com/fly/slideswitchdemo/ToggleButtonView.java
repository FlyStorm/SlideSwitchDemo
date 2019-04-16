package com.fly.slideswitchdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by 杨艳飞 on 2019/4/14 15:28.
 * 作用：自定义View实现滑动开关
 */
public class ToggleButtonView extends View {
    /**
     * 背景是一个Bitmap，定义背景图片
     */
    private Bitmap mBacground;

    private Bitmap mSlideImage;

    /**
     * 标记是否是在触摸控件
     * 因为当手指触摸滑块的时候会调用onTouchEvent方法，而
     * 滑动的时候要操作滑块，进行绘制，就需要调用onDraw
     * 方法，这个两个要交互，中间就要通过标志来做
     */
    private boolean isTouching = false;

    /**
     * 标记控件是否是打开的状态，默认是false，关闭的
     */
    private boolean isOpened;
    private int mCurrentX;

    //实例化接口对象
    private OnToggleStateChangedListener mListener;

    public ToggleButtonView(Context context) {
        super(context);
    }

    public ToggleButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 曝露一个方法，设置滑块的背景
     *
     * @param resId
     */
    public void setSwitchBackground(int resId) {
        mBacground = BitmapFactory.decodeResource(getResources(), resId);
    }

    /**
     * 设置滑块的图片
     */
    public void setSlideImage(int resId) {
        mSlideImage = BitmapFactory.decodeResource(getResources(), resId);
    }

    /**
     * 设置当前滑块的状态
     * 因为默认是关闭的状态，这个时候就需要曝露这个方法
     * @param isOpened
     */
    public void setCurrentState(boolean isOpened){
        this.isOpened=isOpened;

        //触发绘制
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredWidth = mBacground.getWidth();
        int measuredHeight = mBacground.getHeight();
        /**
         * 自定义控件，如果继承自View，一定要调用这个方法！
         */
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    /**
     * 背景和滑块两个东西不一样，肯定要重写onDraw方法
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画背景
        if (mBacground != null) {
            canvas.drawBitmap(mBacground, 0, 0, null);
        }

        //画滑块
        /*if (mSlideImage != null) {
            canvas.drawBitmap(mSlideImage, 0, 0, null);
        }*/
        if (mSlideImage == null) {
            return;
        }


        int slideImageWidth = mSlideImage.getWidth();
        int bacgroundWidth = mBacground.getWidth();

        //当触摸控件时，如果控件在关闭的状态下，点击滑块左侧，绘制成不动的样子。点击滑块右侧，滑块
        //的中线和触摸的X坐标一致
        if (isTouching) {//当触摸控件的时候
            if (!isOpened) {//假如是关闭的状态
                //按下的坐标currentX和滑块的一半做比较
                int mSlideImageWidth = mSlideImage.getWidth();
                if (mCurrentX < mSlideImageWidth / 2) {//假如点击的是滑块的左侧
                    //绘制成不动的样子
                    canvas.drawBitmap(mSlideImage, 0, 0, null);
                }else if (mCurrentX>bacgroundWidth-slideImageWidth/2){//越界问题：如果我们触摸的位置大于这个位置的left，就不滑动
                    canvas.drawBitmap(mSlideImage,bacgroundWidth-slideImageWidth,0,null);
                } else {//点击的是滑块的右侧，滑块的中轴线和触摸的X坐标一致
                    canvas.drawBitmap(mSlideImage, mCurrentX - mSlideImageWidth / 2, 0, null);
                }

            } else {//假如是打开的状态
                //假如当前x的坐标在滑块的右侧，那么就不动
                if (bacgroundWidth-slideImageWidth/2<mCurrentX) {
                    canvas.drawBitmap(mSlideImage, bacgroundWidth - slideImageWidth, 0, null);
                }else if (mCurrentX<slideImageWidth/2){
                    //假如当前X的坐标小于滑块一半的时候就关闭（解决打开状态下越界的问题）
                    canvas.drawBitmap(mSlideImage, 0, 0, null);
                } else {//否则就滑动
                    canvas.drawBitmap(mSlideImage, mCurrentX - slideImageWidth/2, 0, null);
                }
            }
        }else {//没有触摸
            if(isOpened){//打开状态
                canvas.drawBitmap(mSlideImage,bacgroundWidth-slideImageWidth,0,null);
            }else {//关闭状态
                canvas.drawBitmap(mSlideImage, 0, 0, null);
            }
        }
    }

    //如果拖动控件，滑块就需要重新绘制

    /**
     * 一个完整的touch会经历三个过程，按下、移动、离开
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //手指按下，触发一次
                isTouching = true;

                //获得当前的坐标，0.5f代表四舍五入
                mCurrentX = (int) (event.getX() + 0.5f);

                break;
            case MotionEvent.ACTION_MOVE:
                //手指移动，可以没有
                //获得当前的坐标，0.5f代表四舍五入
                mCurrentX = (int) (event.getX() + 0.5f);
                break;
            case MotionEvent.ACTION_UP:
                //手指离开，触发一次
                isTouching = false;

                int backHalf=mBacground.getWidth()/2;
                int slidHalf=mSlideImage.getWidth()/2;

                //如果滑块的中轴线在背景的中轴线左边，就关闭，否则就打开。如果本身是关闭的，再关闭就没意义
                if (mCurrentX<backHalf && isOpened){
                    //关闭
                    isOpened=false;

                    //松开的时候状态会变
                    if (mListener!=null){
                        mListener.onStateChanged(this,isOpened);
                    }
                }else if (mCurrentX>=backHalf && !isOpened){
                    //打开
                    isOpened=true;
                    //松开的时候状态会变
                    if (mListener!=null){
                        mListener.onStateChanged(this,isOpened);
                    }
                }

                break;
            default:
                break;
        }
        invalidate();
        //消费touch事件
        return true;
    }

    public void setOnToggleStateChangedListener(OnToggleStateChangedListener listener)
    {
        this.mListener=listener;
    }

    /**
     * 一般接口是把自己给曝露出去
     * 在拖动的时候，什么时候打开了，什么时候关闭了，就需要做一个监听，曝露一个监听的接口
     */
    public interface  OnToggleStateChangedListener{
        void onStateChanged(ToggleButtonView view,boolean state);
    }
}
