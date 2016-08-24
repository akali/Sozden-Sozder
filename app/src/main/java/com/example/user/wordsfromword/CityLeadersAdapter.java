package com.example.user.wordsfromword;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aqali on 10.08.2016.
 */
public class CityLeadersAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private ArrayList <City> list;

	public CityLeadersAdapter(Context context, ArrayList<City> list) {
		this.context = context;
		this.list = list;
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
			view = inflater.inflate(R.layout.row_cities_item, viewGroup, false);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.scoreTextView.setText(String.valueOf(list.get(i).getScore()));
		viewHolder.cityTextView.setText((CharSequence) list.get(i).getName());
		viewHolder.posTextView.setText(String.valueOf(i + 1));
		return view;
	}
	private class ViewHolder {
		private TextView posTextView, cityTextView, scoreTextView;
		public ViewHolder (View v){
			posTextView = (TextView) v.findViewById(R.id.posTextView);
			cityTextView = (TextView) v.findViewById(R.id.cityTextView);
			scoreTextView = (TextView) v.findViewById(R.id.scoreTextView);
		}

	}
}
