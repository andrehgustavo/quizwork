package com.mathematics.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.quizwork.Question;
import com.quizwork.QuestionAnswer;
import com.quizwork.Quiz;
import com.quizwork.QuizAnswer;
import com.quizwork.User;
import com.mathematics.R;
import com.mathematics.model.NumericAnswer;

public class AnswerQuestionsAdapter extends BaseAdapter {
	private QuizAnswer quizAnswer;
	private LayoutInflater inflater;
	private QuestionAnswer questionAnswer;

	AnswerQuestionsAdapter(Context context, Quiz quiz, User user) {
		this.inflater = LayoutInflater.from(context);
		this.quizAnswer = new QuizAnswer(quiz, user);

		for (Question question : quiz.getQuestions()) {
			quizAnswer.getQuestionAnswers().add(new QuestionAnswer(quizAnswer, question));
		}
	}

	@Override
	@SuppressLint("ViewHolder")
	public View getView(int i, View view, ViewGroup viewGroup) {
		questionAnswer = quizAnswer.getQuestionAnswers().get(i);
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.item_answer_question, viewGroup, false);
		final EditText userAnswer = v.findViewById(R.id.answer_question_input);
		userAnswer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					try {
						questionAnswer.setAnswer(
								new NumericAnswer(Double.valueOf(userAnswer.getText().toString()), questionAnswer.getQuestion()));
					} catch (Exception ignored) {}
				}
			}
		});
		((TextView) v.findViewById(R.id.answer_question_text)).setText(questionAnswer.getQuestion().getText());
		return v;
	}

	@Override
	public int getCount() {
		return quizAnswer.getQuestionAnswers().size();
	}

	@Override
	public Object getItem(int i) {
		return quizAnswer.getQuestionAnswers().get(i);
	}

	@Override
	public long getItemId(int i) {
		return quizAnswer.getQuestionAnswers().get(i).getId();
	}

	QuizAnswer getQuizAnswer() {
		return quizAnswer;
	}
}
