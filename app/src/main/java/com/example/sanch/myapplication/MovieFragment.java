package com.example.sanch.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.sanch.myapplication.data.MovieDbHelper;
import com.example.sanch.myapplication.model.MovieItem;
import com.example.sanch.myapplication.data.MovieDbContract;
import com.example.sanch.myapplication.adapters.MovieAdapter;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieFragment extends Fragment {
    private MovieAdapter movieAdapter;
    private MovieItem[] mMovies;
    private GridView mGridView;
    private boolean mDualPane;
    private int mCurPosition = 0;

    static final String MOVIES_KEYS = "movies";
    static final String SORT_KEY = "sorting key";
    private String POPULAR_MOVIES = "popularity.desc";
    private String HIGHLY_RATED = "vote_average.desc";
    private static final String FAVORITES = "favorites";
    private String mSort_key = POPULAR_MOVIES;
    private final String CUR_INDEX = "cur_index";
    private static final String[] MOVIE_COLUMNS = {
            MovieDbContract.MovieEntry._ID,
            MovieDbContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieDbContract.MovieEntry.COLUMN_TITLE,
            MovieDbContract.MovieEntry.COLUMN_POSTER,
            MovieDbContract.MovieEntry.COLUMN_OVERVIEW,
            MovieDbContract.MovieEntry.COLUMN_RATING,
            MovieDbContract.MovieEntry.COLUMN_REL_DATE
    };

    public MovieFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.movie_detail_container);
        mDualPane = detailsFrame != null;
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurPosition = savedInstanceState.getInt(CUR_INDEX, 0);
        }
    }

    /*
    The system calls this when it's time for the fragment to draw its user interface for the first time.
     To draw a UI for your fragment, you must return a View from this method that is the root of your fragment's layout.
      You can return null if the fragment does not provide a UI.
       */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = new Bundle();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        movieAdapter = new MovieAdapter(getActivity(), new ArrayList<MovieItem>());

        mGridView.setAdapter(movieAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {// start the details screen
                final MovieItem movie = movieAdapter.getItem(position);
                ((Callback) getActivity()).onItemSelected(movie, position, mSort_key);
               /*  final  Intent intent = new Intent(getActivity(),DetailActivity.class);
                 intent.putExtra(MovieItem.EXTRA_MOVIES,movie);
                 startActivity(intent);
               */
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SORT_KEY)) {
                mSort_key = savedInstanceState.getString(SORT_KEY);
            }
            if (savedInstanceState.containsKey(MOVIES_KEYS)) {
                mMovies = (MovieItem[]) savedInstanceState.getParcelableArray(MOVIES_KEYS);

                if (mMovies != null) {
                    movieAdapter.clear();
                    movieAdapter.notifyDataSetChanged();
                    movieAdapter.setMovieData(mMovies);
                }
            } else {
                updateMovies(mSort_key);
            }
        } else {
            updateMovies(mSort_key);
        }
        return rootView;
    }

    private void updateMovies(String sort_by) {
        if (!sort_by.contentEquals(FAVORITES)) {
            FetchMovieDB MovieTask = new FetchMovieDB();
            MovieTask.execute(sort_by);
        } else {
            new FetchFavoriteMoviesTask(getActivity()).execute();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (!mSort_key.contentEquals(POPULAR_MOVIES)) {
            savedInstanceState.putString(SORT_KEY, mSort_key);
        }
        if (mMovies != null) {
            savedInstanceState.putParcelableArray(MOVIES_KEYS, mMovies);
        }
        savedInstanceState.putInt(CUR_INDEX, mCurPosition);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        void onItemSelected(MovieItem movie, int index, String mSort_key);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //  super.onRestoreInstanceState(savedInstanceState);
        mSort_key = savedInstanceState.getString(SORT_KEY);
        mMovies = (MovieItem[]) savedInstanceState.getParcelableArray(MOVIES_KEYS);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_sort_by_highest: {

                movieAdapter.clear();
                movieAdapter.notifyDataSetChanged();
                mSort_key = HIGHLY_RATED;
                updateMovies(mSort_key);
                return true;
            }
            case R.id.action_sort_by_popular: {

                movieAdapter.clear();
                movieAdapter.notifyDataSetChanged();
                mSort_key = POPULAR_MOVIES;
                updateMovies(mSort_key);
                return true;
            }
            case R.id.action_sort_by_favorite: {

                movieAdapter.clear();
                movieAdapter.notifyDataSetChanged();
                mSort_key = FAVORITES;
                updateMovies(mSort_key);
                return true;
            }
            case R.id.action_settings:

                return true;
            default:

                return super.onOptionsItemSelected(item);
        }
    }

    public class FetchMovieDB extends AsyncTask<String, Void, MovieItem[]> {
        final String LOG_TAG = FetchMovieDB.class.getSimpleName();

        @Override
        protected MovieItem[] doInBackground(String... params) {
            try {

                Log.v("Sort by key ..", params[0]);
                final String Movie_url = "http://api.themoviedb.org/3/discover/movie?";

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https").authority("api.themoviedb.org").appendPath("3")
                        .appendPath("discover").appendPath("movie")
                        .appendQueryParameter("sort_by", params[0])
                        .appendQueryParameter("api_key", getString(R.string.themoviesdb_api_key));

                URL url = new URL(builder.toString());
                Log.v("URL ....", builder.toString());
                // URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=57c8bf138eab4c78e72bc17cb9ab65e5");
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = client.newCall(request).execute();
                // Read the input stream into a String
                String inputStream = response.body().string();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                return parseJsonStr(inputStream);
            } catch (
                    IOException e
                    ) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieItem[] movies) {
            super.onPostExecute(movies);
            mMovies = movies;

            if (movies != null) {
                mMovies = movies;
                movieAdapter.setMovieData(mMovies);
                if (mDualPane) {
                    ((Callback) getActivity()).onItemSelected(mMovies[0], mCurPosition, mSort_key);
                }
            }
        }

        private MovieItem[] parseJsonStr(String movieJsonStr) {
            try {
                //Attributes of Json String
                final String M_PATH = "poster_path";
                final String M_TITLE = "original_title";
                final String M_ID = "id";
                final String M_OVERVIEW = "overview";
                final String M_USER_RATING = "vote_average";
                final String M_RELEASE_DATE = "release_date";

                //Convert Json String to Json Object
                JSONObject movieJsonObj = new JSONObject(movieJsonStr);
                //Get the Json Array
                JSONArray movieJsonArray = movieJsonObj.optJSONArray("results");
                MovieItem[] movieInfo = new MovieItem[movieJsonArray.length()];

                for (int i = 0; i < movieJsonArray.length(); i++) {
                    JSONObject post = movieJsonArray.optJSONObject(i);

                    movieInfo[i] = new MovieItem(post.getString(M_PATH),
                            post.getString(M_TITLE),
                            post.getString(M_OVERVIEW),
                            post.getString(M_USER_RATING),
                            post.getString(M_ID),
                            post.getString(M_RELEASE_DATE)
                    );
                    movieInfo[i].setPosterPath(post.getString(M_PATH));
                }
                return movieInfo;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class FetchFavoriteMoviesTask extends AsyncTask<Void, Void, List<MovieItem>> {
        private Context mContext;

        public FetchFavoriteMoviesTask(Context context) {
            mContext = context;
        }

        private List<MovieItem> getFavoriteMoviesDataFromCursor(Cursor cursor) {
            List<MovieItem> results = new ArrayList<>();
            //If no database table
            if(!cursor.moveToFirst())
                return  null;
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    MovieItem movie = new MovieItem(cursor);
                    results.add(movie);
                    Log.v("Movies added",movie.getTitle());
                } while (cursor.moveToNext());
                cursor.close();
            }
            else {
                results = null;
            }
            return results;
        }

        @Override
        protected List<MovieItem> doInBackground(Void... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    MovieDbContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
            return getFavoriteMoviesDataFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(List<MovieItem> movies) {
            if (movies != null) {
                if (movieAdapter != null) {
                    for (MovieItem movie :movies) {
                        movieAdapter.add(movie);
                    }

                    if(mDualPane) {
                        ((Callback) getActivity()).onItemSelected(mMovies[0], mCurPosition, FAVORITES);
                    }
                }

            }
            else
            {
                if(mDualPane) {
                    // No database table found return null;
                    Toast toastMsg = Toast.makeText(getActivity(),R.string.favorite_List_empty, Toast.LENGTH_LONG);
                    toastMsg.show();
                    ((Callback) getActivity()).onItemSelected(null, -1, FAVORITES);
                }
            }
        }
    }
}