package com.datamation.megaheaters.view.receipt;


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
import android.util.Log;
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
import com.datamation.megaheaters.adapter.ReceiptInvoiceHistoryAdapter;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.control.print_preview.ReceiptPreviewAlertBox;
import com.datamation.megaheaters.data.FDDbNoteDS;
import com.datamation.megaheaters.data.RecDetDS;
import com.datamation.megaheaters.data.RecHedDS;
import com.datamation.megaheaters.model.RecDet;
import com.datamation.megaheaters.model.RecHed;
import com.datamation.megaheaters.view.IconPallet_mega;
import com.datamation.megaheaters.view.MainActivity;


import java.util.ArrayList;

public class ReceiptInvoice extends Fragment {
    View view;
    ListView lvInvoiceList;
    ArrayList<RecHed> arrayList;
    FloatingActionButton fab;
    Activity activity;
    String btnType = "U";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sales_mangemet_van_sales_invoice, container, false);
        lvInvoiceList = (ListView) view.findViewById(R.id.lvPhoneInvoiceList);
        registerForContextMenu(lvInvoiceList);
        setHasOptionsMenu(true);
        activity = getActivity();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.dm_logo_64);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        activity.setTitle("UPLOAD DUE RECEIPTS");
        displayInvoiceHistory("N");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnType.equals("N")) {
                    activity.setTitle("UPLOAD DUE RECEIPTS");
                    fab.setImageResource(R.drawable.tick);
                    btnType = "U";
                    displayInvoiceHistory("N");
                } else {
                    activity.setTitle("UPLOADED RECEIPTS");
                    fab.setImageResource(R.drawable.cross);
                    displayInvoiceHistory("U");
                    btnType = "N";
                }
            }
        });

        return view;
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.lvPhoneInvoiceList) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_receipt, menu);
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        RecHed recHed = arrayList.get(info.position);

        switch (item.getItemId()) {

            case R.id.print:
                setBluetooth(true);
                new ReceiptPreviewAlertBox(getActivity()).PrintDetailsDialogbox(getActivity(), "Print preview", recHed.getFPRECHED_REFNO());
                return true;

            case R.id.delete:
                //Toast.makeText(getActivity(), "Delete UnAuthorised!", Toast.LENGTH_SHORT).show();
                deleteReceipt(getActivity(),recHed.getFPRECHED_REFNO());
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private void deleteReceipt(final Context context, final String refno) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Are you sure you want to cancel this receipt?");
        final RecHedDS recHedDS = new RecHedDS(getActivity());
        final RecDetDS detDS = new RecDetDS(getActivity());
        final ArrayList<RecDet> delmrdet = detDS.GetReceiptByRefno(refno);
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

//                try {
//                    recHedDS.DeleteStatusUpdateForEceipt(refno);
//                } catch (Exception e) {
//                    Log.w("Cancel FORDHED", e.toString());
//                }

                try {
                    if(recHedDS.DeleteStatusUpdateForEceipt(refno)>0) {
                        new FDDbNoteDS(getActivity()).UpdateFddbNoteBalanceForReceipt(delmrdet);
                        detDS.UpdateDeleteStatus(refno);
                    }else{
                        Toast.makeText(getActivity(), "Delete UnAuthorised!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.w("Cancel FRECDET", e.toString());
                } finally {
                    displayInvoiceHistory("");
                }

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }
	
	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private void displayInvoiceHistory(String params) {
        lvInvoiceList = (ListView) view.findViewById(R.id.lvPhoneInvoiceList);
        lvInvoiceList.clearTextFilter();
        arrayList = new RecHedDS(getActivity()).getAllCompletedRecHedS(params);
        lvInvoiceList.setAdapter(new ReceiptInvoiceHistoryAdapter(getActivity(), arrayList));
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        for (int i = 0; i < menu.size(); ++i) {
            menu.removeItem(menu.getItem(i).getItemId());
        }

        menu.clear();
        inflater.inflate(R.menu.frag_nonprd_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchItems).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.enterNewItem) {
            UtilityContainer.mLoadFragment(new Receipt(), getActivity());
        } else {
            UtilityContainer.mLoadFragment(new IconPallet_mega(), getActivity());
        }

        return super.onOptionsItemSelected(item);
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

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

}
