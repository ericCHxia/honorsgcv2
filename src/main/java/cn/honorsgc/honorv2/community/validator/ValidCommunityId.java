package cn.honorsgc.honorv2.community.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CommunityIdValidator.class)
public @interface ValidCommunityId {
    String message() default "共同体不存在";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
