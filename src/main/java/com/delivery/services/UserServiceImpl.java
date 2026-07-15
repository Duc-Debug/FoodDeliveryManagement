package com.delivery.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.delivery.exception.NotFoundException;
import com.delivery.exception.ValidationException;
import com.delivery.model.User;
import com.delivery.repository.IRepository;
import com.delivery.util.FileHandler;
import com.delivery.util.CsvMapper;

public class UserServiceImpl implements IUserService {
    private final IRepository<User, String> userRepository;
    private final Path filePath;

    public UserServiceImpl(IRepository<User, String> uRepository, Path filePath) {
        userRepository = uRepository;
        this.filePath = filePath;
        loadDataFromCsv();
    }

    @Override
    public void registerUser(User user) throws Exception {
        validateCanRegister(user);
        userRepository.create(user);
        try {
            saveToFile();
        } catch (IOException ex) {
            userRepository.delete(user.getId());
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public User getUserById(String id) {
        User user = userRepository.readById(id);
        if (user == null) {
            throw new NotFoundException("User Not found: " + id);
        }
        return user;
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.readAll();
    }

    @Override
    public void depositMoney(String userId, double amount) throws Exception {
        User user = userRepository.readById(userId);
        if (user == null) {
            throw new NotFoundException("User Not found: " + userId);
        }
        user.deposit(amount);
        userRepository.update(userId, user);
        try {
            saveToFile();
        } catch (IOException ex) {
            user.deduct(amount);
            userRepository.update(userId, user);
            throw new IOException("Deposit failed: " + ex.getMessage());
        }
    }

    @Override
    public void updateUser(String userId, User user) throws Exception {
        var userOld = userRepository.readById(userId);
        if (userOld == null)
            throw new NotFoundException("UserNotFound");
        var userBackup = new User(
                userId,
                userOld.getName(),
                userOld.getPhoneNumber(),
                userOld.getBalance(),
                userOld.getAddress());
        userRepository.update(userId, user);
        try {
            saveToFile();
        } catch (IOException ex) {
            userRepository.update(userId, userBackup);
            throw new IOException(ex.getMessage());
        }
    }

    @Override
    public void deleteUser(String id) throws Exception {
        var userBackup = userRepository.readById(id);
        if (userBackup == null)
            throw new NotFoundException("Not Found User");
        userRepository.delete(id);
        try {
            saveToFile();
        } catch (Exception ex) {
            userRepository.create(userBackup);
            throw new Exception("Delete failed: " + ex.getMessage());
        }
    }

    private void validateCanRegister(User user) {
        if (user == null) {
            throw new ValidationException("User is required!", "INVALID_INPUT");
        }
        if (user.getId() == null || user.getId().isBlank()) {
            throw new ValidationException("User id is requied!", "INVALID_INPUT");
        }
        var users = userRepository.readAll();
        for (User u : users) {
            if (user.getId().equals(u.getId())) {
                throw new ValidationException("User id is already existing");
            }
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("User name is requied!", "INVALID_INPUT");
        }
        if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
            throw new ValidationException("User PhoneNumber is requied!", "INVALID_INPUT");
        }
        if (user.getBalance() < 0) {
            throw new ValidationException("User balance is not minus!", "INVALID_INPUT");
        }
        if (user.getAddress() == null || user.getAddress().isBlank()) {
            throw new ValidationException("Customer Address is requied!", "INVALID_INPUT");
        }

    }

    private void saveToFile() throws IOException {
        if (filePath == null) {
            return;
        }
        FileHandler.writeToCsv(filePath, userRepository.readAll(), CsvMapper::toCsvRow);
    }

    private static User parseUserCsv(String line) {
        String[] parts = line.split(",");
        return new User(parts[0], parts[1], parts[2], Double.parseDouble(parts[3]), parts[4]);
    }

    private void loadDataFromCsv() {
        try {
            List<User> users = FileHandler.readFromTextFile(filePath, UserServiceImpl::parseUserCsv);
            for (User user : users) {
                userRepository.create(user);
            }
        } catch (Exception e) {
            System.out.println("Not found the Csv" + e.getMessage());
        }
    }

}
