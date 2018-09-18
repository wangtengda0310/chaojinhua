package com.igame.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.codec.digest.DigestUtils;

import net.sf.json.JSONObject;


public class Verify {
	private static final String SALT="jk2hf54df54ad3fa";
	
	public static boolean sendGet(String serverURL, String token, String uid){
		
		String sign = DigestUtils.md5Hex(token + uid + SALT);
//		String sign = MD5.getInstance().getHash(token + uid + SALT);
		String addUrl ="sign="+sign+"&token="+token+"&uid="+uid;
		String totalUrl = serverURL+"verify?"+addUrl;

		BufferedReader reader = null;
		HttpURLConnection conn = null;
		OutputStreamWriter out = null;
		StringBuffer strBuf = null;
		JSONObject json = null; 
		boolean result = false;
		
		try {
			URL myUrl = new URL(totalUrl);
			conn = (HttpURLConnection) myUrl.openConnection();
		    conn.setConnectTimeout(10000);
//		    conn.setRequestMethod("POST");
		    conn.setDoInput(true);
		    conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("charset", "UTF-8");
//		    System.out.println(conn);
//			// 获取URLConnection对象对应的输出流
//          out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
//          // 发送请求参数
//          out.write(totalUrl);
//          // flush输出流的缓冲
//          out.flush();
		    
			strBuf = new StringBuffer();
            //读取URL的响应
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line =null;
            while ((line = reader.readLine()) != null) {
                strBuf.append(line);
            }
            json = JSONObject.fromObject(strBuf.toString()); 
//            System.out.println("json :"+json.toString()); 
            String res = json.getString("res");
//            System.out.println("res :"+res);
            if(res.equals("true")){
            	result = true;
            }
            return result;
		    
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(out!=null){
                    out.close();
                }
                if(reader!=null){
                	reader.close();
                }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
		
	}
}
