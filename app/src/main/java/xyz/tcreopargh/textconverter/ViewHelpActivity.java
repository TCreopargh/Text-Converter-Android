package xyz.tcreopargh.textconverter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;
import java.util.Objects;

public class ViewHelpActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_help);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        WebView viewHelp = findViewById(R.id.viewHelpPage);
        viewHelp.setWebViewClient(new WebViewClient());
        /*viewHelp.getSettings().setJavaScriptEnabled(true);
        viewHelp.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                handler.proceed();
            }
        });*/
        setTitle(getString(R.string.title_activity_view_help));
        String newValue =
            getSharedPreferences("settings", MODE_PRIVATE).getString("appLanguage", "auto");
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = getResources().getConfiguration().locale;
        }
        String lang = "";
        if (newValue != null) {
            switch (newValue) {
                case "auto":
                    lang = locale.getLanguage() + "-" + locale.getCountry();
                    break;
                case "en-us":
                    lang = "en-US";
                    break;
                case "zh-cn":
                    lang = "zh-CN";
                    break;
                case "zh-hk":
                    lang = "zh-HK";
                    break;
            }
        }
        if (lang.equalsIgnoreCase("zh-CN")) {
            viewHelp.loadUrl("file:///android_asset/help_document_zh_simplified.html");
        } else if (lang.equalsIgnoreCase("zh-TW") || lang.equalsIgnoreCase("zh-HK")) {
            viewHelp.loadUrl("file:///android_asset/help_document_zh_traditional.html");
        } else {
            viewHelp.loadUrl("file:///android_asset/help_document_en.html");
        }
    }

    public void returnHome() {
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                returnHome();
                break;
        }
        return true;
    }
}
