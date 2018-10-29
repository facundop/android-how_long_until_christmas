package com.facundoprecentado.howlonguntilchristmas;

import android.content.Intent;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private RewardedVideoAd mRewardedVideoAd;
    private TextView timeUntilText;
    private Button calculateTimeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        timeUntilText = (TextView) findViewById(R.id.timeUntilText);
        calculateTimeButton = (Button) findViewById(R.id.calculateTimeButton);
        calculateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTime();
            }
        });

        loadRewardedVideoAd();
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-1088902000251944/6033351380",
                new AdRequest.Builder().build());
    }

    private void calculateTime() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Find out how long until Christmas with this App!");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRewarded(RewardItem reward) {
        // Load a new Ad
        loadRewardedVideoAd();

        // Reward the user.
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime now = ldt.atZone(ZoneId.of(TimeZone.getDefault().getID()));
        ZonedDateTime christmas = ZonedDateTime.of ( LocalDate.of ( now.getYear() , 12 , 24 ) , LocalTime.of ( 0 , 0 ) , ZoneId.of (TimeZone.getDefault().getID()));

        long diff = ChronoUnit.MILLIS.between(now, christmas);

        new CountDownTimer(diff, 1000) {

            public void onTick(long millisBetweenDates) {
                // Days
                long days = TimeUnit.MILLISECONDS.toDays(millisBetweenDates);

                // Hours
                long diffMinusDays = millisBetweenDates - TimeUnit.DAYS.toMillis(days);
                long hours = TimeUnit.MILLISECONDS.toHours(diffMinusDays);

                // Minutes
                long diffMinusHours = millisBetweenDates - (TimeUnit.DAYS.toMillis(days) + TimeUnit.HOURS.toMillis(hours));
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMinusHours);

                // Seconds
                long diffMinusMinutes =  millisBetweenDates - (TimeUnit.DAYS.toMillis(days) + TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes));
                long seconds = TimeUnit.MILLISECONDS.toSeconds(diffMinusMinutes);

                timeUntilText.setText("Christmas is coming in " + days + " days and " + hours + ":" + minutes + ":" + seconds);
            }

            public void onFinish() {
                timeUntilText.setText("Merry Christmas!!!");
            }
        }.start();
    }

    private void showError() {
        Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() { loadRewardedVideoAd(); }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdLoaded() {}

    @Override
    public void onRewardedVideoAdOpened() {}

    @Override
    public void onRewardedVideoStarted() {}

    @Override
    public void onRewardedVideoCompleted() {}
}
