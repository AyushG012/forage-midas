package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import jakarta.persistence.*;


@Entity
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipient;

    private float amount;

    private float incentive; // ✅ new field for incentive amount


    public TransactionEntity() {
    }

    public TransactionEntity(UserRecord sender, UserRecord recipient, float amount, float incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = incentive;
    }
}