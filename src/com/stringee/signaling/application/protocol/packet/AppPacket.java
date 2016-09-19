package com.stringee.signaling.application.protocol.packet;

import java.io.UnsupportedEncodingException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppPacket {

	/**
	 * ------------------PACKET--------------------------- -->
	 */
	/**
	 * 2 bytes
	 */
	public static final byte[] MAGIC = "BC".getBytes();
	/**
	 * 2 bytes
	 */
	private int length = 0;
	/**
	 * 2 bytes
	 */
	private short service = 0;
	/**
	 * Packet BODY
	 */
	private byte[] data = new byte[0];
	/**
	 * ------------------PACKET--------------------------- <--
	 */
	private final static Logger LOGGER = Logger.getLogger("AppPacket");

	private JSONObject jsonData = null;

	public AppPacket() {
	}

	public AppPacket(AppServiceType st) {
		service = st.getValue();
	}

	public AppPacket(int type) {
		service = (short) type;
	}

	public void setJsonData(JSONObject jsonObj) {
		this.jsonData = jsonObj;
	}

	public void setJsonData(JSONArray jsonArray) {
		try {
			data = jsonArray.toString().getBytes("UTF-8");
			length = data.length;
		} catch (UnsupportedEncodingException ex) {
			LOGGER.error("JSON", ex);
		}
	}

	public void setField(String key, Object value) {
		try {
			if (jsonData == null) {
				jsonData = new JSONObject();
			}
			jsonData.put(key, value);
		} catch (JSONException ex) {
			LOGGER.error("JSON", ex);
		}
	}

	public void decodeJson() {
		if (length > 0) {
			try {
				String jsonString = new String(data, "UTF-8");
				jsonData = new JSONObject(jsonString);
			} catch (UnsupportedEncodingException | JSONException ex) {
				LOGGER.error("JSON: " + new String(data), ex);
			}
		}
	}

	public void encodeJson() {
		if (jsonData != null) {
			try {
				data = jsonData.toString().getBytes("UTF-8");
				length = data.length;
			} catch (UnsupportedEncodingException ex) {
				LOGGER.error("JSON: " + new String(data), ex);
			}
		}
	}

	public Object getField(String key) {
		if (jsonData != null) {
			try {
				return jsonData.get(key);
			} catch (JSONException ex) {
				LOGGER.error("JSON", ex);
			}
		}
		return null;
	}

	public String getFieldString(String key) throws JSONException {
		if (jsonData != null) {
			return jsonData.getString(key);
		}
		return null;
	}

	public String optFieldString(String key, String opt) {
		if (jsonData != null) {
			return jsonData.optString(key, opt);
		}
		return opt;
	}

	public JSONObject optFieldJson(String key) {
		if (jsonData != null) {
			return jsonData.optJSONObject(key);
		}
		return null;
	}

	public Integer optFieldInt(String key, Integer opt) {
		if (jsonData != null) {
			return jsonData.optInt(key, opt);
		}
		return opt;
	}

	public double optFieldDouble(String key, double opt) {
		if (jsonData != null) {
			return jsonData.optDouble(key, opt);
		}
		return opt;
	}

	public Long optFieldLong(String key, Long opt) {
		if (jsonData != null) {
			return jsonData.optLong(key, opt);
		}
		return opt;
	}

	public Integer getFieldInt(String key) throws JSONException {
		if (jsonData != null) {
			return jsonData.getInt(key);
		}
		return null;
	}

	public Long getFieldLong(String key) throws JSONException {
		if (jsonData != null) {
			return jsonData.getLong(key);
		}
		return null;
	}

	public Double getFieldDouble(String key) throws JSONException {
		if (jsonData != null) {
			return jsonData.getDouble(key);
		}
		return null;
	}

	public JSONArray getFieldJsonArray(String key) throws JSONException {
		if (jsonData != null) {
			return jsonData.getJSONArray(key);
		}
		return null;
	}

	public JSONObject getFieldJsonObject(String key) throws JSONException {
		if (jsonData != null) {
			return jsonData.getJSONObject(key);
		}
		return null;
	}

	public JSONObject getJsonData() {
		return jsonData;
	}

	public JSONArray getJsonArray() {
		JSONArray jSONArray = null;
		try {
			jSONArray = new JSONArray(new String(data, "UTF-8"));
		} catch (UnsupportedEncodingException | JSONException ex) {
			LOGGER.error("JSON", ex);
		}
		return jSONArray;
	}

	@Override
	public String toString() {
		String dataString;
		if (jsonData == null) {
			dataString = new String(this.getData());
		} else {
			dataString = jsonData.toString();
		}

		return "Service=" + AppServiceType.getServiceType(this.getService()) + " | "
				+ "Body=" + dataString;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
		length = data.length;
	}

	public void setLength(int aShort) {
		if (aShort < 0) {
			aShort += 65536;
		}
		length = aShort;
	}

	public int getLength() {
		return length;
	}

	public short getService() {
		return service;
	}

	public void setService(short service) {
		this.service = service;
	}

	public void setService(int service) {
		this.service = (short) service;
	}

	public void setService(AppServiceType serviceType) {
		this.service = serviceType.getValue();
	}

}
