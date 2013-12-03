/**
 * BaseApplicationManager
 *
 * @license Dual licensed under the MIT or GPL Version 2 licenses.
 * @author xxxzxxx
 * Copyright 2013, Primitive, inc.
 * The MIT Licens (http://opensource.org/licenses/mit-license.php)
 * GPL Version 2 licenses (http://www.gnu.org/licenses/gpl-2.0.html)
 */
package com.primitive.applicationmanager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import com.primitive.applicationmanager.exception.ApplicationManagerException;
import com.primitive.library.common.log.Logger;

/**
 * BaseApplicationManager
 */
public class BaseApplicationManager implements Serializable {
	private static final long serialVersionUID = 2138865866833793765L;

    private boolean mDebug = false;

	/**
	 * Map<String, String>をJSONObjectに変換し返却します。
	 * @param params
	 * @return JSONObject
	 * @throws JSONException
	 */
	private static JSONObject getJsonObjectFromMap(
			final Map<String, String> params) throws JSONException {
		final Iterator<Entry<String, String>> iter = params.entrySet().iterator();
		final JSONObject holder = new JSONObject();

		while (iter.hasNext()) {
			final Map.Entry<String, String> pairs = (Map.Entry<String, String>) iter.next();
			final String key = (String) pairs.getKey();
			final String value = (String) pairs.getValue();

			holder.put(key, value);
		}
		return holder;
	}

	protected final ServerConfig config;

	/**
	 * BaseApplicationManager
	 * @param config
	 */
	protected BaseApplicationManager(final ServerConfig config) {
		this.config = config;
	}

	/**
	 * HttpClientを生成し返却します。
	 * @return HttpClient
	 */
	protected HttpClient createHttpClient() {
		final HttpClient httpClient = new DefaultHttpClient();
		final HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 1000);
		return httpClient;
	}

	/**
	 * サーバーへ要求を行います。
	 * @param argUrl
	 * @param argParams
	 * @return JSONObject
	 * @throws ApplicationManagerException
	 */
	protected JSONObject requestToResponse(final String argUrl,
			Map<String, String> argParams)
			throws ApplicationManagerException {
		Logger.start();
		Logger.debug("argUrl:" + argUrl);
		if(argParams == null){
			argParams = new HashMap<String,String>();
		}
		Logger.debug("argParams:" + argParams.toString());
		argParams.put("device", "android");

        if (mDebug) {
			argParams.put("mode", "debug");
		}

		argParams.put("protocolVersion", config.protocolVersion.version);

		final HttpClient httpClient = this.createHttpClient();
		String res = null;
		try {
			final HttpPost httpost = new HttpPost(argUrl);
			final JSONObject holder = BaseApplicationManager
					.getJsonObjectFromMap(argParams);
			final StringEntity se = new StringEntity(holder.toString());

			httpost.setEntity(se);
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");
			final BasicResponseHandler responseHandler = new BasicResponseHandler();
			res = httpClient.execute(httpost, responseHandler);
			Logger.debug(res);

			final JSONObject json = new JSONObject(res);
			return json;
		} catch (final Throwable ex) {
			Logger.err(ex);
			throw new ApplicationManagerException(ex);
		}

	}

    /**
     * Set the debug setting.
     */
    public void setDebug(boolean debug) {
        mDebug = debug;
    }

    /**
     * Get the debug setting.
     *
     * @ return boolean
     */
    public boolean isDebug() {
        return mDebug;
    }
}
