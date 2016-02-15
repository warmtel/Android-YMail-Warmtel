package com.mail163.email.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import android.accounts.NetworkErrorException;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ActivityManager.MemoryInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.mail163.email.Email;
import com.mail163.email.Logs;
import com.mail163.email.R;
import com.mail163.email.Email.Global;
import com.mail163.email.activity.Welcome;
import com.mail163.email.service.UpdateService;

public class Utiles {
	public static String getNetStream(Context mContext) {
		int mUid = mContext.getApplicationInfo().uid;
		long recvByte = TrafficStats.getUidRxBytes(mUid);
//		long sendByte = TrafficStats.getUidTxBytes(mUid);
		long totalByte = recvByte;// + sendByte;
		if (totalByte <= -1) {
			return "0";// getString(R.string.device_no_stream);
		}
		return Formatter.formatFileSize(mContext, totalByte);// Byteת��ΪKB����MB
	}

	public static long getNetStreamSize(Context mContext) {
		int mUid = mContext.getApplicationInfo().uid;
		long recvByte = TrafficStats.getUidRxBytes(mUid);
//		long sendByte = TrafficStats.getUidTxBytes(mUid);
		long totalByte = recvByte;// + sendByte;
		if (totalByte <= -1) {
			return 0;
		}
		return totalByte / 1024;// Byteת��ΪKB
	}

	public static String getAvailMemory(Context mContext) {// ��ȡandroid��ǰ�����ڴ��С
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; ��ǰϵͳ�Ŀ����ڴ�

		return Formatter.formatFileSize(mContext, mi.availMem);// ����ȡ���ڴ��С���
	}

	public static long getAvailMemorySize(Context mContext) {// ��ȡandroid��ǰ�����ڴ��С
		ActivityManager am = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; ��ǰϵͳ�Ŀ����ڴ�

		return mi.availMem / 1024;// ����ȡ���ڴ��С���
	}

	public static String getTotalMemory(Context mContext) {
		String str1 = "/proc/meminfo";// ϵͳ�ڴ���Ϣ�ļ�
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;

		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// ��ȡmeminfo��һ�У�ϵͳ���ڴ��С

			arrayOfString = str2.split("\\s+");
			for (String num : arrayOfString) {
				Log.i(str2, num + "\t");
			}

			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// ���ϵͳ���ڴ棬��λ��KB������1024ת��ΪByte
			localBufferedReader.close();

		} catch (IOException e) {
		}
		return Formatter.formatFileSize(mContext, initial_memory);// Byteת��ΪKB����MB���ڴ��С���
	}

	public static String getSdcardAvailSize(Context mContext) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdPath = Environment.getExternalStorageDirectory();
			StatFs sdStatFs = new StatFs(sdPath.getPath());
			long blockSize = sdStatFs.getBlockSize();
			long availableBlocks = sdStatFs.getAvailableBlocks();
			long sdAvailable = availableBlocks * blockSize;
			return Formatter.formatFileSize(mContext, sdAvailable);
		} else {
			return "0byte";
		}

	}

	public static long getSdcardAvailSizes(Context mContext) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdPath = Environment.getExternalStorageDirectory();
			StatFs sdStatFs = new StatFs(sdPath.getPath());
			long blockSize = sdStatFs.getBlockSize();
			long availableBlocks = sdStatFs.getAvailableBlocks();
			long sdAvailable = availableBlocks * blockSize;
			return sdAvailable / 1024;
		} else {
			return 0;
		}

	}

	public static String getSdcardAllSize(Context mContext) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdPath = Environment.getExternalStorageDirectory();
			StatFs sdStatFs = new StatFs(sdPath.getPath());
			long blockSize = sdStatFs.getBlockSize();
			long totalBlocks = sdStatFs.getBlockCount();
			long sdTotal = totalBlocks * blockSize;
			return Formatter.formatFileSize(mContext, sdTotal);
		} else {
			return "0byte";
		}
	}

	public static void showNoNetworkNotification(Context mContext,
			String notifyTitle, String notifyMessage) {
		NotificationManager notificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.icon;
		CharSequence tickerText = notifyTitle;
		long when = System.currentTimeMillis();

		Intent intent = new Intent();
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);//设置UI界面特征
		builder.setSmallIcon(icon);   //通知图标
		builder.setContentTitle(notifyTitle);
		builder.setContentText(notifyMessage);   //通知内容
		builder.setTicker(tickerText);    //刚收到通知时提示
		builder.setAutoCancel(true);          // 点击取消通知
		builder.setWhen(System.currentTimeMillis());
		builder.setContentIntent(contentIntent);  //通知点击行为
		NotificationManager mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = builder.build();
		mNotifyManager.notify(R.string.app_name, notification);//发布通知

	}

	public static void createSoftUpdateVersionDialog(final Context mContext) {
		AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
		alert.setTitle(R.string.soft_update_title).setMessage(
				R.string.soft_update_message).setPositiveButton(
				R.string.confirm, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent updateIntent = new Intent(mContext,
								UpdateService.class);
						updateIntent.putExtra("titleId", R.string.app_name);
						mContext.startService(updateIntent);
					}
				}).setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		alert.create().show();

	}

	public static void showSystemNotification(Context mContext, String message) {
		NotificationManager notificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.icon;
		CharSequence tickerText = mContext.getString(R.string.system_notice);
		// ֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ
		long when = System.currentTimeMillis();
		// ��ʼ�� Notification
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		ComponentName component = new ComponentName(mContext, Welcome.class);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(component);

		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);


		RemoteViews contentView = new RemoteViews(mContext.getPackageName(),
				R.layout.system_notice_remoteview);

		contentView.setTextViewText(R.id.notificationContent, Html
				.fromHtml(message));

		contentView.setImageViewResource(R.id.notificationImage,
				R.drawable.icon);

		notification.contentView = contentView;
		notification.contentIntent = contentIntent;

		notificationManager.notify(R.string.system_notice, notification);
	}

	private static final String NET_TYPE_WIFI = "WIFI";

	public static boolean isWifi(Context mContext) {

		try {

			ConnectivityManager connMng = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInf = connMng.getActiveNetworkInfo();


			if (netInf != null && NET_TYPE_WIFI.equals(netInf.getTypeName())) {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Bitmap getBitmapFromUrl(Context context, String urlPath) {
		if (urlPath == null)
			return null;
		HttpURLConnection conn = null;
		try {
			// 利用string url构建URL对象
			URL mURL = new URL(urlPath.toString());
			conn = (HttpURLConnection) mURL.openConnection();

			conn.setRequestMethod("GET");
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(10000);

			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				InputStream is = conn.getInputStream();
				final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
				OutputStream out = new BufferedOutputStream(dataStream,
						4 * 1024);
				copy(is, out);
				out.flush();

				final byte[] data = dataStream.toByteArray();
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				return bitmap;
			} else {
				throw new NetworkErrorException("response status is "+responseCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return null;
	}

	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[4 * 1024];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	public static void getVersion(Context mContext) {
		try {
			Global.localVersion = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0).versionCode; // ���ñ��ذ汾��
			Global.localVersionName = mContext.getPackageManager()
					.getPackageInfo(mContext.getPackageName(), 0).versionName; // ���ñ��ذ汾
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void fetchSetting(Context context) {
		SharedPreferences sharedata = context.getSharedPreferences(
				Email.STORESETMESSAGE, 0);
		Global.set_store = sharedata.getInt(Global.set_store_name, 0);
		Global.set_sdSize = sharedata.getInt(Global.set_sd_name, Global.set_sdSize);
		Global.set_streamSize = sharedata.getInt(Global.set_stream_name, Global.set_streamSize);
		Global.set_softUpdate = sharedata.getInt(Global.set_softUpdate_name, 0);
		Global.set_shortcut = sharedata.getInt(Global.set_shortcut_name, 1);
		Global.set_security = sharedata.getInt(Global.set_security_name, 1);
		Global.notice_version = sharedata
				.getLong(Global.notice_version_name, 0);

		Global.set_download_select = sharedata.getInt(
				Global.set_download_select_name, 1);
		Email.VISIBLE_LIMIT_DEFAULT = sharedata.getInt(
				Global.set_download_count_name, 20);
		Email.VISIBLE_LIMIT_INCREMENT = Email.VISIBLE_LIMIT_DEFAULT;
		Global.set_sysch_delete = sharedata.getBoolean(
				Global.set_sysch_delete_name, true);
		Global.set_scret_send = sharedata.getBoolean(
				Global.set_scret_send_name, false);

		Global.set_music_background_value = sharedata.getInt(Global.set_music_background, 0);
		
		Global.set_security_password = sharedata.getString(Global.set_security_password_name, "");
	}

	public static void fetchDownMailSetting(Context context) {
		SharedPreferences sharedata = context.getSharedPreferences(
				Email.STORESETMESSAGE, 0);
		Global.set_download_select = sharedata.getInt(
				Global.set_download_select_name, 1);
		Email.VISIBLE_LIMIT_DEFAULT = sharedata.getInt(
				Global.set_download_count_name, 20);
		Email.VISIBLE_LIMIT_INCREMENT = Email.VISIBLE_LIMIT_DEFAULT;
		Global.set_sysch_delete = sharedata.getBoolean(
				Global.set_sysch_delete_name, true);
	}

	/**
	 * ɾ���ļ�������������ļ�
	 * 
	 * @param path
	 *            String �ļ���·�� �� c:/fqf
	 */
	public static void delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (!file.isDirectory()) {
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// ��ɾ���ļ���������ļ�
				delFolder(path + "/" + tempList[i]);// ��ɾ�����ļ���
			}
		}
	}

	public static void delFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			return;
		}
		file.delete();
	}

	/**
	 * ɾ���ļ���
	 * 
	 * @param filePathAndName
	 *            String �ļ���·�������� ��c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // ɾ����������������
			String filePath = folderPath;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			myFilePath.delete(); // ɾ�����ļ���

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/**
	 * �˳�����
	 * 
	 * @param activity
	 *            ������
	 */
	public static void killProcess(final Context mContext) {
		try {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					// android 2.1 ��֮ǰ�汾��Ч
					if (Integer.parseInt(android.os.Build.VERSION.SDK) < 8) {// android
						// sdk
						// 2.1
						// ��֮ǰ�汾
						ActivityManager activityManager = (ActivityManager) mContext
								.getSystemService(Context.ACTIVITY_SERVICE);
						activityManager.restartPackage(mContext
								.getPackageName());
					} else {// android sdk 2.2
						kill(mContext);
					}
				}
			}, 500);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void kill(Context mContext) {
		String packageName = mContext.getPackageName();

		Runtime runtime = Runtime.getRuntime();
		List<String> processLine = new ArrayList<String>();
		try {
			Process process = runtime.exec("ps");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(packageName)) {
					processLine.add(0, line);
				}
			}
			reader.close();

			for (String string : processLine) {
				StringTokenizer stringTokenizer = new StringTokenizer(string);
				int count = 0;
				while (stringTokenizer.hasMoreTokens()) {
					count++;
					String object = stringTokenizer.nextToken();
					if (count == 2) {
						runtime.exec("kill -9 " + object);
						// android.os.Process.killProcess(Integer.parseInt(object));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void clearAccount(Context mContext) {
		String dateBaseDir = "//data//data//com.mail163.email//databases//";
		String prefsBaseDir = "//data//data//com.mail163.email//shared_prefs//";
		delFolder(dateBaseDir);
		delFolder(prefsBaseDir);
		killProcess(mContext);
	}

	public static Drawable fetchBackDrawable(Context mContext) {
		String targetDir = Environment.getDataDirectory() + "/data/"+ mContext.getPackageName() + "/skin/";
		String listViewBack = targetDir + "back1.png";
		Drawable drawable = null;
		File bgFile = new File(listViewBack);
		try {
			drawable = BitmapDrawable.createFromPath(bgFile.getCanonicalPath());
		} catch (IOException e) {
			Logs.e(Logs.LOG_TAG, "error :" + e.getMessage());
			e.printStackTrace();
		}
		return drawable;
	}

	public static String fetchSkinName(Context mContext) {
		String targetDir = Environment.getDataDirectory() + "/data/"+ mContext.getPackageName() + "/skin/";
		String listViewBack = targetDir + "skininfo.txt";
		File skinFile = new File(listViewBack);
		if (skinFile == null || !skinFile.exists()) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		try {
			FileInputStream fis = new FileInputStream(skinFile);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");// �ļ�����Unicode,UTF-8,ASCII,GB2312,Big5
			Reader in = new BufferedReader(isr);
			int ch;
			while ((ch = in.read()) > -1) {
				buffer.append((char) ch);
			}
			in.close();
			 // buffer.toString())���Ƕ����������ַ�
		} catch (IOException e) {
           e.printStackTrace();
		}

		Pattern pattern = Pattern.compile("[&]");
		String[] skinInfoArr = pattern.split(buffer.toString());
		if(skinInfoArr != null && skinInfoArr.length>2){
			return skinInfoArr[2];//skin2.zip&��ɫ���&��ɫ
		}else{
			return "";
		}
		
	}
	/**
     * �ж��Ƿ���ȷ�����ַ
     * @param str
     * @return
     */
    public static boolean isEmailAddress(String str) {
        boolean b = false;
        Pattern pattern = Regex.EMAIL_ADDRESS_PATTERN;
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            b = true;
        }
        return b;
    }
    
    /** 
     * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  

    /** 
     * �����ֻ��ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
}
