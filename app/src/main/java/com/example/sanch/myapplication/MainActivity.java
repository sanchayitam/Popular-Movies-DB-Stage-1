package com.example.sanch.myapplication;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import com.example.sanch.myapplication.model.MovieItem;

public class MainActivity extends AppCompatActivity  implements MovieFragment.Callback{
    // variable to denote a multi-pane UI
    private boolean mTwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

        } else {
            mTwoPane = false;
        }
    }

    @Override
    public void onItemSelected(MovieItem movie, int index , String sort_key) {
           if (mTwoPane) {
           Bundle arguments = new Bundle();
           arguments.putParcelable(DetailActivityFragment.DETAIL_MOVIE, movie);
           arguments.putString(DetailActivityFragment.SORT_BY, sort_key);

           DetailActivityFragment fragment = new DetailActivityFragment();
           fragment.setArguments(arguments);

                if (index == 0) {
                    //add a fragment by specifying the fragment to add and the view in which to insert it.
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.movie_detail_container, fragment, DetailActivityFragment.TAG)
                            .commit();
                } else {
                    //replace  whatever is in the container view with this fragment
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment, DetailActivityFragment.TAG)
                            .commit();
                    ;
                }

        } else {
               //for single pane
                Intent intent = new Intent(this, DetailActivity.class)
                .putExtra(DetailActivityFragment.DETAIL_MOVIE, movie)
                .putExtra(DetailActivityFragment.SORT_BY, sort_key);
                startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}