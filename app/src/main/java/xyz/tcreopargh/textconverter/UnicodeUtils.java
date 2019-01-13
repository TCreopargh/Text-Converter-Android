package xyz.tcreopargh.textconverter;

/**
 * @author Nan
 * @link https://blog.csdn.net/qq_34471736/article/details/70311796
 */
public class UnicodeUtils {

    /**
     * 将utf-8的汉字转换成unicode格式汉字码
     *
     * @param string utf-8
     * @return unicode格式汉字码
     */
    public static String stringToUnicode(String string) {

        StringBuilder unicode = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            unicode.append("\\u").append(String.format("%04x", (int) c));
        }
        return unicode.toString();
    }

    /**
     * 将unicode的汉字码转换成utf-8格式的汉字
     *
     * @param unicode unicode的汉字码
     * @return utf-8格式的汉字
     */
    public static String unicodeToString(String unicode) {

        String str = unicode.replace("0x", "\\");

        StringBuilder string = new StringBuilder();
        String[] hex = str.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            string.append((char) data);
        }
        return string.toString();
    }
}
