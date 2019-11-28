package com.datamation.megaheaters.adapter;

import java.util.ArrayList;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.model.TranSODet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PrintItemAdapter extends ArrayAdapter<TranSODet> {
    Context context;
    ArrayList<TranSODet> list;
    String refno;

    public PrintItemAdapter(Context context, ArrayList<TranSODet> list, String refno) {

        super(context, R.layout.presale_listview_printrow, list);
        this.context = context;
        this.list = list;
        this.refno = refno;
    }

    @Override
    public View getView(int position, final View convertView, final ViewGroup parent) {

        LayoutInflater inflater = null;
        View row = null;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        row = inflater.inflate(R.layout.presale_listview_printrow, parent, false);

        TextView itemname = (TextView) row.findViewById(R.id.printitemname);
        TextView pieceqty = (TextView) row.findViewById(R.id.printpiecesQty);
        TextView printindex = (TextView) row.findViewById(R.id.printindex);
        TextView mrp = (TextView) row.findViewById(R.id.printPreMRP);
        TextView disc = (TextView) row.findViewById(R.id.printPreDisc);
        TextView amount = (TextView) row.findViewById(R.id.printamount);

        itemname.setText(list.get(position).getFTRANSODET_ITEMCODE());
        pieceqty.setText(list.get(position).getFTRANSODET_QTY());
        amount.setText(list.get(position).getFTRANSODET_AMT());
        disc.setText(list.get(position).getFTRANSODET_DISAMT());
        //mrp.setText(""+(Double.parseDouble(list.get(position).getFTRANSODET_AMT()))/Double.parseDouble(list.get(position).getFTRANSODET_QTY()));
//        if(list.get(position).getFTRANSODET_CHANGED_PRICE().equals("0")){
//            mrp.setText(list.get(position).getFTRANSODET_PRICE());
//        }else{
//            mrp.setText(list.get(position).getFTRANSODET_CHANGED_PRICE());
//        }
        mrp.setText(list.get(position).getFTRANSODET_TSELLPRICE());
        position = position + 1;
        String pos = Integer.toString(position);
        printindex.setText(pos + ". ");

        return row;
    }
}
