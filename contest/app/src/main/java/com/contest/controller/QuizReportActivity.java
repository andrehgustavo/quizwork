package com.contest.controller;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.contest.R;
import com.quizwork.Quiz;
import com.quizwork.QuizAnswer;
import com.quizwork.ValidationException;
import com.contest.service.AnswerService;

public class QuizReportActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quiz_report);

		Quiz quiz = (Quiz) getIntent().getSerializableExtra("quiz");
		try {
			((TextView) findViewById(R.id.quiz_report_quantity_answers)).setText(String.valueOf(
					AnswerService.getInstance(this).countByQuiz(quiz)));
			ListView listView = findViewById(R.id.quiz_report_winners);
			listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
					AnswerService.getInstance(this).findByQuiz(quiz)));
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
			// create quiz correction
		} else {
			Toast.makeText(this, "Cannot correct this quiz", Toast.LENGTH_LONG).show();
		}
	}
}
