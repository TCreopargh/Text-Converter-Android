//////////////////////////////////////////////////////////////////
// Powered by TCreopargh. All rights reserved.
// GitHub: https://github.com/TCreopargh/Text-Converter-Android
// Site: https://tcreopargh.xyz
//////////////////////////////////////////////////////////////////
// External libraries:
//////////////////////////////////////////////////////////////////
// com.google.googlejavaformat
// pinyin4j
// AESUtils
// LfilePicker
// com.takwolf:morse-coder
// Toasty
// LovelyDialog
// Snacky
// com.github.medyo:android-about-page
// com.google.code.gson
// AESCrypt-Android
// com.github.tiagohm.MarkdownView:library
// com.orhanobut:dialogplus
// com.koushikdutta.ion:ion
// com.github.franmontiel:AttributionPresenter
// AndroidEdit
// FastScroll-Everywhere
// etc (refer to in-app credit)
//////////////////////////////////////////////////////////////////

package xyz.tcreopargh.textconverter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.github.florent37.viewtooltip.ViewTooltip.Position;
import com.google.android.material.navigation.NavigationView;
import com.google.googlejavaformat.java.Formatter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.koushikdutta.ion.Ion;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.orhanobut.dialogplus.DialogPlus;
import com.scottyab.aescrypt.AESCrypt;
import com.takwolf.morsecoder.MorseCoder;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;
import de.mateware.snacky.Snacky;
import es.dmoral.toasty.Toasty;
import info.debatty.java.stringsimilarity.Damerau;
import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.LongestCommonSubsequence;
import info.debatty.java.stringsimilarity.MetricLCS;
import info.debatty.java.stringsimilarity.NGram;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import info.debatty.java.stringsimilarity.QGram;
import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener,
    View.OnClickListener,
    View.OnLongClickListener {

    public static final String defaultSalt = "Powered by TCreopargh!";
    public static boolean keyGenNeedToReset = true;
    public static String encoding = "UTF-8";

    @SuppressLint("StaticFieldLeak")
    public static MainActivity mainActivity = null;

    public static String returnText = null;
    final int ALL_LOWER = 0;
    final int ALL_UPPER = 1;
    final int CASE_REVERSE = 2;
    final int FIRST_UPPER = 3;
    final int SENTENCE_FIRST = 4;
    final int REQUESTCODE_READ = 1000;
    final int REQUESTCODE_WRITE = 2000;
    final boolean settingsBoolean[] = new boolean[]{false, false, false, true, true};
    final String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
    final String presetsValue[] =
        new String[]{
            "#?([a-f0-9]{6}|[a-f0-9]{3})",
            "[a-z\\d]+(\\.[a-z\\d]+)*@([\\da-z](-[\\da-z])?)+(\\.{1,2}[a-z]+)+",
            "(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?",
            "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)",
            "-?\\d+",
            "(-?\\d+)(\\.\\d+)?",
            "<(\"[^\"]*\"|'[^']*'|[^'\">])*>",
            "\\[\\d+\\]",
            "(?<!http:|\\S)//.*",
            "[\\u2E80-\\u9FFF]+"
        };
    LinearLayout textReplaceLayout,
        textShuffleLayout,
        textSearchLayout,
        textEncryptLayout,
        textMoreLayout;
    Button generateReplacedText;
    EditText replaceInput, replaceOutput, targetSeq, replaceTo;
    CheckBox doUseRegexCheckbox;
    Button shuffle, sortByDictionaryIndex, sortByNumberValue, shuffleReverse;
    EditText shuffleInput;
    EditText shuffleOutput;
    CheckBox noUseSpaces;
    ImageButton searchReset, searchPrevious, searchNext, searchAll;
    EditText searchInput, searchOutput, searchTarget;
    CheckBox doUseRegexSearchCheckbox;
    Button encrypt, decrypt;
    EditText encryptInput, encryptOutput, encryptKey;
    EditText moreInput, moreOutput;
    Button openMoreMenu;
    FloatingTextButton moreFab;
    int currentSearchPos = 0;
    int searchCount = -1;
    int currentSearchCount = -1;
    int begin = 0;
    Pattern pattern;
    Matcher matcher;
    String generatedKey = "";
    String path = "";
    String defaultPath = "";
    String salt = defaultSalt;
    DialogPlus dialogPlus;
    ImageView tick;
    CoordinatorLayout mainContext;
    int initialLayout = 0;
    boolean alreadyLoadedShortcut = false;
    boolean regexCautionIsShown = false;
    boolean isGuideShown = false;
    boolean regexIsDefault = true;
    ToolboxAdapter adapter;
    String tempString = "";
    boolean returnFromEditMode = false;
    boolean hasFind = false;
    private String capsSwitchModes[];
    private String similarityAlgorithms[];
    private String pinyinModes[];
    private long clickTime = 0L;
    private List<ListItems> itemsList = new ArrayList<>();
    private List<Range> searches = new ArrayList<>();

    public static int stringAppearCounter(String srcText, String findText) {
        int count = 0;
        int index = 0;
        while ((index = srcText.indexOf(findText, index)) != -1) {
            index = index + findText.length();
            count++;
        }
        return count;
    }

    public static int regexAppearCounter(String srcText, String findText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

    public static String getMD5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes("UTF8"));
            byte s[] = md.digest();
            StringBuilder result = new StringBuilder();
            for (byte value : s) {
                result.append(Integer.toHexString((0x000000FF & value) | 0xFFFFFF00).substring(6));
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String jsonFormatter(String uglyJSONString) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(uglyJSONString);
        return gson.toJson(je);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLanguage();
        String result;
        if (returnFromEditMode) {
            if (returnText != null) {
                result = returnText;
                returnText = null;
            } else {
                result = tempString;
            }

            if (result != null) {
                switch (getCurrentShowingLayoutId()) {
                    case R.id.textReplaceLayout:
                        replaceInput.setText(result, BufferType.EDITABLE);
                        break;

                    case R.id.textSearchLayout:
                        searchInput.setText(result, BufferType.EDITABLE);
                        break;

                    case R.id.textShuffleLayout:
                        shuffleInput.setText(result, BufferType.EDITABLE);
                        break;

                    case R.id.textEncryptLayout:
                        encryptInput.setText(result, BufferType.EDITABLE);
                        break;

                    case R.id.textMoreLayout:
                        moreInput.setText(result, BufferType.EDITABLE);
                        break;

                    default:
                }
            }
            returnFromEditMode = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshLanguage();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        setTitle(R.string.string_replace);
        mainActivity = this;

        // Floating button, may be used later
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show();
        }
        });
        */

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle =
            new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainContext = findViewById(R.id.appBarMain);

        loadSettings(true);

        adapter = new ToolboxAdapter(MainActivity.this, R.layout.list_layout, itemsList);
        initList();

        String[] presetsTitle =
            new String[]{
                getString(R.string.hex_dec_value),
                getString(R.string.e_mail),
                getString(R.string.url),
                getString(R.string.IP_address),
                getString(R.string.integer_num),
                getString(R.string.regular_num),
                getString(R.string.html_label),
                getString(R.string.wikipedia_notice),
                getString(R.string.code_comment),
                getString(R.string.chinese_character)
            };
        capsSwitchModes =
            new String[]{
                getString(R.string.to_lower_case),
                getString(R.string.to_upper_case),
                getString(R.string.switch_up_low),
                getString(R.string.word_first_upper),
                getString(R.string.sentence_first_upper)
            };
        similarityAlgorithms =
            new String[]{
                getString(R.string.lev_dist),
                getString(R.string.lev_sim),
                getString(R.string.dl_dist),
                getString(R.string.jw_dist),
                getString(R.string.jw_sim),
                getString(R.string.lcs),
                getString(R.string.lcs_measure),
                getString(R.string.n_dist),
                getString(R.string.q_dist)
            };
        pinyinModes =
            new String[]{
                getString(R.string.no_tone),
                getString(R.string.num_tone),
                getString(R.string.unicode_tone)
            };

        if (settingsBoolean[4]) {
            try {
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData data = cm.getPrimaryClip();
                ClipData.Item item = Objects.requireNonNull(data).getItemAt(0);
                String content = item.getText().toString();
                if (!content.isEmpty()) {
                    Snacky.builder()
                        .setView(mainContext)
                        .setDuration(Snacky.LENGTH_LONG)
                        .setText(getString(R.string.clipboard_not_empty))
                        .setActionText(getString(R.string.paste))
                        .setActionClickListener(
                            v -> {
                                if (content.length() < 100 * 1024) {
                                    replaceInput.setText(content);
                                    searchInput.setText(content);
                                    shuffleInput.setText(content);
                                    encryptInput.setText(content);
                                    moreInput.setText(content);
                                    Snacky.builder()
                                        .setView(mainContext)
                                        .setDuration(Snacky.LENGTH_SHORT)
                                        .setText(getString(R.string.copy_success))
                                        .setActionText(R.string.undo)
                                        .setActionClickListener(
                                            v12 -> {
                                                replaceInput.setText("");
                                                searchInput.setText("");
                                                shuffleInput.setText("");
                                                encryptInput.setText("");
                                                moreInput.setText("");
                                            })
                                        .success()
                                        .show();
                                } else {
                                    Snacky.builder()
                                        .setView(mainContext)
                                        .setDuration(Snacky.LENGTH_LONG)
                                        .setText(
                                            getString(
                                                R.string
                                                    .copy_failed_clipboard_too_long))
                                        .setActionText(R.string.confirm)
                                        .setActionClickListener(v1 -> {
                                        })
                                        .error()
                                        .show();
                                }
                            })
                        .info()
                        .show();
                }
            } catch (Exception ignored) {

            }
        }

        Intent intent1 = getIntent();
        if (Intent.ACTION_SEND.equals(intent1.getAction()) && intent1.getType() != null) {
            String content = "";
            if (intent1.getStringExtra(Intent.EXTRA_TEXT) != null) {
                content = intent1.getStringExtra(Intent.EXTRA_TEXT);
            } else if (intent1.getStringExtra(Intent.EXTRA_TITLE) != null) {
                String pathStr = intent1.getStringExtra(Intent.EXTRA_TITLE);
                if (pathStr.matches("^([/] [\\w-]+)*$")) {
                    content = readToString(pathStr);
                } else {
                    content = pathStr;
                }
            }
            if (content == null || content.length() < 100 * 1024) {
                if (content != null) {
                    replaceInput.setText(content);
                    searchInput.setText(content);
                    shuffleInput.setText(content);
                    encryptInput.setText(content);
                    moreInput.setText(content);

                    Snacky.builder()
                        .setView(mainContext)
                        .setDuration(Snacky.LENGTH_SHORT)
                        .setText(getString(R.string.receive_success))
                        .setActionText(R.string.undo)
                        .setActionClickListener(
                            v12 -> {
                                replaceInput.setText("");
                                searchInput.setText("");
                                shuffleInput.setText("");
                                encryptInput.setText("");
                                moreInput.setText("");
                            })
                        .success()
                        .show();
                } else {
                    Snacky.builder()
                        .setView(mainContext)
                        .setDuration(Snacky.LENGTH_LONG)
                        .setText(getString(R.string.err_loading_file))
                        .setActionText(R.string.confirm)
                        .setActionClickListener(v1 -> {
                        })
                        .error()
                        .show();
                }
            } else {
                Snacky.builder()
                    .setView(mainContext)
                    .setDuration(Snacky.LENGTH_LONG)
                    .setText(getString(R.string.share_too_long))
                    .setActionText(R.string.confirm)
                    .setActionClickListener(v1 -> {
                    })
                    .error()
                    .show();
            }
        }

        TapTargetSequence tapTargetSequence =
            new TapTargetSequence(MainActivity.this)
                .targets(
                    TapTarget.forToolbarNavigationIcon(
                        toolbar,
                        getString(R.string.open_menu_here),
                        getString(R.string.app_intro))
                        .outerCircleColor(R.color.colorPrimary)
                        .outerCircleAlpha(0.90f)
                        .targetCircleColor(R.color.colorAllWhite)
                        .titleTextColor(R.color.colorAllWhite)
                        .dimColor(R.color.colorAllBlack)
                        .drawShadow(true)
                        .cancelable(false)
                        .tintTarget(true)
                        .id(1),
                    TapTarget.forToolbarOverflow(
                        toolbar,
                        getString(R.string.more_func_here),
                        getString(R.string.menu_intro))
                        .outerCircleColor(R.color.colorAccent)
                        .outerCircleAlpha(0.90f)
                        .targetCircleColor(R.color.colorAllWhite)
                        .titleTextColor(R.color.colorAllWhite)
                        .dimColor(R.color.colorAllBlack)
                        .drawShadow(true)
                        .cancelable(false)
                        .tintTarget(true)
                        .id(2),
                    TapTarget.forView(
                        tick,
                        getString(R.string.start_to_use),
                        getString(R.string.tap_seq_end))
                        .outerCircleColor(R.color.safeGreen)
                        .outerCircleAlpha(0.90f)
                        .targetCircleColor(R.color.colorAllWhite)
                        .titleTextColor(R.color.colorAllWhite)
                        .dimColor(R.color.colorAllBlack)
                        .drawShadow(true)
                        .cancelable(false)
                        .tintTarget(true)
                        .id(3))
                .listener(
                    new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() {
                            tick.setVisibility(View.GONE);
                            SharedPreferences sharedPreferences1 =
                                getSharedPreferences("settings", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences1.edit();
                            isGuideShown = true;
                            editor.putBoolean("isGuideShown", true);
                            editor.apply();
                        }

                        @Override
                        public void onSequenceStep(
                            TapTarget lastTarget, boolean targetClicked) {
                            if (lastTarget.id() == 2) {
                                tick.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {
                        }
                    });

        if (!isGuideShown) {
            tapTargetSequence.start();
        }

        SharedPreferences sharedPreferences1 = getSharedPreferences("settings", MODE_PRIVATE);
        regexIsDefault = sharedPreferences1.getBoolean("isRegexDefault", true);

        if (regexIsDefault) {
            SharedPreferences.Editor editor = sharedPreferences1.edit();
            for (int i = 0; i < presetsTitle.length; i++) {
                editor.putString(CustomRegex.REGEX_KEY + i, presetsValue[i]);
                editor.putString(CustomRegex.LABEL_KEY + i, presetsTitle[i]);
            }
            editor.putInt(CustomRegex.SIZE_KEY, presetsTitle.length);
            editor.putBoolean("isRegexDefault", false);
            editor.apply();
        }

        try {
            navigationView.setCheckedItem(getCheckedDrawerItemId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        doUseRegexSearchCheckbox.setOnCheckedChangeListener(
            (buttonView, isChecked) -> {
                resetSearch();
                if (isChecked && !regexCautionIsShown) {
                    regexCautionIsShown = true;
                    LovelyStandardDialog lovelyStandardDialog =
                        new LovelyStandardDialog(
                            MainActivity.this,
                            LovelyStandardDialog.ButtonLayout.HORIZONTAL);
                    lovelyStandardDialog
                        .setIcon(R.drawable.ic_warning_white_48dp)
                        .setTitle(R.string.caution)
                        .setTopColorRes(R.color.warningYellow)
                        .setPositiveButtonColorRes(R.color.colorAccent)
                        .setNeutralButtonColorRes(R.color.colorAccent)
                        .setMessage(R.string.regex_info)
                        .setNeutralButton(
                            R.string.view_help,
                            v -> {
                                Intent intent =
                                    new Intent(
                                        MainActivity.this,
                                        ViewHelpActivity.class);
                                startActivity(intent);
                            })
                        .setPositiveButton(
                            R.string.i_know, v -> lovelyStandardDialog.dismiss())
                        .create()
                        .show();
                }
            });

        searchInput.addTextChangedListener(
            new TextWatcher() {
                @Override
                public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    resetSearch();
                }
            });

        searchTarget.addTextChangedListener(
            new TextWatcher() {
                @Override
                public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    resetSearch();
                }
            });

        encryptKey.addTextChangedListener(
            new TextWatcher() {
                @Override
                public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    keyGenNeedToReset = true;
                }
            });

        doUseRegexCheckbox.setOnCheckedChangeListener(
            (buttonView, isChecked) -> {
                if (isChecked) {
                    replaceTo.setHint(
                        getString(R.string.replace_to) + getString(R.string.val_hint));
                } else {
                    replaceTo.setHint(getString(R.string.replace_to));
                }
                if (isChecked && !regexCautionIsShown) {
                    regexCautionIsShown = true;
                    LovelyStandardDialog lovelyStandardDialog =
                        new LovelyStandardDialog(
                            MainActivity.this,
                            LovelyStandardDialog.ButtonLayout.HORIZONTAL);
                    lovelyStandardDialog
                        .setIcon(R.drawable.ic_warning_white_48dp)
                        .setTitle(R.string.caution)
                        .setTopColorRes(R.color.warningYellow)
                        .setPositiveButtonColorRes(R.color.colorAccent)
                        .setNeutralButtonColorRes(R.color.colorAccent)
                        .setMessage(
                            getString(R.string.regex_info)
                                + getString(R.string.val_dialog_hint))
                        .setNeutralButton(
                            R.string.view_help,
                            v -> {
                                Intent intent =
                                    new Intent(
                                        MainActivity.this,
                                        ViewHelpActivity.class);
                                startActivity(intent);
                            })
                        .setPositiveButton(
                            R.string.i_know, v -> lovelyStandardDialog.dismiss())
                        .create()
                        .show();
                }
            });

        moreFab.setOnClickListener(v -> showFunctionsMenu());
    }

    private void refreshLanguage() {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        String newValue =
            getSharedPreferences("settings", MODE_PRIVATE).getString("appLanguage", "auto");
        if (newValue != null) {
            switch (newValue) {
                case "auto":
                    config.setLocale(Locale.getDefault());
                    break;
                case "en-us":
                    config.setLocale(Locale.ENGLISH);
                    break;
                case "zh-cn":
                    config.setLocale(Locale.SIMPLIFIED_CHINESE);
                    break;
                case "zh-hk":
                    config.setLocale(Locale.TRADITIONAL_CHINESE);
                    break;
            }
        }
        resources.updateConfiguration(config, dm);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (dialogPlus == null || !dialogPlus.isShowing()) {
                exit();
            }
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
        int currentShowingLayout = getCurrentShowingLayoutId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_copy_to_clipboard) {
            ClipboardManager clipboardManager =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            String clip = "";
            switch (currentShowingLayout) {
                case R.id.textReplaceLayout:
                    clip = replaceOutput.getText().toString();
                    break;
                case R.id.textShuffleLayout:
                    clip = shuffleOutput.getText().toString();
                    break;
                case R.id.textSearchLayout:
                    clip = searchOutput.getText().toString();
                    break;
                case R.id.textEncryptLayout:
                    clip = encryptOutput.getText().toString();
                    break;
                case R.id.textMoreLayout:
                    clip = moreOutput.getText().toString();
                    break;
                default:
            }
            if (clip.length() > 500000) {
                Toasty.error(
                    MainActivity.this,
                    R.string.copy_fail_too_long,
                    Toast.LENGTH_LONG,
                    true)
                    .show();
            } else {
                ClipData mClipData = ClipData.newPlainText("TextConverter", clip);
                clipboardManager.setPrimaryClip(mClipData);
                Toasty.success(MainActivity.this, R.string.copy_success_1, Toast.LENGTH_SHORT, true)
                    .show();
            }
        } else if (id == R.id.action_reverse_io) {
            switch (currentShowingLayout) {
                case R.id.textReplaceLayout:
                    String buffer1 = replaceInput.getText().toString();
                    replaceInput.setText(
                        replaceOutput.getText().toString(), TextView.BufferType.EDITABLE);
                    Snacky.builder()
                        .setView(mainContext)
                        .setDuration(Snacky.LENGTH_SHORT)
                        .setText(getString(R.string.operation_success))
                        .setActionText(R.string.undo)
                        .setActionClickListener(v -> replaceInput.setText(buffer1))
                        .success()
                        .show();
                    break;

                case R.id.textShuffleLayout:
                    String buffer2 = shuffleInput.getText().toString();
                    shuffleInput.setText(
                        shuffleOutput.getText().toString(), TextView.BufferType.EDITABLE);
                    Snacky.builder()
                        .setView(mainContext)
                        .setDuration(Snacky.LENGTH_SHORT)
                        .setText(getString(R.string.operation_success))
                        .setActionText(R.string.undo)
                        .setActionClickListener(v -> shuffleInput.setText(buffer2))
                        .success()
                        .show();
                    break;

                case R.id.textSearchLayout:
                    String buffer3 = searchInput.getText().toString();
                    searchInput.setText(
                        searchOutput.getText().toString(), TextView.BufferType.EDITABLE);
                    Snacky.builder()
                        .setView(mainContext)
                        .setDuration(Snacky.LENGTH_SHORT)
                        .setText(getString(R.string.operation_success))
                        .setActionText(R.string.undo)
                        .setActionClickListener(v -> searchInput.setText(buffer3))
                        .success()
                        .show();
                    break;

                case R.id.textEncryptLayout:
                    String buffer4 = encryptInput.getText().toString();
                    encryptInput.setText(
                        encryptOutput.getText().toString(), TextView.BufferType.EDITABLE);
                    Snacky.builder()
                        .setView(mainContext)
                        .setDuration(Snacky.LENGTH_SHORT)
                        .setText(getString(R.string.operation_success))
                        .setActionText(R.string.undo)
                        .setActionClickListener(v -> encryptInput.setText(buffer4))
                        .success()
                        .show();
                    break;

                case R.id.textMoreLayout:
                    String buffer5 = moreInput.getText().toString();
                    moreInput.setText(
                        moreOutput.getText().toString(), TextView.BufferType.EDITABLE);
                    Snacky.builder()
                        .setView(mainContext)
                        .setDuration(Snacky.LENGTH_SHORT)
                        .setText(getString(R.string.operation_success))
                        .setActionText(R.string.undo)
                        .setActionClickListener(v -> moreInput.setText(buffer5))
                        .success()
                        .show();
                    break;
                default:
            }
        } else if (id == R.id.action_clear_all) {
            switch (currentShowingLayout) {
                case R.id.textReplaceLayout:
                    final String bufferReplaceInput = replaceInput.getText().toString();
                    final String bufferReplaceOutput = replaceOutput.getText().toString();
                    final String bufferReplaceTo = replaceTo.getText().toString();
                    final String bufferTargetSeq = targetSeq.getText().toString();
                    final boolean bufferDoUseRegex = doUseRegexCheckbox.isChecked();
                    replaceInput.setText("");
                    replaceOutput.setText("");
                    replaceTo.setText("");
                    targetSeq.setText("");
                    resetSearch();
                    doUseRegexCheckbox.setChecked(false);
                    Snacky.builder()
                        .setView(mainContext)
                        .setText(R.string.cleared)
                        .setActionText(R.string.undo)
                        .setDuration(Snacky.LENGTH_LONG)
                        .setActionClickListener(
                            v -> {
                                replaceInput.setText(
                                    bufferReplaceInput, TextView.BufferType.EDITABLE);
                                replaceOutput.setText(
                                    bufferReplaceOutput, TextView.BufferType.EDITABLE);
                                replaceTo.setText(
                                    bufferReplaceTo, TextView.BufferType.EDITABLE);
                                targetSeq.setText(
                                    bufferTargetSeq, TextView.BufferType.EDITABLE);
                                doUseRegexCheckbox.setChecked(bufferDoUseRegex);
                            })
                        .success()
                        .show();
                    break;

                case R.id.textShuffleLayout:
                    final String bufferShuffleInput = shuffleInput.getText().toString();
                    final String bufferShuffleOutput = shuffleOutput.getText().toString();
                    final boolean bufferNoUseSpaces = noUseSpaces.isChecked();
                    shuffleInput.setText("");
                    shuffleOutput.setText("");
                    noUseSpaces.setChecked(false);
                    Snacky.builder()
                        .setView(mainContext)
                        .setText(R.string.cleared)
                        .setActionText(R.string.undo)
                        .setDuration(Snacky.LENGTH_LONG)
                        .setActionClickListener(
                            v -> {
                                shuffleInput.setText(
                                    bufferShuffleInput, TextView.BufferType.EDITABLE);
                                shuffleOutput.setText(
                                    bufferShuffleOutput, TextView.BufferType.EDITABLE);
                                noUseSpaces.setChecked(bufferNoUseSpaces);
                            })
                        .success()
                        .show();
                    break;

                case R.id.textSearchLayout:
                    final String bufferSearchInput = searchInput.getText().toString();
                    final String bufferSearchOutput = searchOutput.getText().toString();
                    final String bufferSearchTarget = searchTarget.getText().toString();
                    final boolean bufferDoUseCheckboxInSearch =
                        doUseRegexSearchCheckbox.isChecked();
                    searchInput.setText("");
                    searchOutput.setText("");
                    searchTarget.setText("");
                    doUseRegexSearchCheckbox.setChecked(false);
                    resetSearch();
                    Snacky.builder()
                        .setView(mainContext)
                        .setText(R.string.cleared)
                        .setActionText(R.string.undo)
                        .setDuration(Snacky.LENGTH_LONG)
                        .setActionClickListener(
                            v -> {
                                searchInput.setText(
                                    bufferSearchInput, TextView.BufferType.EDITABLE);
                                searchOutput.setText(
                                    bufferSearchOutput, TextView.BufferType.EDITABLE);
                                searchTarget.setText(
                                    bufferSearchTarget, TextView.BufferType.EDITABLE);
                                doUseRegexSearchCheckbox.setChecked(
                                    bufferDoUseCheckboxInSearch);
                            })
                        .success()
                        .show();
                    break;

                case R.id.textEncryptLayout:
                    final String bufferEncryptInput = encryptInput.getText().toString();
                    final String bufferEncryptOutput = encryptOutput.getText().toString();
                    final String bufferEncryptKey = encryptKey.getText().toString();
                    encryptInput.setText("");
                    encryptOutput.setText("");
                    encryptKey.setText("");
                    Snacky.builder()
                        .setView(mainContext)
                        .setText(R.string.cleared)
                        .setActionText(R.string.undo)
                        .setDuration(Snacky.LENGTH_LONG)
                        .setActionClickListener(
                            v -> {
                                encryptInput.setText(
                                    bufferEncryptInput, TextView.BufferType.EDITABLE);
                                encryptOutput.setText(
                                    bufferEncryptOutput, TextView.BufferType.EDITABLE);
                                encryptKey.setText(
                                    bufferEncryptKey, TextView.BufferType.EDITABLE);
                            })
                        .success()
                        .show();
                    break;

                case R.id.textMoreLayout:
                    final String bufferMoreInput = moreInput.getText().toString();
                    final String bufferMoreOutput = moreOutput.getText().toString();
                    moreOutput.setText("");
                    moreInput.setText("");
                    Snacky.builder()
                        .setView(mainContext)
                        .setText(R.string.cleared)
                        .setActionText(R.string.undo)
                        .setDuration(Snacky.LENGTH_LONG)
                        .setActionClickListener(
                            v -> {
                                moreInput.setText(
                                    bufferMoreInput, TextView.BufferType.EDITABLE);
                                moreOutput.setText(
                                    bufferMoreOutput, TextView.BufferType.EDITABLE);
                            })
                        .success()
                        .show();
                    break;
                default:
            }
        } else if (id == R.id.load_presets) {
            Intent intent = new Intent(MainActivity.this, CustomRegexActivity.class);
            intent.putExtra("selectionMode", true);
            startActivityForResult(intent, 6);
        } else if (id == R.id.action_read_file) {
            if (ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1);
            } else {
                getFile();
            }
        } else if (id == R.id.action_write_file) {
            String outputString = "";
            boolean doUseFilePicker = true;
            int totLen = 0;
            totLen += replaceInput.getText().toString().length();
            totLen += replaceOutput.getText().toString().length();
            totLen += replaceTo.getText().toString().length();
            totLen += targetSeq.getText().toString().length();
            totLen += searchInput.getText().toString().length();
            totLen += searchOutput.getText().toString().length();
            totLen += searchTarget.getText().toString().length();
            totLen += shuffleInput.getText().toString().length();
            totLen += shuffleOutput.getText().toString().length();
            totLen += encryptInput.getText().toString().length();
            totLen += encryptOutput.getText().toString().length();
            totLen += encryptKey.getText().toString().length();
            totLen += moreInput.getText().toString().length();
            totLen += moreOutput.getText().toString().length();
            if (totLen >= 250 * 1024) {
                doUseFilePicker = false;
            }
            switch (currentShowingLayout) {
                case R.id.textReplaceLayout:
                    outputString = replaceOutput.getText().toString();
                    break;

                case R.id.textSearchLayout:
                    outputString = searchOutput.getText().toString();
                    break;

                case R.id.textShuffleLayout:
                    outputString = shuffleOutput.getText().toString();
                    break;

                case R.id.textEncryptLayout:
                    outputString = encryptOutput.getText().toString();
                    break;

                case R.id.textMoreLayout:
                    outputString = moreOutput.getText().toString();
                    break;

                default:
            }
            if (outputString.length() < 250 * 1024 && doUseFilePicker) {
                if (ContextCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        2);
                } else {
                    getStoreLocation();
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        3);
                } else {
                    storeDirectly();
                }
            }
        } else if (id == R.id.action_char_count) {
            String inStr = "";
            String outStr = "";
            switch (currentShowingLayout) {
                case R.id.textReplaceLayout:
                    inStr = replaceInput.getText().toString();
                    outStr = replaceOutput.getText().toString();
                    break;

                case R.id.textSearchLayout:
                    inStr = searchInput.getText().toString();
                    outStr = searchOutput.getText().toString();
                    break;

                case R.id.textShuffleLayout:
                    inStr = shuffleInput.getText().toString();
                    outStr = shuffleOutput.getText().toString();
                    break;

                case R.id.textEncryptLayout:
                    inStr = encryptInput.getText().toString();
                    outStr = encryptOutput.getText().toString();
                    break;

                case R.id.textMoreLayout:
                    inStr = moreInput.getText().toString();
                    outStr = moreOutput.getText().toString();
                    break;

                default:
            }
            int inLen = inStr.length();
            int outLen = outStr.length();
            Toasty.info(
                MainActivity.this,
                getString(R.string.input_area_count)
                    + inLen
                    + getString(R.string.output_area_count)
                    + outLen,
                Toast.LENGTH_LONG)
                .show();
        } else if (id == R.id.get_string_from_url) {
            LovelyTextInputDialog inputDialog =
                new LovelyTextInputDialog(this)
                    .setTitle(getString(R.string.retrieve_source_code_from_web))
                    .setMessage(getString(R.string.enter_url))
                    .setTopColorRes(R.color.colorAccent)
                    .setCancelable(true)
                    .setIcon(R.drawable.ic_public_white_24dp)
                    .configureEditText(
                        v -> {
                            v.setTextColor(getColor(R.color.colorAllBlack));
                            v.setHint(getString(R.string.example_google));
                            if (getSharedPreferences("settings", MODE_PRIVATE)
                                .getBoolean("doUseMonospaced", false)) {
                                v.setTextAppearance(R.style.MyMonospace);
                            } else {
                                v.setTextAppearance(R.style.MyRegular);
                            }
                            v.clearFocus();
                        })
                    .setConfirmButton(
                        getString(R.string.confirm),
                        text -> {
                            text = text.trim();
                            if (text.matches(
                                "https?://([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?")) {
                                getSourceCodeFromUrl(text);
                            } else {
                                text = "http://" + text;
                                if (text.matches(
                                    "https?://([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?")) {
                                    getSourceCodeFromUrl(text);
                                } else {
                                    Toasty.error(
                                        MainActivity.this,
                                        R.string.illegal_url,
                                        Toast.LENGTH_LONG)
                                        .show();
                                }
                            }
                        });
            inputDialog.create().show();
        } else if (id == R.id.edit_mode) {
            String sendText = "";
            switch (getCurrentShowingLayoutId()) {
                case R.id.textReplaceLayout:
                    sendText = replaceInput.getText().toString();
                    replaceInput.setText("");
                    break;

                case R.id.textSearchLayout:
                    sendText = searchInput.getText().toString();
                    searchInput.setText("");
                    break;

                case R.id.textShuffleLayout:
                    sendText = shuffleInput.getText().toString();
                    shuffleInput.setText("");
                    break;

                case R.id.textEncryptLayout:
                    sendText = encryptInput.getText().toString();
                    encryptInput.setText("");
                    break;

                case R.id.textMoreLayout:
                    sendText = moreInput.getText().toString();
                    moreInput.setText("");
                    break;
            }
            returnFromEditMode = true;
            tempString = sendText;
            TextEditActivity.text = sendText;
            TextEditActivity.currentCharset = encoding;
            Intent intent = new Intent(MainActivity.this, TextEditActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getSourceCodeFromUrl(String text) {
        final ProgressDialog progressDialog =
            ProgressDialog.show(
                MainActivity.this,
                getString(R.string.wait_plz),
                getString(R.string.connecting_hint));
        Ion.with(getApplicationContext())
            .load(text)
            .asString()
            .setCallback(
                (e, result) -> {
                    if (e == null && !result.isEmpty()) {
                        switch (getCurrentShowingLayoutId()) {
                            case R.id.textReplaceLayout:
                                replaceInput.setText(result, BufferType.EDITABLE);
                                break;

                            case R.id.textSearchLayout:
                                searchInput.setText(result, BufferType.EDITABLE);
                                break;

                            case R.id.textShuffleLayout:
                                shuffleInput.setText(result, BufferType.EDITABLE);
                                break;

                            case R.id.textEncryptLayout:
                                encryptInput.setText(result, BufferType.EDITABLE);
                                break;

                            case R.id.textMoreLayout:
                                moreInput.setText(result, BufferType.EDITABLE);
                                break;

                            default:
                        }
                        Toasty.success(
                            MainActivity.this,
                            R.string.retrieve_success,
                            Toast.LENGTH_LONG)
                            .show();
                    } else {
                        if (e == null) {
                            Toasty.error(
                                MainActivity.this,
                                R.string.connection_timeout,
                                Toast.LENGTH_LONG)
                                .show();
                        } else {
                            String errMsg = e.toString();
                            Toasty.error(
                                MainActivity.this,
                                getString(R.string.exception_occurred) + errMsg,
                                Toast.LENGTH_LONG)
                                .show();
                        }
                    }
                    progressDialog.dismiss();
                });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_text_replace) {
            textReplaceLayout.setVisibility(View.VISIBLE);
            textShuffleLayout.setVisibility(View.GONE);
            textSearchLayout.setVisibility(View.GONE);
            textEncryptLayout.setVisibility(View.GONE);
            textMoreLayout.setVisibility(View.GONE);
            moreFab.setVisibility(View.INVISIBLE);
            setTitle(R.string.string_replace);
            // Handle the camera action
        } else if (id == R.id.nav_text_shuffle) {
            textShuffleLayout.setVisibility(View.VISIBLE);
            textReplaceLayout.setVisibility(View.GONE);
            textSearchLayout.setVisibility(View.GONE);
            textEncryptLayout.setVisibility(View.GONE);
            textMoreLayout.setVisibility(View.GONE);
            moreFab.setVisibility(View.INVISIBLE);
            setTitle(R.string.string_shuffle_sort);
        } else if (id == R.id.nav_text_search) {
            textSearchLayout.setVisibility(View.VISIBLE);
            textShuffleLayout.setVisibility(View.GONE);
            textReplaceLayout.setVisibility(View.GONE);
            textEncryptLayout.setVisibility(View.GONE);
            textMoreLayout.setVisibility(View.GONE);
            moreFab.setVisibility(View.INVISIBLE);
            setTitle(R.string.text_search);
        } else if (id == R.id.nav_text_encrypt) {
            textEncryptLayout.setVisibility(View.VISIBLE);
            textSearchLayout.setVisibility(View.GONE);
            textShuffleLayout.setVisibility(View.GONE);
            textReplaceLayout.setVisibility(View.GONE);
            textMoreLayout.setVisibility(View.GONE);
            moreFab.setVisibility(View.INVISIBLE);
            setTitle(R.string.text_encrypt);
        } else if (id == R.id.nav_more_functions) {
            textMoreLayout.setVisibility(View.VISIBLE);
            textEncryptLayout.setVisibility(View.GONE);
            textSearchLayout.setVisibility(View.GONE);
            textShuffleLayout.setVisibility(View.GONE);
            textReplaceLayout.setVisibility(View.GONE);
            moreFab.setVisibility(View.VISIBLE);
            setTitle(R.string.more_handy_function);
        } else if (id == R.id.nav_share) {
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain")
                    .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.action_share))
                    .putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content))
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, getString(R.string.action_share)));
            } catch (Exception e) {
                Toasty.error(MainActivity.this, R.string.no_responsible_apps, Toast.LENGTH_LONG)
                    .show();
            }
        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(MainActivity.this, ViewHelpActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(intent, 5);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivityForResult(intent, 4);
        } else if (id == R.id.nav_manage_regex) {
            Intent intent = new Intent(MainActivity.this, CustomRegexActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initView() {
        textReplaceLayout = findViewById(R.id.textReplaceLayout);
        generateReplacedText = findViewById(R.id.generateReplacedText);
        replaceInput = findViewById(R.id.replaceInput);
        replaceOutput = findViewById(R.id.replaceOutput);
        replaceTo = findViewById(R.id.replaceTo);
        targetSeq = findViewById(R.id.targetSeq);
        doUseRegexCheckbox = findViewById(R.id.doUseRegex);

        textShuffleLayout = findViewById(R.id.textShuffleLayout);
        shuffle = findViewById(R.id.generateShuffleRand);
        shuffleInput = findViewById(R.id.shuffleInput);
        shuffleOutput = findViewById(R.id.shuffledText);
        noUseSpaces = findViewById(R.id.noUseSpaces);
        sortByDictionaryIndex = findViewById(R.id.sortByDictionaryIndex);
        sortByNumberValue = findViewById(R.id.sortByNumberValue);
        shuffleReverse = findViewById(R.id.shuffleReverse);

        textSearchLayout = findViewById(R.id.textSearchLayout);
        searchReset = findViewById(R.id.searchReset);
        searchInput = findViewById(R.id.searchInput);
        searchOutput = findViewById(R.id.searchOutput);
        searchPrevious = findViewById(R.id.searchPrevious);
        searchNext = findViewById(R.id.searchNext);
        searchTarget = findViewById(R.id.searchTargetText);
        doUseRegexSearchCheckbox = findViewById(R.id.doUseRegexInSearch);
        searchAll = findViewById(R.id.searchAll);

        textEncryptLayout = findViewById(R.id.textEncryptLayout);
        encryptOutput = findViewById(R.id.encryptOutput);
        encryptInput = findViewById(R.id.encryptInput);
        encryptKey = findViewById(R.id.encryptKey);
        encrypt = findViewById(R.id.encrypt);
        decrypt = findViewById(R.id.decrypt);

        textMoreLayout = findViewById(R.id.textMoreLayout);
        moreInput = findViewById(R.id.moreInput);
        moreOutput = findViewById(R.id.moreOutput);
        openMoreMenu = findViewById(R.id.openMoreMenu);

        tick = findViewById(R.id.tick);

        moreFab = findViewById(R.id.showMore);

        generateReplacedText.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        shuffleReverse.setOnClickListener(this);
        sortByNumberValue.setOnClickListener(this);
        sortByDictionaryIndex.setOnClickListener(this);
        searchPrevious.setOnClickListener(this);
        searchNext.setOnClickListener(this);
        searchReset.setOnClickListener(this);
        searchAll.setOnClickListener(this);
        encrypt.setOnClickListener(this);
        decrypt.setOnClickListener(this);
        openMoreMenu.setOnClickListener(this);

        searchReset.setOnLongClickListener(this);
        searchAll.setOnLongClickListener(this);
        searchPrevious.setOnLongClickListener(this);
        searchNext.setOnLongClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.generateReplacedText:
                try {
                    boolean bool = false;
                    String textReplaceInput = replaceInput.getText().toString();
                    boolean doUseRegex = doUseRegexCheckbox.isChecked();
                    String textReplaceOutput;
                    String replaceFromStr = targetSeq.getText().toString();
                    String replaceToStr = replaceTo.getText().toString();
                    if (doUseRegex) {
                        StringBuilder input = new StringBuilder(textReplaceInput);
                        Pattern p = Pattern.compile(replaceFromStr);
                        Matcher m = p.matcher(input);
                        replaceToStr =
                            replaceToStr.replace("$$", "\u2333\u2888\u7575\u3139\u6666\u4232");
                        ArrayList<String> strings = new ArrayList<>();
                        while (m.find()) {
                            bool = !bool;
                            strings.add(m.group());
                            char c = bool ? '\ufffe' : '\uffff';
                            StringBuilder temp = new StringBuilder();
                            for (int i = 0; i < m.group().length(); i++) {
                                temp.append(c);
                            }
                            input.replace(m.start(), m.end(), temp.toString());
                        }
                        String str = input.toString();
                        for (int i = 0; i < strings.size(); i++) {
                            String string = strings.get(i);
                            str =
                                str.replaceFirst(
                                    "\\uFFFF+|\\uFFFE+",
                                    replaceToStr.replace("$val", string)
                                        .replace("$num", String.valueOf(i + 1))
                                        .replace("$rev", new StringBuffer(string).reverse())
                                        .replace("$len", String.valueOf(string.length()))
                                        .replace("$low", string.toLowerCase())
                                        .replace("$upp", string.toUpperCase()));
                        }
                        textReplaceOutput =
                            str.replace("\u2333\u2888\u7575\u3139\u6666\u4232", "$");
                    } else {
                        if (settingsBoolean[0]) {
                            textReplaceOutput =
                                textReplaceInput.replace(replaceFromStr, replaceToStr);
                        } else {
                            for (String key : fbsArr) {
                                if (replaceFromStr.contains(key)) {
                                    replaceFromStr = replaceFromStr.replace(key, "\\" + key);
                                }
                            }
                            textReplaceOutput =
                                textReplaceInput.replaceAll(
                                    "(?i)" + replaceFromStr, replaceToStr);
                        }
                    }
                    replaceOutput.setText(textReplaceOutput, BufferType.EDITABLE);
                    replaceInput.clearFocus();
                    replaceOutput.clearFocus();
                    replaceTo.clearFocus();
                    targetSeq.clearFocus();
                } catch (Exception e) {
                    replaceOutput.setText(e.toString(), BufferType.EDITABLE);
                    replaceInput.clearFocus();
                    replaceOutput.clearFocus();
                    replaceTo.clearFocus();
                    targetSeq.clearFocus();
                }
                break;

            case R.id.generateShuffleRand:
                try {
                    char spiltWithChar;
                    if (noUseSpaces.isChecked()) {
                        spiltWithChar = '\n';
                    } else {
                        spiltWithChar = ' ';
                    }
                    String inputStr = shuffleInput.getText().toString();
                    StringBuilder outputBuilder = new StringBuilder();
                    int elementCount = 0;
                    for (int i = 0; i < inputStr.length(); i++) {
                        if (inputStr.charAt(i) == ' ' || inputStr.charAt(i) == '\n') {
                            elementCount++;
                        }
                    }
                    elementCount++;
                    String elements[] = new String[elementCount];
                    for (int i = 0; i < elementCount; i++) {
                        elements = inputStr.split("[\n ]", elementCount);
                    }
                    Random random = new Random();
                    for (int i = 0; i < elementCount; i++) {

                        int targetPos = random.nextInt(elementCount - i) + i;
                        String tempStr = elements[i];
                        elements[i] = elements[targetPos];
                        elements[targetPos] = tempStr;
                    }
                    for (int i = 0; i < elementCount; i++) {
                        if (!elements[i].isEmpty()) {
                            if (i < elementCount - 1) {
                                outputBuilder.append(elements[i]).append(spiltWithChar);
                            } else {
                                outputBuilder.append(elements[i]);
                            }
                        }
                    }
                    shuffleOutput.setText(outputBuilder.toString(), BufferType.EDITABLE);
                    shuffleInput.clearFocus();
                    shuffleOutput.clearFocus();
                } catch (Exception e) {
                    shuffleOutput.setText(getString(R.string.exception_occurred) + e.toString());
                    shuffleInput.clearFocus();
                    shuffleOutput.clearFocus();
                }
                break;

            case R.id.shuffleReverse:
                try {
                    char spiltWithChar;
                    if (noUseSpaces.isChecked()) {
                        spiltWithChar = '\n';
                    } else {
                        spiltWithChar = ' ';
                    }
                    String inputStr = shuffleInput.getText().toString();
                    StringBuilder outputBuilder = new StringBuilder();
                    int elementCount = 0;
                    for (int i = 0; i < inputStr.length(); i++) {
                        if (inputStr.charAt(i) == ' ' || inputStr.charAt(i) == '\n') {
                            elementCount++;
                        }
                    }
                    elementCount++;
                    String elements[] = new String[elementCount];
                    for (int i = 0; i < elementCount; i++) {
                        elements = inputStr.split("[\n ]", elementCount);
                    }
                    for (int i = elements.length - 1; i >= 0; i--) {
                        outputBuilder.append(elements[i]).append(spiltWithChar);
                    }
                    shuffleOutput.setText(outputBuilder.toString());
                    shuffleInput.clearFocus();
                    shuffleOutput.clearFocus();
                } catch (Exception e) {
                    shuffleOutput.setText(getString(R.string.exception_occurred) + e.toString());
                    shuffleInput.clearFocus();
                    shuffleOutput.clearFocus();
                }
                break;

            case R.id.sortByNumberValue:
                try {
                    char spiltWithChar;
                    if (noUseSpaces.isChecked()) {
                        spiltWithChar = '\n';
                    } else {
                        spiltWithChar = ' ';
                    }
                    String inputStr = shuffleInput.getText().toString();
                    StringBuilder outputBuilder = new StringBuilder();
                    int elementCount = 0;
                    for (int i = 0; i < inputStr.length(); i++) {
                        if (inputStr.charAt(i) == ' ' || inputStr.charAt(i) == '\n') {
                            elementCount++;
                        }
                    }
                    elementCount++;
                    String elements[] = new String[elementCount];
                    for (int i = 0; i < elementCount; i++) {
                        elements = inputStr.split("[\n ]", elementCount);
                    }
                    double elementNumbers[] = new double[elementCount];
                    int newElementCount = elementCount;
                    for (int i = 0; i < elementCount; i++) {
                        if (!elements[i].isEmpty()) {
                            elementNumbers[i] = Double.parseDouble(elements[i]);
                        } else {
                            elementNumbers[i] = Double.MAX_VALUE;
                            newElementCount--;
                        }
                    }
                    Arrays.sort(elementNumbers);
                    DecimalFormat decimalFormat =
                        new DecimalFormat("###################.###########");
                    for (int i = 0; i < newElementCount; i++) {
                        if (i < newElementCount - 1) {
                            outputBuilder
                                .append(decimalFormat.format(elementNumbers[i]))
                                .append(spiltWithChar);
                        } else {
                            outputBuilder.append(decimalFormat.format(elementNumbers[i]));
                        }
                    }
                    shuffleOutput.setText(outputBuilder.toString());
                    shuffleInput.clearFocus();
                    shuffleOutput.clearFocus();
                } catch (Exception e) {
                    if (e instanceof NumberFormatException) {
                        shuffleOutput.setText(getString(R.string.digit_err) + e.toString());
                    } else {
                        shuffleOutput.setText(
                            getString(R.string.exception_occurred) + e.toString());
                    }
                    shuffleInput.clearFocus();
                    shuffleOutput.clearFocus();
                }
                break;

            case R.id.sortByDictionaryIndex:
                try {
                    char spiltWithChar;
                    if (noUseSpaces.isChecked()) {
                        spiltWithChar = '\n';
                    } else {
                        spiltWithChar = ' ';
                    }
                    String inputStr = shuffleInput.getText().toString();
                    StringBuilder outputBuilder = new StringBuilder();
                    int elementCount = 0;
                    for (int i = 0; i < inputStr.length(); i++) {
                        if (inputStr.charAt(i) == ' ' || inputStr.charAt(i) == '\n') {
                            elementCount++;
                        }
                    }
                    elementCount++;
                    String elements[] = new String[elementCount];
                    for (int i = 0; i < elementCount; i++) {
                        elements = inputStr.split("[\n ]", elementCount);
                    }
                    Arrays.sort(elements, new PinyinComparator());
                    for (int i = 0; i < elementCount; i++) {
                        if (!elements[i].isEmpty()) {
                            if (i < elementCount - 1) {
                                outputBuilder.append(elements[i]).append(spiltWithChar);
                            } else {
                                outputBuilder.append(elements[i]);
                            }
                        }
                    }
                    shuffleOutput.setText(outputBuilder.toString());
                    shuffleInput.clearFocus();
                    shuffleOutput.clearFocus();
                } catch (Exception e) {
                    shuffleOutput.setText(getString(R.string.exception_occurred) + e.toString());
                    shuffleInput.clearFocus();
                    shuffleOutput.clearFocus();
                }
                break;

            case R.id.searchPrevious:
                try {
                    if (!hasFind) {
                        findAll();
                    }

                    if (currentSearchCount < 1) {
                        currentSearchCount = 1;
                    }
                    if (currentSearchCount == 1) {
                        currentSearchCount = searchCount - currentSearchCount + 2;
                    }
                    Range range = searches.get(currentSearchCount - 2);
                    searchInput.requestFocus();
                    searchInput.setSelection(range.start(), range.end());
                    currentSearchCount--;
                    currentSearchPos = range.end();
                    if (doUseRegexSearchCheckbox.isChecked()) {
                        if (!matcher.find(range.start())) {
                            matcher.reset();
                        }
                        searchOutput.setText(
                            getString(R.string.tot_appear)
                                + searchCount
                                + getString(R.string.tot_appear_1)
                                + currentSearchCount
                                + getString(R.string.matches)
                                + searchInput
                                .getText()
                                .toString()
                                .substring(range.start(), range.end()));
                    } else {
                        searchOutput.setText(
                            getString(R.string.tot_appear)
                                + searchCount
                                + getString(R.string.tot_appear_1)
                                + currentSearchCount
                                + getString(R.string.chu));
                    }

                } catch (Exception e) {
                    if (e instanceof ArrayIndexOutOfBoundsException) {
                        searchOutput.setText(
                            getString(R.string.target_not_found), BufferType.EDITABLE);
                    } else {
                        searchOutput.setText(getString(R.string.exception_occurred) + e.toString());
                    }
                }
                break;

            case R.id.searchNext:
                try {
                    String findTarget = searchTarget.getText().toString();
                    String findSrc = searchInput.getText().toString();
                    if (findTarget.isEmpty() || findSrc.isEmpty()) {
                        Toasty.error(
                            MainActivity.this,
                            R.string.search_is_empty,
                            Toast.LENGTH_LONG,
                            true)
                            .show();
                        return;
                    }
                    boolean doUseRegexInSearch = doUseRegexSearchCheckbox.isChecked();
                    if (!doUseRegexInSearch) {
                        if (!settingsBoolean[0]) {
                            findTarget = findTarget.toLowerCase();
                            findSrc = findSrc.toLowerCase();
                        }
                        if (searchCount == -1) {
                            searchCount = stringAppearCounter(findSrc, findTarget);
                            currentSearchCount = 0;
                            currentSearchPos = 0;
                        }
                        if (searchCount == 0) {
                            searchOutput.setText(
                                getString(R.string.target_not_found), BufferType.EDITABLE);
                        } else {
                            currentSearchPos = findSrc.indexOf(findTarget, currentSearchPos);
                            if (currentSearchPos == -1) {
                                currentSearchCount = 0;
                                resetSearch();
                                onClick(findViewById(R.id.searchNext));
                            } else {
                                currentSearchCount++;
                                searchInput.requestFocus();
                                searchInput.setSelection(
                                    currentSearchPos, currentSearchPos + findTarget.length());
                                searchOutput.setText(
                                    getString(R.string.tot_appear)
                                        + searchCount
                                        + getString(R.string.tot_appear_1)
                                        + currentSearchCount
                                        + getString(R.string.chu));
                                currentSearchPos += findTarget.length();
                            }
                        }
                    } else {
                        if (searchCount == -1) {
                            searchCount = regexAppearCounter(findSrc, findTarget);
                            currentSearchCount = 0;
                            currentSearchPos = 0;
                        }
                        if (searchCount == 0) {
                            searchOutput.setText(
                                getString(R.string.target_not_found), BufferType.EDITABLE);
                        } else {
                            currentSearchCount++;
                            if (matcher.find()) {
                                String group = matcher.group();
                                begin = matcher.start();
                                currentSearchPos = matcher.end();
                                searchInput.requestFocus();
                                searchInput.setSelection(begin, currentSearchPos);
                                searchOutput.setText(
                                    getString(R.string.tot_appear)
                                        + searchCount
                                        + getString(R.string.tot_appear_1)
                                        + currentSearchCount
                                        + getString(R.string.matches)
                                        + group);
                            } else {
                                pattern = Pattern.compile(searchTarget.getText().toString());
                                matcher = pattern.matcher(searchInput.getText().toString());
                                currentSearchCount = 0;
                                resetSearch();
                                onClick(findViewById(R.id.searchNext));
                            }
                        }
                    }
                } catch (Exception e) {
                    searchOutput.setText(getString(R.string.exception_occurred) + e.toString());
                }
                break;

            case R.id.searchReset:
                try {
                    resetSearch();
                    searchOutput.setText("");
                    Toasty.success(MainActivity.this, R.string.hint_reset, Toast.LENGTH_SHORT)
                        .show();
                } catch (Exception e) {
                    searchOutput.setText(getString(R.string.exception_occurred) + e.toString());
                } finally {
                    searchInput.clearFocus();
                    searchOutput.clearFocus();
                    searchTarget.clearFocus();
                }
                break;

            case R.id.searchAll:
                resetSearch();
                try {
                    String findTarget = searchTarget.getText().toString();
                    StringBuilder searchResult = new StringBuilder();
                    String findSrc = searchInput.getText().toString();
                    if (findTarget.isEmpty() || findSrc.isEmpty()) {
                        Toasty.error(
                            MainActivity.this,
                            R.string.search_is_empty,
                            Toast.LENGTH_LONG,
                            true)
                            .show();
                        return;
                    }
                    boolean doUseRegexInSearch = doUseRegexSearchCheckbox.isChecked();
                    if (!doUseRegexInSearch) {
                        String tempFindSrc = findSrc;
                        if (!settingsBoolean[0]) {
                            findTarget = findTarget.toLowerCase();
                            findSrc = findSrc.toLowerCase();
                        }
                        if (searchCount == -1) {
                            searchCount = stringAppearCounter(findSrc, findTarget);
                            currentSearchCount = 0;
                            currentSearchPos = 0;
                        }
                        if (searchCount == 0) {
                            searchOutput.setText(
                                getString(R.string.target_not_found), BufferType.EDITABLE);
                        } else {
                            for (int i = 0; i < searchCount; i++) {
                                currentSearchPos = findSrc.indexOf(findTarget, currentSearchPos);
                                searchResult
                                    .append(
                                        tempFindSrc.substring(
                                            currentSearchPos,
                                            currentSearchPos + findTarget.length()))
                                    .append("\n");
                                currentSearchPos++;
                            }
                        }
                    } else {
                        if (searchCount == -1) {
                            searchCount = regexAppearCounter(findSrc, findTarget);
                            currentSearchCount = 0;
                            currentSearchPos = 0;
                        }
                        if (searchCount == 0) {
                            searchOutput.setText(
                                getString(R.string.target_not_found), BufferType.EDITABLE);
                        } else {
                            currentSearchCount++;
                            while (matcher.find()) {
                                String group = matcher.group();
                                searchResult.append(group).append('\n');
                            }
                        }
                    }
                    if (!searchResult.toString().isEmpty()) {
                        searchOutput.setText(searchResult.toString());
                    }
                    resetSearch();
                    searchInput.clearFocus();
                    searchOutput.clearFocus();
                    searchTarget.clearFocus();
                } catch (Exception e) {
                    searchOutput.setText(getString(R.string.exception_occurred) + e.toString());
                    searchInput.clearFocus();
                    searchOutput.clearFocus();
                    searchTarget.clearFocus();
                }
                break;

            case R.id.encrypt:
                try {
                    if (salt.isEmpty()) {
                        if (encryptKey.getText().toString().isEmpty()) {
                            encryptOutput.setText(
                                getString(R.string.key_empty), BufferType.EDITABLE);
                            return;
                        }
                        String originText = encryptInput.getText().toString();
                        String password = encryptKey.getText().toString();
                        String encryptedText = AESCrypt.encrypt(password, originText);
                        encryptOutput.setText(encryptedText, BufferType.EDITABLE);
                    } else {
                        if (keyGenNeedToReset) {
                            if (encryptKey.getText().toString().isEmpty()) {
                                encryptOutput.setText(
                                    getString(R.string.key_empty), BufferType.EDITABLE);
                                return;
                            }
                            String saltBase64 =
                                Base64.encodeToString(salt.getBytes(), Base64.DEFAULT);
                            generatedKey =
                                AESUtils.generateKey(
                                    encryptKey.getText().toString(), saltBase64);
                            keyGenNeedToReset = false;
                        }
                        String encryptSourceText = encryptInput.getText().toString();
                        String encryptResult;
                        encryptResult = AESUtils.getEnString(encryptSourceText, generatedKey);
                        encryptOutput.setText(encryptResult, BufferType.EDITABLE);
                    }
                    encryptInput.clearFocus();
                    encryptOutput.clearFocus();
                    encryptKey.clearFocus();
                } catch (Exception e) {
                    encryptOutput.setText(
                        getString(R.string.exception_occurred) + e.toString(),
                        BufferType.EDITABLE);
                    encryptInput.clearFocus();
                    encryptOutput.clearFocus();
                    encryptKey.clearFocus();
                }
                break;

            case R.id.decrypt:
                try {
                    if (salt.isEmpty()) {
                        if (encryptKey.getText().toString().isEmpty()) {
                            encryptOutput.setText(
                                getString(R.string.key_empty), BufferType.EDITABLE);
                            return;
                        }
                        String encryptedMsg = encryptInput.getText().toString();
                        String password = encryptKey.getText().toString();
                        String decryptedText = AESCrypt.decrypt(password, encryptedMsg);
                        encryptOutput.setText(decryptedText, BufferType.EDITABLE);
                    } else {
                        if (keyGenNeedToReset) {
                            if (encryptKey.getText().toString().isEmpty()) {
                                encryptOutput.setText(
                                    getString(R.string.key_empty), BufferType.EDITABLE);
                                return;
                            }
                            String saltBase64 =
                                Base64.encodeToString(salt.getBytes(), Base64.DEFAULT);
                            generatedKey =
                                AESUtils.generateKey(
                                    encryptKey.getText().toString(), saltBase64);
                            keyGenNeedToReset = false;
                        }
                        String decryptSourceText = encryptInput.getText().toString();
                        String decryptResult;
                        decryptResult = AESUtils.getDeString(decryptSourceText, generatedKey);
                        encryptOutput.setText(decryptResult, BufferType.EDITABLE);
                    }
                    encryptInput.clearFocus();
                    encryptOutput.clearFocus();
                    encryptKey.clearFocus();
                } catch (Exception e) {
                    if (e instanceof IllegalArgumentException) {
                        if (salt.isEmpty()) {
                            encryptOutput.setText(
                                getString(R.string.not_encrypted), BufferType.EDITABLE);
                        } else {
                            encryptOutput.setText(
                                getString(R.string.not_encrypted)
                                    + "\n"
                                    + getString(R.string.salted_enc),
                                BufferType.EDITABLE);
                        }
                    } else if (e instanceof GeneralSecurityException) {
                        encryptOutput.setText(
                            getString(R.string.decrypt_fail), BufferType.EDITABLE);
                    } else {
                        encryptOutput.setText(
                            getString(R.string.exception_occurred) + e.toString(),
                            BufferType.EDITABLE);
                    }
                    encryptInput.clearFocus();
                    encryptOutput.clearFocus();
                    encryptKey.clearFocus();
                }
                break;

            default:
        }
    }

    private void findAll() {
        int tempSearchPos = currentSearchPos;
        int tempSearchCount = currentSearchCount;
        currentSearchPos = 0;
        currentSearchCount = 0;
        Pattern tempPattern = pattern;
        Matcher tempMatcher = matcher;
        pattern = Pattern.compile(searchTarget.getText().toString());
        matcher = pattern.matcher(searchInput.getText().toString());
        String findTarget = searchTarget.getText().toString();
        String findSrc = searchInput.getText().toString();
        if (findTarget.isEmpty() || findSrc.isEmpty()) {
            Toasty.error(MainActivity.this, R.string.search_is_empty, Toast.LENGTH_LONG, true)
                .show();
            return;
        }
        boolean doUseRegexInSearch = doUseRegexSearchCheckbox.isChecked();
        if (!doUseRegexInSearch) {
            if (!settingsBoolean[0]) {
                findTarget = findTarget.toLowerCase();
                findSrc = findSrc.toLowerCase();
            }
            if (searchCount == -1) {
                searchCount = stringAppearCounter(findSrc, findTarget);
                currentSearchCount = 0;
                currentSearchPos = 0;
            }
            if (searchCount == 0) {
                searchOutput.setText(getString(R.string.target_not_found), BufferType.EDITABLE);
            } else {
                for (int i = 0; i < searchCount; i++) {
                    currentSearchPos = findSrc.indexOf(findTarget, currentSearchPos);
                    searches.add(
                        new Range(currentSearchPos, currentSearchPos + findTarget.length()));
                    currentSearchPos = currentSearchPos + findTarget.length();
                }
            }
        } else {
            if (searchCount == -1) {
                searchCount = regexAppearCounter(findSrc, findTarget);
                currentSearchCount = 0;
                currentSearchPos = 0;
            }
            if (searchCount == 0) {
                searchOutput.setText(getString(R.string.target_not_found), BufferType.EDITABLE);
            } else {
                currentSearchCount++;
                while (matcher.find()) {
                    searches.add(new Range(matcher.start(), matcher.end()));
                }
            }
        }
        currentSearchPos = tempSearchPos;
        currentSearchCount = tempSearchCount;
        hasFind = true;
        pattern = tempPattern;
        matcher = tempMatcher;
    }

    private int getCurrentShowingLayoutId() {
        if (textReplaceLayout.getVisibility() == View.VISIBLE) {
            return R.id.textReplaceLayout;
        } else if (textShuffleLayout.getVisibility() == View.VISIBLE) {
            return R.id.textShuffleLayout;
        } else if (textSearchLayout.getVisibility() == View.VISIBLE) {
            return R.id.textSearchLayout;
        } else if (textEncryptLayout.getVisibility() == View.VISIBLE) {
            return R.id.textEncryptLayout;
        } else if (textMoreLayout.getVisibility() == View.VISIBLE) {
            return R.id.textMoreLayout;
        }
        return -1;
    }

    private void resetSearch() {
        try {
            currentSearchPos = 0;
            searchCount = -1;
            currentSearchCount = -1;
            begin = 0;
            pattern = Pattern.compile(searchTarget.getText().toString());
            matcher = pattern.matcher(searchInput.getText().toString());
            hasFind = false;
            searches.clear();
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUESTCODE_READ) {
                    // List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
                    List<String> list = data.getStringArrayListExtra("paths");
                    path = list.get(0);
                } else if (requestCode == REQUESTCODE_WRITE) {
                    path = data.getStringExtra("path");
                } else if (requestCode == 4) {
                    boolean easterEgg = data.getBooleanExtra("easter_egg", false);
                    if (easterEgg) {
                        rotateAll();
                    }
                } else if (requestCode == 5) {
                    boolean easterEggActivated =
                        data != null && data.getBooleanExtra("easter_egg1", false);
                    if (easterEggActivated) {
                        rotateAll();
                    }
                    loadSettings(false);
                } else if (requestCode == 6) {
                    String regex = data.getStringExtra("regexValue");
                    setRegex(regex);
                }
            }
            if (requestCode == REQUESTCODE_READ) {
                String getFileContent = readToString(path);
                if (getFileContent != null) {
                    switch (getCurrentShowingLayoutId()) {
                        case R.id.textReplaceLayout:
                            replaceInput.setText(getFileContent, BufferType.EDITABLE);
                            break;

                        case R.id.textSearchLayout:
                            searchInput.setText(getFileContent, BufferType.EDITABLE);
                            break;

                        case R.id.textShuffleLayout:
                            shuffleInput.setText(getFileContent, BufferType.EDITABLE);
                            break;

                        case R.id.textEncryptLayout:
                            encryptInput.setText(getFileContent, BufferType.EDITABLE);
                            break;

                        case R.id.textMoreLayout:
                            moreInput.setText(getFileContent, BufferType.EDITABLE);
                            break;

                        default:
                    }
                }
            } else if (requestCode == REQUESTCODE_WRITE) {
                if (path.isEmpty()) {
                    Toasty.warning(
                        MainActivity.this,
                        R.string.target_folder_not_selected,
                        Toast.LENGTH_LONG,
                        true)
                        .show();
                    return;
                }
                String outputString = "";
                switch (getCurrentShowingLayoutId()) {
                    case R.id.textReplaceLayout:
                        outputString = replaceOutput.getText().toString();
                        break;

                    case R.id.textSearchLayout:
                        outputString = searchOutput.getText().toString();
                        break;

                    case R.id.textShuffleLayout:
                        outputString = shuffleOutput.getText().toString();
                        break;

                    case R.id.textEncryptLayout:
                        outputString = encryptOutput.getText().toString();
                        break;

                    case R.id.textMoreLayout:
                        outputString = moreOutput.getText().toString();
                        break;

                    default:
                }
                StringBuilder filename = new StringBuilder(path);
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy-MM-dd_HH:mm:ss");
                Date date = new Date();
                filename.append("/TextConverter-").append(sdf.format(date)).append(".txt");
                writeSDFile(filename.toString(), outputString);
                Toasty.success(
                    MainActivity.this,
                    getString(R.string.file_saved) + filename,
                    Toast.LENGTH_LONG,
                    true)
                    .show();
            }
        } catch (Exception e) {
            Toasty.error(
                MainActivity.this,
                getString(R.string.exception_occurred) + e.toString(),
                Toast.LENGTH_LONG,
                true)
                .show();
        }
    }

    public void writeSDFile(String fileName, String write_str) throws IOException {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        String outputEncoding = sharedPreferences.getString("outputEncoding", "UTF-8");
        File file = new File(fileName);
        FileOutputStream fos = new FileOutputStream(file);
        byte[] bytes = write_str.getBytes(Objects.requireNonNull(outputEncoding));
        fos.write(bytes);
        fos.close();
    }

    private String readToString(String fileName) {

        File file = new File(fileName);

        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        detector.add(new ParsingDetector(false));
        detector.add(JChardetFacade.getInstance());
        detector.add(ASCIIDetector.getInstance());
        detector.add(UnicodeDetector.getInstance());
        java.nio.charset.Charset charset = null;
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean doAlwaysUtf8 = sharedPreferences.getBoolean("doOpenWithUtf8", false);

        if (doAlwaysUtf8) {
            encoding = "UTF-8";
        } else {
            try {
                charset = detector.detectCodepage(file.toURI().toURL());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (charset != null) {
                encoding = charset.name();
            }
            if (encoding.equals("US-ASCII")) {
                encoding = "UTF-8";
            }
        }
        if (!encoding.equals("UTF-8")) {
            Toasty.warning(
                MainActivity.this,
                getString(R.string.current_encoding) + encoding,
                Toast.LENGTH_LONG)
                .show();
        }
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            int result = in.read(filecontent);
            if (result == -1) {
                in.close();
            } else {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            Toasty.error(
                MainActivity.this,
                getString(R.string.encoding_not_supported) + encoding,
                Toast.LENGTH_LONG)
                .show();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getFile();
                } else {
                    Toasty.error(
                        MainActivity.this,
                        R.string.file_permission_denied,
                        Toast.LENGTH_LONG,
                        true)
                        .show();
                }
                break;
            case 2:
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getStoreLocation();
                } else {
                    Toasty.error(
                        MainActivity.this,
                        R.string.file_permission_denied,
                        Toast.LENGTH_LONG,
                        true)
                        .show();
                }
                break;
            case 3:
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    storeDirectly();
                } else {
                    Toasty.error(
                        MainActivity.this,
                        R.string.file_permission_denied,
                        Toast.LENGTH_LONG,
                        true)
                        .show();
                }
                break;
            default:
        }
    }

    private void getStoreLocation() {
        String pathTemp = defaultPath;
        File destDir = new File(pathTemp);
        if (!destDir.exists()) {
            boolean doMkdirSuccess = destDir.mkdirs();
            if (!doMkdirSuccess) {
                Toasty.error(
                    MainActivity.this,
                    R.string.folder_create_failed,
                    Toast.LENGTH_LONG,
                    true)
                    .show();
                pathTemp = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        }
        path = "";
        new LFilePicker()
            .withActivity(MainActivity.this)
            .withBackgroundColor("#03a9f4")
            .withRequestCode(REQUESTCODE_WRITE)
            .withTitle(getString(R.string.select_target_folder))
            .withChooseMode(false)
            .withStartPath(pathTemp)
            .withIconStyle(Constant.ICON_STYLE_YELLOW)
            .withBackIcon(Constant.BACKICON_STYLETHREE)
            .start();
    }

    private void storeDirectly() {
        try {
            path = defaultPath;
            File destDir = new File(path);
            if (!destDir.exists()) {
                boolean doMkdirSuccess = destDir.mkdirs();
                if (!doMkdirSuccess) {
                    Toasty.error(
                        MainActivity.this,
                        R.string.folder_create_failed,
                        Toast.LENGTH_LONG,
                        true)
                        .show();
                    return;
                }
            }
            String outputString = "";
            switch (getCurrentShowingLayoutId()) {
                case R.id.textReplaceLayout:
                    outputString = replaceOutput.getText().toString();
                    break;

                case R.id.textSearchLayout:
                    outputString = searchOutput.getText().toString();
                    break;

                case R.id.textShuffleLayout:
                    outputString = shuffleOutput.getText().toString();
                    break;

                case R.id.textEncryptLayout:
                    outputString = encryptOutput.getText().toString();
                    break;

                case R.id.textMoreLayout:
                    outputString = moreOutput.getText().toString();
                    break;

                default:
            }
            StringBuilder filename = new StringBuilder(path);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("yyyy-MM-dd_HH:mm:ss");
            Date date = new Date();
            filename.append("/TextConverter-").append(sdf.format(date)).append(".txt");
            writeSDFile(filename.toString(), outputString);
            Toasty.info(
                MainActivity.this,
                getString(R.string.text_too_long_saved) + filename,
                Toast.LENGTH_LONG,
                true)
                .show();
        } catch (Exception e) {
            Toasty.error(
                MainActivity.this,
                getString(R.string.exception_occurred) + e.toString(),
                Toast.LENGTH_LONG,
                true)
                .show();
        } finally {
            path = "";
        }
    }

    private void getFile() {
        path = "";
        new LFilePicker()
            .withActivity(MainActivity.this)
            .withBackgroundColor("#03a9f4")
            .withRequestCode(REQUESTCODE_READ)
            .withMutilyMode(false)
            .withTitle(getString(R.string.pick_folder))
            .withIconStyle(Constant.ICON_STYLE_YELLOW)
            .withBackIcon(Constant.BACKICON_STYLETHREE)
            .withIsGreater(false)
            .withFileSize(10 * 1048576)
            .start();
    }

    public char switchCase(char ch) {
        if (ch >= 'a' && ch <= 'z') {
            return Character.toUpperCase(ch);
        } else if (ch >= 'A' && ch <= 'Z') {
            return Character.toLowerCase(ch);
        } else {
            return ch;
        }
    }

    private void rotateAll() {
        textReplaceLayout.setRotation(180);
        textMoreLayout.setRotation(180);
        textEncryptLayout.setRotation(180);
        textShuffleLayout.setRotation(180);
        textSearchLayout.setRotation(180);
        Toasty.custom(
            MainActivity.this,
            ":-p",
            R.drawable.ic_check_white_48dp,
            getColor(R.color.mdPurple),
            Toast.LENGTH_LONG,
            false,
            true)
            .show();
    }

    private void setMultiLine(boolean whether) {
        whether = !whether;
        replaceInput.setHorizontallyScrolling(whether);
        replaceOutput.setHorizontallyScrolling(whether);
        targetSeq.setHorizontallyScrolling(whether);
        replaceTo.setHorizontallyScrolling(whether);
        searchInput.setHorizontallyScrolling(whether);
        searchOutput.setHorizontallyScrolling(whether);
        searchTarget.setHorizontallyScrolling(whether);
        shuffleInput.setHorizontallyScrolling(whether);
        shuffleOutput.setHorizontallyScrolling(whether);
        encryptInput.setHorizontallyScrolling(whether);
        encryptOutput.setHorizontallyScrolling(whether);
        encryptKey.setHorizontallyScrolling(whether);
        moreInput.setHorizontallyScrolling(whether);
        moreOutput.setHorizontallyScrolling(whether);
    }

    private void loadSettings(boolean doReloadLayout) {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
            settingsBoolean[0] = sharedPreferences.getBoolean("doCapsSensitive", false);
            settingsBoolean[1] = sharedPreferences.getBoolean("doUseMonospaced", false);
            settingsBoolean[2] = sharedPreferences.getBoolean("doLowerCaseMorseCode", false);
            settingsBoolean[3] = sharedPreferences.getBoolean("doMultiLine", true);
            settingsBoolean[4] = sharedPreferences.getBoolean("doCheckClipboard", true);
            isGuideShown = sharedPreferences.getBoolean("isGuideShown", false);
            salt = sharedPreferences.getString("salt", defaultSalt);
            initialLayout =
                Integer.parseInt(
                    Objects.requireNonNull(
                        sharedPreferences.getString("initialLayout", "0")));
            defaultPath =
                sharedPreferences.getString(
                    "default_path",
                    Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/TextConverter");
            setMultiLine(settingsBoolean[3]);
            if (settingsBoolean[1]) {
                replaceInput.setTextAppearance(R.style.MyMonospace);
                replaceOutput.setTextAppearance(R.style.MyMonospace);
                replaceTo.setTextAppearance(R.style.MyMonospace);
                targetSeq.setTextAppearance(R.style.MyMonospace);
                searchInput.setTextAppearance(R.style.MyMonospace);
                searchOutput.setTextAppearance(R.style.MyMonospace);
                searchTarget.setTextAppearance(R.style.MyMonospace);
                shuffleInput.setTextAppearance(R.style.MyMonospace);
                shuffleOutput.setTextAppearance(R.style.MyMonospace);
                encryptInput.setTextAppearance(R.style.MyMonospace);
                encryptOutput.setTextAppearance(R.style.MyMonospace);
                encryptKey.setTextAppearance(R.style.MyMonospace);
                moreInput.setTextAppearance(R.style.MyMonospace);
                moreOutput.setTextAppearance(R.style.MyMonospace);
            } else {
                replaceInput.setTextAppearance(R.style.MyRegular);
                replaceOutput.setTextAppearance(R.style.MyRegular);
                replaceTo.setTextAppearance(R.style.MyRegular);
                targetSeq.setTextAppearance(R.style.MyRegular);
                searchInput.setTextAppearance(R.style.MyRegular);
                searchOutput.setTextAppearance(R.style.MyRegular);
                searchTarget.setTextAppearance(R.style.MyRegular);
                shuffleInput.setTextAppearance(R.style.MyRegular);
                shuffleOutput.setTextAppearance(R.style.MyRegular);
                encryptInput.setTextAppearance(R.style.MyRegular);
                encryptOutput.setTextAppearance(R.style.MyRegular);
                encryptKey.setTextAppearance(R.style.MyRegular);
                moreInput.setTextAppearance(R.style.MyRegular);
                moreOutput.setTextAppearance(R.style.MyRegular);
            }
            if (doReloadLayout) {
                switch (initialLayout) {
                    case 0:
                        textReplaceLayout.setVisibility(View.VISIBLE);
                        textShuffleLayout.setVisibility(View.GONE);
                        textSearchLayout.setVisibility(View.GONE);
                        textEncryptLayout.setVisibility(View.GONE);
                        textMoreLayout.setVisibility(View.GONE);
                        moreFab.setVisibility(View.INVISIBLE);
                        setTitle(R.string.string_replace);
                        break;

                    case 1:
                        textSearchLayout.setVisibility(View.VISIBLE);
                        textShuffleLayout.setVisibility(View.GONE);
                        textReplaceLayout.setVisibility(View.GONE);
                        textEncryptLayout.setVisibility(View.GONE);
                        textMoreLayout.setVisibility(View.GONE);
                        moreFab.setVisibility(View.INVISIBLE);
                        setTitle(R.string.text_search);
                        break;

                    case 2:
                        textShuffleLayout.setVisibility(View.VISIBLE);
                        textReplaceLayout.setVisibility(View.GONE);
                        textSearchLayout.setVisibility(View.GONE);
                        textEncryptLayout.setVisibility(View.GONE);
                        textMoreLayout.setVisibility(View.GONE);
                        moreFab.setVisibility(View.INVISIBLE);
                        setTitle(R.string.string_shuffle_sort);
                        break;

                    case 3:
                        textEncryptLayout.setVisibility(View.VISIBLE);
                        textSearchLayout.setVisibility(View.GONE);
                        textShuffleLayout.setVisibility(View.GONE);
                        textReplaceLayout.setVisibility(View.GONE);
                        textMoreLayout.setVisibility(View.GONE);
                        moreFab.setVisibility(View.INVISIBLE);
                        setTitle(R.string.text_encrypt);
                        break;

                    case 4:
                        textMoreLayout.setVisibility(View.VISIBLE);
                        textEncryptLayout.setVisibility(View.GONE);
                        textSearchLayout.setVisibility(View.GONE);
                        textShuffleLayout.setVisibility(View.GONE);
                        textReplaceLayout.setVisibility(View.GONE);
                        moreFab.setVisibility(View.VISIBLE);
                        setTitle(R.string.more_handy_function);
                        break;

                    default:
                }
            }
        } catch (Exception e) {
            Toasty.error(
                MainActivity.this,
                getString(R.string.error_loading_prefs) + e.toString(),
                Toast.LENGTH_LONG)
                .show();
        }
        try {
            String shortcutData = getIntent().getDataString();
            int showLayoutId = -1;
            if (shortcutData != null && !alreadyLoadedShortcut) {
                showLayoutId = Integer.parseInt(shortcutData);
                alreadyLoadedShortcut = true;
            }
            switch (showLayoutId) {
                case -1:
                    break;

                case 0:
                    textReplaceLayout.setVisibility(View.VISIBLE);
                    textShuffleLayout.setVisibility(View.GONE);
                    textSearchLayout.setVisibility(View.GONE);
                    textEncryptLayout.setVisibility(View.GONE);
                    textMoreLayout.setVisibility(View.GONE);
                    moreFab.setVisibility(View.INVISIBLE);
                    setTitle(R.string.string_replace);
                    break;

                case 1:
                    textSearchLayout.setVisibility(View.VISIBLE);
                    textShuffleLayout.setVisibility(View.GONE);
                    textReplaceLayout.setVisibility(View.GONE);
                    textEncryptLayout.setVisibility(View.GONE);
                    textMoreLayout.setVisibility(View.GONE);
                    moreFab.setVisibility(View.INVISIBLE);
                    setTitle(R.string.text_search);
                    break;

                case 2:
                    textShuffleLayout.setVisibility(View.VISIBLE);
                    textReplaceLayout.setVisibility(View.GONE);
                    textSearchLayout.setVisibility(View.GONE);
                    textEncryptLayout.setVisibility(View.GONE);
                    textMoreLayout.setVisibility(View.GONE);
                    moreFab.setVisibility(View.INVISIBLE);
                    setTitle(R.string.string_shuffle_sort);
                    break;

                case 3:
                    textEncryptLayout.setVisibility(View.VISIBLE);
                    textSearchLayout.setVisibility(View.GONE);
                    textShuffleLayout.setVisibility(View.GONE);
                    textReplaceLayout.setVisibility(View.GONE);
                    textMoreLayout.setVisibility(View.GONE);
                    moreFab.setVisibility(View.INVISIBLE);
                    setTitle(R.string.text_encrypt);
                    break;

                case 4:
                    textMoreLayout.setVisibility(View.VISIBLE);
                    textEncryptLayout.setVisibility(View.GONE);
                    textSearchLayout.setVisibility(View.GONE);
                    textShuffleLayout.setVisibility(View.GONE);
                    textReplaceLayout.setVisibility(View.GONE);
                    moreFab.setVisibility(View.VISIBLE);
                    setTitle(R.string.more_handy_function);
                    break;

                default:
            }
        } catch (Exception ignored) {
        }
    }

    private void initList() {
        // 0
        ListItems reverseString =
            new ListItems(getString(R.string.string_reverse), getString(R.string.reverse_disc));
        itemsList.add(reverseString);
        // 1
        ListItems multiplyString =
            new ListItems(
                getString(R.string.string_copy), getString(R.string.string_copy_disc));
        itemsList.add(multiplyString);
        // 2
        ListItems switchCase =
            new ListItems(
                getString(R.string.switch_cases), getString(R.string.switch_caps_disc));
        itemsList.add(switchCase);
        // 3
        ListItems addNumbers =
            new ListItems(
                getString(R.string.add_numbers), getString(R.string.add_numbers_disc));
        itemsList.add(addNumbers);
        // 4
        ListItems htmlFormat =
            new ListItems(
                getString(R.string.format_html), getString(R.string.format_html_disc));
        itemsList.add(htmlFormat);
        // 5
        ListItems customRandom =
            new ListItems(
                getString(R.string.custom_random), getString(R.string.custom_random_disc));
        itemsList.add(customRandom);
        // 6
        ListItems generateMD5 =
            new ListItems(
                getString(R.string.generate_md5), getString(R.string.generate_md5_disc));
        itemsList.add(generateMD5);
        // 7
        ListItems base64encode =
            new ListItems(getString(R.string.to_base_64), getString(R.string.to_base64_disc));
        itemsList.add(base64encode);
        // 8
        ListItems base64decode =
            new ListItems(
                getString(R.string.from_base_64), getString(R.string.from_base64_disc));
        itemsList.add(base64decode);
        // 9
        ListItems morseCode =
            new ListItems(
                getString(R.string.to_morse_code), getString(R.string.morse_code_disc));
        itemsList.add(morseCode);
        // 10
        ListItems formatJson =
            new ListItems(
                getString(R.string.format_json), getString(R.string.format_json_disc));
        itemsList.add(formatJson);
        // 11
        ListItems textRandom =
            new ListItems(
                getString(R.string.text_random), getString(R.string.text_random_disc));
        itemsList.add(textRandom);
        // 12
        ListItems markdownPreview =
            new ListItems(
                getString(R.string.markdown_preview),
                getString(R.string.markdown_preview_disc));
        itemsList.add(markdownPreview);
        // 13
        ListItems unicodeParse =
            new ListItems(
                getString(R.string.unicode_parse), getString(R.string.unicode_parse_disc));
        itemsList.add(unicodeParse);
        // 14
        ListItems toUnicode =
            new ListItems(getString(R.string.to_unicode), getString(R.string.to_unicode_disc));
        itemsList.add(toUnicode);
        // 15
        ListItems stringSimilarity =
            new ListItems(
                getString(R.string.string_similarity),
                getString(R.string.string_similarity_disc));
        itemsList.add(stringSimilarity);
        // 16
        ListItems formatJava =
            new ListItems(
                getString(R.string.format_java_code), getString(R.string.format_java_disc));
        itemsList.add(formatJava);
        // 17
        ListItems stringTrim =
            new ListItems(getString(R.string.trim), getString(R.string.trim_disc));
        itemsList.add(stringTrim);
        // 18
        ListItems escapeEverything =
            new ListItems(getString(R.string.parse_all), getString(R.string.parse_all_disc));
        itemsList.add(escapeEverything);
        // 19
        ListItems parseRegex =
            new ListItems(
                getString(R.string.parse_regex), getString(R.string.parse_regex_disc));
        itemsList.add(parseRegex);
        // 20
        ListItems randString =
            new ListItems(getString(R.string.rand_str), getString(R.string.rand_str_disc));
        itemsList.add(randString);
        // 21
        ListItems toPinyin =
            new ListItems(
                getString(R.string.hanyu_to_pinyin),
                getString(R.string.hanyu_to_pinyin_disc));
        itemsList.add(toPinyin);
    }

    @SuppressLint("SetTextI18n")
    private void showFunctionsMenu() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        dialogPlus =
            DialogPlus.newDialog(this)
                .setAdapter(adapter)
                .setGravity(Gravity.BOTTOM)
                .setExpanded(true, (int) Math.floor(displayMetrics.heightPixels * 0.5))
                .setCancelable(true)
                .setHeader(R.layout.dialog_header)
                .setOnItemClickListener(
                    (dialog, item, view, position) -> {
                        switch (position) {
                            case 0: // reverse
                                try {
                                    String reverseSrc = moreInput.getText().toString();
                                    StringBuilder reverseResult = new StringBuilder();
                                    for (int i = reverseSrc.length() - 1; i >= 0; i--) {
                                        reverseResult.append(reverseSrc.charAt(i));
                                    }
                                    moreOutput.setText(
                                        reverseResult.toString(),
                                        BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                } catch (Exception e) {
                                    moreOutput.setText(
                                        getString(R.string.exception_occurred)
                                            + e.toString(),
                                        BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                }
                                break;

                            case 1: // copy
                                LovelyTextInputDialog lovelyTextInputDialog =
                                    new LovelyTextInputDialog(MainActivity.this);
                                lovelyTextInputDialog
                                    .setTopColorRes(R.color.settingsGrey)
                                    .setIcon(R.drawable.ic_content_copy_black_24dp)
                                    .setTitle(getString(R.string.copy_copies))
                                    .setCancelable(true)
                                    .setMessage(
                                        getString(R.string.input_copy_copies))
                                    .configureView(
                                        v14 -> {
                                            v14.setFocusableInTouchMode(true);
                                            v14.setFocusable(true);
                                        })
                                    .configureEditText(
                                        v15 -> {
                                            v15.setHint(
                                                getString(
                                                    R.string
                                                        .input_copies_1));
                                            v15.setInputType(
                                                InputType
                                                    .TYPE_CLASS_NUMBER);
                                            if (getSharedPreferences(
                                                "settings",
                                                MODE_PRIVATE)
                                                .getBoolean(
                                                    "doUseMonospaced",
                                                    false)) {
                                                v15.setTextAppearance(
                                                    R.style.MyMonospace);
                                            } else {
                                                v15.setTextAppearance(
                                                    R.style.MyRegular);
                                            }
                                            v15.clearFocus();
                                        })
                                    .setConfirmButtonColor(
                                        getColor(R.color.colorAccent))
                                    .setConfirmButton(
                                        R.string.confirm,
                                        text -> {
                                            try {
                                                if (text.isEmpty()) {
                                                    Toasty.error(
                                                        MainActivity
                                                            .this,
                                                        R.string
                                                            .empty_input,
                                                        Toast
                                                            .LENGTH_LONG)
                                                        .show();
                                                    return;
                                                }
                                                StringBuilder outputStr =
                                                    new StringBuilder();
                                                String inputStr =
                                                    moreInput
                                                        .getText()
                                                        .toString();
                                                int n = Integer.parseInt(text);
                                                for (int i = 0; i < n; i++) {
                                                    outputStr.append(inputStr);
                                                }
                                                moreOutput.setText(
                                                    outputStr.toString(),
                                                    BufferType.EDITABLE);
                                            } catch (Exception e) {
                                                Toasty.error(
                                                    MainActivity
                                                        .this,
                                                    getString(
                                                        R.string
                                                            .exception_occurred)
                                                        + e
                                                        .toString(),
                                                    Toast
                                                        .LENGTH_LONG)
                                                    .show();
                                            } finally {
                                                moreInput.clearFocus();
                                                moreOutput.clearFocus();
                                            }
                                        })
                                    .create()
                                    .show();

                                break;

                            case 2: // caps switch
                                try {
                                    final int[] mode = {0};
                                    Builder alertDialog =
                                        new Builder(MainActivity.this);
                                    alertDialog
                                        .setTitle(getString(R.string.select_mode))
                                        .setIcon(R.mipmap.ic_launcher)
                                        .setItems(
                                            capsSwitchModes,
                                            (dialog1, which) -> {
                                                mode[0] = which;
                                                String caseSrc =
                                                    moreInput
                                                        .getText()
                                                        .toString();
                                                StringBuilder caseOutput =
                                                    new StringBuilder();
                                                switch (mode[0]) {
                                                    case ALL_LOWER:
                                                        caseOutput.append(
                                                            caseSrc
                                                                .toLowerCase());
                                                        break;

                                                    case ALL_UPPER:
                                                        caseOutput.append(
                                                            caseSrc
                                                                .toUpperCase());
                                                        break;

                                                    case CASE_REVERSE:
                                                        for (int i = 0;
                                                            i
                                                                < caseSrc
                                                                .length();
                                                            i++) {
                                                            caseOutput.append(
                                                                switchCase(
                                                                    caseSrc
                                                                        .charAt(
                                                                            i)));
                                                        }
                                                        break;

                                                    case FIRST_UPPER:
                                                        // String[]
                                                        // words =
                                                        // caseSrc.split("[
                                                        // \n\r\t|!@#$%^&*(),./?><:\"《》，'。；‘：“—+=！￥…（）\\[\\]~\\\\]");
                                                        final String symbols =
                                                            " \n\r\t|!@#$%^&*(),./?><:\"《》，'。；‘：“—+=！￥…（）[]~\\";
                                                        boolean wordBegin =
                                                            true;
                                                        for (int i = 0;
                                                            i
                                                                < caseSrc
                                                                .length();
                                                            i++) {
                                                            if (wordBegin) {
                                                                caseOutput
                                                                    .append(
                                                                        Character
                                                                            .toUpperCase(
                                                                                caseSrc
                                                                                    .charAt(
                                                                                        i)));
                                                                wordBegin =
                                                                    false;
                                                            } else {
                                                                caseOutput
                                                                    .append(
                                                                        Character
                                                                            .toLowerCase(
                                                                                caseSrc
                                                                                    .charAt(
                                                                                        i)));
                                                            }
                                                            if (symbols
                                                                .contains(
                                                                    String
                                                                        .valueOf(
                                                                            caseSrc
                                                                                .charAt(
                                                                                    i)))) {
                                                                wordBegin =
                                                                    true;
                                                            }
                                                        }

                                                        break;

                                                    case SENTENCE_FIRST:
                                                        boolean sentenceBegin =
                                                            true;
                                                        final String
                                                            endSentence =
                                                            "\n\r\t?!.？。！…;；";
                                                        for (int i = 0;
                                                            i
                                                                < caseSrc
                                                                .length();
                                                            i++) {
                                                            if (sentenceBegin
                                                                && ((caseSrc
                                                                .charAt(
                                                                    i)
                                                                >= 'a'
                                                                && caseSrc
                                                                .charAt(
                                                                    i)
                                                                <= 'z')
                                                                || (caseSrc
                                                                .charAt(
                                                                    i)
                                                                >= 'A'
                                                                && caseSrc
                                                                .charAt(
                                                                    i)
                                                                <= 'Z'))) {
                                                                caseOutput
                                                                    .append(
                                                                        Character
                                                                            .toUpperCase(
                                                                                caseSrc
                                                                                    .charAt(
                                                                                        i)));
                                                                sentenceBegin =
                                                                    false;
                                                            } else {
                                                                caseOutput
                                                                    .append(
                                                                        Character
                                                                            .toLowerCase(
                                                                                caseSrc
                                                                                    .charAt(
                                                                                        i)));
                                                            }
                                                            if (endSentence
                                                                .contains(
                                                                    String
                                                                        .valueOf(
                                                                            caseSrc
                                                                                .charAt(
                                                                                    i)))) {
                                                                sentenceBegin =
                                                                    true;
                                                            }
                                                        }
                                                        break;

                                                    default:
                                                }
                                                moreOutput.setText(
                                                    caseOutput.toString(),
                                                    BufferType.EDITABLE);
                                                moreOutput.clearFocus();
                                                moreInput.clearFocus();
                                            })
                                        .create()
                                        .show();
                                } catch (Exception e) {
                                    moreOutput.setText(
                                        getString(R.string.exception_occurred)
                                            + e.toString(),
                                        BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                }
                                break;

                            case 3: // add numbers
                                try {
                                    String addNumbersSrc =
                                        moreInput.getText().toString();
                                    String numberParagraphs[] =
                                        addNumbersSrc.split("\\cJ");
                                    StringBuilder addNumbersOutput =
                                        new StringBuilder();
                                    for (int i = 1; i <= numberParagraphs.length; i++) {
                                        addNumbersOutput
                                            .append(i)
                                            .append('.')
                                            .append(numberParagraphs[i - 1])
                                            .append('\n');
                                    }
                                    moreOutput.setText(
                                        addNumbersOutput, BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                } catch (Exception e) {
                                    moreOutput.setText(
                                        getString(R.string.exception_occurred)
                                            + e.toString(),
                                        BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                }
                                break;

                            case 4: // HTML formatter
                                try {
                                    Tidy tidy = new Tidy();
                                    tidy.setXHTML(true);
                                    tidy.setIndentContent(true);
                                    tidy.setPrintBodyOnly(true);
                                    tidy.setTidyMark(false);
                                    Document htmlDOM =
                                        tidy.parseDOM(
                                            new ByteArrayInputStream(
                                                moreInput
                                                    .getText()
                                                    .toString()
                                                    .getBytes()),
                                            null);
                                    OutputStream out = new ByteArrayOutputStream();
                                    tidy.pprint(htmlDOM, out);
                                    moreOutput.setText(out.toString());
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                } catch (Exception e) {

                                    moreOutput.setText(
                                        getString(R.string.exception_occurred)
                                            + e.toString(),
                                        BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                }
                                break;

                            case 5: // custom random
                                try {
                                    final String[] dataQuantityString = {"1"};
                                    // final AlertDialog.Builder dialog
                                    // = new AlertDialog.Builder(this);
                                    final LovelyCustomDialog dialog1 =
                                        new LovelyCustomDialog(MainActivity.this);
                                    LayoutInflater layoutInflater =
                                        LayoutInflater.from(MainActivity.this);
                                    @SuppressLint("InflateParams")
                                    View dialogView =
                                        layoutInflater.inflate(
                                            R.layout.dialog_set_quantity, null);
                                    final EditText dataQuantity =
                                        dialogView.findViewById(R.id.dataQuantity);
                                    dialog1.setMessage(R.string.format_instruction)
                                        .setView(dialogView)
                                        .setCancelable(false)
                                        .setTitle(R.string.custom_random)
                                        .setTopColorRes(R.color.colorAccent)
                                        .setIcon(R.drawable.ic_custom)
                                        .setListener(
                                            R.id.viewInstance,
                                            v13 -> {
                                                moreInput.setText(
                                                    getString(
                                                        R.string
                                                            .custom_random_example));
                                                dialog1.dismiss();
                                            })
                                        .setListener(
                                            R.id.cancelDialog,
                                            v12 -> dialog1.dismiss())
                                        .setListener(
                                            R.id.confirmDialog,
                                            v1 -> {
                                                try {
                                                    Random random =
                                                        new Random();
                                                    dataQuantityString[0] =
                                                        dataQuantity
                                                            .getText()
                                                            .toString();
                                                    if (dataQuantityString[0]
                                                        .isEmpty()) {
                                                        dataQuantityString[0] =
                                                            "1";
                                                    }

                                                    int quantity =
                                                        Integer.parseInt(
                                                            dataQuantityString[
                                                                0]);
                                                    StringBuilder
                                                        finalOutputBuilder =
                                                        new StringBuilder();
                                                    int min = 0, max = 0;
                                                    String format =
                                                        moreInput
                                                            .getText()
                                                            .toString();
                                                    for (int q = 0;
                                                        q < quantity;
                                                        q++) {
                                                        StringBuilder
                                                            tempOutput =
                                                            new StringBuilder();
                                                        for (int i = 0;
                                                            i
                                                                < format
                                                                .length();
                                                            i++) {
                                                            if (i
                                                                < format
                                                                .length()
                                                                - 1
                                                                && format
                                                                .charAt(
                                                                    i)
                                                                == '{'
                                                                && format
                                                                .charAt(
                                                                    i
                                                                        + 1)
                                                                == '{') {
                                                                i++;
                                                                tempOutput
                                                                    .append(
                                                                        '{');
                                                                continue;
                                                            }
                                                            if (format.charAt(i)
                                                                == '{') {
                                                                int length = 0;
                                                                for (int j = 1;
                                                                    i + j
                                                                        < format
                                                                        .length();
                                                                    j++) {
                                                                    if (format
                                                                        .charAt(
                                                                            i
                                                                                + j)
                                                                        == ','
                                                                        || format
                                                                        .charAt(
                                                                            i
                                                                                + j)
                                                                        == '，') {
                                                                        min =
                                                                            Integer
                                                                                .parseInt(
                                                                                    format
                                                                                        .substring(
                                                                                            i
                                                                                                + 1,
                                                                                            i
                                                                                                + j));
                                                                        for (int
                                                                            k =
                                                                            1;
                                                                            i
                                                                                + j
                                                                                + k
                                                                                < format
                                                                                .length();
                                                                            k++) {
                                                                            if (format
                                                                                .charAt(
                                                                                    i
                                                                                        + j
                                                                                        + k)
                                                                                == '}') {
                                                                                max =
                                                                                    Integer
                                                                                        .parseInt(
                                                                                            format
                                                                                                .substring(
                                                                                                    i
                                                                                                        + j
                                                                                                        + 1,
                                                                                                    i
                                                                                                        + j
                                                                                                        + k));
                                                                                length =
                                                                                    j
                                                                                        + k;
                                                                                break;
                                                                            }
                                                                        }
                                                                        break;
                                                                    }
                                                                }
                                                                int randNum =
                                                                    random
                                                                        .nextInt(
                                                                            max
                                                                                - min
                                                                                + 1)
                                                                        + min;
                                                                tempOutput
                                                                    .append(
                                                                        String
                                                                            .valueOf(
                                                                                randNum));
                                                                i += length;
                                                            } else {
                                                                tempOutput
                                                                    .append(
                                                                        format
                                                                            .charAt(
                                                                                i));
                                                            }
                                                        }
                                                        finalOutputBuilder
                                                            .append(
                                                                tempOutput
                                                                    .toString());
                                                    }
                                                    moreOutput.setText(
                                                        finalOutputBuilder
                                                            .toString(),
                                                        BufferType
                                                            .EDITABLE);
                                                } catch (Exception e) {
                                                    if (e
                                                        instanceof
                                                        NumberFormatException) {
                                                        moreOutput.setText(
                                                            getString(
                                                                R.string
                                                                    .format_err)
                                                                + e
                                                                .toString(),
                                                            BufferType
                                                                .EDITABLE);
                                                    } else if (e
                                                        instanceof
                                                        IllegalArgumentException) {
                                                        moreOutput.setText(
                                                            getString(
                                                                R.string
                                                                    .args_err),
                                                            BufferType
                                                                .EDITABLE);
                                                    } else {
                                                        moreOutput.setText(
                                                            getString(
                                                                R.string
                                                                    .exception_occurred)
                                                                + e
                                                                .toString(),
                                                            BufferType
                                                                .EDITABLE);
                                                    }
                                                } finally {
                                                    dialog1.dismiss();
                                                    moreOutput.clearFocus();
                                                    moreInput.clearFocus();
                                                }
                                            })
                                        .create()
                                        .show();
                                } catch (Exception e) {
                                    moreOutput.setText(
                                        getString(R.string.exception_occurred)
                                            + e.toString(),
                                        BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                }
                                break;

                            case 6: // generate MD5
                                try {
                                    String md5Src = moreInput.getText().toString();
                                    String md5 = getMD5(md5Src);
                                    moreOutput.setText(md5, BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                } catch (Exception e) {
                                    moreOutput.setText(
                                        getString(R.string.exception_occurred)
                                            + e.toString(),
                                        BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                }
                                break;

                            case 7: // Base64 encode
                                try {
                                    String plainSrc = moreInput.getText().toString();
                                    String base64 =
                                        Base64.encodeToString(
                                            plainSrc.getBytes(),
                                            Base64.DEFAULT);
                                    moreOutput.setText(base64, BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                } catch (Exception e) {
                                    moreOutput.setText(
                                        getString(R.string.exception_occurred)
                                            + e.toString(),
                                        BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                }
                                break;

                            case 8: // Base64 decode
                                try {
                                    String base64Input = moreInput.getText().toString();
                                    String decodedStr =
                                        new String(
                                            Base64.decode(
                                                base64Input.getBytes(),
                                                Base64.DEFAULT));
                                    moreOutput.setText(decodedStr, BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                } catch (Exception e) {
                                    if (e instanceof IllegalArgumentException) {
                                        moreOutput.setText(
                                            getString(R.string.illegal_base64),
                                            BufferType.EDITABLE);
                                    } else {
                                        moreOutput.setText(
                                            getString(R.string.exception_occurred)
                                                + e.toString(),
                                            BufferType.EDITABLE);
                                    }
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                }
                                break;

                            case 9: // morse code
                                try {
                                    String morseCodeInput =
                                        moreInput.getText().toString();
                                    morseCodeInput =
                                        morseCodeInput
                                            .replace(' ', '/')
                                            .replace('\\', '/')
                                            .replace('\n', '/')
                                            .replace('\r', '/')
                                            .replace('*', '.')
                                            .replace('·', '.')
                                            .replace('•', '.')
                                            .replace('●', '.')
                                            .replace('_', '-')
                                            .replace('—', '-');
                                    MorseCoder morseCoder = new MorseCoder();
                                    String decodeStr =
                                        morseCoder.decode(morseCodeInput);
                                    if (settingsBoolean[2]) {
                                        decodeStr = decodeStr.toLowerCase();
                                    }
                                    moreOutput.setText(decodeStr, BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                } catch (Exception e) {
                                    if (e instanceof IllegalArgumentException) {
                                        try {
                                            String morseCodeInput1 =
                                                moreInput.getText().toString();
                                            morseCodeInput1 =
                                                morseCodeInput1.replace('\n', ' ');
                                            MorseCoder morseCoder = new MorseCoder();
                                            moreOutput.setText(
                                                morseCoder.encode(morseCodeInput1),
                                                BufferType.EDITABLE);
                                            moreOutput.clearFocus();
                                            moreInput.clearFocus();
                                        } catch (Exception e1) {
                                            moreOutput.setText(
                                                getString(
                                                    R.string
                                                        .exception_occurred)
                                                    + e1.toString(),
                                                BufferType.EDITABLE);
                                            moreOutput.clearFocus();
                                            moreInput.clearFocus();
                                        }
                                    } else {
                                        moreOutput.setText(
                                            getString(R.string.exception_occurred)
                                                + e.toString(),
                                            BufferType.EDITABLE);
                                    }
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                }
                                break;

                            case 10: // format JSON
                                try {
                                    String uglyJson = moreInput.getText().toString();
                                    String formattedJson = jsonFormatter(uglyJson);
                                    if (!formattedJson.equals("null")) {
                                        moreOutput.setText(
                                            formattedJson, BufferType.EDITABLE);
                                    } else {
                                        moreOutput.setText("", BufferType.EDITABLE);
                                    }
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                } catch (Exception e) {
                                    moreOutput.setText(
                                        getString(R.string.exception_occurred)
                                            + e.toString(),
                                        BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                }
                                break;

                            case 11: // Random Text
                                String textRandomInput = moreInput.getText().toString();
                                final String[] dataQuantityString1 = {"1"};
                                String items[] = textRandomInput.split("\\cJ");
                                LovelyCustomDialog dialog2 =
                                    new LovelyCustomDialog(MainActivity.this);
                                LayoutInflater layoutInflater =
                                    LayoutInflater.from(MainActivity.this);
                                @SuppressLint("InflateParams")
                                View dialogView =
                                    layoutInflater.inflate(
                                        R.layout.dialog_set_quantity, null);
                                final EditText dataQuantity1 =
                                    dialogView.findViewById(R.id.dataQuantity);
                                dialogView
                                    .findViewById(R.id.viewInstance)
                                    .setVisibility(View.GONE);
                                dialog2.setMessage(getString(R.string.test_rand_hint))
                                    .setView(dialogView)
                                    .setCancelable(false)
                                    .setTitle(R.string.text_random)
                                    .setTopColorRes(R.color.mdIndigo)
                                    .setIcon(
                                        R.drawable
                                            .ic_format_list_numbered_white_24dp)
                                    .setListener(
                                        R.id.cancelDialog,
                                        v16 -> dialog2.dismiss())
                                    .setListener(
                                        R.id.confirmDialog,
                                        v17 -> {
                                            try {
                                                dataQuantityString1[0] =
                                                    dataQuantity1
                                                        .getText()
                                                        .toString();
                                                if (dataQuantityString1[0]
                                                    .isEmpty()) {
                                                    dataQuantityString1[0] =
                                                        "1";
                                                }
                                                int quantity =
                                                    Integer.parseInt(
                                                        dataQuantityString1[
                                                            0]);
                                                StringBuilder output =
                                                    new StringBuilder();
                                                Random random = new Random();
                                                for (int i = 0;
                                                    i < quantity;
                                                    i++) {
                                                    int id =
                                                        random.nextInt(
                                                            items.length);
                                                    if (i < quantity - 1) {
                                                        output.append(items[id])
                                                            .append('\n');
                                                    } else {
                                                        output.append(
                                                            items[id]);
                                                    }
                                                }
                                                moreOutput.setText(
                                                    output.toString(),
                                                    BufferType.EDITABLE);
                                            } catch (Exception e) {
                                                moreOutput.setText(
                                                    getString(
                                                        R.string
                                                            .exception_occurred)
                                                        + e.toString(),
                                                    BufferType.EDITABLE);
                                            } finally {
                                                moreInput.clearFocus();
                                                moreOutput.clearFocus();
                                                dialog2.dismiss();
                                            }
                                        })
                                    .create()
                                    .show();

                                break;

                            case 12: // View Markdown
                                int totLen = 0;
                                totLen += replaceInput.getText().toString().length();
                                totLen += replaceOutput.getText().toString().length();
                                totLen += replaceTo.getText().toString().length();
                                totLen += targetSeq.getText().toString().length();
                                totLen += searchInput.getText().toString().length();
                                totLen += searchOutput.getText().toString().length();
                                totLen += searchTarget.getText().toString().length();
                                totLen += shuffleInput.getText().toString().length();
                                totLen += shuffleOutput.getText().toString().length();
                                totLen += encryptInput.getText().toString().length();
                                totLen += encryptOutput.getText().toString().length();
                                totLen += encryptKey.getText().toString().length();
                                totLen += moreInput.getText().toString().length();
                                totLen += moreOutput.getText().toString().length();

                                if (totLen < 250 * 1024) {
                                    String mdText = moreInput.getText().toString();
                                    Intent intent1 =
                                        new Intent(
                                            MainActivity.this,
                                            MarkdownPreviewActivity.class);
                                    intent1.putExtra("markdownText", mdText);
                                    startActivity(intent1);
                                }
                                break;

                            case 13: // UnicodeParse
                                try {
                                    String unicodeCode = moreInput.getText().toString();
                                    String unicodeChar =
                                        UnicodeUtils.unicodeToString(unicodeCode);
                                    moreOutput.setText(
                                        unicodeChar, BufferType.EDITABLE);
                                } catch (Exception e) {
                                    moreOutput.setText(
                                        getString(R.string.exception_occurred)
                                            + e.toString(),
                                        BufferType.EDITABLE);
                                } finally {
                                    moreInput.clearFocus();
                                    moreOutput.clearFocus();
                                }
                                break;

                            case 14: // ToUnicode
                                try {
                                    String unicodeChar = moreInput.getText().toString();
                                    String unicodeCode =
                                        UnicodeUtils.stringToUnicode(unicodeChar);
                                    moreOutput.setText(
                                        unicodeCode, BufferType.EDITABLE);
                                } catch (Exception e) {
                                    moreOutput.setText(
                                        getString(R.string.exception_occurred)
                                            + e.toString(),
                                        BufferType.EDITABLE);
                                } finally {
                                    moreInput.clearFocus();
                                    moreOutput.clearFocus();
                                }
                                break;

                            case 15: // String Similarity
                                String m = moreInput.getText().toString();
                                String n = moreOutput.getText().toString();
                                final String[] outputType = {
                                    getString(R.string.similarity_)
                                };
                                final String[] outputValue = {""};
                                Builder alertDialog = new Builder(MainActivity.this);
                                alertDialog
                                    .setTitle(
                                        getString(
                                            R.string
                                                .choose_similarity_algorithm))
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setItems(
                                        similarityAlgorithms,
                                        (dialog12, which) -> {
                                            outputType[0] =
                                                similarityAlgorithms[which];
                                            switch (which) {
                                                case 0:
                                                    outputValue[0] =
                                                        String.valueOf(
                                                            new NormalizedLevenshtein()
                                                                .distance(
                                                                    m,
                                                                    n));
                                                    break;

                                                case 1:
                                                    outputValue[0] =
                                                        String.valueOf(
                                                            new NormalizedLevenshtein()
                                                                .similarity(
                                                                    m,
                                                                    n));
                                                    break;

                                                case 2:
                                                    outputValue[0] =
                                                        String.valueOf(
                                                            new Damerau()
                                                                .distance(
                                                                    m,
                                                                    n));
                                                    break;

                                                case 3:
                                                    outputValue[0] =
                                                        String.valueOf(
                                                            new JaroWinkler()
                                                                .distance(
                                                                    m,
                                                                    n));
                                                    break;

                                                case 4:
                                                    outputValue[0] =
                                                        String.valueOf(
                                                            new JaroWinkler()
                                                                .similarity(
                                                                    m,
                                                                    n));
                                                    break;

                                                case 5:
                                                    outputValue[0] =
                                                        String.valueOf(
                                                            new LongestCommonSubsequence()
                                                                .distance(
                                                                    m,
                                                                    n));
                                                    break;

                                                case 6:
                                                    outputValue[0] =
                                                        String.valueOf(
                                                            new MetricLCS()
                                                                .distance(
                                                                    m,
                                                                    n));
                                                    break;

                                                case 7:
                                                    outputValue[0] =
                                                        String.valueOf(
                                                            new NGram()
                                                                .distance(
                                                                    m,
                                                                    n));
                                                    break;

                                                case 8:
                                                    outputValue[0] =
                                                        String.valueOf(
                                                            new QGram()
                                                                .distance(
                                                                    m,
                                                                    n));
                                                    break;
                                            }
                                            new LovelyStandardDialog(this)
                                                .setPositiveButtonText(
                                                    R.string.confirm)
                                                .setTitle(outputType[0])
                                                .setMessage(outputValue[0])
                                                .setTopColorRes(
                                                    R.color.mdBrown)
                                                .setIcon(
                                                    R.drawable
                                                        .ic_text_fields_white_48dp)
                                                .create()
                                                .show();
                                        })
                                    .create()
                                    .show();

                                break;

                            case 16: // format java code
                                try {
                                    if (VERSION.SDK_INT < VERSION_CODES.O) {
                                        Toasty.error(
                                            MainActivity.this,
                                            R.string
                                                .sorry_not_supported_8_0,
                                            Toast.LENGTH_LONG)
                                            .show();
                                        break;
                                    }
                                    String formatCodeSrc =
                                        moreInput.getText().toString();

                                    Formatter formatter = new Formatter();
                                    String formattedCode =
                                        formatter.formatSourceAndFixImports(
                                            formatCodeSrc);

                                                /*
                                                Jalopy jalopy = new Jalopy();
                                                jalopy.setEncoding("UTF-8");
                                                SharedPreferences sharedPreferences =
                                                        getSharedPreferences(
                                                                "settings", MODE_PRIVATE);
                                                jalopy.setInput(
                                                        formatCodeSrc,
                                                        Objects.requireNonNull(
                                                                sharedPreferences.getString(
                                                                        "default_path",
                                                                        Environment
                                                                                        .getExternalStorageDirectory()
                                                                                        .getAbsolutePath()
                                                                                + "/TextConverter")));
                                                StringBuffer stringBuffer = new StringBuffer();
                                                jalopy.setOutput(stringBuffer);
                                                String formattedCode = stringBuffer.toString();
                                                */
                                    moreOutput.setText(
                                        formattedCode, BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                } catch (Exception e) {
                                    moreOutput.setText(
                                        getString(R.string.exception_occurred)
                                            + e.toString(),
                                        BufferType.EDITABLE);
                                    moreOutput.clearFocus();
                                    moreInput.clearFocus();
                                }
                                break;

                            case 17: // String Trim
                                String trimInput = moreInput.getText().toString();
                                moreOutput.setText(
                                    trimInput.trim(), BufferType.EDITABLE);
                                break;

                            case 18: // escape Everything
                                String escapeInput = moreInput.getText().toString();
                                final String[] escapeOutput = {""};
                                AlertDialog.Builder builder =
                                    new AlertDialog.Builder(this);
                                builder.setIcon(R.drawable.ic_launcher_round)
                                    .setTitle(getString(R.string.select_operation))
                                    .setItems(
                                        new String[]{
                                            getString(R.string.parse_html),
                                            getString(R.string.unparse_html),
                                            getString(R.string.parse_xml),
                                            getString(R.string.unparse_xml),
                                            getString(R.string.parse_json),
                                            getString(R.string.unparse_json),
                                            getString(R.string.parse_java),
                                            getString(R.string.unparse_java),
                                            getString(R.string.parse_csv),
                                            getString(R.string.unparse_csv),
                                            getString(R.string.parse_ecma),
                                            getString(R.string.unparse_ecma)
                                        },
                                        (dialog13, which) -> {
                                            try {
                                                switch (which) {
                                                    case 0:
                                                        escapeOutput[0] =
                                                            StringEscapeUtils
                                                                .escapeHtml4(
                                                                    escapeInput);
                                                        break;
                                                    case 1:
                                                        escapeOutput[0] =
                                                            StringEscapeUtils
                                                                .unescapeHtml4(
                                                                    escapeInput);
                                                        break;
                                                    case 2:
                                                        escapeOutput[0] =
                                                            StringEscapeUtils
                                                                .escapeXml11(
                                                                    escapeInput);
                                                        break;

                                                    case 3:
                                                        escapeOutput[0] =
                                                            StringEscapeUtils
                                                                .unescapeXml(
                                                                    escapeInput);
                                                        break;
                                                    case 4:
                                                        escapeOutput[0] =
                                                            StringEscapeUtils
                                                                .escapeJson(
                                                                    escapeInput);
                                                        break;
                                                    case 5:
                                                        escapeOutput[0] =
                                                            StringEscapeUtils
                                                                .unescapeJson(
                                                                    escapeInput);
                                                        break;
                                                    case 6:
                                                        escapeOutput[0] =
                                                            StringEscapeUtils
                                                                .escapeJava(
                                                                    escapeInput);
                                                        break;
                                                    case 7:
                                                        escapeOutput[0] =
                                                            StringEscapeUtils
                                                                .unescapeJava(
                                                                    escapeInput);
                                                        break;
                                                    case 8:
                                                        escapeOutput[0] =
                                                            StringEscapeUtils
                                                                .escapeCsv(
                                                                    escapeInput);
                                                        break;
                                                    case 9:
                                                        escapeOutput[0] =
                                                            StringEscapeUtils
                                                                .unescapeCsv(
                                                                    escapeInput);
                                                        break;
                                                    case 10:
                                                        escapeOutput[0] =
                                                            StringEscapeUtils
                                                                .escapeEcmaScript(
                                                                    escapeInput);
                                                        break;
                                                    case 11:
                                                        escapeOutput[0] =
                                                            StringEscapeUtils
                                                                .unescapeEcmaScript(
                                                                    escapeInput);
                                                        break;
                                                }
                                                moreOutput.setText(
                                                    escapeOutput[0],
                                                    BufferType.EDITABLE);
                                            } catch (Exception e) {
                                                moreOutput.setText(
                                                    getString(
                                                        R.string
                                                            .exception_occurred)
                                                        + e.toString(),
                                                    BufferType.EDITABLE);
                                            } finally {
                                                moreInput.clearFocus();
                                                moreOutput.clearFocus();
                                            }
                                        })
                                    .create()
                                    .show();
                                break;

                            case 19: // parse Regex
                                try {
                                    String regexParseInput =
                                        moreInput.getText().toString();
                                    for (String key : fbsArr) {
                                        if (regexParseInput.contains(key)) {
                                            regexParseInput =
                                                regexParseInput.replace(
                                                    key, "\\" + key);
                                        }
                                    }
                                    moreOutput.setText(
                                        regexParseInput, BufferType.EDITABLE);
                                } catch (Exception e) {
                                    moreOutput.setText(
                                        getString(R.string.exception_occurred)
                                            + e.toString(),
                                        BufferType.EDITABLE);
                                } finally {
                                    moreInput.clearFocus();
                                    moreOutput.clearFocus();
                                }
                                break;
                            case 20: // Random string
                                LovelyCustomDialog randStrDialog =
                                    new LovelyCustomDialog(this);
                                randStrDialog
                                    .setView(R.layout.random_string_dialog)
                                    .setIcon(R.drawable.ic_shuffle_white_48dp)
                                    .setTopColorRes(R.color.mdPurple)
                                    .setTitle(getString(R.string.gen_rand_str))
                                    .configureView(
                                        v -> {
                                            EditText startPointBox =
                                                v.findViewById(
                                                    R.id.startPoint);
                                            EditText endPointBox =
                                                v.findViewById(
                                                    R.id.endPoint);
                                            EditText minLengthBox =
                                                v.findViewById(
                                                    R.id.minLength);
                                            EditText maxLengthBox =
                                                v.findViewById(
                                                    R.id.maxLength);
                                            EditText randQuantityBox =
                                                v.findViewById(
                                                    R.id.randTextQuantity);
                                            Button confirm =
                                                v.findViewById(
                                                    R.id.confirmTextRand);
                                            Button dismiss =
                                                v.findViewById(
                                                    R.id.cancelTextRand);
                                            startPointBox.setText(
                                                "0", BufferType.EDITABLE);
                                            endPointBox.setText(
                                                "z", BufferType.EDITABLE);
                                            minLengthBox.setText(
                                                "6", BufferType.EDITABLE);
                                            maxLengthBox.setText(
                                                "18", BufferType.EDITABLE);
                                            confirm.setOnClickListener(
                                                v18 -> {
                                                    try {
                                                        if (startPointBox
                                                            .getText()
                                                            .toString()
                                                            .isEmpty()
                                                            || endPointBox
                                                            .getText()
                                                            .toString()
                                                            .isEmpty()) {
                                                            throw new NumberFormatException(
                                                                "Empty Input");
                                                        }
                                                        char startPoint =
                                                            startPointBox
                                                                .getText()
                                                                .toString()
                                                                .charAt(
                                                                    0);
                                                        char endPoint =
                                                            endPointBox
                                                                .getText()
                                                                .toString()
                                                                .charAt(
                                                                    0);
                                                        int minLength =
                                                            Integer
                                                                .parseInt(
                                                                    minLengthBox
                                                                        .getText()
                                                                        .toString());
                                                        int maxLength =
                                                            Integer
                                                                .parseInt(
                                                                    maxLengthBox
                                                                        .getText()
                                                                        .toString());
                                                        int randQuantity =
                                                            Integer
                                                                .parseInt(
                                                                    randQuantityBox
                                                                        .getText()
                                                                        .toString()
                                                                        .isEmpty()
                                                                        ? "1"
                                                                        : randQuantityBox
                                                                            .getText()
                                                                            .toString());
                                                        if (endPoint
                                                            < startPoint) {
                                                            throw new IllegalArgumentException(
                                                                getString(
                                                                    R.string
                                                                        .codepoint_mismatch));
                                                        }
                                                        if (maxLength
                                                            < minLength) {
                                                            throw new IllegalArgumentException(
                                                                getString(
                                                                    R.string
                                                                        .length_negative));
                                                        }
                                                        RandomStringGenerator
                                                            generator =
                                                            new RandomStringGenerator
                                                                .Builder()
                                                                .withinRange(
                                                                    startPoint,
                                                                    endPoint)
                                                                .build();
                                                        Random random =
                                                            new Random();
                                                        StringBuilder
                                                            output =
                                                            new StringBuilder();
                                                        for (int i = 0;
                                                            i
                                                                < randQuantity;
                                                            i++) {
                                                            int length =
                                                                random
                                                                    .nextInt(
                                                                        maxLength
                                                                            - minLength
                                                                            + 1)
                                                                    + minLength;
                                                            String randStr =
                                                                generator
                                                                    .generate(
                                                                        length);
                                                            output.append(
                                                                randStr);
                                                            if (i
                                                                < randQuantity
                                                                - 1) {
                                                                output
                                                                    .append(
                                                                        '\n');
                                                            }
                                                        }
                                                        moreOutput.setText(
                                                            output
                                                                .toString(),
                                                            BufferType
                                                                .EDITABLE);
                                                    } catch (
                                                        NumberFormatException
                                                            e) {
                                                        Toasty.error(
                                                            MainActivity
                                                                .this,
                                                            R.string
                                                                .format_err_or_empty,
                                                            Toast
                                                                .LENGTH_LONG)
                                                            .show();
                                                    } catch (Exception e) {
                                                        moreOutput.setText(
                                                            getString(
                                                                R.string
                                                                    .exception_occurred)
                                                                + e
                                                                .toString(),
                                                            BufferType
                                                                .EDITABLE);
                                                    } finally {
                                                        moreInput
                                                            .clearFocus();
                                                        moreOutput
                                                            .clearFocus();
                                                        randStrDialog
                                                            .dismiss();
                                                    }
                                                });
                                            dismiss.setOnClickListener(
                                                v19 -> {
                                                    moreInput.clearFocus();
                                                    moreOutput.clearFocus();
                                                    randStrDialog.dismiss();
                                                });
                                        })
                                    .create()
                                    .show();
                                break;

                            case 21: // To hanyu pinyin
                                String hanyu = moreInput.getText().toString();
                                Builder pinyinDialog = new Builder(MainActivity.this);
                                StringBuilder pinyin = new StringBuilder();

                                pinyinDialog
                                    .setTitle(getString(R.string.select_mode))
                                    .setIcon(R.mipmap.ic_launcher)
                                    .setItems(
                                        pinyinModes,
                                        (dialog14, which) -> {
                                            HanyuPinyinOutputFormat format =
                                                new HanyuPinyinOutputFormat();
                                            switch (which) {
                                                case 0: // WITHOUT_TONE
                                                    format.setToneType(
                                                        HanyuPinyinToneType
                                                            .WITHOUT_TONE);
                                                    format.setVCharType(
                                                        HanyuPinyinVCharType
                                                            .WITH_V);
                                                    break;
                                                case 1: // WITH_TONE_NUMBER
                                                    format.setToneType(
                                                        HanyuPinyinToneType
                                                            .WITH_TONE_NUMBER);
                                                    format.setVCharType(
                                                        HanyuPinyinVCharType
                                                            .WITH_V);
                                                    break;
                                                case 2: // WITH_TONE_MARK
                                                    format.setToneType(
                                                        HanyuPinyinToneType
                                                            .WITH_TONE_MARK);
                                                    format.setVCharType(
                                                        HanyuPinyinVCharType
                                                            .WITH_U_UNICODE);
                                                    break;
                                            }
                                            try {
                                                for (int i = 0;
                                                    i < hanyu.length();
                                                    i++) {
                                                    if (hanyu.charAt(i) > 127) {
                                                        if (pinyin.length() > 0
                                                            && pinyin
                                                            .charAt(
                                                                pinyin
                                                                    .length()
                                                                    - 1)
                                                            != ' ') {
                                                            pinyin.append(' ');
                                                        }
                                                        String[] pinyinArray =
                                                            PinyinHelper
                                                                .toHanyuPinyinStringArray(
                                                                    hanyu
                                                                        .charAt(
                                                                            i),
                                                                    format);
                                                        if (pinyinArray
                                                            != null) {
                                                            pinyin.append(
                                                                pinyinArray[
                                                                    0])
                                                                .append(
                                                                    ' ');
                                                        }

                                                    } else {
                                                        pinyin.append(
                                                            hanyu.charAt(
                                                                i));
                                                    }
                                                }
                                                moreOutput.setText(
                                                    pinyin,
                                                    BufferType.EDITABLE);

                                            } catch (
                                                BadHanyuPinyinOutputFormatCombination
                                                    e) {
                                                moreOutput.setText(
                                                    getString(
                                                        R.string
                                                            .exception_occurred)
                                                        + e.toString(),
                                                    BufferType.EDITABLE);
                                            } finally {
                                                moreInput.clearFocus();
                                                moreOutput.clearFocus();
                                            }
                                        })
                                    .create()
                                    .show();
                                break;

                            default:
                                break;
                        }
                        dialog.dismiss();
                    })
                .create();
        dialogPlus.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            Toasty.info(MainActivity.this, R.string.press_more_to_exit, Toast.LENGTH_SHORT).show();
            clickTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    private void setRegex(String regex) {
        switch (getCurrentShowingLayoutId()) {
            case R.id.textReplaceLayout:
                targetSeq.setText(regex, BufferType.EDITABLE);
                doUseRegexCheckbox.setChecked(true);
                Toasty.success(MainActivity.this, R.string.load_success, Toast.LENGTH_SHORT, true)
                    .show();
                break;

            case R.id.textShuffleLayout:
                shuffleInput.setText(regex, BufferType.EDITABLE);
                Toasty.warning(MainActivity.this, R.string.loaded_to_input, Toast.LENGTH_LONG, true)
                    .show();
                break;

            case R.id.textSearchLayout:
                searchTarget.setText(regex, BufferType.EDITABLE);
                doUseRegexSearchCheckbox.setChecked(true);
                Toasty.success(MainActivity.this, R.string.load_success, Toast.LENGTH_SHORT, true)
                    .show();
                break;

            case R.id.textEncryptLayout:
                encryptKey.setText(regex, BufferType.EDITABLE);
                Toasty.success(
                    MainActivity.this,
                    R.string.loaded_as_password,
                    Toast.LENGTH_SHORT,
                    true)
                    .show();
                break;

            case R.id.textMoreLayout:
                moreInput.setText(regex, BufferType.EDITABLE);
                Toasty.warning(MainActivity.this, R.string.loaded_to_input, Toast.LENGTH_LONG, true)
                    .show();
                break;

            default:
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.searchReset:
                ViewTooltip.on(MainActivity.this, mainContext, v)
                    .autoHide(true, 2000)
                    .corner(30)
                    .color(R.color.colorAccentAlpha)
                    .withShadow(true)
                    .position(Position.TOP)
                    .text(R.string.reset_search_result)
                    .show();
                break;
            case R.id.searchAll:
                ViewTooltip.on(MainActivity.this, mainContext, v)
                    .corner(30)
                    .autoHide(true, 2000)
                    .color(R.color.colorAccentAlpha)
                    .withShadow(true)
                    .position(Position.TOP)
                    .text(R.string.search_all)
                    .show();
                break;
            case R.id.searchPrevious:
                ViewTooltip.on(MainActivity.this, mainContext, v)
                    .corner(30)
                    .autoHide(true, 2000)
                    .color(R.color.colorAccentAlpha)
                    .withShadow(true)
                    .position(Position.TOP)
                    .text(R.string.search_previous)
                    .show();
                break;
            case R.id.searchNext:
                ViewTooltip.on(MainActivity.this, mainContext, v)
                    .corner(30)
                    .autoHide(true, 2000)
                    .color(R.color.colorAccentAlpha)
                    .withShadow(true)
                    .position(Position.TOP)
                    .text(R.string.search_next)
                    .show();
                break;
        }
        return true;
    }

    private int getCheckedDrawerItemId() throws Exception {
        switch (getCurrentShowingLayoutId()) {
            case R.id.textReplaceLayout:
                return R.id.nav_text_replace;
            case R.id.textSearchLayout:
                return R.id.nav_text_search;
            case R.id.textShuffleLayout:
                return R.id.nav_text_shuffle;
            case R.id.textEncryptLayout:
                return R.id.nav_text_encrypt;
            case R.id.textMoreLayout:
                return R.id.nav_more_functions;
            default:
                throw new Exception("Unknown Exception");
        }
    }
}
