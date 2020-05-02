package com.mosioj.ideescadeaux.core.model.entities;

import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;

import java.sql.Timestamp;

public class Share {

    private final User user;
    private final double amount;
    private final Timestamp joinDate;

    public Share(User user, double d, Timestamp joinDate) {
        this.user = user;
        this.amount = d;
        this.joinDate = joinDate;
    }

    public User getUser() {
        return user;
    }

    public double getAmount() {
        return amount;
    }

    public String getShareAmount() {
        return String.format("%1$,.2f", amount);
    }

    public String getJoinDate() {
        return MyDateFormatViewer.formatMine(joinDate);
    }
}