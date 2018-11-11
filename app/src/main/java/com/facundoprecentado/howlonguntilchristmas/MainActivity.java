package com.facundoprecentado.howlonguntilchristmas;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    static final String TAG = "HowLongUntilChristmas";


    private AdView mBannerAdView;
    private String AppId = "ca-app-pub-1088902000251944~5103413095";

    private TextInputLayout daysEditText;
    private TextInputLayout hoursEditText;
    private TextInputLayout minutesEditText;
    private TextInputLayout secondsEditText;

    private TextView merryChristmasText;

    private FloatingActionButton shareButton;
    private FloatingActionButton rateButton;
    private FloatingActionButton selectBackgroundButton;

    private ConstraintLayout contentLayout;

    private SharedPreferences sharedPref;

    // Billing
    private BillingClient mBillingClient;
    static final String SKU_PREMIUM = "premium";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "HowLongUntilChristmas started");

        mBillingClient = BillingClient.newBuilder(this).setListener(this).build();

        checkPurchases();

        isUserPremium();

        startTimerUntilChristmas();

        findViewById(R.id.shareButton).setOnClickListener(shareButtonOnClickListener);
        findViewById(R.id.rateButton).setOnClickListener(rateButtonOnClickListener);
        findViewById(R.id.selectBackgroundButton).setOnClickListener(selectBackgroundButtonOnClickListener);

        daysEditText = findViewById(R.id.days_text_input);
        daysEditText.setVisibility(View.GONE);
        hoursEditText = findViewById(R.id.hours_text_input);
        hoursEditText.setVisibility(View.GONE);
        minutesEditText = findViewById(R.id.minutes_text_input);
        minutesEditText.setVisibility(View.GONE);
        secondsEditText = findViewById(R.id.seconds_text_input);
        secondsEditText.setVisibility(View.GONE);

        merryChristmasText = findViewById(R.id.merryChristmasView);
        merryChristmasText.setVisibility(View.GONE);

        buyPremium();

    }

    private void buyPremium() {
        Log.i(TAG, "buyPremium");
        // TODO: Implement buyPremium flow
    }

    private void checkPurchases() {
        Log.i(TAG, "checkPurchases");
        Purchase.PurchasesResult purchases = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        List<Purchase> purchasesList = purchases.getPurchasesList();
        if(purchasesList != null) {
            for (Purchase purchase : purchasesList) {
                Log.i(TAG, purchase.getSku());
            }
        }
    }

    private void isUserPremium() {
        Log.i(TAG, "Check if user is premium.");

        // Banner Ad.
        sharedPref = this.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        boolean premium = sharedPref.getBoolean("premium", false);

        if(!premium) {
            MobileAds.initialize(this, AppId);
            mBannerAdView = findViewById(R.id.bannerAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mBannerAdView.loadAd(adRequest);
        }

    }

    // TODO: Emprolijar flujo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        sharedPref = this.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        int background = sharedPref.getInt("background_resource", R.drawable.background_01);

        contentLayout = findViewById(R.id.mainContentConstraintLayout);
        contentLayout.setBackgroundResource(background);

    }

    private void startTimerUntilChristmas() {
        Log.i(TAG, "Timer started.");
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

    // Not replacing with Calendar. Just waiting for devices to move up to API 26 to deprecate this.
    private long getDiffUntilChristmasInMillisForOlderDevices() {
        try {
            Date now = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
            Date christmas = sdf.parse("24/12/" + (now.getYear() + 1900) + " 00:00:00");
            return christmas.getTime() - now.getTime();
        } catch (ParseException e) {
            // Nothing to do. To be deprecated.
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
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // Button Listeners
    private View.OnClickListener shareButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_msg) + " https://goo.gl/dTXAbo");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
    };

    private View.OnClickListener rateButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent rateIntent = new Intent();
            rateIntent.setAction(Intent.ACTION_VIEW);
            rateIntent.setData(Uri.parse("market://details?id=" + getPackageName()));
            startActivity(rateIntent);
        }
    };

    private View.OnClickListener selectBackgroundButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent selectBackgroundIntent = new Intent(MainActivity.this, SelectBackgroundActivity.class);
            startActivityForResult(selectBackgroundIntent, 0);
        }
    };
    // End Button Listeners

}
