package logback_filtering;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        User user1 = User.builder()
                    .name("seongjin")
                    .email("seongjin@abc.co.kr")
                    .phoneNumber("01012345678")
                    .build();

        User user2 = User.builder()
                    .name("seongjin")
                    .email("seongjin@abc.co.kr")
                    .phoneNumber("None")
                    .build();

        logger.info("user1 = " + user1);
        logger.info("user2 = " + user2);
    }
}
