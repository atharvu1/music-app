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
    public String getArtistName() {
        return artistName;
    }
    public String getCollectionName() {
        return collectionName;
    }
    public String getThumbnailURL() {
        return thumbnailURL;
    }
}