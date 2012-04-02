package edacc.model;

import java.io.Serializable;

/**
 *
 * @author simon
 */
public class Cost extends BaseModel implements IntegerPKModel, Serializable {

    private int id;
    private String name;

    protected Cost(int id, String name) {
        this.id = id;
        this.name = name;
        setNew();
    }

    public Cost() {
        this.id = -1;
        setNew();
    }

    @Override
    public int getId() {
        return id;
    }

    public void setName(String name) {
        if (isSaved() && (name == null ? this.name != null : !name.equals(this.name))) {
            setModified();
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }
}
