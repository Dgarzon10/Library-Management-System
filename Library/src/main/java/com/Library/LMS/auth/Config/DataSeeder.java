package com.Library.LMS.auth.Config;

import com.Library.LMS.Persistence.Entity.Role;
import com.Library.LMS.Persistence.Entity.UserEntity;
import com.Library.LMS.Persistence.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {


    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


// Creates an ADMIN user at application startup if no ADMIN user exists.
// Ensures the presence of an ADMIN user from the beginning.

    @Override
    public void run(String... args) {


        if (userRepository.findByRole(Role.ADMIN).isEmpty()) {
            UserEntity adminUser = UserEntity.builder()
                    .name("Default Admin")
                    .email("admin@example.com")
                    .libraryId("ADMIN")
                    .password(passwordEncoder.encode("securePassword123"))
                    .role(Role.ADMIN)
                    .build();

            userRepository.save(adminUser);
            System.out.println("Admin is initialized with credentials: admin@example.com / securePassword123");
        }
    }
}
