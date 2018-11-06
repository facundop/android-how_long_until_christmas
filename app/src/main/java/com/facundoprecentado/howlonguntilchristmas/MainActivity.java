package com.facundoprecentado.howlonguntilchristmas;

import android.annotation.TargetApi;
import android.content.Intent;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import org.w3c.dom.Text;

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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private RewardedVideoAd mRewardedVideoAd;
    private String TestRewardedVideoAd = "ca-app-pub-3940256099942544/5224354917";
    private String RewardedVideoAd = "ca-app-pub-1088902000251944/6033351380";

    private TextInputLayout daysEditText;
    private TextInputLayout hoursEditText;
    private TextInputLayout minutesEditText;
    private TextInputLayout secondsEditText;

    private TextView merryChristmasText;

    private Button calculateTimeButton;
    private FloatingActionButton shareButton;
    private FloatingActionButton rateButton;

    private ConstraintLayout mainContentConstraintLayout;

    // TODO: Move to a select screen
    int[] drawablearray=new int[]{R.drawable.background_01,
            R.drawable.background_02,
            R.drawable.background_03,
            R.drawable.background_04,
            R.drawable.background_05,
            R.drawable.background_06,
            R.drawable.background_07};
    Timer _t;
    public static int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        calculateTimeButton = (Button) findViewById(R.id.calculateTimeButton);
        calculateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTime();
            }
        });
        calculateTimeButton.setClickable(false);
        calculateTimeButton.setText(R.string.loading);

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
        rateButton = (FloatingActionButton) findViewById(R.id.rateButton);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rateIntent = new Intent();
                rateIntent.setAction(Intent.ACTION_VIEW);
                rateIntent.setData(Uri.parse("market://details?id=" + getPackageName()));
                startActivity(rateIntent);
            }
        });

        daysEditText = (TextInputLayout) findViewById(R.id.days_text_input);
        daysEditText.setVisibility(View.GONE);
        hoursEditText = (TextInputLayout) findViewById(R.id.hours_text_input);
        hoursEditText.setVisibility(View.GONE);
        minutesEditText = (TextInputLayout) findViewById(R.id.minutes_text_input);
        minutesEditText.setVisibility(View.GONE);
        secondsEditText = (TextInputLayout) findViewById(R.id.seconds_text_input);
        secondsEditText.setVisibility(View.GONE);

        merryChristmasText = (TextView) findViewById(R.id.merryChristmasView);
        merryChristmasText.setVisibility(View.GONE);

        loadRewardedVideoAd();

        mainContentConstraintLayout = (ConstraintLayout) findViewById(R.id.mainContentConstraintLayout);

        _t = new Timer();
        _t.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                    public void run() {
                        if (count < drawablearray.length) {
                            mainContentConstraintLayout.setBackgroundResource(drawablearray[count]);
                            count = (count + 1) % drawablearray.length;
                        }
                    }
                });
            }
        }, 1000, 10000);
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
        long diff;

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

                String hoursTimer = hours > 9 ? String.valueOf(hours) : "0" + String.valueOf(hours);
                String minutesTimer = minutes > 9 ? String.valueOf(minutes) : "0" + String.valueOf(minutes);
                String secondsTimer = seconds > 9 ? String.valueOf(seconds) : "0" + String.valueOf(seconds);

                daysEditText.setVisibility(View.VISIBLE);
                daysEditText.getEditText().setText(String.valueOf(days));
                hoursEditText.setVisibility(View.VISIBLE);
                hoursEditText.getEditText().setText(String.valueOf(hoursTimer));
                minutesEditText.setVisibility(View.VISIBLE);
                minutesEditText.getEditText().setText(String.valueOf(minutesTimer));
                secondsEditText.setVisibility(View.VISIBLE);
                secondsEditText.getEditText().setText(String.valueOf(secondsTimer));

            }

            public void onFinish() {
                daysEditText.setVisibility(View.GONE);
                hoursEditText.setVisibility(View.GONE);
                minutesEditText.setVisibility(View.GONE);
                secondsEditText.setVisibility(View.GONE);

                merryChristmasText.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void changeBackgroundImage() {
        mainContentConstraintLayout = (ConstraintLayout) findViewById(R.id.mainContentConstraintLayout);
        mainContentConstraintLayout.setBackgroundResource(R.drawable.background_02);
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

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "Sorry, you need to finish watching the Ad for the timer to appear.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() { loadRewardedVideoAd(); }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Toast.makeText(this, "An error occurred. Trying to load again.", Toast.LENGTH_SHORT).show();
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        calculateTimeButton.setClickable(true);
        calculateTimeButton.setText(R.string.calculate_time_button);
        Toast.makeText(this, "Resource loaded.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdOpened() {}

    @Override
    public void onRewardedVideoStarted() {}

    @Override
    public void onRewardedVideoCompleted() {}
}
