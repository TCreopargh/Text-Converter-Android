package xyz.tcreopargh.textconverter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import de.mateware.snacky.Snacky;
import es.dmoral.toasty.Toasty;
import java.util.Objects;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    int clicks = 100;

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toasty.Config.getInstance().setInfoColor(getColor(R.color.colorPrimary)).apply();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        LinearLayout aboutLayout = findViewById(R.id.aboutLayout);
        Element versionElement =
                new Element()
                        .setTitle("版本号：" + getAppVersionName(this))
                        .setOnClickListener(
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

        Element viewMySiteElement =
                new Element()
                        .setTitle("我的网站")
                        .setOnClickListener(
                                v -> {
                                    String url = "https://tcreopargh.xyz";
                                    CustomTabsIntent.Builder builder =
                                            new CustomTabsIntent.Builder();
                                    builder.setToolbarColor(0x2196f3);
                                    CustomTabsIntent customTabsIntent = builder.build();
                                    customTabsIntent.launchUrl(this, Uri.parse(url));
                                })
                        .setIconDrawable(R.drawable.about_icon_link);
        Element viewRepo =
                new Element()
                        .setTitle("GitHub项目地址")
                        .setOnClickListener(
                                v -> {
                                    String url =
                                            "https://github.com/TCreopargh/Text-Converter-Android";
                                    CustomTabsIntent.Builder builder =
                                            new CustomTabsIntent.Builder();
                                    builder.setToolbarColor(0x2196f3);
                                    CustomTabsIntent customTabsIntent = builder.build();
                                    customTabsIntent.launchUrl(this, Uri.parse(url));
                                })
                        .setIconDrawable(R.drawable.about_icon_github);
        Element sendFeedback =
                new Element()
                        .setTitle("发送反馈")
                        .setIconDrawable(R.drawable.about_icon_email)
                        .setOnClickListener(
                                v -> {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                                        intent.setData(Uri.parse("mailto:admin@tcreopargh.xyz"))
                                                .putExtra(Intent.EXTRA_SUBJECT, "《文本转换》用户反馈")
                                                .putExtra(
                                                        Intent.EXTRA_TEXT,
                                                        "请在此处附上您遇到问题的详细内容，最好能附上截图。感谢您的反馈。");
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        Toasty.error(
                                                        AboutActivity.this,
                                                        "请先安装邮件应用！",
                                                        Toast.LENGTH_LONG)
                                                .show();
                                    }
                                });
        View aboutView =
                new AboutPage(this)
                        .isRTL(false)
                        .setDescription("文本转换：好用轻便的文本高级转换工具")
                        .setImage(R.drawable.about_title)
                        .addGroup("应用信息")
                        .addItem(versionElement)
                        .addItem(
                                new Element()
                                        .setTitle("开发者：TCreopargh")
                                        .setOnClickListener(
                                                v -> Toasty.custom(
                                                                AboutActivity.this,
                                                                "(づ￣ 3￣)づ💗",
                                                                R.drawable.ic_check_white_48dp,
                                                                getColor(R.color.colorAccent),
                                                                Toast.LENGTH_SHORT,
                                                                false,
                                                                true)
                                                        .show()))
                        .addGroup("相关链接")
                        .addItem(viewRepo)
                        .addItem(viewMySiteElement)
                        .addItem(sendFeedback)
                        .addItem(
                                new Element()
                                        .setTitle("为我评分")
                                        .setIconDrawable(R.drawable.ic_star_border_white_24dp)
                                        .setAutoApplyIconTint(true)
                                        .setOnClickListener(
                                                v -> {
                                                    LovelyStandardDialog lovelyStandardDialog =
                                                            new LovelyStandardDialog(
                                                                    AboutActivity.this,
                                                                    LovelyStandardDialog
                                                                            .ButtonLayout
                                                                            .HORIZONTAL);
                                                    lovelyStandardDialog
                                                            .setTitle("为我评分")
                                                            .setTopColorRes(R.color.warningYellow)
                                                            .setIcon(R.drawable.ic_stars_black_24dp)
                                                            .setMessage(
                                                                    "您的评分将是我继续开发的动力，如果您对此App感到满意，或是发现有不足之处，欢迎留下您的宝贵评论。")
                                                            .setButtonsColorRes(R.color.colorAccent)
                                                            .setPositiveButton(
                                                                    "好的",
                                                                    v1 -> {
                                                                        Intent intent =
                                                                                new Intent(
                                                                                        Intent
                                                                                                .ACTION_VIEW);
                                                                        intent.setData(
                                                                                Uri.parse(
                                                                                        "https://www.coolapk.com/apk/212564"));
                                                                        startActivity(intent);
                                                                        lovelyStandardDialog
                                                                                .dismiss();
                                                                    })
                                                            .setNegativeButton(
                                                                    "下次吧",
                                                                    v1 ->
                                                                            lovelyStandardDialog
                                                                                    .dismiss())
                                                            .create()
                                                            .show();
                                                }))
                        .create();
        aboutLayout.addView(aboutView);
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
