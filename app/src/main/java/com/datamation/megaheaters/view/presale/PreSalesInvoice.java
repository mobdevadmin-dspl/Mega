package com.datamation.megaheaters.view.presale;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.adapter.InvoiceHistoryAdapter;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.control.print_preview.SalesPrintPreviewAlertBox;
import com.datamation.megaheaters.data.OrdFreeIssueDS;
import com.datamation.megaheaters.data.OrderDiscDS;
import com.datamation.megaheaters.data.PreSaleTaxDTDS;
import com.datamation.megaheaters.data.PreSaleTaxRGDS;
import com.datamation.megaheaters.data.TranSODetDS;
import com.datamation.megaheaters.data.TranSOHedDS;
import com.datamation.megaheaters.model.TranSODet;
import com.datamation.megaheaters.model.TranSOHed;
import com.datamation.megaheaters.view.IconPallet_mega;
import com.datamation.megaheaters.view.MainActivity;

import java.util.ArrayList;

public class PreSalesInvoice extends Fragment {
    View view;
    ListView lvInvoiceList;
    ArrayList<TranSOHed> arrayList;
    String btnType = "N";
    Activity activity;
    FloatingActionButton fab;

    public static boolean setBluetooth(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = bluetoothAdapter.isEnabled();
        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        } else if (!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        return true;
    }

    	/*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sales_mangement_pre_sales_invoice, container, false);

        lvInvoiceList = (ListView) view.findViewById(R.id.lvPhoneInvoiceList);
        registerForContextMenu(lvInvoiceList);
        activity = getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.dm_logo_64);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        activity.setTitle("UPLOAD DUE ORDERS");
        displayInvoiceHistory("N");
        setHasOptionsMenu(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnType.equals("N")) {
                    activity.setTitle("UPLOAD DUE ORDERS");
                    fab.setImageResource(R.drawable.tick);
                    btnType = "U";
                    displayInvoiceHistory("N");
                } else {
                    activity.setTitle("UPLOADED ORDERS");
                    fab.setImageResource(R.drawable.cross);
                    displayInvoiceHistory("U");
                    btnType = "N";
                }
            }
        });

        return view;
    }

    	/*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*/

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.lvPhoneInvoiceList) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    	/*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*/

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        TranSOHed ordHed = arrayList.get(info.position);
        ArrayList<TranSODet> itemList = new TranSODetDS(getActivity()).getAllOrderDetails(ordHed.getFTRANSOHED_REFNO());
        ordHed.setSoDetArrayList(itemList);
        switch (item.getItemId()) {
            case R.id.cancel:
                delete(getActivity(), ordHed.getFTRANSOHED_REFNO());
                return true;
            case R.id.print:
                setBluetooth(true);
                new SalesPrintPreviewAlertBox(getActivity()).PrintDetailsDialogbox(getActivity(), "Print preview", ordHed.getFTRANSOHED_REFNO());
                return true;
            case R.id.reOrde:
                PreSales preSales = new PreSales();
                Bundle bundle = new Bundle();
                bundle.putBoolean("Active", false);
                bundle.putSerializable("order",ordHed);
                preSales.setArguments(bundle);
                UtilityContainer.mLoadFragment(preSales, activity);


                Toast.makeText(getActivity(), "click successfully..!", Toast.LENGTH_LONG).show();
            default:
                return super.onContextItemSelected(item);
        }
    }

    	/*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*/

    private void delete(final Context context, final String refno) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Delete Order");
        alertDialogBuilder.setMessage("Are you sure you want to cancel this entry?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (!(new TranSOHedDS(getActivity()).restData(refno)))
                    Toast.makeText(getActivity(), "UPLOADED order deletion not possible..!", Toast.LENGTH_LONG).show();
                else {
                    new OrderDiscDS(getActivity()).clearData(refno);
                    new OrdFreeIssueDS(getActivity()).ClearFreeIssues(refno);
                    new PreSaleTaxDTDS(getActivity()).ClearTable(refno);
                    new PreSaleTaxRGDS(getActivity()).ClearTable(refno);
                    new TranSODetDS(getActivity()).restData(refno);
                    Toast.makeText(getActivity(), "Deleted successfully..!", Toast.LENGTH_LONG).show();
                }
                displayInvoiceHistory("N");

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

	    /*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*/

    private void displayInvoiceHistory(String param) {
        lvInvoiceList = (ListView) view.findViewById(R.id.lvPhoneInvoiceList);
        lvInvoiceList.clearTextFilter();
        arrayList = new TranSOHedDS(getActivity()).getAllUnsyncedOrdHed("", param);
        lvInvoiceList.setAdapter(new InvoiceHistoryAdapter(getActivity(), arrayList));
    }

    	/*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        for (int i = 0; i < menu.size(); ++i) {
            menu.removeItem(menu.getItem(i).getItemId());
        }

        inflater.inflate(R.menu.frag_nonprd_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchItems).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                lvInvoiceList = (ListView) view.findViewById(R.id.lvPhoneInvoiceList);
                lvInvoiceList.clearTextFilter();
                arrayList = new TranSOHedDS(getActivity()).getAllUnsyncedOrdHed(newText, btnType.equals("N") ? "U" : "N");
                lvInvoiceList.setAdapter(new InvoiceHistoryAdapter(getActivity(), arrayList));
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    	/*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.enterNewItem) {
            UtilityContainer.mLoadFragment(new PreSales(), getActivity());
        } else {
            UtilityContainer.mLoadFragment(new IconPallet_mega(), getActivity());
        }

        return super.onOptionsItemSelected(item);

    }

}
