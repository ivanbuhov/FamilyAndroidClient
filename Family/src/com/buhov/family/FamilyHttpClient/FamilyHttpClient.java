package com.buhov.family.FamilyHttpClient;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;

import com.buhov.family.FamilyApplication;
import com.buhov.family.FamilyHttpClient.Entities.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class FamilyHttpClient {
	
	private static final String HEADER_USERNAME = "X-Family-Username";
	private static final String HEADER_AUTHCODE = "X-Family-AuthCode";
	
	private static final String SERVICE_ROOT_URL = "http://family.apphb.com/api/";
	private static final String SERVICE_REGISTER_URL = SERVICE_ROOT_URL + "Users/Register";
	private static final String SERVICE_LOGIN_URL = SERVICE_ROOT_URL + "Users/Login";
	private static final String SERVICE_REDIGREES_URL = SERVICE_ROOT_URL + "Pedigrees";
	
	private FamilyApplication app;
	private HttpClient client;
	private Gson gson;
	
	public FamilyHttpClient(FamilyApplication application) {
		this.app = application;
		this.client = new DefaultHttpClient();
		this.gson = new Gson();
	}

	public User Register(UserDTO user) {
		return this.PostRequest(SERVICE_REGISTER_URL, user, User.class, null);
	}
	
	public User Login(UserDTO user) {
		return this.PostRequest(SERVICE_LOGIN_URL, user, User.class, null);
	}
	
	public Pedigree[] GetPedigrees(User user) {
		return this.GetRequest(SERVICE_REDIGREES_URL, Pedigree[].class, user);
	}
	
	private <TResult> TResult GetRequest(String url, Class<TResult> responseEntity, User user) {
		HttpGet request = new HttpGet(url);
		if(user != null) {
			this.addAuthenticationHeaders(request, user);
		}
		
		return this.Request(request, responseEntity);
	}
	
	private <TResult> TResult DeleteRequest(String url, Class<TResult> responseEntity, User user) {
		HttpDelete request = new HttpDelete(url);
		if(user != null) {
			this.addAuthenticationHeaders(request, user);
		}
		
		return this.Request(request, responseEntity);
	}
	
	private <TBody, TResult> TResult PostRequest(String url, TBody entityToSend, Class<TResult> responseEntity, User user) {
		HttpPost request = new HttpPost(url);
		if(user != null) {
			this.addAuthenticationHeaders(request, user);
		}
		try {
			String entityJSON = this.gson.toJson(entityToSend);
			request.setEntity(new StringEntity(entityJSON));
			return this.Request(request, responseEntity);
		}
		catch(UnsupportedEncodingException e) {
			throw new FamilyServiceException("Problem occured while preparing to send the data. Please try again.");
		}
	}
	
	private <TBody, TResult> TResult PutRequest(String url, TBody entityToSend, Class<TResult> responseEntity, User user) throws UnsupportedEncodingException {
		HttpPut request = new HttpPut(url);
		if(user != null) {
			this.addAuthenticationHeaders(request, user);
		}
		
		String entityJSON = this.gson.toJson(entityToSend);
		request.setEntity(new StringEntity(entityJSON));
		return this.Request(request, responseEntity);
	}
	
	private <TResult> TResult Request(HttpUriRequest request, Class<TResult> responseEntity) {
		if(!this.app.isNetworkConnected()) {
			throw new FamilyServiceException("There is no network access.");
		}
		
		HttpResponse response;
		StringBuilder json = new StringBuilder();
		try {
			request.addHeader("Accept", "application/json");
			request.addHeader("Content-Type", "application/json");
			response = this.client.execute(request);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));
			String line;
			while ((line = reader.readLine()) != null) {
				json.append(line);
			}
		}
		catch(Exception e) {
			throw new FamilyServiceException("Problem occured while connecting to the server. Please check your internet connection and try again.");
		}
		
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST) {
			FamilyServiceError error = this.gson.fromJson(json.toString(), FamilyServiceError.class);
			throw new FamilyServiceException(error.getDisplayMessage());
		}
		
		TResult result = this.gson.fromJson(json.toString(), responseEntity);
		return result;
	}
	
	private void addAuthenticationHeaders(HttpRequest request, User user) {
		request.addHeader(HEADER_USERNAME, user.getUsername());
		request.addHeader(HEADER_AUTHCODE, user.getAuthCode());
	}
}
