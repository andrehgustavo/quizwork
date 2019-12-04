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

public class AnswerQuestionsAdapter extends BaseAdapter implements View.OnFocusChangeListener {
	private QuizAnswer quizAnswer;
	private LayoutInflater inflater;

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
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.item_answer_question, viewGroup, false);
		EditText userAnswer = v.findViewById(R.id.answer_question_input);
		userAnswer.setOnFocusChangeListener(this);
		QuestionAnswer questionAnswer = quizAnswer.getQuestionAnswers().get(i);
		userAnswer.setTag(questionAnswer);
		if (questionAnswer.getAnswer() != null)
			userAnswer.setText(((NumericAnswer) questionAnswer.getAnswer()).getNumber().toString());
		((TextView) v.findViewById(R.id.answer_question_text)).setText(questionAnswer.getQuestion().getText());
		return v;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus) {
			QuestionAnswer questionAnswer = ((QuestionAnswer) v.getTag());
			try {
				questionAnswer.setAnswer(
						new NumericAnswer(Double.valueOf(((EditText) v).getText().toString()), questionAnswer.getQuestion()));
			} catch (Exception ignored) {
				questionAnswer.setAnswer(null);
			}
		}
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
