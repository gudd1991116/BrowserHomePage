package com.gudong.browser.browserhomepage.behavior;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

import com.gudong.browser.browserhomepage.R;

import java.lang.ref.WeakReference;

/**
 * Desc:新闻流Behavior
 * Author:gudongdong
 */
public class NewsFlowBehavior extends CoordinatorLayout.Behavior<View> {
    public static final int STATE_OPENED = 0;
    public static final int STATE_CLOSED = 1;

    private Context mContext;
    private OnPagerStateListener mOnpagerStateListener;
    private WeakReference<View> dependency;
    private WeakReference<View> child;

    private Scroller mScroller;
    private Handler mHandler;
    private boolean isScrolling = false;
    private int mCurState = STATE_OPENED;
    private boolean isCouldScrollOpen = true;

    public NewsFlowBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mHandler = new Handler();
        this.mScroller = new Scroller(context);
    }

    @Override
    public boolean onMeasureChild(@NonNull CoordinatorLayout parent, @NonNull View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {

        // 获取屏幕高度
        int screenHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec);
        // 计算child的最大高度
        // child最大高度 = 屏幕高度 - 搜索框高度 - 底部菜单高度
        int maxChildHeight = (int) (screenHeight - getSearchViewHeight() - getBottombarHeight());

        parentHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxChildHeight, View.MeasureSpec.EXACTLY);

        child.measure(parentWidthMeasureSpec, parentHeightMeasureSpec);

        return true;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {

        float scale = 1.f - Math.abs(dependency.getTranslationY() / dependency.getHeight());

        // 初始化位置 = dependency的高度 - searchview的高度
        float initHeight = dependency.getHeight();

        float childTranslationY = scale * initHeight;

        if (dependency.getTranslationY() <= -(dependency.getHeight() - getSearchViewHeight())) {
            child.setTranslationY(getSearchViewHeight());
        } else {
            child.setTranslationY(childTranslationY);
        }
        return true;
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        if (dependency != null && dependency.getId() == R.id.nested_header) {
            this.dependency = new WeakReference<>(dependency);
            this.child = new WeakReference<>(child);
            return true;
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        Log.w("ddd", "onStartNestedScroll.....");
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        Log.e("ddd", "up............");
        View dependency = getDependency();

        float dty = dependency.getTranslationY();
        float minHeaderScrollDistance = -(dependency.getMeasuredHeight() - getSearchViewHeight());

        // 防界面快速滑动抖动-针对fling的情况
        if (type == ViewCompat.TYPE_NON_TOUCH && (dty == 0 || dty <= minHeaderScrollDistance )) {
            ViewCompat.stopNestedScroll(target, type);
        }

        if (dy < 0) {
            return;
        }

        float dTranslationY = dependency.getTranslationY() - dy;
        float dMinTranslationY = -(dependency.getHeight() - getSearchViewHeight());
        if (dTranslationY > dMinTranslationY) {
            dependency.setTranslationY(dTranslationY);
            consumed[1] = dy;
        } else {
            dependency.setTranslationY(dMinTranslationY);
        }
        onFlingFinished(child);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        Log.e("ddd", "down.............");

        if (!isCouldScrollOpen) {
            if (isClosed(child)) {
                return;
            }
        }

        if (dyUnconsumed > 0) {
            return;
        }

        View dependency = getDependency();
        float dTranslationY = dependency.getTranslationY() - dyUnconsumed;
        float dMaxTranslationY = 0;
        if (dTranslationY < dMaxTranslationY) {
            dependency.setTranslationY(dTranslationY);
        } else {
            dependency.setTranslationY(0);
        }
    }

    @Override
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        mScroller.abortAnimation();
        isScrolling = false;
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int type) {
        // 如果手指从屏幕上拿开且并没有一个抛的动作
        if (!isScrolling) {
            float dependencyTY = getDependency().getTranslationY();
            float headerScrollDistance = getHeaderHeight() - getSearchViewHeight();
            float anchorPoint = -(headerScrollDistance / 3);// 获取上下滚动的分界点 小于这个点则向上滚动，大于这个点向下滚动
            Log.e("ddd", "anchorPoint = " + anchorPoint);
            if (dependencyTY == 0 || dependencyTY == -headerScrollDistance) {
                Log.e("ddd", "(dependencyTY == 0 || dependencyTY == -headerScrollDistance) ");
                return;
            }
            Log.e("ddd", "~~~~~~~~~~~~~~~~~~~~~ ");
            float velocityY = 800.f;// 速度800
            // true: 让dependency view向上滚动
            boolean isUpWared = dependencyTY < anchorPoint;

            FlingRunnable flingRunnable = new FlingRunnable(child);

            if (dependencyTY < 0 && dependencyTY > -headerScrollDistance) {
                float targetTranslationY = isUpWared ? (-headerScrollDistance) : 0;
                mScroller.startScroll(0, (int) dependencyTY, 0, (int) (targetTranslationY - dependencyTY), (int) velocityY);
                mHandler.post(flingRunnable);
                isScrolling = true;
            } else {
                float dependencyMeasureHeight = getDependency().getMeasuredHeight();// dependency 的实际高度
                float minDependencyScrollTY = -(dependencyMeasureHeight - getSearchViewHeight());// dependency 可滑动到的最小位置
                float dependencyScrollDistance = dependencyMeasureHeight - getToplinesheight() - getSearchViewHeight();
                float toplineScrollAnchorPoint = getToplinesheight() / 3;// 今日头条高度的三分之一，用作于dependency自动滑动的锚点
                float dependencyAutoScrollLocation = -dependencyScrollDistance - toplineScrollAnchorPoint;// dependency 开始自动滑动的位置

                // true:让dependency view 自动上移，将child移动到searchview下方
                // false：让dependency view 自动下移，将topline移出来
                boolean isToplineUpwared = dependencyTY < dependencyAutoScrollLocation;

                if (dependencyTY < -dependencyScrollDistance && dependencyTY > minDependencyScrollTY) {
                    float dependencyTranslationY = isToplineUpwared ? minDependencyScrollTY : -dependencyScrollDistance;
                    mScroller.startScroll(0, (int) dependencyTY, 0, (int) (dependencyTranslationY - dependencyTY), (int) velocityY);
                    mHandler.post(flingRunnable);
                    isScrolling = true;
                }
            }
//            onFingerStopDrag(child,800);
        }
    }

   /* @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, float velocityX, float velocityY) {
        Log.e("ddd", " velocityY : " + velocityY);
        return onFingerStopDrag(child, velocityY);
    }*/

    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        Log.e("ddd", "onnestedFling......");
        /*if (!ViewCompat.isNestedScrollingEnabled(getDependency())){
            getDependency().setScrollY((int) velocityY);
        }*/
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    private boolean onFingerStopDrag(View child, float velocityY) {
        View dependentView = getDependency();
        float dependentViewTranslationY = dependentView.getTranslationY();
        float minDependentViewScrollOffset = -(getHeaderHeight() - getSearchViewHeight());// 自动滑动最小值 minDependentViewScrollOffset = -430
        Log.e("ddd", "dependent View TranslationY ---= " + dependentView.getTranslationY() + "   min Dependent view scroll offset ----: " + minDependentViewScrollOffset);
        if (dependentViewTranslationY == 0 || dependentViewTranslationY == minDependentViewScrollOffset) {
            return false;
        }
        boolean targetState;
       /* if (Math.abs(velocityY) <= 800) {
            Log.e("ddd", " Math.abs(velocityY) <= 800 velocityY = " + velocityY);
            if (Math.abs(dependentViewTranslationY) < Math.abs(dependentViewTranslationY - minDependentViewScrollOffset)) {
                targetState = false;
            } else {
                targetState = true;
            }
//            velocityY = 800;
        } else {

            if (velocityY > 0) {
                targetState = true;
            } else {
                targetState = false;
            }
        }*/

        boolean isUpward = velocityY > 0;

        if (dependentViewTranslationY > minDependentViewScrollOffset && dependentViewTranslationY < 0) {
           /* mScroller.fling(0,(int) dependentViewTranslationY,0,(int) velocityY,0,0,(int) minDependentViewScrollOffset,0);
            mHandler.post(flingRunnable);
            isScrolling = true;
            return true;*/
//            float targetTranslationY = targetState ? minDependentViewScrollOffset : 0;
//            mScroller.startScroll(0, (int) dependentViewTranslationY, 0, (int) (targetTranslationY - dependentViewTranslationY), (int) (1000000 / Math.abs(velocityY)));
            float targetTranslationY = isUpward ? minDependentViewScrollOffset : 0;

            mScroller.startScroll(0, (int) dependentViewTranslationY, 0, (int) (targetTranslationY - dependentViewTranslationY), (int) (1000000 / Math.abs(velocityY)));
            mHandler.post(new FlingRunnable(child));
            isScrolling = true;
            return true;
        }
        return false;
    }


    /*Runnable flingRunnable = new Runnable() {
        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                getDependency().setTranslationY(mScroller.getCurrY());
                mHandler.post(this);
            } else {
                isScrolling = false;
//                onFlingFinished();
            }
        }
    };*/

    private class FlingRunnable implements Runnable {
        private final View mlayout;

        public FlingRunnable(View layout) {
            this.mlayout = layout;
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                getDependency().setTranslationY(mScroller.getCurrY());
                mHandler.post(this);
            } else {
                isScrolling = false;
                onFlingFinished(mlayout);
            }
        }
    }

    /**
     * 将首页滚动到顶部
     * 页面将自上向下自动滑动
     */
    public void scrollToTop() {
        isCouldScrollOpen = true;
        View dependency = getDependency();
        float dependencyCurTY = dependency.getTranslationY();
        float maxDependencyScrollLocation = 0.f;// 最大滚动到的位置
        float minDependentViewScrollOffset = -(getHeaderHeight() - getSearchViewHeight());// 自动滑动最小值 minDependentViewScrollOffset = -430

        if (dependencyCurTY < maxDependencyScrollLocation) {
            mScroller.startScroll(0, (int) dependencyCurTY, 0, (int) (maxDependencyScrollLocation-dependencyCurTY), 1000);
            mHandler.post(new FlingRunnable(getChild()));
            isScrolling = true;
        }
    }

    /**
     * 设置是否可以滑动打开
     *
     * @param isCouldScrollOpen
     */
    public void setCouldScrollOpen(boolean isCouldScrollOpen) {
        this.isCouldScrollOpen = isCouldScrollOpen;
    }

    private void onFlingFinished(View layout) {
        changeState(isClosed(layout) ? STATE_CLOSED : STATE_OPENED);
    }

    /**
     * 头部view是否关闭了
     *
     * @return
     */
    private boolean isClosed(View child) {
        boolean isClosed = child.getTranslationY() == getSearchViewHeight();
        return isClosed;
    }

    public boolean isClosed() {
        return mCurState == STATE_CLOSED;
    }

    private void changeState(int newState) {
        if (mCurState != newState) {
            mCurState = newState;
            if (mOnpagerStateListener != null) {
                if (mCurState == STATE_OPENED) {
                    mOnpagerStateListener.onPagerOpened();
                } else {
                    mOnpagerStateListener.onPagerClosed();
                }
            }
        }
    }


    private float getHeaderHeight() {
        return mContext.getResources().getDimension(R.dimen.home_header_height);
    }

    private float getSearchViewHeight() {
        return mContext.getResources().getDimension(R.dimen.home_header_searchview_height);
    }

    private float getBottombarHeight() {
        return mContext.getResources().getDimension(R.dimen.home_bottombar_height);
    }

    private float getTablayoutHeight() {
        return mContext.getResources().getDimension(R.dimen.home_tablayout_height);
    }

    private float getToplinesheight() {
        return mContext.getResources().getDimension(R.dimen.home_body_header_topline_height);
    }

    private View getDependency() {
        return dependency.get();
    }

    private View getChild() {
        return child.get();
    }

    public void setOnPagerStateListener(OnPagerStateListener onPagerStateListener) {
        this.mOnpagerStateListener = onPagerStateListener;
    }

    /**
     * callback for headerPager's state
     */
    public interface OnPagerStateListener {
        void onPagerClosed();

        void onPagerOpened();
    }

}
