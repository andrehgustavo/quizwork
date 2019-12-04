package com.quizwork;

import android.content.Context;
import android.widget.Toast;

public class ValidationException extends Exception {
	public ValidationException(String msg) {
		super(msg);
	}

	public void show(Context activityContext) {
		Toast.makeText(activityContext.getApplicationContext(), getMessage(), Toast.LENGTH_LONG).show();
	}
}
