package com.uestc.magicwo.livecampus.index;


import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinuscxj.itemdecoration.GridOffsetsItemDecoration;
import com.liji.circleimageview.CircleImageView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.BaseRequest;
import com.uestc.magicwo.livecampus.R;
import com.uestc.magicwo.livecampus.models.RoomBaseInfoResponse;
import com.uestc.magicwo.livecampus.models.RoomInfoResponse;
import com.uestc.magicwo.livecampus.models.RoomList;
import com.uestc.magicwo.livecampus.net.BaseResponse;
import com.uestc.magicwo.livecampus.net.JsonCallback;
import com.uestc.magicwo.livecampus.net.Urls;
import com.uestc.magicwo.livecampus.videoplayer.NEVideoPlayerActivity;
import com.uestc.magicwo.livecampus.BaseApplication;
import com.uestc.magicwo.livecampus.appbase.AppBaseFragment;
import com.uestc.magicwo.livecampus.videostreaming.PrepareLiveActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;


public class IndexFragment extends AppBaseFragment {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.assort)
    TextView assort;
    @BindView(R.id.head_image_view)
    CircleImageView headImageView;
    @BindView(R.id.head_layout)
    LinearLayout headLayout;


    private IndexAdapter indexAdapter;
    private List<RoomBaseInfoResponse> datas;

    private OnStartDrawerListener mOnStartDrawerListener;

    public void setOnStartDrawerListener(OnStartDrawerListener onStartDrawerListener) {
        mOnStartDrawerListener = onStartDrawerListener;
    }

    @Override
    public int setContentLayout() {
        return R.layout.fragment_first;
    }

    @Override
    public void initView() {
        init_refresh();

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        datas = new ArrayList<>();
        indexAdapter = new IndexAdapter(getActivity(), datas);
        indexAdapter.setOnItemClickListener(new IndexAdapter.OnItemClickListener() {


            @Override
            public void onItemClick(int position, String data) {
//                Toast.makeText(getActivity(), quId, Toast.LENGTH_SHORT).show();
                getRoomUrl(data);

            }
        });
        recyclerView.setAdapter(indexAdapter);
        GridOffsetsItemDecoration offsetsItemDecoration = new GridOffsetsItemDecoration(
                GridOffsetsItemDecoration.GRID_OFFSETS_HORIZONTAL);
        offsetsItemDecoration.setVerticalItemOffsets(15);
        offsetsItemDecoration.setHorizontalItemOffsets(15);
        recyclerView.addItemDecoration(offsetsItemDecoration);
        headImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnStartDrawerListener != null) {
                    mOnStartDrawerListener.openDrawer();
                }
            }
        });

    }

    private void getRoomUrl(String rid) {
//        OkGo.get(Urls.ROOMINFO + "?id=" + rid + "&type=rid")
//                .tag(this)
//                .execute(new JsonCallback<BaseResponse<RoomInfoResponse>>() {
//                    @Override
//                    public void onBefore(BaseRequest request) {
//                        super.onBefore(request);
//                        Toast.makeText(getActivity(), "正在获取房间信息", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        super.onError(call, response, e);
//                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onSuccess(BaseResponse<RoomInfoResponse> roomInfoResponseBaseResponse, Call call, Response response) {
//                        Log.e("-----------直播间信息", roomInfoResponseBaseResponse.getRet().getRtmpPullUrl());
//                        Intent intent = new Intent(getActivity(), NEVideoPlayerActivity.class);
////                intent.putExtra("quId", quId);
////                intent.putExtra("position", position);
//                        intent.putExtra("media_type", BaseApplication.mediaType);
//                        intent.putExtra("decode_type", BaseApplication.decodeType);
//                        intent.putExtra("videoPath", roomInfoResponseBaseResponse.getRet().getRtmpPullUrl());
//                        startActivity(intent);
//                    }
//                });
        Intent intent = new Intent(getActivity(), NEVideoPlayerActivity.class);
        intent.putExtra("media_type", BaseApplication.mediaType);
        intent.putExtra("decode_type", BaseApplication.decodeType);
        if (rid.equals("1")) {
            intent.putExtra("videoPath", "rtmp://v4622ebf4.live.126.net/live/0209abf2449a4fe5ba9fafa90298f4e5");
        }
        if (rid.equals("2")) {
            intent.putExtra("videoPath", "rtmp://v4622ebf4.live.126.net/live/29528fa6d9714d12b4edab590efd51c0");
        }
        if (rid.equals("3")) {
            intent.putExtra("videoPath", "rtmp://v4622ebf4.live.126.net/live/1c33a2fbfac74978b10caa7e9ef250f5");
        }
        intent.putExtra("Rid", rid);
        startActivity(intent);
    }

    private void init_refresh() {
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.app_color));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getRooms();
            }

        });

    }

    @Override
    public void loadData() {
        getRooms();

//        List<String> urls = new ArrayList<>();
//        urls.add("rtmp://v4622ebf4.live.126.net/live/eb54c7f13acc4de3b269a80df9ff76f2");
//        urls.add("http://flv4622ebf4.live.126.net/live/eb54c7f13acc4de3b269a80df9ff76f2.flv?netease=flv4622ebf4.live.126.net");
//        urls.add("http://pullhls4622ebf4.live.126.net/live/eb54c7f13acc4de3b269a80df9ff76f2/playlist.m3u8");
//        urls.add("rtmp://live.hkstv.hk.lxdns.com/live/hks");
//        urls.add("http://202.102.79.114:554/live/tvb8.stream/playlist.m3u8");
//        urls.add("http://119.147.242.8:14207");
//        urls.add("rtsp://116.199.127.68/huayu");
//
//        List<String> imageUrls = new ArrayList<>();
//        imageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1495313669918&di=6d006ff7300ee4bf6fcd4c389bb95c39&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fgamephotolib%2F1506%2F29%2Fc0%2F9042653_1435551074453_medium.jpg");
//        imageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1495313669916&di=bbc98eac35e753069ffed96d3d90e0cc&imgtype=0&src=http%3A%2F%2Fimg5.cache.netease.com%2Fphoto%2F0031%2F2014-09-22%2FA6OSM4HJ3S010031.jpg");
//        imageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1495313669916&di=5346bbbc915fb2be7aa1f677ee92ad3c&imgtype=0&src=http%3A%2F%2Fpic.cdn.zhanqi.tv%2Froom_desc%2F92194%2F20160103123804_84171.jpg");
//        imageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1495313669915&di=7a94fc2f9cb7cb4a55fb070701833cff&imgtype=0&src=http%3A%2F%2Fpic.cdn.zhanqi.tv%2Froom_desc%2F18259%2F20150102161822_31705.jpg");
//        imageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1495315562331&di=274bfdf8c42e269676fc57ce08a7a180&imgtype=0&src=http%3A%2F%2Fwww.feizl.com%2Fupload2007%2F2014_07%2F140705161355342.jpg");
//        imageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1495313904737&di=02d332d0981c4e63484a464621af1de0&imgtype=0&src=http%3A%2F%2Fimg1.utuku.china.com%2Fuploadimg%2Fgame%2F20161222%2F6b077f98-0e80-4cea-b1d0-c20eefcc4823.jpg");
//        imageUrls.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1495313963224&di=393582f15c7c170280844bf1c36b09f2&imgtype=0&src=http%3A%2F%2Fc.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2F4034970a304e251feffaea65a586c9177f3e5319.jpg");
//
//        List<String> nicknames = new ArrayList<>();
//        nicknames.add("Magicwo");
//        nicknames.add("Duelist");
//        nicknames.add("Yuchen_Chiang");
//        nicknames.add("西原");
//        nicknames.add("宅羊");
//
//        nicknames.add("繁花落幕");
//        nicknames.add("snow bamboo");
//
//        List<String> contens = new ArrayList<>();
//        contens.add("来一起玩毒奶粉啊");
//        contens.add("守望先锋");
//        contens.add("最爱田馥甄");
//        contens.add("boom 瞎卡拉卡");
//        contens.add("成电第一帅哥");
//        contens.add("小姐姐我可爱吗");
//        contens.add("Coding中的妹纸收割机");
//
//
//        int[] audienceNum = {11000, 600, 2000, 500, 3000, 9998, 9999};
//
//
//        for (int i = 0; i < urls.size(); i++) {
//            IndexResponse indexResponse = new IndexResponse();
//            indexResponse.setVideoUrl(urls.get(i));
//            indexResponse.setImageUrl(imageUrls.get(i));
//            indexResponse.setTitle(contens.get(i));
//            indexResponse.setNickname(nicknames.get(i));
//            indexResponse.setAudience_num(audienceNum[i]);
//            indexResponse.setuId("hhhsdasdads");
//            datas.add(indexResponse);
//
//        }
//        for (int i = 0; i < urls.size(); i++) {
//            IndexResponse indexResponse = new IndexResponse();
//            indexResponse.setVideoUrl(urls.get(i));
//            indexResponse.setImageUrl(imageUrls.get(i));
//            indexResponse.setTitle(contens.get(i));
//            indexResponse.setNickname(nicknames.get(i));
//            indexResponse.setAudience_num(audienceNum[i]);
//            indexResponse.setuId("hhhsdasdads");
//            datas.add(indexResponse);
//
//        }
//        for (int i = 0; i < urls.size(); i++) {
//            IndexResponse indexResponse = new IndexResponse();
//            indexResponse.setVideoUrl(urls.get(i));
//            indexResponse.setImageUrl(imageUrls.get(i));
//            indexResponse.setTitle(contens.get(i));
//            indexResponse.setNickname(nicknames.get(i));
//            indexResponse.setAudience_num(audienceNum[i]);
//            indexResponse.setuId("hhhsdasdads");
//            datas.add(indexResponse);
//
//        }
//        for (int i = 0; i < urls.size(); i++) {
//            IndexResponse indexResponse = new IndexResponse();
//            indexResponse.setVideoUrl(urls.get(i));
//            indexResponse.setImageUrl(imageUrls.get(i));
//            indexResponse.setTitle(contens.get(i));
//            indexResponse.setNickname(nicknames.get(i));
//            indexResponse.setAudience_num(audienceNum[i]);
//            indexResponse.setuId("hhhsdasdads");
//            datas.add(indexResponse);
//
//        }
//        indexAdapter.notifyDataSetChanged();


    }

    /**
     * 获取房间
     */
    private void getRooms() {
//        OkGo.get(Urls.ROOMLIST + "?records=" + 200 + "&pnum=" + 1)
//                .tag(this)
//                .execute(new JsonCallback<BaseResponse<RoomList>>() {
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        super.onError(call, response, e);
//                        refreshLayout.setRefreshing(false);
//                    }
//
//                    @Override
//                    public void onSuccess(BaseResponse<RoomList> roomListBaseResponse, Call call, Response response) {
//                        datas.clear();
//                        datas.addAll(roomListBaseResponse.getRet().getList());
//                        indexAdapter.notifyDataSetChanged();
//                        refreshLayout.setRefreshing(false);
//                    }
//                });
        datas.clear();
        RoomBaseInfoResponse roomBaseInfoResponse = new RoomBaseInfoResponse();
        roomBaseInfoResponse.setDescription("Alice的直播间");
        roomBaseInfoResponse.setUsername("Alice");
        roomBaseInfoResponse.setRid("1");
        roomBaseInfoResponse.setCover("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498160494473&di=33a7f471ec4f1758989fb8b22e8a7183&imgtype=0&src=http%3A%2F%2Ftupian.enterdesk.com%2F2013%2Fmxy%2F10%2F12%2F2%2F4.jpg");
        datas.add(roomBaseInfoResponse);
        RoomBaseInfoResponse roomBaseInfoResponse2 = new RoomBaseInfoResponse();
        roomBaseInfoResponse2.setDescription("Bob的直播间");
        roomBaseInfoResponse2.setUsername("Bob");
        roomBaseInfoResponse2.setRid("2");
        roomBaseInfoResponse2.setCover("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1498160494471&di=30a5af615ee7ef76f93e69d63f7b43e1&imgtype=0&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2Fa%2F568cd27741af5.jpg");
        datas.add(roomBaseInfoResponse2);
        RoomBaseInfoResponse roomBaseInfoResponse3 = new RoomBaseInfoResponse();
        roomBaseInfoResponse3.setDescription("Root的直播间");
        roomBaseInfoResponse3.setUsername("Root");
        roomBaseInfoResponse3.setRid("3");
        datas.add(roomBaseInfoResponse3);
        indexAdapter.notifyDataSetChanged();
        refreshLayout.setRefreshing(false);


    }


    public interface OnStartDrawerListener {

        void openDrawer();
    }


}
