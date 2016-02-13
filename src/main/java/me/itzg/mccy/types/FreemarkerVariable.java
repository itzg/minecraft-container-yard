package me.itzg.mccy.types;

/**
 * @author Geoff Bourne
 * @since 0.2
 */
public class FreemarkerVariable {
    private String name;

    private Object value;

    public FreemarkerVariable(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
