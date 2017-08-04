package br.gov.caraguatatuba.ocorrencias.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = UniqueConstraintValidator.class)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {

	String message() default "{Unique}";
    
	Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    String[] attributes() default {};
    
    String node() default "";
    
    String key() default "id";
    
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
    	
        Unique[] value();
    }
}
