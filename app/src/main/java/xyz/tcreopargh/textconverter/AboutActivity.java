package xyz.tcreopargh.textconverter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import de.mateware.snacky.Snacky;
import es.dmoral.toasty.Toasty;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import androidx.appcompat.widget.Toolbar;

public class AboutActivity extends AppCompatActivity {

    int clicks = 100;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.colorPrimaryDark));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toasty.Config.getInstance().setInfoColor(getColor(R.color.colorPrimary)).apply();
        Toolbar toolbar = findViewById(R.id.toolbar1);
        ImageView icon = findViewById(R.id.imageView2);
        icon.setOnClickListener(
                v -> {
                    clicks--;
                    if (clicks <= 97 && clicks > 1) {

                        Toasty.info(
                                        AboutActivity.this,
                                        "还需" + clicks + "次点击即可解锁彩蛋！",
                                        Toast.LENGTH_SHORT)
                                .show();
                    } else if (clicks == 1) {
                        Snacky.builder()
                                .setActivity(AboutActivity.this)
                                .setText("点击右边的按钮即可进入困难模式")
                                .setDuration(Snacky.LENGTH_LONG)
                                .setActionText("点我")
                                .setActionClickListener(
                                        v1 -> {
                                            Intent intent = new Intent();
                                            intent.putExtra("easter_egg", true);
                                            Snacky.builder()
                                                    .setActivity(AboutActivity.this)
                                                    .setDuration(Snacky.LENGTH_LONG)
                                                    .setText("已进入困难模式，请返回主界面查看")
                                                    .centerText()
                                                    .success()
                                                    .show();
                                            setResult(RESULT_OK, intent);
                                            clicks = 100;
                                        })
                                .warning()
                                .show();
                    } else if (clicks == 0) {
                        Snacky.builder()
                                .setActivity(AboutActivity.this)
                                .setDuration(Snacky.LENGTH_LONG)
                                .setText("哈哈哈，你完美错过了彩蛋！")
                                .centerText()
                                .error()
                                .show();
                        clicks = 100;
                    }
                });
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        TextView version = findViewById(R.id.appVersion);
        Button sendFeedBack = findViewById(R.id.sendFeedBack);
        version.setText("版本号：" + getAppVersionName(this));
        Button update = findViewById(R.id.update);
        Button viewMySite = findViewById(R.id.viewMyWebsite);
        update.setOnClickListener(
                v -> {
                    String url = "https://github.com/TCreopargh/Text-Converter-Android";
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setToolbarColor(0x2196f3);
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(this, Uri.parse(url));
                    /* //Old invoke
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(
                            Uri.parse("https://github.com/TCreopargh/Text-Converter-Android"));
                    startActivity(intent);
                    */
                });
        viewMySite.setOnClickListener(
                v -> {
                    String url = "https://tcreopargh.xyz";
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    builder.setToolbarColor(0x2196f3);
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(this, Uri.parse(url));
                    /*
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://tcreopargh.xyz"));
                    startActivity(intent);
                    */
                });
        sendFeedBack.setOnClickListener(
                v -> {
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
                LovelyStandardDialog lovelyStandardDialog =
                        new LovelyStandardDialog(
                                AboutActivity.this, LovelyStandardDialog.ButtonLayout.HORIZONTAL);
                lovelyStandardDialog
                        .setTitle("为我评分")
                        .setTopColorRes(R.color.warningYellow)
                        .setIcon(R.drawable.ic_stars_black_24dp)
                        .setMessage("您的评分将是我继续开发的动力，如果您对此App感到满意，或是发现有不足之处，欢迎留下您的宝贵评论。")
                        .setButtonsColorRes(R.color.colorAccent)
                        .setPositiveButton(
                                "好的",
                                v -> {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse("https://www.coolapk.com/apk/212564"));
                                    startActivity(intent);
                                    lovelyStandardDialog.dismiss();
                                })
                        .setNegativeButton("下次吧", v -> lovelyStandardDialog.dismiss())
                        .create()
                        .show();
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
