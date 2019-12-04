package com.contest.model;

import com.quizwork.Question;

public class SubjectiveQuestion extends Question {

	public SubjectiveQuestion() {}

	public SubjectiveQuestion(String text) {
		this.text = text;
	}

	public SubjectiveQuestion(long id, String text) {
		this.id = id;
		this.text = text;
	}

	@Override
	public void validate() {}
}
