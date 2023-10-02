package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {
    private int id;
    private final Map<Integer,User> users;

    public UserStorageImpl() {
        this.id = 0;
        this.users = new HashMap<>();
    }

    @Override
    public User addUser(User user) {
        id++;
        user.setId(id);
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public Optional<User> getUser(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public int deleteUser(int id) {
        if (users.containsKey(id)) {
            users.remove(id);
            return id;
        }
        return 0;
    }

    @Override
    public int deleteAll() {
        users.clear();
        if (users.size() > 0) {
            return 0;
        }
        return 1;
    }

    @Override
    public Optional<User> updateUser(int id, User user) {
        if (users.containsKey(id)) {
            User currentUser = users.get(id);
            currentUser.setName(user.getName());
            currentUser.setEmail(user.getEmail());
            return Optional.of(users.get(id));
        }
        return Optional.empty();
    }
}
