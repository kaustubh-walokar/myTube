package com.cmpe277.lab2.mytube;
import com.google.android.gms.common.Scopes;
import com.google.api.services.youtube.YouTubeScopes;


public class Values {

    public static final String TAG = "MyTube";
    public static final String PLAYLIST_NAME = "SJSU-CMPE-277";
    public static final String APPLICATION_NAME = "com.cmpe277.lab2.mytube";
    public static final String YOUTUBE_PLAYER_KEY ="";
    public static final String YOUTUBE_DATA_KEY ="";
    public static final long NUMBER_OF_VIDEOS_RETURNED = 30;
    public static final String SCOPE_STRING = "oauth2:" + Scopes.PROFILE + " " + YouTubeScopes.YOUTUBE + " " + YouTubeScopes.YOUTUBE_UPLOAD + " " + Scopes.EMAIL ;
    public static final String INITIAL_SEARCH_WORD = "Latest Technology 2015";


}
