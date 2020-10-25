package com.example.jigyasaa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import org.json.JSONArray;

import java.util.ArrayList;

public class fullyoutube_vid extends AppCompatActivity {
    ArrayList<ContactModel> arrayList=new ArrayList<>();
    RecyclerView recyclerView;
    ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullyoutube_vid);
        recyclerView=(RecyclerView)findViewById(R.id.list);
        backButton=findViewById(R.id.backbutton);
        //
        recyclerView.setHasFixedSize(true);
        //to use RecycleView, you need a layout manager. default is LinearLayoutManager
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerAdapter adapter=new RecyclerAdapter(fullyoutube_vid.this);
        recyclerView.setAdapter(adapter);
        //
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Mainitem_results.class);
                startActivity(i);
            }
        });
        String searchFor=getIntent().getStringExtra("search");
        getYoutubeSearches(searchFor);
    }
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.VideoInfoHolder> {
        //these ids are the unique id for each video
        Context ctx;

        public RecyclerAdapter(Context context) {
            this.ctx = context;
        }
        @Override
        public VideoInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_singleyoutube_vid, parent, false);
            return new VideoInfoHolder(itemView);
        }
        @Override
        public void onBindViewHolder(final VideoInfoHolder holder, final int position) {
            holder.youtube_title.setText(arrayList.get(position).getTitle());
            holder.youtube_channel.setText(arrayList.get(position).getChannelname());
            final YouTubeThumbnailLoader.OnThumbnailLoadedListener onThumbnailLoadedListener = new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                @Override
                public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {

                }

                @Override
                public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                    youTubeThumbnailView.setVisibility(View.VISIBLE);
                    holder.relativeLayoutOverYouTubeThumbnailView.setVisibility(View.VISIBLE);
                }
            };

            holder.youTubeThumbnailView.initialize(YouTubeConfig.getApiKey(), new YouTubeThumbnailView.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {

                    youTubeThumbnailLoader.setVideo(arrayList.get(position).getId());
                    youTubeThumbnailLoader.setOnThumbnailLoadedListener(onThumbnailLoadedListener);
                }

                @Override
                public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                    //write something for failure
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }
        public class VideoInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView youtube_channel,youtube_title;
            protected RelativeLayout relativeLayoutOverYouTubeThumbnailView;
            YouTubeThumbnailView youTubeThumbnailView;
            protected ImageView playButton;
            public VideoInfoHolder(View itemView) {
                super(itemView);
                playButton = (ImageView) itemView.findViewById(R.id.btnYoutube_player);
                playButton.setOnClickListener(this);
                relativeLayoutOverYouTubeThumbnailView = (RelativeLayout) itemView.findViewById(R.id.relativeLayout_over_youtube_thumbnail);
                youTubeThumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_thumbnail);
                youtube_channel=itemView.findViewById(R.id.youtube_channel);
                youtube_title=itemView.findViewById(R.id.youtube_title);
            }
            @Override
            public void onClick(View v) {
                Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) ctx,
                        YouTubeConfig.getApiKey(),
                        arrayList.get(getLayoutPosition()).getId(),//video id
                        100,     //after this time, video will start automatically
                        true,               //autoplay or not
                        false);             //lightbox mode or not; show the video in a small box
                ctx.startActivity(intent);
            }
        }
    }
    public void getYoutubeSearches(String search){
        String nogapString=search.replaceAll("\\s+", "");
        String url="https://sanjaygurung.000webhostapp.com/jigyasaa/getyoutubesearch.php?search="+nogapString;
        RequestQueue requestQueue= Volley.newRequestQueue(getApplication());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++){
                        String id=jsonArray.getJSONObject(i).getJSONObject("id").getString("videoId");
                        String title=jsonArray.getJSONObject(i).getJSONObject("snippet").getString("title");
                        String channelname=jsonArray.getJSONObject(i).getJSONObject("snippet").getString("channelTitle");
                        arrayList.add(new ContactModel(id,title,channelname));
                        recyclerView.setAdapter(new RecyclerAdapter(fullyoutube_vid.this));
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                }
                catch (Exception ex){
                    Toast.makeText(getApplicationContext(), ex+"", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }
    public class ContactModel extends ViewModel{
        String id,title,channelname;
        public ContactModel(String id,String title,String channelname){
            this.id=id;
            this.title=title;
            this.channelname=channelname;
        }
        private String getId(){return id;}

        private String getTitle() {
            return title;
        }
        private String getChannelname(){return channelname;}
    }
}
