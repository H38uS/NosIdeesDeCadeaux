package com.mosioj.ideescadeaux.core.model.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class IdeaGroup {

    private final double total;
    private final int id;
    private final List<Share> shares = new ArrayList<>();

    public IdeaGroup(int id, double d) {
        this.id = id;
        this.total = d;
    }

    public double getTotal() {
        return total;
    }

    public String getTotalAmount() {
        return String.format("%1$,.2f", total);
    }

    public int getId() {
        return id;
    }

    public List<Share> getShares() {
        return shares;
    }

    public void addUser(User user, double d) {
        shares.add(new Share(user, d));
    }

    /**
     * @param user The user.
     * @return true if and only if one of the share contains this user.
     */
    public boolean contains(User user) {
        // Gets all users
        List<User> users = getShares().stream().map(Share::getUser).collect(Collectors.toList());
        return users.contains(user);
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
