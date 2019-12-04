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
import android.widget.LinearLayout;
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
			case R.id.create_question_remove:
				questions.remove(i);
				notifyDataSetChanged();
		}
	}

	void showDialog() {
		LinearLayout layout = new LinearLayout(inflater.getContext());
		layout.setOrientation(LinearLayout.VERTICAL);

		final EditText inputQuestion = new EditText(inflater.getContext());
		inputQuestion.setSingleLine(true);
		inputQuestion.setHint("Add a Question.");
		layout.addView(inputQuestion);

		final EditText inputAnswer = new EditText(inflater.getContext());
		inputAnswer.setSingleLine(true);
		inputAnswer.setHint("Add a Answer.");
		inputAnswer.setInputType(InputType.TYPE_CLASS_NUMBER);
		inputAnswer.setRawInputType(Configuration.KEYBOARD_12KEY);
		layout.addView(inputAnswer);

		final EditText inputWeight = new EditText(inflater.getContext());
		inputWeight.setSingleLine(true);
		inputWeight.setHint("Add a question's weight.");
		inputWeight.setInputType(InputType.TYPE_CLASS_NUMBER);
		inputWeight.setRawInputType(Configuration.KEYBOARD_12KEY);
		layout.addView(inputWeight);

		new AlertDialog.Builder(inflater.getContext())
				.setTitle("New Question")
				.setView(layout)
				.setPositiveButton("Add", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String textQuestion = inputQuestion.getText().toString().trim();
						String textAnswer = inputAnswer.getText().toString().trim();
						String textWeight = inputWeight.getText().toString().trim();
						if (textQuestion.isEmpty() || textAnswer.isEmpty() || textWeight.isEmpty())
							return;

						else
							questions.add(new MathQuestion(textQuestion));
							questions.get(questions.size()-1).setCorrect(new NumericAnswer(Double.valueOf(textAnswer), questions.get(questions.size()-1)));
							notifyDataSetChanged();
							Toast.makeText(inflater.getContext(), "Question added", Toast.LENGTH_LONG).show();

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
