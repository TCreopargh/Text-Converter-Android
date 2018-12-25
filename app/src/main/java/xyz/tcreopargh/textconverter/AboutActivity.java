package xyz.tcreopargh.textconverter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import androidx.appcompat.widget.Toolbar;


public class AboutActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.colorPrimaryDark));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        TextView version = findViewById(R.id.appVersion);
        Button sendFeedBack = findViewById(R.id.sendFeedBack);
        version.setText("版本号：" + getAppVersionName(this));
        Button update = findViewById(R.id.update);
        Button viewMySite = findViewById(R.id.viewMyWebsite);
        update.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://github.com/TCreopargh/Text-Converter-Android"));
            startActivity(intent);
        });
        viewMySite.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://tcreopargh.xyz"));
            startActivity(intent);
        });
        sendFeedBack.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:admin@tcreopargh.xyz"))
                        .putExtra(Intent.EXTRA_SUBJECT, "《文本转换》用户反馈")
                        .putExtra(Intent.EXTRA_TEXT, "请在此处附上您遇到问题的详细内容，最好能附上截图。感谢您的反馈。");
                startActivity(intent);
            } catch (Exception e) {
                Toasty.error(AboutActivity.this, "请先安装邮件应用！", Toast.LENGTH_LONG).show();
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

    public void returnHome() {
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.star:
                LovelyStandardDialog lovelyStandardDialog = new LovelyStandardDialog
                        (AboutActivity.this, LovelyStandardDialog.ButtonLayout.HORIZONTAL);
                lovelyStandardDialog.setTitle("为我评分")
                        .setTopColorRes(R.color.warningYellow)
                        .setIcon(R.drawable.ic_stars_black_24dp)
                        .setMessage("您的评分将是我继续开发的动力，如果您对此App感到满意，或是发现有不足之处，欢迎留下您的宝贵评论。")
                        .setButtonsColorRes(R.color.colorAccent)
                        .setPositiveButton("好的", v -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("https://www.coolapk.com/apk/212564"));
                            startActivity(intent);
                            lovelyStandardDialog.dismiss();
                        })
                        .setNegativeButton("下次吧", v -> lovelyStandardDialog.dismiss())
                        .create().show();
                break;
            case android.R.id.home:
                returnHome();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.about_toolbar_menu, menu);
        return true;
    }
}
