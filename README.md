# unique-annotation-validation

Unique annotation validation for Java

##Single attribute example:
```java
imports...

import package.validators.Unique;

@Entity
@Table(name = "foo", uniqueConstraints = {
	@UniqueConstraint(columnNames = "bar"),
})
@Unique.List({
    @Unique(attributes = {"bar"}, node = "bar", message = "{Unique.foo.bar}"),
})
public class Foo implements Serializable {

	@Transient
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(unique = true)
	private String bar;
	
	getters and setters...

	equals and hashcode...
}
```
##Multiples attributes example:
```java
imports...

import package.validators.Unique;

@Entity
@Table(name = "foo", uniqueConstraints = {
	@UniqueConstraint(columnNames = "bar", "foo_bar"),
})
@Unique.List({
    @Unique(attributes = {"bar", "foo_bar"}, node = "bar", message = "{Unique.foo.bar}"),
})
public class Foo implements Serializable {

	@Transient
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(unique = true)
	private String bar;

	@Column(name = "foo_bar", unique = true)
	private String fooBar;
	
	getters and setters...

	equals and hashcode...
}
```
