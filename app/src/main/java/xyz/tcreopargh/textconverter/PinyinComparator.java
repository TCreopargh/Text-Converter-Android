package xyz.tcreopargh.textconverter;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.Comparator;


public class PinyinComparator implements Comparator<Object> {
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

    private String concatPinyinStringArray(String[] pinyinArray) {
        StringBuilder pinyinSbf = new StringBuilder();
        if ((pinyinArray != null) && (pinyinArray.length > 0)) {
            for (String aPinyinArray : pinyinArray) {
                pinyinSbf.append(aPinyinArray);
            }
        }
        return pinyinSbf.toString();
    }
}