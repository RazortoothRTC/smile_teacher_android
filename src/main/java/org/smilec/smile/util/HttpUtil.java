/**
Copyright 2012-2013 SMILE Consortium, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
**/
package org.smilec.smile.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.smilec.smile.bu.BoardManager;
import org.smilec.smile.bu.json.CurrentMessageJSONParser;

import android.accounts.NetworkErrorException;

public class HttpUtil {

	private HttpUtil() {
        // Empty
    }

    private static final InputStream executeMethod(HttpUriRequest request)
        throws NetworkErrorException {

        HttpClient client = new DefaultHttpClient();

        // Execute
        HttpResponse response = null;
        try {
            response = client.execute(request);
        } catch (ClientProtocolException e) {
            throw new NetworkErrorException(
                "Unexpected error executing request: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new NetworkErrorException("Unexpected error request: " + e.getMessage(), e);
        }

        // Check the status code
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();

        try {
            // Returning the content
            HttpEntity entity = response.getEntity();

            if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR
                || statusCode == HttpStatus.SC_NOT_FOUND) {
                JSONObject json = new JSONObject(EntityUtils.toString(entity));
                String message = json.getString("message");
                throw new NetworkErrorException(message);
            }

            if (statusCode != HttpStatus.SC_OK) {
                throw new NetworkErrorException("Unexpected HTTP Status Code: " + statusCode);
            }

            return entity.getContent();
        } catch (IllegalStateException e) {
            throw new NetworkErrorException("Unexpected error returnig the request content: "
                + e.getMessage(), e);
        } catch (IOException e) {
            throw new NetworkErrorException("Unexpected error returnig the request content: "
                + e.getMessage(), e);
        } catch (ParseException e) {
            throw new NetworkErrorException(e.getMessage());
        } catch (JSONException e) {
//        	new SendEmailAsyncTask(e.getMessage(),JSONException.class.getName(),HttpUtil.class.getName()).execute();
            throw new NetworkErrorException(e.getMessage());
        }
    }

    public static final InputStream executePut(String url, String json)
        throws NetworkErrorException, UnsupportedEncodingException, JSONException {
        HttpPut put = new HttpPut(url);
        put.setHeader("Content-Type", SmilePlugUtil.JSON);
        put.setEntity(new StringEntity(json, HTTP.UTF_8));
        return executeMethod(put);
    }
    
    public static final InputStream executeDelete(String url) throws NetworkErrorException, UnsupportedEncodingException, JSONException {
            
    		HttpDelete delete = new HttpDelete(url);
            delete.setHeader("Content-Type", SmilePlugUtil.JSON);
            return executeMethod(delete);
        }

    public static final InputStream executeGet(String url) throws NetworkErrorException {
        HttpGet get = new HttpGet(url);
        return executeMethod(get);
    }

    // This method seems to be never used in smile_teacher_android
    public static final HttpResponse executePost(String url, String json) throws NetworkErrorException {

		try {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new StringEntity(json, HTTP.UTF_8));
			httpPost.setHeader("Accept", SmilePlugUtil.FORM);
			httpPost.setHeader("Content-type", SmilePlugUtil.FORM);
			return new DefaultHttpClient().execute(httpPost);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }

}
