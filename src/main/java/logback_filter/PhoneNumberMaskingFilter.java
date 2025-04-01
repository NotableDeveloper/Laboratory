package logback_filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PhoneNumberMaskingFilter extends Filter<ILoggingEvent> {

    // 010으로 시작하는 11자리 숫자 패턴
    private static final Pattern PHONE_PATTERN = Pattern.compile("010\\d{8}");

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event == null || event.getMessage() == null) {
            return FilterReply.NEUTRAL;
        }

        String originalMessage = event.getFormattedMessage();
        Matcher matcher = PHONE_PATTERN.matcher(originalMessage);

        if (matcher.find()) {
            String maskedMessage = matcher.replaceAll("");
            System.out.println(maskedMessage);
        }

        return FilterReply.NEUTRAL;
    }
}
