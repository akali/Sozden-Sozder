package com.example.user.wordsfromword;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.GridView;


public class GridFragment extends Fragment {
	private static final String LIST_POS = "listPos";
	private static final String CURRENT_LEVEL = "currentLevel";

	private int listPos;

	public static GridFragment newInstance(int listPos) {
		GridFragment fragment = new GridFragment();
		Bundle args = new Bundle();
		args.putInt(LIST_POS, listPos);
		fragment.setArguments(args);
		return fragment;
	}

	public GridFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			listPos = getArguments().getInt(LIST_POS);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_grid, container, false);
		GridView gridView = ((GridView) v.findViewById(R.id.gridView));
		gridView.setAdapter(new WordsAdapter(v.getContext(), listPos));

		return v;
	}
}
