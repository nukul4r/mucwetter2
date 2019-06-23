package net.nukular.mucwetter2;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ortiz.touchview.TouchImageView;

import net.nukular.mucwetter2.entity.ContentItem;
import net.nukular.mucwetter2.task.DownloadBitmapTask;
import net.nukular.mucwetter2.task.DownloadGifTask;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Map<String, String> links;
    private MenuItem aboutItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        initNavigationView(navigationView);
        initAboutView();
    }

    private void initNavigationView(NavigationView navigationView) {
        Gson gson = new GsonBuilder().create();

        TypeToken<List<ContentItem>> token = new TypeToken<List<ContentItem>>() {
        };
        List<ContentItem> items = gson.fromJson(loadItemsJson(), token.getType());

        links = new HashMap<>();

        for (ContentItem item : items) {
            Menu menu = navigationView.getMenu();
            MenuItem newItem = menu.add(item.label);
            links.put(newItem.getTitle().toString(), item.link);
        }

        findViewById(R.id.content_gif_view).setVisibility(View.INVISIBLE);
        findViewById(R.id.content_bitmap_view).setVisibility(View.INVISIBLE);
        findViewById(R.id.spinner).setVisibility(View.INVISIBLE);

        Menu menu = navigationView.getMenu();
        aboutItem = menu.add(1, 0, 0, "Info");
    }

    private void initAboutView() {
        TextView github = findViewById(R.id.about_github);
        github.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/nukul4r/mucwetter2"))));
        github.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_github, 0, 0, 0);
        github.setGravity(Gravity.CENTER_VERTICAL);
        github.setCompoundDrawablePadding(getApplicationContext().getResources().getDimensionPixelOffset(R.dimen.small_padding));

        TextView btc = findViewById(R.id.about_btc);
        btc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("16E9jH7ew5EszUkx8LFwT8ojiDLJox5xaY", "16E9jH7ew5EszUkx8LFwT8ojiDLJox5xaY");
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
        btc.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_bitcoin, 0, 0, 0);
        btc.setGravity(Gravity.CENTER_VERTICAL);
        btc.setCompoundDrawablePadding(getApplicationContext().getResources().getDimensionPixelOffset(R.dimen.small_padding));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        String link = links.get(item.getTitle().toString());
        boolean isGif = StringUtils.isNotBlank(link) && link.endsWith(".gif");

        GifImageView gifView = findViewById(R.id.content_gif_view);
        TouchImageView bitmapView = findViewById(R.id.content_bitmap_view);
        View spinner = findViewById(R.id.spinner);
        View about = findViewById(R.id.content_about);

        gifView.setVisibility(View.INVISIBLE);
        bitmapView.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);
        about.setVisibility(View.INVISIBLE);

        bitmapView.resetZoom();

        if (aboutItem == item) {
            spinner.setVisibility(View.INVISIBLE);
            about.setVisibility(View.VISIBLE);
        } else if (isGif)
            new DownloadGifTask(gifView, spinner).execute(link);
        else {
            new DownloadBitmapTask(bitmapView, spinner).execute(link);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public String loadItemsJson() {
        String json;
        try {
            InputStream is = getApplicationContext().getAssets().open("items.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
