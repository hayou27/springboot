package com.smhrd.board.config;
// 매개변수를 사용하기 위해 경로를 변수로 사용하기 위해 만든 파일

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

// 환경설정 클래스 파일
@Configuration
public class FileUploadConfig {

	@Value("${file.upload-dir}") // applocation.properties에 있는 file.upload-dir를 참고
	private String uploadDir; // C:upload 라는 폴더
	
	public String getUploadDir() {
		
		return this.uploadDir;
	}
	
}
