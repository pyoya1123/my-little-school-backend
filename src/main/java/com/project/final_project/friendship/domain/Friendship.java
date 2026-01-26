package com.project.final_project.friendship.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="friendship")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "requester_id")
    private Integer requesterId;

    @Column(name = "receiver_id")
    private Integer receiverId;

    @Column(name = "request_message")
    private String message;

    private boolean isAccepted = false;

    @Override
    public String toString() {
        return "Friendship{" +
            "id=" + id +
            ", requesterId=" + requesterId +
            ", receiverId=" + receiverId +
            ", isAccepted=" + isAccepted +
            '}';
    }
}
