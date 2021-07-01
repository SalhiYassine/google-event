package com.google;

import java.util.ArrayList;
import java.util.List;

public class VideoPlaylist {

    private int id;
    private String name;
    private List<Video> videos = new ArrayList<>();


    public VideoPlaylist(String name, List<VideoPlaylist> videoPlaylists) {
        this.id = generateID(videoPlaylists);
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Video> getVideos() {
        return videos;
    }

    private int generateID(List<VideoPlaylist> videoPlaylists){
        int id = 0;
        for (VideoPlaylist videoPlaylist : videoPlaylists) {
            if(videoPlaylist.getId() > id){ id = videoPlaylist.getId() + 1;}}
        return id;
    }
    public boolean containsVideo(Video video){
        if(this.videos.contains(video)){
            return true;
        }else{
            return false;
        }
    }

}
