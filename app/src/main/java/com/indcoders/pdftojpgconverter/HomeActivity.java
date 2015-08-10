package com.indcoders.pdftojpgconverter;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity
        implements PDF2JPGFragment.OnFragmentInteractionListener, HistoryFragment.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    Toolbar bar;
    int convert = 1, history = 2, active = convert;
    MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(bar);
        item = (MenuItem) findViewById(R.id.action_example);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PDF2JPGFragment.newInstance(null, null))
                .commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "In Progress..", Toast.LENGTH_SHORT).show();

            return true;
        }
        if (id == R.id.action_example) {


            if (active == convert) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, HistoryFragment.newInstance(null, null))
                        .commit();
                item.setTitle("Converter");
                active = history;
            } else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PDF2JPGFragment.newInstance(null, null))
                        .commit();
                item.setTitle("History");
                active = convert;
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);

    }
}
