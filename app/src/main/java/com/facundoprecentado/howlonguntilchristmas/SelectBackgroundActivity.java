package com.facundoprecentado.howlonguntilchristmas;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SelectBackgroundActivity extends AppCompatActivity {

    private List<BackgroundRecyclerViewItem> backgroundItemList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_background);
        setTitle("Select your background");

        initializeBackgroundItemList();

        // Create the recyclerview.
        RecyclerView backgroundRecyclerView = (RecyclerView)findViewById(R.id.background_view_recycler_list);
        // Create and set the grid layout manager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        backgroundRecyclerView.setLayoutManager(gridLayoutManager);

        // Create and set background recycler view data adapter with background item list.
        BackgroundRecyclerViewDataAdapter backgroundDataAdapter = new BackgroundRecyclerViewDataAdapter(backgroundItemList);
        backgroundRecyclerView.setAdapter(backgroundDataAdapter);
    }

    // Initialise background items in list
    private void initializeBackgroundItemList()
    {
        if(backgroundItemList == null)
        {
            backgroundItemList = new ArrayList<BackgroundRecyclerViewItem>();
            backgroundItemList.add(new BackgroundRecyclerViewItem("Default", R.drawable.background_01));
            backgroundItemList.add(new BackgroundRecyclerViewItem("Red Christmas", R.drawable.background_02));
            backgroundItemList.add(new BackgroundRecyclerViewItem("Classic", R.drawable.background_03));
            backgroundItemList.add(new BackgroundRecyclerViewItem("Ornament 1", R.drawable.background_04));
            backgroundItemList.add(new BackgroundRecyclerViewItem("Ornament 2", R.drawable.background_05));
            backgroundItemList.add(new BackgroundRecyclerViewItem("White Tree", R.drawable.background_06));
            backgroundItemList.add(new BackgroundRecyclerViewItem("Ornament 3", R.drawable.background_07));
        }
    }

}
