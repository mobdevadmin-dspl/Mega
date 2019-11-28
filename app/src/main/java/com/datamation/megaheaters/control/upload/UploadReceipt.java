package com.datamation.megaheaters.control.upload;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.control.TaskType;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.data.RecHedDS;
import com.datamation.megaheaters.model.mapper.ReceiptMapper;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class UploadReceipt extends AsyncTask<ArrayList<ReceiptMapper>, Integer, ArrayList<ReceiptMapper>> {

	Context context;
	ProgressDialog dialog;
	UploadTaskListener taskListener;
	TaskType taskType;

	int totalRecords;

	public static final String SETTINGS = "SETTINGS";
	public static SharedPreferences localSP;

	public UploadReceipt(Context context, UploadTaskListener taskListener, TaskType taskType) {

		this.context = context;
		this.taskListener = taskListener;
		this.taskType = taskType;
		localSP = context.getSharedPreferences(SETTINGS, 0);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = new ProgressDialog(context);
		dialog.show();
	}

	@Override
	protected ArrayList<ReceiptMapper> doInBackground(ArrayList<ReceiptMapper>... params) {

		int recordCount = 0;
		publishProgress(recordCount);
		
		ArrayList<ReceiptMapper> RCSList = params[0];
		totalRecords = RCSList.size();
		
		final String sp_url =localSP.getString("URL", "").toString();
		String URL="http://"+sp_url;

		for(ReceiptMapper c : RCSList){
			
			List<String> List = new ArrayList<String>();
			
			String sJsonHed = new Gson().toJson(c);
			
			List.add(sJsonHed);
			String sURL = URL + context.getResources().getString(R.string.ConnectionURL) + "/insertFrecHed";
			boolean bStatus = false;
			try {
				bStatus = UtilityContainer.mHttpManager(sURL, List.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// boolean bStatus = UtilityContainer.mHttpManager(sURL, new Gson().toJson(c));

			if (bStatus) {
				c.setSynced(true);
			} else {
				c.setSynced(false);
			}
			
			
			Log.v("## Json ##",  List.toString());
			
//			try {
//			// PDADBWebServiceMO
//			HttpPost requestfDam = new HttpPost(URL+ context.getResources().getString(R.string.ConnectionURL) +"/insertFrecHed");
//			StringEntity entityfDam = new StringEntity( List.toString(), "UTF-8");
//			entityfDam.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//			entityfDam.setContentType("application/json");
//			requestfDam.setEntity(entityfDam);
//			// Send request to WCF service
//			DefaultHttpClient httpClientfDamRec = new DefaultHttpClient();
//
//
//			HttpResponse responsefDamRec = httpClientfDamRec.execute(requestfDam);
//			HttpEntity entityfDamEntity = responsefDamRec.getEntity();
//			InputStream is = entityfDamEntity.getContent();
//
//			//StatusLine statusLine = responsefDamRec.getStatusLine();
//		    //int statusCode = statusLine.getStatusCode();
//
//				if (is != null)
//				{
//					BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
//				    StringBuilder sb = new StringBuilder();
//
//				    String line = null;
//				    while ((line = reader.readLine()) != null){
//				        sb.append(line + "\n");
//				    }
//
//				   is.close();
//
//				   String result = sb.toString();
//				   String result_fDamRec = result.replace("\"", "");
//
//				   Log.e("response", "connect:" + result_fDamRec);
//
//				   if (result_fDamRec.trim().equals("200")){
//						 c.setSynced(true);
//					}else {
//						c.setSynced(false);
//					}
//				}
//				}catch(Exception e){
//
//					e.getStackTrace();
//				}
				
				++recordCount;
				publishProgress(recordCount);
			}

		return RCSList;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		dialog.setMessage("Uploading.. Receipt Record " + values[0] + "/" + totalRecords);
	}

	@Override
	protected void onPostExecute(ArrayList<ReceiptMapper> RCSList) {
		super.onPostExecute(RCSList);
		List<String> list = new ArrayList<>();

		if (RCSList.size() > 0) {
			list.add("\nRECEIPT");
			list.add("------------------------------------\n");
		}

		int i = 1;
		for (ReceiptMapper c : RCSList) {
			new RecHedDS(context).updateIsSyncedReceipt(c);

			if (c.isSynced()) {
				list.add(i + ". " + c.getFPRECHED_REFNO()+ " --> Success\n");
			} else {
				list.add(i + ". " + c.getFPRECHED_REFNO() + " --> Failed\n");
			}
			i++;
		}

		dialog.dismiss();
		taskListener.onTaskCompleted(taskType, list);
	}

}
