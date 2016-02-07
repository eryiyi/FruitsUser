package com.lbins.FruitsUser.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.lbins.FruitsUser.R;
import com.lbins.FruitsUser.SecondApplication;
import com.lbins.FruitsUser.adapter.FruitShowListViewAdapter;
import com.lbins.FruitsUser.adapter.OnClickContentItemListener;
import com.lbins.FruitsUser.adapter.ViewPagerAdapter;
import com.lbins.FruitsUser.bean.BaseFragment;
import com.lbins.FruitsUser.bean.FruitBean;
import com.lbins.FruitsUser.data.AdSlide;
import com.lbins.FruitsUser.data.FruitBeanData;
import com.lbins.FruitsUser.handler.ImageHandler;
import com.lbins.FruitsUser.http.AsyncHttpResponseHandler;
import com.lbins.FruitsUser.http.HttpClientUtils;
import com.lbins.FruitsUser.http.HttpParams;
import com.lbins.FruitsUser.servieid.ServerId;
import com.lbins.FruitsUser.util.StringUtil;
import com.lbins.FruitsUser.view.PullToRefreshView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainPageFragment extends BaseFragment implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener,OnClickContentItemListener {
	private PullToRefreshView pulltorefreshscrollview;
	View view;
	ListView listView;
	ScrollView scrollView;
	private List<FruitBean> list;
	public ImageHandler handler2 = new ImageHandler(new WeakReference<MainPageFragment>(this));
	private static FruitShowListViewAdapter adapter;

	//导航
	private ViewPager viewpager;
	private ViewPagerAdapter adapterAd;
	private LinearLayout viewGroup;
	private ImageView dot, dots[];
	private Runnable runnable;
	private int autoChangeTime = 5000;
	private List<AdSlide> lists = new ArrayList<AdSlide>();
	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getListViewData();
		view = inflater.inflate(R.layout.mainpagefragment, null);
		pulltorefreshscrollview = (PullToRefreshView) view.findViewById(R.id.pulltorefreshscrollview);
		pulltorefreshscrollview.setOnHeaderRefreshListener(this);
		pulltorefreshscrollview.setOnFooterRefreshListener(this);
//		LayoutInflater inflater2 = LayoutInflater.from(getActivity());
		listView = (ListView) view.findViewById(R.id.mylv);
//		View header = LayoutInflater.from(getActivity()).inflate(R.layout.viewpagerlayout, null);
//		listView.addHeaderView(header);
		list = new ArrayList<FruitBean>();
		adapter = new FruitShowListViewAdapter(getActivity(), list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Intent intent = new Intent(getActivity(), CommodityDetailTest.class);
//				intent.putExtra("id", list.get(position).getProduct_id());
//				startActivity(intent);
			}
		});

		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage("请稍后");
		progressDialog.show();
		getadv();

		return view;
	}
	
	public void getadv(){
		HttpParams params = new HttpParams();
		HttpClientUtils.getInstance().post(ServerId.serveradress, ServerId.getadv, params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) throws JSONException {
				if(progressDialog != null){
					progressDialog.dismiss();
				}
				JSONArray jsonArray = jsonObject.optJSONArray("data");
				if (jsonArray != null) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject object = jsonArray.optJSONObject(i);
						lists.add(new AdSlide(object.getString("id"),object.getString("url"), object.getString("dateline"),object.getString("imgurl")));
					}

				}
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
				super.onSuccess(jsonObject);
			}
		});

	}
	
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		pulltorefreshscrollview.postDelayed(new Runnable() {

			@Override
			public void run() {
				
				pulltorefreshscrollview.onFooterRefreshComplete();
			}
		}, 1000);
	}
	
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		pulltorefreshscrollview.postDelayed(new Runnable() {

			@Override
			public void run() {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());
				String str = dateFormat.format(curDate);
				pulltorefreshscrollview.setLastUpdated("最近更新:" + str);
				pulltorefreshscrollview.onHeaderRefreshComplete();
			}
		}, 1000);
	}
	
	public void getListViewData(){
		HttpParams params = new HttpParams();
		params.put("user_name", SecondApplication.user_name);
		HttpClientUtils.getInstance().post(ServerId.serveradress, ServerId.lastproduct, params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject s) {
				List<FruitBean> list2  = new ArrayList<FruitBean>();
				if (StringUtil.isJson(s.toString())) {
					try {
						JSONObject jo = new JSONObject(s.toString());
						String code =  jo.getString("code");
						if(Integer.parseInt(code) == 200){
							FruitBeanData data = getGson().fromJson(s.toString(), FruitBeanData.class);
							list2.addAll(data.getData());
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				Message message = new Message();
				message.what = 123;
				message.obj = list2;
				handler.sendMessage(message);
			}
		});
	}
	
	Handler handler = new Handler(Looper.myLooper(), new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case 123:
				//list.clear();
				List<FruitBean> list2 = (List<FruitBean>) msg.obj;
				list.clear();
				list.addAll(list2);
				adapter.notifyDataSetChanged();
				break;
			case 1:
				initViewPager();
//				BitmapUtil.getInstance().download("http://115.29.208.113/", img1, imageView1);
//				BitmapUtil.getInstance().download("http://115.29.208.113/", img2, imageView2);
//				BitmapUtil.getInstance().download("http://115.29.208.113/", img3, imageView3);
				break;
			default:
				break;
			}
			return false;
		}
	});


	private void initViewPager() {
		adapterAd = new ViewPagerAdapter(getActivity());
		adapterAd.change(lists);
		adapterAd.setOnClickContentItemListener(this);
		viewpager = (ViewPager) view.findViewById(R.id.viewpager);
		viewpager.setAdapter(adapterAd);
		viewpager.setOnPageChangeListener(myOnPageChangeListener);
		initDot();
		runnable = new Runnable() {
			@Override
			public void run() {
				int next = viewpager.getCurrentItem() + 1;
				if (next >= adapterAd.getCount()) {
					next = 0;
				}
				viewHandler.sendEmptyMessage(next);
			}
		};
		viewHandler.postDelayed(runnable, autoChangeTime);
	}


	// 初始化dot视图
	private void initDot() {
		viewGroup = (LinearLayout) view.findViewById(R.id.viewGroup);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				10, 10);
		layoutParams.setMargins(4, 3, 4, 3);

		dots = new ImageView[adapterAd.getCount()];
		for (int i = 0; i < adapterAd.getCount(); i++) {

			dot = new ImageView(getActivity());
			dot.setLayoutParams(layoutParams);
			dots[i] = dot;
			dots[i].setTag(i);
			dots[i].setOnClickListener(onClick);

			if (i == 0) {
				dots[i].setBackgroundResource(R.drawable.radiobutton1);
			} else {
				dots[i].setBackgroundResource(R.drawable.radiobutton2);
			}

			viewGroup.addView(dots[i]);
		}
	}

	ViewPager.OnPageChangeListener myOnPageChangeListener = new ViewPager.OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			setCurDot(arg0);
			viewHandler.removeCallbacks(runnable);
			viewHandler.postDelayed(runnable, autoChangeTime);
		}

	};
	// 实现dot点击响应功能,通过点击事件更换页面
	View.OnClickListener onClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int position = (Integer) v.getTag();
			setCurView(position);
		}

	};

	/**
	 * 设置当前的引导页
	 */
	private void setCurView(int position) {
		if (position < 0 || position > adapterAd.getCount()) {
			return;
		}
		viewpager.setCurrentItem(position);
//        if (!StringUtil.isNullOrEmpty(lists.get(position).getNewsTitle())){
//            titleSlide = lists.get(position).getNewsTitle();
//            if(titleSlide.length() > 13){
//                titleSlide = titleSlide.substring(0,12);
//                article_title.setText(titleSlide);//当前新闻标题显示
//            }else{
//                article_title.setText(titleSlide);//当前新闻标题显示
//            }
//        }

	}

	/**
	 * 选中当前引导小点
	 */
	private void setCurDot(int position) {
		for (int i = 0; i < dots.length; i++) {
			if (position == i) {
				dots[i].setBackgroundResource(R.drawable.radiobutton1);
			} else {
				dots[i].setBackgroundResource(R.drawable.radiobutton2);
			}
		}
	}

	/**
	 * 每隔固定时间切换广告栏图片
	 */
	@SuppressLint("HandlerLeak")
	private final Handler viewHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			setCurView(msg.what);
		}

	};

	@Override
	public void onClickContentItem(int position, int flag, Object object) {
		switch (flag){
			case 1:
//				AdSlide adSlide = lists.get(position);
//				Intent webView = new Intent(getActivity(), AdvertisementDetail.class);
//				webView.putExtra("url", adSlide.getUrl());
//				startActivity(webView);
				break;
		}
	}
	
}
