package com.solux.bodybubby;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication 어노테이션에 DB 관련 자동 설정을 제외하는 옵션을 추가합니다.
@SpringBootApplication
public class BodybubbyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BodybubbyApplication.class, args);
	}

}
