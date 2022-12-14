package com.mosioj.ideescadeaux.core.model.entities.text;

import com.google.gson.annotations.Expose;
import com.mosioj.ideescadeaux.core.model.entities.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity(name = "QUESTIONS")
public class Question extends EntityWithText {

    /** Internal id */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    protected int id;

    /** The idea on which this question is written */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id")
    private Idee idea;

    /** The user that have written this comment */
    @ManyToOne
    @JoinColumn(name = "written_by")
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
     * @return The new created question.
     */
    public static Question getIt(User user, Idee idea, String text) {
        Question question = new Question();
        question.writtenBy = user;
        question.idea = idea;
        question.setText(text);
        return question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        return new EqualsBuilder().append(id, question.id)
                                  .append(writtenBy, question.writtenBy)
                                  .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).append(writtenBy).toHashCode();
    }
}
