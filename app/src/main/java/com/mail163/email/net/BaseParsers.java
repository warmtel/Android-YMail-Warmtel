package com.mail163.email.net;

import android.accounts.NetworkErrorException;
import android.app.Application;

import com.mail163.email.Logs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class BaseParsers {
	private String httpUrl;
	private Application mApplication;

	protected BaseParsers(String httpUrl, Application mApplication) {
		try {
			this.mApplication = mApplication;
			this.httpUrl = httpUrl;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected InputStream getInputStream() {
		HttpURLConnection conn = null;
		try {
			// 利用string url构建URL对象
			URL mURL = new URL(httpUrl);
			conn = (HttpURLConnection) mURL.openConnection();

			conn.setRequestMethod("GET");
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(10000);

			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				InputStream is = conn.getInputStream();
				return is;
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

	protected String getResponseXmlString() {
		return get(httpUrl);
	}

	public String get(String url) {
		HttpURLConnection conn = null;
		try {
			// 利用string url构建URL对象
			URL mURL = new URL(url);
			conn = (HttpURLConnection) mURL.openConnection();

			conn.setRequestMethod("GET");
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(10000);

			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {

				InputStream is = conn.getInputStream();
				String response = convertStreamToString(is);
				return response;
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
	protected String convertStreamToString(InputStream is) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"), 8 * 1024);

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			sb.delete(0, sb.length());
			Logs.e(Logs.LOG_TAG, e.getMessage());
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Logs.e(Logs.LOG_TAG, e.getMessage());
			}
		}

		return sb.toString();
	}

}