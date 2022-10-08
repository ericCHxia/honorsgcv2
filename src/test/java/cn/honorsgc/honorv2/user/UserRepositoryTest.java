package cn.honorsgc.honorv2.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
class UserRepositoryTest {
    @Autowired
    UserRepository repository;
    @BeforeEach
    public void prepare() {
        User user = new User();
        user.setName("A");
        user.setUserId("1");
        user.setCollege("1");
        user.setSubject("S1");
        user.setAvatar("");
        user.setPhone("");
        repository.save(user);
        user.setId(null);
        user.setUserId("2");
        user.setCollege("1");
        user.setSubject("S2");
        repository.save(user);
        user.setId(null);
        user.setUserId("3");
        user.setCollege("3");
        user.setSubject("S1");
        repository.save(user);
        repository.flush();
    }
    @AfterEach
    public void clean() {
        repository.deleteAll();
    }
    @Test
    void getCollegeNames() {
        Set<String> names = repository.getCollegeNames();
        Set<String> expected = new HashSet<>();
        expected.add("1");
        expected.add("3");
        assertArrayEquals(names,expected);
    }

    private void assertArrayEquals(Set<String> names, Set<String> expected) {
        assertEquals(names.size(),expected.size());
        assertTrue(names.containsAll(expected));
    }

    @Test
    void getSubjectNames() {
        Set<String> names = repository.getSubjectNames();
        Set<String> expected = new HashSet<>();
        expected.add("S1");
        expected.add("S2");
        assertArrayEquals(names,expected);
    }
}