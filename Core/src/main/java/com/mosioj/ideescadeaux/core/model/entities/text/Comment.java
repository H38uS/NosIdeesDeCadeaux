package com.mosioj.ideescadeaux.core.model.entities.text;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.User;
import com.mosioj.ideescadeaux.core.utils.date.MyDateFormatViewer;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity(name = "COMMENTS")
public class Comment extends EntityWithText {

    /** Internal id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private int id;


    /** The idea on which this question is written */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id")
    private Idee idea;

    /** The user that have written this comment */
    @ManyToOne
    @JoinColumn(name = "written_by")
    @Expose
    private User writtenBy;

    /** When this question was first created. */
    @Column(updatable = false, name = "written_on")
    @CreationTimestamp
    @Expose
    private LocalDateTime creationDate;

    /** Last time this question was updated. */
    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Transient
    @Expose
    private String lastEditedOn;

    @PostLoad
    public void postLoad() {
        this.lastEditedOn = MyDateFormatViewer.formatMine(getUpdatedAt().or(this::getCreationDate).orElse(null));
    }

    /**
     * @return The question identifier.
     */
    public int getId() {
        return id;
    }

    /**
     * @return The user who wrote this message.
     */
    public User getWrittenBy() {
        return writtenBy;
    }

    /**
     * @return The question's idea.
     */
    public Idee getIdea() {
        return idea;
    }

    /**
     * @return When this message was created.
     */
    public Optional<LocalDateTime> getCreationDate() {
        return Optional.ofNullable(creationDate);
    }

    /**
     * @return When this message was last updated.
     */
    public Optional<LocalDateTime> getUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    /**
     * @param user The user who posted a new question/answer.
     * @param idea The idea on which this question is posted.
     * @param text The message text.
     * @return The new created comment.
     */
    public static Comment getIt(User user, Idee idea, String text) {
        Comment comment = new Comment();
        comment.writtenBy = user;
        comment.idea = idea;
        comment.setText(text);
        return comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        return new EqualsBuilder().append(id, comment.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }
}
