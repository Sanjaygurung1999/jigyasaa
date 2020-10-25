package com.example.jigyasaa;

import android.annotation.SuppressLint;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link dictionary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class dictionary extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public dictionary() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment dictionary.
     */
    // TODO: Rename and change types and number of parameters
    public static dictionary newInstance(String param1, String param2) {
        dictionary fragment = new dictionary();
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
    RecyclerView dictionaryRecycle;
    ArrayList<String> words=new ArrayList<>();
    ArrayList<ContactModel> arrayList=new ArrayList<>();
    TextView dictWord,dictPronun;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_dictionary, container, false);
        dictionaryRecycle=root.findViewById(R.id.dictionary_recycle);
        dictionaryRecycle.setAdapter(new ContactAdapter());
        dictionaryRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        dictWord=root.findViewById(R.id.dict_word);
        dictPronun=root.findViewById(R.id.dict_pronun);
        String searchFor=(Objects.requireNonNull(getArguments().getString("itemtosearch")));
        if(!searchFor.equals("")){
            getDictionaryearches(searchFor);
        }
        return root;
    }
    public void getDictionaryearches(String search){
        String nogapString=search.replaceAll("\\s+", "");
        String url="https://sanjaygurung.000webhostapp.com/jigyasaa/dictionary.php?search="+nogapString;
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String word=jsonObject.getString("word");
                    String pronunciation=jsonObject.getString("pronunciation");
                    dictWord.setText(word);
                    dictPronun.setText(pronunciation);
                    for (int j=0;j<jsonObject.getJSONArray("definitions").length();j++){
                        String type=jsonObject.getJSONArray("definitions").getJSONObject(j).getString("type");
                        String define=jsonObject.getJSONArray("definitions").getJSONObject(j).getString("definition");
                        String example=jsonObject.getJSONArray("definitions").getJSONObject(j).getString("example");
                        String emote=jsonObject.getJSONArray("definitions").getJSONObject(j).getString("emoji");
                        String image=jsonObject.getJSONArray("definitions").getJSONObject(j).getString("image_url");
                        arrayList.add(new ContactModel(type,define,example,emote,image));
                        dictionaryRecycle.setAdapter(new ContactAdapter());
                        dictionaryRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                }
                catch (Exception ex){
                    Toast.makeText(getContext(), "No Results", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }
    public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {

        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View root = getLayoutInflater().inflate(R.layout.fragment_dictionary_items, parent, false);
            ContactViewHolder contactViewHolder = new ContactViewHolder(root);
            return contactViewHolder;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
            holder.dictType.setText(arrayList.get(position).getType());
            if(!arrayList.get(position).getEmote().equals("null")){
                holder.dictDefine.setText(arrayList.get(position).getDefine()+arrayList.get(position).getEmote());
            }
            else{
                holder.dictDefine.setText(arrayList.get(position).getDefine());
            }
            holder.dictExample.setText(arrayList.get(position).getExample());
            if(!arrayList.get(position).getImage().equals("null")){
                Glide
                        .with(getContext())
                        .load(arrayList.get(position).getImage())
                        .into(holder.dictImage);
            }
        }
        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView dictType,dictDefine,dictExample;
        ImageView dictImage;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            dictType=itemView.findViewById(R.id.dict_type);
            dictDefine=itemView.findViewById(R.id.dict_definition);
            dictExample=itemView.findViewById(R.id.dict_example);
            dictImage=itemView.findViewById(R.id.dict_image);
        }
    }
    public class ContactModel extends ViewModel {
        String type,define,example,emote,image;
        public ContactModel(String type,String define,String example,String emote,String image){
            this.type=type;
            this.define=define;
            this.example=example;
            this.emote=emote;
            this.image=image;
        }
        private String getType(){return type;}
        private String getDefine(){return define;}
        private String getExample(){return example;}
        private String getEmote(){return emote;}
        private String getImage(){return image;}
    }
}
