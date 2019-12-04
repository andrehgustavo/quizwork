package com.mathematics.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.quizwork.Category;
import com.mathematics.model.NumericAnswer;
import com.mathematics.model.MathQuestion;
import com.quizwork.Quiz;
import com.quizwork.User;

import java.util.ArrayList;
import java.util.List;

import static com.mathematics.dao.DAO.*;

public class QuizDAO extends WithDAO {
	private static QuizDAO quizDAO;

	private QuizDAO(Context context) {
		super(context);
	}

	public Quiz create(Quiz quiz) {
		SQLiteDatabase db = dao.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(QUIZ_NAME, quiz.getName());
		values.put(QUIZ_OPEN, quiz.isOpen());
		values.put(QUIZ_CODE, quiz.getCode());
		values.put(QUIZ_START, getDate(quiz.getStart()));
		values.put(QUIZ_END, getDate(quiz.getEnd()));
		values.put(QUIZ_CATEGORY, quiz.getCategory().getId());
		values.put(QUIZ_USER, quiz.getCreator().getId());

		quiz.setId(db.insert(QUIZ_TABLE, null, values));
		return quiz;
	}

	public List<Quiz> findAllByUser(User user) {
		SQLiteDatabase db = dao.getReadableDatabase();
		Cursor c = db.query(QUIZ_TABLE, null, QUIZ_USER + " = ?",
				new String[]{String.valueOf(user.getId())},
				null, null, null);

		List<Quiz> result = new ArrayList<>();
		while (c.moveToNext()) {
			result.add(new Quiz(
					c.getLong(c.getColumnIndex(QUIZ_ID)),
					c.getString(c.getColumnIndex(QUIZ_NAME)),
					c.getInt(c.getColumnIndex(QUIZ_OPEN)) == 1,
					c.getInt(c.getColumnIndex(QUIZ_CODE)),
					getDate(c.getString(c.getColumnIndex(QUIZ_START))),
					getDate(c.getString(c.getColumnIndex(QUIZ_END))),
					new Category(c.getLong(c.getColumnIndex(QUIZ_CATEGORY))),
					new User(c.getLong(c.getColumnIndex(QUIZ_USER)))));
		}
		c.close();
		return result;
	}

	public Quiz findByCode(String code) {
		SQLiteDatabase db = dao.getReadableDatabase();
		String sql = "SELECT * FROM " + QUIZ_TABLE + "," + QUESTION_TABLE + "," + NUMERIC_ANSWER_TABLE + " WHERE "
				+ QUIZ_ID + "=" + QUESTION_QUIZ + " AND " + QUESTION_ID + "=" + NUMERIC_ANSWER_QUESTION + " AND " + QUIZ_CODE + "= ?";
		Cursor c = db.rawQuery(sql, new String[]{code});

		Quiz result = null;
		MathQuestion lastQuestion = new MathQuestion();
		while (c.moveToNext()) {
			if (result == null) {
				result = new Quiz(
						c.getLong(c.getColumnIndex(QUIZ_ID)),
						c.getString(c.getColumnIndex(QUIZ_NAME)),
						c.getInt(c.getColumnIndex(QUIZ_OPEN)) == 1,
						c.getInt(c.getColumnIndex(QUIZ_CODE)),
						getDate(c.getString(c.getColumnIndex(QUIZ_START))),
						getDate(c.getString(c.getColumnIndex(QUIZ_END))),
						new Category(c.getLong(c.getColumnIndex(QUIZ_CATEGORY))),
						new User(c.getLong(c.getColumnIndex(QUIZ_USER))));
			}
			if (lastQuestion.getId() != c.getLong(c.getColumnIndex(QUESTION_ID))) {
				lastQuestion = new MathQuestion(
						c.getLong(c.getColumnIndex(QUESTION_ID)),
						c.getString(c.getColumnIndex(QUESTION_TEXT)),
						new NumericAnswer(c.getLong(c.getColumnIndex(QUESTION_NUMERIC_ANSWER)), null));
				result.getQuestions().add(lastQuestion);
			}
			lastQuestion.setCorrectAnswer((new NumericAnswer(c.getLong(c.getColumnIndex(NUMERIC_ANSWER_ID)), c.getDouble(c.getColumnIndex(NUMERIC_ANSWER_NUMBER)))));
		}
		c.close();
		return result;
	}

	public List<Quiz> findAllByName(String text) {
		SQLiteDatabase db = dao.getReadableDatabase();
		String sql = "SELECT * FROM " + QUIZ_TABLE + "," + QUESTION_TABLE + "," + NUMERIC_ANSWER_TABLE + " WHERE "
				+ QUIZ_ID + "=" + QUESTION_QUIZ + " AND " + QUESTION_ID + "=" + NUMERIC_ANSWER_QUESTION
				+ " AND " + QUIZ_OPEN + " == 1 AND " + QUIZ_NAME + " LIKE ?";
		Cursor c = db.rawQuery(sql, new String[]{"%" + text + "%"});

		ArrayList<Quiz> result = new ArrayList<>();
		Quiz lastQuiz = new Quiz();
		MathQuestion lastQuestion = new MathQuestion();
		while (c.moveToNext()) {
			if (lastQuiz.getId() != c.getLong(c.getColumnIndex(QUIZ_ID))) {
				lastQuiz = new Quiz(
						c.getLong(c.getColumnIndex(QUIZ_ID)),
						c.getString(c.getColumnIndex(QUIZ_NAME)),
						c.getInt(c.getColumnIndex(QUIZ_OPEN)) == 1,
						c.getInt(c.getColumnIndex(QUIZ_CODE)),
						getDate(c.getString(c.getColumnIndex(QUIZ_START))),
						getDate(c.getString(c.getColumnIndex(QUIZ_END))),
						new Category(c.getLong(c.getColumnIndex(QUIZ_CATEGORY))),
						new User(c.getLong(c.getColumnIndex(QUIZ_USER))));
				result.add(lastQuiz);
			}
			if (lastQuestion.getId() != c.getLong(c.getColumnIndex(QUESTION_ID))) {
				lastQuestion = new MathQuestion(
						c.getLong(c.getColumnIndex(QUESTION_ID)),
						c.getString(c.getColumnIndex(QUESTION_TEXT)),
						new NumericAnswer(c.getLong(c.getColumnIndex(QUESTION_NUMERIC_ANSWER)), c.getDouble(c.getColumnIndex(QUESTION_NUMERIC_ANSWER))));
				lastQuiz.getQuestions().add(lastQuestion);
			}
			lastQuestion.setCorrectAnswer((new NumericAnswer(c.getLong(c.getColumnIndex(NUMERIC_ANSWER_ID)), c.getDouble(c.getColumnIndex(NUMERIC_ANSWER_NUMBER)))));
		}
		c.close();
		return result;
	}

	public static QuizDAO getInstance(Context context) {
		if (quizDAO == null)
			quizDAO = new QuizDAO(context);
		return quizDAO;
	}
}
