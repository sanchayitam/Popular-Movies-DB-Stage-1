/**
 * This file implements Adapter class for accessing the movie data items.
 */
package com.example.sanch.myapplication.adapters;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sanch.myapplication.R;
import com.example.sanch.myapplication.model.MovieItem;
import com.squareup.picasso.Picasso;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieAdapter extends ArrayAdapter {
    private final Context mContext;
    private ArrayList<MovieItem> mMovieData = new ArrayList<>(); ;
    public int mItemSelected = -1;

    public MovieAdapter(Context mContext, ArrayList<MovieItem> movieData) {
        super(mContext,0,movieData);
        this.mContext = mContext;
        this.mMovieData = movieData;
    }

    public void setMovieData( MovieItem[] movieData){
    if(movieData != null){
        for (MovieItem movie : movieData) {
            add(movie);
        }
    }
}

    public void add(MovieItem object) {
        mMovieData.add(object);
        //data has been changed and any View reflecting the data set should refresh itself
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (mMovieData.size());
    }

    @Override
    public MovieItem getItem(int position) {
        return (mMovieData.get(position));
    }

    /*
    The ViewHolder design pattern enables you to access each list item view without the need for the look up,
       saving valuable processor cycles.It avoids frequent call of findViewById() during ListView scrolling,
       and that will make it smooth.
     */
    static class ViewHolder{
                ImageView imageView;
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
            //inflate the layout
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.moviedb_image, parent, false);
            //setup the View holder
            holder = new ViewHolder();
            //Assign imageview to Viewholder
            holder.imageView = (ImageView) row.findViewById(R.id.imageView_moviedb);
            //set the ViewHolder as tag of View.
            row.setTag(holder);
        }
        else
        {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            holder = (ViewHolder)row.getTag();
        }
        
       // Using Picasso to Fetch Movie Posters and Load them into View
        URL url = null;
        final MovieItem movie = mMovieData.get(position);
        final String IMG_URL = " http://image.tmdb.org/t/p/w185" +movie.getPosterPath();
          try {
                 url = new URL(IMG_URL);
                 Log.v("Poster Path ...", IMG_URL);
                 Picasso.with(mContext).load(String.valueOf(url)).error(R.drawable.ic_img_not_found). into(holder.imageView);
          } catch (MalformedURLException e) {
                e.printStackTrace();
          }

          return row;
        }
    }