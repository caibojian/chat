package com.cai.chat_05.adppter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cai.chat_05.PiControlActivity;
import com.cai.chat_05.R;
import com.kyleduo.switchbutton.SwitchButton;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @Description:gridview的Adapter
 * @author http://blog.csdn.net/finddreams
 */
public class PiToCantralAdapter extends BaseAdapter {
	private PiControlActivity mContext;

	public String[] img_text = { "LED灯控制","红外监控"};
	public int[] imgs = { R.drawable.app_transfer, R.drawable.app_fund,
			R.drawable.app_phonecharge, R.drawable.app_creditcard,
			R.drawable.app_movie, R.drawable.app_lottery,
			R.drawable.app_facepay, R.drawable.app_close, R.drawable.app_plane };

	public PiToCantralAdapter(PiControlActivity mContext) {
		super();
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return img_text.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ItemViewHolder mh = null;

		if (convertView == null) {
			convertView = View.inflate(mContext,
					R.layout.pi_contral_item, null);
			mh = new ItemViewHolder(convertView);
			convertView.setTag(mh);
		} else {
			mh = (ItemViewHolder) convertView.getTag();
		}
		mh.pi_to_contral_name.setText(img_text[position]);
		if(mh.ledswitch.isChecked()){
			mh.things_photo.setImageResource(R.drawable.led_on);
		}else{
			mh.things_photo.setImageResource(R.drawable.led_off);
		}
		mh.ledswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				switch (position){
					case 0:
						mContext.controlLed(isChecked,position);
						mContext.showCustomToast("led灯控制"+isChecked);
						break;
					case 1:
						mContext.controlInfrared(isChecked,position);
						mContext.showCustomToast("红外监控"+isChecked);
						break;
					default:
						break;
				}
			}
		});
		return convertView;
	}

	public class ItemViewHolder extends RecyclerView.ViewHolder {
		@InjectView(R.id.things_photo)
		public ImageView things_photo;
		@InjectView(R.id.pi_to_contral_name)
		public TextView pi_to_contral_name;
		@InjectView(R.id.ledswitch)
		public SwitchButton ledswitch;

		public ItemViewHolder(View itemView) {
			super(itemView);
			ButterKnife.inject(this, itemView);
		}
	}

}
