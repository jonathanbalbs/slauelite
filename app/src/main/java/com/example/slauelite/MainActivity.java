package com.example.slauelite;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.asitrack.fontawesome2.FontAwesome.FontAwesomeSolidView;
import com.asitrack.fontawesome2.FontAwesome.FontAwesomeUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.slauelite.ui.main.SectionsPagerAdapter;

import java.sql.Array;

import info.androidhive.fontawesome.FontDrawable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        viewPager.setCurrentItem(1);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        createIcons(tabs);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //setIconColors(viewPager);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //setIconColors(viewPager);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public void createIcons (TabLayout tabs) {

        FontDrawable calendar = new FontDrawable(this, R.string.fa_calendar_check_solid, true, false);
        FontDrawable book = new FontDrawable(this, R.string.fa_book_open_solid, true, false);
        FontDrawable search = new FontDrawable(this, R.string.fa_search_solid, true, false);

        calendar.setTextColor(ContextCompat.getColor(this, R.color.colorGrayLight));
        book.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        search.setTextColor(ContextCompat.getColor(this, R.color.colorGrayLight));

        tabs.getTabAt(0).setIcon(calendar);
        tabs.getTabAt(1).setIcon(book);
        tabs.getTabAt(2).setIcon(search);

    }

    //set tab icon colors
    public void setIconColors (ViewPager viewPager, TabLayout tabs) {

        int position = viewPager.getCurrentItem();

        Drawable calendar = tabs.getTabAt(0).getIcon();
        Drawable book = tabs.getTabAt(1).getIcon();
        Drawable search = tabs.getTabAt(2).getIcon();

        /*switch (position) {
            case 0:
                calendar.setT
                book.setTextColor(R.color.colorSecondary);
                search.setTextColor(R.color.colorSecondary);
                break;

            case 1:
                calendar.setTextColor(R.color.colorSecondary);
                book.setTextColor(R.color.colorAccent);
                search.setTextColor(R.color.colorSecondary);
                break;

            case 2:
                calendar.setTextColor(R.color.colorSecondary);
                book.setTextColor(R.color.colorSecondary);
                search.setTextColor(R.color.colorAccent);
                break;


            default:
                throw new IllegalStateException("Unexpected value: " + position);
        }*/
    }
}