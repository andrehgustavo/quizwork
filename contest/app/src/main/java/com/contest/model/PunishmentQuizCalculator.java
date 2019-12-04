package com.contest.model;

import com.quizwork.QuestionAnswer;
import com.quizwork.QuizAnswer;
import com.quizwork.QuizCalculator;

public class PunishmentQuizCalculator implements QuizCalculator {
	@Override
	public void calculate(QuizAnswer quizAnswer) {
		int score = 0;
		for (QuestionAnswer questionAnswer: quizAnswer.getQuestionAnswers()) {
			if (questionAnswer.getScore() == null) {
				quizAnswer.setScore(null);
				return;
			}
			if (questionAnswer.getScore() > 0) {
				score += questionAnswer.getScore();
			} else {
				score -= 5;
			}
		}
		quizAnswer.setScore(score);
	}
}
