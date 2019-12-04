package com.mathematics.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.mathematics.R;
import com.mathematics.model.NumericAnswer;
import com.mathematics.model.MathQuestion;
import com.quizwork.Question;

import java.util.ArrayList;
import java.util.List;

public class CreateQuestionsAdapter extends BaseAdapter implements View.OnClickListener {
	private List<Question> questions = new ArrayList<>();
	private LayoutInflater inflater;

	CreateQuestionsAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	@SuppressLint("ViewHolder")
	public View getView(int i, View view, ViewGroup viewGroup) {
		View v = inflater.inflate(R.layout.item_create_question, viewGroup, false);
		((TextView) v.findViewById(R.id.create_question_text)).setText(questions.get(i).getText());
		View add = v.findViewById(R.id.create_question_add_option);
		add.setOnClickListener(this);
		add.setTag(i);
		View remove = v.findViewById(R.id.create_question_remove);
		remove.setOnClickListener(this);
		remove.setTag(i);
		Question question = questions.get(i);
		if (question.getCorrect() != null)
			((TextView) v.findViewById(R.id.create_question_answer)).setText(((NumericAnswer) question.getCorrect()).getNumber().toString());

		return v;
	}

	@Override
	public void onClick(View view) {
		int i = (int) view.getTag();
		switch (view.getId()) {
			case R.id.create_question_add_option:
				showDialog(questions.get(i));
				break;
			case R.id.create_question_remove:
				questions.remove(i);
				notifyDataSetChanged();
		}
	}

	void showDialog(final Question question) {
		final EditText input = new EditText(inflater.getContext());
		input.setSingleLine(true);
		if (question != null) {
			input.setInputType(InputType.TYPE_CLASS_NUMBER);
			input.setRawInputType(Configuration.KEYBOARD_12KEY);
		}
		new AlertDialog.Builder(inflater.getContext())
				.setTitle("New " + (question == null ? "Question" : "NumericAnswer"))
				.setView(input)
				.setPositiveButton("Add", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String text = input.getText().toString().trim();
						if (text.isEmpty())
							return;

						if (question == null) {
							questions.add(new MathQuestion(text));
							Toast.makeText(inflater.getContext(), "Question added", Toast.LENGTH_LONG).show();
						} else {
							question.setCorrect(new NumericAnswer(Double.valueOf(text), question));
							notifyDataSetChanged();
							Toast.makeText(inflater.getContext(), "NumericAnswer added", Toast.LENGTH_LONG).show();
						}
					}
				})
				.show();
	}



	@Override
	public int getCount() {
		return questions.size();
	}

	@Override
	public Object getItem(int i) {
		return questions.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	List<Question> getQuestions() {
		return questions;
	}
}
