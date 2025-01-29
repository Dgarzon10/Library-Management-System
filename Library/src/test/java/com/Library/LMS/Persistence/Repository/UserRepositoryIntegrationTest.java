package com.Library.LMS.Persistence.Repository;

import com.Library.LMS.Persistence.Entity.Role;
import com.Library.LMS.Persistence.Entity.UserEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;

    @BeforeEach
    public void setup() {
        testUser = UserEntity.builder()
                .email("Test Email")
                .name("Test Name")
                .libraryId("User1Test")
                .password("")
                .role(Role.USER)
                .build();

        userRepository.save(testUser);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void saveUserTest() {
        UserEntity userEntity = UserEntity.builder()
                .email("Another Email")
                .name("Another Name")
                .libraryId("AnotherUser1Test")
                .password("")
                .role(Role.USER)
                .build();

        UserEntity savedUser = userRepository.save(userEntity);

        assertNotNull(savedUser.getId());
        assertEquals("Another Name", savedUser.getName());
    }

    @Test
    public void getUserByIdTest() {
        Optional<UserEntity> optionalUser = userRepository.findById(testUser.getId());

        assertTrue(optionalUser.isPresent());
        assertEquals(testUser.getId(), optionalUser.get().getId());
    }
    @Test
    public void getUserByEmailTest() {
        Optional<UserEntity> optionalUser = userRepository.findByEmail(testUser.getEmail());

        assertTrue(optionalUser.isPresent());
        assertEquals(testUser.getId(), optionalUser.get().getId());
    }
    @Test
    public void getUserByLibraryIdTest() {
        Optional<UserEntity> optionalUser = userRepository.findByLibraryId(testUser.getLibraryId());

        assertTrue(optionalUser.isPresent());
        assertEquals(testUser.getId(), optionalUser.get().getId());
    }
    @Test
    public void getAllUsersTest() {
        List<UserEntity> users = userRepository.findAll();

        assertEquals(1, users.size());
        assertEquals(testUser.getEmail(), users.get(0).getEmail());
    }

    @Test
    public void updateUserTest() {
        testUser.setName("Updated Name");
        UserEntity updatedUser = userRepository.save(testUser);

        assertEquals(testUser.getName(), updatedUser.getName());

    }
    @Test
    public void deleteUserTest() {
        userRepository.delete(testUser);

        Optional<UserEntity> optionalUser = userRepository.findById(testUser.getId());
        assertFalse(optionalUser.isPresent());
    }
    @Test
    public void findByRoleReturnsCorrectUsers() {

        UserEntity adminUser = UserEntity.builder()
                .email("admin@example.com")
                .name("Admin User")
                .libraryId("AdminUser1")
                .password("")
                .role(Role.ADMIN)
                .build();

        userRepository.save(adminUser);


        List<UserEntity> usersWithUserRole = userRepository.findByRole(Role.USER);


        assertEquals(1, usersWithUserRole.size());
        assertEquals(testUser.getEmail(), usersWithUserRole.get(0).getEmail());
        assertEquals(Role.USER, usersWithUserRole.get(0).getRole());
    }

    @Test
    public void findByRoleNoUsersFound() {

        List<UserEntity> usersWithADMINRole = userRepository.findByRole(Role.ADMIN);


        assertNotNull(usersWithADMINRole);
        assertTrue(usersWithADMINRole.isEmpty());
    }


}