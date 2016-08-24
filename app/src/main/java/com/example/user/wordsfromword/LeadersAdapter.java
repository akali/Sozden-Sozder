package com.example.user.wordsfromword;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aqali on 18.07.2016.
 */
public class LeadersAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<Results> leaders;
	private ArrayList<Integer> colors;

	public LeadersAdapter(Context context, ArrayList<Results> leaders) {
		this.context = context;
		this.leaders = leaders;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		colors = new ArrayList<>();
		colors.add(R.color.l1);
		colors.add(R.color.l2);
		colors.add(R.color.l3);
		colors.add(R.color.l4);
		colors.add(R.color.l5);
		colors.add(R.color.l6);
		colors.add(R.color.l7);
		colors.add(R.color.l8);
		colors.add(R.color.l9);
		colors.add(R.color.l10);
		colors.add(R.color.l11);
		colors.add(R.color.l12);
		colors.add(R.color.l13);
		colors.add(R.color.l14);
		colors.add(R.color.l15);
		colors.add(R.color.l16);
		colors.add(R.color.l17);
		colors.add(R.color.l18);
		colors.add(R.color.l19);
		colors.add(R.color.l20);
		colors.add(R.color.l21);
		colors.add(R.color.l22);
		colors.add(R.color.l23);
		colors.add(R.color.l24);
		colors.add(R.color.l25);
		colors.add(R.color.l26);
		colors.add(R.color.l27);
		colors.add(R.color.l28);
		colors.add(R.color.l29);
		colors.add(R.color.l30);
		colors.add(R.color.l31);
		colors.add(R.color.l32);
		colors.add(R.color.l33);
		colors.add(R.color.l34);
		colors.add(R.color.l35);
		colors.add(R.color.l36);
		colors.add(R.color.l37);
		colors.add(R.color.l38);
		colors.add(R.color.l39);
	}

	@Override
	public int getCount() {
		return leaders.size();
	}

	@Override
	public Object getItem(int i) {
		return null;
	}

	@Override
	public long getItemId(int i) {
		return 0;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder = null;
		if (view == null) {
			view = inflater.inflate(R.layout.row_leaders_item, viewGroup, false);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		int color = context.getResources().getColor(android.R.color.white);
		if(leaders.get(i).getScore()/300 > colors.size())
			color = (context.getResources().getColor(colors.get(colors.size() - 1)));
		else if(leaders.get(i).getScore()/300 > 0)
			color = (context.getResources().getColor(colors.get(leaders.get(i).getScore()/300)));
		else if(leaders.get(i).getScore() > 99)
			color = (context.getResources().getColor(colors.get(0)));
		viewHolder.leadersLinearLayout.setBackgroundColor(color);

		viewHolder.usernameTextView.setText((CharSequence) leaders.get(i).getUser().getProperty("name"));
		viewHolder.scoreTextView.setText(String.valueOf(leaders.get(i).getScore()));
		viewHolder.cityTextView.setText((CharSequence) leaders.get(i).getUser().getProperty("city"));
		viewHolder.posTextView.setText(String.valueOf(i + 1));
		return view;
	}

	private class ViewHolder {
		private TextView usernameTextView, scoreTextView, posTextView, cityTextView;
		private LinearLayout leadersLinearLayout;
		public ViewHolder(View v) {
			leadersLinearLayout = (LinearLayout) v.findViewById(R.id.leadersLinearLayout);
			posTextView = (TextView) v.findViewById(R.id.posTextView);
			cityTextView = (TextView) v.findViewById(R.id.cityTextView);
			usernameTextView = (TextView) v.findViewById(R.id.usernameTextView);
			scoreTextView = (TextView) v.findViewById(R.id.scoreTextView);
		}
	}
}
