package com.datamation.megaheaters.view.receipt;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.adapter.CustomerDebtAdapter;
import com.datamation.megaheaters.adapter.ReceiptPagerAdapter;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.data.FDDbNoteDS;
import com.datamation.megaheaters.model.FDDbNote;
import com.datamation.megaheaters.view.stock_inquiry.StockInquiryDialog;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.LayoutParams;
import java.util.ArrayList;

public class Receipt extends Fragment {
	View view;
	TabLayout tabLayout;
	Activity activity;
	FloatingActionMenu fam;
	FloatingActionButton fab;
	ViewPager viewPager;
	ReceiptPagerAdapter adapter;
	boolean status;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.sales_management_pre_sales, container, false);
		setHasOptionsMenu(true);
		activity = getActivity();

		Bundle mBundle = getArguments();

		if (mBundle != null) {
			status = mBundle.getBoolean("Active");
		}

		tabLayout = (TabLayout) view.findViewById(R.id.tabs);
		tabLayout.addTab(tabLayout.newTab().setText("Customer"));
		tabLayout.addTab(tabLayout.newTab().setText("Header"));
		tabLayout.addTab(tabLayout.newTab().setText("Details"));
		tabLayout.addTab(tabLayout.newTab().setText("Summary"));

		tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
											   @Override
											   public void onTabSelected(TabLayout.Tab tab) {
												   viewPager.setCurrentItem(tab.getPosition());
											   }

											   @Override
											   public void onTabUnselected(TabLayout.Tab tab) {
											   }

											   @Override
											   public void onTabReselected(TabLayout.Tab tab) {
											   }
										   }

		);

		viewPager = (ViewPager) view.findViewById(R.id.pager);
		adapter = new ReceiptPagerAdapter(getChildFragmentManager(), 4);
		viewPager.setAdapter(adapter);

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {

				tabLayout.getTabAt(position).select();

				if (position == 3)
					LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("TAG_SUMMARY"));
				else if (position == 1)
					LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("TAG_HEADER"));
				else if (position == 2)
					LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent("TAG_DETAILS"));

			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		fab = (FloatingActionButton) view.findViewById(R.id.fab);

		SubActionButton.Builder builder = new SubActionButton.Builder(activity);

		ImageView icon_1 = new ImageView(getActivity());
		icon_1.setImageResource(R.drawable.float_1);

		ImageView icon_2 = new ImageView(getActivity());
		icon_2.setImageResource(R.drawable.float_2);

		ImageView icon_3 = new ImageView(getActivity());
		icon_3.setImageResource(R.drawable.float_3);

		ImageView icon_4 = new ImageView(getActivity());
		icon_4.setImageResource(R.drawable.float_4);

		ImageView icon_5 = new ImageView(getActivity());
		icon_5.setImageResource(R.drawable.float_5);

		ImageView icon_6 = new ImageView(getActivity());
		icon_6.setImageResource(R.drawable.float_6);

		SubActionButton button_1 = builder.setContentView(icon_1).build();
		SubActionButton button_2 = builder.setContentView(icon_2).build();
		SubActionButton button_3 = builder.setContentView(icon_3).build();
		SubActionButton button_4 = builder.setContentView(icon_4).build();
		SubActionButton button_5 = builder.setContentView(icon_5).build();
		SubActionButton button_6 = builder.setContentView(icon_6).build();

		LayoutParams params = new LayoutParams(70, 70);

		button_1.setLayoutParams(params);
		button_2.setLayoutParams(params);
		button_3.setLayoutParams(params);
		button_4.setLayoutParams(params);
		button_5.setLayoutParams(params);
		button_6.setLayoutParams(params);

		fam = new FloatingActionMenu.Builder(activity)
				.setStartAngle(-180)
				.setEndAngle(0)
				.setRadius(200)
				.addSubActionView(button_3)
				.addSubActionView(button_2)
				.addSubActionView(button_1)
				.addSubActionView(button_4)
				.addSubActionView(button_5)
				.addSubActionView(button_6)
				.attachTo(fab)
				.build();

		button_3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new StockInquiryDialog(activity);
				fam.close(true);
			}
		});

		button_2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				String s = new SharedPref(getActivity()).getGlobalVal("ReckeyCusCode");

				if (s.equals("***"))
					mCustomerOutstandingDialog("");
				else
					mCustomerOutstandingDialog(s);

				fam.close(true);
			}
		});

		button_1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(getActivity(), "Delete Clicked", Toast.LENGTH_SHORT).show();
				fam.close(true);
			}
		});

		button_4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(getActivity(), "Delete Clicked", Toast.LENGTH_SHORT).show();
				fam.close(true);
			}
		});

		button_5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(getActivity(), "Delete Clicked", Toast.LENGTH_SHORT).show();
				fam.close(true);
			}
		});

		button_6.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(getActivity(), "Delete Clicked", Toast.LENGTH_SHORT).show();
				fam.close(true);
			}
		});


		fam.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
			@Override
			public void onMenuOpened(FloatingActionMenu floatingActionMenu) {

				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						fam.close(true);
					}
				}, 2000);
			}

			@Override
			public void onMenuClosed(FloatingActionMenu floatingActionMenu) {

			}
		});

		return view;
	}

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

	@Override
	public void onStart() {
		super.onStart();
		if (status)
			viewPager.setCurrentItem(2);

	}

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

	public void mMoveToHeader() {
		viewPager.setCurrentItem(1);
	}

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

	public void mMoveToHDetails() {
		viewPager.setCurrentItem(2);
	}

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		fam.close(true);
	}

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

	public void mCustomerOutstandingDialog(String debCode) {

		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		View promptView = layoutInflater.inflate(R.layout.customer_debtor, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setTitle("Customer Outstanding " + (debCode.equals("") ? debCode : "(" + debCode + ")"));
		alertDialogBuilder.setView(promptView);

		final ListView listView = (ListView) promptView.findViewById(R.id.lvCusDebt);
		ArrayList<FDDbNote> list = new FDDbNoteDS(getActivity()).getDebtInfo(debCode);
		listView.setAdapter(new CustomerDebtAdapter(getActivity(), list));

		alertDialogBuilder.setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		alertDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

	}
}
