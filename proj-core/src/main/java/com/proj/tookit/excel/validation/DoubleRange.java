package com.proj.tookit.excel.validation;

/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated element has to be in the appropriate range. Apply on numeric values or string
 * representation of the numeric value.
 *
 * @author pan
 */
@Target({FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {DoubleRange.DoubleRangeValidator.class})
public @interface DoubleRange {

    double min() default 0;

    double max() default Double.MAX_VALUE;

    String message() default "{org.hibernate.validator.constraints.Range.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * 必须实现 ConstraintValidator接口
     */
    class DoubleRangeValidator implements ConstraintValidator<DoubleRange, Double> {
        double max;
        double min;

        /**
         * 校验逻辑的实现
         * @param value 需要校验的 值
         * @return 布尔值结果
         */
        @Override
        public boolean isValid(Double value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }

            if(value > max || value < min){
                return false;
            }

            return true;
        }

        @Override
        public void initialize(DoubleRange constraintAnnotation) {
            this.max = constraintAnnotation.max();
            this.min = constraintAnnotation.min();
        }
    }
}

