package com.example.user.wordsfromword;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by aqali on 14.07.2016.
 */
public class Letter {
	private TextView textView;
	private char letter;
	private int id;
	private Boolean clicked;

	public Letter(TextView textView, char letter, int id, Boolean clicked) {
		this.textView = textView;
		this.letter = letter;
		this.id = id;
		this.clicked = clicked;
	}

	private int rand() {
		return Rand.getRand().getRnd().nextInt();
	}

	public Letter(TextView textView, char letter) {
		this.textView = textView;
		this.letter = letter;
		this.id = rand();
	}

	public Letter(char letter, TextView textView, int id, Boolean clicked) {
		this.letter = letter;
		this.textView = textView;
		this.id = id;
		this.clicked = clicked;
	}

	public Letter(char letter, Context context) {
		this.letter = letter;
		this.id = rand();
//		ImageView imageView = new ImageView(context);
//		imageView.setImageResource(DataHolder.getDataHolder().getLetters().get(String.valueOf(letter)));
		this.textView = new TextView(context);
		this.textView.setText(String.valueOf(letter));
		this.textView.setTextSize(50);
		this.textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
		this.textView.setTextColor(context.getResources().getColor(android.R.color.white));
//		this.textView.setBackgroundResource(R.drawable.button_letter);
		this.textView.setGravity(Gravity.CENTER);
		this.textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "kz_cooper.ttf"));
		this.clicked = false;
	}

	public TextView getTextView() {
		return textView;
	}

	public void setTextView(TextView textView) {
		this.textView = textView;
	}

	public char getLetter() {
		return letter;
	}

	public void setLetter(char letter) {
		this.letter = letter;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Boolean isClicked() {
		return clicked;
	}

	public void setClicked(Boolean clicked) {
		this.clicked = clicked;
		setClickedState(clicked);
	}

	private void setClickedState(boolean b) {
		if (b) {
//			textView.setImageResource(DataHolder.getDataHolder().getPressedLetters().get(String.valueOf(letter)));
//			textView.setImageResource(R.drawable.frame);
			textView.setText("*");
		} else {
//			textView.setImageResource(DataHolder.getDataHolder().getLetters().get(String.valueOf(letter)));
			textView.setText(String.valueOf(letter));
		}
	}
}
