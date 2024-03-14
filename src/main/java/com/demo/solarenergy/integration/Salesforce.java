package com.demo.solarenergy.integration;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import com.demo.solarenergy.database.Sqlite;

public class Salesforce {
	@Autowired 
	Environment env;
	@Autowired
	Sqlite database;

    private static String accessToken;
	private static String instance_url;
	private static String sf_restApi;
	private static String sf_oauth2_url;
	private static String sf_client_id;
	private static String sf_client_secret;
	private static String sf_username;
	private static String sf_password;
	private final HttpClient httpClient = HttpClient.newBuilder()
			.version(HttpClient.Version.HTTP_2)
			.build();
	public Salesforce () {
		Salesforce.sf_client_id = env.getProperty("sf_client_id");
		Salesforce.sf_client_secret = env.getProperty("sf_client_secret");
		Salesforce.sf_username = env.getProperty("sf_username");
		Salesforce.sf_password = env.getProperty("sf_password");
		Salesforce.sf_oauth2_url = env.getProperty("sf_oauth2_url");
		Salesforce.sf_restApi = env.getProperty("sf_restApi");
	}
		

	public void save() {
		if (Salesforce.accessToken != null) {
			try {
				Map<Object, Object> data = new HashMap<>();
				// data.put("timestamp", datetime);
				// data.put("voltage", voltage);
				// data.put("current", current);
	
				String response = new Salesforce().sendPost(Salesforce.instance_url + Salesforce.sf_restApi, data, Salesforce.accessToken);
				response = response.substring(1, response.length() - 1);
				response = response.replace("\\", "");
				System.out.println("SF API response: "+response);
				JSONObject responseJson = new JSONObject(response);
				if (responseJson.has("isSuccess") && responseJson.getBoolean("isSuccess")) {
					// new Sqlite(env.getProperty("databaseName")).updateRecord(responseJson.getString("timestamp"), responseJson.getString("Id"));
				} else {
					new Salesforce().setAccessToken();
				}
	
			} catch (Exception e) {
				System.err.println("Exception on serialEvent: "+e);
			}
		} else {
			new Salesforce().setAccessToken();
		}
	
		new Salesforce().setAccessToken();
	}

    

	public void setAccessToken() {
		Map<Object, Object> logInMap = new HashMap<>();
		logInMap.put("grant_type", "password");
		logInMap.put("client_id", Salesforce.sf_client_id);
		logInMap.put("client_secret", Salesforce.sf_client_secret);
		logInMap.put("username", Salesforce.sf_username);
		logInMap.put("password", Salesforce.sf_password);

		try {
			String logInResponse = new Salesforce().sendPost(Salesforce.sf_oauth2_url, logInMap);
			JSONObject logInJson = new JSONObject(logInResponse);
			if (logInJson.has("error")) {
				System.err.println("Response on oauth2: "+logInJson);
			} else {
				String accessToken = (String) logInJson.get("access_token");
				String tokenType = (String)logInJson.get("token_type");
				Salesforce.instance_url = (String)logInJson.get("instance_url");
				Salesforce.accessToken = tokenType + " "+ accessToken;
			}
		} catch (Exception e) {
			System.err.println("Exception on setAccessToken: "+e);
		}
	}
	public String sendPost(String url, Map<Object, Object> data, String accessToken) throws Exception {
		JSONObject dataJson = new JSONObject(data);
		HttpRequest request = HttpRequest.newBuilder()
				.POST(HttpRequest.BodyPublishers.ofString(dataJson.toString()))
				.uri(URI.create(url))
				.setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
				.header("Authorization", accessToken)
				.header("Content-Type", "application/json")
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			System.err.println(response.body());
		}
		return response.body().toString();
	}

	public String sendPost(String url, Map<Object, Object> data) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
				.POST(buildFormDataFromMap(data))
				.uri(URI.create(url))
				.setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
				.header("Content-Type", "application/x-www-form-urlencoded")
				.build();

		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != 200) {
			System.err.println(response.body());
		}
		return response.body().toString();
	}

	public static HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<Object, Object> entry : data.entrySet()) {
			if (builder.length() > 0) {
				builder.append("&");
			}
			builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
			builder.append("=");
			builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
		}
		return HttpRequest.BodyPublishers.ofString(builder.toString());
	}

}
