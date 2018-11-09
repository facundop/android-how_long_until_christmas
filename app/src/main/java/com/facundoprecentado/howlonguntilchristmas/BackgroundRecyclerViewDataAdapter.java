package com.facundoprecentado.howlonguntilchristmas;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class BackgroundRecyclerViewDataAdapter extends RecyclerView.Adapter<BackgroundRecyclerViewItemHolder> {

    private List<BackgroundRecyclerViewItem> backgroundItemList;

    public BackgroundRecyclerViewDataAdapter(List<BackgroundRecyclerViewItem> backgroundItemList) {
        this.backgroundItemList = backgroundItemList;
    }

    @Override
    public BackgroundRecyclerViewItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get LayoutInflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the RecyclerView item layout xml.
        final View backgroundItemView = layoutInflater.inflate(R.layout.activity_card_view_item, parent, false);

        // Get background title text view object.
        final TextView backgroundTitleView = (TextView) backgroundItemView.findViewById(R.id.card_view_image_title);
        // Get background image view object.
        final ImageView backgroundImageView = (ImageView) backgroundItemView.findViewById(R.id.card_view_image);

        // When click the image.
        backgroundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get background title text.
                String backgroundTitle = backgroundTitleView.getText().toString();
                // Create a snackbar and show it.
                Snackbar snackbar = Snackbar.make(backgroundImageView, "Background " + backgroundTitle + " selected", Snackbar.LENGTH_LONG);

                Activity myActivity=(Activity)(v.getContext()); // all views have a reference to their context
                SharedPreferences sharedPref = myActivity.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                // TODO: Emprolijar el flujo.
                switch (backgroundTitle) {
                    case "Default":
                        editor.putInt("background_resource", R.drawable.background_01);
                        break;
                    case "Red Christmas":
                        editor.putInt("background_resource", R.drawable.background_02);
                        break;
                    case "Classic":
                        editor.putInt("background_resource", R.drawable.background_03);
                        break;
                    case "Ornament 1":
                        editor.putInt("background_resource", R.drawable.background_04);
                        break;
                    case "Ornament 2":
                        editor.putInt("background_resource", R.drawable.background_05);
                        break;
                    case "White Tree":
                        editor.putInt("background_resource", R.drawable.background_06);
                        break;
                    case "Ornament 3":
                        editor.putInt("background_resource", R.drawable.background_07);
                        break;
                    default:
                }
                editor.apply();

                snackbar.show();
            }
        });

        // Create and return our custom Background Recycler View Item Holder object.
        BackgroundRecyclerViewItemHolder ret = new BackgroundRecyclerViewItemHolder(backgroundItemView);
        return ret;
    }

    @Override
    public void onBindViewHolder(BackgroundRecyclerViewItemHolder holder, int position) {
        if (backgroundItemList != null) {
            // Get background item dto in list.
            BackgroundRecyclerViewItem backgroundItem = backgroundItemList.get(position);

            if (backgroundItem != null) {
                // Set background item title.
                holder.getBackgroundTitleText().setText(backgroundItem.getBackgroundName());
                // Set background image resource id.
                holder.getBackgroundImageView().setImageResource(backgroundItem.getBackgroundImageId());
            }
        }
    }

    @Override
    public int getItemCount() {
        int ret = 0;
        if (backgroundItemList != null) {
            ret = backgroundItemList.size();
        }
        return ret;
    }
}