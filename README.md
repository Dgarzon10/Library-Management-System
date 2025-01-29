# technical-assessment
**Build a Mini Library Management System (LMS)**  

The task involves creating a RESTful web service for a library system, where users can perform operations like managing books, authors, users, and borrowing activities. The application should persist data in a relational database.

---

### **Requirements and Features**

#### **Core Functionalities**
1. **Manage Books**:
   - Add, update, delete, and retrieve books.
   - Each book has attributes like title, author, ISBN, and availability status.

2. **Manage Users**:
   - Add, update, delete, and retrieve library users.
   - Each user has attributes like name, email, and a unique library ID.

3. **Borrowing System**:
   - Allow users to borrow books (limit to 3 books per user).
   - Track due dates and borrowing history.
   - Implement logic to mark a book as unavailable when borrowed.

4. **Search**:
   - Implement search functionality for books by title, author, or ISBN.

#### **Additional Requirements**
- **Authentication**: Basic JWT-based user authentication (for Admin and User roles).
- **API Design**: The candidate should design RESTful endpoints.
- **Database Design**: Define and implement a schema for managing entities.

#### **Constraints**
- Books can only be borrowed if available.
- Users cannot borrow more than 2 books at a time.

---

### **Technical Requirements**

#### **Backend**
- Use **Spring Boot** to develop the application.
- Use **Spring Data JPA** for database interactions.
- Implement **Spring Security** for authentication and role-based access control.
- Use **H2 Database** (or any other lightweight relational DB) for development/testing.

#### **Testing**
- Include unit tests and integration tests using **JUnit** and **Mockito**.
- Test REST endpoints with **MockMvc** or **Postman/Swagger** documentation.

#### **Documentation**
- Provide API documentation using **Swagger/OpenAPI**.
- Include a `README.md` with:
  - Instructions to build and run the project.
  - High-level architectural decisions.

---

### **Evaluation Criteria**
1. **Code Quality**:
   - Clean, maintainable, and modular code with proper comments.
   - Use of meaningful variable/method names and SOLID principles.

2. **Use of Spring Features**:
   - Dependency injection, annotations, and proper use of Spring Boot's ecosystem.
   - Understanding of Spring Security, JPA, and REST principles.

3. **Error Handling**:
   - Graceful handling of exceptions (e.g., user not found, book unavailable).
   - Use of proper HTTP status codes.

4. **Database Design**:
   - Well-structured schema with relationships (e.g., One-to-Many for Authors to Books, Many-to-Many for Users to Books).

5. **Testing**:
   - Presence of unit and integration tests.
   - Coverage of edge cases.

6. **Project Setup and Documentation**:
   - Clarity and completeness of setup instructions.
   - API documentation quality.

---

### **Example Endpoints**
- `POST /api/books` - Add a book.
- `GET /api/books/{id}` - Get book details.
- `POST /api/users/{userId}/books` - Borrow a book.
- `GET /api/users/{userId}/books` - View borrowed books.

---

### **Optional Challenges**
For developers who want to go beyond the base requirements:
- Implement caching using **Spring Cache** (e.g., caching popular book searches).
- Implement a scheduling feature using **Spring Scheduler** to notify users of overdue books.
- Use **Docker** to containerize the application.
