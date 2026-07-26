package com.cinema.controller;

import com.cinema.model.User;
import com.cinema.repository.UserRepository;
import com.cinema.service.CheckoutService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

/*
 * Checkout: order summary + (mock) payment page.
 *
 * Login is REQUIRED to proceed to checkout (seats may be selected while logged
 * out; the session keeps the selection through the login step). The order
 * summary shows movie, showtime, seats, ticket counts/types, price per ticket
 * and total before tax. Real payment/confirmation is a later deliverable.
 * Base URL: /api/checkout
 */
@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(
        originPatterns = {"http://localhost:*", "http://127.0.0.1:*", "null"},
        allowCredentials = "true"
)
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final UserRepository userRepository;

    public CheckoutController(CheckoutService checkoutService, UserRepository userRepository) {
        this.checkoutService = checkoutService;
        this.userRepository = userRepository;
    }

    // GET /api/checkout/summary  -> order summary (requires login)
    @GetMapping("/summary")
    public Map<String, Object> summary(HttpSession session) {
        String userId = requireLogin(session);
        String showId = (String) session.getAttribute("bk_showId");
        List<String> seatIds = seatIds(session);
        checkoutService.requireSelection(showId, seatIds);

        return checkoutService.buildSummary(showId,
                intAttr(session, "bk_adult"), intAttr(session, "bk_senior"), intAttr(session, "bk_child"),
                seatIds, emailFor(userId));
    }

    // POST /api/checkout/proceed  -> confirm email, create pending booking, show payment mockup
    @PostMapping("/proceed")
    public Map<String, Object> proceed(@RequestBody(required = false) ProceedRequest request,
                                       HttpSession session) {
        String userId = requireLogin(session);
        String showId = (String) session.getAttribute("bk_showId");
        List<String> seatIds = seatIds(session);
        checkoutService.requireSelection(showId, seatIds);

        int adult = intAttr(session, "bk_adult");
        int senior = intAttr(session, "bk_senior");
        int child = intAttr(session, "bk_child");

        // The user may confirm their existing email or enter a new one.
        String email = (request != null && request.email() != null && !request.email().isBlank())
                ? request.email().trim()
                : emailFor(userId);

        Map<String, Object> summary =
                checkoutService.buildSummary(showId, adult, senior, child, seatIds, email);
        double total = ((Number) summary.get("totalBeforeTax")).doubleValue();

        return checkoutService.proceedToPayment(userId, showId, total, email);
    }

    // ---------------- Helpers ----------------

    private String requireLogin(HttpSession session) {
        Object userId = session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please log in to proceed to checkout");
        }
        return userId.toString();
    }

    private String emailFor(String userId) {
        return userRepository.findById(userId).map(User::getEmail).orElse(null);
    }

    @SuppressWarnings("unchecked")
    private List<String> seatIds(HttpSession session) {
        Object value = session.getAttribute("bk_seatIds");
        return value instanceof List ? (List<String>) value : null;
    }

    private int intAttr(HttpSession session, String key) {
        Object value = session.getAttribute(key);
        return value instanceof Integer ? (Integer) value : 0;
    }

    public record ProceedRequest(String email) {
    }
}
