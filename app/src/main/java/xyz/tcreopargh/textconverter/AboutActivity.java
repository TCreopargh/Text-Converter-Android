package xyz.tcreopargh.textconverter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        TextView version = findViewById(R.id.appVersion);
        Button sendFeedBack = findViewById(R.id.sendFeedBack);
        version.setText("版本号：" + getAppVersionName(this));
        Button update = findViewById(R.id.update);
        Button viewMySite = findViewById(R.id.viewMyWebsite);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/TCreopargh/Text-Converter-Android"));
                startActivity(intent);
            }
        });
        viewMySite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://tcreopargh.xyz"));
                startActivity(intent);
            }
        });
        sendFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:admin@tcreopargh.xyz"))
                        .putExtra(Intent.EXTRA_SUBJECT, "《文本转换》用户反馈")
                        .putExtra(Intent.EXTRA_TEXT, "请在此处附上您遇到问题的详细内容，最好能附上截图。感谢您的反馈。");
                startActivity(intent);
            }
        });
    }

    public static String getAppVersionName(Context context) {
        String versionName;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            return e.toString();
        }
        return versionName;
    }

    public void returnHome(Context context) {
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                returnHome(this);
                break;
        }
        return true;
    }
}
