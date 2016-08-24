package com.example.user.wordsfromword;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aqali on 10.08.2016.
 */
public class BattlesListAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<Battles> list;

	public BattlesListAdapter(Context context) {
		this.list = DataHolder.getDataHolder().getList();
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return list.size();
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
			view = inflater.inflate(R.layout.row_battles_list_item, viewGroup, false);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		Battle b = list.get(i).getBattle();
		String user1 = (String) b.getUser1().getProperty("name");
		String user2 = (String) b.getUser2().getProperty("name");
		String score1 = String.valueOf(b.getScore1()), score2 = String.valueOf(b.getScore2());

		int color = list.get(i).getColor();
		switch (color) {
			case 1: color = R.color.yellow; viewHolder.user1TextView.setTextColor(context.getResources().getColor(R.color.wordOnGray)); score2 = "?"; break;
			case 2: color = R.color.blue; viewHolder.user2TextView.setTextColor(context.getResources().getColor(R.color.wordOnGray)); score2 = "?"; break;
			case 3: color = R.color.green; viewHolder.user1TextView.setTextColor(context.getResources().getColor(R.color.wordOnGray)); break;
			case 4: color = R.color.red; viewHolder.user1TextView.setTextColor(context.getResources().getColor(R.color.wordOnGray)); break;
			default: break;
		}
		viewHolder.rootLinearLayout.setBackgroundResource(color);
		viewHolder.user1TextView.setText(user1);
		viewHolder.user2TextView.setText(user2);
		viewHolder.score1TextView.setText(score1);
		viewHolder.score2TextView.setText(score2);
		return view;
	}

	private class ViewHolder {
		private TextView user1TextView, user2TextView, score1TextView, score2TextView;
		private LinearLayout rootLinearLayout;
		public ViewHolder(View v) {
			rootLinearLayout = (LinearLayout) v.findViewById(R.id.rootLinearLayout);
			user1TextView = (TextView) v.findViewById(R.id.user1TextView);
			user2TextView = (TextView) v.findViewById(R.id.user2TextView);
			score1TextView = (TextView) v.findViewById(R.id.score1TextView);
			score2TextView = (TextView) v.findViewById(R.id.score2TextView);
		}
	}
}
