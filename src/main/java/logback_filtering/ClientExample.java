package logback_filtering;

import logback_filtering.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientExample {
    private static final Logger logger = LoggerFactory.getLogger(ClientExample.class);

    public static void main(String[] args) {
        User user1 = User.builder()
                    .name("홍길동")
                    .email("abcd@abc.co.kr")
                    .phoneNumber("01012345678")
                    .build();

        User user2 = User.builder()
                    .name("김철수")
                    .email("efgh@abc.co.kr")
                    .phoneNumber("01056781234")
                    .build();

        logger.info("user1 = " + user1);
        logger.info("user2 = " + user2);
    }
}
