package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    private View dataView;
    private TextView error;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //data, progress bar and error
        dataView = findViewById(R.id.detail_data);
        error = (TextView)findViewById(R.id.detail_error);
        progressBar = (ProgressBar)findViewById(R.id.detail_progress);

        Intent intent = getIntent();

        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            int id = getIntent().getIntExtra(Intent.EXTRA_TEXT, -1);
            RetrieveDetailAsyncTask task = new RetrieveDetailAsyncTask();
            task.execute(id);
        }

    }

    private void showData(){
        dataView.setVisibility(View.VISIBLE);
        error.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void showProgressBar(){
        dataView.setVisibility(View.INVISIBLE);
        error.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showError(){
        dataView.setVisibility(View.INVISIBLE);
        error.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    //retrieve a movie details
    private class RetrieveDetailAsyncTask extends AsyncTask<Integer,Void, Movie>{

        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected Movie doInBackground(Integer... params) {
            Movie movie = null;
            try{
                movie = NetworkUtils.getMovieDetailById(params[0]);
            }catch (Exception e){
                Log.e(TAG,"Error while retrieving movie details");
                e.printStackTrace();
                return null;
            }
            return movie;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            super.onPostExecute(movie);

            if(movie != null) {

                TextView titleView = (TextView) findViewById(R.id.detail_title);
                ImageView imageView = (ImageView) findViewById(R.id.detail_image);
                TextView ratingView = (TextView) findViewById(R.id.detail_rating);
                TextView releaseView = (TextView) findViewById(R.id.detail_release);
                TextView plotView = (TextView) findViewById(R.id.detail_plot);

                titleView.setText(movie.getTitle());
                Picasso.with(DetailActivity.this).load(NetworkUtils.BASE_POSTER_URL + movie.getPosterPath()).into(imageView);
                ratingView.setText(String.format("%.1f/10", movie.getRating()));
                releaseView.setText(movie.getRelease());
                plotView.setText(movie.getPlot());

                showData();
            }
            else
                showError();
        }
    }
}
