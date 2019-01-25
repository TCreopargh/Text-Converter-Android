package xyz.tcreopargh.textconverter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.didikee.donate.AlipayDonate;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsIntent.Builder;
import com.franmontiel.attributionpresenter.AttributionPresenter;
import com.franmontiel.attributionpresenter.entities.Attribution;
import com.franmontiel.attributionpresenter.entities.Library;
import com.franmontiel.attributionpresenter.entities.License;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog.ButtonLayout;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toasty.Config.reset();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toasty.Config.getInstance().setInfoColor(getColor(R.color.colorPrimary)).apply();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        LinearLayout aboutLayout = findViewById(R.id.aboutLayout);
        final Toast[] toast = {
            Toasty.info(
                    AboutActivity.this,
                    getString(R.string.more_clicks)
                            + clicks
                            + getString(R.string.unlock_easter_egg),
                    Toast.LENGTH_SHORT)
        };
        Element versionElement =
                new Element()
                        .setTitle(getString(R.string.version_name) + getAppVersionName(this))
                        .setOnClickListener(
                                v -> {
                                    clicks--;
                                    if (clicks <= 97 && clicks > 1) {
                                        toast[0].cancel();
                                        toast[0] =
                                                Toasty.info(
                                                        AboutActivity.this,
                                                        getString(R.string.more_clicks)
                                                                + clicks
                                                                + getString(
                                                                        R.string
                                                                                .unlock_easter_egg));
                                        toast[0].show();
                                    } else if (clicks == 1) {
                                        Snacky.builder()
                                                .setActivity(AboutActivity.this)
                                                .setText(getString(R.string.hard_mode))
                                                .setDuration(Snacky.LENGTH_LONG)
                                                .setActionText(getString(R.string.click_me))
                                                .setActionClickListener(
                                                        v1 -> {
                                                            Intent intent = new Intent();
                                                            intent.putExtra("easter_egg", true);
                                                            Snacky.builder()
                                                                    .setActivity(AboutActivity.this)
                                                                    .setDuration(Snacky.LENGTH_LONG)
                                                                    .setText(
                                                                            getString(
                                                                                    R.string
                                                                                            .in_hard_mode))
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
                                                .setText(getString(R.string.missed_easter_egg))
                                                .centerText()
                                                .error()
                                                .show();
                                        clicks = 100;
                                    }
                                });

        Element viewMySiteElement =
                new Element()
                        .setTitle(getString(R.string.my_website))
                        .setOnClickListener(
                                v -> {
                                    String url = "https://tcreopargh.xyz";
                                    Builder builder = new Builder();
                                    builder.setToolbarColor(0x2196f3);
                                    CustomTabsIntent customTabsIntent = builder.build();
                                    customTabsIntent.launchUrl(this, Uri.parse(url));
                                })
                        .setIconDrawable(R.drawable.about_icon_link);
        Element viewRepo =
                new Element()
                        .setTitle(getString(R.string.github_address))
                        .setOnClickListener(
                                v -> {
                                    String url =
                                            "https://github.com/TCreopargh/Text-Converter-Android";
                                    Builder builder = new Builder();
                                    builder.setToolbarColor(0x2196f3);
                                    CustomTabsIntent customTabsIntent = builder.build();
                                    customTabsIntent.launchUrl(this, Uri.parse(url));
                                })
                        .setIconDrawable(R.drawable.about_icon_github);
        Element sendFeedback =
                new Element()
                        .setTitle(getString(R.string.send_feedback))
                        .setIconDrawable(R.drawable.about_icon_email)
                        .setOnClickListener(
                                v -> {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                                        intent.setData(Uri.parse("mailto:admin@tcreopargh.xyz"))
                                                .putExtra(
                                                        Intent.EXTRA_SUBJECT,
                                                        R.string.feedback_title)
                                                .putExtra(
                                                        Intent.EXTRA_TEXT,
                                                        R.string.feedback_message);
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        Toasty.error(
                                                        AboutActivity.this,
                                                        R.string.install_mail_first,
                                                        Toast.LENGTH_LONG)
                                                .show();
                                    }
                                });
        View aboutView =
                new AboutPage(this)
                        .isRTL(false)
                        .setDescription(getString(R.string.about_intro))
                        .setImage(R.drawable.banner_new)
                        .addGroup(getString(R.string.app_info))
                        .addItem(versionElement)
                        .addItem(
                                new Element()
                                        .setTitle(getString(R.string.developer_me))
                                        .setOnClickListener(
                                                v ->
                                                        Toasty.custom(
                                                                        AboutActivity.this,
                                                                        "(づ￣ 3￣)づ💗",
                                                                        R.drawable
                                                                                .ic_check_white_48dp,
                                                                        getColor(
                                                                                R.color
                                                                                        .colorAccent),
                                                                        Toast.LENGTH_SHORT,
                                                                        false,
                                                                        true)
                                                                .show()))
                        .addGroup(getString(R.string.relevant_links))
                        .addItem(viewRepo)
                        .addItem(viewMySiteElement)
                        .addItem(sendFeedback)
                        .addItem(
                                new Element()
                                        .setTitle(getString(R.string.rate_me))
                                        .setIconDrawable(R.drawable.ic_star_border_white_24dp)
                                        .setAutoApplyIconTint(true)
                                        .setOnClickListener(
                                                v -> {
                                                    LovelyStandardDialog lovelyStandardDialog =
                                                            new LovelyStandardDialog(
                                                                    AboutActivity.this,
                                                                    ButtonLayout.HORIZONTAL);
                                                    lovelyStandardDialog
                                                            .setTitle(getString(R.string.rate_me))
                                                            .setTopColorRes(R.color.warningYellow)
                                                            .setIcon(R.drawable.ic_stars_black_24dp)
                                                            .setMessage(
                                                                    getString(R.string.rate_intro))
                                                            .setButtonsColorRes(R.color.colorAccent)
                                                            .setPositiveButton(
                                                                    getString(R.string.okay),
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
                                                                    getString(R.string.maybe_later),
                                                                    v1 ->
                                                                            lovelyStandardDialog
                                                                                    .dismiss())
                                                            .create()
                                                            .show();
                                                }))
                        .addItem(
                                new Element()
                                        .setTitle(getString(R.string.donate_me))
                                        .setIconDrawable(R.drawable.ic_attach_money_black_24dp)
                                        .setAutoApplyIconTint(true)
                                        .setOnClickListener(
                                                v -> {
                                                    LovelyStandardDialog lovelyStandardDialog1 =
                                                            new LovelyStandardDialog(
                                                                    AboutActivity.this,
                                                                    ButtonLayout.HORIZONTAL);
                                                    lovelyStandardDialog1
                                                            .setTitle(getString(R.string.donate_me))
                                                            .setTopColorRes(R.color.mdMagenta)
                                                            .setIcon(
                                                                    R.drawable
                                                                            .ic_attach_money_white_48dp)
                                                            .setMessage(
                                                                    getString(
                                                                            R.string.donate_intro))
                                                            .setButtonsColorRes(R.color.colorAccent)
                                                            .setPositiveButton(
                                                                    getString(R.string.alipay),
                                                                    v1 -> {
                                                                        String payCode =
                                                                                "fkx04710ib3jtulb74vfx3b";
                                                                        boolean
                                                                                hasInstalledAlipayClient =
                                                                                        AlipayDonate
                                                                                                .hasInstalledAlipayClient(
                                                                                                        this);
                                                                        if (hasInstalledAlipayClient) {
                                                                            AlipayDonate
                                                                                    .startAlipayClient(
                                                                                            this,
                                                                                            payCode);
                                                                        } else {
                                                                            Toasty.error(
                                                                                            AboutActivity
                                                                                                    .this,
                                                                                            R.string
                                                                                                    .install_alipay_first,
                                                                                            Toast
                                                                                                    .LENGTH_LONG)
                                                                                    .show();
                                                                        }
                                                                        lovelyStandardDialog1
                                                                                .dismiss();
                                                                    })
                                                            .setNegativeButton(
                                                                    getString(R.string.wechat),
                                                                    v1 -> {
                                                                        String url =
                                                                                "https://donate.tcreopargh.xyz/";
                                                                        Builder builder =
                                                                                new Builder();
                                                                        builder.setToolbarColor(
                                                                                0x2196f3);
                                                                        CustomTabsIntent
                                                                                customTabsIntent =
                                                                                        builder
                                                                                                .build();
                                                                        customTabsIntent.launchUrl(
                                                                                this,
                                                                                Uri.parse(url));
                                                                        lovelyStandardDialog1
                                                                                .dismiss();
                                                                    })
                                                            .setNeutralButton(
                                                                    getString(R.string.maybe_later),
                                                                    v1 ->
                                                                            lovelyStandardDialog1
                                                                                    .dismiss())
                                                            .create()
                                                            .show();
                                                }))
                        .addItem(
                                new Element()
                                        .setTitle(getString(R.string.os_libs))
                                        .setIconDrawable(R.drawable.ic_account_balance_black_24dp)
                                        .setAutoApplyIconTint(true)
                                        .setOnClickListener(
                                                v -> {
                                                    AttributionPresenter attributionPresenter =
                                                            new AttributionPresenter.Builder(this)
                                                                    .addAttributions(Library.GSON)
                                                                    .addAttributions(
                                                                            new Attribution.Builder(
                                                                                            "Google Java Format")
                                                                                    .addCopyrightNotice(
                                                                                            "")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright 2015 Google Inc.")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .setWebsite(
                                                                                            "https://github.com/google/google-java-format")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "DialogPlus")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright 2016 Orhan Obut")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .setWebsite(
                                                                                            "https://github.com/orhanobut/dialogplus")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "Java-MorseCoder")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright 2017 TakWolf")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .setWebsite(
                                                                                            "https://github.com/TakWolf/Java-MorseCoder")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "Toasty")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .LGPL_3)
                                                                                    .setWebsite(
                                                                                            "https://github.com/GrenderG/Toasty")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "TapTargetView")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright 2016 Keepsafe Software Inc.")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .setWebsite(
                                                                                            "https://github.com/KeepSafe/TapTargetView")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "Snacky")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright 2018 Mate Siede")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .setWebsite(
                                                                                            "https://github.com/matecode/Snacky")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "LovelyDialog")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright 2016 Yaroslav Shevchuk")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .setWebsite(
                                                                                            "https://github.com/yarolegovich/LovelyDialog")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "pinyin4j")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .GPL_2)
                                                                                    .setWebsite(
                                                                                            "https://sourceforge.net/projects/pinyin4j/")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "Floating Text Button")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .setWebsite(
                                                                                            "https://github.com/dimorinny/floating-text-button")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "AESCrypt")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright (c) 2014 Scott Alexander-Bown")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .setWebsite(
                                                                                            "https://github.com/scottyab/AESCrypt-Android")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "java-aes-crypto")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .MIT)
                                                                                    .setWebsite(
                                                                                            "https://github.com/tozny/java-aes-crypto")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "Android About Page")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright (c) 2016 Mehdi Sakout")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .MIT)
                                                                                    .setWebsite(
                                                                                            "https://github.com/medyo/android-about-page")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "MarkdownView")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright 2017-2018 tiagohm")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .setWebsite(
                                                                                            "https://github.com/tiagohm/MarkdownView")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "AttributionPresenter")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright 2017 Francisco José Montiel Navarro")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .setWebsite(
                                                                                            "https://github.com/franmontiel/AttributionPresenter")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "LFilePicker")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright (C) 2017 leonHua")
                                                                                    .setWebsite(
                                                                                            "https://github.com/leonHua/LFilePicker")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "ion")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright 2013 Koushik Dutta (2013)")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .setWebsite(
                                                                                            "https://github.com/koush/ion")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "AndroidEdit")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .setWebsite(
                                                                                            "https://github.com/qinci/AndroidEdit")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "FastScroll-Everywhere")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright 2016 Mixiaoxiao")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .setWebsite(
                                                                                            "https://github.com/Mixiaoxiao/FastScroll-Everywhere")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "SwipeMenuRecyclerView")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright 2019 Zhenjie Yan")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .setWebsite(
                                                                                            "https://github.com/yanzhenjie/SwipeRecyclerView")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "java-string-similarity")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .MIT)
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright 2015 Thibault Debatty")
                                                                                    .setWebsite(
                                                                                            "https://github.com/tdebatty/java-string-similarity")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "cpdetector")
                                                                                    .setWebsite(
                                                                                            "http://cpdetector.sourceforge.net/donation.shtml")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .GPL_2)
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "Jtidy")
                                                                                    .setWebsite(
                                                                                            "http://jtidy.sourceforge.net/index.html")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright (c) 1998-2000 World Wide Web Consortium")
                                                                                    .addLicense(
                                                                                            "License",
                                                                                            "http://jtidy.sourceforge.net/license.html")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "AndroidDonate")
                                                                                    .setWebsite(
                                                                                            "https://github.com/didikee/AndroidDonate")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .MIT)
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "ViewTooltip")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .setWebsite(
                                                                                            "https://github.com/florent37/ViewTooltip")
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright 2017 Florent37, Inc.")
                                                                                    .build(),
                                                                            new Attribution.Builder(
                                                                                            "Apache Commons Text")
                                                                                    .setWebsite(
                                                                                            "https://commons.apache.org/proper/commons-text/")
                                                                                    .addLicense(
                                                                                            License
                                                                                                    .APACHE)
                                                                                    .addCopyrightNotice(
                                                                                            "Copyright © 2019 The Apache Software Foundation.")
                                                                                    .build())
                                                                    .build();
                                                    attributionPresenter.showDialog(
                                                            getString(R.string.os_libs));
                                                }))
                        .create();

        aboutLayout.addView(aboutView);
        setTitle(R.string.about);
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
