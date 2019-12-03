package com.contest.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

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

	public List<QuizAnswer> findByQuiz(Quiz quiz) {
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

	public static AnswerDAO getInstance(Context context) {
		if (answerDAO == null)
			answerDAO = new AnswerDAO(context);
		return answerDAO;
	}
}
