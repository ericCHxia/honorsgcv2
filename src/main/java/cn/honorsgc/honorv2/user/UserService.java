package cn.honorsgc.honorv2.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    public UserRepository repository() {
        return repository;
    }

    @Override
    public User loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = repository.getByUserId(s);
        if (user==null) {
            throw new UsernameNotFoundException("学号不存在");
        }
        return user;
    }

    public static void flushUser(User user){
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }
}
