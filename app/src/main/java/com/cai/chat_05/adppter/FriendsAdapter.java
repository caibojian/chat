package com.cai.chat_05.adppter;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.cai.chat_05.R;
import com.cai.chat_05.bean.Friends;
import com.cai.chat_05.utils.ImgUtil;
import com.cai.chat_05.view.CircularImage;
import com.cai.chat_05.view.CustomListView;

public class FriendsAdapter extends BaseAdapter implements SectionIndexer {
	static final String LOG_TAG = FriendsAdapter.class.getCanonicalName();

	private List<Friends> list = null;
	private Context mContext;
	private Map<String, String> mCallRecords;
	private CustomListView mCustomListView;
	private HashMap<String, SoftReference<Bitmap>> hashMaps = new HashMap<String, SoftReference<Bitmap>>();

	public FriendsAdapter(Context mContext, List<Friends> list, Map<String, String> callRecods,
			CustomListView customListView) {
		this.mContext = mContext;
		this.list = list;
		this.mCallRecords = callRecods;
		this.mCustomListView = customListView;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<Friends> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		Log.v(LOG_TAG, "数组大小"+list.size());
		Log.v(LOG_TAG, "position的值"+position);
		return list.get(position-1);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final Friends mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.fragment_chatgroup_item, null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			viewHolder.iconView = (CircularImage) view.findViewById(R.id.icon);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		viewHolder.tvLetter.setVisibility(View.GONE);

		String path = list.get(position).getAvatarPath();
		if (path != null && !path.isEmpty()) {
			String p[] = path.split("#");
			if (p != null && p.length == 2) {
				viewHolder.iconView.setImage(p[0], p[1]);
			}
		}

		if (hashMaps.containsKey(path)) {
			viewHolder.iconView.setImageBitmap(hashMaps.get(path).get());
			// 另一个地方缓存释放资源
			ImgUtil.getInstance().reomoveCache(path);
		} else {
			viewHolder.iconView.setTag(path);
			// ImgUtil.getInstance().loadBitmap(path,
			// new OnLoadBitmapListener() {
			// @Override
			// public void loadImage(Bitmap bitmap, String path) {
			// ImageView iv = (ImageView) mCustomListView
			// .findViewWithTag(path);
			// if (bitmap != null && iv != null) {
			// bitmap = SystemMethod.toRoundCorner(bitmap, 15);
			// iv.setImageBitmap(bitmap);
			//
			// if (!hashMaps.containsKey(path)) {
			// hashMaps.put(path,
			// new SoftReference<Bitmap>(bitmap));
			// }
			// }
			// }
			// });
		}

		viewHolder.tvTitle.setText(this.list.get(position).getName());
		return view;

	}

	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		CircularImage iconView;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}