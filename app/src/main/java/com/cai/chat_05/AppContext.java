package com.cai.chat_05;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;


import com.cai.chat_05.base.BaseApplication;
import com.cai.chat_05.bean.User;
import com.cai.chat_05.cache.DataCleanManager;
import com.cai.chat_05.utils.MethodsCompat;
import com.cai.chat_05.utils.StringUtils;

import org.kymjs.kjframe.KJBitmap;

import java.util.List;
import java.util.Properties;
import java.util.UUID;



/**
 * 
 * @author chenbiao
 *
 */
public class AppContext extends BaseApplication {

	public static final int PAGE_SIZE = 20;// 默认分页大小

	private static AppContext instance;

	private boolean login;

	private User user;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		init();
	}

	private void init() {
		// 初始化网络请求
//		AsyncHttpClient client = new AsyncHttpClient();
//		PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
//		client.setCookieStore(myCookieStore);
//		ApiHttpClient.setHttpClient(client);
//		ApiHttpClient.setCookie(ApiHttpClient.getCookie(this));
//
//		ApiHttpClient.setHttpClient(client);
//		ApiHttpClient.setCookie(ApiHttpClient.getCookie(this));
//
//		// Log控制器
//		KJLoger.openDebutLog(true);
//		TLog.DEBUG = BuildConfig.DEBUG;

	}

	public boolean isLogin() {
		return login;
	}

	/**
	 * 获得登录用户的信息
	 * 
	 * @return
	 */
	public User getLoginUser() {
		User user = new User();
		user.setId(StringUtils.toInt(getProperty("user.uid"), 0));
		user.setName(getProperty("user.name"));

		return user;
	}

	/**
	 * 保存登录信息
	 * 
	 */
	@SuppressWarnings("serial")
	public void saveUserInfo(final User user) {

		this.login = true;
		setProperties(new Properties() {
			{
				setProperty("user.uid", String.valueOf(user.getId()));
				setProperty("user.name", user.getName());
				setProperty("user.account", user.getAccount());

			}
		});
	}

	/**
	 * 获得当前app运行的AppContext
	 * 
	 * @return
	 */
	public static AppContext getInstance() {
		return instance;
	}

	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties() {
		return AppConfig.getAppConfig(this).get();
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value);
	}

	/**
	 * 获取cookie时传AppConfig.CONF_COOKIE
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		String res = AppConfig.getAppConfig(this).get(key);
		return res;
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}

	/**
	 * 获取App唯一标识
	 * 
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if (StringUtils.isEmpty(uniqueID)) {
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	/**
	 * 清除保存的缓存
	 */
	public void cleanCookie() {
		removeProperty(AppConfig.CONF_COOKIE);
	}

	/**
	 * 清除app缓存
	 */
	public void clearAppCache() {
		DataCleanManager.cleanDatabases(this);
		// 清除数据缓存
		DataCleanManager.cleanInternalCache(this);
		// 2.2版本才有将应用缓存转移到sd卡的功能
		if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			DataCleanManager.cleanCustomCache(MethodsCompat
					.getExternalCacheDir(this));
		}
		// 清除编辑器保存的临时内容
		Properties props = getProperties();
		for (Object key : props.keySet()) {
			String _key = key.toString();
			if (_key.startsWith("temp"))
				removeProperty(_key);
		}
		new KJBitmap().cleanCache();
	}

	public static void setLoadImage(boolean flag) {
		set(AppConfig.KEY_LOAD_IMAGE, flag);
	}

	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	public static boolean isFristStart() {
		return getPreferences().getBoolean(AppConfig.KEY_FRITST_START, true);
	}

	public static void setFristStart(boolean frist) {
		set(AppConfig.KEY_FRITST_START, frist);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public static Intent getExplicitIntent(Context context,
										   Intent implicitIntent) {
		// Retrieve all services that can match the given intent
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent,
				0);
		// Make sure only one match was found
		if (resolveInfo == null || resolveInfo.size() != 1) {
			return null;
		}
		// Get component info and create ComponentName
		ResolveInfo serviceInfo = resolveInfo.get(0);
		String packageName = serviceInfo.serviceInfo.packageName;
		String className = serviceInfo.serviceInfo.name;
		ComponentName component = new ComponentName(packageName, className);
		// Create a new intent. Use the old one for extras and such reuse
		Intent explicitIntent = new Intent(implicitIntent);
		// Set the component to be explicit
		explicitIntent.setComponent(component);
		return explicitIntent;
	}
}
