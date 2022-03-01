package cn.honorsgc.honorv2.community.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CommunityTypeIdValidator.class)
public @interface ValidCommunityTypeId {
    String message() default "共同体类型不存在";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
