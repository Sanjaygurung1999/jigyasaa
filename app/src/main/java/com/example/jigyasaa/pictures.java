package com.example.jigyasaa;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
 * Use the {@link pictures#newInstance} factory method to
 * create an instance of this fragment.
 */
public class pictures extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public pictures() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment pictures.
     */
    // TODO: Rename and change types and number of parameters
    public static pictures newInstance(String param1, String param2) {
        pictures fragment = new pictures();
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
    RecyclerView flickr_recycle;
    ArrayList<ContactModel> arrayList=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_pictures, container, false);
        flickr_recycle=root.findViewById(R.id.flickr_recycle);
        flickr_recycle.setAdapter(new ContactAdapter());
        flickr_recycle.setLayoutManager(new LinearLayoutManager(getContext()));
        assert getArguments() != null;
        String searchfor=getArguments().getString("itemtosearch");
        assert searchfor != null;
        if(!searchfor.equals("")){
            getFlickrPhoto(searchfor);
        }
        return  root;
    }
    public void getFlickrPhoto(String search){
        String nogapString=search.replaceAll("\\s+", "");
        String url="https://sanjaygurung.000webhostapp.com/jigyasaa/flicker.php?search="+nogapString;
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++){
                        String id=jsonArray.getJSONObject(i).getString("id");
                        String server=jsonArray.getJSONObject(i).getString("server");
                        String farm=jsonArray.getJSONObject(i).getString("farm");
                        String secret=jsonArray.getJSONObject(i).getString("secret");
                        String title=jsonArray.getJSONObject(i).getString("title");
                        arrayList.add(new ContactModel(id,server,farm,secret,title));
                        flickr_recycle.setAdapter(new ContactAdapter());
                        flickr_recycle.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                }
                catch (Exception ex){
                    Toast.makeText(getContext(),ex+ "", Toast.LENGTH_SHORT).show();
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
            View root = getLayoutInflater().inflate(R.layout.fragment_flickr_photos,parent,false);
            ContactViewHolder contactViewHolder = new ContactViewHolder(root);
            return contactViewHolder;
        }
        @Override
        public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
            //holder.flickr_title.setText(arrayList.get(position).getTitle());
            Glide
                    .with(getContext())
                    .load("http://farm"+arrayList.get(position).getFarm()+".staticflickr.com/"+arrayList.get(position).getserverId()
                            +"/"+arrayList.get(position).getId()+"_"+arrayList.get(position).getSecret()+".jpg")
                    .into(holder.flickr_photo);
        }
        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        //TextView flickr_title;
        ImageView flickr_photo;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            //flickr_title=itemView.findViewById(R.id.flickr_title);
            flickr_photo=itemView.findViewById(R.id.flickr_photo);
        }
    }
    public class ContactModel extends ViewModel {
        String id,serverid,farm,secret,title;
        public ContactModel(String id,String serverid,String farm,String secret,String title){
            this.id=id;
            this.serverid=serverid;
            this.farm=farm;
            this.secret=secret;
            this.title=title;
        }
        private String getId(){return id;}
        private String getserverId(){ return serverid;}
        private String getFarm(){ return farm;}
        private String getSecret(){ return secret;}
        private String getTitle(){return title;}
    }
}
