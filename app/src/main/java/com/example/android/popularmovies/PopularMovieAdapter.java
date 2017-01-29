package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by v.dipaolo on 23/01/2017.
 */

//va cambiata la struttura dati che memorizza i film. Non è un array ma un arrayList
public class PopularMovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG = "PopularMovieAdapter";
    public static final int TYPE_DATA = 0;
    public static final int TYPE_LOADER = 1;

    private boolean endReached = false;
    private List<Movie> movies;
    private PopularMovieItemClickListener listener;

    public PopularMovieAdapter(List<Movie> movies, PopularMovieItemClickListener listener){
        this.movies = movies;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //get context
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        if(viewType == TYPE_DATA) {
            //data item
            View itemView = inflater.inflate(R.layout.item, parent, false);
            return new PopularMovieViewHolder(itemView);
        }else{
            //loading item
            View itemView = inflater.inflate(R.layout.loader_item, parent, false);
            return new LoaderViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PopularMovieViewHolder) {
            PopularMovieViewHolder viewHolder = (PopularMovieViewHolder)holder;
            viewHolder.bindData(position);
        }
    }

    @Override
    public int getItemCount() {

        if(movies == null)
            return 0;
        else
            if(endReached)
                return movies.size();//no more pages to load
            else
                return movies.size() + 1;//the added item is the progress bar
    }

    //change the list of items
    public void setMoviesArray(List<Movie> movies){

        //remove all items
        this.movies.clear();
        notifyDataSetChanged();

        //load new items
        this.movies = movies;
        notifyDataSetChanged();
    }

    //add new items to the list
    public void addMovies(List<Movie> moviesLoaded){

        if(moviesLoaded==null)
            return;

        movies.addAll(moviesLoaded);
        notifyDataSetChanged();
    }

    //notify all data has been retrieved. No more pages to load
    public void notifyEnd(){
        endReached = true;
    }

    //the type of the items is given by their position
    @Override
    public int getItemViewType(int position) {
        return position > (movies.size() - 1) ? TYPE_LOADER : TYPE_DATA;
    }

    //view holder of data items. Represents movies
    public class PopularMovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView imageView;

        public PopularMovieViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.poster_imageView);

            itemView.setOnClickListener(this);
        }

        public void bindData(int position){
            Picasso.with(imageView.getContext()).load(NetworkUtils.BASE_POSTER_URL + movies.get(position).getPosterPath()).into(imageView);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            int id = movies.get(position).getId();
            listener.onClick(id);
        }
    }


    //view holder representing progress bar. It is used to suggest more data is loading
    public class LoaderViewHolder extends RecyclerView.ViewHolder{

        private ProgressBar progressBar;

        public LoaderViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar)itemView.findViewById(R.id.loading_progress_bar);
        }
    }

    //manage click on data items
    public interface PopularMovieItemClickListener{
        void onClick(int id);
    }
}
