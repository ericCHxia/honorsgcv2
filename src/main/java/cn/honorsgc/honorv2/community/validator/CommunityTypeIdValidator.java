package cn.honorsgc.honorv2.community.validator;

import cn.honorsgc.honorv2.community.repository.CommunityTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CommunityTypeIdValidator implements ConstraintValidator<ValidCommunityTypeId,Integer> {
    @Autowired
    private CommunityTypeRepository typeRepository;

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return integer==null||typeRepository.findById(integer).isPresent();
    }
}
