package com.datamation.megaheaters.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datamation.megaheaters.R;
import com.datamation.megaheaters.control.SharedPref;
import com.datamation.megaheaters.data.PreProductDS;
import com.datamation.megaheaters.listviewitems.CustomKeypadDialog;
import com.datamation.megaheaters.listviewitems.CustomKeypadDialogDiscount;
import com.datamation.megaheaters.listviewitems.CustomKeypadDialogPrice;
import com.datamation.megaheaters.model.PreProduct;

import java.util.ArrayList;

/**
 * Created by Dhanushika on 1/18/2018.
 */

public class NewPreProductAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<PreProduct> preProductArrayList;
    Context context;
    private String preText = null;


    public NewPreProductAdapter(Context context, ArrayList<PreProduct> preProductArrayList) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.preProductArrayList = preProductArrayList;
    }


    @Override
    public int getCount() {
        if (preProductArrayList != null)
            return preProductArrayList.size();
        return 0;
    }

    @Override
    public PreProduct getItem(int position) {
        return preProductArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_product_item_pre, parent, false);

            viewHolder.lnStripe = (LinearLayout) convertView.findViewById(R.id.lnProductStripe);
            viewHolder.itemCode = (TextView) convertView.findViewById(R.id.row_itemcode);
            viewHolder.ItemName = (TextView) convertView.findViewById(R.id.row_itemname);
            viewHolder.Price = (TextView) convertView.findViewById(R.id.row_price);
            viewHolder.Discount = (TextView) convertView.findViewById(R.id.row_discount);
            viewHolder.lblQty = (TextView) convertView.findViewById(R.id.et_qty);
            viewHolder.btnPlus = (ImageButton) convertView.findViewById(R.id.btnAddition);
            viewHolder.btnMinus = (ImageButton) convertView.findViewById(R.id.btnSubtract);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final PreProduct preProduct = getItem(position);

        viewHolder.itemCode.setText(preProduct.getPREPRODUCT_ITEMCODE()+"(QOH | "+preProduct.getPREPRODUCT_QOH()+")");
        viewHolder.ItemName.setText(preProduct.getPREPRODUCT_ITEMNAME());
        if(preProduct.getPREPRODUCT_PRICE().isEmpty() || preProduct.getPREPRODUCT_PRICE()==null){
            viewHolder.Price.setText("0.00");
        }else{
            viewHolder.Price.setText(preProduct.getPREPRODUCT_PRICE());
        }
        if (new SharedPref(context).getGlobalVal("PrekeyPayType").equalsIgnoreCase("credit")){
            viewHolder.Discount.setText(new SharedPref(context).getGlobalVal("PrekeyCreditDis"));
            new PreProductDS(context).updateProductDiscount(preProduct.getPREPRODUCT_ITEMCODE(), new SharedPref(context).getGlobalVal("PrekeyCreditDis"));
        }else{
            viewHolder.Discount.setText(preProduct.getPREPRODUCT_DISCOUNT());
        }
       // viewHolder.Discount.setText(preProduct.getPREPRODUCT_DISCOUNT());
        viewHolder.lblQty.setText(preProduct.getPREPRODUCT_QTY());
        //-------------------------------------------------------------------------------------------------------------------------------------------
              /*Change colors*/
        if (Integer.parseInt(viewHolder.lblQty.getText().toString()) > 0) {
            viewHolder.lnStripe.setBackground(context.getResources().getDrawable(R.drawable.custom_textbox_new));

        } else {
            viewHolder.lnStripe.setBackground(context.getResources().getDrawable(R.drawable.custom_textbox));
        }
        //------------------------------------------------------------------------------------------------------------------------------------
        viewHolder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(viewHolder.lblQty.getText().toString());

                if (--qty >= 0) {
                    viewHolder.lblQty.setText((Integer.parseInt(viewHolder.lblQty.getText().toString()) - 1) + "");
                    preProduct.setPREPRODUCT_QTY(viewHolder.lblQty.getText().toString());
                    new PreProductDS(context).updateProductQty(preProduct.getPREPRODUCT_ITEMCODE(), viewHolder.lblQty.getText().toString());
                }

                //*Change colors*//*
                if (qty == 0)
                    viewHolder.lnStripe.setBackground(context.getResources().getDrawable(R.drawable.custom_textbox));
            }
        });

        //------------------------------------------------------------------------------------------------------------------------------
        viewHolder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double qty = Double.parseDouble(viewHolder.lblQty.getText().toString());
                viewHolder.lnStripe.setBackground(context.getResources().getDrawable(R.drawable.custom_textbox_new));

                //   if (qty <= (Double.parseDouble(viewHolder.HoQ.getText().toString()))) {
                if (qty >= 0) {
                    viewHolder.lblQty.setText((Integer.parseInt(viewHolder.lblQty.getText().toString()) + 1) + "");
                    preProduct.setPREPRODUCT_QTY(viewHolder.lblQty.getText().toString());
                    new PreProductDS(context).updateProductQty(preProduct.getPREPRODUCT_ITEMCODE(), viewHolder.lblQty.getText().toString());
                }
            }
        });
        //---------------------------------------------------------------------------------------------------------------------------------------------------
        viewHolder.btnPlus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        viewHolder.btnPlus.setBackground(context.getResources().getDrawable(R.drawable.icon_plus_d));
                    }
                    break;

                    case MotionEvent.ACTION_UP: {
                        viewHolder.btnPlus.setBackground(context.getResources().getDrawable(R.drawable.icon_plus));
                    }
                    break;
                }
                return false;
            }
        });
        //---------------------------------------------------------------------------------------------------------------------------------------------

        viewHolder.btnMinus.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        viewHolder.btnMinus.setBackground(context.getResources().getDrawable(R.drawable.icon_minus_d));
                    }
                    break;

                    case MotionEvent.ACTION_UP: {
                        viewHolder.btnMinus.setBackground(context.getResources().getDrawable(R.drawable.icon_minus));
                    }
                    break;
                }
                return false;
            }
        });

//--------------------------------------------------------------------------------------------------------------------------
        viewHolder.lblQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomKeypadDialog keypad = new CustomKeypadDialog(context, false, new CustomKeypadDialog.IOnOkClickListener() {
                    @Override
                    public void okClicked(double value) {
                        String distrStock = preProduct.getPREPRODUCT_QOH();
                        int enteredQty = (int) value;
                        Log.d("<>+++++", "" + distrStock);
                        //Not check Exceeds Qty in Pre sales------------------------------------
                        new PreProductDS(context).updateProductQty(preProduct.getPREPRODUCT_ITEMCODE(), String.valueOf(enteredQty));
                        preProduct.setPREPRODUCT_QTY(String.valueOf(enteredQty));
                        viewHolder.lblQty.setText(preProduct.getPREPRODUCT_QTY());

                        //*Change colors*//**//*
                        if (Integer.parseInt(viewHolder.lblQty.getText().toString()) > 0)
                            viewHolder.lnStripe.setBackground(context.getResources().getDrawable(R.drawable.custom_textbox_new));
                        else
                            viewHolder.lnStripe.setBackground(context.getResources().getDrawable(R.drawable.custom_textbox));

                    }
                });
                keypad.show();
                keypad.setHeader("SELECT QUANTITY");
                keypad.loadValue(Double.parseDouble(preProduct.getPREPRODUCT_QTY()));
            }
        });
    viewHolder.Price.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Rashmi - 2018-8-7
            final String flag = preProduct.getPREPRODUCT_IS_CHANGE_PRICE();
            final double lGrnPrice,grossProfit,markupPer,sellingPrice;
            if(!preProduct.getPREPRODUCT_LGRNPRICE().equals("")) {
                lGrnPrice = Double.parseDouble(preProduct.getPREPRODUCT_LGRNPRICE());//cost price
            }else{
                lGrnPrice = 0.0;
            }
            if(!preProduct.getPREPRODUCT_PRICE().equals("")){
                sellingPrice = Double.parseDouble(preProduct.getPREPRODUCT_PRICE());
            }else{
                sellingPrice = 0.0;
            }

            markupPer = Double.parseDouble(preProduct.getPREPRODUCT_MARKUP_PER());
           // grossProfit = sellingPrice - (lGrnPrice + (lGrnPrice*markupPer)/100);
            grossProfit =  (lGrnPrice + (lGrnPrice*markupPer)/100);
            //if change price flag is 1 cannot change price -info by samanvi 2018-8-7
            if(preProduct.getPREPRODUCT_IS_CHANGE_PRICE().equals("0")) {
                CustomKeypadDialogPrice keypadPrice = new CustomKeypadDialogPrice(context, true, new CustomKeypadDialogPrice.IOnOkClickListener() {
                    @Override
                    public void okClicked(double value) {
            //price cannot be changed less than gross profit
                        if(value >= grossProfit) {
                        //  save changed price
                            new PreProductDS(context).updateProductPrice(preProduct.getPREPRODUCT_ITEMCODE(), String.valueOf(value));
                        //  value should be set for another variable in preProduct
                        //  preProduct.setPREPRODUCT_PRICE(String.valueOf(value));
                            preProduct.setPREPRODUCT_CHANGED_PRICE(String.valueOf(value));
                            viewHolder.Price.setText(preProduct.getPREPRODUCT_CHANGED_PRICE());
                        }else{
                            Toast.makeText(context,"Gross Profit Exceeded",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                keypadPrice.show();

                keypadPrice.setHeader("CHANGE PRICE");
//                if(preProduct.getPREPRODUCT_CHANGED_PRICE().equals("0")){
                keypadPrice.loadValue(Double.parseDouble(preProduct.getPREPRODUCT_PRICE()));
//                }else {
//                    keypadPrice.loadValue(Double.parseDouble(preProduct.getPREPRODUCT_CHANGED_PRICE()));
//                }
            }else{
                Toast.makeText(context,"Price cannot be change..",Toast.LENGTH_LONG).show();
            }
        }
    });
        viewHolder.Discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new SharedPref(context).getGlobalVal("PrekeyPayType").equalsIgnoreCase("credit")){
                    CustomKeypadDialogDiscount discountKeypad = new CustomKeypadDialogDiscount(context, true, new CustomKeypadDialogDiscount.IOnOkClickListener() {
                        @Override
                        public void okClicked(double value) {

                            new PreProductDS(context).updateProductDiscount(preProduct.getPREPRODUCT_ITEMCODE(), String.valueOf(value));
                            preProduct.setPREPRODUCT_DISCOUNT(String.valueOf(value));
                            viewHolder.Discount.setText(preProduct.getPREPRODUCT_DISCOUNT());
                        }
                    });
                discountKeypad.show();

                discountKeypad.setHeader("ENTER DISCOUNT");
//                if(preProduct.getPREPRODUCT_CHANGED_PRICE().equals("0")){
                discountKeypad.loadValue(Double.parseDouble(preProduct.getPREPRODUCT_DISCOUNT()));
            }else{
                    //should pass debtor credit discount of customer
//                    new PreProductDS(context).updateProductDiscount(preProduct.getPREPRODUCT_ITEMCODE(), new SharedPref(context).getGlobalVal("PrekeyPayType"));
//                    preProduct.setPREPRODUCT_DISCOUNT(new SharedPref(context).getGlobalVal("PrekeyCreditDis"));
//                    viewHolder.Discount.setText(preProduct.getPREPRODUCT_DISCOUNT());
                    Toast.makeText(context,"Credit discount already applied",Toast.LENGTH_LONG).show();
                }
            }
        });
     /*   viewHolder.lblQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    preText = viewHolder.lblQty.getText().toString();
                else
                    preText = null;
            }
        });*/


        return convertView;
    }

    @Override
    public int getItemViewType(final int position) {
        return position;
    }

    private static class ViewHolder {
        LinearLayout lnStripe;
        TextView itemCode;
        TextView ItemName;
        TextView Price;
        TextView Discount;
        TextView lblQty;
        ImageButton btnPlus;
        ImageButton btnMinus;
    }


}
