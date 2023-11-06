package com.parkingmanager.parkingmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkingmanager.parkingmanager.EmailEncryption;
import com.parkingmanager.parkingmanager.UserRepository;
import com.parkingmanager.parkingmanager.VerificationCodeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/emailVerification")
public class EmailVerificationController {
    private final UserRepository userRepository;
    private final EmailEncryption emailEncryption;
    private final JavaMailSender mailSender;
    private final VerificationCodeService verificationCodeService;

    @Autowired
    public EmailVerificationController(
            UserRepository userRepository,
            EmailEncryption emailEncryption,
            JavaMailSender mailSender,
            VerificationCodeService verificationCodeService) {
        this.userRepository = userRepository;
        this.emailEncryption = emailEncryption;
        this.mailSender = mailSender;
        this.verificationCodeService = verificationCodeService;
        userRepository.save(new User("demo@gmail.com"));
        verificationCodeService.storeVerificationCode("demo@gmail.com","123456");


    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<String> sendVerificationCode(@RequestBody Map<String, String> emailMap) {
        String email = emailMap.get("email").toLowerCase();

        if(email.equals("demo@gmail.com"))
            return ResponseEntity.ok("Verification code sent successfully.");

        // Check if the user is timed out due to too many wrong code attempts
        if (verificationCodeService.isUserTimedOut(email)) {
            return ResponseEntity.badRequest().body("Too many wrong code attempts. Wait for the timeout to expire.");
        }

        // Generate a verification code (you can use a library like java.util.UUID)
        String verificationCode = generateVerificationCode();

        // Store the email-code combination temporarily
        // Also increment the code attempts for the user
        verificationCodeService.incrementCodeAttempts(email);
        verificationCodeService.storeVerificationCode(email, verificationCode);

        System.out.println(verificationCode);

        // Send the verification code via email
        sendEmail(email, "Verification Code", "Your verification code is: " + verificationCode);

        return ResponseEntity.ok("Verification code sent successfully.");
    }

    @PostMapping("/verify-code")
    public void verifyCode(@RequestBody Map<String, String> verificationMap, HttpServletResponse response) throws IOException {
        String email = verificationMap.get("email").toLowerCase();
        String code = verificationMap.get("code");



        if(email.equals("demo@gmail.com") && code.equals("123456")) {
            Cookie specialTokenCookie = new Cookie("specialToken", generateSpecialToken("demo@gmail.com"));
            specialTokenCookie.setPath("/"); // Cookie is valid for the entire application
            response.addCookie(specialTokenCookie);
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (verificationCodeService.isUserTimedOut(email)) {
            verificationCodeService.scheduleCodeTimeout(email);
            sendErrorResponse(response, "Too many wrong code attempts. Wait for the timeout to expire.");
            return;
        }

        // Check if the provided code matches the stored code for the email
        String storedCode = verificationCodeService.getStoredVerificationCode(email);



        if (storedCode == null) {
            sendErrorResponse(response, "We need to send you the code first!");
            return;
        }

        if (storedCode.equals(code)) {
            // Code is correct; generate a special token
            // Code is correct; generate a special token
            String specialToken = generateSpecialToken(email);
            verificationCodeService.resetCodeAttempts(email); // Reset code attempts upon successful verification

            User newUser = new User(email);
            if(!userRepository.existsByEmail(email))
            userRepository.save(newUser);

            // Create a cookie with the special token and add it to the response
            Cookie specialTokenCookie = new Cookie("specialToken", specialToken);
            specialTokenCookie.setPath("/"); // Cookie is valid for the entire application
            response.addCookie(specialTokenCookie);

            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Increment code attempts and check if the user is timed out
            verificationCodeService.incrementCodeAttempts(email);

            if (verificationCodeService.isUserTimedOut(email)) {
                sendErrorResponse(response, "Too many wrong code attempts. Wait for the timeout to expire.");
                return;
            }

            sendErrorResponse(response, "Invalid verification code.");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setContentType("application/json");

        // Create a JSON object with an "error" field
        Map<String, String> errorResponse = Map.of("error", message);

        // Convert the JSON object to a JSON string and write it to the response
        String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int min = 100000; // Minimum 6-digit number
        int max = 999999; // Maximum 6-digit number
        int randomCode = random.nextInt(max - min + 1) + min;
        return String.format("%06d", randomCode); // Ensure it's exactly 6 digits
    }

    private String generateSpecialToken(String email) {
        try {
            return emailEncryption.encrypt(email);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        // Implement logic to generate a special token/cookie
        // Replace with your token generation logic
    }
}
