package logback_filtering;

import ch.qos.logback.core.pattern.CompositeConverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberConverter extends CompositeConverter {
    private static final Pattern PHONE_PATTERN = Pattern.compile("010\\d{8}");

    @Override
    protected String transform(Object o, String s) {
        if(s == null) return null;

        Matcher matcher = PHONE_PATTERN.matcher(s);
        return matcher.replaceAll("");
    }
}
