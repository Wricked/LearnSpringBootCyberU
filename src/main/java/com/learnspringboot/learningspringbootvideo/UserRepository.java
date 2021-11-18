package com.learnspringboot.learningspringbootvideo;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Victor
 * Date 18/11/2021 10:45
 * @project learning-spring-boot-video
 */
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
