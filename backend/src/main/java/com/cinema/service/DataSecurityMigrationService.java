package com.cinema.service;

import com.cinema.model.PaymentCard;
import com.cinema.model.User;
import com.cinema.repository.PaymentCardRepository;
import com.cinema.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/*
 * One-time-per-startup security pass over the database.
 *
 * The DataSeeder (owned by a teammate) inserts sample accounts and cards with
 * PLAINTEXT passwords and card numbers. Rather than editing that file, this
 * service runs AFTER startup (ApplicationReadyEvent, which fires after all
 * CommandLineRunners including the seeder) and:
 *   - hashes any password that is not already hashed
 *   - encrypts any card number that is not already encrypted
 *
 * It is idempotent: once values are hashed/encrypted, later runs skip them.
 * This lets seeded demo accounts log in AND satisfies the security requirement
 * that stored passwords are hashed and card data encrypted (TC10).
 */
@Service
public class DataSecurityMigrationService {

    private final UserRepository userRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final PasswordService passwordService;
    private final EncryptionService encryptionService;

    public DataSecurityMigrationService(UserRepository userRepository,
                                        PaymentCardRepository paymentCardRepository,
                                        PasswordService passwordService,
                                        EncryptionService encryptionService) {
        this.userRepository = userRepository;
        this.paymentCardRepository = paymentCardRepository;
        this.passwordService = passwordService;
        this.encryptionService = encryptionService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void secureExistingData() {
        int hashed = 0;
        for (User user : userRepository.findAll()) {
            if (!passwordService.isHashed(user.getPasswordHash())) {
                user.setPasswordHash(passwordService.hash(user.getPasswordHash()));
                userRepository.save(user);
                hashed++;
            }
        }

        int encrypted = 0;
        for (PaymentCard card : paymentCardRepository.findAll()) {
            if (!encryptionService.isEncrypted(card.getCardNumber())) {
                card.setCardNumber(encryptionService.encrypt(card.getCardNumber()));
                paymentCardRepository.save(card);
                encrypted++;
            }
        }

        if (hashed > 0 || encrypted > 0) {
            System.out.println("[SecurityMigration] Hashed " + hashed
                    + " password(s), encrypted " + encrypted + " card number(s).");
        }
    }
}
