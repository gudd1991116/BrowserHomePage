# BrowserHomePage

## 使用Coordinatorlayout Behavior 实现的一个浏览器首页界面  

<img src="https://github.com/gudd1991116/Raw/blob/master/BrowserHomePage/5ogjp-r31pi.gif" width="30%"/>    <img src="https://github.com/gudd1991116/Raw/blob/master/BrowserHomePage/tt4il-id1x5.gif" width="30%"/>  

#### 监听首页头部的展开与关闭需要实现接口：
```java
    public interface OnPagerStateListener {
        void onPagerClosed();

        void onPagerOpened();
    }
```
#### 调用这个接口需要拿到使用NewsFlowBehavior类的实例，如何拿呢？请看：
* 首先需要找到使用该Behavior的View
* 根据找到的View取得其使用的Behavior
* 通过获取的Behavior设置监听事件

```
  <android.support.v4.widget.NestedScrollView
        android:id="@+id/news_flow_nsv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:fillViewport="true"
        app:layout_behavior="@string/header_newsflow_behavior">  //此处使用了NewsFlowBehavior 

        <android.support.v7.widget.RecyclerView
            android:id="@+id/news_flow_recyclerview"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
    </android.support.v4.widget.NestedScrollView>
```
```
  NestedScrollView news_flow_nsv = findViewById(R.id.news_flow_nsv);
  NewsFlowBehavior newsFlowBehavior = (NewsFlowBehavior) ((CoordinatorLayout.LayoutParams)news_flow_nsv.getLayoutParams()).getBehavior();
  newsFlowBehavior.setOnPagerStateListener(new NewsFlowBehavior.OnPagerStateListener() {
            @Override
            public void onPagerClosed() {
                newsFlowBehavior.setCouldScrollOpen(false);// 控制头部不能下拉显示出来
            }

            @Override
            public void onPagerOpened() {
                newsFlowBehavior.setCouldScrollOpen(true);// 控制头部可以下拉显示出来
            }
        });
```
