package com.contest.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.contest.R;
import com.contest.model.ObjectiveQuestion;
import com.contest.model.Option;
import com.contest.model.SubjectiveQuestion;
import com.quizwork.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateQuestionsAdapter extends BaseAdapter implements View.OnClickListener, AdapterView.OnItemClickListener {
	private List<Question> questions = new ArrayList<>();
	private LayoutInflater inflater;

	CreateQuestionsAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	@SuppressLint("ViewHolder")
	public View getView(int i, View view, ViewGroup viewGroup) {
		LinearLayout v = (LinearLayout) inflater.inflate(R.layout.item_create_question, viewGroup, false);
		((TextView) v.findViewById(R.id.create_question_text)).setText(questions.get(i).getText());
		View remove = v.findViewById(R.id.create_question_remove);
		remove.setOnClickListener(this);
		remove.setTag(i);
		View add = v.findViewById(R.id.create_question_add_option);
		ListView optionsView = v.findViewById(R.id.create_question_options);
		if (questions.get(i) instanceof ObjectiveQuestion) {
			add.setOnClickListener(this);
			add.setTag(i);
			optionsView.setAdapter(new ArrayAdapter<>(
					inflater.getContext(),
					android.R.layout.simple_list_item_single_choice,
					((ObjectiveQuestion) questions.get(i)).getOptions()));
			optionsView.setOnItemClickListener(this);
		} else {
			add.setVisibility(View.INVISIBLE);
			v.removeView(optionsView);
		}
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
		LinearLayout layout = new LinearLayout(inflater.getContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		final EditText input = new EditText(inflater.getContext());
		input.setSingleLine(true);
		layout.addView(input);
		final Spinner select = new Spinner(inflater.getContext());
		if (question == null) {
			ArrayAdapter adapter = new ArrayAdapter(inflater.getContext(), android.R.layout.simple_spinner_item,
					Arrays.asList("Objective", "Subjective"));
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			select.setAdapter(adapter);
			layout.addView(select);
		}
		new AlertDialog.Builder(inflater.getContext())
				.setTitle("New " + (question == null ? "Question" : "Option"))
				.setView(layout)
				.setPositiveButton("Add", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String text = input.getText().toString();
						if (text.isEmpty())
							return;
						if (question == null) {
							if (select.getSelectedItem().toString().equals("Objective"))
								questions.add(new ObjectiveQuestion(text));
							else
								questions.add(new SubjectiveQuestion(text));
							Toast.makeText(inflater.getContext(), "Question added", Toast.LENGTH_LONG).show();
						} else {
							((ObjectiveQuestion) question).getOptions().add(new Option(text, question));
							Toast.makeText(inflater.getContext(), "Option added", Toast.LENGTH_LONG).show();
						}
					}
				})
				.show();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
		Option op = (Option) parent.getItemAtPosition(i);
		op.getQuestion().setCorrect(op);
		for (int j = 0; j < parent.getChildCount(); ++j) {
			((CheckedTextView) parent.getChildAt(j)).setChecked(false);
		}
		((CheckedTextView) view).setChecked(true);
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
