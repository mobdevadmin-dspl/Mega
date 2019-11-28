package com.datamation.megaheaters.view.Customer_registration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.adapter.NewCustomerAdapter;
import com.datamation.megaheaters.control.UtilityContainer;
import com.datamation.megaheaters.data.NewCustomerDS;
import com.datamation.megaheaters.model.NewCustomer;
import com.datamation.megaheaters.view.IconPallet;
import com.datamation.megaheaters.view.MainActivity;

import java.util.ArrayList;

/**
 * Created by Dhanushika on 4/4/2018.
 */

public class CustomerRegMain extends Fragment {
    private  ListView listView_cusDet;
    private  View view;
    private ArrayList<NewCustomer>customerArrayList;
    private FloatingActionButton fab;
    String btnType = "U";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.cus_reg_main, container, false);

        listView_cusDet=(ListView) view.findViewById(R.id.lvCuslist);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("UPLOAD DUE CUSTOMER");
        toolbar.setLogo(R.drawable.dm_logo_64);
        setHasOptionsMenu(true);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnType.equals("N")) {
                    getActivity().setTitle("UPLOAD DUE CUSTOMER");
                    fab.setImageResource(R.drawable.tick);
                    btnType = "U";
                    fatahData("N");
                } else {
                    getActivity().setTitle("UPLOADED CUSTOMER");
                    fab.setImageResource(R.drawable.cross);
                    fatahData("U");
                    btnType = "N";
                }
            }
        });
        fatahData("N");
         /* connect context menu with listview for long click */
        registerForContextMenu(listView_cusDet);

        listView_cusDet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewCustomer newCustomer = customerArrayList.get(position);
                deleteDialog(getActivity(), newCustomer.getCUSTOMER_ID());
            }
        });
        return view;
    }

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
                listView_cusDet.clearTextFilter();
                customerArrayList=new NewCustomerDS(getActivity()).getAllNewCusDetails(newText,btnType.equals("N") ? "U" : "N");
                listView_cusDet.setAdapter(new NewCustomerAdapter(getActivity(),customerArrayList));

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.lvexpenselist) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.enterNewItem) {
            UtilityContainer.mLoadFragment(new AddNewCusRegistration(), getActivity());
        } else if (item.getItemId() == R.id.exitExpence) {
            UtilityContainer.mLoadFragment(new IconPallet(), getActivity());
        }
        return super.onOptionsItemSelected(item);
    }

    public void fatahData(String param){
        listView_cusDet.clearTextFilter();
        customerArrayList=new NewCustomerDS(getActivity()).getAllNewCusDetails("",param);
        listView_cusDet.setAdapter(new NewCustomerAdapter(getActivity(),customerArrayList));
    }




    private void deleteDialog(final Context context, final String refno) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Are you sure you want to cancel this entry?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (new NewCustomerDS(getActivity()).isEntrySynced(refno))
                    Toast.makeText(getActivity(), "Synced entry. Unable to delete.", Toast.LENGTH_LONG).show();
                else {
                    int count = new NewCustomerDS(getActivity()).deleteRecord(refno);
                    if (count > 0) {
                        new NewCustomerDS(getActivity()).deleteRecord(refno);
                        fatahData("");
                    }
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
}
