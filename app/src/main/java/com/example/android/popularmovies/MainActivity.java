package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.NetworkUtils;

import org.w3c.dom.Text;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements PopularMovieAdapter.PopularMovieItemClickListener {

    private static final String TAG = "MainActivity";
    private final static int VISIBLE_THRESHOLD = 5;
    private final static int WIDTH_IMAGE = 185;

    private RecyclerView recyclerView;
    private PopularMovieAdapter adapter;
    private TextView error;
    private ProgressBar progressBar;

    private boolean isLoading = false;
    private String sortBy = NetworkUtils.SORT_BY_POPULARITY;
    private int page = 1;
    private int totalPages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView)findViewById(R.id.poster_grid);

        //provide an indicative width, in pixels, to the FlexibleGridLayoutManager object,
        //so that it can determine the number of column to use, based on screen size
        int pixels = Math.round(WIDTH_IMAGE*getResources().getDisplayMetrics().density);
        final GridLayoutManager layoutManager = new FlexibleGridLayoutManager(this, pixels);
        recyclerView.setLayoutManager(layoutManager);

        //error textView e progressBar
        error = (TextView)findViewById(R.id.error_view);
        progressBar = (ProgressBar)findViewById(R.id.progress_view);

        //adapter
        new InitAdapterAsyncTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==R.id.action_popular) {
            if (!sortBy.equals(NetworkUtils.SORT_BY_POPULARITY)) {
                sortBy = NetworkUtils.SORT_BY_POPULARITY;
                new UpdateAdapterAsyncTask().execute(sortBy);
            }

            return true;
        }


        if(id==R.id.action_rated) {
            if (!sortBy.equals(NetworkUtils.SORT_BY_RANKING)) {
                sortBy = NetworkUtils.SORT_BY_RANKING;
                new UpdateAdapterAsyncTask().execute(sortBy);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //when a film is selected, show DetailActivity
    @Override
    public void onClick(int id) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, id);
        startActivity(intent);
    }

    //show movie posters
    private void showData(){
        recyclerView.setVisibility(View.VISIBLE);
        error.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    //data loading
    private void showProgressBar(){
        recyclerView.setVisibility(View.INVISIBLE);
        error.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    //show error
    private void showError(){
        recyclerView.setVisibility(View.INVISIBLE);
        error.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    //create adapter and complete the initialization
    private class InitAdapterAsyncTask extends AsyncTask<Void, Void, Void>{

        private List<Movie> movies;

        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Pair<Integer, List<Movie>> pair = NetworkUtils.getSortedMoviesPosterAndPages(NetworkUtils.SORT_BY_POPULARITY);
                totalPages = pair.first;
                movies = pair.second;
            }catch (Exception e){
                Log.e(TAG,"Error during initialization");
                e.printStackTrace();
                movies = null;
            }
                return  null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(movies==null) {
                showError();
                return;
            }

            try {
                //create adapter and set it in recycler view
                adapter = new PopularMovieAdapter(movies, MainActivity.this);
                recyclerView.setAdapter(adapter);
                showData();

                //load more items
                //data item 1 column, load item 2 column
                final GridLayoutManager layoutManager = (GridLayoutManager)recyclerView.getLayoutManager();

                layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        switch(adapter.getItemViewType(position)){
                            case PopularMovieAdapter.TYPE_LOADER:
                                return 2;
                            case PopularMovieAdapter.TYPE_DATA:
                                return 1;
                            default:
                                return -1;
                        }
                    }
                });

                //load more items
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        int totalItemCount = layoutManager.getItemCount();
                        int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                        if (!isLoading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD) &&
                                page < totalPages) {
                            isLoading = true;
                            new Handler().post(new Runnable(){
                                public void run(){
                                    new LoaderAdapterAsyncTask().execute();  //set a delayed load of data
                                }
                            });

                        }
                    }
                });
            }catch (Exception e){
                Log.e(TAG,"Error during initialization");
                e.printStackTrace();
                showError();
            }
        }
    }

    //load more movies
    private class LoaderAdapterAsyncTask extends AsyncTask<Void, Void, Void> {

        private List<Movie> movies;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                page++;
                movies = NetworkUtils.getSortedMoviesPoster(sortBy, page);
            } catch (Exception e) {
                Log.e(TAG,"Error while loading more movies");
                e.printStackTrace();
                movies = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(movies==null) {
                showError();
                isLoading = false;
                return;
            }

            try {
                isLoading = false;
                adapter.addMovies(movies);
                if(page==totalPages)
                    adapter.notifyEnd();//notify all pages has been loaded
            } catch (Exception e) {
                Log.e(TAG,"Error while loading more movies");
                e.printStackTrace();
                showError();
            }
        }
    }

    //manage the change of sorting criteria
    private class UpdateAdapterAsyncTask extends AsyncTask<String, Void, Void> {

        private List<Movie> movies;

        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                Pair<Integer, List<Movie>> pair = NetworkUtils.getSortedMoviesPosterAndPages(params[0]);
                totalPages = pair.first;
                movies = pair.second;
            } catch (Exception e) {
                Log.e(TAG,"Error while changing sorting criteria");
                e.printStackTrace();
                movies = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if(movies==null) {
                showError();
                page = 1;
                return;
            }

            try {
                adapter.setMoviesArray(movies);
                page = 1;
                showData();
            } catch (Exception e) {
                Log.e(TAG,"Error while changing sorting criteria");
                e.printStackTrace();
                showError();
            }
        }
    }
}
