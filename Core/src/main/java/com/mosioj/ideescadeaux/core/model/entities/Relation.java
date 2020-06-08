package com.mosioj.ideescadeaux.core.model.entities;

public class Relation {

    private final User first;
    private final User second;

    /**
     * @param first  One user of this relation.
     * @param second The other user of this relation.
     */
    public Relation(User first, User second) {
        this.first = first;
        this.second = second;
    }

    public User getFirst() {
        return first;
    }

    public User getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "Relation [first=" + first + ", second=" + second + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Relation))
            return false;
        Relation other = (Relation) obj;
        if (second == null) {
            return other.second == null;
        } else return second.equals(other.second);
    }

}
