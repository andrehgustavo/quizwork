package com.mathematics.model;

import com.quizwork.QuestionAnswer;
import com.quizwork.QuizAnswer;
import com.quizwork.QuizCalculator;

public class WeightedAverageQuizCalculator implements QuizCalculator {
	@Override
	public void calculate(QuizAnswer quizAnswer) {
		int score = 0;
		for (QuestionAnswer questionAnswer: quizAnswer.getQuestionAnswers())
			score += questionAnswer.getScore();
		quizAnswer.setScore(score / quizAnswer.getQuestionAnswers().size());
	}
}
