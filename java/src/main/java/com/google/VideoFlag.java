package com.google;

public class VideoFlag {

    private Video video;
    private String reason;

    public VideoFlag(Video video) {
        this.video = video;
        this.reason = "Not supplied";
    }

    public VideoFlag(Video video, String reason) {
        this.video = video;
        this.reason = reason;
    }

    public Video getVideo() {
        return video;
    }

    public String getReason() {
        return reason;
    }




}
