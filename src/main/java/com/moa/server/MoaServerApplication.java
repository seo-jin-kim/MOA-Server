package com.moa.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

// 아래 exclude 뒤에 적혀있는거 보안관련 기능 넣는게 좋다고해서 넣었는데 일단 제외처리했어요
@EnableJpaAuditing
@SpringBootApplication(exclude = {
        SecurityAutoConfiguration.class,
        org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration.class
})
public class MoaServerApplication {

    @PostConstruct
    public void started() {
        // 전역 타임존을 한국 시간(Asia/Seoul)으로 설정
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
        SpringApplication.run(MoaServerApplication.class, args);
    }
}

