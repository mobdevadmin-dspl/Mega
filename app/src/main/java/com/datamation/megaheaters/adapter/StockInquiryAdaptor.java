package com.datamation.megaheaters.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.datamation.megaheaters.model.StockInfo;
import com.datamation.megaheaters.R;

public class StockInquiryAdaptor extends ArrayAdapter<StockInfo> {
    Context context;
    ArrayList<StockInfo> list;

    public StockInquiryAdaptor(Context context, ArrayList<StockInfo> list) {

        super(context, R.layout.row_stock_inquiry, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        LayoutInflater inflater = null;
        View row = null;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.row_stock_inquiry, parent, false);

        TextView itemcode = (TextView) row.findViewById(R.id.row_itemcode);
        TextView itemname = (TextView) row.findViewById(R.id.row_itemname);
        TextView qty = (TextView) row.findViewById(R.id.row_qty);

        itemcode.setText(list.get(position).getStock_Itemcode());
        itemname.setText(list.get(position).getStock_Itemname());
        qty.setText(list.get(position).getStock_Qoh());
        return row;
    }
}
