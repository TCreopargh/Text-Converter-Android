package xyz.tcreopargh.textconverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TCreopargh
 */
public class TextConverter {

    public static final String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};

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
            md.update(plainText.getBytes(StandardCharsets.UTF_8));
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

    public static String replaceWithRegex(String textReplaceInput, String replaceFromStr, String replaceToStr) {
        boolean bool = false;
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
        return str;
    }
    public static String replaceWithoutRegex(boolean doCapsSensetive, String input, String replaceFromStr, String replaceToStr) {
        String textReplaceOutput;
        if (doCapsSensetive) {
            textReplaceOutput =
                input.replace(replaceFromStr, replaceToStr);
        } else {
            for (String key : fbsArr) {
                if (replaceFromStr.contains(key)) {
                    replaceFromStr = replaceFromStr.replace(key, "\\" + key);
                }
            }
            textReplaceOutput =
                input.replaceAll(
                    "(?i)" + replaceFromStr, replaceToStr);
        }
        return  textReplaceOutput;
    }
}
