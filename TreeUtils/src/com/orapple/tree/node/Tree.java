package com.orapple.tree.node;


import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) // 标识该注解只能用在实体的类型中
public @interface Tree {

    // 标识实体对象的那个属性为关联树结构的父ID
    String fatherIdField() default "fatherId";

    // 标识实体对象的那个属性为关联树结构的唯一主键ID
    String uniqueIdField() default "id";

    // 标识实体对象的那个属性为树结构的排序依据
    String orderField() default "";

    // 标识实体对象的某个属性为存储树形结构子节点的数据
    String childListField() default "children";

    // 标识树节点的root值
    String fatherRootValue() default "";

}
