package com.facundoprecentado.howlonguntilchristmas;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BackgroundRecyclerViewItemHolder extends RecyclerView.ViewHolder {

    private TextView backgroundTitleText = null;
    private ImageView backgroundImageView = null;

    public BackgroundRecyclerViewItemHolder(View itemView) {
        super(itemView);

        if(itemView != null) {
            backgroundTitleText = (TextView)itemView.findViewById(R.id.card_view_image_title);
            backgroundImageView = (ImageView)itemView.findViewById(R.id.card_view_image);
        }
    }

    public TextView getBackgroundTitleText() {
        return backgroundTitleText;
    }

    public ImageView getBackgroundImageView() {
        return backgroundImageView;
    }
}