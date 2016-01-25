package com.example.sanch.myapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;
import android.support.v7.widget.CardView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.example.sanch.myapplication.model.MovieItem;
import com.example.sanch.myapplication.model.Trailer;
import com.example.sanch.myapplication.model.Review;
import com.example.sanch.myapplication.adapters.MovieAdapter;
import com.example.sanch.myapplication.adapters.ReviewAdapter;
import com.example.sanch.myapplication.adapters.TrailerAdapter;
import com.example.sanch.myapplication.data.MovieDbContract;
import com.squareup.picasso.Picasso;
import android.widget.ShareActionProvider;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.os.AsyncTask;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment  {

    public static final String TAG = DetailActivityFragment.class.getSimpleName();
    static final String DETAIL_MOVIE = "DETAIL_MOVIE";
    static final String SORT_BY = "SORT_BY";
    static final String MOVIE_KEY = "MOVIE_KEY";
    private static final String FAVORITES = "favorites";

    private MovieItem mMovie;
    private TextView mTitle;
    private TextView mOverview;
    private TextView mDate;
    private TextView mVoteAverage;
    private ImageView mPosterImage;
    private ListView mTrailersView;
    private ListView mReviewsView;
    private Button mFavorite;
    private CardView mReviewsCardView;
    private CardView mTrailersCardView;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter mReviewAdapter;
    private Trailer mTrailer;
    private ShareActionProvider mShareActionProvider;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        rootView.setBackgroundColor(Color.WHITE);

        Bundle arguments = getArguments();

       if (arguments != null) {
            mMovie = arguments.getParcelable(DetailActivityFragment.DETAIL_MOVIE);

        }
        if(mMovie == null)return null;

       if(savedInstanceState == null)
       {
          if (mMovie != null) {
              String Key = mMovie.getId();
              new FetchTrailersTask().execute(Key);
              new FetchReviewsTask().execute(Key);
             }
         }
    /*    else {
           final Intent intent = getActivity().getIntent();
             if (intent != null && intent.hasExtra(DETAIL_MOVIE)) {
           mMovie = intent.getParcelableExtra(DETAIL_MOVIE);
         }*/
    //   }
       mTitle = (TextView) rootView.findViewById(R.id.detail_movie_title);
       mOverview = ((TextView) rootView.findViewById(R.id.detail_movie_plot));
       mVoteAverage = ((TextView) rootView.findViewById(R.id.detail_movie_user_rating));
       mDate = ((TextView) rootView.findViewById(R.id.detail_movie_release_date));
       mPosterImage = ((ImageView) rootView.findViewById(R.id.detail_poster_image_view));
       mTrailersView = (ListView) rootView.findViewById(R.id.detail_trailers);
       mReviewsView = (ListView) rootView.findViewById(R.id.detail_reviews);
       mReviewsCardView = (CardView) rootView.findViewById(R.id.reviews_cardview);
       mTrailersCardView = (CardView) rootView.findViewById(R.id.trailers_cardview);
       mFavorite = (Button) rootView.findViewById(R.id.favorite_button);

       mTrailerAdapter = new TrailerAdapter(getActivity(), new ArrayList<Trailer>());
       mTrailersView.setAdapter(mTrailerAdapter);

        //On displaying the favorite movies , disable the button in the detail view
        if((arguments.getString(DetailActivityFragment.SORT_BY)).contentEquals(FAVORITES)) {
            mFavorite.setVisibility(View.INVISIBLE);
        }
        else {
            mFavorite.setVisibility(View.VISIBLE);
        }

       mFavorite.setOnClickListener(new View.OnClickListener() {
                     public void onClick(View v) {
                         cacheMovies();
                     }
                 });

       mTrailersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Trailer trailer = mTrailerAdapter.getItem(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                    startActivity(intent);
                }
            });
            mReviewAdapter = new ReviewAdapter(getActivity(), new ArrayList<Review>());
            mReviewsView.setAdapter(mReviewAdapter);
            setRetainInstance(true);
            if (mMovie != null) {
                mTitle.setText(mMovie.getTitle());
                final String IMG_URL = " http://image.tmdb.org/t/p/w185" + mMovie.getPosterPath();
                URL url = null;
                try {
                     url = new URL(IMG_URL);
                  //  Log.v("Poster Path ...", IMG_URL);
                     Picasso.with(getActivity()).load(String.valueOf(url)).error(R.drawable.ic_img_not_found).into(mPosterImage);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                mDate.setText(mMovie.getRelease_date());
                mVoteAverage.setText(mMovie.getUser_rating() + "/10");
                mOverview.setText(mMovie.getOverview());
        }

        return rootView;
    }

    /* The system calls this function when creating the fragment.
     *  We initialize essential components of the fragment that we want to
     * retain when the fragment is paused or stopped, then resumed. */
        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Caches the favorite movies in the database
     */
    private void cacheMovies() {
        final long movieId = Long.parseLong(mMovie.getId());
        final Uri movieUri = MovieDbContract.MovieEntry.CONTENT_URI;
        //Retrieving data from SQLite databases in Android is done using Cursors.
        final Cursor cursor = getActivity().getContentResolver().query(movieUri, null, null, null, null);
        if (cursor != null) {
         int result =  AsynDbUtility(mMovie.getId());
            // nothing in database, add it
            //data to be inserted in the table
             if(result == 0){
                final ContentValues movieValues = new ContentValues();

                movieValues.put(MovieDbContract.MovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
                movieValues.put(MovieDbContract.MovieEntry.COLUMN_TITLE, mMovie.getTitle());
                movieValues.put(MovieDbContract.MovieEntry.COLUMN_OVERVIEW, mMovie.getOverview());
                movieValues.put(MovieDbContract.MovieEntry.COLUMN_POSTER,
                        mMovie.getPosterPath());
                movieValues.put(MovieDbContract.MovieEntry.COLUMN_RATING,
                        mMovie.getUser_rating());
                movieValues.put(MovieDbContract.MovieEntry.COLUMN_REL_DATE,
                        mMovie.getRelease_date());

                getActivity().getContentResolver()
                        .insert(MovieDbContract.MovieEntry.CONTENT_URI, movieValues);
                Toast toastMsg = Toast.makeText(getActivity(), getString(R.string.added_to_favorites), Toast.LENGTH_SHORT);
                toastMsg.show();
            }
            cursor.close();
        }
    }

   // check if movie is in favorites
   public Integer AsynDbUtility (final String movieID) {
           int numRows = 0;
           Cursor cursor = getActivity().getContentResolver().query(
                           MovieDbContract.MovieEntry.CONTENT_URI,
                           null,   // projection
                           MovieDbContract.MovieEntry.COLUMN_MOVIE_ID + " = ?", // selection
                           new String[]{movieID},   // selectionArgs
                           null    // sort order
               );
           numRows = cursor.getCount();
           cursor.close();
           // if it is in favorites db, no insertions
           if (numRows == 1) {
            /*       getActivity().getContentResolver().delete(
                           MovieDbContract.MovieEntry.CONTENT_URI,
                           MovieDbContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                           new String[]{(movieID)}
                   );
            */
               Toast toastMsg = Toast.makeText(getActivity(), getString(R.string.deleted_from_favorites), Toast.LENGTH_SHORT);
               toastMsg.show();
               }

       return numRows;
   }

        private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie.getTitle() + " " +
                "http://www.youtube.com/watch?v=" + mTrailer.getKey());
        return shareIntent;
    }

    public class FetchTrailersTask extends AsyncTask<String, Void, List<Trailer>> {
        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

   @Override
   protected List<Trailer> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            Log.v("The key ..", params[0]);
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos";
                String inputStream = null;

                Uri builtUrl = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", getString(R.string.themoviesdb_api_key))
                        .build();
                try {
                    URL url = new URL(builtUrl.toString());
                    Log.v("URL ....", builtUrl.toString());
                    // URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=57c8bf138eab4c78e72bc17cb9ab65e5");
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response response = client.newCall(request).execute();
                    // Read the input stream into a String
                    inputStream = response.body().string();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    return null;
                }
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                return getTrailerDataFromJson(inputStream);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

   private List<Trailer> getTrailerDataFromJson(String jsonStr) throws JSONException {
            JSONObject trailerJson = new JSONObject(jsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray("results");
            List<Trailer> results = new ArrayList<>();

            for (int i = 0; i < trailerArray.length(); i++) {
                JSONObject trailer = trailerArray.getJSONObject(i);
                // Only show Trailers which are on Youtube
                if (trailer.getString("site").contentEquals("YouTube")) {
                    Trailer trailerObj = new Trailer(trailer);
                    results.add(trailerObj);
                }
            }

            return results;
        }

   @Override
   protected void onPostExecute(List<Trailer> trailers) {
            if (trailers != null && trailers.size() > 0) {
             mTrailersCardView.setVisibility(View.VISIBLE);
                if (mTrailerAdapter != null) {
                    mTrailerAdapter.clear();
                    for (Trailer trailer : trailers) {
                        mTrailerAdapter.add(trailer);
                    }
                }
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareMovieIntent());
                }
            }
        }
    }

   public class FetchReviewsTask extends AsyncTask<String, Void, List<Review>> {
        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

   @Override
   protected List<Review> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
           }
            Log.v("The key ..", params[0]);
            try {
                final String BASE_URL = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews";
                String inputStream = null;
                Uri builtUrl = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter("api_key", getString(R.string.themoviesdb_api_key))
                        .build();
                try {
                    URL url = new URL(builtUrl.toString());

                    Log.v("URL ....", builtUrl.toString());
                    // URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=57c8bf138eab4c78e72bc17cb9ab65e5");
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response response = client.newCall(request).execute();
                    // Read the input stream into a String
                    inputStream = response.body().string();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    return null;
                }
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                return getReviewDataFromJson(inputStream);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

   private List<Review> getReviewDataFromJson(String jsonStr) throws JSONException {
            JSONObject reviewJson = new JSONObject(jsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray("results");
            List<Review> results = new ArrayList<>();

            for (int i = 0; i < reviewArray.length(); i++) {
                JSONObject review = reviewArray.getJSONObject(i);
                results.add(new Review(review));
            }

            return results;
        }

   @Override
   protected void onPostExecute(List<Review> reviews) {
          if (reviews != null && reviews.size() > 0) {
              mReviewsCardView.setVisibility(View.VISIBLE);
              if (mReviewAdapter != null) {
                  mReviewAdapter.setReviews(reviews);
              }
          }
      }
   }
}

