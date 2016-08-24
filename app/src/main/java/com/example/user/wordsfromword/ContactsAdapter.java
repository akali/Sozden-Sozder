package com.example.user.wordsfromword;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aqali on 8/18/16.
 */
public class ContactsAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<Contact> list;
	private LayoutInflater inflater;

	public ContactsAdapter(Context context, ArrayList<Contact> list) {
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

		viewHolder.nameTextView.setText(list.get(i).getName());
		viewHolder.numberTextView.setText(String.valueOf(list.get(i).getNumber()));
		return view;
	}

	private class ViewHolder {
		private TextView nameTextView, numberTextView;
		private ImageView checkBoxImageView;
		public ViewHolder(View v) {
			checkBoxImageView = (ImageView) v.findViewById(R.id.checkBoxImageView);
			nameTextView = (TextView) v.findViewById(R.id.nameTextView);
			numberTextView = (TextView) v.findViewById(R.id.numberTextView);
		}
	}
}
