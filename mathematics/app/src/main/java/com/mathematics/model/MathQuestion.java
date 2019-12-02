package com.mathematics.model;

import com.quizwork.Answer;
import com.quizwork.Question;
import com.quizwork.ValidationException;

import java.io.Serializable;

public class MathQuestion extends Question implements Serializable {
	private NumericAnswer numericAnswer;

	public MathQuestion() {}

	public MathQuestion(String text) {
		this.text = text;
		this.numericAnswer = null;
	}

	public MathQuestion(long id, String text, Answer correct) {
		this.id = id;
		this.text = text;
		this.correct = correct;
		this.numericAnswer = null;
	}

	@Override
	public void validate() throws ValidationException {
		if (numericAnswer == null)
			throw new ValidationException("At least one Options are required for each Question");
		if (numericAnswer.getNumber() == null)
				throw new ValidationException("A answer is required");

		if (correct == null)
			throw new ValidationException("You must select the Correct numericAnswer for each Question");
	}

	public NumericAnswer getCorrectAnswer() {
		return numericAnswer;
	}

	public void setCorrectAnswer(NumericAnswer numericAnswer) {
		this.numericAnswer = numericAnswer;
	}
}
