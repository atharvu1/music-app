package com.example.musicapp;

public class SongInfoModel {
    String trackName;
    String artistName;
    String collectionName;
    String thumbnailURL;


    SongInfoModel(String trackName, String artistName, String collectionName, String thumbnailURL){
        this.trackName = trackName;
        this.artistName = artistName;
        this.collectionName = collectionName;
        this.thumbnailURL = thumbnailURL;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }
}
