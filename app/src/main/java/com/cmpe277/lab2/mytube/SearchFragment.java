package com.cmpe277.lab2.mytube;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;



public class SearchFragment extends Fragment {
    private EditText searchInput;
    private ListView videosFound;
    private List<VideoItem> searchResults;
    private Handler handler;
    private Context context = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        context = getActivity().getApplicationContext();

        searchInput = (EditText)v.findViewById(R.id.search_input);
        videosFound = (ListView)v.findViewById(R.id.videos_found);

        handler = new Handler();

        //searchOnYoutube(Values.INITIAL_SEARCH_WORD);


        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    searchOnYoutube(v.getText().toString());
                    return false;
                }
                return true;
            }
        });

        videosFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                Log.d("SEARCH", "Item clicked " + id);
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("VIDEO_ID", searchResults.get(pos).getId());
                startActivity(intent);
            }

        });



        return  v;
    }

    private void searchOnYoutube(final String keywords){
        new Thread(){
            public void run(){
                YoutubeConnector yc = new YoutubeConnector(context);
                searchResults = yc.search(keywords);
                handler.post(new Runnable(){
                    public void run(){
                        updateVideosFound();
                    }
                });
            }
        }.start();
    }

    private void updateVideosFound(){
        ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(context, R.layout.video_item, searchResults){
            AView oneView;

           @Override
            public View getView(int position, View convertView, final ViewGroup parent) {

                if(convertView == null){
                    //convertView = getActivity().getLayoutInflater().inflate(R.layout.video_item, parent, false);
                    // inflate the layout
                  LayoutInflater inflater = getActivity().getLayoutInflater();
                  convertView = inflater.inflate(R.layout.video_item, parent, false);
                    // well set up the ViewHolder

                    ImageView thumbnail = (ImageView)convertView.findViewById(R.id.video_thumbnail);
                    TextView id = (TextView)convertView.findViewById(R.id.video_id);
                    //TextView description = (TextView)convertView.findViewById(R.id.video_description);

                    oneView = new AView();
                    oneView.imageView = (ImageView)convertView.findViewById(R.id.video_thumbnail);
                    oneView.title = (TextView)convertView.findViewById(R.id.video_title);
                    oneView.viewCounts = (TextView)convertView.findViewById(R.id.view_count);
                    oneView.publishDate = (TextView)convertView.findViewById(R.id.publish_date);
                    oneView.favoriteBtn = (ImageButton)convertView.findViewById(R.id.favorite);
                    oneView.id=(TextView)convertView.findViewById(R.id.video_id);
                    oneView.favoriteBtn.setTag(position);

                    // store the holder with the view.
                    convertView.setTag(oneView);
                }else{
                    // we've just avoided calling findViewById() on resource everytime
                    // just use the oneView
                    oneView = (AView) convertView.getTag();
                }
                ImageView imageView = (ImageView)convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView)convertView.findViewById(R.id.video_title);
                TextView viewCounts = (TextView)convertView.findViewById(R.id.view_count);
                TextView publishDate = (TextView)convertView.findViewById(R.id.publish_date);
               TextView id = (TextView)convertView.findViewById(R.id.video_id);
//                final ImageButton favoriteBtn = (ImageButton)convertView.findViewById(R.id.favorite);
                //oneView.favoriteBtn.setTag(position);
                //convertView.setTag(oneView);
                // object item based on the position
                //VideoItem objectItem = data[position];

                oneView.favoriteBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        int placePosition = (Integer) oneView.favoriteBtn.getTag();
                        Log.d("MYTUBE", "Place Position" + placePosition);
                        View p = (ViewGroup) arg0.getParent();
                        String  s= ((TextView) p.findViewById(R.id.video_id)).getText().toString();
                       // Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                        YoutubeConnector yc = new YoutubeConnector(context);
                        try {
                            yc.insertVideo(s);
                            Log.d(Values.TAG,"added "+s+" to fav");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (!searchResults.get(placePosition).isFavorite()) {
                            oneView.favoriteBtn.setImageResource(android.R.drawable.btn_star_big_on);
                            searchResults.get(placePosition).setIsFavorite(true);
                            Toast.makeText(context,
                                    "Item added to favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            oneView.favoriteBtn.setImageResource(android.R.drawable.btn_star_big_off);
                            searchResults.get(placePosition).setIsFavorite(false);
                            Toast.makeText(context,
                                    "Item removed from favorites", Toast.LENGTH_SHORT).show();
                        }

                    }

                });

                VideoItem item = searchResults.get(position);

                Picasso.with(context).load(item.getThumbnailURL()).into(imageView);
                title.setText(item.getTitle());
                viewCounts.setText("Views: " + item.getViewCount());
                id.setText(item.getId());
                publishDate.setText(item.getPublishDate());


//                description.setText(searchResult.getDescription());

               return convertView;
            }
            /*@Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }*/
        };

        videosFound.setAdapter(adapter);
    }

}
