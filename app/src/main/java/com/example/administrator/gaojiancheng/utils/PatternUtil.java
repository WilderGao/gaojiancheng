package com.example.administrator.gaojiancheng.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式判断输入类
 * Created by Administrator on 2017/11/7.
 */

public class PatternUtil {
    /**
     * 验证账号是否符合邮箱格式
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        Pattern pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
