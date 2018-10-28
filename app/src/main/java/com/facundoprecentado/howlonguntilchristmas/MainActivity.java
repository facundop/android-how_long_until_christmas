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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextView timeUntilText;
    Button calculateTimeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Find out how long until Christmas!");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        timeUntilText = (TextView) findViewById(R.id.timeUntilText);
        calculateTimeButton = (Button) findViewById(R.id.calculateTimeButton);
        calculateTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTime();
            }
        });

    }

    private void calculateTime() {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
