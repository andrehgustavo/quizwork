package com.contest.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.contest.model.Option;
import com.contest.model.SubjectiveQuestion;
import com.quizwork.QuestionAnswer;
import com.quizwork.QuizAnswer;
import com.quizwork.Quiz;
import com.quizwork.User;

import java.util.ArrayList;
import java.util.List;

import static com.contest.dao.DAO.*;

public class AnswerDAO extends WithDAO {
	private static AnswerDAO answerDAO;

	private AnswerDAO(Context context) {
		super(context);
	}

	public QuizAnswer create(QuizAnswer quizAnswer) {
		SQLiteDatabase db = dao.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(QUIZ_ANSWER_QUIZ, quizAnswer.getQuiz().getId());
		values.put(QUIZ_ANSWER_CREATOR, quizAnswer.getCreator().getId());
		values.put(QUIZ_ANSWER_SCORE, quizAnswer.getScore());
		quizAnswer.setId(db.insert(QUIZ_ANSWER_TABLE, null, values));

		for (QuestionAnswer aq : quizAnswer.getQuestionAnswers()) {
			if (aq.getQuestion() instanceof SubjectiveQuestion) {
				values.clear();
				values.put(OPTION_TEXT, ((Option) aq.getAnswer()).getText());
				aq.getAnswer().setId(db.insert(OPTION_TABLE, null, values));
			}
			values.clear();
			values.put(QUESTION_ANSWER_ANSWER, quizAnswer.getId());
			values.put(QUESTION_ANSWER_QUESTION, aq.getQuestion().getId());
			values.put(QUESTION_ANSWER_OPTION, aq.getAnswer().getId());
			values.put(QUESTION_ANSWER_SCORE, aq.getScore());
			aq.setId(db.insert(QUESTION_ANSWER_TABLE, null, values));
		}
		return quizAnswer;
	}

	public long countByUser(User user) {
		return DatabaseUtils.queryNumEntries(dao.getReadableDatabase(), QUIZ_ANSWER_TABLE, QUIZ_ANSWER_CREATOR + "=" + user.getId());
	}

	public long countByQuiz(Quiz quiz) {
		return DatabaseUtils.queryNumEntries(dao.getReadableDatabase(), QUIZ_ANSWER_TABLE, QUIZ_ANSWER_QUIZ + "=" + quiz.getId());
	}

	public List<QuizAnswer> findAllByQuiz(Quiz quiz) {
		SQLiteDatabase db = dao.getReadableDatabase();
		String sql = "SELECT * FROM "
				+ QUIZ_ANSWER_TABLE + "," + USER_TABLE + " WHERE "
				+ QUIZ_ANSWER_CREATOR + "=" + USER_ID + " AND " + QUIZ_ANSWER_QUIZ + "= ?"
				+ " ORDER BY " + QUIZ_ANSWER_SCORE + " DESC";
		Cursor c = db.rawQuery(sql, new String[]{String.valueOf(quiz.getId())});

		List<QuizAnswer> result = new ArrayList<>();
		QuizAnswer quizAnswer;
		while (c.moveToNext()) {
			quizAnswer = new QuizAnswer(
					c.getLong(c.getColumnIndex(QUIZ_ANSWER_ID)),
					c.getLong(c.getColumnIndex(QUIZ_ANSWER_QUIZ)),
					new User(
							c.getLong(c.getColumnIndex(USER_ID)),
							c.getString(c.getColumnIndex(USER_NAME)),
							null));
			if (!c.isNull(c.getColumnIndex(QUIZ_ANSWER_SCORE)))
				quizAnswer.setScore(c.getInt(c.getColumnIndex(QUIZ_ANSWER_SCORE)));
			result.add(quizAnswer);
		}
		c.close();
		return result;
	}

	public QuizAnswer findForCorrectionByQuizAnswer(QuizAnswer quizAnswer) {
		SQLiteDatabase db = dao.getReadableDatabase();
		String sql = "SELECT * FROM " + QUIZ_ANSWER_TABLE
				+ " JOIN " + QUIZ_TABLE + " ON " + QUIZ_ANSWER_QUIZ + "=" + QUIZ_ID
				+ " JOIN " + QUESTION_ANSWER_TABLE + " ON " + QUIZ_ANSWER_ID + "=" + QUESTION_ANSWER_ANSWER
				+ " JOIN " + OPTION_TABLE + " ON " + QUESTION_ANSWER_OPTION + "=" + OPTION_ID
				+ " JOIN " + QUESTION_TABLE + " ON " + QUESTION_ANSWER_QUESTION + "=" + QUESTION_ID
				+ " WHERE " + QUIZ_ANSWER_ID + "= ?"
				+ " ORDER BY " + QUESTION_ID;
		Cursor c = db.rawQuery(sql, new String[]{String.valueOf(quizAnswer.getId())});

		QuizAnswer quizAnswerResult = null;
		while (c.moveToNext()) {
			if (quizAnswerResult == null) {
				quizAnswerResult = new QuizAnswer(
						c.getLong(c.getColumnIndex(QUIZ_ANSWER_ID)),
						c.getString(c.getColumnIndex(QUIZ_NAME)));
			}
			quizAnswerResult.getQuestionAnswers().add(
					new QuestionAnswer(c.getLong(c.getColumnIndex(QUESTION_ANSWER_ID)),
							new SubjectiveQuestion(c.getString(c.getColumnIndex(QUESTION_TEXT))),
							new Option(0, c.getString(c.getColumnIndex(OPTION_TEXT)))));
		}
		c.close();
		return quizAnswerResult;
	}

	public QuizAnswer updateCorrection(QuizAnswer quizAnswer) {
		SQLiteDatabase db = dao.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(QUIZ_ANSWER_SCORE, quizAnswer.getScore());
		db.update(QUIZ_ANSWER_TABLE, values, QUIZ_ANSWER_ID + "=" + quizAnswer.getId(), null);

		for (QuestionAnswer questionAnswer: quizAnswer.getQuestionAnswers()) {
			values.clear();
			values.put(QUESTION_ANSWER_SCORE, questionAnswer.getScore());
			db.update(QUESTION_ANSWER_TABLE, values, QUESTION_ANSWER_ID + "=" + questionAnswer.getId(), null);
		}
		return quizAnswer;
	}

	public static AnswerDAO getInstance(Context context) {
		if (answerDAO == null)
			answerDAO = new AnswerDAO(context);
		return answerDAO;
	}
}
