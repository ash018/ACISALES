package com.example.akash.acisales;

/**
 * Created by Akash on 6/13/2017.
 */





import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.view.LayoutInflater;

import android.widget.ArrayAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.example.akash.acisales.R;

public class CustomAdapter extends ArrayAdapter {
    Context context;
    private ArrayList<HashMap<String,String>> saleslist;

    private int[] mTo;
    private String[] mFrom;
    private List<? extends Map<String, ?>> mData;

    private int mResource;
    private int mDropDownResource;
    private SimpleAdapter.ViewBinder mViewBinder;

    public CustomAdapter(Context context,
                         ArrayList<HashMap <String,String>> data, int resource, String[] from, @IdRes int[] to) {

        super (context, resource, data );

        this.context = context;
        saleslist=new ArrayList<> ();
        saleslist.addAll (data);
        mData = data;
        mResource = mDropDownResource = resource;
        mFrom = from;
        mTo = to;

        
    }
    
    private void bindView(int position, View view){
        final Map dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }

        final SimpleAdapter.ViewBinder binder = mViewBinder;
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else if (v instanceof TextView) {
                            // Note: keep the instanceof TextView check at the bottom of these
                            // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                            setViewText((TextView) v, text);
                        } else {
                            throw new IllegalStateException(v.getClass().getName() +
                                    " should be bound to a Boolean, not a " +
                                    (data == null ? "<unknown type>" : data.getClass()));
                        }
                    } else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the bottom of these
                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView) {
                        if (data instanceof Integer) {
                            //setViewImage((ImageView) v, (Integer) data);
                        } else {
                            setViewImage((ImageView) v, text);
                        }
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }

    public void setViewText(TextView v, String text) {
        v.setText ( text );
    }

    public void setViewImage(ImageView v, String value) {
        try {
            v.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            v.setImageURI( Uri.parse(value));
        }
    }

    public SimpleAdapter.ViewBinder getViewBinder() {
        return mViewBinder;
    }

    public void setViewBinder(SimpleAdapter.ViewBinder viewBinder) {
        mViewBinder = viewBinder;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.list_item, null);
            final ViewHolder holder = new ViewHolder();
            //final ViewHolder holder2 = new ViewHolder ();


            holder.businessname = (TextView) view.findViewById ( R.id.businessname );
            holder.businessname.setTag ( saleslist.get(position) );


            holder.curr = (TextView) view.findViewById ( R.id.curr );
            holder.curr.setTag ( saleslist.get(position) );

            holder.SLSCurLMSD = (TextView) view.findViewById ( R.id.SLSCurLMSD );
            holder.SLSCurLMSD.setTag ( saleslist.get(position) );

            holder.SLSCurSPLY = (TextView) view.findViewById ( R.id.SLSCurSPLY );
            holder.SLSCurSPLY.setTag ( saleslist.get(position));

            holder.tvctotal = (TextView) view.findViewById(R.id.ctotal);
            holder.tvctotal.setTag(saleslist.get(position));

            holder.SLSCumm = (TextView) view.findViewById ( R.id.SLSCumm );
            holder.SLSCumm.setTag ( saleslist.get(position));





            view.setTag(holder);
        } else {
            view = convertView;

            ((ViewHolder) view.getTag()).businessname.setTag(saleslist
                    .get(position));

            ((ViewHolder) view.getTag()).curr.setTag(saleslist
                    .get(position));

            ((ViewHolder) view.getTag()).SLSCurLMSD.setTag(saleslist
                    .get(position));

            ((ViewHolder) view.getTag()).SLSCurSPLY.setTag(saleslist
                    .get(position));


            ((ViewHolder) view.getTag()).tvctotal.setTag(saleslist
                    .get(position));

            ((ViewHolder) view.getTag()).SLSCumm.setTag(saleslist
                    .get(position));


        }
        //bindView (  );
        ViewHolder holder = (ViewHolder) view.getTag();

        HashMap <String,String> el = (HashMap<String, String>) holder.tvctotal.getTag();
        holder.businessname.setText (el.get("businessname"));
        holder.curr.setText (el.get("curr"));
        holder.SLSCurLMSD.setText (el.get("SLSCurLMSD"));
        String SLSCurLMSDStr = holder.SLSCurLMSD.getText().toString ();
        float SLSCurLMSDFloat = Float.parseFloat ( SLSCurLMSDStr );
        if(SLSCurLMSDFloat > 1.00) {
            holder.SLSCurLMSD.setBackgroundDrawable ( context.getResources ().getDrawable ( R.drawable.greentableborder ) );
            holder.SLSCurLMSD.setTextColor (context.getResources ().getColor (R.color.black));
        }
        else {
            //holder.SLSCurLMSD.setBackgroundResource ( R.color.red );
            TextView tvLMSD = (TextView) view.findViewById ( R.id.SLSCurLMSD );
            tvLMSD.setTextColor (context.getResources ().getColor (  R.color.white ) );
            tvLMSD.setBackgroundDrawable ( context.getResources ().getDrawable ( R.drawable.redtableborder ) );
        }


        holder.SLSCurSPLY.setText ( el.get("SLSCurSPLY") );
        String SLSCurSPLYStr = holder.SLSCurSPLY.getText().toString ();
        float SLSCurSPLYFloat = Float.parseFloat ( SLSCurSPLYStr );
        if(SLSCurSPLYFloat > 1.00) {
            holder.SLSCurSPLY.setBackgroundDrawable (context.getResources ().getDrawable ( R.drawable.greentableborder ));
            holder.SLSCurSPLY.setTextColor (context.getResources ().getColor (R.color.black));
        }
        else {

            holder.SLSCurSPLY.setBackgroundDrawable (context.getResources ().getDrawable ( R.drawable.redtableborder ));
            holder.SLSCurSPLY.setTextColor ( context.getResources().getColor (R.color.white) );

        }

            holder.tvctotal.setText(el.get ( "ctotal" ));
            holder.SLSCumm.setText ( el.get("SLSCumm") );





        return view;
    }

    @Override
    public int getCount() {
        return saleslist.size();
    }

    @Override
    public Object getItem(int position) {
        return saleslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        TextView businessname;
        TextView curr;
        TextView tvctotal;
        TextView SLSCurLMSD;
        TextView SLSCurSPLY;
        TextView SLSCumm;
        ImageView checkedBrand;
    }

}
