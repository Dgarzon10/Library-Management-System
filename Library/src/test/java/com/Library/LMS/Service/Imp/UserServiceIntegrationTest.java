package com.Library.LMS.Service.Imp;

import static org.junit.jupiter.api.Assertions.*;

import com.Library.LMS.Persistence.Entity.UserEntity;
import com.Library.LMS.auth.AuthenticationService;
import com.Library.LMS.auth.model.UserRegister;
import com.Library.LMS.dto.BookDTO;
import com.Library.LMS.dto.UserDTO;
import com.Library.LMS.exception.ResourceConflictException;
import com.Library.LMS.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private BookService bookService;
    @Autowired
    private BorrowingService borrowingService;

    private UserRegister user;


    @BeforeEach
    void setup() {
        user = UserRegister.builder()
                .email("Test Email")
                .name("Test Name")
                .libraryId("User1Test")
                .password("password")
                .build();
    }

    @Test
    void create_ShouldCreateUser_WhenSuccess() {

        UserDTO createdUser = authenticationService.register(user);

        assertNotNull(createdUser);
        assertEquals("Test Name", createdUser.getName());
        assertEquals("Test Email", createdUser.getEmail());
    }

    @Test
    void create_ShouldThrowException_WhenAlreadyExist() {
        authenticationService.register(user);

        assertThrows(ResourceConflictException.class, () -> authenticationService.register(user));
    }

    @Test
    void update_ShouldUpdateUser_WhenValidDTO() {
        UserDTO userDTO = authenticationService.register(user);
        userDTO.setName("John Smith");

        UserDTO updated = userService.update(userDTO.getId(), userDTO);

        assertNotEquals(updated.getName(),user.getName());
    }
    @Test
    void update_ShouldThrowException_WhenUserNotFound() {
        UserDTO userDTO = new UserDTO();
        assertThrows(ResourceNotFoundException.class, () -> userService.update(999L,userDTO));

    }

    @Test
    void delete_ShouldDeleteUser_WhenNotBorrowsActive() {
        UserDTO userDTO = authenticationService.register(user);

        userService.delete(userDTO.getId());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(userDTO.getId()));
    }

    @Test
    void delete_ShouldThrowException_WhenHasBorrows() {
        BookDTO book = BookDTO.builder()
                .title("Test Title")
                .author("Test Author")
                .isbn(123456789L)
                .availability_status(true)
                .build();
        BookDTO bookDTO = bookService.create(book);
        UserDTO userDTO = authenticationService.register(user);

        borrowingService.borrow(userDTO.getId(),bookDTO.getId() );



        assertThrows(ResourceConflictException.class, () -> userService.delete(userDTO.getId()));
    }

    @Test
    void getById_ShouldReturnUser_WhenUserExists() {
        UserDTO userDTO = authenticationService.register(user);

        UserDTO userById = userService.getById(userDTO.getId());

        assertNotNull(userById);
        assertEquals(userDTO.getId(), userById.getId());
        assertEquals(userDTO.getName(), userById.getName());
    }

    @Test
    void getById_ShouldThrowException_WhenUserDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> userService.getById(999L));
    }

    @Test
    void getByEmail_ShouldReturnUser_WhenEmailExists() {
        UserDTO userDTO = authenticationService.register(user);

        UserDTO userByEmail = userService.getByEmail("Test Email");

        assertNotNull(userByEmail);
        assertEquals(userDTO.getEmail(), userByEmail.getEmail());
        assertEquals(userDTO.getName(), userByEmail.getName());
    }

    @Test
    void getByEmail_ShouldThrowException_WhenEmailDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> userService.getByEmail("nonexistent@example.com"));
    }

    @Test
    void getAll_ShouldReturnUsers_WhenUsersExist() {
        authenticationService.register(user);
        UserRegister user2 = UserRegister.builder()
                .email("Test Email 2")
                .name("Test Name 2")
                .password("test")
                .libraryId("2User1Test")
                .build();
        authenticationService.register(user2);


        List<UserDTO> users = userService.getAll();


        assertEquals(3, users.size());
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoUsersExist() {
        List<UserDTO> users = userService.getAll();

        assertNotNull(users);
        assertEquals(1,users.size());
    }
}
