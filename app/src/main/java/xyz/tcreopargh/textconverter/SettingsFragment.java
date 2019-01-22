package xyz.tcreopargh.textconverter;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static xyz.tcreopargh.textconverter.SettingsActivity.REQUESTCODE_WRITE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;
import de.mateware.snacky.Snacky;
import es.dmoral.toasty.Toasty;
import java.io.File;
import java.util.Locale;
import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {
    private MyListener myListener;
    private String defaultSalt = MainActivity.defaultSalt;
    private String salt = defaultSalt;
    private Preference storePath, initialLayout, outputEncoding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myListener = (MyListener) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences3 =
                Objects.requireNonNull(getContext()).getSharedPreferences("settings", MODE_PRIVATE);
        String pathTemp =
                sharedPreferences3.getString(
                        "default_path",
                        Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/TextConverter");
        storePath.setSummary(getString(R.string.file_pos_current) + pathTemp);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                boolean whether = Objects.requireNonNull(data).getBooleanExtra("easter_egg", false);
                Intent intent = new Intent();
                intent.putExtra("easter_egg1", whether);
                Objects.requireNonNull(getActivity()).setResult(RESULT_OK, intent);
            }
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName("settings");
        setPreferencesFromResource(R.xml.app_preferences, rootKey);
        Preference resetSettings = findPreference("resetSettings");
        Preference about = findPreference("about");
        Preference saltItem = findPreference("saltItem");
        Preference language = findPreference("appLanguage");
        storePath = findPreference("storePath");
        initialLayout = findPreference("initialLayout");
        outputEncoding = findPreference("outputEncoding");
        SharedPreferences sharedPreferences0 =
                Objects.requireNonNull(getContext()).getSharedPreferences("settings", MODE_PRIVATE);
        String pathTemp =
                sharedPreferences0.getString(
                        "default_path",
                        Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/TextConverter");
        loadInitLayout();
        storePath.setSummary(getString(R.string.file_pos_current) + pathTemp);
        salt =
                Objects.requireNonNull(getContext())
                        .getSharedPreferences("settings", MODE_PRIVATE)
                        .getString("salt", defaultSalt);
        resetSettings.setOnPreferenceClickListener(
                preference -> {
                    LovelyStandardDialog lovelyStandardDialog1 =
                            new LovelyStandardDialog(
                                    getActivity(), LovelyStandardDialog.ButtonLayout.HORIZONTAL);
                    lovelyStandardDialog1
                            .setTitle(getString(R.string.reset_all_settings))
                            .setTopColorRes(R.color.colorAccent)
                            .setIcon(R.drawable.ic_settings_backup_restore_white_48dp)
                            .setMessage(getString(R.string.settings_reset_ask))
                            .setButtonsColorRes(R.color.colorAccent)
                            .setPositiveButton(
                                    R.string.confirm,
                                    v -> {
                                        SharedPreferences sharedPreferences1 =
                                                Objects.requireNonNull(getContext())
                                                        .getSharedPreferences(
                                                                "settings", MODE_PRIVATE);
                                        Editor editor = sharedPreferences1.edit();
                                        editor.clear();
                                        boolean result = editor.commit();
                                        if (result) {
                                            myListener.sendContent(true);
                                            SettingsActivity activity =
                                                    (SettingsActivity) getActivity();
                                            Objects.requireNonNull(activity).reLoadFragView();
                                            Toasty.success(
                                                            Objects.requireNonNull(getActivity()),
                                                            R.string.reset_success,
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                        } else {
                                            Toasty.error(
                                                            Objects.requireNonNull(getActivity()),
                                                            R.string.reset_failed,
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    })
                            .setNegativeButton(
                                    R.string.cancel_zh, v -> lovelyStandardDialog1.dismiss())
                            .create()
                            .show();
                    return true;
                });
        about.setOnPreferenceClickListener(
                preference -> {
                    Intent intent = new Intent(getActivity(), AboutActivity.class);
                    Objects.requireNonNull(getActivity()).startActivityForResult(intent, 0);
                    return true;
                });
        saltItem.setOnPreferenceClickListener(
                preference -> {
                    LovelyTextInputDialog lovelyTextInputDialog =
                            new LovelyTextInputDialog(getActivity());
                    lovelyTextInputDialog
                            .setTopColorRes(R.color.safeGreen)
                            .setIcon(R.drawable.ic_lock_white)
                            .setTitle(getString(R.string.aes_add_salt))
                            .setMessage(getString(R.string.salt_intro))
                            .configureView(
                                    v14 -> {
                                        v14.setFocusableInTouchMode(true);
                                        v14.setFocusable(true);
                                    })
                            .configureEditText(
                                    v15 -> {
                                        v15.setHint(getString(R.string.empty_to_not_use_salt));
                                        if (Objects.requireNonNull(getContext())
                                                .getSharedPreferences("settings", MODE_PRIVATE)
                                                .getBoolean("doUseMonospaced", false)) {
                                            v15.setTextAppearance(R.style.MyMonospace);
                                        } else {
                                            v15.setTextAppearance(R.style.MyRegular);
                                        }
                                        v15.setText(salt, BufferType.EDITABLE);
                                        v15.clearFocus();
                                    })
                            .setConfirmButtonColor(
                                    Objects.requireNonNull(getContext())
                                            .getColor(R.color.colorAccent))
                            .setNegativeButtonColor(getContext().getColor(R.color.colorAccent))
                            .setNegativeButton(
                                    getString(R.string.reset_default),
                                    v16 -> {
                                        try {
                                            String tempSalt = salt;
                                            salt = defaultSalt;
                                            MainActivity.keyGenNeedToReset = true;
                                            SharedPreferences sharedPreferences =
                                                    getContext()
                                                            .getSharedPreferences(
                                                                    "settings", MODE_PRIVATE);
                                            Editor editor = sharedPreferences.edit();
                                            editor.putString("salt", salt);
                                            editor.apply();
                                            Snacky.builder()
                                                    .setActivity(
                                                            Objects.requireNonNull(getActivity()))
                                                    .setDuration(Snacky.LENGTH_LONG)
                                                    .setText(getString(R.string.already_reset))
                                                    .setActionText(R.string.undo)
                                                    .setActionClickListener(
                                                            v1 -> {
                                                                salt = tempSalt;
                                                                SharedPreferences
                                                                        sharedPreferences1 =
                                                                                getContext()
                                                                                        .getSharedPreferences(
                                                                                                "settings",
                                                                                                MODE_PRIVATE);
                                                                Editor editor1 =
                                                                        sharedPreferences1.edit();
                                                                editor1.putString("salt", tempSalt);
                                                                editor1.apply();
                                                            })
                                                    .success()
                                                    .show();
                                        } catch (Exception e) {
                                            Toasty.error(
                                                            Objects.requireNonNull(getActivity()),
                                                            getContext()
                                                                            .getString(
                                                                                    R.string
                                                                                            .exception_occurred)
                                                                    + e.toString(),
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                        } finally {
                                            lovelyTextInputDialog.dismiss();
                                        }
                                    })
                            .setConfirmButton(
                                    R.string.confirm,
                                    text -> {
                                        try {
                                            salt = text;
                                            MainActivity.keyGenNeedToReset = true;
                                            SharedPreferences sharedPreferences =
                                                    getContext()
                                                            .getSharedPreferences(
                                                                    "settings", MODE_PRIVATE);
                                            Editor editor = sharedPreferences.edit();
                                            editor.putString("salt", salt);
                                            editor.apply();
                                            if (salt.isEmpty()) {
                                                /*
                                                Toasty.success(
                                                                Objects.requireNonNull(
                                                                        getActivity()),
                                                                "已设置为不对密码进行加盐！",
                                                                Toast.LENGTH_LONG)
                                                        .show();
                                                        */
                                                Toasty.custom(
                                                                Objects.requireNonNull(
                                                                        getActivity()),
                                                                R.string.no_use_salt,
                                                                R.drawable.lock_open,
                                                                getActivity()
                                                                        .getColor(
                                                                                R.color
                                                                                        .warningToast),
                                                                Toast.LENGTH_LONG,
                                                                true,
                                                                true)
                                                        .show();
                                            } else {
                                                /*
                                                Toasty.success(
                                                                Objects.requireNonNull(
                                                                        getActivity()),
                                                                "设置成功！",
                                                                Toast.LENGTH_LONG)
                                                        .show();*/
                                                Toasty.custom(
                                                                Objects.requireNonNull(
                                                                        getActivity()),
                                                                R.string.settings_success,
                                                                R.drawable.ic_lock,
                                                                getActivity()
                                                                        .getColor(
                                                                                R.color
                                                                                        .successToast),
                                                                Toast.LENGTH_LONG,
                                                                true,
                                                                true)
                                                        .show();
                                            }
                                        } catch (Exception e) {
                                            Toasty.error(
                                                            Objects.requireNonNull(getActivity()),
                                                            getContext()
                                                                            .getString(
                                                                                    R.string
                                                                                            .exception_occurred)
                                                                    + e.toString(),
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                        } finally {
                                            lovelyTextInputDialog.dismiss();
                                        }
                                    })
                            .show();
                    return true;
                });
        storePath.setOnPreferenceClickListener(
                preference -> {
                    if (ContextCompat.checkSelfPermission(
                                    Objects.requireNonNull(getActivity()),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        getActivity()
                                .requestPermissions(
                                        new String[] {
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        },
                                        233);
                    } else {
                        getStoreLocation();
                    }
                    return true;
                });
        initialLayout.setOnPreferenceChangeListener(
                (preference, newValue) -> {
                    int layoutId = Integer.parseInt(newValue.toString());
                    String[] array = getResources().getStringArray(R.array.layoutArray);
                    initialLayout.setSummary(
                            getString(R.string.init_layout_current) + array[layoutId]);
                    return true;
                });
        outputEncoding.setOnPreferenceChangeListener(
                (preference, newValue) -> {
                    outputEncoding.setSummary(getString(R.string.encoding_current) + newValue);
                    return true;
                });
        language.setOnPreferenceChangeListener(
                (preference, newValue) -> {
                    Intent intent =
                            Objects.requireNonNull(getActivity())
                                    .getBaseContext()
                                    .getPackageManager()
                                    .getLaunchIntentForPackage(
                                            getActivity().getBaseContext().getPackageName());
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    }
                    startActivity(intent);
                    return true;
                });
    }

    private void getStoreLocation() {
        SharedPreferences sharedPreferences =
                Objects.requireNonNull(getContext()).getSharedPreferences("settings", MODE_PRIVATE);
        String pathTemp =
                sharedPreferences.getString(
                        "default_path",
                        Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/TextConverter");
        File destDir = new File(pathTemp);
        if (!destDir.exists()) {
            boolean doMkdirSuccess = destDir.mkdirs();
            if (!doMkdirSuccess) {
                Toasty.error(
                                Objects.requireNonNull(getActivity()),
                                R.string.fail_mkdir,
                                Toast.LENGTH_LONG,
                                true)
                        .show();
                pathTemp = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        }
        SettingsActivity.path = "";
        new LFilePicker()
                .withActivity(getActivity())
                .withBackgroundColor("#03a9f4")
                .withRequestCode(REQUESTCODE_WRITE)
                .withTitle(getString(R.string.pick_folder))
                .withChooseMode(false)
                .withStartPath(pathTemp)
                .withIconStyle(Constant.ICON_STYLE_YELLOW)
                .withBackIcon(Constant.BACKICON_STYLETHREE)
                .start();
    }

    private void loadInitLayout() {
        try {
            SharedPreferences sharedPreferences0 =
                    Objects.requireNonNull(getContext())
                            .getSharedPreferences("settings", MODE_PRIVATE);
            int layoutId =
                    Integer.parseInt(
                            Objects.requireNonNull(
                                    sharedPreferences0.getString("initialLayout", "0")));
            String currentEncoding = sharedPreferences0.getString("outputEncoding", "UTF-8");
            String[] array = getResources().getStringArray(R.array.layoutArray);
            initialLayout.setSummary(getString(R.string.init_layout_current) + array[layoutId]);
            outputEncoding.setSummary(getString(R.string.encoding_current) + currentEncoding);
        } catch (Exception ignored) {
        }
    }

    public interface MyListener {
        void sendContent(boolean isSettingsReset);
    }
}
