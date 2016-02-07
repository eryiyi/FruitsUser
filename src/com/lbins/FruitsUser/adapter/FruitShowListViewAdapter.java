package com.lbins.FruitsUser.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lbins.FruitsUser.R;
import com.lbins.FruitsUser.bean.FruitBean;
import com.lbins.FruitsUser.networkbitmap.BitmapUtil;
import com.lbins.FruitsUser.servieid.ServerId;

import java.util.List;

public class FruitShowListViewAdapter extends BaseAdapter{
    //适配器对数据源的引用
    List<FruitBean> list;
    //布局加载器，作用是将一个xml的布局文件，转换成java代码能够识别的View对象
    LayoutInflater inflater;
    Context context;

    public FruitShowListViewAdapter(Context context, List<FruitBean> list){
        this.list = list;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }
    @Override
    public int getCount() {
        if(list != null){
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int arg0) {
        if(list != null && arg0 < list.size()){
            return list.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int arg0) {

        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //对象引用保留
        Holder holder;
        if(convertView == null){
            holder = new Holder();
            convertView = inflater.inflate(R.layout.itemfruit, null);
            holder.compic = (ImageView) convertView.findViewById(R.id.comimage);
            holder.comdescribe = (TextView) convertView.findViewById(R.id.comname);
            holder.compri = (TextView) convertView.findViewById(R.id.comprice);
            holder.comsuppri = (TextView) convertView.findViewById(R.id.comsuppri);
            holder.addshopcart = (TextView) convertView.findViewById(R.id.purchasequantity);
            holder.numbner = (TextView) convertView.findViewById(R.id.numbner);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }

        if(position < list.size()){
            if(list.get(position).getGoods_data().get(0) != null){
                BitmapUtil.getInstance().download(ServerId.serveradress, list.get(position).getGoods_data().get(0).getProduct_pic(), holder.compic);
                holder.comdescribe.setText(list.get(position).getGoods_data().get(0).getGoods_name());
                switch (Integer.parseInt((list.get(position).getGoods_data().get(0).getIs_send()))){
//				'1未付款  2已支付 3已发货(未送货) 4已收货(已送货) 5已评价 6已完成 7已取消', 8送货中
                    case 1:
                        holder.addshopcart.setText(R.string.status1);
                        break;
                    case 2:
                        holder.addshopcart.setText(R.string.status2);
                        break;
                    case 3:
                        holder.addshopcart.setText(R.string.status3);
                        break;
                    case 4:
                        holder.addshopcart.setText(R.string.status4);
                        break;
                    case 5:
                        holder.addshopcart.setText(R.string.status5);
                        break;
                    case 6:
                        holder.addshopcart.setText(R.string.status6);
                        break;
                    case 7:
                        holder.addshopcart.setText(R.string.status7);
                        break;
                    case 8:
                        holder.addshopcart.setText(R.string.status8);
                        break;
                }
            }

            holder.comsuppri.setText(context.getResources().getString( R.string.songhuodizhi) + list.get(position).getAddress());
            holder.numbner.setText(context.getResources().getString( R.string.jiehuoriqi) +list.get(position).getSend_date());

        }
        return convertView;
    }

    /**
     * 对每个数据项里面的控件的引用
     * 也是每个数据项里面的控件的保存
     * 使用他可以在getView方法里面当convertView不为空，也就是这个数据项是使用缓存的View的时候，
     * 他可以快捷的访问到View里面的控件的引用
     */
    class Holder{
        ImageView compic;
        TextView comdescribe;
        TextView numbner;
        TextView compri;
        TextView comsuppri;
        TextView addshopcart;
    }
}
