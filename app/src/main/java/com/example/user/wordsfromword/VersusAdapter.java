package com.example.user.wordsfromword;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.backendless.BackendlessUser;

import java.util.ArrayList;

/**
 * Created by aqali on 04.08.2016.
 */
public class VersusAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList<BackendlessUser> list;
	public VersusAdapter(Context context, ArrayList<BackendlessUser> list) {
		this.list = list;
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
			view = inflater.inflate(R.layout.row_battle_item, viewGroup, false);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		viewHolder.usernameTextView.setText((CharSequence) list.get(i).getProperty("name"));
		viewHolder.cityTextView.setText((CharSequence) list.get(i).getProperty("city"));
		viewHolder.posTextView.setText(String.valueOf(i + 1));
		int score = 0;
		if (list.get(i).getProperty("score") != null)
			score = (int) list.get(i).getProperty("score");
		viewHolder.scoreTextView.setText(String.valueOf(score));

//		int color = list.get(i).getColor();
//		switch (color) {
//			case 1: color = R.color.yellow; break;
//			case 2: color = R.color.blue; break;
//			case 3: color = R.color.green; break;
//			case 4: color = R.color.red; break;
//			default: break;
//		}
//		viewHolder.rootLinearLayout.setBackgroundResource(color);
		return view;
	}

	private class ViewHolder {
		private TextView usernameTextView, scoreTextView, posTextView, cityTextView;
		private LinearLayout rootLinearLayout;
		public ViewHolder(View v) {
			rootLinearLayout = (LinearLayout) v.findViewById(R.id.rootLinearLayout);
			posTextView = (TextView) v.findViewById(R.id.posTextView);
			cityTextView = (TextView) v.findViewById(R.id.cityTextView);
			usernameTextView = (TextView) v.findViewById(R.id.usernameTextView);
			scoreTextView = (TextView) v.findViewById(R.id.scoreTextView);
		}
	}
}
