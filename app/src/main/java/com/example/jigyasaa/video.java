package com.example.jigyasaa;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.JsonArray;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link video#newInstance} factory method to
 * create an instance of this fragment.
 */
public class video extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public video() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment video.
     */
    // TODO: Rename and change types and number of parameters
    public static video newInstance(String param1, String param2) {
        video fragment = new video();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    YouTubePlayer YPlayer;
    List<String> arrayList=new ArrayList<>();
    TextView seeAll;
    private static final String TAG = "Checking";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_video, container, false);
        seeAll=root.findViewById(R.id.textView4);
        assert getArguments() != null;
        final String searchFor=(Objects.requireNonNull(getArguments().getString("itemtosearch")));
        if(!searchFor.equals("")){
            getYoutubeSearches(searchFor);
            seeAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!arrayList.isEmpty()){
                        Intent i=new Intent(getContext(),fullyoutube_vid.class);
                        i.putExtra("search",searchFor);
                        startActivity(i);
                    }
                }
            });
        }
        //
        return root;
    }
    public void getYoutubeSearches(String search){
        String nogapString=search.replaceAll("\\s+", "");
        String url="https://sanjaygurung.000webhostapp.com/jigyasaa/getyoutubesearch.php?search="+nogapString;
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++){
                        String id=jsonArray.getJSONObject(i).getJSONObject("id").getString("videoId");
                        arrayList.add(id);
                    }
                    //get youtube videos
                   getYoutubeVideos(arrayList);
                }
                catch (Exception ex){
                    Toast.makeText(getContext(), ex+"", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }
    public void getYoutubeVideos(final List arrayList){
        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        youTubePlayerFragment.initialize(YouTubeConfig.getApiKey(),new YouTubePlayer.OnInitializedListener()
        {
            @Override
            public void onInitializationSuccess (YouTubePlayer.Provider arg0, YouTubePlayer
                    youTubePlayer,boolean b){
                if (!b) {
                    YPlayer = youTubePlayer;
                    YPlayer.loadVideo((String) arrayList.get(0));
                }
            }
            @Override
            public void onInitializationFailure (YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1){
                // TODO Auto-generated method stub

            }
        });
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        //ignore the red line
        fragmentTransaction.replace(R.id.view,youTubePlayerFragment);
        fragmentTransaction.commit();
    }
}
