package com.money.api.validator;

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
import org.springframework.util.ObjectUtils;

public class UniqueConstraintValidator  implements ConstraintValidator<Unique, Object> {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	private String[] attributes;
	private String[] associations;
	private String[] associationsKeys;
	private String node;
	private String key;
	private String message;

	@Override
	public void initialize(final Unique unique) {
		
		this.attributes = unique.attributes();
		this.associations = unique.associations();
		this.associationsKeys = unique.associationsKeys();
		this.node = unique.node();
		this.key = unique.key();
		this.message = unique.message();
	}

	@Override
	public boolean isValid(final Object object, final ConstraintValidatorContext context) {
		
		if(ObjectUtils.isEmpty(this.entityManager))
			
            return true;
		
		try {
			
            Class<? extends Object> cls = object.getClass();
            
            String id = BeanUtils.getProperty(object, this.key);
            
            CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
            
            CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
            
            Root<? extends Object> root = criteria.from(cls);
            
            Predicate[] predicates = createRestrictions(object, id, builder, root);
            
            if(predicates.length == 0)
            	
            	return true;
            
            criteria.select(builder.count(root)).where(predicates);
            
            Long result = this.entityManager.createQuery(criteria).getSingleResult();
            
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(this.message).addPropertyNode(this.node).addConstraintViolation();
            
            return result.equals(0L);

        } catch (Exception e) {
			
        	e.printStackTrace();
        }
		
		return true;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Predicate[] createRestrictions(Object object, String id, CriteriaBuilder builder, Root<? extends Object> root) throws Exception {
		
		List<Predicate> predicates = new ArrayList<>();
        
        for (String attribute : this.attributes) {
        	
        	if(ObjectUtils.isEmpty(BeanUtils.getProperty(object, attribute))) {
        		
        		predicates.clear();
        		
        		return predicates.toArray(new Predicate[predicates.size()]);
        	}
        	
        	boolean attributeHasAssociation = false;
        	
        	if(!ObjectUtils.isEmpty(attribute)) {
        		
        		if (this.associations.length > 0) {
        			
        			int index = 0;
        			
        			for (String association : this.associations) {
        				
        				if(!ObjectUtils.isEmpty(BeanUtils.getProperty(object, association))) {
        					
        					if (attribute.equals(association)) {
        						
        						String associationKey = key;
        						
        						if (this.associationsKeys.length > 0)
        							
        							associationKey = this.associationsKeys[index];
        						
        						predicates.add(builder.equal(root.get(association).get(associationKey), BeanUtils.getProperty(object, association + "." + associationKey)));
        						
        						attributeHasAssociation = true;
        					}
        					
        					index++;
        				}
        			}
        		}
    			
        		if (!attributeHasAssociation) {
        			
        			if (root.get(attribute).getJavaType().isEnum())
        					
        				predicates.add(builder.equal(root.get(attribute), Enum.valueOf((Class<Enum>)root.get(attribute).getJavaType(), BeanUtils.getProperty(object, attribute))));
        				
        			else
        				
        				predicates.add(builder.equal(root.get(attribute), BeanUtils.getProperty(object, attribute)));
        		}
        	}
        }

        if(!ObjectUtils.isEmpty(id))
        	
            predicates.add(builder.notEqual(root.get(this.key), id));
        
        return predicates.toArray(new Predicate[predicates.size()]);
	}
}
