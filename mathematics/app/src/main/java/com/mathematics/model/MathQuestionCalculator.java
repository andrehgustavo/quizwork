package com.mathematics.model;

import android.util.Log;

import com.quizwork.QuestionAnswer;
import com.quizwork.QuestionCalculator;
import com.quizwork.ValidationException;

public class MathQuestionCalculator implements QuestionCalculator {
	@Override
	public void calculate(QuestionAnswer questionAnswer) throws ValidationException {
		Log.d("Math", "UserAnswer: " + ((NumericAnswer) questionAnswer.getAnswer()).getNumber());
		Log.d("Math", "Correct: " + ((NumericAnswer)questionAnswer.getQuestion().getCorrect()).getNumber());
		if (questionAnswer.getAnswer() == null)
			throw new ValidationException("Question "
					+ (questionAnswer.getQuizAnswer().getQuestionAnswers().indexOf(questionAnswer)+ 1)
					+ " needs to be answered.");

		if (((NumericAnswer) questionAnswer.getAnswer()).getNumber().equals(((NumericAnswer) questionAnswer.getQuestion().getCorrect()).getNumber()))
			questionAnswer.setScore(1);
		else
			questionAnswer.setScore(0);
	}
}
