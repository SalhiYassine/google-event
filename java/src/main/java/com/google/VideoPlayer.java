package com.google;

import java.util.*;

public class VideoPlayer {

  private final VideoLibrary videoLibrary;
  private List<VideoPlaylist> videoPlaylists = new ArrayList<>();
  private List<VideoFlag> videoFlags = new ArrayList<>();

  private Video currentVideo = null;
  private boolean currentVideoPlaying = false;

  public VideoPlayer() {
    this.videoLibrary = new VideoLibrary();
  }

  public void numberOfVideos() {
    System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
  }

  public void showAllVideos() {
    System.out.println("Here's a list of all available videos:");

    List<String> names = new ArrayList<>();
    for(Video video: videoLibrary.getVideos()){
      names.add(video.getVideoId());
    }
    Collections.sort(names);

    for(String name: names){
      Video video = videoLibrary.getVideo(name);
      System.out.print(video.getTitle()
              + " (" +
              video.getVideoId()
              + ") ");
      System.out.print("[");
      int counter = 1;

      for (String tag: video.getTags()) {
        if(counter == video.getTags().size()){
          System.out.print(tag);
        }else{
          System.out.print(tag+" ");
          counter++;
        }
      }
      System.out.print("]\n");
    }
  }

  public void playVideo(String videoId) {

    Video video = videoLibrary.getVideo(videoId);
    if(!(video == null)){
      if(!videoIsFlagged(videoId)){
        if(currentVideo != null){
          stopVideo();
        }
        currentVideo = video;
        currentVideoPlaying = true;
        System.out.println("Playing video: "+ currentVideo.getTitle());
      }else{
        System.out.println("Cannot play video: Video is currently flagged (reason: "+getFlagFromVideo(videoId).getReason()+")");
      }
    }else{
      System.out.println("Cannot play video: Video does not exist");
    }
  }

  public void stopVideo() {
    if(currentVideo != null ){
      currentVideoPlaying = false;
      System.out.println("Stopping video: "+currentVideo.getTitle());
      currentVideo = null;
    }else{
      System.out.println("Cannot stop video: No video is currently playing");
    }
  }

  public void playRandomVideo() {
    if(currentVideoPlaying){stopVideo();}
    if(getFlaggedVideos().size() < this.videoLibrary.getVideos().size()){

      Random random = new Random();
      int randomIndex = random.nextInt(videoLibrary.getVideos().size() - 1);
      Video randomVideo = videoLibrary.getVideos().get(randomIndex);
      playVideo(randomVideo.getVideoId());
    }else{
      System.out.println("No videos available");
    }
  }

  public void pauseVideo() {
    if(currentVideo != null){
      if(currentVideoPlaying == true){
        currentVideoPlaying = false;
        System.out.println("Pausing video: "+currentVideo.getTitle());
      }else{
        System.out.println("Video already paused: "+currentVideo.getTitle());
      }
    }else{
      System.out.println("Cannot pause video: No video is currently playing");
    }
  }

  public void continueVideo() {
    if(currentVideo == null ){
      currentVideo = null;
      currentVideoPlaying = false;
      System.out.print("Cannot continue video: No video is currently playing");
    }else{
      if(currentVideoPlaying == true){
        System.out.print("Cannot continue video: Video is not paused");
      }else{
        currentVideoPlaying = true;
        System.out.print("Continuing video: "+currentVideo.getTitle());
      }

    }
  }

  public void showPlaying() {

    if(currentVideo != null){

      if(currentVideoPlaying){
        System.out.print("Currently playing: ");
        printVideo(currentVideo);
      }else{
        System.out.print("Currently playing: ");
        printVideo(currentVideo);
        System.out.print(" - PAUSED");
      }
    }else{
      System.out.println("No video is currently playing!");
    }
  }

  public void createPlaylist(String playlistName) {
    boolean nameExists = false;
    for (VideoPlaylist videoPlaylist : this.videoPlaylists) {
      if(videoPlaylist.getName().equalsIgnoreCase(playlistName)){
        nameExists = true;
      }
    }
    if(nameExists){
      System.out.println("Cannot create playlist: A playlist with the same name already exists");
    }else {
      VideoPlaylist newVideoPlaylist = new VideoPlaylist(playlistName, this.videoPlaylists);
      videoPlaylists.add(newVideoPlaylist);
      System.out.println("Successfully created new playlist: "+ newVideoPlaylist.getName());
    }
  }

  public void addVideoToPlaylist(String playlistName, String videoId) {
    VideoPlaylist videoPlaylist = findPlaylistByName(playlistName, this.videoPlaylists);
    if(!(videoPlaylist == null)){
      Video video = videoLibrary.getVideo(videoId);
      if(!(video == null)){
        if(!videoPlaylist.containsVideo(video)){
          if(!videoIsFlagged(videoId)){
            videoPlaylist.getVideos().add(video);
            System.out.println("Added video to "+playlistName+": "+video.getTitle());
          }else{
            VideoFlag flag = getFlagFromVideo(videoId);
            System.out.println("Cannot play video: Video is currently flagged (reason: "+flag.getReason()+")");
          }
        }else{
          System.out.println("Cannot add video to "+playlistName+": Video already added");
        }
      }else{
        System.out.println("Cannot add video to "+ playlistName+": Video does not exist");
      }
    }else{
      System.out.println("Cannot add video to "+ playlistName+": Playlist does not exist");
    }
  }

  public void showAllPlaylists() {
    if(this.videoPlaylists.size() >= 1){
      List<String> names = new ArrayList<>();
      for(VideoPlaylist videoPlaylist : this.videoPlaylists){
        names.add(videoPlaylist.getName());
      }
      Collections.sort(names);
      System.out.println("Showing all playlists:");
      for(String name: names){
        VideoPlaylist videoPlaylistOBJ = findPlaylistByName(name, this.videoPlaylists);
        System.out.println(videoPlaylistOBJ.getName());
      }
    }else{
      System.out.println("No playlists exist yet");
    }

  }

  public void showPlaylist(String playlistName) {
    VideoPlaylist videoPlaylist = findPlaylistByName(playlistName, this.videoPlaylists);
    if(videoPlaylist != null){
      System.out.println("Showing playlist: "+playlistName);
      if(!videoPlaylist.getVideos().isEmpty()){
        for (Video video: videoPlaylist.getVideos()) {
          printVideo(video);
          if(videoPlaylist.getVideos().size() != 1){
            System.out.print("\n");
          }
        }
      }else{
        System.out.println("No videos here yet");
      }

    }else{
      System.out.println("Cannot show playlist "+ playlistName+": Playlist does not exist");
    }
  }

  public void removeFromPlaylist(String playlistName, String videoId) {
    VideoPlaylist videoPlaylist = findPlaylistByName(playlistName, this.videoPlaylists);
    if(!(videoPlaylist == null)){
      Video video = videoLibrary.getVideo(videoId);
      if(!(video == null)){
        if(videoPlaylist.containsVideo(video)){
          videoPlaylist.getVideos().remove(video);
          System.out.println("Removed video from "+playlistName+": "+video.getTitle());
        }else{
          System.out.println("Cannot remove video from "+playlistName+": Video is not in playlist");
        }

      }else{
        System.out.println("Cannot remove video from "+playlistName+": Video does not exist");
      }
    }else{
      System.out.println("Cannot remove video from "+playlistName+": Playlist does not exist");

    }
  }

  public void clearPlaylist(String playlistName) {
    VideoPlaylist videoPlaylist = findPlaylistByName(playlistName, this.videoPlaylists);
    if(!(videoPlaylist == null)){
      List<Video> videos = new ArrayList<>();
      for (Video video: videoPlaylist.getVideos()) {
        videos.add(video);
      }
      for (Video video: videos) {
        videoPlaylist.getVideos().remove(video);
      }
      System.out.println("\nSuccessfully removed all videos from "+playlistName);
    }else{
      System.out.println("Cannot clear playlist "+playlistName+": Playlist does not exist");
    }
  }

  public void deletePlaylist(String playlistName) {
    VideoPlaylist videoPlaylist = findPlaylistByName(playlistName, this.videoPlaylists);
    if (videoPlaylist != null) {
      this.videoPlaylists.remove(videoPlaylist);
      System.out.println("Deleted playlist: "+playlistName);
    }else{
      System.out.println("Cannot delete playlist "+playlistName+": Playlist does not exist");
    }
  }

  public void searchVideos(String searchTerm) {
    List<Video> matchingSearch = findVideosBySearch(searchTerm);
    // Search Function
    if(matchingSearch.size() > 0){
      List<Video> sortedVids = sortVideos(matchingSearch);
      System.out.println("Here are the results for "+ searchTerm +":");
      for (int i = 0; i < sortedVids.size(); i++) {
        Video video = sortedVids.get(i);
        printVideo(video, i+1);
        System.out.print("\n");
      }
      System.out.println("Would you like to play any of the above? If yes, specify the number of the video.");
      System.out.println("If your answer is not a valid number, we will assume it's a no.");
      Scanner input = new Scanner(System.in);
      String response = input.nextLine();

      if(isNumeric(response)){
        if(Integer.parseInt(response) <= sortedVids.size()){
          playVideo(sortedVids.get(Integer.parseInt(response) - 1).getVideoId());
        }
      }
    }else{
      System.out.println("No search results for " + searchTerm);
    }
  }

  public void searchVideosWithTag(String videoTag) {
    List<Video> matchingSearch = findVideosByTag(videoTag);
    // Search Function
    if(matchingSearch.size() > 0){
      List<Video> sortedVids = sortVideos(matchingSearch);
      System.out.println("Here are the results for "+ videoTag +":");
      for (int i = 0; i < sortedVids.size(); i++) {
        Video video = sortedVids.get(i);
        printVideo(video, i+1);
        System.out.print("\n");
      }
      System.out.println("Would you like to play any of the above? If yes, specify the number of the video.");
      System.out.println("If your answer is not a valid number, we will assume it's a no.");
      Scanner input = new Scanner(System.in);
      String response = input.nextLine();

      if(isNumeric(response)){
        if(Integer.parseInt(response) <= sortedVids.size()){
          playVideo(sortedVids.get(Integer.parseInt(response) - 1).getVideoId());
        }
      }
    }else{
      System.out.println("No search results for " + videoTag);
    }
  }

  public void flagVideo(String videoId) {
    Video video = videoLibrary.getVideo(videoId);
    if(video != null){
      if(!videoIsFlagged(videoId)){
        VideoFlag flag = new VideoFlag(video);
        this.videoFlags.add(flag);
        if(this.currentVideo != null && this.currentVideo.getTitle().equalsIgnoreCase(flag.getVideo().getTitle())){
          stopVideo();
        }
        System.out.println("Successfully flagged video: "+video.getTitle()+" (reason: "+flag.getReason()+")");
      }else{
        System.out.println("Cannot flag video: Video is already flagged");
      }
    }else{
      System.out.println("Cannot flag video: Video does not exist");
    }


  }

  public void flagVideo(String videoId, String reason) {
    Video video = videoLibrary.getVideo(videoId);
    if(video != null){
      if(!videoIsFlagged(videoId)){
        VideoFlag flag = new VideoFlag(video, reason);
        this.videoFlags.add(flag);
        System.out.println("Successfully flagged video: "+video.getTitle()+" (reason: "+flag.getReason()+")");
      }else{
        System.out.println("Cannot flag video: Video is already flagged");
      }
    }else{
      System.out.println("Cannot flag video: Video does not exist");
    }
  }

  public void allowVideo(String videoId) {
    Video video = videoLibrary.getVideo(videoId);
    if(video != null){
      if(videoIsFlagged(videoId)){
         VideoFlag flag = getFlagFromVideo(videoId);
         this.videoFlags.remove(flag);
         System.out.println("Successfully removed flag from video: "+video.getTitle());
      }else{
        System.out.println("Cannot remove flag from video: Video is not flagged");
      }
    }else{
      System.out.println("Cannot remove flag from video: Video does not exist");
    }

  }


  private List<Video> getFlaggedVideos(){
    List<Video> videos = new ArrayList<>();
    for (VideoFlag flag: this.videoFlags) {
      Video video = flag.getVideo();
      videos.add(video);
    }
    return videos;
  }

  private VideoFlag getFlagFromVideo(String videoID){
    for (VideoFlag flag: this.videoFlags) {
      if(flag.getVideo().getVideoId().equals(videoID)){
        return flag;
      }
    }
    return null;
  }

  private boolean videoIsFlagged(String videoID){
    Video video = videoLibrary.getVideo(videoID);
    if(video != null){
      if(getFlaggedVideos().contains(video)){
        return true;
      }
    }
    return false;
  }




  private List<Video> sortVideos(List<Video> videos){
    List<String> names = new ArrayList<>();
    for(Video video: videos){
      names.add(video.getVideoId());
    }
    Collections.sort(names);
    List<Video> videosSorted = new ArrayList<>();
    for(String name: names){
      Video newVid = videoLibrary.getVideo(name);
      videosSorted.add(newVid);
    }
    return videosSorted;
  }

  private List<Video> findVideosBySearch(String name){
    List<Video> videos = new ArrayList<>();
    for (Video video: this.videoLibrary.getVideos()) {
      if(video.getTitle().toLowerCase().contains(name)){
        videos.add(video);
      }
    }
    return videos;
  }
  private List<Video> findVideosByTag(String search){
    List<Video> videos = new ArrayList<>();
    for (Video video: this.videoLibrary.getVideos()) {
      for (String tag: video.getTags()) {
        if(tag.toLowerCase().contains(search)){
          videos.add(video);
        }
      }

    }
    return videos;
  }

  private VideoPlaylist findPlaylistByName(String name, List<VideoPlaylist> videoPlaylists){
    for (VideoPlaylist videoPlaylist : videoPlaylists) {
      if(videoPlaylist.getName().equalsIgnoreCase(name)){
        return videoPlaylist;
      }
    }
    return null;
  }

  private void printVideo(Video video){
    System.out.print(video.getTitle()
            + " (" +
            video.getVideoId()
            + ") ");
    System.out.print("[");
    int counter = 1;

    for (String tag: video.getTags()) {
      if(counter == video.getTags().size()){
        System.out.print(tag);
      }else{
        System.out.print(tag+" ");
        counter++;
      }
    }
    System.out.print("]");
  }

  private void printVideo(Video video, int index){
    System.out.print(index+") "+video.getTitle()
            + " (" +
            video.getVideoId()
            + ") ");
    System.out.print("[");
    int counter = 1;

    for (String tag: video.getTags()) {
      if(counter == video.getTags().size()){
        System.out.print(tag);
      }else{
        System.out.print(tag+" ");
        counter++;
      }
    }
    System.out.print("]");
  }

  private static boolean isNumeric(String str){
    return str != null && str.matches("[0-9.]+");
  }

}