package cn.imustacm.heartbeat.fragment;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.imustacm.heartbeat.R;
import cn.imustacm.heartbeat.adapter.CardAdapter;
import cn.imustacm.heartbeat.base.BaseApplication;
import cn.imustacm.heartbeat.base.BaseFragment;
import cn.imustacm.heartbeat.entity.Card;
import cn.imustacm.heartbeat.entity.LikeOrHate;
import cn.imustacm.heartbeat.entity.User;
import cn.imustacm.heartbeat.ui.CardDetailActivity;
import cn.imustacm.heartbeat.utils.LogUtils;

/**
 * 项目名：HeartBeat
 * 包名：cn.imustacm.heartbeat.fragment
 * 文件名：HomePageFragment
 * 描述：主页
 */

public class HomePageFragment extends BaseFragment implements OnItemSwipeListener, OnRefreshListener, BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.RequestLoadMoreListener {

    // 卡片适配器数据
    private List<Card> adapterDataList;
    // 全部卡片数据
    private List<Card> cardAllDataList;
    // 载入卡片数据
    private List<Card> cardDataList;
    // 全部数据条数
    private int maxCardNum;
    // 当前已经加载的条数
    private int loadCardNum;
    // 滚动视图
    private RecyclerView recyclerView;
    // 卡片适配器
    private CardAdapter cardAdapter;
    // 第一次载入
    private boolean isFirst = true;

    @Override
    public void onStart() {
        super.onStart();
        // 第一次进入时进行刷新
        if (isFirst) {
            loadCard(null, true);
            isFirst = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, null);

        adapterDataList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.recycler_view);

        cardAdapter = new CardAdapter(R.layout.card_layout, adapterDataList);

        recyclerView.setAdapter(cardAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(cardAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        final RefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new MaterialHeader(getContext()).setShowBezierWave(true));
        refreshLayout.setEnableLoadMore(false);

        // 设置动画效果（从右滑入）
        cardAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);
        // 开启滑动删除功能
        cardAdapter.enableSwipeItem();
        // 设置加载更多（上滑）监听器
        cardAdapter.setOnLoadMoreListener(this, recyclerView);
        // 设置刷新（下滑）监听器
        refreshLayout.setOnRefreshListener(this);
        // 设置删除监听器
        cardAdapter.setOnItemSwipeListener(this);
        // 设置点击监听器
        cardAdapter.setOnItemClickListener(this);

        return view;
    }

    // 滑动监听器
    @Override
    public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
        if (cardAdapter.getData().size() == 1) {
            cardAdapter.addData(cardAdapter.getData().get(viewHolder.getAdapterPosition()));
        }
    }

    @Override
    public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {

    }

    @Override
    public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
        final Card cardData = cardAdapter.getData().get(pos);
        final User user = User.getCurrentUser(User.class);
        // 对该卡片进行不喜欢操作时先进行查询，防止数据重复插入
        BmobQuery<LikeOrHate> query = new BmobQuery<>();
        query.addWhereEqualTo("user", user);
        query.addWhereEqualTo("card", cardData);
        query.findObjects(new FindListener<LikeOrHate>() {
            @Override
            public void done(List<LikeOrHate> list, BmobException e) {
                if (e == null) {
                    // 查询成功
                    if (list.size() == 0) {
                        // 该用户没有对该卡片做出过评价，插入一条评价
                        LikeOrHate likeOrHate = new LikeOrHate(user, cardData, 0);
                        // 保存
                        likeOrHate.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    // 保存成功，不进行任何处理
                                    LogUtils.e("HomePageFragment...done(String, BmobException):" + "成功插入一条LikeOrHate数据");
                                } else {
                                    // 保存失败
                                    LogUtils.e("HomePageFragment...done(String, BmobException):" + e.getErrorCode());
                                }
                            }
                        });
                    } else {
                        // 该用户已经对该卡片做出过评价，不需要进行任何处理
                    }
                } else {
                    // 查询失败
                    LogUtils.e("HomePageFragment...done(List<>, BmobException):" + e.getErrorCode());
                }
            }
        });
    }

    @Override
    public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {

    }

    // 加载监听器
    @Override
    public void onLoadMoreRequested() {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cardDataList.size() >= cardAllDataList.size()) {
                    Toast.makeText(getContext(), "没有更多动态", Toast.LENGTH_LONG).show();
                    cardAdapter.loadMoreEnd();
                } else {
                    Card newData = cardAllDataList.get(cardDataList.size());
                    cardDataList.add(newData);
                    cardAdapter.addData(newData);
                    cardAdapter.loadMoreComplete();
                }
            }
        }, 300);
    }

    // 刷新监听器
    @Override
    public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
        loadCard(refreshLayout, false);
    }

    private void loadCard(final RefreshLayout refreshLayout, final boolean isFirst) {

        BmobQuery<Card> query = new BmobQuery<>();
        query.include("fromUser");
        query.findObjects(new FindListener<Card>() {
            @Override
            public void done(List<Card> list, BmobException e) {
                if (e == null) {
                    LogUtils.e("查询成功，共查询到" + list.size() + "条记录");
                    Collections.shuffle(list, new Random());
                    int loadNum = Math.min(5, list.size());
                    cardAllDataList = new ArrayList<Card>(list);
                    adapterDataList = new ArrayList<Card>(cardAllDataList.subList(0, loadNum));
                    cardDataList = new ArrayList<Card>(adapterDataList);
                    cardAdapter.setNewData(adapterDataList);
                    if (!isFirst) {
                        refreshLayout.finishRefresh(1200);
                    }
                } else {
                    LogUtils.e("查询失败");
                    if (!isFirst) {
                        refreshLayout.finishRefresh(1200, false);
                    }
                }
            }
        });
    }

    // 点击监听器
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        final Card temp = (Card) adapter.getData().get(position);
        // 查询该用户对于该卡片的态度
        BmobQuery<LikeOrHate> query = new BmobQuery<>();
        query.addWhereEqualTo("user", User.getCurrentUser(User.class).getObjectId());
        query.addWhereEqualTo("card", temp.getObjectId());
        query.include("user,card.fromUser");
        query.findObjects(new FindListener<LikeOrHate>() {
            @Override
            public void done(List<LikeOrHate> list, BmobException e) {
                if (e == null) {
                    final Intent intent = new Intent(getActivity(), CardDetailActivity.class);
                    if (list.size() == 0) {
                        // 无该用户对该卡片的评价
                        final LikeOrHate likeOrHate = new LikeOrHate(User.getCurrentUser(User.class), temp, 0);
                        likeOrHate.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    // 上传成功
                                    intent.putExtra("State", likeOrHate);
                                    startActivity(intent);
                                } else {
                                    // 上传失败
                                    LogUtils.e("HomePageFragment...done(String, BmobException):" + e.getErrorCode());
                                }
                            }
                        });
                    } else {
                        // 有该用户对该卡片的评价
                        LikeOrHate temp = list.get(0);
                        final LikeOrHate likeOrHate = new LikeOrHate(temp.getUser(), temp.getCard(), temp.getState());
                        likeOrHate.setObjectId(temp.getObjectId());
                        intent.putExtra("State", likeOrHate);
                        startActivity(intent);
                    }
                } else {
                    LogUtils.e("HomePageFragment...done(List<>, BmobException):" + e.getErrorCode());
                }
            }
        });
    }
}
