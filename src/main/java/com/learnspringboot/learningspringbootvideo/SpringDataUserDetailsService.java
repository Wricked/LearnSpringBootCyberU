package com.learnspringboot.learningspringbootvideo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Victor
 * Date 18/11/2021 10:59
 * @project learning-spring-boot-video
 */
@Component
public class SpringDataUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Autowired
    public SpringDataUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.learnspringboot.learningspringbootvideo.User user = repository.findByUsername(username);
        return new User(user.getUsername(),"{noop}" + user.getPassword(), Stream.of(user.getRoles()).map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }
}
