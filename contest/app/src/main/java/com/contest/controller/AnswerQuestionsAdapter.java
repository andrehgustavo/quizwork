package com.contest.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.contest.R;
import com.contest.model.ObjectiveQuestion;
import com.quizwork.QuizAnswer;
import com.quizwork.QuestionAnswer;
import com.contest.model.Option;
import com.quizwork.Question;
import com.quizwork.Quiz;
import com.quizwork.User;

public class AnswerQuestionsAdapter extends BaseAdapter implements View.OnClickListener, View.OnFocusChangeListener {
	private QuizAnswer quizAnswer;
	private LayoutInflater inflater;
	private int itemBackgroundResource;

	AnswerQuestionsAdapter(Context context, Quiz quiz, User user) {
		this.inflater = LayoutInflater.from(context);
		this.quizAnswer = new QuizAnswer(quiz, user);
		for (Question question : quiz.getQuestions()) {
			quizAnswer.getQuestionAnswers().add(new QuestionAnswer(quizAnswer, question));
		}
		TypedValue outValue = new TypedValue();
		context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
		itemBackgroundResource = outValue.resourceId;
	}

	@Override
	@SuppressLint("ViewHolder")
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewGroup v = (ViewGroup) inflater.inflate(R.layout.item_answer_question, viewGroup, false);
		QuestionAnswer questionAnswer = quizAnswer.getQuestionAnswers().get(i);
		((TextView) v.findViewById(R.id.answer_question_text)).setText(questionAnswer.getQuestion().getText());
		if (questionAnswer.getQuestion() instanceof ObjectiveQuestion) {
			for (Option op : ((ObjectiveQuestion) questionAnswer.getQuestion()).getOptions()) {
				CheckedTextView item = (CheckedTextView) inflater.inflate(android.R.layout.simple_list_item_single_choice, viewGroup, false);
				item.setText(op.getText());
				item.setBackgroundResource(itemBackgroundResource);
				item.setTag(new Object[]{questionAnswer, op, v});
				item.setOnClickListener(this);
				v.addView(item);
			}
		} else {
			EditText input = new EditText(inflater.getContext());
			input.setLines(3);
			input.setHint("Text answer");
			input.setTag(questionAnswer);
			input.setOnFocusChangeListener(this);
			if (questionAnswer.getAnswer() != null)
				input.setText(((Option) questionAnswer.getAnswer()).getText());
			v.addView(input);
		}
		return v;
	}

	@Override
	public void onClick(View view) {
		Object[] item = (Object[]) view.getTag();
		((QuestionAnswer) item[0]).setAnswer((Option) item[1]);
		ViewGroup v = (ViewGroup) item[2];
		for (int i = 1; i < v.getChildCount(); ++i) {
			((CheckedTextView) v.getChildAt(i)).setChecked(false);
		}
		((CheckedTextView) view).setChecked(true);
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		if (!hasFocus) {
			QuestionAnswer questionAnswer = (QuestionAnswer) view.getTag();
			questionAnswer.setAnswer(
					new Option(((EditText) view).getText().toString(), questionAnswer.getQuestion()));
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
