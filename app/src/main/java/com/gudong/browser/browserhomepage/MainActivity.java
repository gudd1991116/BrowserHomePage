package com.gudong.browser.browserhomepage;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gudong.browser.browserhomepage.behavior.NewsFlowBehavior;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Context mContext;
    RecyclerView news_flow_recyclerview;
    RecyclerView sites_view;
    ViewPager viewpager;
    NestedScrollView nested_header;
    NestedScrollView news_flow_nsv;

    NewsFlowBehavior newsFlowBehavior = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mContext = this;

        initView();

        initData();
    }

    private void initData() {
        List<String> strings = new ArrayList<>();
        for (int i = 0;i< 20;i++) {
            strings.add("i = " + i);
        }

        MyTopSiteAdapter topSiteAdapter = new MyTopSiteAdapter(strings);
        sites_view.setLayoutManager(new GridLayoutManager(this,4));
        sites_view.setHasFixedSize(true);
        sites_view.setAdapter(topSiteAdapter);
        topSiteAdapter.notifyDataSetChanged();


        List<View> views = new ArrayList<>();
        for (int i = 0;i<4;i++) {
            TextView textView = new TextView(this);
            textView.setText("position = " + i);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(20);
            textView.setTextColor(Color.parseColor("#000000"));
            textView.setBackgroundColor(Color.parseColor("#ffffff"));
            views.add(textView);
        }
        MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter(views);
        viewpager.setAdapter(viewPagerAdapter);


        List<String> strings1 = new ArrayList<>();
        for (int i = 0;i< 50;i++) {
            strings1.add(" i = " + i);
        }

        MyNewsFlowAdapter newsFlowAdapter = new MyNewsFlowAdapter(strings1);
        news_flow_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        news_flow_recyclerview.setHasFixedSize(true);
        news_flow_recyclerview.setAdapter(newsFlowAdapter);
        newsFlowAdapter.notifyDataSetChanged();
    }

    private void initView() {
        nested_header = findViewById(R.id.nested_header);
        news_flow_nsv = findViewById(R.id.news_flow_nsv);
        nested_header.setSmoothScrollingEnabled(false);
        news_flow_recyclerview = findViewById(R.id.news_flow_recyclerview);
        sites_view = findViewById(R.id.sites_view);
        viewpager = findViewById(R.id.viewpager);
        initBehavior();
    }

    private void initBehavior() {
        newsFlowBehavior = (NewsFlowBehavior) ((CoordinatorLayout.LayoutParams)news_flow_nsv.getLayoutParams()).getBehavior();
        if (newsFlowBehavior == null) {
            return;
        }
        newsFlowBehavior.setOnPagerStateListener(new NewsFlowBehavior.OnPagerStateListener() {
            @Override
            public void onPagerClosed() {
//                newsFlowBehavior.setCouldScrollOpen(false);
//                Toast.makeText(mContext, "close", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPagerOpened() {
//                newsFlowBehavior.setCouldScrollOpen(true);
//                Toast.makeText(mContext, "open", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (!newsFlowBehavior.isTop()) {
            newsFlowBehavior.scrollToTop();
        }else{
            super.onBackPressed();
        }
    }

    class MyTopSiteAdapter extends RecyclerView.Adapter<MyTopSiteAdapter.MyHolder>{

        List<String> strings;
        public MyTopSiteAdapter(List<String> strings) {
            this.strings = strings;
        }

        @NonNull
        @Override
        public MyTopSiteAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MyHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.top_site_item,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyTopSiteAdapter.MyHolder myHolder, int i) {
            myHolder.list_item.setText(strings.get(i));
        }

        @Override
        public int getItemCount() {
            return strings.size();
        }

        class MyHolder extends RecyclerView.ViewHolder{
            TextView list_item;
            public MyHolder(@NonNull View itemView) {
                super(itemView);
                list_item = itemView.findViewById(R.id.textview);
            }
        }
    }

    class MyNewsFlowAdapter extends RecyclerView.Adapter<MyNewsFlowAdapter.MyHolder>{

        List<String> strings;
        public MyNewsFlowAdapter(List<String> strings) {
            this.strings = strings;
        }

        @NonNull
        @Override
        public MyNewsFlowAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new MyHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.recyclerview_item,viewGroup,false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyNewsFlowAdapter.MyHolder myHolder, int i) {
            myHolder.list_item.setText(strings.get(i));
        }

        @Override
        public int getItemCount() {
            return strings.size();
        }

        class MyHolder extends RecyclerView.ViewHolder{
            TextView list_item;
            public MyHolder(@NonNull View itemView) {
                super(itemView);
                list_item = itemView.findViewById(R.id.item);
            }
        }
    }

    class MyViewPagerAdapter extends PagerAdapter{
        List<View> views;
        public MyViewPagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(views.get(position));
//            super.destroyItem(container, position, object);
        }
    }


}
