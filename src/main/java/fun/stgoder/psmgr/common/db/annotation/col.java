package fun.stgoder.psmgr.common.db.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface col {

    public String value() default "";

    public boolean pk() default false;

    public boolean nn() default false;

    public boolean uq() default false;

    public boolean b() default false;

    public boolean un() default false;

    public boolean zf() default false;

    public boolean ai() default false;

    public int len() default 45;

    public String expr() default "";

    public String comment() default "";
}
