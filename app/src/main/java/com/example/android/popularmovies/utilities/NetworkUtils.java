package com.example.android.popularmovies.utilities;

import android.net.Uri;
import android.support.v4.util.Pair;
import android.util.Log;

import com.example.android.popularmovies.model.Movie;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by v.dipaolo on 23/01/2017.
 */

//manage the interaction with theMovieDB
public class NetworkUtils {

    private static final String TAG = "NetworkUtils";
    public static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/w185";
    private static final String API_KEY ="";
    public static final String SORT_BY_POPULARITY = "popular";
    public static final String SORT_BY_RANKING = "top_rated";



    public static Pair<Integer, List<Movie>> getSortedMoviesPosterAndPages(String sortBy){
        URL url = buildURLDiscover(sortBy,1);

        Log.d(TAG, url.toString());

        String jsonResult = getMovies(url);
        List<Movie> moviesPoster = JsonUtils.getImagesArray(jsonResult);
        int totalPages = JsonUtils.getPages(jsonResult);

        if(moviesPoster==null || totalPages == 0)
            return null;

        return new Pair<Integer, List<Movie>>(totalPages, moviesPoster);
    }

    /**
     * This method allows to load a page of ids and poster paths of movies sorted by the provided
     * criteria
     *
     * @param sortBy The sorting criteria
     * @param page The page result
     * @return The list of java object representing the movies
     */
    public static List<Movie> getSortedMoviesPoster(String sortBy, int page){
        URL url = buildURLDiscover(sortBy,page);

        Log.d(TAG, url.toString());

        String jsonResult = getMovies(url);
        List<Movie> moviesPoster = JsonUtils.getImagesArray(jsonResult);
        return moviesPoster;
    }

    /**
     * This method allows to retrieve movie details by movie id
     *
     * @param id The id of a movie
     * @return The java object representing the movie with all details
     */
    public static Movie getMovieDetailById(int id){
        URL url = buildURLGetById(id);
        String jsonResult = getMovies(url);
        Movie movie = JsonUtils.getMovieDetailsFromJson(jsonResult);
        return movie;
    }


    /**
     * This method builds the url to retrieve a page result of movies sorted by the provided criteria
     *
     * @param sortBy The sorting criteria
     * @param page The result page
     * @return The url to retrieve the movies
     */
    private static URL buildURLDiscover(String sortBy, int page) {

        String path = "3/movie/" + sortBy;

        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .path(path)
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("page", ""+page)
                .build();

        URL url = null;

        try {
            url = new URL(uri.toString());
        }catch (MalformedURLException e) {
            Log.e("NetworkUtils", "Malformed URL");
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method builds the url to retrieve movie details by id
     *
     * @param id A movie id
     * @return The url to retrieve the movie details
     */
    private static URL buildURLGetById(int id) {

        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("api.themoviedb.org")
                .path("3/movie/" + id)
                .appendQueryParameter("api_key", API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(uri.toString());
        }catch (MalformedURLException e) {
            Log.e(TAG, "Malformed URL");
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method invokes the web service and returns the result
     *
     * @param url The URL to invoke
     * @return The response of an API invocation in json format
     */
    private static String getMovies(URL url) {

        if(url == null)
            return null;

        HttpsURLConnection urlConnection = null;
        Scanner scanner = null;

        try {
            urlConnection = (HttpsURLConnection)url.openConnection();
            InputStream in = urlConnection.getInputStream();

            scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Connection error");
            e.printStackTrace();
            return  null;
        }finally {
            if(urlConnection != null)
                urlConnection.disconnect();
            if(scanner != null)
                scanner.close();
        }
    }

}
