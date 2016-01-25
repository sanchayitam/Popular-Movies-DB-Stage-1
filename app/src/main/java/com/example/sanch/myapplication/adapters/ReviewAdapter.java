/**
 * This file implements Adapter class for accessing the review data items.
 */
package com.example.sanch.myapplication.adapters;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.sanch.myapplication.model.Review;
import com.example.sanch.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanch on 1/13/2016.
 */
public class ReviewAdapter extends BaseAdapter {
    private final Context mContext;
    private List<Review> mObject = new ArrayList<>();

    public ReviewAdapter(Context mContext, List<Review> object) {
        this.mContext = mContext;
        this.mObject = object;
    }

    public void setReviews( List<Review> object){
        if(object != null){
            for (Review obj :object) {
                add(obj);
            }
        }
    }

    public void add(Review object) {
        mObject.add(object);
        notifyDataSetChanged();;
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
    public Review getItem(int position) {
        return (mObject.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

   static class ViewHolder {
        public final TextView authorView;
        public final TextView contentView;

        public ViewHolder(View view) {
            authorView = (TextView) view.findViewById(R.id.review_author);
            contentView = (TextView) view.findViewById(R.id.review_content);
        }
    }

    /* Gets a view to display the review data at the specified position in the data set.
      Parameters:
      position : The position of the review item within the adapter's data set of the item whose view we want
      rootview : Use old view if possible
      parent   : The parent that the view which get eventually attached to.

      Returns the view corresponding to the data item at the specified position
     */
    @Override
    public View getView(int position, View rootView, ViewGroup parent) {
        View row = rootView;
        ViewHolder holder ;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.review_item, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)row.getTag();
        }

        final Review review = getItem(position);
        holder.authorView.setText(review.getAuthor());
        holder.contentView.setText(Html.fromHtml(review.getContent()));

        return row;
    }
   }
