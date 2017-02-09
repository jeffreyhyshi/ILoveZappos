package com.example.jeffreys.ilovezappos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final EditText searchText = (EditText) findViewById(R.id.search_box);
        RequestQueue queue = Volley.newRequestQueue(this);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == IME_ACTION_DONE) {
                    Intent openPageIntent = new Intent(getApplicationContext(), MainActivity.class);
                    Bundle searchQueryBundle = new Bundle();
                    String searchQuery = v.getText().toString();
                    searchQueryBundle.putString("search_query", searchQuery);

                    openPageIntent.putExtras(searchQueryBundle);
                    startActivity(openPageIntent);
                    return true;
                }
                return false;
            }
        });
    }
}
