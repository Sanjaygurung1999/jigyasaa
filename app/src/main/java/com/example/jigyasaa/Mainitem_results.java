package com.example.jigyasaa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.youtube.player.YouTubePlayerFragment;

public class Mainitem_results extends AppCompatActivity {
    ImageButton backButton;
    EditText searchforItemText;
    TextView removeText,applyText;
    BottomNavigationView mediaSelectionNavi;
    Fragment currentFragment=null;
    FragmentTransaction ft;
    String stringtoSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainitem_results);
        //declarations
        backButton=findViewById(R.id.backbutton);
        searchforItemText=findViewById(R.id.tosearchtext);
        removeText=findViewById(R.id.remove_text);
        applyText=findViewById(R.id.apply_text);
        mediaSelectionNavi=findViewById(R.id.item_bottomnavi);
        //applytext
        applyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Mainitem_results.class);
                i.putExtra("searchfor",searchforItemText.getText().toString());
                startActivity(i);
            }
        });
        mediaSelectionNavi.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //remove text
        removeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchforItemText.setText(null);
            }
        });
        //backbutton action
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });
        //intent data
        String itemtoSearch=getIntent().getStringExtra("searchfor");
        searchforItemText.setText(itemtoSearch);
        //itemselection navi
        ft = getSupportFragmentManager().beginTransaction();
        currentFragment = new cart();
        Bundle firstBundle=new Bundle();
        firstBundle.putString("itemtosearch",searchforItemText.getText().toString());
        currentFragment.setArguments(firstBundle);
        ft.replace(R.id.selected_item_layout, currentFragment);
        ft.commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            final Bundle bundle = new Bundle();
            stringtoSearch=searchforItemText.getText().toString();
            switch (item.getItemId()) {
                case R.id.cart:
                    currentFragment = new cart();
                    bundle.putString("itemtosearch",stringtoSearch);
                    currentFragment.setArguments(bundle);
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.selected_item_layout, currentFragment);
                    ft.commit();
                    return true;
                case R.id.youtube:
                    currentFragment = new video();
                    bundle.putString("itemtosearch",stringtoSearch);
                    currentFragment.setArguments(bundle);
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.selected_item_layout, currentFragment);
                    ft.commit();
                    return true;
                case R.id.twittertweet:
                    currentFragment = new twitter();
                    bundle.putString("itemtosearch",stringtoSearch);
                    currentFragment.setArguments(bundle);
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.selected_item_layout, currentFragment);
                    ft.commit();
                    return true;
                case R.id.picture:
                    currentFragment = new pictures();
                    bundle.putString("itemtosearch",stringtoSearch);
                    currentFragment.setArguments(bundle);
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.selected_item_layout, currentFragment);
                    ft.commit();
                    return true;
                case R.id.dictionary:
                    currentFragment = new dictionary();
                    bundle.putString("itemtosearch",stringtoSearch);
                    currentFragment.setArguments(bundle);
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.selected_item_layout, currentFragment);
                    ft.commit();
                    return true;
            }
            return false;
        }
    };
}
