package com.datamation.megaheaters.view.vansale;

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
import com.datamation.megaheaters.adapter.VanInvoiceHistoryAdapter;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.control.print_preview.VanSalePrintPreviewAlertBox;
import com.datamation.megaheaters.data.DispDetDS;
import com.datamation.megaheaters.data.DispHedDS;
import com.datamation.megaheaters.data.DispIssDS;
import com.datamation.megaheaters.data.FInvRDetDS;
import com.datamation.megaheaters.data.FInvRHedDS;
import com.datamation.megaheaters.data.InvDetDS;
import com.datamation.megaheaters.data.InvHedDS;
import com.datamation.megaheaters.data.InvTaxDTDS;
import com.datamation.megaheaters.data.InvTaxRGDS;
import com.datamation.megaheaters.data.ItemLocDS;
import com.datamation.megaheaters.data.OrdFreeIssueDS;
import com.datamation.megaheaters.data.OrderDiscDS;
import com.datamation.megaheaters.data.StkIssDS;
import com.datamation.megaheaters.model.InvDet;
import com.datamation.megaheaters.model.InvHed;
import com.datamation.megaheaters.view.IconPallet_mega;
import com.datamation.megaheaters.view.MainActivity;

import java.util.ArrayList;

public class VanSaleInvoice extends Fragment {
    View view;

    ListView lvInvoiceList;
    ArrayList<InvHed> arrayList;
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
        view = inflater.inflate(R.layout.sales_mangemet_van_sales_invoice, container, false);

        lvInvoiceList = (ListView) view.findViewById(R.id.lvPhoneInvoiceList);
        registerForContextMenu(lvInvoiceList);
        setHasOptionsMenu(true);

        activity = getActivity();
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setLogo(R.drawable.dm_logo_64);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        activity.setTitle("UPLOAD DUE INVOICES");
        displayInvoiceHistory("N");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnType.equals("N")) {
                    activity.setTitle("UPLOAD DUE INVOICES");
                    fab.setImageResource(R.drawable.tick);
                    btnType = "U";
                    displayInvoiceHistory("N");
                } else {
                    activity.setTitle("UPLOADED INVOICES");
                    fab.setImageResource(R.drawable.cross);
                    displayInvoiceHistory("U");
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
        InvHed ordHed = arrayList.get(info.position);
        ArrayList<InvDet>invDetArrayList=new InvDetDS(getActivity()).getAllInvDet(ordHed.getFINVHED_REFNO());
        ordHed.setInvDetArrayList(invDetArrayList);

        switch (item.getItemId()) {
            case R.id.cancel:
                DeleteDialog(getActivity(), ordHed.getFINVHED_REFNO());
                return true;

            case R.id.print:
                setBluetooth(true);
                new VanSalePrintPreviewAlertBox(getActivity()).PrintDetailsDialogbox(getActivity(), "Print preview", ordHed.getFINVHED_REFNO());
                return true;
            case R.id.reOrde:

                VanSales vanSales = new VanSales();
                Bundle bundle = new Bundle();
                bundle.putBoolean("Active", false);
                bundle.putSerializable("order",ordHed);
                vanSales.setArguments(bundle);
                UtilityContainer.mLoadFragment(vanSales, activity);
                return  true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private void DeleteDialog(final Context context, final String refno) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage("Are you sure you want to delete this entry?");
        final InvHedDS invHedDS = new InvHedDS(getActivity());
        final InvDetDS detDS = new InvDetDS(getActivity());
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

				/* If invoice is synced [false => invoice is synced] */

                String locCode = invHedDS.restData(refno);
                if (locCode.equals(""))
                    Toast.makeText(getActivity(), "Synced invoice deletion not possible..!", Toast.LENGTH_LONG).show();
                /* delete invoice details */
                else {

					/*reset STKIss & StkIn*/
					new FInvRDetDS(getActivity()).restData(new FInvRHedDS(getActivity()).getActiveReturnRef(refno));
					new FInvRHedDS(getActivity()).restData(new FInvRHedDS(getActivity()).getActiveReturnRef(refno));
                    new StkIssDS(getActivity()).resetData(refno);
                    new OrderDiscDS(getActivity()).clearData(refno);
                    new OrdFreeIssueDS(getActivity()).ClearFreeIssues(refno);
                    new ItemLocDS(getActivity()).UpdateInvoiceQOH(refno, "+", locCode);
                    new ItemLocDS(getActivity()).UpdateInvoiceQOHInReturn(refno, "-", locCode);
                    new InvTaxDTDS(getActivity()).ClearTable(refno);
                    new InvTaxRGDS(getActivity()).ClearTable(refno);

					/*Dispatch tables*/
                    new DispHedDS(getActivity()).clearTable(refno);
                    new DispDetDS(getActivity()).clearTable(refno);
                    new DispIssDS(getActivity()).clearTable(refno);

                    detDS.restData(refno);
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

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private void displayInvoiceHistory(String param) {
        lvInvoiceList = (ListView) view.findViewById(R.id.lvPhoneInvoiceList);
        lvInvoiceList.clearTextFilter();
        InvHedDS hedDS = new InvHedDS(getActivity());
        arrayList = hedDS.getAllUnsyncedInvHed("", param);
        lvInvoiceList.setAdapter(new VanInvoiceHistoryAdapter(getActivity(), arrayList));
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

                lvInvoiceList = (ListView) view.findViewById(R.id.lvPhoneInvoiceList);
                lvInvoiceList.clearTextFilter();
                InvHedDS hedDS = new InvHedDS(getActivity());
                arrayList = hedDS.getAllUnsyncedInvHed(newText, btnType.equals("N") ? "U" : "N");
                lvInvoiceList.setAdapter(new VanInvoiceHistoryAdapter(getActivity(), arrayList));
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.enterNewItem) {
            UtilityContainer.mLoadFragment(new VanSales(), getActivity());
        } else {
            UtilityContainer.mLoadFragment(new IconPallet_mega(), getActivity());
        }

        return super.onOptionsItemSelected(item);
    }
}
