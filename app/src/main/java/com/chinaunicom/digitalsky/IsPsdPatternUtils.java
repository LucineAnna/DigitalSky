package com.chinaunicom.digitalsky;

import java.util.regex.Pattern;

public class IsPsdPatternUtils {
    //密码格式，必须包含小写字母和数字，可以是字母数字下划线组成，长度6-18位
    private static Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*?[a-z])(?=.*?[0-9])[a-zA-Z0-9_]{6,18}$");
    public static boolean isPsdPattern(String password){
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}
