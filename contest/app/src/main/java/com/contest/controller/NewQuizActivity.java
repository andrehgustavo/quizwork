package com.contest.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.contest.R;
import com.quizwork.Category;
import com.quizwork.Quiz;
import com.quizwork.User;
import com.quizwork.ValidationException;
import com.contest.service.QuizService;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class NewQuizActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
		TimePickerDialog.OnTimeSetListener, AutoCompleteAdapter.OnSelectAutoComplete {

	private int idViewDate;
	private GregorianCalendar date;
	private Quiz quiz;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_quiz);
		setTitle("New Quiz");
		quiz = new Quiz();
		quiz.setCategory(new Category());
		quiz.setCreator((User) getIntent().getSerializableExtra("user"));
		((AutoCompleteTextView) findViewById(R.id.quiz_category))
				.setAdapter(new AutoCompleteAdapter(this, this));
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
		date = new GregorianCalendar(year, month, dayOfMonth);
		Calendar c = Calendar.getInstance();
		new TimePickerDialog(
				NewQuizActivity.this,
				NewQuizActivity.this,
				c.get(Calendar.HOUR_OF_DAY),
				c.get(Calendar.MINUTE),
				DateFormat.is24HourFormat(this)
		).show();
	}

	@Override
	@SuppressLint("DefaultLocale")
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		((TextView) findViewById(idViewDate))
				.setText(String.format("%d/%d/%d %d:%d",
						date.get(GregorianCalendar.DAY_OF_MONTH),
						date.get(GregorianCalendar.MONTH),
						date.get(GregorianCalendar.YEAR),
						hourOfDay, minute)
				);
		date.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
		date.set(GregorianCalendar.MINUTE, minute);
		if (idViewDate == R.id.quiz_view_start_date) {
			quiz.setStart(date);
		} else {
			quiz.setEnd(date);
		}
	}

	public void editDate(View view) {
		Calendar c = Calendar.getInstance();
		new DatePickerDialog(
				NewQuizActivity.this,
				NewQuizActivity.this,
				c.get(Calendar.YEAR),
				c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH)
		).show();

		if (view.getId() == R.id.quiz_start_date) {
			idViewDate = R.id.quiz_view_start_date;
		} else {
			idViewDate = R.id.quiz_view_end_date;
		}
	}

	@Override
	public void onSelectAutoComplete(Category category) {
		if (category == null) {
			quiz.setCategory(new Category());
		} else {
			quiz.setCategory(category);
			((AutoCompleteTextView) findViewById(R.id.quiz_category)).setText(category.getName());
		}
	}

	public void setOpen(View view) {
		quiz.setOpen(((Switch) view).isChecked());
	}

	public void submit(View view) {
		try {
			if (quiz.getCategory().getId() == 0)
				quiz.getCategory().setName(((EditText) findViewById(R.id.quiz_category)).getText().toString());
			quiz.setName(((EditText) findViewById(R.id.quiz_name)).getText().toString());

			QuizService.getInstance(this).create(quiz);
			startActivityForResult(new Intent(this, CreateQuestionsActivity.class).putExtra("quiz", quiz), 0);
		} catch (ValidationException msg) {
			msg.show(this);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			setResult(resultCode, data);
			finish();
		}
	}
}
