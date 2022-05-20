package com.mosioj.ideescadeaux.core.model.entities;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "GROUP_IDEA_CONTENT",
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "user_id"}))
public class IdeaGroupContent {

    /** The table's id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_content;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private IdeaGroup group;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Expose
    private User user;

    @Column(name = "price")
    public double amount;

    @Column(name = "join_date")
    @UpdateTimestamp
    private LocalDateTime joinDate;

    @Expose
    private String formattedAmount;

    @Expose
    private String formattedDate;

    public IdeaGroupContent() {
    }

    public IdeaGroupContent(IdeaGroup group, User user, double amount) {
        this.group = group;
        this.user = user;
        this.amount = amount;
    }

    @PostLoad
    private void postLoad() {
        this.formattedAmount = String.format("%1$,.2f", amount);
        this.formattedDate = MyDateFormatViewer.formatMine(joinDate);
    }

    /**
     * @return The user that holds this group participation.
     */
    public User getUser() {
        return user;
    }

    /**
     * @return The amount of this group participation.
     */
    public double getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        IdeaGroupContent that = (IdeaGroupContent) o;

        return new EqualsBuilder().append(group, that.group)
                                  .append(user, that.user)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(group).append(user).toHashCode();
    }
}