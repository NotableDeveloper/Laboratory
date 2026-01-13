package logback_filtering.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDataConverter extends CompositeConverter<ILoggingEvent> {
    private static final Pattern USER_NAME = Pattern.compile("[가-힣]{2,}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("010\\d{8}");

    @Override
    protected String transform(ILoggingEvent event, String s) {
        if (s == null) return null;

        Matcher phoneMatcher = PHONE_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();
        while (phoneMatcher.find()) {
            String original = phoneMatcher.group();
            String masked = original.substring(0, 3) + "****" + original.substring(7);
            phoneMatcher.appendReplacement(sb, masked);
        }
        phoneMatcher.appendTail(sb);
        String result = sb.toString();

        Matcher nameMatcher = USER_NAME.matcher(result);
        sb = new StringBuffer();
        while (nameMatcher.find()) {
            String name = nameMatcher.group();
            StringBuilder maskedName = new StringBuilder();
            maskedName.append(name.charAt(0));
            for (int i = 1; i < name.length(); i++) {
                maskedName.append("*");
            }
            nameMatcher.appendReplacement(sb, maskedName.toString());
        }
        nameMatcher.appendTail(sb);

        return sb.toString();
    }
}