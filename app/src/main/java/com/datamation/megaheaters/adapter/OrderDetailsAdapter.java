package com.datamation.megaheaters.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.data.FreeHedDS;
import com.datamation.megaheaters.data.ItemsDS;
import com.datamation.megaheaters.model.FreeHed;
import com.datamation.megaheaters.model.TranSODet;

import java.text.NumberFormat;
import java.util.ArrayList;
//pubudu
public class OrderDetailsAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    ArrayList<TranSODet> list;
    Context context;
    ArrayList<FreeHed> arrayList;
    private NumberFormat numberFormat;
    public OrderDetailsAdapter(Context context, ArrayList<TranSODet> list){
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;

    }
    @Override
    public int getCount() {
        if (list != null) return list.size();
        return 0;
    }
    @Override
    public TranSODet getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder viewHolder;
        numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        if(convertView ==null) {
            viewHolder = new ViewHolder();
            convertView =inflater.inflate(R.layout.row_order_details,parent,false);
            viewHolder.lblItem = (TextView) convertView.findViewById(R.id.row_item);
            viewHolder.lblQty = (TextView) convertView.findViewById(R.id.row_cases);
            viewHolder.lblAMt = (TextView) convertView.findViewById(R.id.row_piece);
            viewHolder.showStatus=(TextView)convertView.findViewById(R.id.row_free_status);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.lblItem.setText(new ItemsDS(convertView.getContext()).getItemNameByCode(list.get(position).getFTRANSODET_ITEMCODE()) + " - " + list.get(position).getFTRANSODET_ITEMCODE());
        viewHolder.lblQty.setText(list.get(position).getFTRANSODET_QTY());
       // viewHolder.lblAMt.setText(list.get(position).getFTRANSODET_AMT());
      //  viewHolder.lblAMt.setText(list.get(position).getFTRANSODET_AMT());
        if(list.get(position).getFTRANSODET_CHANGED_PRICE().equals("0.0"))
        viewHolder.lblAMt.setText(""+numberFormat.format(Double.parseDouble(list.get(position).getFTRANSODET_QTY())*Double.parseDouble(list.get(position).getFTRANSODET_PRICE())));
        else
        viewHolder.lblAMt.setText(""+numberFormat.format(Double.parseDouble(list.get(position).getFTRANSODET_QTY())*Double.parseDouble(list.get(position).getFTRANSODET_CHANGED_PRICE())));

        FreeHedDS freeHedDS = new FreeHedDS(context);
        arrayList = freeHedDS.getFreeIssueItemDetailByRefno(list.get(position).getFTRANSODET_ITEMCODE(),"" );
        for(FreeHed freeHed:arrayList){
            int itemQty = (int) Float.parseFloat(freeHed.getFFREEHED_ITEM_QTY());
            int enterQty = (int) Float.parseFloat(list.get(position).getFTRANSODET_QTY());

            if(enterQty<itemQty){
                //other products------this procut has't free items
                viewHolder.showStatus.setBackgroundColor(Color.WHITE);
            }else{
                //free item eligible product
                viewHolder.showStatus.setBackground(context.getResources().getDrawable(R.drawable.ic_free_b));
            }


        }


        return convertView;
    }
    private  static  class  ViewHolder{
        TextView lblItem;
        TextView lblQty;
        TextView lblAMt;
        TextView showStatus;

    }

}
