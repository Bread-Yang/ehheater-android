package com.vanward.ehheater.activity.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vanward.ehheater.R;

public class RightFragment extends Fragment implements OnClickListener {

	private LayoutInflater mInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.main_right_fragment, container,
				false);
		initView(view);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mInflater = LayoutInflater.from(getActivity());

	}

	private void initView(View view) {
		// title
		view.findViewById(R.id.ivTitleBtnLeft).setVisibility(View.GONE);
		view.findViewById(R.id.ivTitleBtnRigh).setVisibility(View.GONE);
		TextView title = (TextView) view.findViewById(R.id.ivTitleName);
		title.setText("右边");

		setListener();

	}

	private void setListener() {

	}

	@Override
	public void onClick(View v) {

	}

}
