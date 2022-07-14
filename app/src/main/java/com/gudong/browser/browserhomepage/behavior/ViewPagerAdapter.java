package com.gudong.browser.browserhomepage.behavior;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

class ViewPagerAdapter extends PagerAdapter {
    List<RecyclerView> recyclerViews ;
    public ViewPagerAdapter(List<RecyclerView> recyclerViews) {
        this.recyclerViews = recyclerViews;
    }

    @Override
    public int getCount() {
        return recyclerViews.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(recyclerViews.get(position));
        return recyclerViews.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(recyclerViews.get(position));
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "page "+(position+1);
    }
}
