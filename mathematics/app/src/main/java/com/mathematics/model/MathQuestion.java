package com.mathematics.model;

import com.quizwork.Answer;
import com.quizwork.Question;
import com.quizwork.ValidationException;

import java.io.Serializable;

public class MathQuestion extends Question implements Serializable {
	private Integer weight;

	public MathQuestion(String text) {
		this.text = text;
	}

	public MathQuestion(long id, String text, int weight, Answer correct) {
		this.id = id;
		this.text = text;
		this.correct = correct;
		this.weight = weight;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	@Override
	public void validate() throws ValidationException {
		if (correct == null)
			throw new ValidationException("You need answer all the questions");
	}
}
