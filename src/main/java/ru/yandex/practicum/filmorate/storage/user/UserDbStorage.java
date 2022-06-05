package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.models.Friend;
import ru.yandex.practicum.filmorate.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

@Component
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User saveUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sqlQuery = "INSERT INTO user (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getName());
                stmt.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
                return stmt;
            }, keyHolder);

            user.setId(
                    Objects.requireNonNull(keyHolder.getKey()).intValue()
            );
        } catch (Exception e) {
            String message = "Не удалось сохранить пользователя";

            log.error("SaveUser. {}", message);
            throw new RuntimeException(message);
        }

        return user;
    }

    @Override
    public User findById(int id) {
        String sqlQuery = "SELECT id, email, login, name, birthday FROM user WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
        }  catch (EmptyResultDataAccessException e) {
            throw e;
        } catch (Exception e) {
            String message = "Не удалось получить пользователя";

            log.error("FindUserById. {}", message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "SELECT id, email, login, name, birthday FROM user";

        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось получить список пользователей";

            log.error("FindAllUsers. {}", message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE user SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";

        try {
            jdbcTemplate.update(sqlQuery,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId());
        } catch (Exception e) {
            String message = "Не удалось обновить данные пользователя";

            log.error("UpdateUser. {}", message);
            throw new RuntimeException(message);
        }

        return user;
    }

    @Override
    public int saveFriend(int userId, int friendId) {
        Friend friend = findFriend(userId, friendId);

        if (friend == null) {
            // если дружбы нет, создаем ее
            createFriend(userId, friendId);
        } else if (friend.getUserId() == friendId && !friend.isStatus()) {
            // если пользователь в этой дружбе друг и она не подтвержденная, подтверждаем ее
            confirmFriend(friend);
        }

        return getCountFriends(userId);
    }

    @Override
    public int deleteFriend(int userId, int friendId) {
        Friend friend = findFriend(userId, friendId);

        if (friend != null) {
            if (friend.getFriendId() == userId) {
                // если в дружбе пользователь был другом то удаляем подтверждение
                deleteConfirm(friend);
            } else {
                // если нет, удаляем дружбу
                removeFriend(friend);

                if (friend.isStatus()) {
                    // если она была подтвержденная создаем ее для друга
                    createFriend(friend.getFriendId(), friend.getUserId());
                }
            }
        }

        return getCountFriends(userId);
    }

    @Override
    public Collection<User> findFriends(int userId) {
        String sqlQuery = "SELECT u.id, u.email, u.login, u.name, u.birthday FROM user u " +
                "JOIN friend f ON u.id = f.friend_id " +
                "WHERE (user_id = ?) OR (friend_id = ? AND status = 1)";

        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось найти друзей пользователя";

            log.error("FindFriends. {}", message);
            throw new RuntimeException(message);
        }
    }

    @Override
    public Collection<User> findCommonFriends(int userId, int otherUserId) {
        String sqlQuery = "SELECT id, email, login,  name,  birthday FROM user " +
                "WHERE id IN (" +
                "(SELECT user_id FROM friend  WHERE (friend_id = ? AND status = 1) " +
                "UNION SELECT friend_id FROM friend WHERE user_id = ?) " +
                "INTERSECT " +
                "(SELECT user_id FROM friend  WHERE (friend_id = ? AND status = 1) " +
                "UNION  SELECT friend_id  FROM friend WHERE user_id = ?))";

        try {
            return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, userId, otherUserId, otherUserId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось найти общих друзей пользователей";

            log.error("FindCommonFriends. {}", message);
            throw new RuntimeException(message);
        }
    }

    private Friend findFriend(int userId, int friendId) {
        String sqlQuery = "SELECT user_id, friend_id, status FROM friend " +
                "WHERE (user_id = ? AND friend_id = ?) OR (friend_id = ? AND user_id = ?)";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFriend, userId, friendId, userId, friendId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            String message = "Не удалось найти дружбу";

            log.error("FindFriend. {}", message);
            throw new RuntimeException(message);
        }
    }

    private void createFriend(int userId, int friendId) {
        String sqlQuery = "INSERT INTO friend (user_id, friend_id) VALUES (?, ?)";

        try {
            jdbcTemplate.update(sqlQuery, userId, friendId);
        } catch (Exception e) {
            String message = "Не удалось добавить пользователя в друзья";

            log.error("CreateFriend. {}", message);
            throw new RuntimeException(message);
        }
    }

    private void removeFriend(Friend friend) {
        String sqlQuery = "DELETE FROM friend WHERE user_id = ? AND friend_id = ?";

        try {
            jdbcTemplate.update(sqlQuery, friend.getUserId(), friend.getFriendId());
        } catch (Exception e) {
            String message = "Не удалось удалить пользователя из друзей";

            log.error("RemoveFriend. {}", message);
            throw new RuntimeException(message);
        }
    }

    private void confirmFriend(Friend friend) {
        String sqlQuery = "UPDATE friend SET status = 1 WHERE user_id = ? AND friend_id = ?";

        try {
            jdbcTemplate.update(sqlQuery, friend.getUserId(), friend.getFriendId());
        } catch (Exception e) {
            String message = "Не удалось подтвердить дружбу";

            log.error("ConfirmFriend. {}", message);
            throw new RuntimeException(message);
        }
    }

    private void deleteConfirm(Friend friend) {
        String sqlQuery = "UPDATE friend SET status = 0 WHERE user_id = ? AND friend_id = ?";

        try {
            jdbcTemplate.update(sqlQuery, friend.getUserId(), friend.getFriendId());
        } catch (Exception e) {
            String message = "Не удалось удалить подтверждение дружбы";

            log.error("DeleteConfirm. {}", message);
            throw new RuntimeException(message);
        }
    }

    private int getCountFriends(int userId) {
        String sqlQuery = "SELECT COUNT(user_id) as countFriends FROM friend " +
                "WHERE (user_id = ?) OR (friend_id = ? AND status = 1)";

        try {
            SqlRowSet countRows =  jdbcTemplate.queryForRowSet(sqlQuery, userId, userId);

            if (countRows.next()) {
                return countRows.getInt("countFriends");
            }

            return 0;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        } catch (Exception e) {
            String message = "Не удалось посчитать количество друзей пользователя";

            log.error("GetCountFriends. {}", message);
            throw new RuntimeException(message);
        }
    }

    private User mapRowToUser(ResultSet resultSet, int i) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    private Friend mapRowToFriend(ResultSet resultSet, int i) throws SQLException {
        return Friend.builder()
                .userId(resultSet.getInt("user_id"))
                .friendId(resultSet.getInt("friend_id"))
                .status(resultSet.getBoolean("status"))
                .build();
    }
}
