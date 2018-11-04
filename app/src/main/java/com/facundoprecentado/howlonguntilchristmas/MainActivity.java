package com.facundoprecentado.howlonguntilchristmas;

import android.annotation.TargetApi;
import android.content.Intent;
import android.icu.util.TimeZone;
import android.os.Build;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private RewardedVideoAd mRewardedVideoAd;
    private TextView daysUntilText;
    private TextView hoursUntilText;
    private Button calculateTimeButton;
    private String TestRewardedVideoAd = "ca-app-pub-3940256099942544/5224354917";
    private String RewardedVideoAd = "ca-app-pub-1088902000251944/6033351380";
    private FloatingActionButton shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        daysUntilText = (TextView) findViewById(R.id.daysUntilText);
        hoursUntilText = (TextView) findViewById(R.id.hoursUntilText);
        calculateTimeButton = (Button) findViewById(R.id.calculateTimeButton);
        calculateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTime();
            }
        });
        shareButton = (FloatingActionButton) findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent sendIntent = new Intent();
               sendIntent.setAction(Intent.ACTION_SEND);
               sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_msg) + " https://goo.gl/dTXAbo");
               sendIntent.setType("text/plain");
               startActivity(sendIntent);
           }
        });

        loadRewardedVideoAd();
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(RewardedVideoAd,
                new AdRequest.Builder().build());
    }

    private void calculateTime() {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewarded(RewardItem reward) {
        // Load a new Ad
        loadRewardedVideoAd();

        // Reward the user.
        long diff = 0;

        // Newer API can use LocalDateTime, ZonedDateTime and ChronoUnit
        if(Build.VERSION.SDK_INT >= 26) {
            diff = getDiffUntilChristmasInMillis();
        } else {
            diff = getDiffUntilChristmasInMillisForOlderDevices();
        }

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

                String daysTimer = days == 1 ? getString(R.string.day) : getString(R.string.days);
                String hoursTimer = hours > 9 ? String.valueOf(hours) : "0" + String.valueOf(hours);
                String minutesTimer = minutes > 9 ? String.valueOf(minutes) : "0" + String.valueOf(minutes);
                String secondsTimer = seconds > 9 ? String.valueOf(seconds) : "0" + String.valueOf(seconds);

                daysUntilText.setText(days + " " + daysTimer);
                hoursUntilText.setText(hoursTimer + ":" + minutesTimer + ":" + secondsTimer);
            }

            public void onFinish() {
                daysUntilText.setText(getString(R.string.merry));
                hoursUntilText.setText(R.string.christmas);
            }
        }.start();
    }

    // Not replacing with Calendar. Just waiting for devices to move up to API 26 to deprecate this.
    private long getDiffUntilChristmasInMillisForOlderDevices() {
        try {
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
            Date christmas = sdf.parse("24/12/" + (now.getYear() + 1900) + " 00:00:00");
            return christmas.getTime() - now.getTime();
        } catch (ParseException e) {
            // TODO: Fix this crap
        }
        return 0;
    }

    @TargetApi(26)
    private long getDiffUntilChristmasInMillis() {
        LocalDateTime ldt = LocalDateTime.now();
        ZonedDateTime now = ldt.atZone(ZoneId.of(TimeZone.getDefault().getID()));
        ZonedDateTime christmas = ZonedDateTime.of ( LocalDate.of ( now.getYear() , 12 , 24 ) , LocalTime.of ( 0 , 0 ) , ZoneId.of (TimeZone.getDefault().getID()));

        return ChronoUnit.MILLIS.between(now, christmas);
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
