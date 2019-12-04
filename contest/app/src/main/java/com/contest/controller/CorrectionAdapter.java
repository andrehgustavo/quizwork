package com.contest.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.contest.R;
import com.contest.model.Option;
import com.quizwork.QuestionAnswer;
import com.quizwork.QuizAnswer;

public class CorrectionAdapter extends BaseAdapter implements View.OnFocusChangeListener {
	private QuizAnswer quizAnswer;
	private LayoutInflater inflater;

	CorrectionAdapter(Context context, QuizAnswer quizAnswer) {
		this.inflater = LayoutInflater.from(context);
		this.quizAnswer = quizAnswer;
	}

	@Override
	@SuppressLint("ViewHolder")
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.item_answer_question, viewGroup, false);

		QuestionAnswer questionAnswer = quizAnswer.getQuestionAnswers().get(i);
		((TextView) v.findViewById(R.id.answer_question_text)).setText(questionAnswer.getQuestion().getText());

		TextView answerText = new TextView(inflater.getContext());
		answerText.setText(((Option) questionAnswer.getAnswer()).getText());
		v.addView(answerText);

		EditText input = new EditText(inflater.getContext());
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		input.setHint("Score");
		input.setTag(questionAnswer);
		input.setOnFocusChangeListener(this);
		if (questionAnswer.getScore() != null)
			input.setText(questionAnswer.getScore().toString());
		v.addView(input);
		return v;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus) {
			QuestionAnswer questionAnswer = (QuestionAnswer) v.getTag();
			try {
				questionAnswer.setScore(Integer.parseInt(((EditText) v).getText().toString().trim()));
			} catch (Exception e) {
				questionAnswer.setScore(null);
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
