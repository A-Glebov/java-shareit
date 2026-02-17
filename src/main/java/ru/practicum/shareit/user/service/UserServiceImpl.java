package ru.practicum.shareit.user.service;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto saveUser(UserDto userDto) {
        log.info("Запрос на сохранение пользователя {}", userDto);
        if (userDto.getEmail().isBlank()) {
            String errorMessage = "Email должен быть указан";
            log.error("Ошибка валидации создания пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (isEmailExists(userDto.getEmail())) {
            String errorMessage = "Этот email уже используется";
            log.error("Ошибка валидации создания пользователя: {}", errorMessage);
            throw new ValidationException(errorMessage);
        }
        User user = userRepository.saveUser(userMapper.userDtoToUserModel(userDto));
        log.info("Создан пользователь {}", userDto);
        return userMapper.userModelToUserDto(user);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        log.info("Запрос на обновление пользователя id {}", userId);
        User savedUser = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));

        String newEmail = userDto.getEmail();
        if (newEmail != null && !newEmail.isBlank()) {
            if (!newEmail.equals(savedUser.getEmail())) {
                if (isEmailExists(newEmail)) {
                    String errorMessage = "Этот email уже используется";
                    log.error("Ошибка валидации обновления email пользователя: {}", errorMessage);
                    throw new ValidationException(errorMessage);
                }
            }
            savedUser = savedUser.toBuilder().email(newEmail).build();
            log.debug("Пользователю установлен email {}", newEmail);
        }

        String newName = userDto.getName();
        if (newName != null && !newName.isBlank()) {
            savedUser = savedUser.toBuilder().name(newName).build();
            log.debug("Пользователю установлено имя {}", newName);
        }

        UserDto updatedUserDto = userMapper.userModelToUserDto(savedUser);
        log.info("Данные пользователя {} обновлены", updatedUserDto);
        return updatedUserDto;
    }

    @Override
    public UserDto getUserById(@NotNull long userId) {
        log.info("Запрос на получение пользователя id: {}", userId);
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        UserDto userDto = userMapper.userModelToUserDto(user);
        log.info("Получен пользователь {}", userDto);
        return userDto;
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userRepository.getAllUsers().stream()
                .map(userMapper::userModelToUserDto)
                .toList();
    }

    @Override
    public void deleteUserById(@NotNull long userId) {
        log.info("Запрос на удаление пользователя id: {}", userId);
        userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        userRepository.deleteUserById(userId);
        log.info("Пользователь id: {} успешно удален", userId);
    }

    private boolean isEmailExists(String email) {
        log.info("Проверка email на уникальность");
        return userRepository.getAllUsers().stream()
                .map(User::getEmail)
                .anyMatch(existEmail -> existEmail.equals(email));
    }
}
