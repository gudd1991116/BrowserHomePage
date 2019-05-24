package com.gudong.browser.browserhomepage.behavior;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.gudong.browser.browserhomepage.R;

/**
 * Desc:头部behavior[包括背景，包括天气，搜索框，名站和今日头条]
 * Author:gudongdong
 */
public class HeaderBehavior extends CoordinatorLayout.Behavior<View> {

    private Context mContext;
    private Scroller mScroller;
    private Handler mHandler;
    private boolean isScrolling = false;
    private View child;

    public HeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        this.mHandler = new Handler();
        this.mScroller = new Scroller(context);
    }

    @Override
    public boolean onMeasureChild(@NonNull CoordinatorLayout parent, @NonNull View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {

        int itemHeight = 0;
        int topSiteViewMarginHeight = 0;
        int childViewHeight = 0;

        NestedScrollView nestedScrollView = (NestedScrollView) child;
        int nestedChildCount = nestedScrollView.getChildCount();

        for (int z = 0; z < nestedChildCount; z++) {
            final View nestedChildView = nestedScrollView.getChildAt(z);
            Log.d("ddd", "第一层for，z ...... nestedChildView != null && nestedChildView instanceof Relativelayout ?  --> " + (nestedChildView != null && nestedChildView instanceof RelativeLayout));
            if (nestedChildView != null && nestedChildView instanceof RelativeLayout) {
                RelativeLayout relativeLayout = (RelativeLayout) nestedChildView;
                int childCount = relativeLayout.getChildCount();
                Log.d("ddd", "childCount == " + childCount);
                for (int i = 0; i < childCount; i++) {
                    final View childView = relativeLayout.getChildAt(i);
                    // 计算子View的高度和margin高度
                    RelativeLayout.LayoutParams rlpc = (RelativeLayout.LayoutParams) childView.getLayoutParams();
                    int ctm = rlpc.topMargin;
                    int cbm = rlpc.bottomMargin;
                    int chm = rlpc.height;
                    childViewHeight = childViewHeight + ctm + cbm + chm;

//                    final View childView = child.getChildAt(i);
                    Log.d("ddd", "第二层for,i....... childView != null && childView instanceof RecyclerView ？--》  " + (childView != null && childView instanceof RecyclerView));
                    if (childView != null && childView instanceof RecyclerView) {
                        RecyclerView recyclerView = (RecyclerView) childView;
                        // 计算获取recyclerview的margin的高度（包括top/bottom）
                        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
                        int topMargin = rlp.topMargin;
                        int bottomMargin = rlp.bottomMargin;
                        topSiteViewMarginHeight = topMargin + bottomMargin;// recyclerview的margin的高度

                        RecyclerView.Adapter adapter = recyclerView.getAdapter();
                        Log.d("ddd", "recyclerview.getadapter == null? " + (recyclerView.getAdapter()));
                        if (adapter != null) {
                            int itemCount = adapter.getItemCount();
                            for (int j = 0; j < itemCount; j++) {
                                if (j % 4 == 0) {
                                    View childAt = recyclerView.getChildAt(j);
                                    if (childAt != null) {
                                        Log.d("ddd", "child item height : " + childAt.getMeasuredHeight());
                                        itemHeight += childAt.getMeasuredHeight();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 当前view的最大高度 = 头部高度 + 名站高度 + 名站margin高度 + 今日头条高度
//        int totalHeight = (int) (getHeaderHeight() + itemHeight + topSiteViewMarginHeight + getToplineHeight());
        int totalHeight = childViewHeight + itemHeight;

        int heightSpec = View.MeasureSpec.makeMeasureSpec(totalHeight, View.MeasureSpec.EXACTLY);
        child.measure(parentWidthMeasureSpec, heightSpec);


        return true;
    }

    /*@Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 && child == target;
    }*/

    /*@Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
//        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        if (dy < 0) {
            return;
        }

        // 上滑
        float childScrollTY = child.getTranslationY() - dy;
        float minChildScrollOffset = -(child.getHeight() - getSearchViewHeight());

        if (childScrollTY > minChildScrollOffset) {
            child.setTranslationY(childScrollTY);
            consumed[1] = dy;
        }


    }*/

   /* @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        float childScrollOffset = child.getTranslationY() - dyUnconsumed;
        float maxChildScrollOffset = 0;
        float minChildScrollOffset = -(child.getHeight() - getSearchViewHeight());
        if (dyUnconsumed > 0) {// 上推
            Log.i("ddd", "dyUnconsumed > 0 : " + dyUnconsumed);
            if (childScrollOffset > minChildScrollOffset) {
                Log.i("ddd", "childScrollOffset > minChildScrollOffset and child.getTranslationY() - dyUnconsumed = " + (child.getTranslationY() - dyUnconsumed));
                child.setTranslationY(child.getTranslationY() - dyUnconsumed);
            }else{
                child.setTranslationY(minChildScrollOffset);
            }
        }else if(dyUnconsumed < 0){// 下拉
            Log.i("ddd", "dyUnconsumed < 0 : " + dyUnconsumed);
            if (childScrollOffset < maxChildScrollOffset) {
                Log.i("ddd", "childScrollOffset < maxChildScrollOffset ---(child.getTranslationY() - dyUnconsumed) = " + (child.getTranslationY() - dyUnconsumed));
                child.setTranslationY(child.getTranslationY() - dyUnconsumed);
            }else{
                child.setTranslationY(0);
            }
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
            float childTY = child.getTranslationY();
            float headerScrollDistance = getHeaderHeight() - getSearchViewHeight();
            float anchorPoint = -(headerScrollDistance / 3);// 获取上下滚动的分界点 小于这个点则向上滚动，大于这个点向下滚动
//            Log.e("ddd", "anchorPoint = " + anchorPoint);
            if (childTY == 0 || childTY == -headerScrollDistance){
                return;
            }
            float velocityY = 800.f;// 速度800
            boolean isUpWared;
            if (childTY < anchorPoint) {// 让child向上滚动
                isUpWared = true;
            }else{
                isUpWared = false;
            }

            if (childTY < 0 && childTY > -headerScrollDistance) {
                float targetTranslationY = isUpWared ? (-headerScrollDistance) : 0;
                mScroller.startScroll(0,(int)childTY,0,(int) (targetTranslationY - childTY),(int) velocityY);
                mHandler.post(new FlingRunnable(child));
                isScrolling = true;
            }
//            onFingerStopDrag(child,800);
        }
    }
    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, float velocityX, float velocityY) {
        Log.e("ddd", " velocityY : " + velocityY);
        return onFingerStopDrag(child,velocityY);
    }

    private boolean onFingerStopDrag(View child, float velocityY) {
//        View dependentView = getDependentView();
        float dependentViewTranslationY = child.getTranslationY();
        float minDependentViewScrollOffset = -(getHeaderHeight() - getSearchViewHeight());// 自动滑动最小值 minDependentViewScrollOffset = -430
        Log.e("ddd", "dependent View TranslationY ---= " + child.getTranslationY() + "   min Dependent view scroll offset ----: " + minDependentViewScrollOffset);
        if (dependentViewTranslationY == 0 || dependentViewTranslationY == minDependentViewScrollOffset) {
            return false;
        }
        boolean targetState;
        if (Math.abs(velocityY) <= 800) {
            Log.e("ddd", " Math.abs(velocityY) <= 800 velocityY = "+ velocityY);
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
        }

        if (dependentViewTranslationY > minDependentViewScrollOffset && dependentViewTranslationY<0) {
//        if (dependentViewTranslationY > minDependentViewScrollOffset && dependentViewTranslationY < -1) {
            float targetTranslationY = targetState ? minDependentViewScrollOffset : 0;
            mScroller.startScroll(0, (int) dependentViewTranslationY, 0, (int) (targetTranslationY - dependentViewTranslationY), (int) (1000000 / Math.abs(velocityY)));
//            mScroller.startScroll(0, (int) dependentViewTranslationY, 0, (int) (targetTranslationY - dependentViewTranslationY), (int)  Math.abs(velocityY));
            mHandler.post(new FlingRunnable(child));
            isScrolling = true;
            return true;
        }
        return false;
    }

    class FlingRunnable implements Runnable{
        View child;
        public FlingRunnable(View child) {
            this.child = child;
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                child.setTranslationY(mScroller.getCurrY());
                mHandler.post(this);
            } else {
                isScrolling = false;
            }
        }
    }*/


    // 获取搜索框的高度
    private float getSearchViewHeight() {
        return mContext.getResources().getDimension(R.dimen.home_header_searchview_height);
    }

    private float getHeaderHeight() {
        return mContext.getResources().getDimension(R.dimen.home_header_height);
    }

    private float getToplineHeight() {
        return mContext.getResources().getDimension(R.dimen.home_body_header_topline_height);
    }
}
