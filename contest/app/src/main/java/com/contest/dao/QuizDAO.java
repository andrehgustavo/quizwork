package com.contest.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.contest.model.ObjectiveQuestion;
import com.contest.model.Option;
import com.contest.model.SubjectiveQuestion;
import com.quizwork.Category;
import com.quizwork.Question;
import com.quizwork.Quiz;
import com.quizwork.User;

import java.util.ArrayList;
import java.util.List;

import static com.contest.dao.DAO.*;

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
		String sql = "SELECT * FROM " + QUIZ_TABLE + " JOIN " + QUESTION_TABLE + " ON " + QUIZ_ID + "=" + QUESTION_QUIZ
				+ " LEFT OUTER JOIN " + OPTION_TABLE + " ON " + QUESTION_ID + "=" + OPTION_QUESTION
				+ " WHERE " + QUIZ_CODE + " = ?";
		Cursor c = db.rawQuery(sql, new String[]{code});

		Quiz result = null;
		Question lastQuestion = new SubjectiveQuestion();
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
			if (c.isNull(c.getColumnIndex(OPTION_ID))) {
				lastQuestion = new SubjectiveQuestion(
						c.getLong(c.getColumnIndex(QUESTION_ID)),
						c.getString(c.getColumnIndex(QUESTION_TEXT)));
				result.getQuestions().add(lastQuestion);
			} else {
				if (lastQuestion.getId() != c.getLong(c.getColumnIndex(QUESTION_ID))) {
					lastQuestion = new ObjectiveQuestion(
							c.getLong(c.getColumnIndex(QUESTION_ID)),
							c.getString(c.getColumnIndex(QUESTION_TEXT)),
							new Option(c.getLong(c.getColumnIndex(QUESTION_OPTION)), null));
					result.getQuestions().add(lastQuestion);
				}
				((ObjectiveQuestion) lastQuestion).getOptions().add(new Option(c.getLong(c.getColumnIndex(OPTION_ID)), c.getString(c.getColumnIndex(OPTION_TEXT))));
			}
		}
		c.close();
		return result;
	}

	public List<Quiz> findAllByName(String text) {
		SQLiteDatabase db = dao.getReadableDatabase();
		String sql = "SELECT * FROM " + QUIZ_TABLE + " JOIN " + QUESTION_TABLE + " ON " + QUIZ_ID + "=" + QUESTION_QUIZ
				+ " LEFT OUTER JOIN " + OPTION_TABLE + " ON " + QUESTION_ID + "=" + OPTION_QUESTION
				+ " WHERE " + QUIZ_OPEN + " == 1 AND " + QUIZ_NAME + " LIKE ?";
		Cursor c = db.rawQuery(sql, new String[]{"%" + text + "%"});

		ArrayList<Quiz> result = new ArrayList<>();
		Quiz lastQuiz = new Quiz();
		Question lastQuestion = new SubjectiveQuestion();
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
			if (c.isNull(c.getColumnIndex(OPTION_ID))) {
				lastQuestion = new SubjectiveQuestion(
						c.getLong(c.getColumnIndex(QUESTION_ID)),
						c.getString(c.getColumnIndex(QUESTION_TEXT)));
				lastQuiz.getQuestions().add(lastQuestion);
			} else {
				if (lastQuestion.getId() != c.getLong(c.getColumnIndex(QUESTION_ID))) {
					lastQuestion = new ObjectiveQuestion(
							c.getLong(c.getColumnIndex(QUESTION_ID)),
							c.getString(c.getColumnIndex(QUESTION_TEXT)),
							new Option(c.getLong(c.getColumnIndex(QUESTION_OPTION)), null));
					lastQuiz.getQuestions().add(lastQuestion);
				}
				((ObjectiveQuestion) lastQuestion).getOptions().add(new Option(c.getLong(c.getColumnIndex(OPTION_ID)), c.getString(c.getColumnIndex(OPTION_TEXT))));
			}
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
