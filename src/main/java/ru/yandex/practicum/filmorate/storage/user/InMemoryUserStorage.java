package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Хранилище пользователей
 */
@Component
public class InMemoryUserStorage implements UserStorage {
    private static int globalId = 1;

    private Map<Integer, User> users = new ConcurrentHashMap<>();

    /**
     * Сохранение пользователя
     */
    @Override
    public User saveUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);

        return user;
    }

    /**
     * Получение пользователя
     */
    @Override
    public User findById(int id) {
        return users.get(id);
    }

    /**
     * Получение всех пользователей
     */
    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    /**
     * Обновление пользователя
     */
    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);

        return user;
    }

    /**
     * Сохранение в друзья
     */
    @Override
    public int saveFriend(int userId, int friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);

        user.saveFriend(friendId);
        friend.saveFriend(userId);

        return user.getCountFriends();
    }

    /**
     * Удаление из друзей
     */
    @Override
    public int deleteFriend(int userId, int friendId){
        User user = users.get(userId);
        User friend = users.get(friendId);

        user.deleteFriend(friendId);
        friend.deleteFriend(userId);

        return user.getCountFriends();
    }

    /**
     * Получение друзей пользователя
     */
    @Override
    public Collection<User> findFriends(int userId) {
        Set<Integer> friends = users.get(userId).getFriends();

        return users.values()
                .stream()
                .filter(u -> friends.contains(u.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Получение друзей, общих с другим пользователем
     */
    @Override
    public Collection<User> findCommonFriends(int userId, int otherUserId) {
        Set<Integer> friendsUser = users.get(userId).getFriends();
        Set<Integer> friendsOtherUser = users.get(otherUserId).getFriends();
        friendsUser.retainAll(friendsOtherUser);

        return users.values()
                .stream()
                .filter(u -> friendsUser.contains(u.getId()))
                .collect(Collectors.toList());
    }

    private static Integer getNextId() {
        return globalId++;
    }
}
