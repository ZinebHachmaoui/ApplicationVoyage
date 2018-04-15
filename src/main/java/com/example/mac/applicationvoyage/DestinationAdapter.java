package com.example.mac.applicationvoyage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DestinationAdapter extends BaseAdapter{


    Context contextForAdapter;
    private JSONArray jsonArray;
    private JSONArray database;
    private int memoryLength;
    LayoutInflater inflater;

    public class ViewHolder {
       // TextView tvType;
        TextView tvDistance;
        TextView tvDisplay;
        ImageView preview;
        RatingBar stars;
    }

    public DestinationAdapter(Context context, JSONArray objects) {
        inflater = LayoutInflater.from(context);
        this.database = objects;
        memoryLength = 0;
        Synch.testPOI = true;
        Synch.testParcours = true;
        Synch.testCity = true;

        jsonArray = UpdateFilter();

        memoryLength = database.length();
        contextForAdapter = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            Log.v("DestinationAdapter", "convertView is null");
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item, parent, false);

           // holder.tvType = (TextView) convertView.findViewById(R.id.txtType);
            holder.tvDistance = (TextView) convertView.findViewById(R.id.Distance);
            holder.tvDisplay = (TextView) convertView.findViewById(R.id.Display);
            holder.preview = (ImageView) convertView.findViewById(R.id.ListImage);
            holder.stars=(RatingBar) convertView.findViewById( R.id.stars ) ;
            convertView.setTag(holder);
        } else {
            Log.v("DestinationAdapter", "convertView is not null");
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) jsonArray.get(position);
            holder.tvDisplay.setText(jsonObject.getString("display"));
            holder.stars.setRating( jsonObject.getInt( "stars" ) );
            if (jsonObject.getString("type").equals("POI") || jsonObject.getString("type").equals("PARCOURS") ) {
                holder.tvDistance.setText(contextForAdapter.getResources().getString(R.string.A) + " " + jsonObject.getString("distance") + " " + contextForAdapter.getResources().getString(R.string.B));
            } else {
                holder.tvDistance.setText("  ");
            }
            if (jsonObject.getString("type").equals("POI") || jsonObject.getString("type").equals("CITY") || jsonObject.getString("type").equals("PARCOURS") ||jsonObject.getString("type").equals("ACTIVITY") ) {
                ImageLoader mImageLoader;
                mImageLoader = Synch.getInstance(contextForAdapter).getImageLoader();
                mImageLoader.get(jsonObject.getString("media"), ImageLoader.getImageListener(holder.preview, android.R.drawable.ic_popup_sync, android.R.drawable.ic_dialog_alert));
            } else {
                holder.preview.setImageDrawable(contextForAdapter.getResources().getDrawable(android.R.drawable.ic_notification_clear_all));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v("DestinationAdapter", "Error in getView");
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public JSONObject getItem(int position) {
        JSONObject object = null;
        try {
            object = (JSONObject) jsonArray.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.v("DestinationAdapter", "Error getItem");
        }
        return object;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public JSONArray UpdateFilter() {
        JSONArray FilteredArrayNames = new JSONArray();
        int start;
        if ( database.length() != memoryLength) {
            start = memoryLength;
        } else {
            start = 0;
        }
        for (int i = start; i < database.length(); i++) {
            try {
                String dataNames = ((JSONObject) database.get(i)).getString("type");
                if ( Synch.testPOI && dataNames.equals("POI") || Synch.testParcours && dataNames.equals("PARCOURS") || Synch.testCity && dataNames.equals("CITY") )  {
                    FilteredArrayNames.put(database.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.v("DestinationAdapter", "Error in Filtering Element (nbr " + i +") to array");
            }
        }

        return FilteredArrayNames;
    }

    @Override
    public void notifyDataSetChanged() {
        JSONArray tmp = UpdateFilter();
        if (memoryLength != database.length()) {
            for(int i=0; i<tmp.length(); i++) {
                try {
                    jsonArray.put(tmp.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.v("DestinationAdapter", "Error in NotifyDataSetChanged Element (nbr " + i +") to array");
                }
            }
            memoryLength = database.length();
        } else {
            jsonArray = tmp;
        }
        super.notifyDataSetChanged();
    }
}
