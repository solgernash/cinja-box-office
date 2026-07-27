package com.cinema.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Sends account emails (confirmation, password reset, profile-change notice) via Gmail SMTP.
// Credentials come from application.properties (spring.mail.*). Until a real Gmail address is
// configured there, emails are logged to the console instead of sent. Sending is wrapped so it
// can never break registration or any other feature.
@Service
public class EmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String fromAddress;
    private final String appBaseUrl;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider,
                        @Value("${spring.mail.username:}") String fromAddress,
                        @Value("${app.base-url:http://localhost:8080}") String appBaseUrl) {
        this.mailSenderProvider = mailSenderProvider;
        this.fromAddress = fromAddress;
        this.appBaseUrl = appBaseUrl;
    }

    public void sendVerificationEmail(String toEmail, String token) {
        String link = appBaseUrl + "/api/auth/verify?token=" + token;
        String html = "<h2>Welcome to Cinja Box Office</h2>"
                + "<p>Please confirm your account by clicking the link below:</p>"
                + "<p><a href=\"" + link + "\">Verify my account</a></p>"
                + "<p>If the link does not work, copy this URL into your browser:<br>" + link + "</p>";
        send(toEmail, "Confirm your Cinja Box Office account", html, link);
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String link = appBaseUrl + "/api/auth/reset-password?token=" + token;
        String html = "<h2>Password reset requested</h2>"
                + "<p>Use the link below to reset your password (valid for 1 hour):</p>"
                + "<p><a href=\"" + link + "\">Reset my password</a></p>"
                + "<p>If you did not request this, you can ignore this email.</p>"
                + "<p>Reset token: " + token + "</p>";
        send(toEmail, "Reset your Cinja Box Office password", html, link);
    }

    public void sendProfileChangedEmail(String toEmail) {
        String html = "<h2>Your profile was updated</h2>"
                + "<p>This is a confirmation that your Cinja Box Office profile information "
                + "was recently changed. If this was not you, please reset your password.</p>";
        send(toEmail, "Your Cinja Box Office profile was updated", html, null);
    }

    /*
     * Order confirmation sent at checkout. Builds an HTML summary from the
     * checkout summary map (movie, showtime, seats, tickets, total before tax).
     */
    public void sendCheckoutConfirmationEmail(String toEmail, Map<String, Object> summary, String bookingNumber) {
        StringBuilder html = new StringBuilder();
        html.append("<h2>Your Cinja Box Office order</h2>");
        html.append("<p>Thank you for your order! Here is your summary.</p>");
        html.append("<p><strong>Booking #:</strong> ").append(bookingNumber).append("</p>");
        html.append("<p><strong>Movie:</strong> ").append(summary.get("movieTitle")).append("</p>");
        html.append("<p><strong>Showtime:</strong> ").append(summary.get("showDate"))
                .append(" ").append(summary.get("showTime"))
                .append(" (Showroom ").append(summary.get("showroomNumber")).append(")</p>");

        html.append("<p><strong>Seats:</strong> ");
        if (summary.get("selectedSeats") instanceof List<?> seatList) {
            List<String> parts = new ArrayList<>();
            for (Object seat : seatList) {
                parts.add(String.valueOf(seat));
            }
            html.append(String.join(", ", parts));
        }
        html.append("</p>");

        html.append("<p><strong>Tickets:</strong></p><ul>");
        if (summary.get("tickets") instanceof List<?> lines) {
            for (Object lineObj : lines) {
                if (lineObj instanceof Map<?, ?> line) {
                    html.append("<li>").append(line.get("count")).append(" x ").append(line.get("type"))
                            .append(" @ $").append(line.get("pricePerTicket"))
                            .append(" = $").append(line.get("subtotal")).append("</li>");
                }
            }
        }
        html.append("</ul>");
        html.append("<p><strong>Total (before tax):</strong> $").append(summary.get("totalBeforeTax")).append("</p>");
        html.append("<p>Payment is processed on the payment page.</p>");

        send(toEmail, "Your Cinja Box Office order confirmation", html.toString(), null);
    }

    // Configured once a real Gmail address (containing "@") is set in application.properties.
    private boolean isConfigured() {
        return fromAddress != null && fromAddress.contains("@");
    }

    /*
     * Sends the email via Gmail SMTP when configured, otherwise logs it.
     * Never throws: a failed email must not break registration/profile updates.
     */
    private void send(String to, String subject, String html, String link) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (!isConfigured() || mailSender == null) {
            System.out.println("========== [EMAIL - not sent, mail not configured] ==========");
            System.out.println("To:      " + to);
            System.out.println("Subject: " + subject);
            if (link != null) {
                System.out.println("Link:    " + link);
            }
            System.out.println("==============================================================");
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true); // true = send as HTML
            mailSender.send(message);
            System.out.println("[EMAIL] Sent to " + to + " (\"" + subject + "\")");
        } catch (Exception e) {
            System.out.println("[EMAIL] Error sending to " + to + ": " + e.getMessage());
        }
    }
}
