package com.contest.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.contest.R;
import com.contest.service.AnswerService;
import com.quizwork.QuizAnswer;
import com.quizwork.ValidationException;

public class CorrectionActivity extends AppCompatActivity {
	private CorrectionAdapter correctionAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_correction);

		try {
			QuizAnswer quizAnswer = (QuizAnswer) getIntent().getSerializableExtra("quizAnswer");
			quizAnswer = AnswerService.getInstance(this).findForCorrectionByQuizAnswer(quizAnswer);

			((TextView) findViewById(R.id.correction_quiz_name)).setText(quizAnswer.getQuiz().getName());
			correctionAdapter = new CorrectionAdapter(this, quizAnswer);
			((ListView) findViewById(R.id.correction_questions)).setAdapter(correctionAdapter);
		} catch (ValidationException e) {
			e.show(this);
			finish();
		}
	}

	public void save(View view) {
		try {
			AnswerService.getInstance(this).updateCorrection(correctionAdapter.getQuizAnswer());
			Toast.makeText(this, "Correction performed", Toast.LENGTH_LONG).show();
			setResult(Activity.RESULT_OK);
			finish();
		} catch (ValidationException e) {
			e.show(this);
		}
	}
}
