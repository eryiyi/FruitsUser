package com.lbins.FruitsUser.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import com.lbins.FruitsUser.MainActivity;
import com.lbins.FruitsUser.R;
import com.lbins.FruitsUser.SecondApplication;
import com.lbins.FruitsUser.bean.BaseFragment;
import com.lbins.FruitsUser.bean.Response;
import com.lbins.FruitsUser.city.CityList;
import com.lbins.FruitsUser.http.AsyncHttpResponseHandler;
import com.lbins.FruitsUser.http.HttpClientUtils;
import com.lbins.FruitsUser.http.HttpParams;
import com.lbins.FruitsUser.servieid.ServerId;
import com.lbins.FruitsUser.sharedpreference.SharedPrefsUtil;
import com.lbins.FruitsUser.ui.Logon;
import com.lbins.FruitsUser.ui.MipcaActivityCapture;
import com.lbins.FruitsUser.util.StringUtil;
import org.json.JSONException;
import org.json.JSONObject;


public class PublicTitleFragment extends BaseFragment {
	private final static int SCANNIN_GREQUEST_CODE = 1;
	private View view;
	TextView title;
	int getchange;
	RelativeLayout magnifer;
	RelativeLayout shoppingcart;
	private static int numbercount;
	ImageView numberimage;
	TextView select_city;//城市
	TextView number;
	TextView saoyisao;
	FrameLayout frameimg;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.publictitle, null);
		registerBoradcastReceiver();
		title = (TextView) view.findViewById(R.id.title);
		frameimg = (FrameLayout) view.findViewById(R.id.frameimg);
		magnifer = (RelativeLayout) view.findViewById(R.id.magnifer);
		shoppingcart = (RelativeLayout) view.findViewById(R.id.shoppingcart);
		numberimage = (ImageView) view.findViewById(R.id.numberimage);
		number = (TextView) view.findViewById(R.id.number);
		saoyisao = (TextView) view.findViewById(R.id.saoyisao);
		getchange = getArguments().getInt("changetitle", 100);
		System.out.println("getchange = " + getchange);
		switch (getchange) {
		case BottomFragment.MAINPAGE:
			title.setText(R.string.sellfruit);
			break;
		case BottomFragment.PERSONALCENTER:
			magnifer.setVisibility(View.GONE);
			shoppingcart.setVisibility(View.GONE);
			title.setText(R.string.personalcenter);
			break;

		default:
			break;
		}
		shoppingcart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if ("0".equals(getGson().fromJson(getSp().getString("is_login", ""), String.class))) {
					Toast.makeText(getActivity(), "请登录后查看购物车", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(getActivity(), Logon.class);
					intent.putExtra("nozero", 2);
					startActivity(intent);
				}else{
//					Intent intent = new Intent(getActivity(), ShoppingCartList.class);
//					startActivity(intent);
				}
			}
		});
		
		magnifer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), CityList.class);
				startActivity(intent);
			}
		});
		if ("0".equals(getGson().fromJson(getSp().getString("is_login", ""), String.class))) {
			numbercount = SharedPrefsUtil.getValue(getActivity(), "Setting" + getGson().fromJson(getSp().getString("uid", ""), String.class), 0);
			if (numbercount != 0) {
				frameimg.setVisibility(View.VISIBLE);
				number.setText(numbercount + "");
			}else {
				frameimg.setVisibility(View.GONE);
			}
		}
		select_city = (TextView) view.findViewById(R.id.select_city);
		//
		if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("city", ""), String.class))){
			select_city.setText(getGson().fromJson(getSp().getString("city", ""), String.class));
		}else {
			select_city.setText("重庆");
		}
		saoyisao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//扫一扫
				Intent intent = new Intent();
				intent.setClass(getActivity(), MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case SCANNIN_GREQUEST_CODE:
				if(resultCode == getActivity().RESULT_OK){
					Bundle bundle = data.getExtras();
					//显示扫描到的内容
					String result = bundle.getString("result");
					String str = StringUtil.getStrFromJson(result);
					saoyisao(str);
				}
				break;
		}
	}


	void saoyisao(String result){
		HttpParams params = new HttpParams();
		params.put("user_name", SecondApplication.user_name);
		params.put("order_id", result);
		HttpClientUtils.getInstance().post(ServerId.serveradress, ServerId.SCAN_URL, params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(JSONObject jsonObject) {
				try {
//					object = jsonObject.getJSONObject("data");
					Response.code = jsonObject.getInt("code");
				} catch (JSONException e) {
					e.printStackTrace();
				}finally{
					Message message = new Message();
					message.what = 123;
					handler.sendMessage(message);
				}
			}
		});
	}

	Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
				case 123:
					if(Response.code == 200 ){
						//
						Toast.makeText(getActivity(), R.string.saosuccess, Toast.LENGTH_SHORT).show();
						Intent intent = new Intent("address_success");
						getActivity().sendBroadcast(intent);
					}
					else if(Response.code == -1 ){
						Toast.makeText(getActivity(), R.string.saoerro1r, Toast.LENGTH_SHORT).show();
					}

					else{
						Toast.makeText(getActivity(), R.string.saoerror, Toast.LENGTH_SHORT).show();
					}
					break;
				default:
					break;
			}
			return true;
		}
	});

	@Override
	public void onResume() {
		if ("1".equals(getGson().fromJson(getSp().getString("is_login", ""), String.class))) {
			frameimg.setVisibility(View.VISIBLE);
			numbercount = SharedPrefsUtil.getValue(getActivity(), "Setting" + getGson().fromJson(getSp().getString("uid", ""), String.class), 0);
			number.setText(numbercount + "");
		}
		super.onResume();
	}



	//广播接收动作
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("add_goods_success")) {
				number.setText(String.valueOf((numbercount+1)));
			}
			if (action.equals("select_city_success")) {
				//
				if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("city", ""), String.class))){
					select_city.setText(getGson().fromJson(getSp().getString("city", ""), String.class));
				}else {
					select_city.setText("重庆");
				}
			}
		}
	};

	//注册广播
	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("add_goods_success");
		myIntentFilter.addAction("select_city_success");
		//注册广播
		getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mBroadcastReceiver);
	}
}
