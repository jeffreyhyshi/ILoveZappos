package com.example.jeffreys.ilovezappos;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.jeffreys.ilovezappos.databinding.ActivityMainBinding;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setTitle("");

        final NetworkImageView pic = (NetworkImageView) findViewById(R.id.main_image);

        final RelativeLayout content = (RelativeLayout) findViewById(R.id.include);
        final ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
        final Button button = (Button) findViewById(R.id.item_button);
        final TextView noResults = (TextView) findViewById(R.id.no_results);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewGroup) view.getParent()).removeView(view);
            }
        });

        final RequestQueue queue = Volley.newRequestQueue(this);
        final ImageLoader imageLoader = new ImageLoader(queue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(2);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
        final String baseUrl = "https://api.zappos.com/Search?term=";
        final String keyUrl = "&key=b743e26728e16b81da139182bb2094357c31d331";
        final String searchQuery = getIntent().getExtras().getString("search_query");

        JsonObjectRequest searchRequest = new JsonObjectRequest(Request.Method.GET,
                baseUrl + searchQuery + keyUrl,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        final JSONObject firstItem = response.optJSONArray("results").optJSONObject(0);
                        if (!firstItem.optString("currentResultCount").equals("0")) {
                            Item item = new Item(firstItem.optString("productName", "n/a"),
                                    firstItem.optString("price", "n/a"),
                                    firstItem.optString("percentOff", "0%"),
                                    firstItem.optString("brandName", "n/a"));

                            binding.include.setItem(item);
                            content.setVisibility(View.VISIBLE);

                            pic.setDefaultImageResId(R.drawable.ic_broken_image_white_24dp);
                            pic.setImageUrl(firstItem.optString("thumbnailImageUrl"), imageLoader);

                            bar.setVisibility(View.INVISIBLE);

                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse(firstItem.optString("productUrl"));
                                    Intent webIntent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(webIntent);
                                }
                            });
                        } else {
                            bar.setVisibility(View.INVISIBLE);
                            noResults.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        bar.setVisibility(View.INVISIBLE);
                        noResults.setVisibility(View.VISIBLE);
                    }
                });

        queue.add(searchRequest);


    }

    public class Item {
        public final String itemName;
        public final String itemPrice;
        public final String itemDiscount;
        public final String itemBrand;

        public Item(String name, String price, String discount, String brand) {
            itemName = StringEscapeUtils.unescapeHtml4(name);
            itemPrice = price;
            itemDiscount = discount.equals("0%") ? "" : discount + "off";
            itemBrand = StringEscapeUtils.unescapeHtml4(brand);
        }

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
