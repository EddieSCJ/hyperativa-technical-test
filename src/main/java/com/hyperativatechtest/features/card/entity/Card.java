package com.hyperativatechtest.features.card.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "encrypted_card_number", nullable = false, columnDefinition = "TEXT")
    private String encryptedCardNumber;

    @Column(name = "card_hash", nullable = false, unique = true, length = 64)
    private String cardHash;

    @Column(name = "lot_id", length = 50)
    private String lotId;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;
}

