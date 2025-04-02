package logback_filtering;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class PhoneNumberFilter extends Filter<ILoggingEvent> {
    private static final Pattern PHONE_PATTERN = Pattern.compile("010\\d{8}");

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event == null || event.getMessage() == null) return FilterReply.NEUTRAL;

        String originalMessage = event.getFormattedMessage();
        Matcher matcher = PHONE_PATTERN.matcher(originalMessage);

        if (matcher.find()) return FilterReply.DENY;
        else return FilterReply.NEUTRAL;
    }
}