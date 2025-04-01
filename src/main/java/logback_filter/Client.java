package logback_filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        User user = User.builder()
                .name("seongjin")
                .email("seongjin@abc.co.kr")
                .phoneNumber("01012345678")
                .build();

        logger.info("user = " + user);
    }
}
