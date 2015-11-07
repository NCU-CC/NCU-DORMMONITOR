package com.da.ncudormmoitor.andr.client.net;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
/*
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
*/

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.HtmlNode;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.TagNodeVisitor;

import android.util.Log;

public class NetService {

	private static NetService sInstance;
	private static double ByteToGB = Math.pow(2, 30);
	DecimalFormat df = new DecimalFormat("##.00");

	private NetService() {
	}

	public static void init() {
		if (sInstance == null)
			sInstance = new NetService();
	}

	public static NetService getInstance() {
		return sInstance;
	}

	public final static int UPLOAD_OUT_INDEX = 2;
	public final static int DOWNLOAD_OUT_INDEX = 4;
	public final static int UPLOAD_ALL_INDEX = 6;
	public final static int DOWNOAD_ALL_INDEX = 8;

	public final static int UPLOAD_OUT_LONG_INDEX = 1;
	public final static int DOWNLOAD_OUT_LONG_INDEX = 3;
	public final static int UPLOAD_ALL_LONG_INDEX = 5;
	public final static int DOWNOAD_ALL_LONG_INDEX = 7;

	private HtmlCleaner cleaner;
	private ArrayList<String> resultTotal;
	private ArrayList<String> result;
	private String UpLoadFlowAll;
	private String UpLoadFlowOut;
	private String DownLoadFlowAll;
	private String DownLoadFlowOut;

	private boolean isLock;
	private String mLockMsg;

	/**
	 * 是否有被鎖網?
	 * 
	 * @param dormUrl
	 * @param ipNum
	 * @return isLock
	 */
	public boolean isLock(String dormUrl, String ipNum) {
		cleaner = new HtmlCleaner();
		isLock = false;
		try {
			TagNode node;
			node = cleaner.clean(new URL(dormUrl + ipNum), "big5");
			node.traverse(new TagNodeVisitor() {
				@Override
				public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
					if (htmlNode instanceof TagNode) {
						TagNode tag = (TagNode) htmlNode;
						String tagName = tag.getName();

						if ("small".equals(tagName)) {
							//if (tag.getChildTags() == null) {
								isLock = true;
								mLockMsg = tag.getText().toString();
								Log.d("islock", mLockMsg);
							//}
						}
					}
					return true;
				}
			});
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isLock;
	}

	/**
	 * 
	 * @param dormUrl
	 * @param ipNum
	 *             resultTotal 裡面有9組值,第1組值無用,其他是各個總量,請參照最上方index
	 */
	public ArrayList<String> getTotalFlow(String dormUrl, String ipNum)
			throws ArrayIndexOutOfBoundsException {
		cleaner = new HtmlCleaner();
		resultTotal = new ArrayList<String>();
		try {
			TagNode node;
			node = cleaner.clean(new URL(dormUrl + ipNum));
			node.traverse(new TagNodeVisitor() {
				int index = 0;

				@Override
				public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
					if (htmlNode instanceof TagNode) {
						TagNode tag = (TagNode) htmlNode;
						String tagName = tag.getName();

						if ("td".equals(tagName)) {
							String matchString = tag.getParent()
									.getAttributeByName("bgcolor");
							if (matchString != null
									&& matchString.equals("#ffffbb")) {
								Log.d("NetService.getTotalFlow()", tag.getText().toString());
								String[] temp = tag.getText().toString()
										.split("\n");

								resultTotal.add(temp[0]);
								if (index > 0)
									resultTotal.add(temp[1]);
								index++;
							}
						}
					}
					return true;
				}
			});
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultTotal;
	}

	/**
	 * 校外上傳流量詳細資訊
	 * 
	 * @param dormUrl
	 * @param ipNum
	 * @return 回傳一組String,有144組校外流量上傳資訊,單位KB
	 */
	public ArrayList<String> getDetailFlow(String dormUrl, String ipNum) {
		cleaner = new HtmlCleaner();
		result = new ArrayList<String>();
		try {
			TagNode node;

			node = cleaner.clean(new URL(dormUrl + ipNum));
			node.traverse(new TagNodeVisitor() {
				int index = 0;

				@Override
				public boolean visit(TagNode tagNode, HtmlNode htmlNode) {
					if (htmlNode instanceof TagNode) {
						TagNode tag = (TagNode) htmlNode;
						String tagName = tag.getName();

						if ("td".equals(tagName)) {
							String matchString = tag.getParent()
									.getAttributeByName("bgcolor");
							if (matchString != null
									&& (matchString.equals("#ffffee") || matchString
											.equals("#eeeeee"))) {
								if ((index % 5 == 1)) {
									result.add(tag.getText().toString());
								}
								index++;
							}
						}
					}
					return true;
				}
			});
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * @return 校外上傳總量(Byte)
	 */
	public String getUpLoadFlow() throws IndexOutOfBoundsException{
		if (resultTotal.get(UPLOAD_OUT_LONG_INDEX) == null)//UPLOAD_OUT_INDEX @edited
			return null;
		else {
            UpLoadFlowOut = resultTotal.get(UPLOAD_OUT_LONG_INDEX);//UPLOAD_OUT_INDEX @edited
            return UpLoadFlowOut;
        }
	}

	/**
	 * 
	 * @return 校外下載總量(Byte)
	 */
	public String getDownLoadFlow() throws IndexOutOfBoundsException{
		if (resultTotal.get(DOWNLOAD_OUT_LONG_INDEX) == null) {//DOWNLOAD_OUT_INDEX @edited
			throw new IndexOutOfBoundsException("parse error.");
		} else {
			DownLoadFlowOut = resultTotal.get(DOWNLOAD_OUT_LONG_INDEX);//DOWNLOAD_OUT_INDEX @edited
			return DownLoadFlowOut;
		}
	}

	/**
	 * 
	 * @return 全部上傳總量(Byte)
	 */
	public String getUpLoadFlowAll() throws IndexOutOfBoundsException{
		if (resultTotal.get(UPLOAD_ALL_LONG_INDEX) == null)//UPLOAD_ALL_INDEX@edited
			throw new IndexOutOfBoundsException("parse error.");
		else {
			UpLoadFlowAll = resultTotal.get(UPLOAD_ALL_LONG_INDEX);//UPLOAD_ALL_INDEX@edited
			return UpLoadFlowAll;
		}
	}

	/**
	 * 
	 * @return 全部下載總量(Byte)
	 */
	public String getDownLoadFlowAll() throws IndexOutOfBoundsException{
		if (resultTotal.get(DOWNOAD_ALL_LONG_INDEX) == null) {//DOWNOAD_ALL_INDEX@edited
			throw new IndexOutOfBoundsException("parse error.");
		} else {
			DownLoadFlowAll = resultTotal.get(DOWNOAD_ALL_LONG_INDEX);//DOWNOAD_ALL_INDEX@edited
			return DownLoadFlowAll;
		}
	}

	/**
	 * 取得某個頁面的html
	 * 
	 * @param url
	 * @return
	 */
	public int sendRequest(String url) {
		String TAG = this.getClass().getName();
		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(url);
		int statusCode = 0;
		try {
			HttpResponse response = client.execute(getRequest);
			statusCode = response.getStatusLine().getStatusCode();
			Log.e("Http Status code", Integer.toString(statusCode));
			/*
			 * if (response.getStatusLine().getStatusCode() != 200) { Log.d(TAG,
			 * "Status fail"); return null; }
			 */

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return builder.toString();
		return statusCode;
	}

	public String getLockMsg() {
		return mLockMsg;
	}
}
