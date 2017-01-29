package com.example.android.popularmovies.utilities;

import android.util.Log;

import com.example.android.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v.dipaolo on 23/01/2017.
 */

//traslate json in java objects
public class JsonUtils {

    private static final String TAG = "JsonUtils";

    /**
     * Translate a json representing a collection of movies in the list of the corresponding java objects.
     * Only movie ids and movie poster paths are taken into account.
     *
     * @param json A json representing a collection of movies
     * @return The list of java object representing the collection of movies
     */
    public static List<Movie> getImagesArray(String json){

        if(json == null)
            return null;

        List<Movie> moviesPath = null;

        try {

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonResultsArray = jsonObject.getJSONArray("results");

            //number of movies in the first page
            int resultNum = jsonResultsArray.length();
            moviesPath = new ArrayList<Movie>();

            for (int i = 0; i < resultNum; i++) {
                JSONObject currMovie = jsonResultsArray.getJSONObject(i);
                String currPosterPath = currMovie.getString("poster_path");
                int currId = currMovie.getInt("id");
                moviesPath.add(new Movie(currId, currPosterPath));
            }
        }catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON");
            e.printStackTrace();
            return  null;
        }

        return moviesPath;
    }

    /**
     * Translate a json representing a movie in the corresponding java object
     *
     * @param json A json representing a movie
     * @return The java object representing the movie
     */
    public static Movie getMovieDetailsFromJson(String json){


        if(json == null)
            return null;

        Movie movie = null;

        try {

            JSONObject jsonObject = new JSONObject(json);
            int id = jsonObject.getInt("id");
            String path = jsonObject.getString("poster_path");
            String title = jsonObject.getString("original_title");
            String releaseDate = jsonObject.getString("release_date");
            double rating = jsonObject.getDouble("vote_average");
            String plot = jsonObject.getString("overview");

            //remove escape character
            if(path.startsWith("\\"))
                path = path.substring(1);

            movie = new Movie(id, path);

            movie.setTitle(title);
            movie.setRelease(releaseDate);
            movie.setRating(rating);
            movie.setPlot(plot);

        }catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON");
            e.printStackTrace();
            return null;
        }

        return movie;
    }

    /**
     * return the total number of result pages
     *
     * @param json A json representing a collection of movies
     * @return The number of the result pages
     */
    public static int getPages(String json){

        if(json == null)
            return 0;

        try {
            JSONObject jsonObject = new JSONObject(json);
            int totalPages = jsonObject.getInt("total_pages");
            return totalPages;
        }catch (JSONException e){
            Log.e(TAG, "Error parsing JSON");
            e.printStackTrace();
            return 0;
        }
    }
}
