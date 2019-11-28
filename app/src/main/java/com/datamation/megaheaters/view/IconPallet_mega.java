package com.datamation.megaheaters.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.megaheaters.PushNotification.MessagesView;
import com.datamation.megaheaters.PushNotification.objMessage;
import com.datamation.megaheaters.R;
import com.datamation.megaheaters.adapter.GetMacAddress;
import com.datamation.megaheaters.control.ConnectionDetector;
import com.datamation.megaheaters.control.ReferenceNum;
import com.datamation.megaheaters.control.TaskType;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.control.download.DownloadTaskListener;
import com.datamation.megaheaters.control.download.Downloader;
import com.datamation.megaheaters.control.upload.UploadExpenses;
import com.datamation.megaheaters.control.upload.UploadNonProd;
import com.datamation.megaheaters.control.upload.UploadPreSales;
import com.datamation.megaheaters.control.upload.UploadReceipt;
import com.datamation.megaheaters.control.upload.UploadSalesReturn;
import com.datamation.megaheaters.control.upload.UploadTaskListener;
import com.datamation.megaheaters.control.upload.UploadTour;
import com.datamation.megaheaters.control.upload.UploadVanSales;
import com.datamation.megaheaters.data.FDayExpHedDS;
import com.datamation.megaheaters.data.FDaynPrdHedDS;
import com.datamation.megaheaters.data.FInvRHedDS;
import com.datamation.megaheaters.data.InvDetDS;
import com.datamation.megaheaters.data.InvHedDS;
import com.datamation.megaheaters.data.MessageDS;
import com.datamation.megaheaters.data.NewCustomerDS;
import com.datamation.megaheaters.data.RecHedDS;
import com.datamation.megaheaters.data.SalRepDS;
import com.datamation.megaheaters.data.ServerDatabaseDS;
import com.datamation.megaheaters.data.SharedPreferencesClass;
import com.datamation.megaheaters.data.TourDS;
import com.datamation.megaheaters.data.TranIssDS;
import com.datamation.megaheaters.data.TranSOHedDS;
import com.datamation.megaheaters.model.NewCustomer;
import com.datamation.megaheaters.model.mapper.ExpenseMapper;
import com.datamation.megaheaters.model.mapper.NonProdMapper;
import com.datamation.megaheaters.model.mapper.PreSalesMapper;
import com.datamation.megaheaters.model.mapper.ReceiptMapper;
import com.datamation.megaheaters.model.mapper.SalesReturnMapper;
import com.datamation.megaheaters.model.mapper.TourMapper;
import com.datamation.megaheaters.model.mapper.VanSalesMapper;
import com.datamation.megaheaters.view.Customer_registration.CustomerRegMain;
import com.datamation.megaheaters.view.chat.DeviceChat;
import com.datamation.megaheaters.view.chat.objUser;
import com.datamation.megaheaters.view.expense.ExpenseMain;
import com.datamation.megaheaters.view.non_productive.NonProductiveMain;
import com.datamation.megaheaters.view.presale.PreSales;
import com.datamation.megaheaters.view.presale.PreSalesInvoice;
import com.datamation.megaheaters.view.receipt.ReceiptInvoice;
import com.datamation.megaheaters.view.sales_return.SalesReturnHistory;
import com.datamation.megaheaters.view.stock_inquiry.StockInquiryDialog;
import com.datamation.megaheaters.view.tour.TourInfo;
import com.datamation.megaheaters.view.vansale.VanSaleInvoice;
import com.datamation.megaheaters.view.vansale.VanSales;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class IconPallet_mega extends Fragment implements View.OnClickListener, DownloadTaskListener, UploadTaskListener {

    public static final String SETTINGS = "SETTINGS";
    public static SharedPreferences localSP;
    public static String connURLsvc;
    MainActivity activity;
    boolean isSecondarySync = false;
    Animation animScale;
    boolean downloadStock = false;
    List<String> resultList;

    ImageButton imgTour, imgPresale, imgVansale, imgNonprod, imgDashboard, imgExpense, imgStockInq,
            imgSync, imgUpload, imgStockDown, imgPrinter, imgScheduleUp,
            imgDatabase, imgSalesRep, imgSalRet, imgReceipt, notification, chat,imgDashboard2,imgAddUser;
    View view;
    boolean isPresalePending = false, isInvoicePending = false, isNonprdPending = false;
    private TextView msgCount;

    private ArrayList<objUser> users;
    private RecyclerView recyclerView;
//    public static FirebaseAuth mAuth;
//    private FirebaseAuth.AuthStateListener mAuthListener;
//    private FirebaseUser user;
    //icon
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.icon_pallet_mega, container, false);
        activity = (MainActivity) getActivity();

//        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        getActivity().setTitle("mFiNAC");


        toolbar.setLogo(R.drawable.dm_logo_64);
        setHasOptionsMenu(true);
        resultList = new ArrayList<>();

        connURLsvc = getResources().getString(R.string.ConnectionURL);
        localSP = activity.getSharedPreferences(SETTINGS, 0);

        animScale = AnimationUtils.loadAnimation(activity, R.anim.anim_scale);
        imgTour = (ImageButton) view.findViewById(R.id.imgTourInfo);
        imgNonprod = (ImageButton) view.findViewById(R.id.imgNonProd);
        imgDashboard = (ImageButton) view.findViewById(R.id.imgDashboard);
        imgExpense = (ImageButton) view.findViewById(R.id.imgExpense);
        imgStockInq = (ImageButton) view.findViewById(R.id.imgStockInquiry);
        imgSync = (ImageButton) view.findViewById(R.id.imgSync);
        imgUpload = (ImageButton) view.findViewById(R.id.imgUpload);
        imgStockDown = (ImageButton) view.findViewById(R.id.imgDownload);
        imgPrinter = (ImageButton) view.findViewById(R.id.imgPrinter);
        imgScheduleUp = (ImageButton) view.findViewById(R.id.imgScheUpload);
        imgDatabase = (ImageButton) view.findViewById(R.id.imgSqlite);
        imgSalesRep = (ImageButton) view.findViewById(R.id.imgSalrep);
        imgSalRet = (ImageButton) view.findViewById(R.id.imgReturn);
        imgReceipt = (ImageButton) view.findViewById(R.id.imgReceipt);
        imgPresale = (ImageButton) view.findViewById(R.id.imgPresale);
        imgVansale = (ImageButton) view.findViewById(R.id.imgVansale);
        imgAddUser= (ImageButton) view.findViewById(R.id.imgAddUser);

        notification = (ImageButton) view.findViewById(R.id.notification);
        chat = (ImageButton) view.findViewById(R.id.Chat);
        imgDashboard2 = (ImageButton) view.findViewById(R.id.imgDashboard2);

        if (activity.synced) {

            if (new InvHedDS(activity).validateActiveInvoices())
                imgVansale.setImageResource(R.drawable.vansale_active);

            if (new TranSOHedDS(activity).validateActivePreSales()) {
                imgPresale.setImageResource(R.drawable.presale_active);
                isPresalePending = true;
            } else
                isPresalePending = false;

            if(new FDaynPrdHedDS(activity).validateActiveNonPrd()){
                imgNonprod.setImageResource(R.drawable.nonprod_pending);
                isNonprdPending = true;
            } else
                isNonprdPending = false;

            if (new FInvRHedDS(getActivity()).isAnyActive())
                imgSalRet.setImageResource(R.drawable.returns_active);
        }

        imgTour.setOnClickListener(IconPallet_mega.this);
        imgPresale.setOnClickListener(IconPallet_mega.this);
        imgVansale.setOnClickListener(IconPallet_mega.this);
        imgNonprod.setOnClickListener(IconPallet_mega.this);
        imgDashboard.setOnClickListener(IconPallet_mega.this);
        imgExpense.setOnClickListener(IconPallet_mega.this);
        imgStockInq.setOnClickListener(IconPallet_mega.this);
        imgSync.setOnClickListener(IconPallet_mega.this);
        imgUpload.setOnClickListener(IconPallet_mega.this);
        imgStockDown.setOnClickListener(IconPallet_mega.this);
        imgPrinter.setOnClickListener(IconPallet_mega.this);
        imgScheduleUp.setOnClickListener(IconPallet_mega.this);
        imgDatabase.setOnClickListener(IconPallet_mega.this);
        imgSalesRep.setOnClickListener(IconPallet_mega.this);
        imgSalRet.setOnClickListener(IconPallet_mega.this);
        imgReceipt.setOnClickListener(IconPallet_mega.this);
        notification.setOnClickListener(IconPallet_mega.this);
        chat.setOnClickListener(IconPallet_mega.this);
        imgDashboard2.setOnClickListener(IconPallet_mega.this);
        imgAddUser.setOnClickListener(IconPallet_mega.this);


        //get all messages
        MessageDS messageDS = new MessageDS(getActivity());
        ArrayList<objMessage> messages = messageDS.getAllMessages();
        msgCount = (TextView) view.findViewById(R.id.msgCount);
        if (messages.size() > 0) {
            msgCount.setVisibility(View.VISIBLE);
            msgCount.setText("" + messages.size());
        }

//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // User is signed in
//                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//
//                } else {
//                    // User is signed out
//                    Log.d(TAG, "onAuthStateChanged:signed_out");
//                    //   Login("test@gmail.com", "123456");
//                }
//                // ...
//            }
//        };

        return view;
    }

    public void Login(String uname, String pw) {

//        mAuth.signInWithEmailAndPassword(uname, pw)
//                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
//
//                        if (task.isSuccessful()) {
//                            Log.w(TAG, "signInWithEmail:failed", task.getException());
//                            //   Signup("ss@gmail.com", "123456");
//                        } else {
//                            Log.e("signup", localSP.getString("MAC_Address", "No MAC Address").replace(":","_") + "@gmail.com");
//                            // Signup(localSP.getString("MAC_Address", "No MAC Address") + "@gmail.com", "123456");
//                        }
//
//                        // ...
//                    }
//
//                });
    }

//    public void Signup(String email, String password) {
//
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            Login(localSP.getString("MAC_Address", "No MAC Address") + "@gmail.com", "123456");
//                            //updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//
//                            //  updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });
//    }

    @Override
    public void onResume() {
        super.onResume();
        MessageDS messageDS = new MessageDS(getActivity());
        ArrayList<objMessage> messages = messageDS.getAllMessages();
        if (messages.size() > 0) {
            msgCount.setVisibility(View.VISIBLE);
            msgCount.setText("" + messages.size());
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imgTourInfo:
                imgTour.startAnimation(animScale);
                UtilityContainer.mLoadFragment(new TourInfo(), activity);
                break;

            case R.id.imgDashboard2:
                imgTour.startAnimation(animScale);
                UtilityContainer.mLoadFragment(new DashboardSummery(), activity);
                break;

            case R.id.imgPresale:
                imgPresale.startAnimation(animScale);

                if (isPresalePending) {
                    PreSales preSales = new PreSales();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("Active", true);
                    preSales.setArguments(bundle);
                    UtilityContainer.mLoadFragment(preSales, activity);
                } else
                    UtilityContainer.mLoadFragment(new PreSalesInvoice(), activity);

                break;

            case R.id.imgVansale:
                imgVansale.startAnimation(animScale);

                if (isInvoicePending) {
                    VanSales vanSales = new VanSales();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("Active", true);
                    vanSales.setArguments(bundle);
                    UtilityContainer.mLoadFragment(vanSales, activity);
                } else
                    UtilityContainer.mLoadFragment(new VanSaleInvoice(), activity);

                break;
            case R.id.notification:
                notification.startAnimation(animScale);

                if (isInvoicePending) {
                    MessagesView messagesView = new MessagesView();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("Active", true);
                    messagesView.setArguments(bundle);
                    UtilityContainer.mLoadFragment(messagesView, activity);
                } else
                    UtilityContainer.mLoadFragment(new MessagesView(), activity);

                break;

            case R.id.Chat:
                chat.startAnimation(animScale);

                if (isInvoicePending) {
                    DeviceChat deviceChat = new DeviceChat();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("Active", true);
                    deviceChat.setArguments(bundle);
                    UtilityContainer.mLoadFragment(deviceChat, activity);
                } else
                    UtilityContainer.mLoadFragment(new DeviceChat(), activity);

                break;

            case R.id.imgNonProd:
                imgNonprod.startAnimation(animScale);
                if (isNonprdPending){
                    NonProductiveMain nonprd = new NonProductiveMain();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("Active", true);
                    nonprd.setArguments(bundle);
                    UtilityContainer.mLoadFragment(nonprd, activity);
                } else
                UtilityContainer.mLoadFragment(new NonProductiveMain(), activity);
                break;

            case R.id.imgDashboard:
                imgDashboard.startAnimation(animScale);
                UtilityContainer.mLoadFragment(new Dashboard(), activity);
                break;

            case R.id.imgExpense:
                imgExpense.startAnimation(animScale);
                UtilityContainer.mLoadFragment(new ExpenseMain(), activity);
                break;

            case R.id.imgStockInquiry:
                imgStockInq.startAnimation(animScale);
                new StockInquiryDialog(getActivity());
                break;

            case R.id.imgSync:
                imgSync.startAnimation(animScale);
                mSecondarySynchronize();
                break;

            case R.id.imgUpload:
                imgUpload.startAnimation(animScale);
                mUploadDialog();
                break;

            case R.id.imgDownload:
                imgStockDown.startAnimation(animScale);
                mDownloadStockData(activity);
                break;

            case R.id.imgPrinter:
                imgPrinter.startAnimation(animScale);
                UtilityContainer.mPrinterDialogbox(activity);
                break;

            case R.id.imgScheUpload:
                imgScheduleUp.startAnimation(animScale);
                UtilityContainer.mRescheduleUploadDialogbox(activity);
                break;

            case R.id.imgSqlite:
                imgDatabase.startAnimation(animScale);
                UtilityContainer.mSQLiteDatabase(activity);
                break;

            case R.id.imgSalrep:
                imgSalesRep.startAnimation(animScale);
                UtilityContainer.mRepsDetailsDialogbox(activity);
                break;
            case R.id.imgReturn:
                imgSalRet.startAnimation(animScale);
                UtilityContainer.mLoadFragment(new SalesReturnHistory(), activity);
                break;
            case R.id.imgReceipt:

                UtilityContainer.mLoadFragment(new ReceiptInvoice(),activity);
                imgReceipt.startAnimation(animScale);

                break;

            case R.id.imgAddUser:

                UtilityContainer.mLoadFragment(new CustomerRegMain(),activity);
                imgReceipt.startAnimation(animScale);

                break;

        }
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onTaskCompleted(TaskType taskType, String result) {

        String URL = "http://" + localSP.getString("URL", "").toString();
        SalRepDS salRepDS = new SalRepDS(activity);

        switch (taskType) {

            case FSALREP:

                Log.v("FSALREP", result.toString());
                if (!result.equals("0")) {
                    String FCONTROL_URL = connURLsvc + "/fControl/mobile123/" + localSP.getString("Console_DB", "").toString();
                    new Downloader(activity, IconPallet_mega.this, TaskType.FCONTROL, URL, FCONTROL_URL).execute();
                } else {
                    Toast.makeText(activity, "MAC address is not registered with ERP !", Toast.LENGTH_LONG).show();
                    mSyncDialogbox();
                }
                break;

            case FCONTROL:
                Log.v("FCONTROL", result.toString());

                String FDEBTOR_URL = connURLsvc + "/Fdebtor/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + salRepDS.getCurrentRepCode();
                new Downloader(activity, IconPallet_mega.this, TaskType.FDEBTOR, URL, FDEBTOR_URL).execute();

                break;

            case FDEBTOR:
                Log.v("FDEBTOR", result.toString());

                String FCOMPANYSETTING_URL = connURLsvc + "/fCompanySetting/mobile123/" + localSP.getString("Console_DB", "").toString();
                new Downloader(activity, IconPallet_mega.this, TaskType.FCOMPANYSETTING, URL, FCOMPANYSETTING_URL).execute();

                break;

            case FCOMPANYSETTING:
                Log.v("FCOMPANYSETTING", result.toString());

                String FCOMPANYBRANCH_URL = connURLsvc + "/FCompanyBranch/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + salRepDS.getCurrentRepCode();
                new Downloader(activity, IconPallet_mega.this, TaskType.FCOMPANYBRANCH, URL, FCOMPANYBRANCH_URL).execute();

                break;

            case FCOMPANYBRANCH:
                Log.v("FCOMPANYBRANCH", result.toString());
                String FLOCATIONS_URL = connURLsvc + "/fLocations/mobile123/" + localSP.getString("Console_DB", "").toString()+ "/" + salRepDS.getCurrentRepCode();
                new Downloader(activity, IconPallet_mega.this, TaskType.FLOCATIONS, URL, FLOCATIONS_URL).execute();

                break;

            case FLOCATIONS:
                Log.v("FLOCATIONS", result.toString());
                String Farea_URL = connURLsvc + "/farea/mobile123/" + localSP.getString("Console_DB", "").toString();
                new Downloader(activity, IconPallet_mega.this, TaskType.FAREA, URL, Farea_URL).execute();

                break;

            case FAREA:
                Log.v("FAREA", result.toString());
                String downLoadURL = connURLsvc + "/fItemLoc/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + salRepDS.getCurrentLocCode();
                new Downloader(activity, IconPallet_mega.this, TaskType.FITEMLOC, URL, downLoadURL).execute();

                break;

            case FITEMLOC:
//                if (downloadStock) {
//                    Log.v("FSTKIN", result.toString());
//                    String fstkin_URL = connURLsvc + "/fstkin/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + salRepDS.getCurrentRepCode();
//                    new Downloader(activity, IconPallet_mega.this, TaskType.FSTKIN, URL, fstkin_URL).execute();
//                } else {
                    Log.v("FITEMLOC", result.toString());
                    String FITEMPRI_URL = connURLsvc + "/fItemPri/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + salRepDS.getCurrentRepCode();
                    new Downloader(activity, IconPallet_mega.this, TaskType.FITEMPRI, URL, FITEMPRI_URL).execute();
                //}

                break;

            case FITEMPRI:
                Log.v("FITEMPRI", result.toString());

                String FITEM_URL = connURLsvc + "/fItems/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + salRepDS.getCurrentRepCode();
                new Downloader(activity, IconPallet_mega.this, TaskType.FITEMS, URL, FITEM_URL).execute();

                break;

            case FITEMS:
                Log.v("FITEMS", result.toString());
//                                String ftax = connURLsvc + "/Ftax/mobile123/" + localSP.getString("Console_DB", "").toString();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FTAX, URL, ftax).execute();
//
//                break;
//
//            case FTAX:
//                Log.v("FTAX", result.toString());
                String ftaxhed = connURLsvc + "/Ftaxhed/mobile123/" + localSP.getString("Console_DB", "").toString();
                new Downloader(activity, IconPallet_mega.this, TaskType.FTAXHED, URL, ftaxhed).execute();

                break;

            case FTAXHED:
                Log.v("FTAXHED", result.toString());
                String ftaxdet = connURLsvc + "/Ftaxdet/mobile123/" + localSP.getString("Console_DB", "").toString();
                new Downloader(activity, IconPallet_mega.this, TaskType.FTAXDET, URL, ftaxdet).execute();

                break;
//            case FTAXDET:
//                Log.v("FTAXDET", result.toString());
//                String fddbnote = connURLsvc + "/fDdbNote/mobile123/" + localSP.getString("Console_DB", "").toString();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FDDBNOTE, URL, fddbnote).execute();
            case FTAXDET:
                Log.v("FTAXDET", result.toString());
                String fddbnote = connURLsvc + "/fDdbNoteWithCondition/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + salRepDS.getCurrentRepCode();
                new Downloader(activity, IconPallet_mega.this, TaskType.FDDBNOTE, URL, fddbnote).execute();

                break;
            case FDDBNOTE:
                Log.v("FDDBNOTE", result.toString());
                String freason = connURLsvc + "/freason/mobile123/" + localSP.getString("Console_DB", "").toString();
                new Downloader(activity, IconPallet_mega.this, TaskType.FREASON, URL, freason).execute();

                break;

            case FREASON:
                Log.v("FREASON", result.toString());
                                String Froute_URL = connURLsvc + "/froute/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + salRepDS.getCurrentRepCode();
                new Downloader(activity, IconPallet_mega.this, TaskType.FROUTE, URL, Froute_URL).execute();

                break;

            case FROUTE:

                Log.v("FROUTE", result.toString());
//                String Ftown_URL = connURLsvc + "/ftown/mobile123/" + localSP.getString("Console_DB", "").toString();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FTOWN, URL, Ftown_URL).execute();
//
//                break;
//
//            case FTOWN:
//
//                Log.v("FTOWN", result.toString());
                String Fbank_URL = connURLsvc + "/fbank/mobile123/" + localSP.getString("Console_DB", "").toString();
                new Downloader(activity, IconPallet_mega.this, TaskType.FBANK, URL, Fbank_URL).execute();
                break;

            case FBANK:

                Log.v("FBANK", result.toString());
                String Froutedet_URL = connURLsvc + "/froutedet/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + salRepDS.getCurrentRepCode();
                new Downloader(activity, IconPallet_mega.this, TaskType.FROUTEDET, URL, Froutedet_URL).execute();

                break;
            case FROUTEDET:
                String fexpense = connURLsvc + "/fexpense/mobile123/" + localSP.getString("Console_DB", "").toString();
                new Downloader(activity, IconPallet_mega.this, TaskType.FEXPENSE, URL, fexpense).execute();

                break;
            case FEXPENSE:

                Log.v("FFREEMSLAB", result.toString());
                String FSTKIN_URL = connURLsvc + "/fstkin/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + salRepDS.getCurrentRepCode();
                new Downloader(activity, IconPallet_mega.this, TaskType.FSTKIN, URL, FSTKIN_URL).execute();

                break;

            case FSTKIN:

                Log.v("FROUTEDET", result.toString());
                SharedPreferencesClass.setLocalSharedPreference(activity, "Sync_Status", "Success");

                if (!new SalRepDS(activity).getCurrentRepCode().equals("") && localSP.getString("Sync_Status", "").toString().equals("Success")) {

                    if (downloadStock) {
                        downloadStock = false;
                        Toast.makeText(activity, "Stock download completed successfully!", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(activity, "Sync completed successfully!", Toast.LENGTH_LONG).show();

                    if (!isSecondarySync) {
                        Intent mainActivity = new Intent(activity, LoginActivity.class);
                        startActivity(mainActivity);
                        activity.finish();
                    }

                } else {
                    Toast.makeText(activity, "Sync unsuccessful!", Toast.LENGTH_LONG).show();
                    Intent mainActivity = new Intent(activity, SplashActivity.class);
                    startActivity(mainActivity);
                    activity.finish();
                }

                break;
                //


                //
//






//
//            case FEXPENSE:
//                Log.v("FEXPENSE", result.toString());
//                String tourhed = connURLsvc + "/ftourhed/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + salRepDS.getCurrentRepCode();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FTOURHED, URL, tourhed).execute();
//
//                break;
//
//            case FTOURHED:
//
//                Log.v("FTOURHED", result.toString());
//                String FFREEITEM_URL = connURLsvc + "/fFreeslab/mobile123/" + localSP.getString("Console_DB", "").toString();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FFREESLAB, URL, FFREEITEM_URL).execute();
//
//                break;
//
//            case FFREESLAB:
//
//                Log.v("FFREESLAB", result.toString());
//                String FFREEHed_URL = connURLsvc + "/fFreehed/mobile123/" + localSP.getString("Console_DB", "").toString();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FFREEHED, URL, FFREEHed_URL).execute();
//
//                break;
//
//            case FFREEHED:
//
//                Log.v("FFREEHED", result.toString());
//                String FFREEdet_URL = connURLsvc + "/fFreedet/mobile123/" + localSP.getString("Console_DB", "").toString();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FFREEDET, URL, FFREEdet_URL).execute();
//
//                break;
//
//            case FFREEDET:
//
//                Log.v("FFREEDET", result.toString());
//                String FFREEdeb_URL = connURLsvc + "/fFreedeb/mobile123/" + localSP.getString("Console_DB", "").toString();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FFREEDEB, URL, FFREEdeb_URL).execute();
//
//                break;
//
//            case FFREEDEB:
//
//                Log.v("FAREA", result.toString());

//                String Ffree_URL = connURLsvc + "/ffreeitem/mobile123/" + localSP.getString("Console_DB", "").toString();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FFREEITEM, URL, Ffree_URL).execute();
//
//                break;
//
//            case FFREEITEM:
//
//                Log.v("FFREEITEM", result.toString());
//                String Fdebpri_URL = connURLsvc + "/fdebitempri/mobile123/" + localSP.getString("Console_DB", "").toString();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FDEBITEMPRI, URL, Fdebpri_URL).execute();
//
//                break;
//
//            case FDEBITEMPRI:
//
//                Log.v("FDEBITEMPRI", result.toString());

//                String Fdiscdeb_URL = connURLsvc + "/fdiscdeb/mobile123/" + localSP.getString("Console_DB", "").toString();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FDISCDEB, URL, Fdiscdeb_URL).execute();
//
//                break;
//
//            case FDISCDEB:
//
//                Log.v("FDISCDEB", result.toString());
//                String Fdiscdet_URL = connURLsvc + "/fdiscdet/mobile123/" + localSP.getString("Console_DB", "").toString();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FDISCDET, URL, Fdiscdet_URL).execute();
//
//                break;
//
//            case FDISCDET:
//
//                Log.v("FDISCDET", result.toString());
//                String Fdiscslab_URL = connURLsvc + "/fdiscslab/mobile123/" + localSP.getString("Console_DB", "").toString();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FDISCSLAB, URL, Fdiscslab_URL).execute();
//
//                break;
//
//            case FDISCSLAB:
//
//                Log.v("FDISCDET", result.toString());
//                String Fdisched_URL = connURLsvc + "/fdisched/mobile123/" + localSP.getString("Console_DB", "").toString();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FDISCHED, URL, Fdisched_URL).execute();
//
//                break;
//
//            case FDISCHED:
//
//                Log.v("FDISCHED", result.toString());
//                String Ffreeslab_URL = connURLsvc + "/ffreemslab/mobile123/" + localSP.getString("Console_DB", "").toString();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FFREEMSLAB, URL, Ffreeslab_URL).execute();
//
//                break;
//
//            case FFREEMSLAB:
//
//                Log.v("FFREEMSLAB", result.toString());

//                String fstkin_URL = connURLsvc + "/fstkin/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + salRepDS.getCurrentRepCode();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FSTKIN, URL, fstkin_URL).execute();
//
//                break;
//
//            case FSTKIN:

//                Log.v("FSTKIN", result.toString());
//                String FSTKIN_URL = connURLsvc + "/fbrandtarget/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + salRepDS.getCurrentRepCode();
//                new Downloader(activity, IconPallet_mega.this, TaskType.FBRANDTARGET, URL, FSTKIN_URL).execute();
//
//                break;
//
//            case FBRANDTARGET:
//
//                Log.v("fbrandtarget", result.toString());




            default:
                break;
        }
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mSyncDialogbox() {

        final String sp_url = localSP.getString("URL", "").toString();
        String spConsole_DB = localSP.getString("Console_DB", "").toString();

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View promptView = layoutInflater.inflate(R.layout.settings_sync_layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Download Data");
        alertDialogBuilder.setView(promptView);

        final EditText serverURL = (EditText) promptView.findViewById(R.id.et_server_url);
        final Spinner sp_serverDB = (Spinner) promptView.findViewById(R.id.spinner_server_db);

        ServerDatabaseDS ds = new ServerDatabaseDS(activity);

        List<String> list = ds.getAllDatabaseName();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_serverDB.setAdapter(dataAdapter);

        if (!sp_url.equals(""))
            serverURL.setText(sp_url);

        if (!spConsole_DB.equals("")) {
            ArrayAdapter myAdap = (ArrayAdapter) sp_serverDB.getAdapter();
            int spinnerPosition = myAdap.getPosition(spConsole_DB);
            sp_serverDB.setSelection(spinnerPosition);
        }

        alertDialogBuilder.setCancelable(false).setPositiveButton("DOWNLOAD", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String selectedDB = sp_serverDB.getSelectedItem().toString();
                SharedPreferencesClass.setLocalSharedPreference(activity, "Console_DB", selectedDB);

                if (localSP.getString("MAC_Address", "No MAC Address").equals("No MAC Address")) {


                    GetMacAddress macAddress = new GetMacAddress();
                    SharedPreferencesClass.setLocalSharedPreference(activity, "MAC_Address", macAddress.getMacAddress(activity));
                }
                Log.e("Mac", localSP.getString("MAC_Address", "No MAC Address"));

                //register user with firebase db for chating
                String URL = "http://" + sp_url;
                boolean connectionStatus = new ConnectionDetector(activity).isConnectingToInternet();
                if (connectionStatus == true) {

                    if (Patterns.WEB_URL.matcher(URL).matches()) {

                        try {
                            String FSALREP_URL = connURLsvc + "/fSalRep/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + localSP.getString("MAC_Address", "").toString().replace(":", "");
                            new Downloader(activity, IconPallet_mega.this, TaskType.FSALREP, URL, FSALREP_URL).execute();
                        } catch (Exception e) {

                        }

                    } else {
                        Toast.makeText(activity, "Invalid URL Entered. Please Enter Valid URL.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(activity, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }


/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mRemoveActiveInvoice() {
        String refno = new InvHedDS(activity).getActiveInvoiceRef();

        if (!new InvHedDS(activity).getActiveInvoiceRef().equals("None")) {

            new InvHedDS(activity).restData(refno);
            new TranIssDS(activity).ClearTable(refno);
            new InvDetDS(activity).restData(refno);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.commit();
        }
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mSecondarySynchronize() {

        isSecondarySync = true;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage("Do you want to sync data..?");
        alertDialogBuilder.setTitle("Synchronize Data");
        alertDialogBuilder.setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @SuppressWarnings("unchecked")
            public void onClick(DialogInterface dialog, int id) {

                String sText = "Complete following transactions to continue;\n";

                String resPreSales = new TranSOHedDS(getActivity()).getActiveInvoiceRef();

                if (!resPreSales.equals("None"))
                    sText += "\nPre Sales : " + resPreSales;

                String resVanSales = new InvHedDS(activity).getActiveInvoiceRef();

                if (!resVanSales.equals("None"))
                    sText += "\nVan Sales : " + resVanSales;

                if (sText.length() >= 45) {
                    String URL = "http://" + localSP.getString("URL", "").toString();
                    String FSALREP_URL = connURLsvc + "/fSalRep/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + localSP.getString("MAC_Address", "").toString().replace(":", "");

                    if (new InvHedDS(activity).isAllSynced() && new TranSOHedDS(activity).isAllSynced())
                        new Downloader(activity, IconPallet_mega.this, TaskType.FSALREP, URL, FSALREP_URL).execute();
                    else
                        Toast.makeText(activity, "Please upload completed invoices before sync..!", Toast.LENGTH_SHORT).show();
                } else
                    mActiveTransactionDialog(sText);


            }

        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();

    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mDownloadStockData(final Context context) {

        isSecondarySync = true;
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage("Do you want to download stock..?");
        alertDialogBuilder.setTitle("Stock Update");
        alertDialogBuilder.setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @SuppressWarnings("unchecked")
            public void onClick(DialogInterface dialog, int id) {

                String sText = "Complete following transactions to continue;\n";

                String resPreSales = new TranSOHedDS(getActivity()).getActiveInvoiceRef();

                if (!resPreSales.equals("None"))
                    sText += "\nPre Sales : " + resPreSales;

                String resVanSales = new InvHedDS(activity).getActiveInvoiceRef();

                if (!resVanSales.equals("None"))
                    sText += "\nVan Sales : " + resVanSales;

                if (sText.length() >= 45) {

                    final String downLoadURL = connURLsvc + "/fItemLoc/mobile123/" + localSP.getString("Console_DB", "").toString() + "/" + new SalRepDS(context).getCurrentRepCode();

                    if (new InvHedDS(context).isAllSynced() && new TranSOHedDS(context).isAllSynced()) {
                        new Downloader(context, IconPallet_mega.this, TaskType.FCONTROL, downLoadURL, "").execute();
                        downloadStock = true;
                    } else
                        Toast.makeText(context, "Please upload completed invoices before sync..!", Toast.LENGTH_SHORT).show();

                } else
                    mActiveTransactionDialog(sText);


            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.mnu_exit, menu);
    }
    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.quit:

//                List<Fragment> list = activity.getSupportFragmentManager().getFragments();
//                activity.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).remove(list.get(0)).commit();
//
//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                getActivity().finish();
//                    }
//                }, 1000);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private void mUploadDialog() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage("Do you want to upload data..?");
        alertDialogBuilder.setTitle("Uploader");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("UPLOAD", new DialogInterface.OnClickListener() {
            @SuppressWarnings("unchecked")
            public void onClick(DialogInterface dialog, int id) {

                boolean connectionStatus = new ConnectionDetector(activity).isConnectingToInternet();

                if (connectionStatus == true) {

                    String sText = "Complete following transactions to continue;\n";

                    String resPreSales = new TranSOHedDS(getActivity()).getActiveInvoiceRef();

                    if (!resPreSales.equals("None"))
                        sText += "\nPre Sales --> " + resPreSales;

                    String resVanSales = new InvHedDS(activity).getActiveInvoiceRef();

                    if (!resVanSales.equals("None"))
                        sText += "\nVan Sales --> " + resVanSales;

                    if (sText.length() == 45) {

                        ArrayList<PreSalesMapper> ordHedList = new TranSOHedDS(getActivity()).getAllUnSyncOrdHed();
                        ArrayList<VanSalesMapper> InvHedList = new InvHedDS(getActivity()).getAllUnsynced();
                        ArrayList<SalesReturnMapper> InvRHedList = new FInvRHedDS(getActivity()).getUnSyncedReturnWithInvoice();
                        ArrayList<SalesReturnMapper> InvRHedList2 = new FInvRHedDS(getActivity()).getUnSyncedReturnWithoutInvoice();
                        ArrayList<TourMapper> TourList = new TourDS(getActivity()).getUnsyncedTourData();
                        ArrayList<ExpenseMapper> Explist = new FDayExpHedDS(getActivity()).getUnSyncedData();
                        ArrayList<NonProdMapper> NonProdList = new FDaynPrdHedDS(getActivity()).getUnSyncedData();
                        ArrayList<NewCustomer>customerArrayList=new NewCustomerDS(getActivity()).getUnsyncRecord();
                        ArrayList<ReceiptMapper> ReceiptList = new RecHedDS(getActivity()).getAllUnsyncedRecHed();


					/* If records available for upload then */
                        if ((InvHedList.size() <= 0) && (TourList.size() <= 0) && (Explist.size() <= 0) && (NonProdList.size() <= 0) &&(InvRHedList.size()<=0)&& (ReceiptList.size() <= 0) && (InvRHedList2.size() <= 0))
                            Toast.makeText(getActivity(), "No Records to upload !", Toast.LENGTH_LONG).show();
                        else
                            new UploadVanSales(activity, IconPallet_mega.this, TaskType.UPLOADVANSALES).execute(InvHedList);

                    } else
                        mActiveTransactionDialog(sText);

                } else
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();

            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();

        alertD.show();
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onTaskCompleted(TaskType taskType, List<String> tempList) {

        resultList.addAll(tempList);

        switch (taskType) {


            case UPLOADVANSALES: {
//                ArrayList<VanSalesMapper> list = new InvHedDS(getActivity()).getAllUnsynced();
                ArrayList<ReceiptMapper> list = new RecHedDS(getActivity()).getAllUnsyncedRecHed();
                new UploadReceipt(getActivity(), IconPallet_mega.this, TaskType.UPLOADRECEIPT).execute(list);
//                new UploadVanSales(getActivity(), IconPallet.this, TaskType.UPLOADVANSALES).execute(list);
            }
            break;
            case UPLOADRECEIPT: {
                ArrayList<SalesReturnMapper> list = new FInvRHedDS(getActivity()).getUnSyncedReturnWithInvoice();
               // if(new FInvRHedDS(getActivity()).getItemCount(new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.))) > 0)
                new UploadSalesReturn(getActivity(), IconPallet_mega.this, TaskType.UPLOADRETURN,"insertReturn").execute(list);
            }
            break;
            case UPLOADRETURN: {
                ArrayList<SalesReturnMapper> list = new FInvRHedDS(getActivity()).getUnSyncedReturnWithoutInvoice();
                // if(new FInvRHedDS(getActivity()).getItemCount(new ReferenceNum(getActivity()).getCurrentRefNo(getResources().getString(R.string.))) > 0)
                new UploadSalesReturn(getActivity(), IconPallet_mega.this, TaskType.UPLOAD_RETURNS,"insertReturns").execute(list);
            }
            break;
            case UPLOAD_RETURNS: {
                ArrayList<NonProdMapper> list = new FDaynPrdHedDS(getActivity()).getUnSyncedData();
                new UploadNonProd(getActivity(), IconPallet_mega.this, TaskType.UPLOAD_NONPROD).execute(list);
            }
            break;

            case UPLOAD_NONPROD: {
                ArrayList<TourMapper> list = new TourDS(getActivity()).getUnsyncedTourData();
                new UploadTour(getActivity(), IconPallet_mega.this, TaskType.UPLOADTOUR).execute(list);
            }
            break;
//
            case UPLOADTOUR: {
                ArrayList<ExpenseMapper> list = new FDayExpHedDS(getActivity()).getUnSyncedData();
                new UploadExpenses(getActivity(), IconPallet_mega.this, TaskType.UPLOAD_EXPENSE).execute(list);
            }
            break;
//            case UPLOAD_NONPROD: {
//                ArrayList<NewCustomer> list = new NewCustomerDS(getActivity()).getUnsyncRecord();
//                new UplordNewCustomer(getActivity(), IconPallet.this, TaskType.UPLOADNEWCUSTOMER).execute(list);
//            }
//            break;
//            case UPLOADNEWCUSTOMER: {
//                ArrayList<ReceiptMapper> list = new RecHedDS(getActivity()).getAllUnsyncedRecHed();
//                new UploadReceipt(getActivity(), IconPallet.this, TaskType.UPLOADRECEIPT).execute(list);
//            }
//            break;

            case UPLOAD_EXPENSE:{
                String msg = "";
                for (String s : resultList) {
                    msg += s;
                }
                resultList.clear();
                mUploadResult(msg);
            }
            break;

            default:
                break;
        }
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mActiveTransactionDialog(String sMessage) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(sMessage);
        alertDialogBuilder.setTitle("Active Transactions");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_info);

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
        alertD.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public void mUploadResult(String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle("Upload Summary");

        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
        alertD.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

    }

    @Override
    public void onStart() {
        super.onStart();
      //  mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
    }
}
