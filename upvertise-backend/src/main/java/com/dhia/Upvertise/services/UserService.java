package com.dhia.Upvertise.services;
//@Service
//@Slf4j
//@RequiredArgsConstructor
public class UserService {
//    private final UserRepository userRepository;
//
//    @Transactional
//    public void createUser(UserEventDTO event) {
//        // Check if user already exists
//        if (userRepository.existsById(event.getUserId())) {
//            log.warn("User already exists: {}", event.getUserId());
//            return;
//        }
//
//        User user = User.builder()
//                .id(event.getUserId())
//                .email(event.getEmail())
//                .username(event.getUsername())
//                .firstName(event.getFirstName())
//                .lastName(event.getLastName())
//                .createdAt(new Date(event.getTimestamp()))
//                .build();
//
//        userRepository.save(user);
//        log.info("Created new user: {}", event.getEmail());
//    }
//
//    @Transactional
//    public void updateLastLogin(String userId) {
//        userRepository.findById(userId).ifPresent(user -> {
//            user.setLastLoginAt(new Date());
//            userRepository.save(user);
//            log.info("Updated last login for user: {}", userId);
//        });
//    }
//
//    @Transactional
//    public void updateUser(UserEventDTO event) {
//        userRepository.findById(event.getUserId()).ifPresent(user -> {
//            user.setEmail(event.getEmail());
//            user.setUsername(event.getUsername());
//            user.setFirstName(event.getFirstName());
//            user.setLastName(event.getLastName());
//            user.setUpdatedAt(new Date());
//            userRepository.save(user);
//            log.info("Updated user profile: {}", event.getUserId());
//        });
//    }
}
