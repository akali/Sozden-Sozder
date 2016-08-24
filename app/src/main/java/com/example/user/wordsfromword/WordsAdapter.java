package com.example.user.wordsfromword;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.nio.channels.DatagramChannel;

public class WordsAdapter extends BaseAdapter {
	private static final String TAG = "WordsAdapter";
	Context context;
	LayoutInflater inflater;
	int listPos;

	public WordsAdapter(Context context, int listPos) {
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listPos = listPos;
	}

	public WordsAdapter(Context context) {
		this.context = context;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listPos = 0;
	}

	@Override
	public int getCount() {
//		int cnt = DataHolder.getDataHolder().area;
//		int size = DataHolder.getDataHolder().getAnswers().size();
////		Log.e(TAG, "size = " + size + ", listPos = " + listPos + ", cnt = " + cnt);
//		if (cnt * listPos + cnt > size)
//			cnt = size - cnt * listPos;
////		Log.e(TAG, "cnt = " + cnt);
//		return cnt;
		return DataHolder.getDataHolder().getAnswers().size();
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
		ViewHolder viewHolder;
		if (view == null) {
			view = inflater.inflate(R.layout.grid_item_1, null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.textView.setText(DataHolder.getDataHolder().getAnswers().get(listPos * DataHolder.getDataHolder().area + i));

		return view;
	}

	private class ViewHolder {
		TextView textView;

		public ViewHolder(View view) {
			textView = (TextView) view.findViewById(R.id.textView);
		}
	}
}
