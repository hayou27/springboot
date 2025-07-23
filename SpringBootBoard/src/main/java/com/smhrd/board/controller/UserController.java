package com.smhrd.board.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smhrd.board.entity.UserEntity;
import com.smhrd.board.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
	
	
	@Autowired
	UserService userService;
	
	
	// 회원가입 기능 구현
	@PostMapping("/register.do")
	public String register(@RequestParam String id, @RequestParam String pw, @RequestParam String name, @RequestParam int age) {
		// boot에 경우 변수명 == input의 name 값과 일치
		
		
		
		// 필요한 데이터 -> 회원가입을 위한 input 태그 데이터
		
		
		
		System.out.println("id : " +id);
		System.out.println("pw : " +pw);
		System.out.println("name : " +name);
		System.out.println("age : " +age);
		
		// userEntity -> lombok 
		UserEntity entity = new UserEntity();
		entity.setUserId(id);
		entity.setPw(pw);
		entity.setName(name);
		entity.setAge(age);
		
		userService.register(entity);
		return "login";
	}
	
	// 로그인 기능
	// 1. 매핑할 메소드 만들기
	@PostMapping("/login.do")
	public String login(@RequestParam String id , @RequestParam String pw, HttpSession session ) {
		
		// 2. 필요한 데이터 -> id, pw
		
		// 3. DB 접근
		// 3-1. service 객체가 생성되어 있는지 여부 판단 -> @Autowired 확인 하기
		// 3-2. service체 기능 구현 하기
		
		// 3-4. 서비스 기능 실행
		Optional<UserEntity> entity =  userService.login(id, pw);
		
		// Optional 객체에 .isPresent()
		// -> 데이터가 있으면 true, 없으면 false를 리턴해 주는 함수
		
		if(entity.isPresent()) {
			// entity는 현재 Optional 객체
			// UserEntity
			// .get()을 사용하여 optional에 있는 데이터 가지고 오기
			session.setAttribute("user", entity.get());
			return "redirect:/";
		}else {
			// 로그인 실패 시 alert 창 띄우기
			// queryparameter 전송 가능 
			return "redirect:/login?error=true";
		}
		
		
		
	}
	
	// 로그아웃 기능
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		
		// 필요한 데이터 -> HttpSession session
		
		session.removeAttribute("user");
		
		return "redirect:/";
	}
	
	
	
	
	
	
	
	
}
