package com.example.jigyasaa;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link twitter#newInstance} factory method to
 * create an instance of this fragment.
 */
public class twitter extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public twitter() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment twitter.
     */
    // TODO: Rename and change types and number of parameters
    public static twitter newInstance(String param1, String param2) {
        twitter fragment = new twitter();
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
    ArrayList<ContactModel> arrayList=new ArrayList<>();
    RecyclerView twitterrecycle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_twitter, container, false);
        twitterrecycle=root.findViewById(R.id.twittertweetrecycle);
        twitterrecycle.setAdapter(new ContactAdapter());
        twitterrecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        assert getArguments() != null;
        String searchFor=getArguments().getString("itemtosearch");
        assert searchFor != null;
        if(!searchFor.equals("")){
            getTweets(searchFor);
        }
        return root;
    }
    public void getTweets(String search){
        String nogapString=search.replaceAll("\\s+", "");
        String url="https://sanjaygurung.000webhostapp.com/jigyasaa/getTwitterSearch.php?search="+nogapString;
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++){
                        String userid=jsonArray.getJSONObject(i).getJSONObject("user").getString("screen_name");
                        String name="@"+jsonArray.getJSONObject(i).getJSONObject("user").getString("name");
                        String date=jsonArray.getJSONObject(i).getString("created_at");
                        String text=jsonArray.getJSONObject(i).getString("full_text");
                        if(text.contains("RT")){
                            text=jsonArray.getJSONObject(i).getJSONObject("retweeted_status").getString("full_text");
                        }
                        arrayList.add(new ContactModel(userid,name,date,text));
                        twitterrecycle.setAdapter(new ContactAdapter());
                        twitterrecycle.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                }
                catch (Exception ex){
                    Toast.makeText(getContext(),"Results Obtained", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }
    public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder>{

        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View root = getLayoutInflater().inflate(R.layout.fragment_twittertweets,parent,false);
            ContactViewHolder contactViewHolder = new ContactViewHolder(root);
            return contactViewHolder;
        }
        @Override
        public void onBindViewHolder(@NonNull final ContactViewHolder holder, final int position) {
            holder.tweetuserid.setText(arrayList.get(position).getId());
            holder.tweetusername.setText(arrayList.get(position).getName());
            holder.tweetdate.setText(arrayList.get(position).getDate());
            holder.tweettext.setText(arrayList.get(position).getText());
            RequestQueue requestQueue= Volley.newRequestQueue(getContext());
            StringRequest stringRequest=new StringRequest(Request.Method.GET, "https://sanjaygurung.000webhostapp.com/jigyasaa/twitteruserimage.php?screenname="+arrayList.get(position).getId(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String image=response;
                        Glide
                                .with(getContext())
                                .load(image)
                                .into(holder.userimage);
                    }
                    catch (Exception ex){
                        Toast.makeText(getContext(),ex+"", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(stringRequest);
        }
        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tweetusername,tweetuserid,tweettext,tweetdate;
        LinearLayout tweetlayout;
        ImageView userimage;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tweetuserid = itemView.findViewById(R.id.tweetuserid);
            tweetusername = itemView.findViewById(R.id.tweetusername);
            tweetdate = itemView.findViewById(R.id.tweetdate);
            tweettext = itemView.findViewById(R.id.actualtweet);
            tweetlayout = itemView.findViewById(R.id.tweetlayout);
            userimage=itemView.findViewById(R.id.tweetprofileimage);
        }
    }
    public class ContactModel extends ViewModel {
        String id,name,date,text;
        public ContactModel(String id,String name,String date,String text){
            this.id=id;
            this.name=name;
            this.date=date;
            this.text=text;
        }
        private String getId(){return id;}
        private String getName(){ return name;}
        private String getDate(){ return date;}
        private String getText(){ return text;}
    }
}
