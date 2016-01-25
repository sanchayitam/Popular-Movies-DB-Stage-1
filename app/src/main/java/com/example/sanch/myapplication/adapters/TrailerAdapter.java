/**
 * This file implements Adapter class for accessing the trailer data items.
 */

package com.example.sanch.myapplication.adapters;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.sanch.myapplication.model.Trailer;
import com.example.sanch.myapplication.R;
import com.squareup.picasso.Picasso;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends BaseAdapter{
    private final Context mContext;
    private List<Trailer> mObject = new ArrayList<>();

    public TrailerAdapter(Context mContext, List<Trailer> object) {
        this.mContext = mContext;
        this.mObject = object;
    }

    public void setTrailers( List<Trailer> object){
        if(object != null){
            for (Trailer obj :object) {
                add(obj);
            }
        }
    }

    public void add(Trailer object) {
        mObject.add(object);
        notifyDataSetChanged();
    }

    public  void clear(){
        mObject.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (mObject.size());
    }

    @Override
    public Trailer getItem(int position) {
        return (mObject.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

     static class ViewHolder {
        public final ImageView imageView;
        public final TextView nameView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.trailer_image);
            nameView = (TextView) view.findViewById(R.id.trailer_name);
        }
    }

    /* Gets a view to display the trailer data at the specified position in the data set.
      Parameters:
      position : The position of the trailer item within the adapter's data set of the item whose view we want
      rootview : Use old view if possible
      parent   : The parent that the view which get eventually attached to.

      Returns the view corresponding to the data item at the specified position
     */
    @Override
    public View getView(int position, View rootView, ViewGroup parent) {
        View row = rootView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.trailer_item, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)row.getTag();
        }

        // Using Picasso to Fetch Movie Posters and Load them into View
        final Trailer trailer = getItem(position);
        String yt_thumbnail_url = "http://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg";
        holder.nameView.setText(trailer.getName());
        Picasso.with(mContext).load(String.valueOf(yt_thumbnail_url)). into(holder.imageView);

        return row;
    }
}
