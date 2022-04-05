package cn.honorsgc.honorv2.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService service;
    @Autowired
    private UserRepository repository;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUserId("1");
        user.setPassword("f25a2fc72690b780b2a14e140ef6a9e0");
        user.setName("甲");
        user.setPrivilege(0);
        user.setClassId("1");
        user.setAvatar("");
        user.setPhone("");
        user.setCollege("卓越学院");
        user.setSubject("无专业");
        repository.save(user);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void loadUserByUsername() {
        assertDoesNotThrow(()->{
            User user=service.loadUserByUsername("1");
            assertEquals(user.getName(),"甲");
        });

        assertThrows(UsernameNotFoundException.class,()->service.loadUserByUsername("-1"));
    }
}