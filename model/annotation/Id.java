package model.annotation;

import java.lang.annotation.*;

/** Marker: this field is the unique id of the entity. */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id { }
