package com.palla.gallery.service;

import com.palla.gallery.dto.UserDto;
import com.palla.gallery.entity.User;
import com.palla.gallery.exception.UserNotFoundException;
import com.palla.gallery.mapper.UserToUserDetailsMapper;
import com.palla.gallery.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {


    @Autowired
    private UserRepository repository;

    public UserDto saveUser(User user) {
        User response = repository.save(user);
        return new UserDto(response.getUserId(), response.getUsername());
    }

    public User getUserByUsername(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    @Cacheable(value = "users")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userInfo = repository.findByUsername(username);
        return userInfo.map(UserToUserDetailsMapper::new).orElseThrow(() -> new UsernameNotFoundException("user not found " + username));
    }
}
