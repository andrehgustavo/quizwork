package com.contest.model;

import com.quizwork.QuestionAnswer;
import com.quizwork.QuestionCalculator;
import com.quizwork.ValidationException;

public class ObjectiveSubjectiveQuestionCalculator implements QuestionCalculator {
	@Override
	public void calculate(QuestionAnswer questionAnswer) throws ValidationException {
		if (questionAnswer.getAnswer() == null
				|| ((Option) questionAnswer.getAnswer()).getText() == null
				|| ((Option) questionAnswer.getAnswer()).getText().trim().isEmpty())
			throw new ValidationException("Question "
					+ (questionAnswer.getQuizAnswer().getQuestionAnswers().indexOf(questionAnswer)+ 1)
					+ " needs to be answered.");

		if (questionAnswer.getQuestion() instanceof SubjectiveQuestion) {
			questionAnswer.setScore(((SubjectiveQuestion) questionAnswer.getQuestion()).getScore());
		} else {
			if (questionAnswer.getAnswer().getId() == questionAnswer.getQuestion().getCorrect().getId())
				questionAnswer.setScore(10);
			else
				questionAnswer.setScore(0);
		}
	}
}
