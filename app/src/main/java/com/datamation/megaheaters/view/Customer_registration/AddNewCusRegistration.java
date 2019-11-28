package com.datamation.megaheaters.view.Customer_registration;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import com.datamation.megaheaters.adapter.Customer_Adapter;
import com.datamation.megaheaters.control.TaskType;
import com.datamation.megaheaters.control.upload.UploadTaskListener;
import com.github.clans.fab.FloatingActionButton;
import com.datamation.megaheaters.R;
import com.datamation.megaheaters.adapter.DistrictAdapter;
import com.datamation.megaheaters.adapter.RouteAdapter;
import com.datamation.megaheaters.adapter.TownAdapter;
import com.datamation.megaheaters.control.ReferenceNum;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.data.CompanyBranchDS;
import com.datamation.megaheaters.data.NewCustomerDS;
import com.datamation.megaheaters.data.RouteDS;
import com.datamation.megaheaters.data.SalRepDS;
import com.datamation.megaheaters.data.TownDS;
import com.datamation.megaheaters.data.fDistrictDS;
import com.datamation.megaheaters.model.NewCustomer;
import com.datamation.megaheaters.model.Route;
import com.datamation.megaheaters.model.Town;
import com.datamation.megaheaters.model.fDistrict;
import com.datamation.megaheaters.view.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by Dhanushika on 4/4/2018.
 */

public class AddNewCusRegistration extends Fragment implements UploadTaskListener {
    public EditText customerCode,
            customerName, editTextCNic, OtherCode, businessRegno, district,
            town, route, addressline1, addressline2, city, mobile, phone, fax, emailaddress;
    public ImageButton btn_Route, btn_District, btn_Town, CustomerbtnSearch;
    private ArrayList<fDistrict> fDistrictArrayList;
    private ArrayList<Town> townArrayList;
    private ArrayList<Route> routeArrayList;
    private ArrayList<NewCustomer> newCustomerArrayList;
    private String DisCode;
    private ImageView img, img2, img3, img4;
    private Switch mySwitch;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    private static int IMAGE1 = 0;
    private static int IMAGE2 = 0;
    private static int IMAGE3 = 0;
    private static int IMAGE4 = 0;
    private byte[] byteArray;
    Bitmap bitimage = null;
    Bitmap bitimage1 = null;
    Bitmap bitimage2 = null;
    Bitmap bitimage3 = null;
    private static String pictureName = null;
    private Uri filePath;
    ArrayList<Uri> uris = new ArrayList<>();
    private FloatingActionButton fab, fabDiscard, fabExit;
    SharedPref mSharedPref;
    private ArrayList<String> pictureDownloadurl;
    private ReferenceNum referenceNum;
    private String  jsonstr;
   private    ProgressDialog progressDialog;
    public static SharedPreferences localSP;
    public static final String SETTINGS = "SETTINGS";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.finac_new_customer_registration, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("Add New Customer");
        toolbar.setLogo(R.drawable.dm_logo_64);
        mSharedPref = new SharedPref(getActivity());
        referenceNum = new ReferenceNum(getActivity());
        localSP = ((MainActivity) getActivity()).getSharedPreferences(SETTINGS, 0);

        customerCode = (EditText) rootView.findViewById(R.id.editTextCustomer_Code);
        customerName = (EditText) rootView.findViewById(R.id.editText2);
        editTextCNic = (EditText) rootView.findViewById(R.id.editTextCNic);
        OtherCode = (EditText) rootView.findViewById(R.id.editTextOthercode);
        businessRegno = (EditText) rootView.findViewById(R.id.editText3);
        addressline1 = (EditText) rootView.findViewById(R.id.editText7);
        addressline2 = (EditText) rootView.findViewById(R.id.editText8);
        city = (EditText) rootView.findViewById(R.id.editText9);
        mobile = (EditText) rootView.findViewById(R.id.editText10);
        phone = (EditText) rootView.findViewById(R.id.editText11);
        fax = (EditText) rootView.findViewById(R.id.editText12);
        emailaddress = (EditText) rootView.findViewById(R.id.editText20);
        route = (EditText) rootView.findViewById(R.id.spinner4);
        district = (EditText) rootView.findViewById(R.id.spinner5);
        town = (EditText) rootView.findViewById(R.id.spinner3);
        pictureDownloadurl = new ArrayList<>();


        btn_Town = (ImageButton) rootView.findViewById(R.id.btn_T);
        btn_Route = (ImageButton) rootView.findViewById(R.id.btn_R);
        btn_District = (ImageButton) rootView.findViewById(R.id.btn_D);
        CustomerbtnSearch = (ImageButton) rootView.findViewById(R.id.btn_C6);

        img = (ImageView) rootView.findViewById(R.id.imageView2);
        img2 = (ImageView) rootView.findViewById(R.id.imageView3);
        img3 = (ImageView) rootView.findViewById(R.id.imageView5);
        img4 = (ImageView) rootView.findViewById(R.id.imageView4);
        mySwitch = (Switch) rootView.findViewById(R.id.switch1);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fabDiscard = (FloatingActionButton) rootView.findViewById(R.id.fab2);
        fabExit = (FloatingActionButton) rootView.findViewById(R.id.fab3);
        //-----------------------------------------------------------------------------------------------------------------

        //show new customer ref no
        if (mySwitch.isChecked() == true) {
            customerName.requestFocus();
            customerCode.setText(referenceNum.getCurrentRefNo(getResources().getString(R.string.newCusVal)));
            CustomerbtnSearch.setEnabled(false);
        } else {
            CustomerbtnSearch.setEnabled(true);
        }


        btn_District.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prodcutDetailsDialogbox(1);

            }
        });
        btn_Town.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prodcutDetailsDialogbox(2);
            }
        });
        btn_Route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prodcutDetailsDialogbox(3);
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMAGE1 = 1;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(intent,
                        CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMAGE2 = 2;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(intent,
                        CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMAGE3 = 3;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(intent,
                        CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IMAGE4 = 4;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getActivity().startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        fabExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitData();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customerName.getText().length() != 0
                        && addressline1.getText().length() != 0 && addressline2.getText().length() != 0
                        && mobile.getText().length() != 0
                        && town.getText().length() != 0 && district.getText().length() != 0 && route.getText().length() != 0 && city.getText().length() != 0) {
                    SaveAndUplord();

                } else {
                    Snackbar snackbar = Snackbar.make(v, "Fill All the Fields",
                            Snackbar.LENGTH_LONG);
                    View snackBarView = snackbar.getView();
                    snackBarView.setBackgroundColor(Color.RED);
                    snackbar.show();
                }

            }
        });
        CustomerbtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prodcutDetailsDialogbox(4);
            }
        });
        //get old customer for update record
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    customerName.requestFocus();
                    customerCode.setText(referenceNum.getCurrentRefNo(getResources().getString(R.string.newCusVal)));
                    CustomerbtnSearch.setEnabled(false);

                    editTextCNic.setEnabled(true);
                    customerCode.setEnabled(true);
                    customerName.setEnabled(true);
                    OtherCode.setEnabled(true);
                    businessRegno.setEnabled(true);
                    btn_District.setEnabled(true);
                    btn_Town.setEnabled(true);
                    btn_Route.setEnabled(true);
                    addressline1.setEnabled(true);
                    addressline2.setEnabled(true);
                    city.setEnabled(true);
                    mobile.setEnabled(true);
                    phone.setEnabled(true);
                    fax.setEnabled(true);
                    emailaddress.setEnabled(true);
                    img.setEnabled(true);
                    img2.setEnabled(true);
                    img3.setEnabled(true);
                    img3.setEnabled(true);
                } else {
                    CustomerbtnSearch.setEnabled(true);
                    customerCode.setText("");
                    customerCode.setEnabled(false);
                    customerName.setEnabled(false);
                    OtherCode.setEnabled(false);
                    editTextCNic.setEnabled(false);
                    businessRegno.setEnabled(false);
                    btn_District.setEnabled(false);
                    btn_Town.setEnabled(false);
                    btn_Route.setEnabled(false);
                    addressline1.setEnabled(false);
                    addressline2.setEnabled(false);
                    city.setEnabled(false);
                    mobile.setEnabled(false);
                    phone.setEnabled(false);
                    fax.setEnabled(false);
                    emailaddress.setEnabled(false);
                    img.setEnabled(false);
                    img2.setEnabled(false);
                    img3.setEnabled(false);
                    img3.setEnabled(false);
                }
            }
        });

        fabDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearFiled();
            }
        });
        return rootView;
    }

    //---------------------------------Get Details from db---------------------------------------
    public void prodcutDetailsDialogbox(final int Flag) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.details_search_item);
        dialog.setCancelable(true);
        final SearchView search = (SearchView) dialog.findViewById(R.id.et_search);
        final ListView Detlist = (ListView) dialog.findViewById(R.id.lv_product_list);

        final fDistrictDS ds = new fDistrictDS(getActivity());
        final TownDS townDS = new TownDS(getActivity());
        final RouteDS routeDS = new RouteDS(getActivity());
        final NewCustomerDS newCustomerDS = new NewCustomerDS(getActivity());

        Detlist.clearTextFilter();
        if (Flag == 1) {
            fDistrictArrayList = ds.getDistrict("");
            Detlist.setAdapter(new DistrictAdapter(getActivity(), fDistrictArrayList));
            town.setText("");
            route.setText("");
        } else if (Flag == 2) {
            townArrayList = townDS.getTown(DisCode);
            Detlist.setAdapter(new TownAdapter(getActivity(), townArrayList));
            route.setText("");
        } else if (Flag == 3) {
            routeArrayList = routeDS.getRoute();
            Detlist.setAdapter(new RouteAdapter(getActivity(), routeArrayList));

        } else if (Flag == 4) {
            newCustomerArrayList = newCustomerDS.getAllNewCusDetailsForEdit("");
            Detlist.setAdapter(new Customer_Adapter(getActivity(), newCustomerArrayList));

        }
//================================================
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if (Flag == 1) {
                        fDistrictArrayList = ds.getDistrict(newText);
                        Detlist.setAdapter(new DistrictAdapter(getActivity(), fDistrictArrayList));
                    } else if (Flag == 2) {
                        //search from arraylist not from DB
                        ArrayList<Town> townS_result = new ArrayList<Town>();
                        townS_result.clear();
                        for (Town town : townArrayList) {

                            String tName = town.getFTOWN_NAME();
                            if (tName.startsWith(newText)) {

                                townS_result.add(town);
                            }
                        }
                        Detlist.setAdapter(new TownAdapter(getActivity(), townS_result));

                    } else if (Flag == 3) {
                        routeArrayList = routeDS.getRoute();
                        Detlist.setAdapter(new RouteAdapter(getActivity(), routeArrayList));
                    } else if (Flag == 4) {
                        newCustomerArrayList = newCustomerDS.getAllNewCusDetailsForEdit("");
                        Detlist.setAdapter(new Customer_Adapter(getActivity(), newCustomerArrayList));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
        //-=========================================
        Detlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (Flag == 1) {
                    district.setText(fDistrictArrayList.get(position).getDISTRICT_NAME());
                    DisCode = fDistrictArrayList.get(position).getDISTRICT_CODE();
                } else if (Flag == 2) {
                    town.setText(townArrayList.get(position).getFTOWN_NAME());
                } else if (Flag == 3) {
                    route.setText(routeArrayList.get(position).getFROUTE_ROUTECODE());

                } else if (Flag == 4) {
                    editTextCNic.setEnabled(true);
                    customerName.setEnabled(true);
                    OtherCode.setEnabled(true);
                    businessRegno.setEnabled(true);
                    addressline1.setEnabled(true);
                    addressline2.setEnabled(true);
                    city.setEnabled(true);
                    mobile.setEnabled(true);
                    phone.setEnabled(true);
                    fax.setEnabled(true);
                    emailaddress.setEnabled(true);
                    img.setEnabled(true);
                    img2.setEnabled(true);
                    img3.setEnabled(true);
                    img3.setEnabled(true);

                    btn_District.setEnabled(true);
                    btn_Town.setEnabled(true);
                    btn_Route.setEnabled(true);


                    customerCode.setText(newCustomerArrayList.get(position).getCUSTOMER_ID());
                    customerName.setText(newCustomerArrayList.get(position).getNAME());
                    OtherCode.setText(newCustomerArrayList.get(position).getC_OTHERCODE());
                    businessRegno.setText(newCustomerArrayList.get(position).getC_REGNUM());
                    editTextCNic.setText(newCustomerArrayList.get(position).getCUSTOMER_NIC());
                    addressline1.setText(newCustomerArrayList.get(position).getADDRESS1());
                    addressline2.setText(newCustomerArrayList.get(position).getADDRESS2());

                    fDistrictDS districtDS = new fDistrictDS(getActivity());
                    district.setText(districtDS.getName(newCustomerArrayList.get(position).getDISTRICT()));

                    RouteDS routeDS1 = new RouteDS(getActivity());
                    route.setText(newCustomerArrayList.get(position).getROUTE_ID());

                    TownDS townDS1 = new TownDS(getActivity());
                    route.setText(townDS1.getCode(newCustomerArrayList.get(position).getC_TOWN()));
                    if (newCustomerArrayList.get(position).getCITY().isEmpty()) {
                        city.setText("N/A");
                    } else {
                        city.setText(newCustomerArrayList.get(position).getCITY());
                    }

                    if (newCustomerArrayList.get(position).getMOBILE().isEmpty()) {
                        mobile.setText("-");
                    } else {
                        mobile.setText(newCustomerArrayList.get(position).getMOBILE());
                    }

                    if (newCustomerArrayList.get(position).getPHONE().isEmpty()) {
                        phone.setText("N/A");
                    } else {
                        phone.setText(newCustomerArrayList.get(position).getPHONE());
                    }

                    if (newCustomerArrayList.get(position).getFAX().isEmpty()) {
                        fax.setText("N/A");
                    } else {
                        fax.setText(newCustomerArrayList.get(position).getFAX());
                    }

                    if (newCustomerArrayList.get(position).getE_MAIL().isEmpty()) {
                        emailaddress.setText("N/A");
                    } else {
                        emailaddress.setText(newCustomerArrayList.get(position).getE_MAIL());
                    }

                }


                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public void SaveAndUplord() {
        ArrayList<NewCustomer> cusList = new ArrayList<NewCustomer>();
        cusList.clear();
        NewCustomerDS newCustomerDS = new NewCustomerDS(getActivity());
        NewCustomer newCustomer = new NewCustomer();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        SalRepDS fSalRe = new SalRepDS(getActivity());
        TownDS townDS = new TownDS(getActivity());
        fDistrictDS districtDS = new fDistrictDS(getActivity());

        CompanyBranchDS branchDS = new CompanyBranchDS(getActivity());

        newCustomer.setnNumVal(branchDS.getCurrentNextNumVal(getActivity().getResources().getString(R.string.newCusVal)));
        newCustomer.setCONSOLE_DB(localSP.getString("Console_DB", "").toString());
        newCustomer.setC_REPCODE(fSalRe.getCurrentRepCode());
        newCustomer.setCUSTOMER_ID(customerCode.getText().toString());
        newCustomer.setC_OTHERCODE(OtherCode.getText().toString());
        newCustomer.setNAME(customerName.getText().toString());
        newCustomer.setCUSTOMER_NIC(editTextCNic.getText().toString());
        newCustomer.setADDRESS1(addressline1.getText().toString());
        newCustomer.setADDRESS2(addressline2.getText().toString());
        newCustomer.setCITY(city.getText().toString());

        newCustomer.setPHONE(phone.getText().toString());
        newCustomer.setMOBILE(mobile.getText().toString());
        newCustomer.setFAX(fax.getText().toString());
        newCustomer.setE_MAIL(emailaddress.getText().toString());
        newCustomer.setC_TOWN(townDS.getCode(town.getText().toString()));//
        newCustomer.setROUTE_ID(route.getText().toString());//
        newCustomer.setDISTRICT(districtDS.getCode(district.getText().toString()));//
        newCustomer.setOLD_CODE("NA");

        newCustomer.setCUSTOMER_ID(customerCode.getText().toString());
        newCustomer.setC_OTHERCODE(OtherCode.getText().toString());


        if (pictureDownloadurl.size() > 0) {

            newCustomer.setC_IMAGE(pictureDownloadurl.get(0));
        } else {
            newCustomer.setC_IMAGE("NA");
        }

        if (pictureDownloadurl.size() > 1) {
            newCustomer.setC_IMAGE1(pictureDownloadurl.get(1));
        } else {
            newCustomer.setC_IMAGE1("NA");
        }

        if (pictureDownloadurl.size() > 2) {
            newCustomer.setC_IMAGE2(pictureDownloadurl.get(2));
        } else {
            newCustomer.setC_IMAGE2("NA");
        }

        if (pictureDownloadurl.size() > 3) {
            newCustomer.setC_IMAGE3(pictureDownloadurl.get(3));
        } else {
            newCustomer.setC_IMAGE3("NA");
        }
        if (businessRegno.getText().toString().isEmpty()) {
            newCustomer.setC_REGNUM("NA");
        } else {
            newCustomer.setC_REGNUM(businessRegno.getText().toString());
        }

        newCustomer.setC_ADDDATE(dateFormat.format(date));
        newCustomer.setC_LATITUDE("0.000");
        newCustomer.setC_LONGITUDE("0.000");
        newCustomer.setAddMac("0");
        newCustomer.setC_SYNCSTATE("0");

        cusList.add(newCustomer);
        if (newCustomerDS.createOrUpdateCustomer(cusList) > 0) {
            referenceNum.nNumValueInsertOrUpdate(getResources().getString(R.string.newCusVal));
           UtilityContainer.mLoadFragment(new CustomerRegMain(), getActivity());
           // new UplordNewCustomer(getActivity(),AddNewCusRegistration.this, TaskType.UPLOADNEWCUSTOMER).execute(cusList);

            android.widget.Toast.makeText(getActivity(), " saved successfully..!", android.widget.Toast.LENGTH_SHORT).show();
        }else{
            android.widget.Toast.makeText(getActivity(), "Failed..", android.widget.Toast.LENGTH_SHORT).show();
        }
    }


    //--------------------------------------------------------------------------------------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

                    Bitmap bmp = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    try {
                        if (IMAGE1 == 1) {
                            img.setImageBitmap(bitmap);
                            bitimage = bitmap;

                            pictureName = "img" + String.valueOf(IMAGE1);

                            IMAGE1 = 0;


                            filePath = data.getData();
                            uris.add(filePath);
                            Log.d("<>1>>>>", "" + filePath);

                        } else if (IMAGE2 == 2) {
                            img2.setImageBitmap(bitmap);
                            bitimage1 = bitmap;
                            pictureName = "img" + String.valueOf(IMAGE2);
                            IMAGE2 = 0;
                            filePath = data.getData();
                            uris.add(filePath);

                            Log.d("<><>2>>>", "" + filePath);
                        } else if (IMAGE3 == 3) {
                            img3.setImageBitmap(bitmap);
                            bitimage2 = bitmap;
                            pictureName = "img" + String.valueOf(IMAGE3);
                            IMAGE3 = 0;
                            filePath = data.getData();
                            uris.add(filePath);
                            Log.d("<>22<>>>>", "" + filePath);

                        } else if (IMAGE4 == 4) {
                            img4.setImageBitmap(bitmap);
                            bitimage3 = bitmap;
                            pictureName = "img" + String.valueOf(IMAGE4);
                            IMAGE4 = 0;
                            filePath = data.getData();
                            uris.add(filePath);
                            Log.d("<><>>>>", "" + filePath);

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    System.out.println("uris ; " + uris.size() + "-" + pictureName);
                    uploadFile(pictureName);


                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void uploadFile(String imgName) {
        //if there is a file to upload

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String imagepathName = sdf.format(new Date());
        imagepathName = imgName + imagepathName;

        if (filePath != null) {
            //displaying a progress dialog while upload is going on
        progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            FirebaseApp.initializeApp(getActivity());

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference riversRef = storage.getReferenceFromUrl("gs://indrasfa.appspot.com/").child("images/" + imagepathName);
            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();
                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            System.out.println("downloadUri" + downloadUri.toString());

                            pictureDownloadurl.add(downloadUri.toString()); // add uploaded image url to list

                            //and displaying a success toast
                            Toast.makeText(getActivity(), "Picture Uploaded ", Toast.LENGTH_LONG).show();
                        }

                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });


        }


        //if there is not any file
        else {
            //you can display an error toast
        }


        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();


        try {

            for (int i = 0; i < uris.size(); i++) {

                jsonObject.put("value", uris.get(i));
                jsonArray.put(jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }

        jsonstr = jsonArray.toString();

        System.out.println("jsonstr" + jsonstr);

    }

    public void ClearFiled() {
        customerName.setText("");
        editTextCNic.setText("");
        OtherCode.setText("");
        businessRegno.setText("");
        district.setText("");
        town.setText("");
        route.setText("");
        addressline1.setText("");
        addressline2.setText("");
        city.setText("");
        mobile.setText("");
        phone.setText("");
        fax.setText("");
        emailaddress.setText("");

    }


    private void exitData() {
        UtilityContainer.mLoadFragment(new CustomerRegMain(), getActivity());
        android.widget.Toast.makeText(getActivity(), "Success", android.widget.Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTaskCompleted(TaskType taskType, List<String> list) {

    }

    @Override
    public void onDetach() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("OnResume","oo");

    }
}
