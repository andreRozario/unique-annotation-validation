package br.gov.caraguatatuba.ocorrencias.validators;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.util.StringUtils;

public class UniqueConstraintValidator implements ConstraintValidator<Unique, Object> {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	private String[] attributes;
	private String node;
	private String key;
	private String message;

	@Override
	public void initialize(final Unique unique) {
		
		this.attributes = unique.attributes();
		this.node = unique.node();
		this.key = unique.key();
		this.message = unique.message();
	}

	@Override
	public boolean isValid(final Object object, final ConstraintValidatorContext context) {
		
		if(StringUtils.isEmpty(object)) {
			
            return true;
        }
		
		try {
			
            Class<? extends Object> cls = object.getClass();
            
            String id = BeanUtils.getProperty(object, key);
            
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            
            Root<? extends Object> root = criteria.from(cls);
            
            List<Predicate> predicates = new ArrayList<>();
            
            for (String attribute : attributes) {
            	
            	if(!StringUtils.isEmpty(attribute))
            		predicates.add(builder.equal(root.get(attribute), BeanUtils.getProperty(object, attribute)));
            }

            if(!StringUtils.isEmpty(id))
                predicates.add(builder.notEqual(root.get(key), id));
            
            criteria.select(builder.count(root)).where(predicates.toArray(new Predicate[predicates.size()]));
            
            Long result = entityManager.createQuery(criteria).getSingleResult();
            
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(this.message).addNode(this.node).addConstraintViolation();
            
            return result == 0;

        } catch (Exception e) {
			
        	if(!StringUtils.isEmpty(entityManager))
        		e.printStackTrace();
            
        } finally {
			
            if (!StringUtils.isEmpty(entityManager))
            	entityManager.close();
        }
		
		return true;

	}

}
