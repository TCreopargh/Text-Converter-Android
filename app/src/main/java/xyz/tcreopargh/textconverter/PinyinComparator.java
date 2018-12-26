package xyz.tcreopargh.textconverter;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.Comparator;

public class PinyinComparator implements Comparator<String> {
    /*
        public int compare(Object o1, Object o2) {
            if (((String) o1).isEmpty()) {
                return 1;
            }
            if (((String) o2).isEmpty()) {
                return -1;
            }
            char c1 = ((String) o1).charAt(0);
            char c2 = ((String) o2).charAt(0);
            return concatPinyinStringArray(
                    PinyinHelper.toHanyuPinyinStringArray(c1)).compareTo(
                    concatPinyinStringArray(PinyinHelper
                            .toHanyuPinyinStringArray(c2)));
        }
    */
    // Code inferred from: https://he7ning3.iteye.com/blog/2287384
    public int compare(String o1, String o2) {
        for (int i = 0; i < o1.length() && i < o2.length(); i++) {
            // 逐个获取字母
            char codePoint1 = o1.charAt(i);
            char codePoint2 = o2.charAt(i);
            // 确定字符是否在增补字符范围内,在范围内则跳过(确定指定字符是否为Unicode空白字符)
            if (Character.isSupplementaryCodePoint(codePoint1)
                    || Character.isSupplementaryCodePoint(codePoint2)) {
                // 如果不相等则返回比较结果
                if (codePoint1 != codePoint2) {
                    return codePoint1 - codePoint2;
                } else { // 相等则比较下一个
                    continue;
                }
            }
            // 将汉字转换为拼音，不是汉子则为NULL
            String pinyin1 = pinyin(codePoint1);
            String pinyin2 = pinyin(codePoint2);
            // 不为汉字则与原英文比较
            if (pinyin1 == null) {
                pinyin1 = (codePoint1 + "");
            }
            if (pinyin2 == null) {
                pinyin2 = (codePoint2 + "");
            }
            // 忽略大小写比较
            if (!pinyin1.toLowerCase().equals(pinyin2.toLowerCase())) {
                return pinyin1.toLowerCase().compareTo(pinyin2.toLowerCase());
            } else {
                // 不忽略大小写比较
                if (!pinyin1.equals(pinyin2)) {
                    return pinyin1.compareTo(pinyin2);
                }
            }
        }
        return o1.length() - o2.length();
    }

    private String pinyin(char c) {
        String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c);
        if (pinyins == null) {
            return null;
        }
        return pinyins[0];
    }
    /*
    private String concatPinyinStringArray(String[] pinyinArray) {
        StringBuilder pinyinSbf = new StringBuilder();
        if ((pinyinArray != null) && (pinyinArray.length > 0)) {
            for (String aPinyinArray : pinyinArray) {
                pinyinSbf.append(aPinyinArray);
            }
        }
        return pinyinSbf.toString();
    }
    */
}
