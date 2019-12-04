package com.contest.service;

import android.annotation.SuppressLint;
import android.content.Context;

import com.contest.dao.AnswerDAO;
import com.contest.model.ObjectiveSubjectiveQuestionCalculator;
import com.quizwork.QuestionAnswer;
import com.quizwork.QuizAnswer;
import com.quizwork.Quiz;
import com.contest.model.PunishmentQuizCalculator;
import com.quizwork.User;
import com.quizwork.ValidationException;
import com.contest.model.WithContext;

import java.util.List;

public class AnswerService extends WithContext {
	@SuppressLint("StaticFieldLeak")
	private static AnswerService service;

	private AnswerService(Context context) {
		super(context);
	}

	public QuizAnswer create(QuizAnswer quizAnswer) throws ValidationException {
		if (quizAnswer.getQuiz() == null)
			throw new ValidationException("Quiz is required in QuizAnswer");
		if (quizAnswer.getCreator() == null)
			throw new ValidationException("Creator User is required in QuizAnswer");
		if (quizAnswer.getQuestionAnswers().get(0).getQuizAnswer() == null)
			throw new ValidationException("QuizAnswer is required in QuestionAnswer");
		if (quizAnswer.getQuestionAnswers().get(0).getQuestion() == null)
			throw new ValidationException("Question is required in QuestionAnswer");

		quizAnswer.calculateScore(new PunishmentQuizCalculator(), new ObjectiveSubjectiveQuestionCalculator());

		return AnswerDAO.getInstance(context).create(quizAnswer);
	}

	public long countByUser(User user) throws ValidationException {
		if (user == null)
			throw new ValidationException("Invalid user");

		return AnswerDAO.getInstance(context).countByUser(user);
	}

	public long countByQuiz(Quiz quiz) throws ValidationException {
		if (quiz == null)
			throw new ValidationException("Invalid quiz");

		return AnswerDAO.getInstance(context).countByQuiz(quiz);
	}

	public List<QuizAnswer> findAllByQuiz(Quiz quiz) throws ValidationException {
		if (quiz == null || quiz.getId() < 1)
			throw new ValidationException("Invalid quiz");

		return AnswerDAO.getInstance(context).findAllByQuiz(quiz);
	}

	public QuizAnswer findForCorrectionByQuizAnswer(QuizAnswer quizAnswer) throws ValidationException {
		if (quizAnswer == null || quizAnswer.getId() < 1)
			throw new ValidationException("Invalid QuizAnswer");

		return AnswerDAO.getInstance(context).findForCorrectionByQuizAnswer(quizAnswer);
	}

	public QuizAnswer updateCorrection(QuizAnswer quizAnswer) throws ValidationException {
		for (QuestionAnswer questionAnswer: quizAnswer.getQuestionAnswers())
			if (questionAnswer.getScore() == null || questionAnswer.getScore() < 0 || questionAnswer.getScore() > 10)
				throw new ValidationException("All questions must be corrected with values from 0 to 10");

		quizAnswer.calculateScore(new PunishmentQuizCalculator(), new ObjectiveSubjectiveQuestionCalculator());

		return AnswerDAO.getInstance(context).updateCorrection(quizAnswer);
	}

	public static AnswerService getInstance(Context context) {
		if (service == null)
			service = new AnswerService(context);
		return service;
	}
}
