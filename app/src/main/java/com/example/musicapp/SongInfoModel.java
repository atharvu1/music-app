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

}
