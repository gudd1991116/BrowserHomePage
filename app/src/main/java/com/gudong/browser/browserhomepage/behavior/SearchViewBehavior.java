package com.gudong.browser.browserhomepage.behavior;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;


import com.gudong.browser.browserhomepage.R;

import java.lang.ref.WeakReference;

/**
 * Desc:搜索框behavior
 * Author:gudongdong
 */
public class SearchViewBehavior extends CoordinatorLayout.Behavior<View> {
    private Context mContext;
    private WeakReference<View> dependentView;
    private Scroller mScroller;
    private Handler mHandler;
    private ArgbEvaluator mArgbEvaluator;


    public SearchViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mHandler = new Handler();
        this.mScroller = new Scroller(context);
        this.mArgbEvaluator = new ArgbEvaluator();
    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        if (dependency != null && dependency.getId() == R.id.nested_header) {
            dependentView = new WeakReference<>(dependency);
            return true;
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {

        float scrollDistanceScale = 1.f - Math.abs(dependency.getTranslationY() / (getHeaderHeight() - getSearchViewHeight()));
        Log.i("gudd", "searchViewBehavior scrollDistanceScale : " + scrollDistanceScale);
        float childInitHeight = getHeaderHeight() - getSearchViewHeight();
        float childTranslationY = childInitHeight * scrollDistanceScale;
        if (dependency.getTranslationY() <= -(getHeaderHeight() - getSearchViewHeight())) {
            child.setTranslationY(0);
            setChildAnimator(0, child);
        } else {
            child.setTranslationY(childTranslationY);
            setChildAnimator(scrollDistanceScale, child);
        }

        return true;
    }

    /**
     * 设置search textview 的联动动画
     *
     * @param scrollProgress 滑动进度
     * @param child          当前的searchview部分
     */
    private void setChildAnimator(float scrollProgress, View child) {
        // 设置child背景透明色 从完全透明到完全不透明
        child.setBackgroundColor((int) mArgbEvaluator.evaluate(
                scrollProgress,
                child.getResources().getColor(R.color._f2f2f2),
                child.getResources().getColor(R.color._00ffffff)));

        View subChild = getSubChildOfChild(child);
        if (subChild != null && subChild instanceof CardView) {
            // 设置cardview
            subChild.setBackgroundColor((int) mArgbEvaluator.evaluate(
                    scrollProgress,
                    child.getResources().getColor(R.color._ffffff),
                    child.getResources().getColor(R.color._80ffffff)));


            // 设置margin
            float collapsedMargin = subChild.getResources().getDimension(R.dimen.home_search_collapsing_margin);
            // 设置marginleft or right值
            float initMargin = subChild.getResources().getDimension(R.dimen.home_search_view_initmargin);
            int margin = (int) (collapsedMargin + (initMargin - collapsedMargin) * scrollProgress);

            LinearLayout.LayoutParams cl = (LinearLayout.LayoutParams) subChild.getLayoutParams();
            int marginTop = cl.topMargin;
            int marginBottom = cl.bottomMargin;
            cl.setMargins(margin, marginTop, margin, marginBottom);
            subChild.setLayoutParams(cl);

            setSearchTextViewAnimator(scrollProgress, subChild,child);
        }

    }

    private View getSubChildOfChild(View child) {
        // 获取child 里面search textview 的视图
        if (null != child && child instanceof LinearLayout) {
            LinearLayout childOfLL = (LinearLayout) child;
            int childCount = childOfLL.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    return childOfLL.getChildAt(i);
                }
            }
        }
        return null;
    }

    /**
     *
     * @param scrollProgress 滑动进度
     * @param subChild 此入为child的第一层子view   cardview
     * @param child
     */
    private void setSearchTextViewAnimator(float scrollProgress, View subChild, View child) {
//        View subView = getSubChildOfChild(child);
        if (null != subChild & subChild instanceof CardView) {
            CardView cardView = (CardView) subChild;
            int childCount = cardView.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    View childView = cardView.getChildAt(i);
                    if (null != childView && childView instanceof TextView) {
                        TextView textView = (TextView) childView;
                        int textMeasureWidth = textView.getMeasuredWidth();
                        // 计算child的宽度
                        int childMeasuredWidth = child.getMeasuredWidth();
                        // 获取当前view所在父容器中的距左距离
                        int initMargin = (childMeasuredWidth - textMeasureWidth) / 2;
                        float subMarginLeft = textView.getResources().getDimension(R.dimen.home_search_collapsing_margin);

                        LinearLayout.LayoutParams cl = (LinearLayout.LayoutParams)cardView.getLayoutParams();
                        float cardviewMarginLeft = cl.leftMargin;

                        int margin = (int) (subMarginLeft + (initMargin - cardviewMarginLeft - subMarginLeft) * scrollProgress);
                        Log.i("ddd", "textMeasureWidth : "+textMeasureWidth+"   childMeasuredWidth : "+childMeasuredWidth+"  scrollProgress : "+scrollProgress +"  margin : " + margin);
//                        textView.setTranslationX(margin);
                        CardView.LayoutParams ll = (CardView.LayoutParams) textView.getLayoutParams();
                        ll.setMargins(margin,ll.topMargin,ll.rightMargin,ll.bottomMargin);
                        textView.setLayoutParams(ll);
                    }
                }
            }
        }
    }

    // 获取从searchview底部往上的到屏幕的高度
    private float getHeaderHeight() {
        return mContext.getResources().getDimension(R.dimen.home_header_height);
    }

    // 搜索框的高度
    private float getSearchViewHeight() {
        return mContext.getResources().getDimension(R.dimen.home_header_searchview_height);
    }
}
