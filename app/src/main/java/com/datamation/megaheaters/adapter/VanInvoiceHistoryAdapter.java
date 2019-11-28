package com.datamation.megaheaters.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.datamation.megaheaters.data.DebtorDS;
import com.datamation.megaheaters.model.InvHed;
import com.datamation.megaheaters.R;

public class VanInvoiceHistoryAdapter extends ArrayAdapter<InvHed> {
    Context context;
    ArrayList<InvHed> list;

    public VanInvoiceHistoryAdapter(Context context, ArrayList<InvHed> list) {

        super(context, R.layout.row_invoice_history, list);
        this.context = context;
        this.list = list;
    }


    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        LayoutInflater inflater = null;
        View row = null;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.row_invoice_history, parent, false);

        TextView refno = (TextView) row.findViewById(R.id.refno);
        TextView date = (TextView) row.findViewById(R.id.date);
        TextView amount = (TextView) row.findViewById(R.id.amount);
        TextView customer = (TextView) row.findViewById(R.id.customer);

        DebtorDS debtorDS = new DebtorDS(context);
        refno.setText(list.get(position).getFINVHED_REFNO());
        date.setText(list.get(position).getFINVHED_ADDDATE());
        amount.setText(list.get(position).getFINVHED_TOTALAMT());
        customer.setText(debtorDS.getCustNameByCode(list.get(position).getFINVHED_DEBCODE()));

        return row;
    }
}
