package com.hyperativatechtest.features.card.repository;

import com.hyperativatechtest.features.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, String> {

    Optional<Card> findByCardHash(String cardHash);

    boolean existsByCardHash(String cardHash);
}

