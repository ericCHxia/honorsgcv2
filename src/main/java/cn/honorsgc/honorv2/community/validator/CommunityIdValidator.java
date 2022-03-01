package cn.honorsgc.honorv2.community.validator;

import cn.honorsgc.honorv2.community.repository.CommunityRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CommunityIdValidator  implements ConstraintValidator<ValidCommunityId,Long> {
    @Autowired
    private CommunityRepository repository;
    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintValidatorContext) {
        return id==null||repository.findById(id).isPresent();
    }
}
