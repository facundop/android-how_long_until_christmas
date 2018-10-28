package com.facundoprecentado.howlonguntilchristmass;

import android.icu.util.TimeZone;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import java.sql.Time;
import java.util.Date;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        // Get local TimeZone
        TimeZone tz = TimeZone.getDefault();
        DateTimeZone localTimeZone = DateTimeZone.forID(tz.getID());

        DateTime start = new DateTime(localTimeZone);
        DateTime end = new DateTime(start.getYear(), 12, 24, 0, 0, 0, localTimeZone);

        int daysBetweenDates = Days.daysBetween(start, end).getDays();
        int hoursBetweenDates = Hours.hoursBetween(start, end).getHours();
        int minutesBetweenDates = Minutes.minutesBetween(start, end).getMinutes();

        timeUntilText.setText("Days Until Christmass: " + daysBetweenDates + " Hours: " + hoursBetweenDates % 24 + " Minutes: " + minutesBetweenDates % 60);
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
