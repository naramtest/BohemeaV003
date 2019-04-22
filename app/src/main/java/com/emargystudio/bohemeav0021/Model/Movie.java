package com.emargystudio.bohemeav0021.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "movies")
public class Movie implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int tmdb_id;
    private String title;
    private String genres;
    private String release_date;
    private String poster_path;
    private String backdrop_path;
    private String overview;
    private int vote_average;
    private int runtime;

    public Movie(int id, int tmdb_id, String title, String genres, String release_date, String poster_path, String backdrop_path, String overview, int vote_average, int runtime) {
        this.id = id;
        this.tmdb_id = tmdb_id;
        this.title = title;
        this.genres = genres;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.backdrop_path = backdrop_path;
        this.overview = overview;
        this.vote_average = vote_average;
        this.runtime = runtime;
    }

    @Ignore
    public Movie(int tmdb_id, String title, String genres, String release_date, String poster_path, String backdrop_path, String overview, int vote_average, int runtime) {
        this.tmdb_id = tmdb_id;
        this.title = title;
        this.genres = genres;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.backdrop_path = backdrop_path;
        this.overview = overview;
        this.vote_average = vote_average;
        this.runtime = runtime;
    }

    @Ignore
    protected Movie(Parcel in) {
        id = in.readInt();
        tmdb_id = in.readInt();
        title = in.readString();
        genres = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        backdrop_path = in.readString();
        overview = in.readString();
        vote_average = in.readInt();
        runtime = in.readInt();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTmdb_id() {
        return tmdb_id;
    }

    public void setTmdb_id(int tmdb_id) {
        this.tmdb_id = tmdb_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public int getVote_average() {
        return vote_average;
    }

    public void setVote_average(int vote_average) {
        this.vote_average = vote_average;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(tmdb_id);
        dest.writeString(title);
        dest.writeString(genres);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeString(backdrop_path);
        dest.writeString(overview);
        dest.writeInt(vote_average);
        dest.writeInt(runtime);
    }
}
