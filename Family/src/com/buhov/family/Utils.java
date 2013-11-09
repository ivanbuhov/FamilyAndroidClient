package com.buhov.family;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import com.buhov.family.FamilyHttpClient.Entities.User;
import com.buhov.family.FamilyHttpClient.Entities.UserDTO;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

public class Utils {
	
	private static final int MIN_USERNAME_LENGTH = 5;
	private static final int MAX_USERNAME_LENGTH = 30;
	private static final int MIN_PASSWORD_LENGTH = 5;
	private static final int MAX_PASSWORD_LENGTH = 30;
	
	public static boolean validateUserInput(Context context, EditText usernameView, EditText passwordView) {
		
		Resources res = context.getResources();
		
		String username = usernameView.getText().toString();
		String password = passwordView.getText().toString();
		
		boolean valid = true;
		View focusView = passwordView;

		// Check for a valid password.
		if (TextUtils.isEmpty(password)) {
			passwordView.setError(res.getString(R.string.error_field_required));
			focusView = passwordView;
			valid = false;
		} else if (password.length() < MIN_PASSWORD_LENGTH) {
			passwordView.setError(res.getString(R.string.error_short_password));
			focusView = passwordView;
			valid = false;
		} else if(password.length() > MAX_PASSWORD_LENGTH) {
			passwordView.setError(res.getString(R.string.error_long_password));
			focusView = passwordView;
			valid = false;
		}else if (TextUtils.isEmpty(username)) {
			usernameView.setError(res.getString(R.string.error_field_required));
			focusView = usernameView;
			valid = false;
		} else if (username.length() < MIN_USERNAME_LENGTH) {
			usernameView.setError(res.getString(R.string.error_short_username));
			focusView = usernameView;
			valid = false;
		} else if(username.length() > MAX_USERNAME_LENGTH) {
			usernameView.setError(res.getString(R.string.error_long_username));
			focusView = usernameView;
			valid = false;
		}
		
		if(!valid) {
			focusView.requestFocus();
		}
		
		return valid;
	}
	
	public static User encryptUser(UserDTO user) {
		String authCode = sha1( user.getUsername() + sha1(user.getPassword()) );
		return new User(user.getUsername(),  authCode);
	}
	
	private static String sha1(String text)
	{
	    String sha1 = "";
	    try
	    {
	        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
	        crypt.reset();
	        crypt.update(text.getBytes("UTF-8"));
	        sha1 = byteToHex(crypt.digest());
	    }
	    catch(NoSuchAlgorithmException e)
	    {
	        e.printStackTrace();
	    }
	    catch(UnsupportedEncodingException e)
	    {
	        e.printStackTrace();
	    }
	    return sha1;
	}

	private static String byteToHex(final byte[] hash)
	{
	    Formatter formatter = new Formatter();
	    for (byte b : hash)
	    {
	        formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}
}