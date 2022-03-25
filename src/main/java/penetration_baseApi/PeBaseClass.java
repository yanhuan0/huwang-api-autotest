package penetration_baseApi;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PeBaseClass {
    private static String charSet = "UTF-8";
    private static CloseableHttpClient httpClient = null;
    private static CloseableHttpResponse response = null;
    
    private static void currentTime() {
    	DateFormat normalDf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	long timestamp = new Date().getTime();
    	String dateStr=normalDf1.format(timestamp);
    	System.out.println(dateStr);
	}
    
    private static CloseableHttpClient createSSLClientDefault(){
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                //信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,hostnameVerifier);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return  HttpClients.createDefault();
    }
    private static HttpPost setPostHeader(HttpPost httpPost,String url,String api,
    		String Cookie) {
		try {
			if (httpPost != null) {
				//httpPost.setHeader("Content-Type","multipart/form-data; boundary=----WebKitFormBoundarygVN7AJFiHBUCikZO");
	            //httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
				httpPost.setHeader("Accept","*/*");
				httpPost.setHeader("Accept-Encoding","gzip, deflate, br");
	            httpPost.setHeader("Accept-Language","zh-CN,zh;q=0.9,zh-TW;q=0.8,en-US;q=0.7,en;q=0.6");
	            httpPost.setHeader("Connection","keep-alive");
	            httpPost.setHeader("Cookie",Cookie);
	            httpPost.setHeader("Host",url);
	            httpPost.setHeader("Origin","https://"+url);
	            httpPost.setHeader("Referer","https://"+url+api);
	            httpPost.setHeader("Sec-Fetch-Dest","empty");
	            httpPost.setHeader("Sec-Fetch-Mode","cors");
	            httpPost.setHeader("Sec-Fetch-Site","same-origin");
	            httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36");
	            httpPost.setHeader("X-Requested-With","XMLHttpRequest");
	            
	            return httpPost;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	return null;
	}
    private static HttpPut setPutHeader(HttpPut httpPut,String url,String api,
    		String Cookie) {
		try {
			if (httpPut != null) {
				//httpPost.setHeader("Content-Type","multipart/form-data; boundary=----WebKitFormBoundarygVN7AJFiHBUCikZO");
	            //httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
				httpPut.setHeader("Accept","*/*");
				httpPut.setHeader("Accept-Encoding","gzip, deflate, br");
				httpPut.setHeader("Accept-Language","zh-CN,zh;q=0.9,zh-TW;q=0.8,en-US;q=0.7,en;q=0.6");
				httpPut.setHeader("Connection","keep-alive");
				httpPut.setHeader("Cookie",Cookie);
				httpPut.setHeader("Host",url);
				httpPut.setHeader("Origin","https://"+url);
				httpPut.setHeader("Referer","https://"+url+api);
				httpPut.setHeader("Sec-Fetch-Dest","empty");
				httpPut.setHeader("Sec-Fetch-Mode","cors");
				httpPut.setHeader("Sec-Fetch-Site","same-origin");
				httpPut.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36");
				httpPut.setHeader("X-Requested-With","XMLHttpRequest");
	            
	            return httpPut;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	return null;
	}
    private static HttpGet setGetHeader(HttpGet httpGet,String url,String api,String Cookie) {
		try {
			if (httpGet != null) {
				httpGet.setHeader("Content-Type", "application/json; charset=UTF-8");
				httpGet.setHeader("Accept-Encoding","gzip, deflate, br");
				httpGet.setHeader("Accept-Language","zh-CN,zh;q=0.9,zh-TW;q=0.8,en-US;q=0.7,en;q=0.6");
				httpGet.setHeader("Connection","keep-alive");
				httpGet.setHeader("Cookie",Cookie);
				httpGet.setHeader("Host",url);
				httpGet.setHeader("Referer","https://"+url+api);
				httpGet.setHeader("Sec-Fetch-Dest","empty");
				httpGet.setHeader("Sec-Fetch-Mode","cors");
				httpGet.setHeader("Sec-Fetch-Site","same-origin");
				httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36");
				httpGet.setHeader("X-Requested-With","XMLHttpRequest");
	            
	            return httpGet;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	return null;
	}
    
    private static HttpDeleteWithBody setDeleteHeader(HttpDeleteWithBody httpDelete,String url,String api,
    		String Cookie) {
		try {
			if (httpDelete != null) {
				//httpPost.setHeader("Content-Type","multipart/form-data; boundary=----WebKitFormBoundarygVN7AJFiHBUCikZO");
	            //httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
				httpDelete.setHeader("Accept","*/*");
				httpDelete.setHeader("Accept-Encoding","gzip, deflate, br");
				httpDelete.setHeader("Accept-Language","zh-CN,zh;q=0.9,zh-TW;q=0.8,en-US;q=0.7,en;q=0.6");
				httpDelete.setHeader("Connection","keep-alive");
				httpDelete.setHeader("Cookie",Cookie);
				httpDelete.setHeader("Host",url);
				httpDelete.setHeader("Origin","https://"+url);
				httpDelete.setHeader("Referer","https://"+url+api);
				httpDelete.setHeader("Sec-Fetch-Dest","empty");
				httpDelete.setHeader("Sec-Fetch-Mode","cors");
				httpDelete.setHeader("Sec-Fetch-Site","same-origin");
				httpDelete.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36");
				httpDelete.setHeader("X-Requested-With","XMLHttpRequest");
	            
	            return httpDelete;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	return null;
	}

public static  MultipartEntityBuilder setMultipartEntity() {
	MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	builder.setCharset(Charset.forName(charSet));
	builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	builder.setContentType(ContentType.MULTIPART_FORM_DATA);
	return builder;
	
}
public static CloseableHttpResponse getPostResponse(String url, String api,
		String Cookie,String[] uploadStrings) throws SocketException,IOException{
	try {
        httpClient = createSSLClientDefault();
    	String urlString = "https://"+url+api;
    	HttpPost httpPost = new HttpPost(urlString);
    	RequestConfig config = RequestConfig.custom()
    			.setConnectTimeout(50000)   //设置连接超时时间
    			.setSocketTimeout(50000)    //设置读取超时时间
    			.build();
    	httpPost.setConfig(config);
    	httpPost = setPostHeader(httpPost,url,api,Cookie);  //setHeader for httpPost
    	if (uploadStrings.length > 2) {
    		String entityType = uploadStrings[0]; 
    		String jsonStr = uploadStrings[1]; 
    		String name = uploadStrings[2];
    		File file = new File(uploadStrings[3]);
    		String contentType = uploadStrings[4];
    		String fileName = uploadStrings[5];
    		if (entityType == "formData") {
        		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        		builder = setMultipartEntity();
        		builder.addBinaryBody(name,file,ContentType.create(contentType),fileName);

    			@SuppressWarnings("unchecked")
    			HashMap<String,Object> formDataMap = new Gson().fromJson(jsonStr, HashMap.class);
    			for (String key:formDataMap.keySet()) {
    				ContentBody contentBody = new StringBody((String) formDataMap.get(key),ContentType.create("multipart/form-data",charSet));
    				builder.addPart(key,contentBody);
    				//builder.addTextBody(key,formDataMap.get(key).toString(),ContentType.create("text/plain",Charset.forName("UTF-8")));
    				//System.out.println(key+": "+formDataMap.get(key));
    			}
    			HttpEntity httpEntity = builder.build();
    			httpPost.setEntity(httpEntity);

    			/* 有下面两行时无法执行通过
    			httpPost.setHeader("Content-Length",String.valueOf(contentlength));
    			httpPost.setHeader("Content-type", "multipart/form-data; boundary="+boundary);
    			 * 
    			 */
			}else if (entityType == "x-www-form-urlencoded") {
				System.out.println("x-www-form-urlencoded type cannot upload file!");
			}else {
				System.out.println("Unknown entity type!");
			}
		}else {
			String entityType = uploadStrings[0];
			String jsonStr = uploadStrings[1];
			if (entityType == "formData") {
				/*
				 *Use MultipartEntityBuilder... 
				*/
	    		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	    		builder = setMultipartEntity();
				@SuppressWarnings("unchecked")
				HashMap<String,Object> formDataMap = new Gson().fromJson(jsonStr, HashMap.class);
				for (String key:formDataMap.keySet()) {
					ContentBody contentBody = new StringBody((String) formDataMap.get(key),ContentType.create("multipart/form-data",charSet));
					builder.addPart(key,contentBody);
					//builder.addTextBody(key,formDataMap.get(key).toString(),ContentType.create("text/plain",Charset.forName("UTF-8")));
					//System.out.println(key+": "+formDataMap.get(key));
				}
				HttpEntity httpEntity = builder.build();
				httpPost.setEntity(httpEntity);
			}else if (entityType == "x-www-form-urlencoded") {
				/*
				 *Use UrlEncodedFormEntity... 
				 */
				httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				if (isJson.validate(jsonStr)) {
					@SuppressWarnings("unchecked")
					HashMap<String,String> jsonStrMap = new Gson().fromJson(jsonStr, HashMap.class);
					for (String key:jsonStrMap.keySet()) {
						params.add(new BasicNameValuePair(key,jsonStrMap.get(key)));
						System.out.println(key+": "+jsonStrMap.get(key));
					}
				}else {
					int paramLength = jsonStr.split(",").length;
					for (int i = 0; i < paramLength; i++) {
						String subStr = jsonStr.split(",")[i];
						String key = subStr.substring(0, subStr.indexOf(":"));
						String value = subStr.substring(key.length()+1, subStr.length());
						params.add(new BasicNameValuePair(key, value));
						System.out.println(key+": "+value);
					}
				}
				UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params,charSet);
				httpPost.setEntity(urlEncodedFormEntity);
			}else if (entityType == "application/json") {
				httpPost.setHeader("Content-Type", "application/json");
				StringEntity requestEntity = new StringEntity(jsonStr,charSet); 
				httpPost.setEntity(requestEntity);
				
			}
			else {
				System.out.println("Unknown entity type!");
			}

		}
    	//String entityString = EntityUtils.toString(httpPost.getEntity(), charSet);
    	//System.out.println(entityString);
    	System.out.println(httpPost);
        response = httpClient.execute(httpPost);
        return response;
	} catch (IOException e) {
		System.out.println("============>>> printStackTrace <<<=========");
		e.printStackTrace();
	}
	return null;
}

public static CloseableHttpResponse getPutResponse(String url, String api,
		String Cookie,String[] uploadStrings) throws SocketException,IOException{
	try {
        httpClient = createSSLClientDefault();
    	String urlString = "https://"+url+api;
    	HttpPut httpPut = new HttpPut(urlString);
    	RequestConfig config = RequestConfig.custom()
    			.setConnectTimeout(50000)   //设置连接超时时间
    			.setSocketTimeout(50000)    //设置读取超时时间
    			.build();
    	httpPut.setConfig(config);
    	httpPut = setPutHeader(httpPut,url,api,Cookie);  //setHeader for httpPut
    	if (uploadStrings.length > 2) {
    		String entityType = uploadStrings[0]; 
    		String jsonStr = uploadStrings[1]; 
    		String name = uploadStrings[2];
    		File file = new File(uploadStrings[3]);
    		String contentType = uploadStrings[4];
    		String fileName = uploadStrings[5];
    		if (entityType == "formData") {
        		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        		builder = setMultipartEntity();
        		builder.addBinaryBody(name,file,ContentType.create(contentType),fileName);

    			@SuppressWarnings("unchecked")
    			HashMap<String,Object> formDataMap = new Gson().fromJson(jsonStr, HashMap.class);
    			for (String key:formDataMap.keySet()) {
    				ContentBody contentBody = new StringBody((String) formDataMap.get(key),ContentType.create("multipart/form-data",charSet));
    				builder.addPart(key,contentBody);
    				//builder.addTextBody(key,formDataMap.get(key).toString(),ContentType.create("text/plain",Charset.forName("UTF-8")));
    				//System.out.println(key+": "+formDataMap.get(key));
    			}
    			HttpEntity httpEntity = builder.build();
    			httpPut.setEntity(httpEntity);

    			/* 有下面两行时无法执行通过
    			httpPost.setHeader("Content-Length",String.valueOf(contentlength));
    			httpPost.setHeader("Content-type", "multipart/form-data; boundary="+boundary);
    			 * 
    			 */
			}else if (entityType == "x-www-form-urlencoded") {
				System.out.println("x-www-form-urlencoded type cannot upload file!");
			}else {
				System.out.println("Unknown entity type!");
			}
		}else {
			String entityType = uploadStrings[0];
			String jsonStr = uploadStrings[1];
			if (entityType == "formData") {
				/*
				 *Use MultipartEntityBuilder... 
				*/
	    		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	    		builder = setMultipartEntity();
				@SuppressWarnings("unchecked")
				HashMap<String,Object> formDataMap = new Gson().fromJson(jsonStr, HashMap.class);
				for (String key:formDataMap.keySet()) {
					ContentBody contentBody = new StringBody((String) formDataMap.get(key),ContentType.create("multipart/form-data",charSet));
					builder.addPart(key,contentBody);
					//builder.addTextBody(key,formDataMap.get(key).toString(),ContentType.create("text/plain",Charset.forName("UTF-8")));
					//System.out.println(key+": "+formDataMap.get(key));
				}
				HttpEntity httpEntity = builder.build();
				httpPut.setEntity(httpEntity);
			}else if (entityType == "x-www-form-urlencoded") {
				/*
				 *Use UrlEncodedFormEntity... 
				 */
				httpPut.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				if (isJson.validate(jsonStr)) {
					@SuppressWarnings("unchecked")
					HashMap<String,String> jsonStrMap = new Gson().fromJson(jsonStr, HashMap.class);
					for (String key:jsonStrMap.keySet()) {
						params.add(new BasicNameValuePair(key,jsonStrMap.get(key)));
						System.out.println(key+": "+jsonStrMap.get(key));
					}
				}else {
					
					int paramLength = jsonStr.split(",x_bound_x,").length;
					for (int i = 0; i < paramLength; i++) {
						String subStr = jsonStr.split(",x_bound_x,")[i];
						String key = subStr.substring(0, subStr.indexOf(":"));
						String value = subStr.substring(key.length()+1, subStr.length());
						params.add(new BasicNameValuePair(key, value));
						System.out.println(key+": "+value);
					}
				}
				UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params,charSet);
				httpPut.setEntity(urlEncodedFormEntity);
			}else if (entityType == "application/json") {
				httpPut.setHeader("Content-Type", "application/json");
				StringEntity requestEntity = new StringEntity(jsonStr,charSet); 
				httpPut.setEntity(requestEntity);
				
			}
			else {
				System.out.println("Unknown entity type!");
			}

		}
    	//String entityString = EntityUtils.toString(httpPost.getEntity(), charSet);
    	//System.out.println(entityString);
    	System.out.println(httpPut);
        response = httpClient.execute(httpPut);
        return response;
	} catch (IOException e) {
		System.out.println("============>>> printStackTrace <<<=========");
		e.printStackTrace();
	}
	return null;
}

public static CloseableHttpResponse getDeleteResponse(String url, String api,
		String Cookie,String[] uploadStrings) throws SocketException,IOException{
	
	//String entityType = uploadStrings[0];
	String jsonStr = uploadStrings[1];
	
    httpClient = createSSLClientDefault();
    String urlString = "https://"+url+api;
    HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(urlString);
	RequestConfig config = RequestConfig.custom()
			.setConnectTimeout(50000)   //设置连接超时时间
			.setSocketTimeout(50000)    //设置读取超时时间
			.build();
	httpDelete.setConfig(config);
    httpDelete = setDeleteHeader(httpDelete, url, api, Cookie);
   	httpDelete.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
   
   	List<NameValuePair> params = new ArrayList<NameValuePair>();
	int idLength = jsonStr.split(",").length;
	for (int i = 0; i < idLength; i++) {
		String key = jsonStr.split(",")[i].split(":")[0];
		String value = jsonStr.split(",")[i].split(":")[1];
		params.add(new BasicNameValuePair(key, value));
	}

	UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params,charSet);
	
	httpDelete.setEntity(urlEncodedFormEntity);
   	
   	System.out.println(httpDelete);
    response = httpClient.execute(httpDelete);
    return response;

}

public static CloseableHttpResponse getGetResponse(String url, String api, String Cookie) {
	try {
        httpClient = createSSLClientDefault();
    	String urlString = "https://"+url+api;
    	HttpGet httpGet = new HttpGet(urlString);
    	RequestConfig config = RequestConfig.custom()
    			.setConnectTimeout(50000)   //设置连接超时时间
    			.setSocketTimeout(50000)    //设置读取超时时间
    			.build();
    	httpGet.setConfig(config);
    	httpGet = setGetHeader(httpGet,url,api,Cookie);
        response = httpClient.execute(httpGet);
        currentTime();
        System.out.println(urlString);
        System.out.println(response);
        System.out.println("Done...");
        return response;

	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}

public static  JsonObject getResponseDataJson(CloseableHttpResponse httpResponse) throws SocketException, IOException {
	if (response != null) {
		HttpEntity resEntity = null;
		resEntity = response.getEntity();
        if (resEntity != null) {
			String entityString = EntityUtils.toString(resEntity, charSet);
			String entityjsonString = toJson.toJsonString(entityString);
			JsonParser jParser = new JsonParser();
			JsonObject jObject = jParser.parse(entityjsonString).getAsJsonObject();
			EntityUtils.consume(resEntity);
			return jObject;
        }
        response.close();
    }
	return null;
}

public static List<Integer> getRandomNum(int requMin, int requMax, int targetLength) {
    if(requMax-requMin < 1){
         System.out.print("最小值和最大值数据有误");
         return null;
     }else if(requMax-requMin <targetLength){
         System.out.print("指定随机个数超过范围");
         return null;
     }
     List<Integer> list = new ArrayList<>();
     List<Integer> requList = new ArrayList<>();
     for (int i = requMin; i < requMax; i++) {
         requList.add(i);
     }
     for (int i = 0; i < targetLength; i++) {
         // 取出一个随机角标
         int r = (int) (Math.random() * requList.size());
         list.add(requList.get(r));
         // 移除已经取过的值
         requList.remove(r);
     }
     return list;
 }
}
