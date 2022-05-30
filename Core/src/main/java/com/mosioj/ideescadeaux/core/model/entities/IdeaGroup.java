package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity(name = "GROUP_IDEA")
public class IdeaGroup {

    /** The table's id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private int id;

    @Column(name = "needed_price")
    @Expose
    public double total;

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER)
    @Expose
    private Set<IdeaGroupContent> ideaGroupContents = new HashSet<>();

    @Transient
    @Expose
    private String formattedTotal;

    public IdeaGroup() {
        // For Hibernate
    }

    public IdeaGroup(int id, double total) {
        this.id = id;
        this.total = total;
    }

    @PostLoad
    private void postLoad() {
        this.formattedTotal = String.format("%1$,.2f", total);
    }

    public int getId() {
        return id;
    }

    public Set<IdeaGroupContent> getShares() {
        return ideaGroupContents;
    }

    /**
     * @param user The user.
     * @return true if and only if one of the share contains this user.
     */
    public boolean contains(User user) {
        return getShares().stream().map(IdeaGroupContent::getUser).anyMatch(u -> u.equals(user));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdeaGroup ideaGroup = (IdeaGroup) o;
        return id == ideaGroup.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
