package xyz.tcreopargh.textconverter;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static xyz.tcreopargh.textconverter.SettingsActivity.REQUESTCODE_WRITE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
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
import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {
    private MyListener myListener;
    private String defaultSalt = MainActivity.defaultSalt;
    private String salt = defaultSalt;
    private Preference storePath, initialLayout;

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
        storePath.setSummary("文件的存放默认路径和起始位置\n当前位置: " + pathTemp);
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
        storePath = findPreference("storePath");
        initialLayout = findPreference("initialLayout");
        SharedPreferences sharedPreferences0 =
                Objects.requireNonNull(getContext()).getSharedPreferences("settings", MODE_PRIVATE);
        String pathTemp =
                sharedPreferences0.getString(
                        "default_path",
                        Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/TextConverter");
        loadInitLayout();
        storePath.setSummary("文件的存放默认路径和起始位置\n当前位置: " + pathTemp);
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
                            .setTitle("重置设置")
                            .setTopColorRes(R.color.colorAccent)
                            .setIcon(R.drawable.ic_settings_backup_restore_white_48dp)
                            .setMessage("是否要重置所有设置？此操作不可撤销。")
                            .setButtonsColorRes(R.color.colorAccent)
                            .setPositiveButton(
                                    "确定",
                                    v -> {
                                        SharedPreferences sharedPreferences1 =
                                                Objects.requireNonNull(getContext())
                                                        .getSharedPreferences(
                                                                "settings", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences1.edit();
                                        editor.clear();
                                        boolean result = editor.commit();
                                        if (result) {
                                            myListener.sendContent(true);
                                            SettingsActivity activity =
                                                    (SettingsActivity) getActivity();
                                            Objects.requireNonNull(activity).reLoadFragView();
                                            Toasty.success(
                                                            Objects.requireNonNull(getActivity()),
                                                            "重置成功！",
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                        } else {
                                            Toasty.error(
                                                            Objects.requireNonNull(getActivity()),
                                                            "重置失败！",
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                        }
                                    })
                            .setNegativeButton("取消", v -> lovelyStandardDialog1.dismiss())
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
                            .setTitle("AES加盐")
                            .setMessage(
                                    "盐（Salt），在密码学中，是指在散列之前将散列内容（例如：密码）的任意固定位置插入特定的字符串。"
                                            + "这个在散列中加入字符串的方式称为“加盐”。"
                                            + "其作用是让加盐后的散列结果和没有加盐的结果不相同，在不同的应用情景中，这个处理可以增加额外的安全性。\n"
                                            + "注意：解密时需要盐和密码都对应才能够正确解密！\n"
                                            + "此项留空则表示不对密码进行加盐处理！")
                            .configureView(
                                    v14 -> {
                                        v14.setFocusableInTouchMode(true);
                                        v14.setFocusable(true);
                                    })
                            .configureEditText(
                                    v15 -> {
                                        v15.setHint("留空则表示不对密码进行加盐处理");
                                        if (Objects.requireNonNull(getContext())
                                                .getSharedPreferences("settings", MODE_PRIVATE)
                                                .getBoolean("doUseMonospaced", false)) {
                                            v15.setTextAppearance(R.style.MyMonospace);
                                        } else {
                                            v15.setTextAppearance(R.style.MyRegular);
                                        }
                                        v15.setText(salt, TextView.BufferType.EDITABLE);
                                        v15.clearFocus();
                                    })
                            .setConfirmButtonColor(
                                    Objects.requireNonNull(getContext())
                                            .getColor(R.color.colorAccent))
                            .setNegativeButtonColor(getContext().getColor(R.color.colorAccent))
                            .setNegativeButton(
                                    "恢复默认",
                                    v16 -> {
                                        try {
                                            String tempSalt = salt;
                                            salt = defaultSalt;
                                            MainActivity.keyGenNeedToReset = true;
                                            SharedPreferences sharedPreferences =
                                                    getContext()
                                                            .getSharedPreferences(
                                                                    "settings", MODE_PRIVATE);
                                            SharedPreferences.Editor editor =
                                                    sharedPreferences.edit();
                                            editor.putString("salt", salt);
                                            editor.apply();
                                            Snacky.builder()
                                                    .setActivity(
                                                            Objects.requireNonNull(getActivity()))
                                                    .setDuration(Snacky.LENGTH_LONG)
                                                    .setText("已恢复")
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
                                                                SharedPreferences.Editor editor1 =
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
                                            SharedPreferences.Editor editor =
                                                    sharedPreferences.edit();
                                            editor.putString("salt", salt);
                                            editor.apply();
                                            if (salt.isEmpty()) {
                                                Toasty.success(
                                                                Objects.requireNonNull(
                                                                        getActivity()),
                                                                "已设置为不对密码进行加盐！",
                                                                Toast.LENGTH_LONG)
                                                        .show();
                                            } else {
                                                Toasty.success(
                                                                Objects.requireNonNull(
                                                                        getActivity()),
                                                                "设置成功！",
                                                                Toast.LENGTH_LONG)
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
                    initialLayout.setSummary("选择进入应用时首先显示的界面\n当前设置: " + array[layoutId]);
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
                                "文件夹创建失败！",
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
                .withTitle("选择目标文件夹")
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
            String[] array = getResources().getStringArray(R.array.layoutArray);
            initialLayout.setSummary("选择进入应用时首先显示的界面\n当前设置: " + array[layoutId]);
        } catch (Exception ignored) {
        }
    }

    public interface MyListener {
        void sendContent(boolean isSettingsReset);
    }
}
