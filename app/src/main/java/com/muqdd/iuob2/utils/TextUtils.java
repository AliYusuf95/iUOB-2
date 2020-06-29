package com.muqdd.iuob2.utils;

import androidx.annotation.StringDef;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.util.regex.Pattern;

import static java.lang.annotation.RetentionPolicy.SOURCE;


/**
 * Created by Ali Yusuf on 10/20/2017.
 * iUOB-2
 */

@SuppressWarnings({"WeakerAccess","unused"})
public class TextUtils {

    // regular expression
    private static final String NAME_REGEX = "^[a-zA-Z_\\s]{3,}$";
    private static final String ORG_REGEX = "^[a-zA-Z_\\s]{3,}$";
    private static final String MJR_REGEX = "^[a-zA-Z_\\s]{2,}$";
    private static final String CPR_REGEX = "\\d{9}";
    private static final String MOBILE_REGEX = "^(\\+\\d{1,3}[- ]?)?\\d{3,14}$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9-]+\\.[a-zA-Z.]{2,5}$";
    private static final String HASHTAG_REGEX = "[#]+[A-Za-z0-9-_]+\\b";

    @Retention(SOURCE)
    @StringDef({NAME_REGEX, ORG_REGEX, MJR_REGEX, CPR_REGEX, MOBILE_REGEX, EMAIL_REGEX, HASHTAG_REGEX})
    @interface ValidationType {}

    // error message
    private static final String NAME_MSG = "Invalid Name";
    private static final String ORG_MSG = "Invalid Organization";
    private static final String MJR_MSG = "Invalid Major";
    private static final String EMAIL_MSG = "Invalid Email";
    private static final String MOBILE_MSG = "Invalid Mobile";
    private static final String CPR_MSG = "Invalid CPR";
    private static final String HT_MSG = "Invalid Hashtag";

    public static boolean isNameValid(TextView textView, boolean isRequired) {
        return isValid(textView, NAME_REGEX, NAME_MSG, isRequired);
    }

    public static boolean isOrgValid(TextView textView, boolean isRequired) {
        return isValid(textView, ORG_REGEX, ORG_MSG, isRequired);
    }

    public static boolean isMjrValid(TextView textView, boolean isRequired) {
        return isValid(textView, MJR_REGEX, MJR_MSG, isRequired);
    }

    public static boolean isEmailValid(TextView textView, boolean isRequired) {
        return isValid(textView, EMAIL_REGEX, EMAIL_MSG, isRequired);
    }

    public static boolean isCPRValid(TextView textView, boolean isRequired) {
        return isValid(textView, CPR_REGEX, CPR_MSG, isRequired);
    }

    public static boolean isMobileValid(TextView textView, boolean isRequired) {
        return isValid(textView, MOBILE_REGEX, MOBILE_MSG, isRequired);
    }

    public static boolean isHshTagValid(TextView textView, boolean isRequired) {
        return isValid(textView, HASHTAG_REGEX, HT_MSG, isRequired);
    }

    private static boolean isValid(TextView textView, @ValidationType String regex, String errMsg, boolean isRequired) {
        String field = textView.getText().toString().trim();
        textView.setError(null);

        // text isRequired and editText is blank, so return false
        if (isRequired && !hasText(textView)) return false;

        // pattern doesn't match so returning false
        if (!Pattern.matches(regex, field)) {
            textView.setError(errMsg);
            return false;
        }
        return true;
    }

    public static boolean isValid(String text, @ValidationType String regex, boolean isRequired) {
        String field = text.trim();

        // text isRequired and string is blank, so return false
        if (isRequired && !hasText(field)) return false;

        // pattern does match so returning true
        return Pattern.matches(regex, field);
    }

    public static boolean hasText(TextView tv) {
        tv.setError(null);
        if (!hasText(tv.getText().toString())) {
            tv.setError("Field is required");
            return false;
        }
        return true;
    }

    public static boolean hasText(String string) {
        return string != null && string.trim().length() != 0;
    }

}