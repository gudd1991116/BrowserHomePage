package com.gudong.browser.browserhomepage.behavior;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Desc:头部behavior[包括背景，包括天气，搜索框，名站和今日头条]
 * Author:gudongdong
 */
public class HeaderBehavior extends CoordinatorLayout.Behavior<View> {

    public HeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onMeasureChild(@NonNull CoordinatorLayout parent, @NonNull View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {

        int itemHeight = 0;
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
        int totalHeight = childViewHeight + itemHeight;

        int heightSpec = View.MeasureSpec.makeMeasureSpec(totalHeight, View.MeasureSpec.EXACTLY);
        child.measure(parentWidthMeasureSpec, heightSpec);
        return true;
    }
}
