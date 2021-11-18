package com.learnspringboot.learningspringbootvideo;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author Victor
 * Date 16/11/2021 12:01
 * @project learning-spring-boot-video
 */
public interface ImageRepository extends PagingAndSortingRepository<Image, Long> {

    public Image findByName(String name);
}
