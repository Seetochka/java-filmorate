package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    void testSaveUser() {
        User user = User.builder()
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .build();

        user = userStorage.saveUser(user);
        Optional<User> userOptional = userStorage.findById(user.getId());

        assertThat(userOptional).isPresent();
        assertThat(user).isEqualTo(userOptional.get());
    }

    @Test
    void testFindById() {
        Optional<User> userOptional = userStorage.findById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                )
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("email", "mail@mail.ru")
                )
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("login", "dolore")
                );
    }

    @Test
    void testDeleteUser() {
        User user = User.builder()
                .email("delete_user")
                .login("login-delete-user")
                .name("name")
                .birthday(LocalDate.now())
                .build();

        user = userStorage.saveUser(user);

        Collection<User> users = userStorage.findAll();
        assertThat(users).hasSize(5);

        userStorage.deleteUser(user.getId());

        users = userStorage.findAll();
        assertThat(users).hasSize(4);

        Optional<User> deletedUser = userStorage.findById(user.getId());

        assertThat(deletedUser).isEmpty();
    }

    @Test
    void testFindAll() {
        Collection<User> users = userStorage.findAll();
        assertThat(users).hasSize(4);
    }

    @Test
    void testUpdateUser() {
        Optional<User> userOptional = userStorage.findById(2);
        assertThat(userOptional).isPresent();

        User user = userOptional.get();
        user.setName("Update Name");

        userStorage.updateUser(user);

        Optional<User> updatedUserOptional = userStorage.findById(2);
        assertThat(updatedUserOptional).isPresent();

        assertThat(user).isEqualTo(updatedUserOptional.get());
    }

    @Test
    void testSaveFriend() {
        Collection<User> friends = userStorage.findFriends(1);
        assertThat(friends).hasSize(1);

        userStorage.saveFriend(1, 4);

        friends = userStorage.findFriends(1);
        assertThat(friends).hasSize(2);
    }

    @Test
    void testDeleteFriend() {
        Collection<User> friends = userStorage.findFriends(1);
        assertThat(friends).hasSize(2);

        userStorage.deleteFriend(1, 3);

        friends = userStorage.findFriends(1);
        assertThat(friends).hasSize(1);
    }

    @Test
    void testFindFriends() {
        Collection<User> friends = userStorage.findFriends(1);
        assertThat(friends).hasSize(2);
    }

    @Test
    void testFindCommonFriends() {
        userStorage.saveFriend(2, 4);

        Collection<User> friendsUser2 = userStorage.findFriends(2);
        Collection<User> commonFriends = userStorage.findCommonFriends(1, 2);

        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends).isEqualTo(friendsUser2);
    }
}
