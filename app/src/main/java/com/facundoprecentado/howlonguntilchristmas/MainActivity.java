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
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.google.android.gms.ads.AdRequest;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private AdView mBannerAdView;
    private String AppId = "ca-app-pub-1088902000251944~5103413095";

    // String bannerAd = ca-app-pub-1088902000251944/1965654411
    // String testBannerAd = ca-app-pub-3940256099942544/6300978111

    private TextInputLayout daysEditText;
    private TextInputLayout hoursEditText;
    private TextInputLayout minutesEditText;
    private TextInputLayout secondsEditText;

    private TextView merryChristmasText;

    private ConstraintLayout contentLayout;

    private SharedPreferences sharedPref;
    private int prefBackground;

    // Billing
    private BillingClient mBillingClient;
    static final String SKU_PREMIUM = "premium";
    private boolean isUserPremium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();

        mBillingClient = BillingClient.newBuilder(this).setListener(this).build();

        connectBillingClient();

        checkPurchases();
        isUserPremium();

        startTimerUntilChristmas();

        findViewById(R.id.shareButton).setOnClickListener(shareButtonOnClickListener);
        findViewById(R.id.rateButton).setOnClickListener(rateButtonOnClickListener);
        findViewById(R.id.selectBackgroundButton).setOnClickListener(selectBackgroundButtonOnClickListener);
        findViewById(R.id.buyPremiumButton).setOnClickListener(buyPremiumButtonOnClickListener);

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
    }

    private void connectBillingClient() {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {

            }
            @Override
            public void onBillingServiceDisconnected() {

            }
        });
    }

    private void buyPremium() {
        // Miro el historial de purchases para ver si tiene premium. Si lo tiene, no hay nada para comprar.
        checkPurchases();

        if(!isUserPremium) {
            startPurchaseFlow(SKU_PREMIUM, BillingClient.SkuType.INAPP);
        }
    }

    public void startPurchaseFlow(final String skuId, final String billingType) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setType(billingType)
                .setSku(skuId)
                .build();
        mBillingClient.launchBillingFlow(MainActivity.this, billingFlowParams);
    }

    private void checkPurchases() {
        Purchase.PurchasesResult purchases = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
        List<Purchase> purchasesList = purchases.getPurchasesList();
        if(purchasesList != null) {
            for (Purchase purchase : purchasesList) {
                if (purchase.getSku().equals(SKU_PREMIUM)) {
                    makeUserPremium();
                }
            }
        }
    }

    private void makeUserPremium() {
        findViewById(R.id.buyPremiumButton).setVisibility(View.GONE);
        findViewById(R.id.bannerAdView).setVisibility(View.GONE);
    }

    private void isUserPremium() {
        if(!isUserPremium) {
            MobileAds.initialize(this, AppId);
            mBannerAdView = findViewById(R.id.bannerAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mBannerAdView.loadAd(adRequest);
        } else {
            findViewById(R.id.buyPremiumButton).setVisibility(View.GONE);
        }
    }

    // TODO: Emprolijar flujo. Tengo que saber si vengo de comprar algo o de seleccionar un fondo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        sharedPref = this.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        int background = sharedPref.getInt("background_resource", R.drawable.background_01);

        contentLayout = findViewById(R.id.mainContentConstraintLayout);
        contentLayout.setBackgroundResource(background);

    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        if(responseCode != 0) {
            Toast.makeText(this, "An error occurred processing the payment.", Toast.LENGTH_SHORT).show();
        }
        if (responseCode == 0 && purchases != null) {
            for (Purchase purchase : purchases) {
                Toast.makeText(this, "Thank you for your support. Restart the App to remove the Ads.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    // Preferences - Preferred background & premium status
    private void loadData() {
        sharedPref = this.getSharedPreferences("my_prefs", Context.MODE_PRIVATE);

        prefBackground = sharedPref.getInt("background_resource", R.drawable.background_01);
        findViewById(R.id.mainContentConstraintLayout).setBackgroundResource(prefBackground);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("premium", false);
    }

    // Christmas Timer
    private void startTimerUntilChristmas() {
        long diff;

        if(Build.VERSION.SDK_INT >= 26) {
            diff = getDiffUntilChristmasInMillis();
        } else {
            diff = getDiffUntilChristmasInMillisForOlderDevices();
        }

        new CountDownTimer(diff, 1000) {

            public void onTick(long millisBetweenDates) {
                long days = TimeUnit.MILLISECONDS.toDays(millisBetweenDates);

                long diffMinusDays = millisBetweenDates - TimeUnit.DAYS.toMillis(days);
                long hours = TimeUnit.MILLISECONDS.toHours(diffMinusDays);

                long diffMinusHours = millisBetweenDates - (TimeUnit.DAYS.toMillis(days) + TimeUnit.HOURS.toMillis(hours));
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMinusHours);

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
    // End Christmas Timer

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

    private View.OnClickListener buyPremiumButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            buyPremium();
        }
    };
    // End Button Listeners

}
