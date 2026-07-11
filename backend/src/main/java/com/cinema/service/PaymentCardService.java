package com.cinema.service;

import com.cinema.model.Customer;
import com.cinema.model.PaymentCard;
import com.cinema.model.User;
import com.cinema.repository.PaymentCardRepository;
import com.cinema.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
 * Manages a customer's stored payment cards.
 *
 * Security / rules:
 *  - Card numbers are encrypted (EncryptionService) before storage.
 *  - Card numbers are only ever returned masked (last 4 digits).
 *  - A customer may store at most 3 payment cards (rubric constraint).
 */
@Service
public class PaymentCardService {

    private static final int MAX_CARDS = 3;

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    public PaymentCardService(PaymentCardRepository paymentCardRepository,
                              UserRepository userRepository,
                              EncryptionService encryptionService) {
        this.paymentCardRepository = paymentCardRepository;
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }

    public Map<String, Object> addCard(String userId, String cardNumber,
                                       String cardholderName, String expirationDate) {
        Customer customer = requireCustomer(userId);
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card number is required");
        }

        List<PaymentCard> existing = paymentCardRepository.findByCustomerUser_ID(userId);
        if (existing.size() >= MAX_CARDS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You cannot store more than " + MAX_CARDS + " payment cards");
        }

        PaymentCard card = new PaymentCard(null,
                encryptionService.encrypt(cardNumber.replaceAll("\\s", "")),
                cardholderName, expirationDate);
        card.setCustomer(customer);
        return toMasked(paymentCardRepository.save(card));
    }

    public List<Map<String, Object>> listCards(String userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (PaymentCard card : paymentCardRepository.findByCustomerUser_ID(userId)) {
            result.add(toMasked(card));
        }
        return result;
    }

    public Map<String, Object> updateCard(String userId, String cardId,
                                          String cardNumber, String cardholderName, String expirationDate) {
        PaymentCard card = requireOwnedCard(userId, cardId);
        if (cardNumber != null && !cardNumber.isBlank()) {
            card.setCardNumber(encryptionService.encrypt(cardNumber.replaceAll("\\s", "")));
        }
        if (cardholderName != null && !cardholderName.isBlank()) {
            card.setCardholderName(cardholderName);
        }
        if (expirationDate != null && !expirationDate.isBlank()) {
            card.setExpirationDate(expirationDate);
        }
        return toMasked(paymentCardRepository.save(card));
    }

    public void deleteCard(String userId, String cardId) {
        PaymentCard card = requireOwnedCard(userId, cardId);
        paymentCardRepository.delete(card);
    }

    // ---------------- Helpers ----------------

    private Customer requireCustomer(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!(user instanceof Customer customer)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only customers can store payment cards");
        }
        return customer;
    }

    private PaymentCard requireOwnedCard(String userId, String cardId) {
        PaymentCard card = paymentCardRepository.findById(cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment card not found"));
        if (card.getCustomer() == null || !userId.equals(card.getCustomer().getUser_ID())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This card does not belong to you");
        }
        return card;
    }

    // Never expose the real (decrypted) number to clients.
    private Map<String, Object> toMasked(PaymentCard card) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", card.getPaymentCardID());
        map.put("cardholderName", card.getCardholderName());
        map.put("expirationDate", card.getExpirationDate());
        map.put("cardNumber", encryptionService.mask(card.getCardNumber()));
        return map;
    }
}
