//This app is developed by TCreopargh.
//Site: https://tcreopargh.xyz
//including libraries:
//com.google.googlejavaformat
//pinyin4j
//AESUtils
//LfilePicker
//com.takwolf:morse-coder
//Toasty

package xyz.tcreopargh.textconverter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.googlejavaformat.java.Formatter;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.takwolf.morsecoder.MorseCoder;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    Button generateReplacedText;
    EditText replaceInput, replaceOutput, targetSeq, replaceTo;
    CheckBox doUseRegexCheckbox;
    LinearLayout textReplaceLayout, textShuffleLayout, textSearchLayout, textEncryptLayout, textMoreLayout;

    Button shuffle, sortByDictionaryIndex, sortByNumberValue, shuffleReverse;
    EditText shuffleInput;
    EditText shuffleOutput;
    CheckBox noUseSpaces;

    Button searchReset, searchNext, searchAll;
    EditText searchInput, searchOutput, searchTarget;
    CheckBox doUseRegexSearchCheckbox;

    Button encrypt, decrypt;
    EditText encryptInput, encryptOutput, encryptKey;
    CheckBox doPasswordVisible;

    EditText moreInput, moreOutput;
    Button reverseText, addIndent, formatCode, addNumbers, customRandom, generateMD5, toBase64, fromBase64, toMorseCode, fromMorseCode;

    //EditText dataQuantity;

    int currentSearchPos = 0;
    int searchCount = -1;
    int currentSearchCount = -1;
    int begin = 0;

    Pattern pattern;
    Matcher matcher;

    final int ALL_LOWER = 0;
    final int ALL_UPPER = 1;
    final int CASE_REVERSE = 2;
    final int FIRST_UPPER = 3;
    final int SENTENCE_FIRST = 4;
    int caseSwitchStatus = ALL_LOWER;

    boolean regexCautionIsShown = false;
    //View settingsView;

    String generatedKey = "";

    boolean keyGenNeedToReset = true;

    String path = "";

    final int REQUESTCODE_READ = 1000;
    final int REQUESTCODE_WRITE = 2000;

    final boolean doCapsSensitive[] = new boolean[]{false, false};

    final String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};

    final String presetsTitle[] = new String[]{
            "十六进制数值",
            "电子邮箱",
            "URL",
            "IP地址",
            "整数",
            "常规数字",
            "HTML标签",
            "维基百科注释",
            "代码注释",
            "汉字"
    };
    final String presetsValue[] = new String[]{
            "#?([a-f0-9]{6}|[a-f0-9]{3})",
            "[a-z\\d]+(\\.[a-z\\d]+)*@([\\da-z](-[\\da-z])?)+(\\.{1,2}[a-z]+)+",
            "(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?",
            "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)",
            "-?\\d+",
            "(-?\\d+)(\\.\\d+)?",
            "<([a-z]+)([^<]+)*(?:>(.*)<\\/\\1>|\\s+\\/>)",
            "\\[\\d+\\]",
            "(?<!http:|\\S)//.*",
            "[\\u2E80-\\u9FFF]+"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
        setTitle(R.string.string_replace);

        //Floating button, may be used later
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        doCapsSensitive[0] = sharedPreferences.getBoolean("doCapsSensitive", false);
        doCapsSensitive[1] = sharedPreferences.getBoolean("doUseMonospaced", false);
        if (doCapsSensitive[1]) {
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

        doUseRegexSearchCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                resetSearch();
                if (isChecked && !regexCautionIsShown) {
                    regexCautionIsShown = true;
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle(R.string.caution)
                            .setMessage(R.string.regex_info)
                            .setPositiveButton(R.string.i_know, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setNeutralButton(R.string.view_help, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(MainActivity.this, ViewHelpActivity.class);
                            startActivity(intent);
                        }
                    }).create().show();
                }
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                resetSearch();
            }
        });
        searchTarget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                resetSearch();
            }
        });
        encryptKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                keyGenNeedToReset = true;
            }
        });
        doUseRegexCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && !regexCautionIsShown) {
                    regexCautionIsShown = true;
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle(R.string.caution)
                            .setMessage(R.string.regex_info)
                            .setPositiveButton(R.string.i_know, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setNeutralButton(R.string.view_help, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, ViewHelpActivity.class);
                            startActivity(intent);
                        }
                    }).create().show();
                }
            }
        });
        doPasswordVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    encryptKey.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    encryptKey.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
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
        int currentShowingLayout = getCurrentShowingLayoutId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_copy_to_clipboard) {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Error", "");
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
            if (clip.length() > 512000) {
                Toasty.error(MainActivity.this, "内容过长，无法复制到剪贴板！", Toast.LENGTH_LONG, true).show();
            } else {
                mClipData = ClipData.newPlainText("TextConverter", clip);
                clipboardManager.setPrimaryClip(mClipData);
                Toasty.success(MainActivity.this, "复制成功！", Toast.LENGTH_LONG, true).show();
            }
        } else if (id == R.id.action_reverse_io) {
            switch (currentShowingLayout) {
                case R.id.textReplaceLayout:
                    replaceInput.setText(replaceOutput.getText().toString(), TextView.BufferType.EDITABLE);
                    break;
                case R.id.textShuffleLayout:
                    shuffleInput.setText(shuffleOutput.getText().toString(), TextView.BufferType.EDITABLE);
                    break;
                case R.id.textSearchLayout:
                    searchInput.setText(searchOutput.getText().toString(), TextView.BufferType.EDITABLE);
                    break;
                case R.id.textEncryptLayout:
                    encryptInput.setText(encryptOutput.getText().toString(), TextView.BufferType.EDITABLE);
                    break;
                case R.id.textMoreLayout:
                    moreInput.setText(moreOutput.getText().toString(), TextView.BufferType.EDITABLE);
                    break;
                default:
            }
        } else if (id == R.id.action_clear_all) {
            DrawerLayout container = findViewById(R.id.drawer_layout);
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
                    Snackbar.make(container, R.string.cleared, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    replaceInput.setText(bufferReplaceInput, TextView.BufferType.EDITABLE);
                                    replaceOutput.setText(bufferReplaceOutput, TextView.BufferType.EDITABLE);
                                    replaceTo.setText(bufferReplaceTo, TextView.BufferType.EDITABLE);
                                    targetSeq.setText(bufferTargetSeq, TextView.BufferType.EDITABLE);
                                    doUseRegexCheckbox.setChecked(bufferDoUseRegex);
                                }
                            }).show();
                    break;
                case R.id.textShuffleLayout:
                    final String bufferShuffleInput = shuffleInput.getText().toString();
                    final String bufferShuffleOutput = shuffleOutput.getText().toString();
                    final boolean bufferNoUseSpaces = noUseSpaces.isChecked();
                    shuffleInput.setText("");
                    shuffleOutput.setText("");
                    noUseSpaces.setChecked(false);
                    Snackbar.make(container, R.string.cleared, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    shuffleInput.setText(bufferShuffleInput, TextView.BufferType.EDITABLE);
                                    shuffleOutput.setText(bufferShuffleOutput, TextView.BufferType.EDITABLE);
                                    noUseSpaces.setChecked(bufferNoUseSpaces);
                                }
                            }).show();
                    break;
                case R.id.textSearchLayout:
                    final String bufferSearchInput = searchInput.getText().toString();
                    final String bufferSearchOutput = searchOutput.getText().toString();
                    final String bufferSearchTarget = searchTarget.getText().toString();
                    final boolean bufferDoUseCheckboxInSearch = doUseRegexSearchCheckbox.isChecked();
                    searchInput.setText("");
                    searchOutput.setText("");
                    searchTarget.setText("");
                    doUseRegexSearchCheckbox.setChecked(false);
                    resetSearch();
                    Snackbar.make(container, R.string.cleared, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    searchInput.setText(bufferSearchInput, TextView.BufferType.EDITABLE);
                                    searchOutput.setText(bufferSearchOutput, TextView.BufferType.EDITABLE);
                                    searchTarget.setText(bufferSearchTarget, TextView.BufferType.EDITABLE);
                                    doUseRegexSearchCheckbox.setChecked(bufferDoUseCheckboxInSearch);
                                }
                            }).show();
                    break;
                case R.id.textEncryptLayout:
                    final String bufferEncryptInput = encryptInput.getText().toString();
                    final String bufferEncryptOutput = encryptOutput.getText().toString();
                    final String bufferEncryptKey = encryptKey.getText().toString();
                    final boolean bufferDoPasswordVisible = doPasswordVisible.isChecked();
                    encryptInput.setText("");
                    encryptOutput.setText("");
                    encryptKey.setText("");
                    doPasswordVisible.setChecked(false);
                    Snackbar.make(container, R.string.cleared, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    encryptInput.setText(bufferEncryptInput, TextView.BufferType.EDITABLE);
                                    encryptOutput.setText(bufferEncryptOutput, TextView.BufferType.EDITABLE);
                                    encryptKey.setText(bufferEncryptKey, TextView.BufferType.EDITABLE);
                                    doPasswordVisible.setChecked(bufferDoPasswordVisible);
                                }
                            }).show();
                    break;
                case R.id.textMoreLayout:
                    final String bufferMoreInput = moreInput.getText().toString();
                    final String bufferMoreOutput = moreOutput.getText().toString();
                    moreOutput.setText("");
                    moreInput.setText("");
                    Snackbar.make(container, R.string.cleared, Snackbar.LENGTH_LONG)
                            .setAction(R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    moreInput.setText(bufferMoreInput, TextView.BufferType.EDITABLE);
                                    moreOutput.setText(bufferMoreOutput, TextView.BufferType.EDITABLE);
                                }
                            }).show();
                    break;
                default:
            }
        } else if (id == R.id.load_presets) {
            final String[] preset = {""};
            final int currentShowingLayoutFinal = currentShowingLayout;
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("选择预设值")
                    .setIcon(R.mipmap.ic_launcher)
                    .setItems(presetsTitle, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            preset[0] = presetsValue[which];
                            switch (currentShowingLayoutFinal) {
                                case R.id.textReplaceLayout:
                                    targetSeq.setText(preset[0]);
                                    doUseRegexCheckbox.setChecked(true);
                                    break;
                                case R.id.textShuffleLayout:
                                    Toasty.warning(MainActivity.this, "当前界面不需要正则表达式！", Toast.LENGTH_LONG, true).show();
                                    break;
                                case R.id.textSearchLayout:
                                    searchTarget.setText(preset[0]);
                                    doUseRegexSearchCheckbox.setChecked(true);
                                    break;
                                case R.id.textEncryptLayout:
                                    Toasty.warning(MainActivity.this, "当前界面不需要正则表达式！", Toast.LENGTH_LONG, true).show();
                                    break;
                                case R.id.textMoreLayout:
                                    Toasty.warning(MainActivity.this, "当前界面不需要正则表达式！", Toast.LENGTH_LONG, true).show();
                                    break;
                                default:
                            }

                        }
                    }).create().show();
        } else if (id == R.id.action_read_file) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                getFile();
            }
        } else if (id == R.id.action_write_file) {
            String outputString = "";
            boolean doUseFilePicker = true;
            if(replaceInput.getText().toString().length()>500*1024
                    ||shuffleInput.getText().toString().length()>500*1024
                    ||searchInput.getText().toString().length()>500*1024
                    ||moreInput.getText().toString().length()>500*1024
                    ||encryptInput.getText().toString().length()>500*1024) {
                doUseFilePicker = false;
            }
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
            if (outputString.length() < 500 * 1024 && doUseFilePicker) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    getStoreLocation();
                }
            } else {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                } else {
                    storeDirectly();
                }
            }
        }
        return super.onOptionsItemSelected(item);
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
            setTitle(R.string.string_replace);
            // Handle the camera action
        } else if (id == R.id.nav_text_shuffle) {
            textShuffleLayout.setVisibility(View.VISIBLE);
            textReplaceLayout.setVisibility(View.GONE);
            textSearchLayout.setVisibility(View.GONE);
            textEncryptLayout.setVisibility(View.GONE);
            textMoreLayout.setVisibility(View.GONE);
            setTitle(R.string.string_shuffle_sort);
        } else if (id == R.id.nav_text_search) {
            textSearchLayout.setVisibility(View.VISIBLE);
            textShuffleLayout.setVisibility(View.GONE);
            textReplaceLayout.setVisibility(View.GONE);
            textEncryptLayout.setVisibility(View.GONE);
            textMoreLayout.setVisibility(View.GONE);
            setTitle(R.string.text_search);
        } else if (id == R.id.nav_text_encrypt) {
            textEncryptLayout.setVisibility(View.VISIBLE);
            textSearchLayout.setVisibility(View.GONE);
            textShuffleLayout.setVisibility(View.GONE);
            textReplaceLayout.setVisibility(View.GONE);
            textMoreLayout.setVisibility(View.GONE);
            setTitle(R.string.text_encrypt);
        } else if (id == R.id.nav_more_functions) {
            textMoreLayout.setVisibility(View.VISIBLE);
            textEncryptLayout.setVisibility(View.GONE);
            textSearchLayout.setVisibility(View.GONE);
            textShuffleLayout.setVisibility(View.GONE);
            textReplaceLayout.setVisibility(View.GONE);
            setTitle(R.string.more_handy_function);
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain")
                    .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.action_share))
                    .putExtra(Intent.EXTRA_TEXT, "我正在使用“文本转换”工具，你也来试试看吧！点击查看:https://blog.tcreopargh.xyz/archives/425")
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, getString(R.string.action_share)));
        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(MainActivity.this, ViewHelpActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.settings)
                    .setIcon(R.drawable.baselinesettings)
                    //.setMessage("仅在不使用正则表达式时有效")
                    .setMultiChoiceItems(new String[]{"区分大小写", "设置输入框为等宽字体"}, doCapsSensitive, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            try {
                                if (which == 0) {
                                    doCapsSensitive[which] = isChecked;
                                    Toasty.success(MainActivity.this, "设置成功，此项设置仅在不使用正则表达式时有效！", Toast.LENGTH_SHORT, true).show();
                                } else if (which == 1) {
                                    if (isChecked) {
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
                                        doCapsSensitive[which] = true;
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
                                        doCapsSensitive[which] = false;
                                    }
                                    Toasty.success(MainActivity.this, "设置成功！", Toast.LENGTH_LONG, true).show();
                                }
                                SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("doCapsSensitive", doCapsSensitive[0]);
                                editor.putBoolean("doUseMonospaced", doCapsSensitive[1]);
                                editor.apply();
                            } catch (Exception e) {
                                Toasty.error(MainActivity.this, "设置失败！错误信息：" + e.toString(), Toast.LENGTH_SHORT, true).show();
                            }
                        }
                    })
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
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
        searchNext = findViewById(R.id.searchNext);
        searchTarget = findViewById(R.id.searchTargetText);
        doUseRegexSearchCheckbox = findViewById(R.id.doUseRegexInSearch);
        searchAll = findViewById(R.id.searchAll);

        textEncryptLayout = findViewById(R.id.textEncryptLayout);
        encryptOutput = findViewById(R.id.encryptOutput);
        encryptInput = findViewById(R.id.encryptInput);
        encryptKey = findViewById(R.id.encryptKey);
        encrypt = findViewById(R.id.encrypt);
        doPasswordVisible = findViewById(R.id.doPasswordVisible);
        decrypt = findViewById(R.id.decrypt);

        textMoreLayout = findViewById(R.id.textMoreLayout);
        moreInput = findViewById(R.id.moreInput);
        moreOutput = findViewById(R.id.moreOutput);
        reverseText = findViewById(R.id.textReverse);
        addIndent = findViewById(R.id.upperLowerCase);
        addNumbers = findViewById(R.id.addNumbers);
        formatCode = findViewById(R.id.formatCode);
        customRandom = findViewById(R.id.customRandom);
        generateMD5 = findViewById(R.id.toMD5);
        toBase64 = findViewById(R.id.toBase64);
        fromBase64 = findViewById(R.id.fromBase64);
        toMorseCode = findViewById(R.id.toMorseCode);
        fromMorseCode = findViewById(R.id.fromMorseCode);

        generateReplacedText.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        shuffleReverse.setOnClickListener(this);
        sortByNumberValue.setOnClickListener(this);
        sortByDictionaryIndex.setOnClickListener(this);
        searchNext.setOnClickListener(this);
        searchReset.setOnClickListener(this);
        searchAll.setOnClickListener(this);
        encrypt.setOnClickListener(this);
        decrypt.setOnClickListener(this);
        reverseText.setOnClickListener(this);
        addIndent.setOnClickListener(this);
        addNumbers.setOnClickListener(this);
        formatCode.setOnClickListener(this);
        customRandom.setOnClickListener(this);
        generateMD5.setOnClickListener(this);
        toBase64.setOnClickListener(this);
        fromBase64.setOnClickListener(this);
        toMorseCode.setOnClickListener(this);
        fromMorseCode.setOnClickListener(this);

        //settingsView=View.inflate(this,R.layout.settings_layout,null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.generateReplacedText:
                try {
                    String textReplaceInput = replaceInput.getText().toString();
                    boolean doUseRegex = doUseRegexCheckbox.isChecked();
                    String textReplaceOutput;
                    String replaceFromStr = targetSeq.getText().toString();
                    String replaceToStr = replaceTo.getText().toString();
                    if (doUseRegex) {
                        textReplaceOutput = textReplaceInput.replaceAll(replaceFromStr, replaceToStr);
                    } else {
                        if (doCapsSensitive[0]) {
                            textReplaceOutput = textReplaceInput.replace(replaceFromStr, replaceToStr);
                        } else {
                            for (String key : fbsArr) {
                                if (replaceFromStr.contains(key)) {
                                    replaceFromStr = replaceFromStr.replace(key, "\\" + key);
                                }
                            }
                            textReplaceOutput = textReplaceInput.replaceAll("(?i)" + replaceFromStr, replaceToStr);
                        }
                    }
                    replaceOutput.setText(textReplaceOutput, TextView.BufferType.EDITABLE);
                    replaceInput.clearFocus();
                    replaceOutput.clearFocus();
                    replaceTo.clearFocus();
                    targetSeq.clearFocus();
                } catch (Exception e) {
                    replaceOutput.setText(e.toString(), TextView.BufferType.EDITABLE);
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
                    for (int i = 0; i < elementCount; i++) {
                        Random random = new Random();
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
                    shuffleOutput.setText(outputBuilder.toString(), TextView.BufferType.EDITABLE);
                    shuffleInput.clearFocus();
                    shuffleOutput.clearFocus();
                } catch (Exception e) {
                    shuffleOutput.setText(getString(R.string.exception_occured) + e.toString());
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
                    shuffleOutput.setText(getString(R.string.exception_occured) + e.toString());
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
                    DecimalFormat decimalFormat = new DecimalFormat("###################.###########");
                    for (int i = 0; i < newElementCount; i++) {
                        if (i < newElementCount - 1) {
                            outputBuilder.append(decimalFormat.format(elementNumbers[i])).append(spiltWithChar);
                        } else {
                            outputBuilder.append(decimalFormat.format(elementNumbers[i]));
                        }
                    }
                    shuffleOutput.setText(outputBuilder.toString());
                    shuffleInput.clearFocus();
                    shuffleOutput.clearFocus();
                } catch (Exception e) {
                    if (e instanceof NumberFormatException) {
                        shuffleOutput.setText("发生错误：所有元素必须是数字！" + e.toString());
                    } else {
                        shuffleOutput.setText(getString(R.string.exception_occured) + e.toString());
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
                    shuffleOutput.setText(getString(R.string.exception_occured) + e.toString());
                    shuffleInput.clearFocus();
                    shuffleOutput.clearFocus();
                }
                break;
            case R.id.searchNext:
                try {
                    String findTarget = searchTarget.getText().toString();
                    String findSrc = searchInput.getText().toString();
                    if (findTarget.isEmpty() || findSrc.isEmpty()) {
                        Toasty.error(MainActivity.this, "查找内容不能为空！", Toast.LENGTH_LONG, true).show();
                        return;
                    }
                    boolean doUseRegexInSearch = doUseRegexSearchCheckbox.isChecked();
                    if (!doUseRegexInSearch) {
                        if (!doCapsSensitive[0]) {
                            findTarget = findTarget.toLowerCase();
                            findSrc = findSrc.toLowerCase();
                        }
                        if (searchCount == -1) {
                            searchCount = stringAppearCounter(findSrc, findTarget);
                            currentSearchCount = 0;
                            currentSearchPos = 0;
                        }
                        if (searchCount == 0) {
                            searchOutput.setText("未查找到目标！", TextView.BufferType.EDITABLE);
                        } else {
                            currentSearchPos = findSrc.indexOf(findTarget, currentSearchPos);
                            if (currentSearchPos == -1) {
                                currentSearchCount = 0;
                                resetSearch();
                                onClick(findViewById(R.id.searchNext));
                            } else {
                                currentSearchCount++;
                                searchInput.requestFocus();
                                searchInput.setSelection(currentSearchPos, currentSearchPos + findTarget.length());
                                searchOutput.setText("目标共出现" + searchCount + "处，正在选中第" + currentSearchCount + "处");
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
                            searchOutput.setText("未查找到目标！", TextView.BufferType.EDITABLE);
                        } else {
                            currentSearchCount++;
                            if (matcher.find()) {
                                String group = matcher.group();
                                begin = matcher.start();
                                currentSearchPos = matcher.end();
                                searchInput.requestFocus();
                                searchInput.setSelection(begin, currentSearchPos);
                                searchOutput.setText("目标共出现" + searchCount + "处，正在选中第" + currentSearchCount + "处\n匹配到的内容为：" + group);
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
                    searchOutput.setText(getString(R.string.exception_occured) + e.toString());
                }
                break;
            case R.id.searchReset:
                try {
                    resetSearch();
                    searchOutput.setText("");
                } catch (Exception e) {
                    searchOutput.setText(getString(R.string.exception_occured) + e.toString());
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
                        Toasty.error(MainActivity.this, "查找内容不能为空！", Toast.LENGTH_LONG, true).show();
                        return;
                    }
                    boolean doUseRegexInSearch = doUseRegexSearchCheckbox.isChecked();
                    if (!doUseRegexInSearch) {
                        String tempFindSrc = findSrc;
                        if (!doCapsSensitive[0]) {
                            findTarget = findTarget.toLowerCase();
                            findSrc = findSrc.toLowerCase();
                        }
                        if (searchCount == -1) {
                            searchCount = stringAppearCounter(findSrc, findTarget);
                            currentSearchCount = 0;
                            currentSearchPos = 0;
                        }
                        if (searchCount == 0) {
                            searchOutput.setText("未查找到目标！", TextView.BufferType.EDITABLE);
                        } else {
                            for (int i = 0; i < searchCount; i++) {
                                currentSearchPos = findSrc.indexOf(findTarget, currentSearchPos);
                                searchResult.append(tempFindSrc.substring(currentSearchPos, currentSearchPos + findTarget.length())).append("\n");
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
                            searchOutput.setText("未查找到目标！", TextView.BufferType.EDITABLE);
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
                    searchOutput.setText(getString(R.string.exception_occured) + e.toString());
                    searchInput.clearFocus();
                    searchOutput.clearFocus();
                    searchTarget.clearFocus();
                }
                break;
            case R.id.encrypt:
                try {
                    if (keyGenNeedToReset) {
                        if (encryptKey.getText().toString().isEmpty()) {
                            encryptOutput.setText(getString(R.string.key_empty), TextView.BufferType.EDITABLE);
                            return;
                        }
                        generatedKey = AESUtils.generateKey(encryptKey.getText().toString(), "TCreopargh_is_handsome");
                        keyGenNeedToReset = false;
                    }
                    String encryptSourceText = encryptInput.getText().toString();
                    String encryptResult;
                    encryptResult = AESUtils.getEnString(encryptSourceText, generatedKey);
                    encryptOutput.setText(encryptResult, TextView.BufferType.EDITABLE);
                    encryptInput.clearFocus();
                    encryptOutput.clearFocus();
                    encryptKey.clearFocus();
                } catch (Exception e) {
                    encryptOutput.setText(getString(R.string.exception_occured) + e.toString(), TextView.BufferType.EDITABLE);
                    encryptInput.clearFocus();
                    encryptOutput.clearFocus();
                    encryptKey.clearFocus();
                }
                break;
            case R.id.decrypt:
                try {
                    if (keyGenNeedToReset) {
                        if (encryptKey.getText().toString().isEmpty()) {
                            encryptOutput.setText(getString(R.string.key_empty), TextView.BufferType.EDITABLE);
                            return;
                        }
                        generatedKey = AESUtils.generateKey(encryptKey.getText().toString(), "TCreopargh_is_handsome");
                        keyGenNeedToReset = false;
                    }
                    String decryptSourceText = encryptInput.getText().toString();
                    String decryptResult;
                    decryptResult = AESUtils.getDeString(decryptSourceText, generatedKey);
                    encryptOutput.setText(decryptResult, TextView.BufferType.EDITABLE);
                    encryptInput.clearFocus();
                    encryptOutput.clearFocus();
                    encryptKey.clearFocus();
                } catch (Exception e) {
                    if (e instanceof IllegalArgumentException) {
                        encryptOutput.setText("输入内容不是加密后的密文！\n" +
                                "本应用采用加盐加密，普通AES密文解密时会出现错误！", TextView.BufferType.EDITABLE);
                    } else {
                        encryptOutput.setText(getString(R.string.exception_occured) + e.toString(), TextView.BufferType.EDITABLE);
                    }
                    encryptInput.clearFocus();
                    encryptOutput.clearFocus();
                    encryptKey.clearFocus();
                }
                break;
            case R.id.textReverse:
                try {
                    String reverseSrc = moreInput.getText().toString();
                    StringBuilder reverseResult = new StringBuilder();
                    for (int i = reverseSrc.length() - 1; i >= 0; i--) {
                        reverseResult.append(reverseSrc.charAt(i));
                    }
                    moreOutput.setText(reverseResult.toString(), TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                } catch (Exception e) {
                    moreOutput.setText(getString(R.string.exception_occured) + e.toString(), TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                }
                break;
            case R.id.upperLowerCase:
                try {
                    String caseSrc = moreInput.getText().toString();
                    StringBuilder caseOutput = new StringBuilder();
                    String toastMessage = "未知错误";
                    switch (caseSwitchStatus) {
                        case ALL_LOWER:
                            caseOutput.append(caseSrc.toLowerCase());
                            toastMessage = "全部转为小写，再次点击全部转为大写";
                            caseSwitchStatus++;
                            break;
                        case ALL_UPPER:
                            caseOutput.append(caseSrc.toUpperCase());
                            toastMessage = "全部转为大写，再次点击切换大小写";
                            caseSwitchStatus++;
                            break;
                        case CASE_REVERSE:
                            for (int i = 0; i < caseSrc.length(); i++) {
                                caseOutput.append(switchCase(caseSrc.charAt(i)));
                            }
                            toastMessage = "切换大小写，再次点击单词首字母大写";
                            caseSwitchStatus++;
                            break;
                        case FIRST_UPPER:
                            //String[] words = caseSrc.split("[ \n\r\t|!@#$%^&*(),./?><:\"《》，'。；‘：“—+=！￥…（）\\[\\]~\\\\]");
                            final String symbols = " \n\r\t|!@#$%^&*(),./?><:\"《》，'。；‘：“—+=！￥…（）[]~\\";
                            boolean wordBegin = true;
                            for (int i = 0; i < caseSrc.length(); i++) {
                                if (wordBegin) {
                                    caseOutput.append(Character.toUpperCase(caseSrc.charAt(i)));
                                    wordBegin = false;
                                } else {
                                    caseOutput.append(Character.toLowerCase(caseSrc.charAt(i)));
                                }
                                if (symbols.contains(String.valueOf(caseSrc.charAt(i)))) {
                                    wordBegin = true;
                                }
                            }
                            toastMessage = "单词首字母大写，再次点击句子首字母大写";
                            caseSwitchStatus++;
                            break;
                        case SENTENCE_FIRST:
                            boolean sentenceBegin = true;
                            final String endSentence = "\n\r\t?!.？。！…;；";
                            for (int i = 0; i < caseSrc.length(); i++) {
                                if (sentenceBegin && ((caseSrc.charAt(i) >= 'a' && caseSrc.charAt(i) <= 'z') ||
                                        (caseSrc.charAt(i) >= 'A' && caseSrc.charAt(i) <= 'Z'))) {
                                    caseOutput.append(Character.toUpperCase(caseSrc.charAt(i)));
                                    sentenceBegin = false;
                                } else {
                                    caseOutput.append(Character.toLowerCase(caseSrc.charAt(i)));
                                }
                                if (endSentence.contains(String.valueOf(caseSrc.charAt(i)))) {
                                    sentenceBegin = true;
                                }
                            }
                            toastMessage = "句子首字母大写，再次点击全部转为小写";
                            caseSwitchStatus = 0;
                            break;
                        default:
                            caseSwitchStatus = ALL_LOWER;
                    }
                    moreOutput.setText(caseOutput.toString(), TextView.BufferType.EDITABLE);
                    Toasty.info(MainActivity.this, toastMessage, Toast.LENGTH_SHORT, true).show();
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                } catch (Exception e) {
                    moreOutput.setText(getString(R.string.exception_occured) + e.toString(), TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                }
                break;
            case R.id.addNumbers:
                try {
                    String addNumbersSrc = moreInput.getText().toString();
                    String numberParagraphs[] = addNumbersSrc.split("\\cJ");
                    StringBuilder addNumbersOutput = new StringBuilder();
                    for (int i = 1; i <= numberParagraphs.length; i++) {
                        addNumbersOutput.append(i).append('.').append(numberParagraphs[i - 1]).append('\n');
                    }
                    moreOutput.setText(addNumbersOutput, TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                } catch (Exception e) {
                    moreOutput.setText(getString(R.string.exception_occured) + e.toString(), TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                }
                break;
            case R.id.formatCode:
                try {
                    String formatCodeSrc = moreInput.getText().toString();
                    Formatter formatter = new Formatter();
                    String formattedCode = formatter.formatSource(formatCodeSrc);
                    moreOutput.setText(formattedCode, TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                } catch (Exception e) {
                    moreOutput.setText(getString(R.string.exception_occured) + e.toString(), TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                }
                break;
            case R.id.customRandom:
                try {
                    final String[] dataQuantityString = {"1"};
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    LayoutInflater layoutInflater = LayoutInflater.from(this);
                    View dialogView = layoutInflater.inflate(R.layout.dialog_set_quantity, null);
                    dialog.setView(dialogView);
                    dialog.setCancelable(false)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setNeutralButton("查看示例", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    moreInput.setText("生成随机时间：{0,23}:{0,59}\n");
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        final EditText dataQuantity = dialogView.findViewById(R.id.dataQuantity);
                                        dataQuantityString[0] = dataQuantity.getText().toString();
                                        if (dataQuantityString[0].isEmpty()) {
                                            dataQuantityString[0] = "1";
                                        }

                                        int quantity = Integer.parseInt(dataQuantityString[0]);
                                        StringBuilder finalOutputBuilder = new StringBuilder();
                                        int min = 0, max = 0;
                                        String format = moreInput.getText().toString();
                                        for (int q = 0; q < quantity; q++) {
                                            StringBuilder tempOutput = new StringBuilder();
                                            for (int i = 0; i < format.length(); i++) {
                                                if (i < format.length() - 1 && format.charAt(i) == '{' && format.charAt(i + 1) == '{') {
                                                    i++;
                                                    tempOutput.append('{');
                                                    continue;
                                                }
                                                if (format.charAt(i) == '{') {
                                                    int length = 0;
                                                    for (int j = 1; i + j < format.length(); j++) {
                                                        if (format.charAt(i + j) == ',' || format.charAt(i + j) == '，') {
                                                            min = Integer.parseInt(format.substring(i + 1, i + j));
                                                            for (int k = 1; i + j + k < format.length(); k++) {
                                                                if (format.charAt(i + j + k) == '}') {
                                                                    max = Integer.parseInt(format.substring(i + j + 1, i + j + k));
                                                                    length = j + k;
                                                                    break;
                                                                }
                                                            }
                                                            break;
                                                        }
                                                    }
                                                    Random random = new Random();
                                                    int randNum = random.nextInt(max - min + 1) + min;
                                                    tempOutput.append(String.valueOf(randNum));
                                                    i += length;
                                                } else {
                                                    tempOutput.append(format.charAt(i));
                                                }
                                            }
                                            finalOutputBuilder.append(tempOutput.toString());
                                        }
                                        moreOutput.setText(finalOutputBuilder.toString(), TextView.BufferType.EDITABLE);
                                    } catch (Exception e) {
                                        if (e instanceof NumberFormatException) {
                                            moreOutput.setText("输入格式错误：" + e.toString(), TextView.BufferType.EDITABLE);
                                        } else if (e instanceof IllegalArgumentException) {
                                            moreOutput.setText("参数错误，随机数的上界必须大于下界！", TextView.BufferType.EDITABLE);
                                        } else {
                                            moreOutput.setText(getString(R.string.exception_occured) + e.toString(), TextView.BufferType.EDITABLE);
                                        }
                                    } finally {
                                        dialog.dismiss();
                                        moreOutput.clearFocus();
                                        moreInput.clearFocus();
                                    }
                                    //dataQuantityString[0] = dataQuantity.getText().toString();
                                    //Toast.makeText(MainActivity.this,dataQuantityString[0],Toast.LENGTH_LONG).show();
                                }
                            }).create().show();
                } catch (Exception e) {
                    moreOutput.setText(getString(R.string.exception_occured) + e.toString(), TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                }
                break;
            case R.id.toMD5:
                try {
                    String md5Src = moreInput.getText().toString();
                    String md5 = getMD5(md5Src);
                    moreOutput.setText(md5, TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                } catch (Exception e) {
                    moreOutput.setText(getString(R.string.exception_occured) + e.toString(), TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                }
                break;
            case R.id.toBase64:
                try {
                    String plainSrc = moreInput.getText().toString();
                    String base64 = Base64.encodeToString(plainSrc.getBytes(), Base64.DEFAULT);
                    moreOutput.setText(base64, TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                } catch (Exception e) {
                    moreOutput.setText(getString(R.string.exception_occured) + e.toString(), TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                }
                break;
            case R.id.fromBase64:
                try {
                    String base64Input = moreInput.getText().toString();
                    String decodedStr = new String(Base64.decode(base64Input.getBytes(), Base64.DEFAULT));
                    moreOutput.setText(decodedStr, TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                } catch (Exception e) {
                    if (e instanceof IllegalArgumentException) {
                        moreOutput.setText("输入内容不是合法的Base64编码！", TextView.BufferType.EDITABLE);
                    } else {
                        moreOutput.setText(getString(R.string.exception_occured) + e.toString(), TextView.BufferType.EDITABLE);
                    }
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                }
                break;
            case R.id.toMorseCode:
                try {
                    String morseCodeInput = moreInput.getText().toString();
                    MorseCoder morseCoder = new MorseCoder();
                    moreOutput.setText(morseCoder.encode(morseCodeInput), TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                } catch (Exception e) {
                    moreOutput.setText(getString(R.string.exception_occured) + e.toString(), TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                }
                break;
            case R.id.fromMorseCode:
                try {
                    String morseCodeInput = moreInput.getText().toString();
                    morseCodeInput = morseCodeInput.replace(' ', '/').replace('\\', '/')
                            .replace('\n', '/').replace('\r', '/')
                            .replace('*', '.').replace('·', '.')
                            .replace('_', '-').replace('—', '-');
                    MorseCoder morseCoder = new MorseCoder();
                    moreOutput.setText(morseCoder.decode(morseCodeInput), TextView.BufferType.EDITABLE);
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                } catch (Exception e) {
                    if (e instanceof IllegalArgumentException) {
                        moreOutput.setText("输入内容不是合法的摩斯电码！", TextView.BufferType.EDITABLE);
                    } else {
                        moreOutput.setText(getString(R.string.exception_occured) + e.toString(), TextView.BufferType.EDITABLE);
                    }
                    moreOutput.clearFocus();
                    moreInput.clearFocus();
                }
                break;
            default:
        }
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

    private void resetSearch() {
        try {
            currentSearchPos = 0;
            searchCount = -1;
            currentSearchCount = -1;
            begin = 0;
            pattern = Pattern.compile(searchTarget.getText().toString());
            matcher = pattern.matcher(searchInput.getText().toString());
        } catch (Exception ignored) {
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == REQUESTCODE_READ) {
                    //List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);
                    List<String> list = data.getStringArrayListExtra("paths");
                    path = list.get(0);
                } else if (requestCode == REQUESTCODE_WRITE) {
                    path = data.getStringExtra("path");
                }
            }
            if (requestCode == REQUESTCODE_READ) {
                String getFileContent = readToString(path);
                switch (getCurrentShowingLayoutId()) {
                    case R.id.textReplaceLayout:
                        replaceInput.setText(getFileContent, TextView.BufferType.EDITABLE);
                        break;
                    case R.id.textSearchLayout:
                        searchInput.setText(getFileContent, TextView.BufferType.EDITABLE);
                        break;
                    case R.id.textShuffleLayout:
                        shuffleInput.setText(getFileContent, TextView.BufferType.EDITABLE);
                        break;
                    case R.id.textEncryptLayout:
                        encryptInput.setText(getFileContent, TextView.BufferType.EDITABLE);
                        break;
                    case R.id.textMoreLayout:
                        moreInput.setText(getFileContent, TextView.BufferType.EDITABLE);
                        break;
                    default:
                }
            } else if (requestCode == REQUESTCODE_WRITE) {
                if (path.isEmpty()) {
                    Toasty.warning(MainActivity.this, "未选定目标文件夹！", Toast.LENGTH_LONG, true).show();
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
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat();
                sdf.applyPattern("yyyy-MM-dd_HH:mm:ss");
                Date date = new Date();
                filename.append("/TextConverter-").append(sdf.format(date)).append(".txt");
                writeSDFile(filename.toString(), outputString);
                Toasty.success(MainActivity.this, "文件已保存为: " + filename, Toast.LENGTH_LONG, true).show();
            }
        } catch (Exception e) {
            Toasty.error(MainActivity.this, getString(R.string.exception_occured) + e.toString(), Toast.LENGTH_LONG, true).show();
        }
    }

    public void writeSDFile(String fileName, String write_str) throws IOException {

        File file = new File(fileName);

        FileOutputStream fos = new FileOutputStream(file);

        byte[] bytes = write_str.getBytes();

        fos.write(bytes);

        fos.close();
    }

    public String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long filelength = file.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("抱歉，本系统不支持以下编码格式：" + encoding);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getFile();
                } else {
                    Toasty.warning(MainActivity.this, "您拒绝了文件访问权限，因此本功能无法运行。", Toast.LENGTH_LONG, true).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getStoreLocation();
                } else {
                    Toasty.warning(MainActivity.this, "您拒绝了文件访问权限，因此本功能无法运行。", Toast.LENGTH_LONG, true).show();
                }
                break;
            case 3:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    storeDirectly();
                } else {
                    Toasty.warning(MainActivity.this, "您拒绝了文件访问权限，因此本功能无法运行。", Toast.LENGTH_LONG, true).show();
                }
                break;
            default:
        }
    }

    private void getStoreLocation() {
        String pathTemp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TextConverter";
        File destDir = new File(pathTemp);
        if (!destDir.exists()) {
            boolean doMkdirSuccess = destDir.mkdirs();
            if (!doMkdirSuccess) {
                Toasty.error(MainActivity.this, "文件夹创建失败！", Toast.LENGTH_LONG, true).show();
                pathTemp = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        }
        path = "";
        new LFilePicker()
                .withActivity(MainActivity.this)
                .withBackgroundColor("#03a9f4")
                .withRequestCode(REQUESTCODE_WRITE)
                .withTitle("选择目标文件夹")
                .withChooseMode(false)
                .withStartPath(pathTemp)
                .withIconStyle(Constant.ICON_STYLE_YELLOW)
                .withBackIcon(Constant.BACKICON_STYLETHREE)
                .start();
    }

    private void storeDirectly() {
        try {
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TextConverter";
            File destDir = new File(path);
            if (!destDir.exists()) {
                boolean doMkdirSuccess = destDir.mkdirs();
                if (!doMkdirSuccess) {
                    Toasty.error(MainActivity.this, "文件夹创建失败！", Toast.LENGTH_LONG, true).show();
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
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.applyPattern("yyyy-MM-dd_HH:mm:ss");
            Date date = new Date();
            filename.append("/TextConverter-").append(sdf.format(date)).append(".txt");
            writeSDFile(filename.toString(), outputString);
            Toasty.info(MainActivity.this, "文件过大，因此将直接保存。\n文件已保存为: " + filename, Toast.LENGTH_LONG, true).show();
        } catch (Exception e) {
            Toasty.error(MainActivity.this, getString(R.string.exception_occured) + e.toString(), Toast.LENGTH_LONG, true).show();
        } finally {
            path = "";
        }
    }

    private void getFile() {
        /*
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
        */
        path = "";
        new LFilePicker()
                .withActivity(MainActivity.this)
                .withBackgroundColor("#03a9f4")
                .withRequestCode(REQUESTCODE_READ)
                .withMutilyMode(false)
                .withTitle("选择文件")
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
}
