package com.datamation.megaheaters.view.sales_return;

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
import com.datamation.megaheaters.adapter.ReturnHistoryAdapter;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.control.print_preview.SalesPrintPreviewAlertBox;
import com.datamation.megaheaters.control.print_preview.VanSalePrintPreviewAlertBox;
import com.datamation.megaheaters.data.FInvRDetDS;
import com.datamation.megaheaters.data.FInvRHedDS;
import com.datamation.megaheaters.model.FInvRHed;
import com.datamation.megaheaters.view.IconPallet;
import com.datamation.megaheaters.view.IconPallet_mega;
import com.datamation.megaheaters.view.MainActivity;


import java.util.ArrayList;

public class SalesReturnHistory extends Fragment {
    View view;

    ListView lvReturnList;
    ArrayList<FInvRHed> arrayList;
    FloatingActionButton fab;
    Activity activity;
    String btnType = "U";

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

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sales_mangement_pre_sales_invoice, container, false);

        lvReturnList = (ListView) view.findViewById(R.id.lvPhoneInvoiceList);
        registerForContextMenu(lvReturnList);
        setHasOptionsMenu(true);

        activity = getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.dm_logo_64);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        activity.setTitle("UPLOAD DUE RETURNS");
        displayReturnHistory("N");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnType.equals("N")) {
                    activity.setTitle("UPLOAD DUE RETURNS");
                    fab.setImageResource(R.drawable.tick);
                    btnType = "U";
                    displayReturnHistory("N");
                } else {
                    activity.setTitle("UPLOADED RETURNS");
                    fab.setImageResource(R.drawable.cross);
                    displayReturnHistory("U");
                    btnType = "N";
                }
            }
        });

        return view;
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.lvPhoneInvoiceList) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        FInvRHed returnHed = arrayList.get(info.position);
        switch (item.getItemId()) {
            case R.id.cancel:
                delete(getActivity(), returnHed.getFINVRHED_REFNO());
                return true;
            case R.id.print:
                setBluetooth(true);
                new VanSalePrintPreviewAlertBox(getActivity()).PrintDetailsDialogbox(getActivity(), "Print preview", returnHed.getFINVRHED_REFNO());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void delete(final Context context, final String refno) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Delete Return");
        alertDialogBuilder.setMessage("Are you sure you want to cancel this entry?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if ((new FInvRHedDS(getActivity()).blockInnerReturnDelete(refno) > 0)) {
                    new FInvRDetDS(getActivity()).restData(refno);
                    Toast.makeText(getActivity(), "Deleted successfully..!", Toast.LENGTH_LONG).show();
                      }else {
                    Toast.makeText(getActivity(), "UPLOADED return or Inner return deletion not possible here..!", Toast.LENGTH_LONG).show();


                }

                displayReturnHistory("N");

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alertD = alertDialogBuilder.create();

        alertD.show();
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private void displayReturnHistory(String param) {
        lvReturnList = (ListView) view.findViewById(R.id.lvPhoneInvoiceList);
        lvReturnList.clearTextFilter();
        arrayList = new FInvRHedDS(getActivity()).getAllUnsyncedReturnHed("", param);
        lvReturnList.setAdapter(new ReturnHistoryAdapter(getActivity(), arrayList));
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

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
                lvReturnList = (ListView) view.findViewById(R.id.lvPhoneInvoiceList);
                lvReturnList.clearTextFilter();
                arrayList = new FInvRHedDS(getActivity()).getAllUnsyncedReturnHed(newText, btnType.equals("N") ? "U" : "N");
                lvReturnList.setAdapter(new ReturnHistoryAdapter(getActivity(), arrayList));
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.enterNewItem)
            UtilityContainer.mLoadFragment(new SalesReturn(), activity);
        else
            UtilityContainer.mLoadFragment(new IconPallet_mega(), activity);

        return super.onOptionsItemSelected(item);
    }

}
