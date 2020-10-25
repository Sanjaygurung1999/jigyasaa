package com.example.jigyasaa;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link cart#newInstance} factory method to
 * create an instance of this fragment.
 */
public class cart extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public cart() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment cart.
     */
    // TODO: Rename and change types and number of parameters
    public static cart newInstance(String param1, String param2) {
        cart fragment = new cart();
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
    RecyclerView cart_recycle;
    ArrayList<ContactModel> arrayList=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_cart, container, false);
        cart_recycle=root.findViewById(R.id.cart_recycle);
        cart_recycle.setAdapter(new ContactAdapter());
        cart_recycle.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        String searchFor=(Objects.requireNonNull(getArguments().getString("itemtosearch")));
        if(!searchFor.equals("")){
            getebaySearches(searchFor);
        }
        return root;
    }
    public void getebaySearches(String search){
        String nogapString=search.replaceAll("\\s+", "");
        String url="https://sanjaygurung.000webhostapp.com/jigyasaa/ebay.php?search="+nogapString;
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    for (int j=0;j<jsonObject.getJSONArray("itemSummaries").length();j++){
                        String title=jsonObject.getJSONArray("itemSummaries").getJSONObject(j).getString("title");
                        String weburl=jsonObject.getJSONArray("itemSummaries").getJSONObject(j).getString("itemWebUrl");
                        String imageurl;
                        if(jsonObject.getJSONArray("itemSummaries").getJSONObject(j).has("image")){
                           imageurl=jsonObject.getJSONArray("itemSummaries").getJSONObject(j).getJSONObject("image").getString("imageUrl");
                        }
                        else{
                            imageurl="https://i.ebayimg.com/images/g/59kAAOSwaENdvGQY/s-l500.jpg";
                        }

                        String price=jsonObject.getJSONArray("itemSummaries").getJSONObject(j).getJSONObject("price").getString("value")+" "+jsonObject.getJSONArray("itemSummaries").getJSONObject(j).getJSONObject("price").getString("currency");
                        String condition=jsonObject.getJSONArray("itemSummaries").getJSONObject(j).getString("condition");
                        arrayList.add(new ContactModel(title,imageurl,price,weburl,condition));
                        cart_recycle.setAdapter(new ContactAdapter());
                        cart_recycle.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
                    }
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
    public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder>{
        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View root = getLayoutInflater().inflate(R.layout.fragment_ebay_item,parent,false);
            ContactViewHolder contactViewHolder = new ContactViewHolder(root);
            return contactViewHolder;
        }
        @Override
        public void onBindViewHolder(@NonNull ContactViewHolder holder, final int position) {
            holder.ebay_title.setText(arrayList.get(position).getTitle());
            holder.ebay_price.setText(arrayList.get(position).getPrice());
            holder.ebay_condition.setText(arrayList.get(position).getCondition());
            Glide
                    .with(getContext())
                    .load(arrayList.get(position).getImageurl())
                    .into(holder.ebay_image);
            holder.cart_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(arrayList.get(position).getWeburl()));
                    startActivity(browserIntent);
                }
            });
        }
        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        ImageView ebay_image;
        TextView ebay_title,ebay_price,ebay_condition;
        LinearLayout cart_layout;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            ebay_image=itemView.findViewById(R.id.ebay_image);
            ebay_title=itemView.findViewById(R.id.ebay_title);
            ebay_price=itemView.findViewById(R.id.ebay_price);
            ebay_condition=itemView.findViewById(R.id.ebay_condition);
            cart_layout=itemView.findViewById(R.id.cart_layout);
        }
    }
    public class ContactModel extends ViewModel {
        String title,imageurl,price,weburl,condition;
        public ContactModel(String title,String imageurl,String price,String weburl,String condition){
            this.title=title;
            this.imageurl=imageurl;
            this.price=price;
            this.weburl=weburl;
            this.condition=condition;
        }
        private String getTitle(){return title;}
        private String getImageurl(){return imageurl;}
        private String getPrice(){return price;}
        private String getWeburl(){return weburl;}
        private String getCondition(){return condition;}
    }
}
