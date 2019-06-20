package com.track.toy.helper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

public class HttpHelper {

	private HttpHelper() {
	}

	public static String post(String reqURL, Map<String, String> params) {
		StringBuilder sendData = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sendData.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		if (sendData.length() > 0) {
			sendData.setLength(sendData.length() - 1);
		}
		return post(reqURL, sendData.toString());
	}

	public static String post(String reqURL, String sendData) {
		HttpURLConnection httpURLConnection = null;
		OutputStream out = null;
		InputStream in = null;
		int httpStatusCode = 0;
		try {
			URL sendUrl = new URL(reqURL);
			httpURLConnection = (HttpURLConnection) sendUrl.openConnection();
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setConnectTimeout(150000);
			httpURLConnection.setReadTimeout(150000);
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			out = httpURLConnection.getOutputStream();
			out.write(sendData.getBytes());
			out.flush();
			httpStatusCode = httpURLConnection.getResponseCode();
			in = httpURLConnection.getInputStream();
			byte[] by = new byte[1024];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			int len = -1;
			while ((len = in.read(by)) != -1) {
				bos.write(by, 0, len);
			}
			return bos.toString("utf-8");

		} catch (Exception e) {
//			e.printStackTrace();
			return "Failed!" + httpStatusCode;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
				httpURLConnection = null;
			}
		}
	}  

	@SuppressWarnings("deprecation")
	public static String patch(String url, String json) {
		String resultObj = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPatch httpPatch = new HttpPatch(url);
			httpPatch.setHeader("Content-type", "application/json");
			httpPatch.setHeader("Charset", HTTP.UTF_8);
			httpPatch.setHeader("Accept", "application/json");
			httpPatch.setHeader("Accept-Charset", HTTP.UTF_8);
			if (json != null) {
				StringEntity entity;
				entity = new StringEntity(json.toString(), HTTP.UTF_8);
				httpPatch.setEntity(entity);
			}
			HttpResponse response = httpClient.execute(httpPatch);
			resultObj = EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultObj;
	}

	public static String post(String url, byte[] params) {
		HttpURLConnection httpConn = null;
		OutputStream out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			httpConn = (HttpURLConnection) realUrl.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Accept-Charset", "utf-8");
			httpConn.setRequestProperty("Connection", "keep-alive");
			httpConn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
			httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset = UTF-8");
			httpConn.setRequestProperty("Accept", "application/xml");
			httpConn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.803.0 Safari/535.1");
			httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			out = httpConn.getOutputStream();
			out.write(params);
			out.flush();
			in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
			String line = "";
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static String post(String urlStr, Map<String, String> params, Map<String, String> fileMap) {
		String res = "";
		HttpURLConnection conn = null;
		String BOUNDARY = "----------------------------605066937030";
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

			OutputStream out = new DataOutputStream(conn.getOutputStream());
			// text
			if (params != null) {
				StringBuffer strBuf = new StringBuffer();
				Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, String> entry = iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"\r\n\r\n");
					strBuf.append(inputValue);
				}
				out.write(strBuf.toString().getBytes());
			}

			// file
			if (fileMap != null) {
				Iterator<Map.Entry<String, String>> iter = fileMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, String> entry = iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					File file = new File(inputValue);
					String filename = file.getName();
					MagicMatch match = Magic.getMagicMatch(file, false, true);
					String contentType = match.getMimeType();

					StringBuffer strBuf = new StringBuffer();
					strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename
							+ "\"\r\n");
					strBuf.append("Content-Type:" + contentType + "\r\n\r\n");

					out.write(strBuf.toString().getBytes());

					DataInputStream in = new DataInputStream(new FileInputStream(file));
					int bytes = 0;
					byte[] bufferOut = new byte[1024];
					while ((bytes = in.read(bufferOut)) != -1) {
						out.write(bufferOut, 0, bytes);
					}
					in.close();
				}
			}

			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();

			StringBuffer strBuf = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuf.append(line).append("\n");
			}
			res = strBuf.toString();
			reader.close();
			reader = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return res;
	}
}
