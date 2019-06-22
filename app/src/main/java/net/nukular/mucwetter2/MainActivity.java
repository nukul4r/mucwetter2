package net.nukular.mucwetter2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.nukular.mucwetter2.entity.ContentItem;
import net.nukular.mucwetter2.task.DownloadBitmapTask;
import net.nukular.mucwetter2.task.DownloadGifTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Map<String, String> links;

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

        GifImageView gifView = findViewById(R.id.content_gif_view);
        ImageView bitmapView = findViewById(R.id.content_bitmap_view);
        try {
            if (link.endsWith(".gif")) {
                gifView.setImageDrawable(new DownloadGifTask().execute(link).get());
                bitmapView.setVisibility(View.INVISIBLE);
                gifView.setVisibility(View.VISIBLE);
            } else {
                bitmapView.setImageBitmap(new DownloadBitmapTask().execute(link).get());
                gifView.setVisibility(View.INVISIBLE);
                bitmapView.setVisibility(View.VISIBLE);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String loadItemsJson() {
        String json = null;
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
