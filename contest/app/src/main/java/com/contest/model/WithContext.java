package com.contest.model;

import android.content.Context;

public abstract class WithContext {
	protected Context context;

	protected WithContext(Context context) {
		this.context = context;
	}
}
