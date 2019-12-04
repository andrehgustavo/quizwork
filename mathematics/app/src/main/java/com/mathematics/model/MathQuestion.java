package com.mathematics.model;

import com.quizwork.Answer;
import com.quizwork.Question;
import com.quizwork.ValidationException;

import java.io.Serializable;

public class MathQuestion extends Question implements Serializable {

	public MathQuestion() {}

	public MathQuestion(String text) {
		this.text = text;
	}

	public MathQuestion(long id, String text, Answer correct) {
		this.id = id;
		this.text = text;
		this.correct = correct;
	}

	@Override
	public void validate() throws ValidationException {
		if (correct == null)
			throw new ValidationException("You must select the Correct numericAnswer for each Question");
	}
}
