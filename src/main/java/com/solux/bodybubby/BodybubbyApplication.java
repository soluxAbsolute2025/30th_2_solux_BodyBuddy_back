package com.solux.bodybubby;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration; // <--- 이 부분 추가
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration; // <--- 이 부분 추가

// @SpringBootApplication 어노테이션에 DB 관련 자동 설정을 제외하는 옵션을 추가합니다.
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
public class BodybubbyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BodybubbyApplication.class, args);
	}

}
