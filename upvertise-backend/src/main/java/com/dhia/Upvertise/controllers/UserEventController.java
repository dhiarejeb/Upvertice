package com.dhia.Upvertise.controllers;
//@RestController
//@RequestMapping("/users")
//@Slf4j
//@RequiredArgsConstructor
//public class UserEventController {
//    private final UserService userService;
//    private final EmailService emailService;
//
//    @PostMapping("/register")
//    public ResponseEntity<Void> handleRegistration(@RequestBody UserEventDTO event) {
//        log.info("Received registration event for user: {}", event.getEmail());
//        try {
//            // Create user in your database
//            userService.createUser(event);
//
//            // Send welcome email
//            emailService.sendWelcomeEmail(event);
//
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            log.error("Error handling registration", e);
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<Void> handleLogin(@RequestBody UserEventDTO event) {
//        log.info("Received login event for user: {}", event.getUserId());
//        try {
//            userService.updateLastLogin(event.getUserId());
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            log.error("Error handling login", e);
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    @PostMapping("/update")
//    public ResponseEntity<Void> handleProfileUpdate(@RequestBody UserEventDTO event) {
//        log.info("Received profile update event for user: {}", event.getUserId());
//        try {
//            userService.updateUser(event);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            log.error("Error handling profile update", e);
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//}
