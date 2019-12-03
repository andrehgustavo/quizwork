package com.contest.model;

import com.quizwork.Question;

public class SubjectiveQuestion extends Question {
	private Integer score;

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

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
}
