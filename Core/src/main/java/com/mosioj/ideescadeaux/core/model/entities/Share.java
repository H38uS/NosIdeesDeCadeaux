package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;

import java.time.Instant;

public class Share {

    @Expose
    private final User user;

    @Expose
    private final String formattedAmount;

    @Expose
    private final String formattedDate;

    private final double amount;

    public Share(User user, double d, Instant joinDate) {
        this.user = user;
        this.amount = d;
        this.formattedAmount = String.format("%1$,.2f", amount);
        this.formattedDate = MyDateFormatViewer.formatMine(joinDate);
    }

    public User getUser() {
        return user;
    }

    public double getAmount() {
        return amount;
    }
}