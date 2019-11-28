package com.datamation.megaheaters.control.upload;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;


import com.datamation.megaheaters.R;
import com.datamation.megaheaters.control.TaskType;
import com.datamation.megaheaters.data.FInvRHedDS;
import com.datamation.megaheaters.model.mapper.SalesReturnMapper;
import com.google.gson.Gson;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class UploadSalesReturn extends AsyncTask<ArrayList<SalesReturnMapper>, Integer, ArrayList<SalesReturnMapper>> {

	Context context;
	ProgressDialog dialog;
	UploadTaskListener taskListener;
	TaskType taskType;
	String functionName;

	int totalRecords;

	// Shared Preferences variables
	public static final String SETTINGS = "SETTINGS";
	public static SharedPreferences localSP;

	public UploadSalesReturn(Context context, UploadTaskListener taskListener, TaskType taskType, String function) {

		this.context = context;
		this.taskListener = taskListener;
		this.taskType = taskType;
		this.functionName = function;

		localSP = context.getSharedPreferences(SETTINGS, 0);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = new ProgressDialog(context);
		// dialog.setTitle("Uploading records");
		dialog.show();
	}

	@Override
	protected ArrayList<SalesReturnMapper> doInBackground(ArrayList<SalesReturnMapper>... params) {

		int recordCount = 0;
		publishProgress(recordCount);

		ArrayList<SalesReturnMapper> RCSList = params[0];
		totalRecords = RCSList.size();

		final String sp_url = localSP.getString("URL", "").toString();
		String URL = "http://" + sp_url;

		for (SalesReturnMapper c : RCSList) {

			List<String> List = new ArrayList<String>();
			String sJsonHed = new Gson().toJson(c);
			List.add(sJsonHed);

			try {
				HttpPost requestfDam = new HttpPost(URL + context.getResources().getString(R.string.ConnectionURL) + "/"+functionName);
				StringEntity entityfDam = new StringEntity(List.toString(), "UTF-8");
				entityfDam.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
				entityfDam.setContentType("application/json");
				requestfDam.setEntity(entityfDam);

				HttpParams HttpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(HttpParams, 10000);
				HttpConnectionParams.setSoTimeout(HttpParams, 12000);

				DefaultHttpClient httpClientfDamRec = new DefaultHttpClient(HttpParams);
				HttpResponse responsefDamRec = httpClientfDamRec.execute(requestfDam);
				HttpEntity entityfDamEntity = responsefDamRec.getEntity();
				InputStream is = entityfDamEntity.getContent();

				if (is != null) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
					StringBuilder sb = new StringBuilder();

					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}

					is.close();

					String result = sb.toString();
					String result_fDamRec = result.replace("\"", "");

					Log.e("response", "connect:" + result_fDamRec);

					if (result_fDamRec.trim().equals("200")) {
						c.setSYNCSTATUS(true);
					} else {
						c.setSYNCSTATUS(false);
					}
				}
			} catch (Exception e) {

				e.getStackTrace();
			}

			++recordCount;
			publishProgress(recordCount);
		}

		return RCSList;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		dialog.setMessage("Uploading.. Return Record " + values[0] + "/" + totalRecords);
	}

	@Override
	protected void onPostExecute(ArrayList<SalesReturnMapper> RCSList) {
		super.onPostExecute(RCSList);
		List<String> list = new ArrayList<>();

		if (RCSList.size() > 0) {
			list.add("\nRETURN");
			list.add("------------------------------------\n");
		}
		int i = 1;
		for (SalesReturnMapper c : RCSList) {
			new FInvRHedDS(context).updateIsSynced(c);

			if (c.isSYNCSTATUS()) {
				list.add(i + ". " + c.getFINVRHED_REFNO()+ " --> Success\n");
			} else {
				list.add(i + ". " + c.getFINVRHED_REFNO() + " --> Failed\n");
			}
			i++;
		}

		dialog.dismiss();
		taskListener.onTaskCompleted(taskType, list);

	}

}
