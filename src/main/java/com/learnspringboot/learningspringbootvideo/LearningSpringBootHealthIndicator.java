package com.learnspringboot.learningspringbootvideo;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Victor
 * Date 17/11/2021 14:03
 * @project learning-spring-boot-video
 */
@Component
public class LearningSpringBootHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            int responseCode = ((HttpURLConnection)new URL("http://www.google.com.br/78").openConnection()).getResponseCode();
            if(responseCode >= 200 && responseCode < 300) {
                return Health.up().build();
            } else {
                return Health.down().withDetail("HTTP status code ", responseCode).build();
            }
        } catch (IOException e) {
            return Health.down(e).build();
        }
    }
}
