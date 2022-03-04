package cn.honorsgc.honorv2.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserConvert {
    @Autowired
    private UserRepository repository;

    public List<User> toUser(List<Long> ids){
        return repository.findAllById(ids);
    }

    public User toUser(Long id){
        return repository.findById(id).orElse(null);
    }
}
