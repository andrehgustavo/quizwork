package com.contest.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.contest.R;
import com.quizwork.Quiz;
import com.quizwork.QuizAnswer;
import com.quizwork.ValidationException;
import com.contest.service.AnswerService;

public class QuizReportActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
	private Quiz quiz;
	private ArrayAdapter<QuizAnswer> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz_report);

		quiz = (Quiz) getIntent().getSerializableExtra("quiz");
		try {
			adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
					AnswerService.getInstance(this).findAllByQuiz(quiz));
			((TextView) findViewById(R.id.quiz_report_quantity_answers)).setText(String.valueOf(adapter.getCount()));
			ListView listView = findViewById(R.id.quiz_report_winners);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(this);
		} catch (ValidationException e) {
			e.show(this);
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		QuizAnswer quizAnswer = (QuizAnswer) adapterView.getItemAtPosition(i);
		if (quizAnswer.getScore() == null) {
			startActivityForResult(new Intent(this, CorrectionActivity.class)
					.putExtra("quizAnswer", quizAnswer), 0);
		} else {
			Toast.makeText(this, "Cannot correct this quiz", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			try {
				adapter.clear();
				adapter.addAll(AnswerService.getInstance(this).findAllByQuiz(quiz));
			} catch (ValidationException e) {
				e.show(this);
				finish();
			}
		}
	}
}
