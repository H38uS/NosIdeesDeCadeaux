package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IdeaGroup {

    @Expose
    private final int id;

    @Expose
    private final double total;

    @Expose
    private final String formattedTotal;

    @Expose
    private final List<Share> shares = new ArrayList<>();

    public IdeaGroup(int id, double d) {
        this.id = id;
        this.total = d;
        this.formattedTotal = String.format("%1$,.2f", total);
    }

    public double getTotal() {
        return total;
    }

    public String getTotalAmount() {
        return formattedTotal;
    }

    public int getId() {
        return id;
    }

    public List<Share> getShares() {
        return shares;
    }

    public void addUser(User user, double d, Timestamp timestamp) {
        shares.add(new Share(user, d, timestamp));
    }

    /**
     * @param user The user.
     * @return true if and only if one of the share contains this user.
     */
    public boolean contains(User user) {
        return getShares().stream().map(Share::getUser).anyMatch(u -> u.equals(user));
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

}
