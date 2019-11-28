package com.datamation.megaheaters.control.download;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.datamation.megaheaters.control.TaskType;
import com.datamation.megaheaters.data.AreaDS;
import com.datamation.megaheaters.data.BankDS;
import com.datamation.megaheaters.data.BrandDS;
import com.datamation.megaheaters.data.BrandTargetDS;
import com.datamation.megaheaters.data.CompanyBranchDS;
import com.datamation.megaheaters.data.CompanySettingDS;
import com.datamation.megaheaters.data.ControlDS;
import com.datamation.megaheaters.data.DebItemPriDS;
import com.datamation.megaheaters.data.DebtorDS;
import com.datamation.megaheaters.data.DiscdebDS;
import com.datamation.megaheaters.data.DiscdetDS;
import com.datamation.megaheaters.data.DischedDS;
import com.datamation.megaheaters.data.DiscslabDS;
import com.datamation.megaheaters.data.ExpenseDS;
import com.datamation.megaheaters.data.FDDbNoteDS;
import com.datamation.megaheaters.data.FIteDebDetDS;
import com.datamation.megaheaters.data.FItenrDetDS;
import com.datamation.megaheaters.data.FItenrHedDS;
import com.datamation.megaheaters.data.FinvDetL3DS;
import com.datamation.megaheaters.data.FreeDebDS;
import com.datamation.megaheaters.data.FreeDetDS;
import com.datamation.megaheaters.data.FreeHedDS;
import com.datamation.megaheaters.data.FreeItemDS;
import com.datamation.megaheaters.data.FreeMslabDS;
import com.datamation.megaheaters.data.FreeSlabDS;
import com.datamation.megaheaters.data.GroupDS;
import com.datamation.megaheaters.data.ItemLocDS;
import com.datamation.megaheaters.data.ItemPriDS;
import com.datamation.megaheaters.data.ItemsDS;
import com.datamation.megaheaters.data.LocationsDS;
import com.datamation.megaheaters.data.MerchDS;
import com.datamation.megaheaters.data.ReasonDS;
import com.datamation.megaheaters.data.RouteDS;
import com.datamation.megaheaters.data.RouteDetDS;
import com.datamation.megaheaters.data.STKInDS;
import com.datamation.megaheaters.data.SalRepDS;
import com.datamation.megaheaters.data.SkuDS;
import com.datamation.megaheaters.data.SubBrandDS;
import com.datamation.megaheaters.data.TaxDS;
import com.datamation.megaheaters.data.TaxDetDS;
import com.datamation.megaheaters.data.TaxHedDS;
import com.datamation.megaheaters.data.TermDS;
import com.datamation.megaheaters.data.TourHedDS;
import com.datamation.megaheaters.data.TownDS;
import com.datamation.megaheaters.data.TypeDS;
//import com.datamation.indrasfa.data.fDistrictDS;
import com.datamation.megaheaters.model.Area;
import com.datamation.megaheaters.model.Bank;
import com.datamation.megaheaters.model.Brand;
import com.datamation.megaheaters.model.BrandTarget;
import com.datamation.megaheaters.model.CompanyBranch;
import com.datamation.megaheaters.model.CompanySetting;
import com.datamation.megaheaters.model.Control;
import com.datamation.megaheaters.model.DebItemPri;
import com.datamation.megaheaters.model.Debtor;
import com.datamation.megaheaters.model.Discdeb;
import com.datamation.megaheaters.model.Discdet;
import com.datamation.megaheaters.model.Disched;
import com.datamation.megaheaters.model.Discslab;
import com.datamation.megaheaters.model.Expense;
import com.datamation.megaheaters.model.FDDbNote;
import com.datamation.megaheaters.model.FIteDebDet;
import com.datamation.megaheaters.model.FItenrDet;
import com.datamation.megaheaters.model.FItenrHed;
import com.datamation.megaheaters.model.FinvDetL3;
import com.datamation.megaheaters.model.FreeDeb;
import com.datamation.megaheaters.model.FreeDet;
import com.datamation.megaheaters.model.FreeHed;
import com.datamation.megaheaters.model.FreeItem;
import com.datamation.megaheaters.model.FreeMslab;
import com.datamation.megaheaters.model.FreeSlab;
import com.datamation.megaheaters.model.Group;
import com.datamation.megaheaters.model.ItemLoc;
import com.datamation.megaheaters.model.ItemPri;
import com.datamation.megaheaters.model.Items;
import com.datamation.megaheaters.model.Locations;
import com.datamation.megaheaters.model.Merch;
import com.datamation.megaheaters.model.Reason;
import com.datamation.megaheaters.model.Route;
import com.datamation.megaheaters.model.RouteDet;
import com.datamation.megaheaters.model.SKU;
import com.datamation.megaheaters.model.SalRep;
import com.datamation.megaheaters.model.StkIn;
import com.datamation.megaheaters.model.SubBrand;
import com.datamation.megaheaters.model.TERMS;
import com.datamation.megaheaters.model.Tax;
import com.datamation.megaheaters.model.TaxDet;
import com.datamation.megaheaters.model.TaxHed;
import com.datamation.megaheaters.model.TourHed;
import com.datamation.megaheaters.model.Town;
import com.datamation.megaheaters.model.Type;
//import com.datamation.indrasfa.model.fDistrict;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Downloader extends AsyncTask<Void, Integer, String> {

    Context context;
    DownloadTaskListener taskListener;
    String ConnectionURL, downLoadURL;
    TaskType taskType;
    String downloadingDataType = "";
    String TAG = "Downloader ";
    int totalRecords = 0;
    ProgressDialog progressDialog;

    public Downloader(Context context, DownloadTaskListener taskListener, TaskType taskType, String ConnURL, String downLoadURL) {

        this.context = context;
        this.taskListener = taskListener;
        this.ConnectionURL = ConnURL;
        this.taskType = taskType;
        this.downLoadURL = downLoadURL;

        this.totalRecords = 0;

        switch (taskType) {
            case DATABASENAME:
                downloadingDataType = "Main Server";
                break;

            case FDEBTOR:
                downloadingDataType = "Fdebtor";
                break;

            case FCONTROL:
                downloadingDataType = "fControl";
                break;

            case FCOMPANYSETTING:
                downloadingDataType = "fCompanySetting";
                break;

            case FCOMPANYBRANCH:
                downloadingDataType = "FCompanyBranch";
                break;

            case FSALREP:
                downloadingDataType = "fSalRep";
                break;

            case FLOCATIONS:
               downloadingDataType = "fLocations";
               break;

            case FITEMLOC:
                downloadingDataType = "FItemLoc";
                break;

            case FITEMPRI:
                downloadingDataType = "fItemPri";
                break;

            case FITEMS:
                downloadingDataType = "fItems";
                break;

            case FAREA:
                downloadingDataType = "fArea";
                break;

            case FREASON:
                downloadingDataType = "fReason";
                break;

            case FROUTE:
                downloadingDataType = "fRoute";
                break;

            case FBANK:
                downloadingDataType = "fBank";
                break;

            case FDDBNOTE:
                downloadingDataType = "fDDbNotes";
                break;
//
            case FEXPENSE:
                downloadingDataType = "fExpense";
                break;
//
//            case FTOWN:
//                downloadingDataType = "fTown";
//                break;
//
//            case FMERCH:
//                downloadingDataType = "FMerch";
//                break;
//
            case FROUTEDET:
                downloadingDataType = "fRouteDet";
                break;
//
//            case FTRGCAPUL:
//                downloadingDataType = "FTrgCapUL";
//                break;
//
//            case FTYPE:
//                downloadingDataType = "fType";
//                break;
//
//            case FSUBBRAND:
//                downloadingDataType = "fSubBrand";
//                break;
//
//            case FGROUP:
//                downloadingDataType = "fGroup";
//                break;
//
//            case FSKU:
//                downloadingDataType = "FSKU";
//                break;
//
//            case FBRAND:
//                downloadingDataType = "fbrand";
//                break;
//
//            case FDEALER:
//                downloadingDataType = "fDealer";
//                break;
//
//            case FDISCDEB:
//                downloadingDataType = "Fdiscdeb";
//                break;
//
//            case FDISCDET:
//                downloadingDataType = "Fdiscdet";
//                break;
//
//            case FDISCSLAB:
//                downloadingDataType = "Fdiscslab";
//                break;
//
//            case FDISCHED:
//                downloadingDataType = "FDisched";
//                break;
//
//
//            case FDISCVHED:
//                downloadingDataType = "FDiscvhed";
//                break;
//
//            case FDISCVDET:
//                downloadingDataType = "FDiscvdet";
//                break;
//            case FDISCVDEB:
//                downloadingDataType = "FDiscvdeb";
//                break;
//
//            case FDSCHHED:
//                downloadingDataType = "fDSchHed";
//                break;
//            case FDSCHDET:
//                downloadingDataType = "fDSchDet";
//                break;
//            case FSIZECOMB:
//                downloadingDataType = "fSizeComb";
//                break;
//            case FSIZEIN:
//                downloadingDataType = "fSizeIn";
//                break;
//            case FCRCOMB:
//                downloadingDataType = "fCrComb";
//                break;
//
//            case FTERMS:
//                downloadingDataType = "fTerms";
//                break;
//            case FTAX:
//                downloadingDataType = "fTax";
//                break;
            case FTAXHED:
                downloadingDataType = "fTaxHed";
                break;
            case FTAXDET:
                downloadingDataType = "fTaxDet";
                break;
//
            case FSTKIN:
                downloadingDataType = "fStkIn";
                break;


//            case FDISTRICT:
//                downloadingDataType = "fDistrict";
//                break;

            default:
                break;
        }
    }

    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        // progressDialog.setTitle("Downloading...");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        String resultStr = "";

        int recordCount = 0;
        publishProgress(recordCount);

        try {

            URL json = new URL(ConnectionURL + downLoadURL);

            URLConnection jc = json.openConnection();
            BufferedReader readerfdblist = new BufferedReader(new InputStreamReader(jc.getInputStream()));

            String line = readerfdblist.readLine();
            resultStr = line;
            JSONObject jsonResponse = new JSONObject(line);

            switch (taskType) {
                case DATABASENAME:
                    // your work here
                    break;

                case FITEMLOC: {
                    JSONArray jsonArray = jsonResponse.getJSONArray("fItemLocResult");
                    ArrayList<ItemLoc> list = new ArrayList<ItemLoc>();
                    totalRecords = jsonArray.length();

                    new ItemLocDS(context).deleteAllItemLoc();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = (JSONObject) jsonArray.get(i);
                        ItemLoc loc = new ItemLoc();

                        loc.setFITEMLOC_ITEM_CODE(jObject.getString("ItemCode"));
                        loc.setFITEMLOC_LOC_CODE(jObject.getString("LocCode"));
                        loc.setFITEMLOC_QOH(jObject.getString("QOH"));
                        loc.setFITEMLOC_RECORD_ID(jObject.getString("RecordId"));

                        list.add(loc);
                        ++recordCount;
                        publishProgress(recordCount);
                    }
                    new ItemLocDS(context).InsertOrReplaceItemLoc(list);
                }

                break;

                case FITEMPRI: {
                    ArrayList<ItemPri> list = new ArrayList<ItemPri>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("fItemPriResult");
                    Log.v(TAG, "Array Length ItemPri DB :" + jsonArray.length());
                    totalRecords = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        ItemPri pri = new ItemPri();
                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        pri.setFITEMPRI_ADD_MACH(jObject.getString("AddMach"));
                        pri.setFITEMPRI_ADD_USER(jObject.getString("AddUser"));
                        pri.setFITEMPRI_ITEM_CODE(jObject.getString("ItemCode"));
                        pri.setFITEMPRI_PRICE(jObject.getString("Price"));
                        pri.setFITEMPRI_PRIL_CODE(jObject.getString("PrilCode"));
                        pri.setFITEMPRI_TXN_MACH(jObject.getString("TxnMach"));
                        pri.setFITEMPRI_TXN_USER(jObject.getString("Txnuser"));
                        pri.setFITEMPRI_MAX_PRICE(jObject.getString("MaxPrice"));
                        pri.setFITEMPRI_MIN_PRICE(jObject.getString("MiniPrice"));

                        list.add(pri);
                        ++recordCount;
                        publishProgress(recordCount);

                    }
                    new ItemPriDS(context).InsertOrReplaceItemPri(list);
                }
                break;
//                case FITENRDET:
//                    // downloadingDataType = "FItenrDet";
//                {
//                    ArrayList<FItenrDet> list = new ArrayList<FItenrDet>();
//
//                    JSONArray jsonArray = jsonResponse.getJSONArray("fItenrDetResult");
//
//                    Log.v(TAG, "Array Length FItenrDet DB :" + jsonArray.length());
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//
//                        FItenrDet fItenrDet = new FItenrDet();
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//                        fItenrDet.setFITENRDET_NO_OUTLET(jObject.getString("NoOutlet"));
//                        fItenrDet.setFITENRDET_NO_SHCUCAL(jObject.getString("NoShcuCal"));
//                        fItenrDet.setFITENRDET_RD_TARGET(jObject.getString("RDTarget"));
//                        fItenrDet.setFITENRDET_REF_NO(jObject.getString("RefNo"));
//                        fItenrDet.setFITENRDET_REMARKS(jObject.getString("Remarks"));
//                        fItenrDet.setFITENRDET_ROUTE_CODE(jObject.getString("RouteCode"));
//                        fItenrDet.setFITENRDET_TXN_DATE(jObject.getString("TxnDate"));
//
//                        list.add(fItenrDet);
//                        ++recordCount;
//                        publishProgress(recordCount);
//                    }
//                    new FItenrDetDS(context).createOrUpdateFItenrDet(list);
//                }
//                break;

//                case FITENRHED:
//                    // downloadingDataType = "fItenrHed";
//                {
//                    ArrayList<FItenrHed> list = new ArrayList<FItenrHed>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("fItenrHedResult");
//                    Log.v(TAG, "Array Length FItenrHed DB :" + jsonArray.length());
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//
//                        FItenrHed fItenrHed = new FItenrHed();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//                        fItenrHed.setFITENRHED_COST_CODE(jObject.getString("CostCode"));
//                        fItenrHed.setFITENRHED_DEAL_CODE(jObject.getString("DealCode"));
//                        fItenrHed.setFITENRHED_MONTH(jObject.getString("Month"));
//                        fItenrHed.setFITENRHED_REF_NO(jObject.getString("RefNo"));
//                        fItenrHed.setFITENRHED_REMARKS1(jObject.getString("Remarks1"));
//                        fItenrHed.setFITENRHED_REP_CODE(jObject.getString("RepCode"));
//                        fItenrHed.setFITENRHED_YEAR(jObject.getString("Year"));
//                        list.add(fItenrHed);
//                        ++recordCount;
//                        publishProgress(recordCount);
//                    }
//                    new FItenrHedDS(context).createOrUpdateFItenrHed(list);
//                }
//                break;

//                case FITEDEBDET:
//                    // downloadingDataType = "fIteDebDet";
//                {
//                    ArrayList<FIteDebDet> list = new ArrayList<FIteDebDet>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("fIteDebDetResult");
//                    Log.v(TAG, "Array Length FIteDebDet DB :" + jsonArray.length());
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//
//                        FIteDebDet fIteDebDet = new FIteDebDet();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        fIteDebDet.setFITEDEBDET_DEB_CODE(jObject.getString("DebCode"));
//                        fIteDebDet.setFITEDEBDET_REF_NO(jObject.getString("RefNo"));
//                        fIteDebDet.setFITEDEBDET_ROUTE_CODE(jObject.getString("RouteCode"));
//                        fIteDebDet.setFITEDEBDET_TXN_DATE(jObject.getString("TxnDate"));
//
//                        list.add(fIteDebDet);
//                        ++recordCount;
//                        publishProgress(recordCount);
//                    }
//                    new FIteDebDetDS(context).createOrUpdateFIteDebDet(list);
//                }
//                break;



                case FITEMS:
                    // downloadingDataType ="fItems";
                {
                    ArrayList<Items> list = new ArrayList<Items>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("fItemsResult");
                    Log.v(TAG, "Array Length Items DB :" + jsonArray.length());
                    totalRecords = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        Items itm = new Items();
                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        itm.setFITEM_AVGPRICE(jObject.getString("AvgPrice"));
                        itm.setFITEM_BRANDCODE(jObject.getString("BrandCode"));
                        itm.setFITEM_GROUPCODE(jObject.getString("GroupCode"));
                        itm.setFITEM_ITEM_CODE(jObject.getString("ItemCode"));
                        itm.setFITEM_ITEM_NAME(jObject.getString("ItemName"));
                        itm.setFITEM_ITEMSTATUS(jObject.getString("ItemStatus"));
                        itm.setFITEM_PRILCODE(jObject.getString("PrilCode"));
                        itm.setFITEM_TYPECODE(jObject.getString("TypeCode"));
                        itm.setFITEM_UNITCODE(jObject.getString("UnitCode"));
                        itm.setFITEM_VENPCODE(jObject.getString("VenPcode"));
                        //itm.setFITEM_NOUCASE(jObject.getString("NOUCase"));
                        itm.setFITEM_NOUCASE("");
                        itm.setFITEM_REORDER_LVL(jObject.getString("ReOrderLvl"));
                        itm.setFITEM_REORDER_QTY(jObject.getString("ReOrderQty"));
                        itm.setFITEM_TAXCOMCODE(jObject.getString("TaxComCode"));
//                        itm.setFITEM_MARKUP(jObject.getString("Markup"));
//                        itm.setFITEM_MARKUP_PER(jObject.getString("MarkupPer"));
//                        itm.setFITEM_IS_CHANGE_PRICE(jObject.getString("ChangePrice"));
//                        itm.setFITEM_LGRNPRICE(jObject.getString("LGrnPrice"));

                        list.add(itm);
                        ++recordCount;
                        publishProgress(recordCount);
                    }

                    new ItemsDS(context).InsertOrReplaceItems(list);

                }
                break;

                case FDEBTOR: {
                    ArrayList<Debtor> list = new ArrayList<Debtor>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("FdebtorResult");
                    Log.v(TAG, "Array Length Debtor DB :" + jsonArray.length());
                    totalRecords = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        Debtor aDebtor = new Debtor();

                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        aDebtor.setFDEBTOR_ADD1(jObject.getString("DebAdd1"));
                        aDebtor.setFDEBTOR_ADD2(jObject.getString("DebAdd2"));
                        aDebtor.setFDEBTOR_ADD3(jObject.getString("DebAdd3"));
                        aDebtor.setFDEBTOR_AREA_CODE(jObject.getString("AreaCode"));
                        aDebtor.setFDEBTOR_CHK_CRD_LIMIT(jObject.getString("ChkCrdLmt"));
                        aDebtor.setFDEBTOR_CHK_CRD_PRD(jObject.getString("ChkCrdPrd"));
                        aDebtor.setFDEBTOR_CODE(jObject.getString("DebCode"));
                        aDebtor.setFDEBTOR_CRD_LIMIT(jObject.getString("CrdLimit"));
                        aDebtor.setFDEBTOR_CRD_PERIOD(jObject.getString("CrdPeriod"));
                        aDebtor.setFDEBTOR_DBGR_CODE(jObject.getString("DbGrCode"));
                        aDebtor.setFDEBTOR_EMAIL(jObject.getString("DebEMail"));
                        aDebtor.setFDEBTOR_MOB(jObject.getString("DebMob"));
                        aDebtor.setFDEBTOR_NAME(jObject.getString("DebName"));
                        aDebtor.setFDEBTOR_PRILLCODE(jObject.getString("PrilCode"));
                        aDebtor.setFDEBTOR_RANK_CODE(jObject.getString("RankCode"));
                        aDebtor.setFDEBTOR_STATUS(jObject.getString("Status"));
                        aDebtor.setFDEBTOR_TAX_REG(jObject.getString("TaxReg"));
                        aDebtor.setFDEBTOR_TELE(jObject.getString("DebTele"));
                        aDebtor.setFDEBTOR_TOWN_CODE(jObject.getString("TownCode"));
                        aDebtor.setFDEBTOR_REPCODE(jObject.getString("RepCode"));
                        aDebtor.setFDEBTOR_CREDITDISCOUNT(jObject.getString("CreditDiscount"));

                        list.add(aDebtor);
                        ++recordCount;
                        publishProgress(recordCount);

                    }
                /* Update to database */
                    new DebtorDS(context).InsertOrReplaceDebtor(list);

                }
                break;

                case FCONTROL: {
                    ArrayList<Control> list = new ArrayList<Control>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("fControlResult");
                    Log.v(TAG, "Array Length Control DB :" + jsonArray.length());
                    totalRecords = jsonArray.length();
                    String Type = "0";
                    for (int i = 0; i < jsonArray.length(); i++) {

                        Control ctrl = new Control();

                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        ctrl.setFCONTROL_COM_ADD1(jObject.getString("ComAdd1"));
                        ctrl.setFCONTROL_COM_ADD2(jObject.getString("ComAdd2"));
                        ctrl.setFCONTROL_COM_ADD3(jObject.getString("ComAdd3"));
                        ctrl.setFCONTROL_COM_NAME(jObject.getString("ComName"));
                        ctrl.setFCONTROL_BASECUR(jObject.getString("basecur"));
                        ctrl.setFCONTROL_COM_EMAIL(jObject.getString("comemail"));
                        ctrl.setFCONTROL_COM_TEL1(jObject.getString("comtel1"));
                        ctrl.setFCONTROL_COM_TEL2(jObject.getString("comtel2"));
                        ctrl.setFCONTROL_COM_FAX(jObject.getString("comfax1"));
                        ctrl.setFCONTROL_COM_WEB(jObject.getString("comweb"));
                        ctrl.setFCONTROL_CONAGE1(jObject.getString("conage1"));
                        ctrl.setFCONTROL_CONAGE2(jObject.getString("conage2"));
                        ctrl.setFCONTROL_CONAGE3(jObject.getString("conage3"));
                        ctrl.setFCONTROL_CONAGE4(jObject.getString("conage4"));
                        ctrl.setFCONTROL_CONAGE5(jObject.getString("conage5"));
                        ctrl.setFCONTROL_CONAGE6(jObject.getString("conage6"));
                        ctrl.setFCONTROL_CONAGE7(jObject.getString("conage7"));
                        ctrl.setFCONTROL_CONAGE8(jObject.getString("conage8"));
                        ctrl.setFCONTROL_CONAGE9(jObject.getString("conage9"));
                        ctrl.setFCONTROL_CONAGE10(jObject.getString("conage10"));
                        ctrl.setFCONTROL_CONAGE11(jObject.getString("conage11"));
                        ctrl.setFCONTROL_CONAGE12(jObject.getString("conage12"));
                        ctrl.setFCONTROL_CONAGE13(jObject.getString("conage13"));
                        ctrl.setFCONTROL_CONAGE14(jObject.getString("conage14"));
                        ctrl.setFCONTROL_COM_REGNO(jObject.getString("comRegNo"));
                        ctrl.setFCONTROL_SYSTYPE("");
                       // ctrl.setFCONTROL_COMDISPER(jObject.getString("DisPer"));
                        // ctrl.setFCONTROL_VATCMTAXNO(jObject.getString("vatcmtaxno"));

                        // Type = jObject.getString("SysType");

                        list.add(ctrl);

                        ++recordCount;
                        publishProgress(recordCount);
                    }

                    ControlDS ds = new ControlDS(context);
                    if (ds.createOrUpdateFControl(list) > 0) {

                        Log.v("createOrUpdateControl", "Result : Control Data Inserted successfully");

                        return "" + Type;
                    }
                }
                break;

                case FCOMPANYSETTING: {

                    ArrayList<CompanySetting> list = new ArrayList<CompanySetting>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("fCompanySettingResult");
                    Log.v(TAG, "Array Length CompanySetting DB :" + jsonArray.length());
                    totalRecords = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        CompanySetting setting = new CompanySetting();

                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        setting.setFCOMPANYSETTING_CHAR_VAL(jObject.getString("cCharVal"));
                        setting.setFCOMPANYSETTING_COMPANY_CODE(jObject.getString("cCompanyCode"));
                        setting.setFCOMPANYSETTING_LOCATION_CHAR(jObject.getString("cLocationChar"));
                        setting.setFCOMPANYSETTING_REMARKS(jObject.getString("cRemarks"));
                        setting.setFCOMPANYSETTING_GRP(jObject.getString("cSettingGrp"));
                        setting.setFCOMPANYSETTING_SETTINGS_CODE(jObject.getString("cSettingsCode"));
                        setting.setFCOMPANYSETTING_NUM_VAL(jObject.getString("nNumVal"));
                        setting.setFCOMPANYSETTING_TYPE(jObject.getString("nType"));

                        list.add(setting);
                        ++recordCount;
                        publishProgress(recordCount);

                    }
                    new CompanySettingDS(context).createOrUpdateFCompanySetting(list);
                }

                break;

                case FAREA: {
                    ArrayList<Area> list = new ArrayList<Area>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("fAreaResult");
                    Log.v(TAG, "Array Length Area DB :" + jsonArray.length());

                    totalRecords = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Area area = new Area();

                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        area.setFAREA_ADD_MACH(jObject.getString("AddMach"));
                        area.setFAREA_ADD_USER(jObject.getString("AddUser"));
                        area.setFAREA_AREA_CODE(jObject.getString("AreaCode"));
                        area.setFAREA_AREA_NAME(jObject.getString("AreaName"));
                        area.setFAREA_DEAL_CODE(jObject.getString("DealCode"));
                        area.setFAREA_REQ_CODE(jObject.getString("RegCode"));

                        list.add(area);
                        ++recordCount;
                        publishProgress(recordCount);
                    }
                    new AreaDS(context).createOrUpdateArea(list);

                }
                break;

                case FLOCATIONS: {
                    ArrayList<Locations> list = new ArrayList<Locations>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("fLocationsResult");
                    Log.v(TAG, "Array Length fLocations DB :" + jsonArray.length());

                    totalRecords = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Locations locations = new Locations();

                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        locations.setFLOCATIONS_ADD_MACH(jObject.getString("AddMach"));
                        locations.setFLOCATIONS_ADD_USER(jObject.getString("AddUser"));
                        locations.setFLOCATIONS_LOC_CODE(jObject.getString("LocCode"));
                        locations.setFLOCATIONS_LOC_NAME(jObject.getString("LocName"));
                        locations.setFLOCATIONS_LOC_T_CODE(jObject.getString("LoctCode"));
                        locations.setFLOCATIONS_REP_CODE(jObject.getString("RepCode"));

                        list.add(locations);
                        ++recordCount;
                        publishProgress(recordCount);
                    }
                    new LocationsDS(context).createOrUpdateFLocations(list);
                }
                break;

                case FCOMPANYBRANCH: {
                    ArrayList<CompanyBranch> list = new ArrayList<CompanyBranch>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("FCompanyBranchResult");
                    Log.v(TAG, "Array Length CompanyBranch DB :" + jsonArray.length());

                    totalRecords = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        CompanyBranch branch = new CompanyBranch();

                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        branch.setFCOMPANYBRANCH_BRANCH_CODE(jObject.getString("BranchCode"));
                        branch.setFCOMPANYBRANCH_CSETTINGS_CODE(jObject.getString("cSettingsCode"));
                        branch.setFCOMPANYBRANCH_NNUM_VAL(jObject.getString("nNumVal"));
                        branch.setNYEAR(jObject.getString("nYear"));
                        branch.setNMONTH(jObject.getString("nMonth"));

                        list.add(branch);

                        ++recordCount;
                        publishProgress(recordCount);

                    }
                    new CompanyBranchDS(context).createOrUpdateFCompanyBranch(list);
                }
                break;

                case FSALREP: {
                    ArrayList<SalRep> list = new ArrayList<SalRep>();
                    JSONArray jsonArrayfSalRep = jsonResponse.getJSONArray("fSalRepResult");
                    Log.v(TAG, "Array Length fSalRep :" + jsonArrayfSalRep.length());
                    totalRecords = jsonArrayfSalRep.length();

                    for (int i = 0; i < jsonArrayfSalRep.length(); i++) {

                        SalRep rep = new SalRep();

                        JSONObject jObject = (JSONObject) jsonArrayfSalRep.get(i);

                        rep.setADDMACH(jObject.getString("AddMach"));
                        rep.setADDUSER(jObject.getString("AddUser"));
                        rep.setEMAIL(jObject.getString("RepEMail"));
                        rep.setMACID(jObject.getString("macid"));
                        rep.setMOBILE(jObject.getString("RepMobil"));
                        rep.setNAME(jObject.getString("RepName"));
                        rep.setPASSWORD(jObject.getString("Password"));
                        rep.setPREFIX(jObject.getString("RepPrefix"));
                        rep.setRECORDID(jObject.getString("RecordId"));
                        rep.setREPCODE(jObject.getString("RepCode"));
                        rep.setREPID(jObject.getString("RepIdNo"));
                        rep.setSTATUS(jObject.getString("Status"));
                        rep.setTELE(jObject.getString("RepTele"));
                        rep.setLOCCODE(jObject.getString("LocCode"));
                        rep.setPrilCode(jObject.getString("PriLCode"));


                        list.add(rep);
                        ++recordCount;
                        publishProgress(recordCount);
                    }

                    new SalRepDS(context).createOrUpdateSalRep(list);
				/*
				 * return salrep array length to check if MAC is registered to
				 * SettingsActivity OnTaskCompleted
				 */
                    return String.valueOf(jsonArrayfSalRep.length());

                }
                case FREASON: {
                    ArrayList<Reason> list = new ArrayList<Reason>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("fReasonResult");
                    Log.v(TAG, "Array Length Reason DB :" + jsonArray.length());

                    totalRecords = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Reason reason = new Reason();

                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        reason.setFREASON_ADD_DATE(jObject.getString("AddDate"));
                        reason.setFREASON_ADD_MACH(jObject.getString("AddMach"));
                        reason.setFREASON_ADD_USER(jObject.getString("AddUser"));
                        reason.setFREASON_CODE(jObject.getString("ReaCode"));
                        reason.setFREASON_NAME(jObject.getString("ReaName"));
                        reason.setFREASON_REATCODE(jObject.getString("ReaTcode"));

                        list.add(reason);

                        ++recordCount;
                        publishProgress(recordCount);

                    }

                    new ReasonDS(context).createOrUpdateFReason(list);

                }
                break;

                case FROUTE:

                {
                    ArrayList<Route> list = new ArrayList<Route>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("fRouteResult");
                    Log.v(TAG, "Array Length Route DB :" + jsonArray.length());

                    totalRecords = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Route route = new Route();

                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        route.setFROUTE_ADDDATE(jObject.getString("AddDate"));
                        route.setFROUTE_ADD_MACH(jObject.getString("AddMach"));
                        route.setFROUTE_ADD_USER(jObject.getString("AddUser"));
                        route.setFROUTE_AREACODE(jObject.getString("AreaCode"));
                        // route.setFROUTE_DEALCODE(jObject.getString("DealCode"));
                        route.setFROUTE_FREQNO(jObject.getString("FreqNo"));
                        route.setFROUTE_KM(jObject.getString("Km"));
                        route.setFROUTE_MINPROCALL(jObject.getString("MinProcall"));
                        route.setFROUTE_RDALORATE(jObject.getString("RDAloRate"));
                        route.setFROUTE_RDTARGET(jObject.getString("RDTarget"));
                        route.setFROUTE_REMARKS(jObject.getString("Remarks"));
                        route.setFROUTE_REPCODE(jObject.getString("RepCode"));
                        route.setFROUTE_ROUTECODE(jObject.getString("RouteCode"));
                        route.setFROUTE_ROUTE_NAME(jObject.getString("RouteName"));
                        route.setFROUTE_STATUS(jObject.getString("Status"));
                        route.setFROUTE_TONNAGE(jObject.getString("Tonnage"));

                        list.add(route);

                        ++recordCount;
                        publishProgress(recordCount);

                    }
                    if (new RouteDS(context).createOrUpdateFRoute(list) > 0) {
                        Log.v("CreateOrUpdateFRoute", "Result : Route Data Inserted successfully");
                    }
                }
                break;

                case FBANK:
                    // downloadingDataType="fBank";
                {
                    ArrayList<Bank> list = new ArrayList<Bank>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("fbankResult");
                    Log.v(TAG, "Array Length Bank DB :" + jsonArray.length());

                    totalRecords = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Bank bank = new Bank();

                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        bank.setFBANK_BANK_CODE(jObject.getString("Bankcode"));
                        bank.setFBANK_BANK_NAME(jObject.getString("Bankname"));
                        bank.setFBANK_BANK_ACC_NO(jObject.getString("Bankaccno"));
                        bank.setFBANK_BRANCH(jObject.getString("Branch"));
                        bank.setFBANK_ADD1(jObject.getString("Bankadd1"));
                        bank.setFBANK_ADD2(jObject.getString("Bankadd2"));
                        bank.setFBANK_ADD_DATE(jObject.getString("AddDate"));
                        bank.setFBANK_ADD_MACH(jObject.getString("AddMach"));
                        bank.setFBANK_ADD_USER(jObject.getString("AddUser"));

                        list.add(bank);

                        ++recordCount;
                        publishProgress(recordCount);

                    }
                    if (new BankDS(context).createOrUpdateBank(list) > 0) {
                        Log.v("CreateOrUpdateFbank", "Result : bank Data Inserted successfully");
                    }
                }

                break;

                case FDDBNOTE:
                    // downloadingDataType="fDdbNote";
                {
                    ArrayList<FDDbNote> list = new ArrayList<FDDbNote>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("fDdbNoteWithConditionResult");
                    Log.v(TAG, "Array Length fDdbNote DB :" + jsonArray.length());

                    totalRecords = jsonArray.length();
                    new FDDbNoteDS(context).deleteAll();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        FDDbNote fdDbNote = new FDDbNote();

                        JSONObject jObject = (JSONObject) jsonArray.get(i);
                        BigDecimal totbal = new BigDecimal(jObject.getString("TotBal"));
                        BigDecimal pdaamt = new BigDecimal(jObject.getString("PdaAmt"));
                        BigDecimal roundOff_totbal = totbal.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                        BigDecimal roundOff_pdaamt = pdaamt.setScale(2, BigDecimal.ROUND_HALF_EVEN);

                        fdDbNote.setFDDBNOTE_ADD_DATE(jObject.getString("AddDate"));
                        fdDbNote.setFDDBNOTE_ADD_MACH(jObject.getString("AddMach"));
                        fdDbNote.setFDDBNOTE_ADD_USER(jObject.getString("AddUser"));
                        fdDbNote.setFDDBNOTE_AMT(jObject.getString("Amt"));
                        fdDbNote.setFDDBNOTE_B_AMT(jObject.getString("BAmt"));
                        fdDbNote.setFDDBNOTE_B_TAX_AMT(jObject.getString("BTaxAmt"));
                        fdDbNote.setFDDBNOTE_CUR_CODE(jObject.getString("CurCode"));
                        fdDbNote.setFDDBNOTE_CUR_RATE(jObject.getString("CurRate"));
                        fdDbNote.setFDDBNOTE_DEB_CODE(jObject.getString("DebCode"));
                        fdDbNote.setFDDBNOTE_MANU_REF(jObject.getString("ManuRef"));
                        fdDbNote.setFDDBNOTE_OV_PAY_AMT(jObject.getString("OvPayAmt"));
                        fdDbNote.setFDDBNOTE_REF_INV(jObject.getString("RefInv"));
                        fdDbNote.setFDDBNOTE_REFNO(jObject.getString("RefNo"));
                        fdDbNote.setFDDBNOTE_REFNO1(jObject.getString("RefNo1"));
                        fdDbNote.setFDDBNOTE_REMARKS(jObject.getString("Remarks"));
                        fdDbNote.setFDDBNOTE_REP_CODE(jObject.getString("RepCode"));
                        fdDbNote.setFDDBNOTE_SALE_REF_NO(jObject.getString("SaleRefNo"));
                        fdDbNote.setFDDBNOTE_TAX_AMT(jObject.getString("TaxAmt"));
                        fdDbNote.setFDDBNOTE_TAX_COM_CODE(jObject.getString("TaxComCode"));
                        fdDbNote.setFDDBNOTE_TOT_BAL(String.valueOf((roundOff_totbal.subtract(roundOff_pdaamt))));
                        fdDbNote.setFDDBNOTE_TOT_BAL1(jObject.getString("TotBal1"));
                        fdDbNote.setFDDBNOTE_TXN_DATE(jObject.getString("TxnDate"));
                        fdDbNote.setFDDBNOTE_TXN_TYPE(jObject.getString("TxnType"));

                        list.add(fdDbNote);

                        ++recordCount;
                        publishProgress(recordCount);

                    }
                    if (new FDDbNoteDS(context).createOrUpdateFDDbNote(list) > 0) {
                        Log.v("createOrUpdateFDDbNote", "Result : FDDBnote Inserted successfully");
                    }
                }
                break;

                case FEXPENSE:
                    // downloadingDataType="fExpense";
                {
                    ArrayList<Expense> list = new ArrayList<Expense>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("fExpenseResult");
                    Log.v(TAG, "Array Length Expense DB :" + jsonArray.length());

                    totalRecords = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Expense expense = new Expense();

                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        expense.setFEXPENSE_ADD_DATE(jObject.getString("AddDate"));
                        expense.setFEXPENSE_ADD_MACH(jObject.getString("AddMach"));
                        expense.setFEXPENSE_ADD_USER(jObject.getString("AddUser"));
                        expense.setFEXPENSE_CODE(jObject.getString("ExpCode"));
                        // expense.setFEXPENSE_GRP_CODE(jObject.getString("ExpGrpCode"));
                        expense.setFEXPENSE_NAME(jObject.getString("ExpName"));
                        expense.setFEXPENSE_STATUS(jObject.getString("Status"));

                        list.add(expense);

                        ++recordCount;
                        publishProgress(recordCount);

                    }

                    if (new ExpenseDS(context).createOrUpdateFExpense(list) > 0) {
                        Log.v("CreateOrUpdateFexpense", "Result : expense Data Inserted successfully");
                    }
                }
                break;
//
//                case FTOWN:
//                    // downloadingDataType="fTown";
//                {
//                    ArrayList<Town> list = new ArrayList<Town>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("fTownResult");
//                    Log.v(TAG, "Array Length Route DB :" + jsonArray.length());
//
//                    new TownDS(context).deleteAll();
//
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        Town town = new Town();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        town.setFTOWN_ADDDATE(jObject.getString("AddDate"));
//                        town.setFTOWN_ADD_MACH(jObject.getString("AddMach"));
//                        town.setFTOWN_ADD_USER(jObject.getString("AddUser"));
//                        town.setFTOWN_DISTR_CODE(jObject.getString("DistrCode"));
//                        town.setFTOWN_CODE(jObject.getString("TownCode"));
//                        town.setFTOWN_NAME(jObject.getString("TownName"));
//
//                        list.add(town);
//
//                        ++recordCount;
//                        publishProgress(recordCount);
//
//                    }
//                    if (new TownDS(context).createOrUpdateTown(list) > 0) {
//                        Log.v("CreateOrUpdateFRoute", "Result : Route Data Inserted successfully");
//                    }
//                }
//                break;

//                case FMERCH:
//                    // downloadingDataType="FMerch";
//                {
//                    ArrayList<Merch> list = new ArrayList<Merch>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("FMerchResult");
//                    Log.v(TAG, "Array Length Merch DB :" + jsonArray.length());
//
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        Merch merch = new Merch();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        merch.setFMERCH_CODE(jObject.getString("MerchCode"));
//                        merch.setFMERCH_NAME(jObject.getString("MerchName"));
//                        merch.setFMERCH_ADD_USER(jObject.getString("AddUser"));
//                        merch.setFMERCH_ADD_DATE(jObject.getString("AddDate"));
//                        merch.setFMERCH_ADD_MACH(jObject.getString("AddMach"));
//                        merch.setFMERCH_TIMESTAMP_COLUMN(jObject.getString("timestamp_column"));
//                        list.add(merch);
//
//                        ++recordCount;
//                        publishProgress(recordCount);
//
//                    }
//                    if (new MerchDS(context).createOrUpdateFMerch(list) > 0) {
//                        Log.v("CreateOrUpdateFmerch", "Result : merch Data Inserted successfully");
//                    }
//                }
//                break;
//
                case FROUTEDET:
                    // downloadingDataType ="fRouteDet";
                {
                    ArrayList<RouteDet> list = new ArrayList<RouteDet>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("fRouteDetResult");
                    Log.v(TAG, "Array Length RouteDet DB :" + jsonArray.length());

                    totalRecords = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        RouteDet routeDet = new RouteDet();

                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        routeDet.setFROUTEDET_DEB_CODE(jObject.getString("DebCode"));
                        routeDet.setFROUTEDET_ROUTE_CODE(jObject.getString("RouteCode"));
                        list.add(routeDet);

                        ++recordCount;
                        publishProgress(recordCount);

                    }
                    if (new RouteDetDS(context).InsertOrReplaceRouteDet(list) > 0) {
                        Log.v("CreateOrUpdatefRouteDet", "Result : Route Data Inserted successfully");
                    }
                }
                break;
//
//                case FTRGCAPUL:
//                    downloadingDataType = "FTrgCapUL";
//                    break;
//
//                case FTYPE: {
//                    ArrayList<Type> list = new ArrayList<Type>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("fTypeResult");
//                    Log.v(TAG, "Array Length Type DB :" + jsonArray.length());
//
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        Type type = new Type();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        type.setFTYPE_ADD_DATE(jObject.getString("AddDate"));
//                        type.setFTYPE_ADD_MACH(jObject.getString("AddMach"));
//                        type.setFTYPE_ADD_USER(jObject.getString("AddUser"));
//                        type.setFTYPE_CODE(jObject.getString("TypeCode"));
//                        type.setFTYPE_NAME(jObject.getString("TypeName"));
//                        list.add(type);
//
//                        ++recordCount;
//                        publishProgress(recordCount);
//
//                    }
//                    if (new TypeDS(context).createOrUpdateType(list) > 0) {
//                        Log.v("CreateOrUpdateFRoute", "Result : Route Data Inserted successfully");
//                    }
//                }
//                break;
//
//                case FSUBBRAND:
//                    // downloadingDataType="fSubBrand";
//                {
//                    ArrayList<SubBrand> list = new ArrayList<SubBrand>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("fSubBrandResult");
//                    Log.v(TAG, "Array Length SubBrand DB :" + jsonArray.length());
//
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        SubBrand subBrand = new SubBrand();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        subBrand.setFSUBBRAND_ADD_DATE(jObject.getString("AddDate"));
//                        subBrand.setFSUBBRAND_ADD_MACH(jObject.getString("AddMach"));
//                        subBrand.setFSUBBRAND_ADD_USER(jObject.getString("AddUser"));
//                        subBrand.setFSUBBRAND_CODE(jObject.getString("SBrandCode"));
//                        subBrand.setFSUBBRAND_NAME(jObject.getString("SBrandName"));
//
//                        list.add(subBrand);
//
//                        ++recordCount;
//                        publishProgress(recordCount);
//
//                    }
//                    if (new SubBrandDS(context).createOrUpdateSubBrand(list) > 0) {
//                        Log.v("CreateOrUpdateFsubBrand", "Result : subBrand Data Inserted successfully");
//                    }
//                }
//                break;
//
//                case FGROUP:
//                    // downloadingDataType="fGroup";
//                {
//                    ArrayList<Group> list = new ArrayList<Group>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("fGroupResult");
//                    Log.v(TAG, "Array Length Group DB :" + jsonArray.length());
//
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        Group group = new Group();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        group.setFGROUP_ADD_DATE(jObject.getString("AddDate"));
//                        group.setFGROUP_ADD_MACH(jObject.getString("AddMach"));
//                        group.setFGROUP_ADD_USER(jObject.getString("AddUser"));
//                        group.setFGROUP_CODE(jObject.getString("GroupCode"));
//                        group.setFGROUP_NAME(jObject.getString("GroupName"));
//
//                        list.add(group);
//
//                        ++recordCount;
//                        publishProgress(recordCount);
//
//                    }
//                    if (new GroupDS(context).createOrUpdateGroup(list) > 0) {
//                        Log.v("createOrUpdateGroup", "Result : group Data Inserted successfully");
//                    }
//                }
//                break;
//
//                case FSKU:
//                    // downloadingDataType="FSKU";
//                {
//                    ArrayList<SKU> list = new ArrayList<SKU>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("FSKUResult");
//                    Log.v(TAG, "Array Length SKU DB :" + jsonArray.length());
//
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        SKU sku = new SKU();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        sku.setFSKU_ADD_DATE(jObject.getString("AddDate"));
//                        sku.setFSKU_ADD_MACH(jObject.getString("AddMach"));
//                        sku.setFSKU_ADD_USER(jObject.getString("AddUser"));
//                        sku.setFSKU_BRAND_CODE(jObject.getString("BrandCode"));
//                        sku.setFSKU_GROUP_CODE(jObject.getString("GroupCode"));
//                        sku.setFSKU_ITEM_STATUS(jObject.getString("ItemStatus"));
//                        sku.setFSKU_MUST_SALE(jObject.getString("MustSale"));
//                        sku.setFSKU_NOUCASE(jObject.getString("NOUCase"));
//                        sku.setFSKU_ORDSEQ(jObject.getString("OrdSeq"));
//                        sku.setFSKU_SUB_BRAND_CODE(jObject.getString("SBrandCode"));
//                        sku.setFSKU_CODE(jObject.getString("SKUCode"));
//                        sku.setFSKU_NAME(jObject.getString("SkuName"));
//                        sku.setFSKU_SIZE_CODE(jObject.getString("SkuSizCode"));
//                        sku.setFSKU_TONNAGE(jObject.getString("Tonnage"));
//                        sku.setFSKU_TYPE_CODE(jObject.getString("TypeCode"));
//                        sku.setFSKU_UNIT(jObject.getString("Unit"));
//
//                        list.add(sku);
//
//                        ++recordCount;
//                        publishProgress(recordCount);
//
//                    }
//                    if (new SkuDS(context).createOrUpdateSku(list) > 0) {
//                        Log.v("createOrUpdateSku", "Result : sku Data Inserted successfully");
//                    }
//                }
//                break;

//                case FBRAND:
//                    // downloadingDataType="fbrand";
//                {
//                    ArrayList<Brand> list = new ArrayList<Brand>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("FbrandResult");
//                    Log.v(TAG, "Array Length Brand DB :" + jsonArray.length());
//
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        Brand brand = new Brand();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        brand.setFBRAND_ADD_MACH(jObject.getString("AddMach"));
//                        brand.setFBRAND_ADD_USER(jObject.getString("AddUser"));
//                        brand.setFBRAND_CODE(jObject.getString("BrandCode"));
//                        brand.setFBRAND_NAME(jObject.getString("BrandName"));
//
//                        list.add(brand);
//
//                        ++recordCount;
//                        publishProgress(recordCount);
//
//                    }
//                    if (new BrandDS(context).createOrUpdateBrand(list) > 0) {
//                        Log.v("createOrUpdateBrand", "Result : brand Data Inserted successfully");
//                    }
//                }
//                break;
//
//                case FDISCDEB: {
//                    ArrayList<Discdeb> list = new ArrayList<Discdeb>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("FdiscdebResult");
//                    Log.v(TAG, "Array Length discdeb DB :" + jsonArray.length());
//
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        Discdeb discdeb = new Discdeb();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        discdeb.setFDISCDEB_REF_NO(jObject.getString("Refno"));
//                        discdeb.setFDISCDEB_DEB_CODE(jObject.getString("Debcode"));
//
//                        list.add(discdeb);
//
//                        ++recordCount;
//                        publishProgress(recordCount);
//                    }
//                    if (new DiscdebDS(context).createOrUpdateDiscdeb(list) > 0) {
//                        Log.v("createOrUpdateDiscdeb", "Result : discdeb Data Inserted successfully");
//                    }
//                }
//                break;
//
//                case FDISCDET:
//                    // downloadingDataType="Fdiscdet";
//                {
//                    ArrayList<Discdet> list = new ArrayList<Discdet>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("FdiscdetResult");
//                    Log.v(TAG, "Array Length Disched DB :" + jsonArray.length());
//
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        Discdet discdet = new Discdet();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        discdet.setFDISCDET_REF_NO(jObject.getString("Refno"));
//                        discdet.setFDISCDET_ITEM_CODE(jObject.getString("Itemcode"));
//                        discdet.setFDISCHED_TIEMSTAMP_COLUMN("");
//
//                        list.add(discdet);
//
//                        ++recordCount;
//                        publishProgress(recordCount);
//
//                    }
//                    if (new DiscdetDS(context).createOrUpdateDiscdet(list) > 0) {
//                        Log.v("createOrUpdateDiscdet", "Result : discdet Data Inserted successfully");
//                    }
//                }
//                break;
//
//                case FDISCSLAB:
//                    // downloadingDataType ="Fdiscslab";
//                {
//                    ArrayList<Discslab> list = new ArrayList<Discslab>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("FdiscslabResult");
//                    Log.v(TAG, "Array Length Discslab DB :" + jsonArray.length());
//
//                    totalRecords = jsonArray.length();
//                    new DiscslabDS(context).deleteAll();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        Discslab discslab = new Discslab();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        discslab.setFDISCSLAB_REF_NO(jObject.getString("Refno"));
//                        discslab.setFDISCSLAB_SEQ_NO("");
//                        discslab.setFDISCSLAB_QTY_F(jObject.getString("Qtyf"));
//                        discslab.setFDISCSLAB_QTY_T(jObject.getString("Qtyt"));
//                        discslab.setFDISCSLAB_DIS_PER(jObject.getString("Disper"));
//                        discslab.setFDISCSLAB_DIS_AMUT(jObject.getString("Disamt"));
//                        discslab.setFDISCSLAB_TIMESTAMP_COLUMN("");
//
//                        list.add(discslab);
//
//                        ++recordCount;
//                        publishProgress(recordCount);
//                    }
//
//                    if (new DiscslabDS(context).createOrUpdateDiscslab(list) > 0) {
//                        Log.v("createOrUpdateDiscslab", "Result : Discslab Data Inserted successfully");
//                    }
//                }
//                break;
//
//                case FDISCHED:
//                    // downloadingDataType ="FDisched";
//                {
//                    ArrayList<Disched> list = new ArrayList<Disched>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("FDischedResult");
//                    Log.v(TAG, "Array Length Disched DB :" + jsonArray.length());
//
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        Disched disched = new Disched();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        disched.setFDISCHED_REF_NO(jObject.getString("Refno"));
//                        disched.setFDISCHED_TXN_DATE(jObject.getString("Txndate"));
//                        disched.setFDISCHED_DISC_DESC(jObject.getString("DiscDesc"));
//                        disched.setFDISCHED_PRIORITY(jObject.getString("Priority"));
//                        disched.setFDISCHED_DIS_TYPE(jObject.getString("DisType"));
//                        disched.setFDISCHED_V_DATE_F(jObject.getString("Vdatef"));
//                        disched.setFDISCHED_V_DATE_T(jObject.getString("Vdatet"));
//                        disched.setFDISCHED_REMARK("");
//                        disched.setFDISCHED_ADD_USER("");
//                        disched.setFDISCHED_ADD_DATE("");
//                        disched.setFDISCHED_ADD_MACH("");
//                        disched.setFDISCHED_RECORD_ID("");
//                        disched.setFDISCHED_TIMESTAMP_COLUMN("");
//
//                        list.add(disched);
//
//                        ++recordCount;
//                        publishProgress(recordCount);
//
//                    }
//                    if (new DischedDS(context).createOrUpdateDisched(list) > 0) {
//                        Log.v("createOrUpdateDisched", "Result : Disched Data Inserted successfully");
//                    }
//
//                }
//                break;
//
//                case FFREEITEM: {
//                    ArrayList<FreeItem> list = new ArrayList<FreeItem>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("fFreeItemResult");
//                    Log.v(TAG, "Array Length FreeItem DB :" + jsonArray.length());
//
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        FreeItem freeItem = new FreeItem();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        freeItem.setFFREEITEM_ITEMCODE(jObject.getString("Itemcode"));
//                        freeItem.setFFREEITEM_REFNO(jObject.getString("Refno"));
//                        list.add(freeItem);
//                        ++recordCount;
//                        publishProgress(recordCount);
//
//                    }
//                    if (new FreeItemDS(context).createOrUpdateFreeItem(list) > 0) {
//                        Log.v("createOrUpdateFreeItem", "Result : FreeItem Data Inserted successfully");
//                    }
//                }
//                break;
//
//                case FFREEMSLAB:
//                    // downloadingDataType="FfreeMslab";
//                {
//                    ArrayList<FreeMslab> list = new ArrayList<FreeMslab>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("fFreeMslabResult");
//                    Log.v(TAG, "Array Length FreeMslab DB :" + jsonArray.length());
//
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        FreeMslab freeMslab = new FreeMslab();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        freeMslab.setFFREEMSLAB_REFNO(jObject.getString("Refno"));
//                        freeMslab.setFFREEMSLAB_QTY_F(jObject.getString("Qtyf"));
//                        freeMslab.setFFREEMSLAB_QTY_T(jObject.getString("Qtyt"));
//                        freeMslab.setFFREEMSLAB_ITEM_QTY(jObject.getString("ItemQty"));
//                        freeMslab.setFFREEMSLAB_FREE_IT_QTY(jObject.getString("FreeItQty"));
//                        freeMslab.setFFREEMSLAB_ADD_USER(jObject.getString("AddUser"));
//                        freeMslab.setFFREEMSLAB_ADD_DATE(jObject.getString("AddDate"));
//                        freeMslab.setFFREEMSLAB_ADD_MACH(jObject.getString("AddMach"));
//                        freeMslab.setFFREEMSLAB_SEQ_NO(jObject.getString("Seqno"));
//
//                        list.add(freeMslab);
//
//                        ++recordCount;
//                        publishProgress(recordCount);
//
//                    }
//                    if (new FreeMslabDS(context).createOrUpdateFreeMslab(list) > 0) {
//                        Log.v("createOrUpdatefreeMslab", "Result : freeMslab Data Inserted successfully");
//                    }
//                }
//                break;
//
//                case FDISCVHED: {
//                    // ArrayList<Discvhed> list = new ArrayList<Discvhed>();
//                    // JSONArray jsonArray =
//                    // jsonResponse.getJSONArray("FDiscvhedResult");
//                    // Log.v(TAG, "Array Length Discvhed DB :" +
//                    // jsonArray.length());
//
//                    // totalRecords = jsonArray.length();
//
//                    // for (int i = 0; i < jsonArray.length(); i++) {
//                    // Discvhed discvhed = new Discvhed();
//                    //
//                    // JSONObject jObject = (JSONObject) jsonArray.get(i);
//                    //
//                    // discvhed.setFDISCVHED_REF_NO(jObject.getString("Refno"));
//                    // discvhed.setFDISCVHED_TXN_DATE(jObject.getString("Txndate"));
//                    // discvhed.setFDISCVHED_DISC_DESC(jObject.getString("DiscDesc"));
//                    // discvhed.setFDISCVHED_PRIORITY(jObject.getString("Priority"));
//                    // discvhed.setFDISCVHED_DIS_TYPE(jObject.getString("DisType"));
//                    // discvhed.setFDISCVHED_V_DATE_F(jObject.getString("Vdatef"));
//                    // discvhed.setFDISCVHED_V_DATE_T(jObject.getString("Vdatet"));
//                    // discvhed.setFDISCVHED_REMARKS(jObject.getString("Remarks"));
//                    //
//                    // list.add(discvhed);
//                    //
//                    // ++recordCount;
//                    // publishProgress(recordCount);
//                    //
//                    // }
//                    // if (new DiscvhedDS(context).createOrUpdateDiscvhed(list) > 0)
//                    // {
//                    // Log.v("createOrUpdateDiscvhed", "Result : Discvhed Data
//                    // Inserted successfully");
//                    // }
//                }
//                break;
//                case FDISCVDET: {
//                    // ArrayList<Discvdet> list = new ArrayList<Discvdet>();
//                    // JSONArray jsonArray =
//                    // jsonResponse.getJSONArray("FdiscvdetResult");
//                    // Log.v(TAG, "Array Length Discvhed DB :" +
//                    // jsonArray.length());
//                    //
//                    // totalRecords = jsonArray.length();
//                    //
//                    // for (int i = 0; i < jsonArray.length(); i++) {
//                    // Discvdet discvdet = new Discvdet();
//                    //
//                    // JSONObject jObject = (JSONObject) jsonArray.get(i);
//                    //
//                    // discvdet.setFDISCVDET_REF_NO(jObject.getString("Refno"));
//                    // discvdet.setFDISCVDET_VALUE_F(jObject.getString("Valuef"));
//                    // discvdet.setFDISCVDET_VALUE_T(jObject.getString("Valuet"));
//                    // discvdet.setFDISCVDET_DISPER(jObject.getString("Disper"));
//                    // discvdet.setFDISCVDET_DIS_AMT(jObject.getString("Disamt"));
//                    //
//                    // list.add(discvdet);
//                    //
//                    // ++recordCount;
//                    // publishProgress(recordCount);
//                    //
//                    // }
//                    // if (new DiscvdetDS(context).createOrUpdateDiscvdet(list) > 0)
//                    // {
//                    // Log.v("createOrUpdateDiscvdet", "Result : Discvdet Data
//                    // Inserted successfully");
//                    // }
//                }
//                break;
//                case FDISCVDEB: {
//                    // ArrayList<Discvdeb> list = new ArrayList<Discvdeb>();
//                    // JSONArray jsonArray =
//                    // jsonResponse.getJSONArray("FDiscvdebResult");
//                    // Log.v(TAG, "Array Length Discvdeb DB :" +
//                    // jsonArray.length());
//                    //
//                    // totalRecords = jsonArray.length();
//                    //
//                    // for (int i = 0; i < jsonArray.length(); i++) {
//                    // Discvdeb discvdeb = new Discvdeb();
//                    //
//                    // JSONObject jObject = (JSONObject) jsonArray.get(i);
//                    //
//                    // discvdeb.setFDISCVDEB_REF_NO(jObject.getString("Refno"));
//                    // discvdeb.setFDISCVDED_DEB_CODE(jObject.getString("Debcode"));
//                    //
//                    // list.add(discvdeb);
//                    //
//                    // ++recordCount;
//                    // publishProgress(recordCount);
//                    //
//                    // }
//                    // if (new DiscvdebDS(context).createOrUpdateDiscvdeb(list) > 0)
//                    // {
//                    // Log.v("createOrUpdateDiscvdeb", "Result : Discvdeb Data
//                    // Inserted successfully");
//                    // }
//                }
//                break;
//                case FINVHEDL3:
//                    // downloadingDataType="FinvHedL3";
//                {
//                    // ArrayList<FinvHedL3> list = new ArrayList<FinvHedL3>();
//                    // JSONArray jsonArray =
//                    // jsonResponse.getJSONArray("RepLastThreeInvHedResult");
//                    // Log.v(TAG, "Array Length FinvHedL3 DB :" +
//                    // jsonArray.length());
//                    //
//                    // totalRecords = jsonArray.length();
//                    //
//                    // for (int i = 0; i < jsonArray.length(); i++) {
//                    // FinvHedL3 finvHedL3 = new FinvHedL3();
//                    //
//                    // JSONObject jObject = (JSONObject) jsonArray.get(i);
//                    //
//                    // finvHedL3.setFINVHEDL3_DEB_CODE(jObject.getString("DebCode"));
//                    // finvHedL3.setFINVHEDL3_REF_NO(jObject.getString("RefNo"));
//                    // finvHedL3.setFINVHEDL3_REF_NO1(jObject.getString("RefNo1"));
//                    // finvHedL3.setFINVHEDL3_TOTAL_AMT(jObject.getString("TotalAmt"));
//                    // finvHedL3.setFINVHEDL3_TOTAL_TAX(jObject.getString("TotalTax"));
//                    // finvHedL3.setFINVHEDL3_TXN_DATE(jObject.getString("TxnDate"));
//                    //
//                    // list.add(finvHedL3);
//                    //
//                    // ++recordCount;
//                    // publishProgress(recordCount);
//                    //
//                    // }
//                    // if (new FinvHedL3DS(context).createOrUpdateFinvHedL3(list) >
//                    // 0) {
//                    // Log.v("createOrUpdatefinvHedL3", "Result : finvHedL3 Data
//                    // Inserted successfully");
//                    // }
//                }
//                break;
//                case FINVDETL3: {
//                    ArrayList<FinvDetL3> list = new ArrayList<FinvDetL3>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("RepLastThreeInvDetResult");
//                    Log.v(TAG, "Array Length FinvDetL3 DB :" + jsonArray.length());
//
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        FinvDetL3 finvDetL3 = new FinvDetL3();
//
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        finvDetL3.setFINVDETL3_AMT(jObject.getString("Amt"));
//                        finvDetL3.setFINVDETL3_ITEM_CODE(jObject.getString("ItemCode"));
//                        finvDetL3.setFINVDETL3_QTY(jObject.getString("Qty"));
//                        finvDetL3.setFINVDETL3_REF_NO(jObject.getString("RefNo"));
//                        finvDetL3.setFINVDETL3_SEQ_NO(jObject.getString("SeqNo"));
//                        finvDetL3.setFINVDETL3_TAX_AMT(jObject.getString("TaxAmt"));
//                        finvDetL3.setFINVDETL3_TAX_COM_CODE(jObject.getString("TaxComCode"));
//                        finvDetL3.setFINVDETL3_TXN_DATE(jObject.getString("TxnDate"));
//
//                        list.add(finvDetL3);
//                        ++recordCount;
//                        publishProgress(recordCount);
//                    }
//
//                    if (new FinvDetL3DS(context).createOrUpdateFinvDetL3(list) > 0) {
//                        Log.v("createOrUpdatefinvDetL3", "Result : finvDetL3 Data Inserted successfully");
//                    }
//                }
//                break;
//
//                case FTERMS: {
//
//                    ArrayList<TERMS> list = new ArrayList<TERMS>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("FtermResult");
//                    Log.v(TAG, "Array Length TermDS DB :" + jsonArray.length());
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//
//                        TERMS term = new TERMS();
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        term.setTERMCODE(jObject.getString("termcode"));
//                        term.setTERMDES(jObject.getString("termdes"));
//                        term.setTERMDISPER(jObject.getString("termdisper"));
//
//                        list.add(term);
//                        ++recordCount;
//                        publishProgress(recordCount);
//                    }
//
//                    if (new TermDS(context).createOrUpdateTerms(list) > 0) {
//                        Log.v("TermDS ", "Result : TermDS Data Inserted successfully");
//                    }
//                }
//
//                case FTAX: {
//                    ArrayList<Tax> list = new ArrayList<Tax>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("FTaxResult");
//                    Log.v(TAG, "Array Length FTaxResult DB :" + jsonArray.length());
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//
//                        Tax tax = new Tax();
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        tax.setTAXCODE(jObject.getString("TaxCode"));
//                        tax.setTAXNAME(jObject.getString("TaxName"));
//                        tax.setTAXPER(jObject.getString("TaxPer"));
//                        tax.setTAXREGNO(jObject.getString("TaxRegNo"));
//
//                        list.add(tax);
//                        ++recordCount;
//                        publishProgress(recordCount);
//
//                    }
//                    if (new TaxDS(context).createOrUpdateTaxHed(list) > 0) {
//                        Log.v("TaxDS", "Result : fDSchDet Data TaxDS successfully");
//                    }
//
//                }
//                break;

                case FTAXHED: {
                    ArrayList<TaxHed> list = new ArrayList<TaxHed>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("fTaxHedResult");
                    Log.v(TAG, "Array Length FTAXHED DB :" + jsonArray.length());
                    totalRecords = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        TaxHed taxHed = new TaxHed();
                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        taxHed.setACTIVE(jObject.getString("Active"));
                        taxHed.setTAXCOMCODE(jObject.getString("TaxComCode"));
                        taxHed.setTAXCOMNAME(jObject.getString("TaxComName"));

                        list.add(taxHed);
                        ++recordCount;
                        publishProgress(recordCount);

                    }
                    if (new TaxHedDS(context).createOrUpdateTaxHed(list) > 0) {
                        Log.v("TaxHedDS", "Result : TaxHedDS Data Inserted successfully");
                    }

                }
                break;

                case FTAXDET: {
                    ArrayList<TaxDet> list = new ArrayList<TaxDet>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("fTaxDetResult");
                    Log.v(TAG, "Array Length FTAXDET DB :" + jsonArray.length());
                    totalRecords = jsonArray.length();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        TaxDet taxDet = new TaxDet();
                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        taxDet.setTAXVAL(jObject.getString("TaxRate"));
                        taxDet.setSEQ(jObject.getString("TaxSeq"));
                        taxDet.setTAXCODE(jObject.getString("TaxCode"));
                        taxDet.setTAXCOMCODE(jObject.getString("TaxComCode"));
                        taxDet.setMODE(jObject.getString("TaxMode"));
                        taxDet.setRATE(jObject.getString("TaxPer"));
                        // taxDet.setTAXTYPE(jObject.getString("TaxName"));

                        list.add(taxDet);
                        ++recordCount;
                        publishProgress(recordCount);

                    }
                    if (new TaxDetDS(context).createOrUpdateTaxDet(list) > 0) {
                        Log.v("TaxDetDS", "Result : TaxDetDS Data Inserted successfully");
                    }

                }
                break;
//
//                case FTOURHED: {
//
//                    ArrayList<TourHed> list = new ArrayList<TourHed>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("FTourHedResult");
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//
//                        TourHed hed = new TourHed();
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        hed.setTOURHED_ADDMACH(jObject.getString("AddMach"));
//                        hed.setTOURHED_ADDUSER(jObject.getString("AddUser"));
//                        hed.setTOURHED_AREACODE(jObject.getString("AreaCode"));
//                        hed.setTOURHED_CLSFLG(jObject.getString("Clsflg"));
//                        hed.setTOURHED_COSTCODE(jObject.getString("CostCode"));
//                        hed.setTOURHED_DRIVERCODE(jObject.getString("DriverCode"));
//                        hed.setTOURHED_HELPERCODE(jObject.getString("HelperCode"));
//                        hed.setTOURHED_LOCCODE(jObject.getString("LocCode"));
//                        hed.setTOURHED_LOCCODEF(jObject.getString("LocCodeF"));
//                        hed.setTOURHED_LORRYCODE(jObject.getString("LorryCode"));
//                        hed.setTOURHED_MANUREF(jObject.getString("ManuRef"));
//                        hed.setTOURHED_REFNO(jObject.getString("RefNo"));
//                        hed.setTOURHED_REMARKS(jObject.getString("Remarks"));
//                        hed.setTOURHED_REPCODE(jObject.getString("RepCode"));
//                        hed.setTOURHED_ROUTECODE(jObject.getString("RouteCode"));
//                        hed.setTOURHED_TOURTYPE(jObject.getString("TourType"));
//                        hed.setTOURHED_TXNDATE(jObject.getString("TxnDate"));
//                        hed.setTOURHED_VANLOADFLG(jObject.getString("VanLoadFlg"));
//
//                        list.add(hed);
//                        ++recordCount;
//                        publishProgress(recordCount);
//
//                    }
//                    if (new TourHedDS(context).createOrUpdateTourHed(list) > 0) {
//                        Log.v("TourHedDS", "Result : TourHedDS Data Inserted successfully");
//                    }
//
//                }
//                break;
//
                case FSTKIN: {


                    new STKInDS(context).deleteAll();
                    ArrayList<StkIn> list = new ArrayList<StkIn>();
                    JSONArray jsonArray = jsonResponse.getJSONArray("FStkInResult");
                    totalRecords = jsonArray.length();



                    for (int i = 0; i < jsonArray.length(); i++) {

                        StkIn hed = new StkIn();
                        JSONObject jObject = (JSONObject) jsonArray.get(i);

                        hed.setBALQTY((jObject.getString("BalQty")));
                        hed.setCOSTPRICE((jObject.getString("CostPrice")));
                        hed.setINQTY((jObject.getString("InQty")));
                        hed.setITEMCODE((jObject.getString("ItemCode")));
                        hed.setLOCCODE((jObject.getString("LocCode")));
                        hed.setOTHCOST((jObject.getString("OthCost")));
                        hed.setREFNO((jObject.getString("RefNo")));
                        hed.setSTKRecDate((jObject.getString("StkRecDate")));
                        hed.setSTKRECNO((jObject.getString("StkRecNo")));
                        hed.setTXNDATE((jObject.getString("TxnDate")));
                        hed.setTXNTYPE((jObject.getString("TxnType")));

                        list.add(hed);
                        ++recordCount;
                        publishProgress(recordCount);

                    }
                    new STKInDS(context).createUpdateSTKIn(list);

                }
                break;
//
//                case FDEBITEMPRI: {
//
//                    ArrayList<DebItemPri> list = new ArrayList<DebItemPri>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("fDebItemPriResult");
//                    totalRecords = jsonArray.length();
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//
//                        DebItemPri hed = new DebItemPri();
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        hed.setBRANDCODE(jObject.getString("BrandCode"));
//                        hed.setDEBCODE(jObject.getString("DebCode"));
//                        hed.setDISPER(jObject.getString("Disper"));
//
//                        list.add(hed);
//                        ++recordCount;
//                        publishProgress(recordCount);
//
//                    }
//                    if (new DebItemPriDS(context).createOrUpdateDebItemPri(list) > 0) {
//                        Log.v("TourHedDS", "Result : TourHedDS Data Inserted successfully");
//                    }
//
//                }
//
//                case FBRANDTARGET: {
//
//                    new BrandTargetDS(context).deleteAll();
//
//                    ArrayList<BrandTarget> list = new ArrayList<BrandTarget>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("FBrandTargetResult");
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//
//                        BrandTarget hed = new BrandTarget();
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        hed.setBRANDTARGET_BRANDCODE(jObject.getString("BrandCode"));
//                        hed.setBRANDTARGET_COSTCODE(jObject.getString("CostCode"));
//                        hed.setBRANDTARGET_TARGET(jObject.getString("Target"));
//
//                        list.add(hed);
//                        ++recordCount;
//                        publishProgress(recordCount);
//                    }
//
//                    new BrandTargetDS(context).createOrUpdateBrandTarget(list);
//
//                }
//
//                break;
//                case FDISTRICT:{
//                    new fDistrictDS(context).deleteAll();
//
//
//                    ArrayList<fDistrict> list = new ArrayList<fDistrict>();
//                    JSONArray jsonArray = jsonResponse.getJSONArray("fDistrictResult");
//
//                    for (int i = 0; i < jsonArray.length(); i++) {
//
//                        fDistrict hed = new fDistrict();
//                        JSONObject jObject = (JSONObject) jsonArray.get(i);
//
//                        hed.setDISTRICT_CODE(jObject.getString("DistrCode"));
//                        hed.setDISTRICT_NAME(jObject.getString("DistrName"));
//                        hed.setDISTRICT_PROVECODE(jObject.getString("ProvCode"));
//
//                        list.add(hed);
//                        ++recordCount;
//                        publishProgress(recordCount);
//                    }
//
//                    new fDistrictDS(context).createOrUpdatefDistrict(list);
//
//                }
//
//                break;

                default:
                    break;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "FileNotFound";
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return JSON String
        return resultStr;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        String titleMsg = "Downloading " + downloadingDataType + " data";
        progressDialog.setMessage(titleMsg + " " + progress[0] + "/" + totalRecords);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        progressDialog.dismiss();
        taskListener.onTaskCompleted(taskType, result);

    }

}
