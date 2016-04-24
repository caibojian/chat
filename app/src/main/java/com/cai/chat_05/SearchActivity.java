package com.cai.chat_05;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.cai.chat_05.adppter.SearchViewPagerAdapter;
import com.cai.chat_05.bean.Constants;
import com.cai.chat_05.view.TitleBarView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchActivity extends FragmentActivity {
	private ImageView imageView;// 动画图片
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度

	@InjectView(R.id.vPager)
	protected ViewPager mViewPager;

	@InjectView(R.id.title_bar)
	protected TitleBarView mTitleBarView;
	@InjectView(R.id.tab_title_search_friends)
	protected TextView searchFriendsTitle;
	@InjectView(R.id.tab_title_search_group)
	protected TextView searchGroupTitle;

	private int userId;
	private String token;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		Intent intent = getIntent();
		userId = intent.getIntExtra(Constants.INTENT_EXTRA_USER_ID, 0);
		token = intent.getStringExtra(Constants.INTENT_EXTRA_TOKEN);

		setContentView(R.layout.activity_search);
		ButterKnife.inject(this);
		InitImageView();
		mViewPager.setAdapter(new SearchViewPagerAdapter(this, userId, token));

		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		mViewPager.setCurrentItem(0);
		searchFriendsTitle.setTextColor(getResources().getColor(
				R.color.text_color_blue));

		searchFriendsTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchFriendsTitle.setTextColor(getResources().getColor(
						R.color.text_color_blue));
				searchGroupTitle.setTextColor(getResources().getColor(
						R.color.text_color));
				mViewPager.setCurrentItem(0);
			}
		});
		searchGroupTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchFriendsTitle.setTextColor(getResources().getColor(
						R.color.text_color));
				searchGroupTitle.setTextColor(getResources().getColor(
						R.color.text_color_blue));
				mViewPager.setCurrentItem(1);

			}
		});
	}

	private void InitImageView() {
		mTitleBarView.setCommonTitle(View.GONE, View.VISIBLE, View.GONE,
				View.GONE);
		mTitleBarView.setTitleText(R.string.friends_search);
		
		imageView = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.a)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 2 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		imageView.setImageMatrix(matrix);// 设置动画初始位置
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageSelected(int arg0) {
			/*
			 * 两种方法，这个是一种，下面还有一种，显然这个比较麻烦 Animation animation = null; switch
			 * (arg0) { case 0: if (currIndex == 1) { animation = new
			 * TranslateAnimation(one, 0, 0, 0); } else if (currIndex == 2) {
			 * animation = new TranslateAnimation(two, 0, 0, 0); } break; case
			 * 1: if (currIndex == 0) { animation = new
			 * TranslateAnimation(offset, one, 0, 0); } else if (currIndex == 2)
			 * { animation = new TranslateAnimation(two, one, 0, 0); } break;
			 * case 2: if (currIndex == 0) { animation = new
			 * TranslateAnimation(offset, two, 0, 0); } else if (currIndex == 1)
			 * { animation = new TranslateAnimation(one, two, 0, 0); } break;
			 * 
			 * }
			 */
			switch (arg0) {
			case 0: {
				searchFriendsTitle.setTextColor(getResources().getColor(
						R.color.text_color_blue));
				searchGroupTitle.setTextColor(getResources().getColor(
						R.color.text_color));
				break;
			}
			case 1:
				searchFriendsTitle.setTextColor(getResources().getColor(
						R.color.text_color));
				searchGroupTitle.setTextColor(getResources().getColor(
						R.color.text_color_blue));
				break;
			}
			Animation animation = new TranslateAnimation(one * currIndex, one
					* arg0, 0, 0);// 显然这个比较简洁，只有一行代码。
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			imageView.startAnimation(animation);
		}

	}
}
