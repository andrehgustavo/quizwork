package com.mathematics.model;

import androidx.annotation.NonNull;

import com.quizwork.Answer;
import com.quizwork.Question;

import java.io.Serializable;

public class NumericAnswer extends Answer implements Serializable {
	private Double number;

	public NumericAnswer(Double number, Question question) {
		this.number = number;
		this.question = question;
	}

	public NumericAnswer(long id, Double number) {
		this.id = id;
		this.number = number;
	}

	public Double getNumber() {
		return number;
	}

	public void setNumber(Double number) {
		this.number = number;
	}

	@NonNull
	@Override
	public String toString() {
		return number.toString();
	}
}
