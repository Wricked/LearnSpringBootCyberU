package com.learnspringboot.learningspringbootvideo;

import javax.persistence.*;

/**
 * @author Victor
 * Date 16/11/2021 12:01
 * @project learning-spring-boot-video
 */

@Entity
public class Image {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToOne
    private User owner;

    public Image() {
    }

    public Image(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
