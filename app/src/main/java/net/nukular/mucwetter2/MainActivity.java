package net.nukular.mucwetter2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

        initHome();
        showHome();
    }

    private void initHome() {
        String content = "";

        InputStream is;
        try {
            is = getAssets().open("home.html");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            content = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ((WebView) findViewById(R.id.home)).loadData(content, "text/html", "UTF-8");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        String imageUrl = item.getTitleCondensed().toString();

        if (item.getItemId() == R.id.nav_home) {
            showHome();
        } else if (imageUrl.endsWith(".gif")) {
            new DownloadGifTask((GifImageView) findViewById(R.id.content_gif_view)).execute(imageUrl);
            findViewById(R.id.content_bitmap_view).setVisibility(View.INVISIBLE);
            findViewById(R.id.content_gif_view).setVisibility(View.VISIBLE);
        } else {
            new DownloadBitmapTask((PhotoView) findViewById(R.id.content_bitmap_view)).execute(imageUrl);
            findViewById(R.id.content_gif_view).setVisibility(View.INVISIBLE);
            findViewById(R.id.content_bitmap_view).setVisibility(View.VISIBLE);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showHome() {
        findViewById(R.id.content_bitmap_view).setVisibility(View.INVISIBLE);
        findViewById(R.id.content_gif_view).setVisibility(View.INVISIBLE);
    }


}
