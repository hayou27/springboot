package com.smhrd.board.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.smhrd.board.config.BucketConfig;
import com.smhrd.board.config.FileUploadConfig;
import com.smhrd.board.entity.BoardEntity;
import com.smhrd.board.entity.UserEntity;
import com.smhrd.board.service.BoardService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/board") // controller에 requestmapping 진행 시 default url 변경
public class BoardController {

	
	@Autowired
	BoardService boardService;
	
	
	private final BucketConfig bucketConfig;
	private final AmazonS3 amazonS3;
    private final FileUploadConfig fileUploadConfig;

    BoardController(FileUploadConfig fileUploadConfig, BucketConfig bucketConfig, AmazonS3 amazonS3) {
        this.fileUploadConfig = fileUploadConfig;
        this.bucketConfig = bucketConfig;
        this.amazonS3 = amazonS3;
      
    }

	// 글쓰기 기능 구현
	@PostMapping("/write")
	public String write(@RequestParam String title, @RequestParam String content, 
			HttpSession session, @RequestParam MultipartFile image) {
		
		String imgPath = "";
		
		if(!image.isEmpty()) {
			String img_name =  image.getOriginalFilename();

			String file_name = UUID.randomUUID() + "_" + img_name;
		
			try {
				ObjectMetadata metadata = new ObjectMetadata();
		        metadata.setContentLength(image.getSize());
		        metadata.setContentType(image.getContentType());

		        PutObjectRequest request = new PutObjectRequest(bucketConfig.getbucketName(), file_name, image.getInputStream(), metadata)
		                .withCannedAcl(CannedAccessControlList.PublicRead); // public 접근 허용

		        amazonS3.putObject(request);
		        imgPath = amazonS3.getUrl(bucketConfig.getbucketName(), file_name).toString();
			
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}	
		BoardEntity entity = new BoardEntity();
		entity.setTitle(title);
		entity.setContent(content);
		entity.setImgPath(imgPath);
		
		UserEntity user = (UserEntity) session.getAttribute("user");
		
		String writer = user.getUserId();
		
		entity.setWriter(writer);
		
		BoardEntity result = boardService.write(entity);
		if(result != null) {
			
			return "redirect:/";
			
		}else {
			return "redirect:/board/write";
		}
		
	
	}
	
	// 게시글 상세페이지 이동
	@GetMapping("/detail/{id}") //{} url의 변수
	public String detail(@PathVariable Long id, Model model) { // url의 변수 가지고 오는 법
		System.out.println(id);
		
		
		Optional<BoardEntity> entity =  boardService.detail(id);
		
		model.addAttribute("detail", entity.get());
		
		return "detail";
	}
	
	// 게시글 수정 페이지 이동
	@GetMapping("/edit/{id}")
	public String edit(@PathVariable Long id, Model model) {
		
	
		Optional<BoardEntity> entity =  boardService.detail(id);
		
		model.addAttribute("edit", entity.get());
		return "edit";
	}
	
	@PostMapping("/update")
	public String update(@RequestParam Long id, @RequestParam String title,
			@RequestParam String content, @RequestParam String oldImgPath, @RequestParam MultipartFile image) {

		
		Optional<BoardEntity> board =  boardService.detail(id);
		BoardEntity entity = board.get();
		
		String uploadDir = fileUploadConfig.getUploadDir();
		
		// 새로운 이미지 저장 시 기존 이미지 삭제
		if(!image.isEmpty()) {
			if(oldImgPath != null && !oldImgPath.isEmpty()) {
				
				String oldFile = oldImgPath.replace("/upload/", ""); 
				
				Path oldFilePath = Paths.get(uploadDir, oldFile);
			
				try {
					Files.deleteIfExists(oldFilePath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
				// 새로운 이미지 저장
				try {
		               String newFileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
		               Path newFilePath = Paths.get(uploadDir, newFileName);
		               image.transferTo(newFilePath.toFile());
		               entity.setImgPath("/upload/" + newFileName);
		            } catch (IllegalStateException e) {
		               // TODO Auto-generated catch block
		               e.printStackTrace();
		            } catch (IOException e) {
		               // TODO Auto-generated catch block
		               e.printStackTrace();
		            }
			}
		}
		
		entity.setTitle(title);
		entity.setContent(content);
		
		
		boardService.write(entity);
		return "redirect:/board/detail/" + id;
	}
	
	
	
}
