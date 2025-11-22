package com.expensetracker.service;

import com.expensetracker.dto.CreateUserRequest;
import com.expensetracker.dto.UserDTO;

import java.util.List;

/**
 * Service interface for User operations
 * Follows Interface Segregation Principle (ISP) and Dependency Inversion Principle (DIP)
 */
public interface UserService {

    UserDTO createUser(CreateUserRequest request);

    UserDTO getUserById(Long id);

    UserDTO getUserByUsername(String username);

    List<UserDTO> getAllUsers();

    UserDTO updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
