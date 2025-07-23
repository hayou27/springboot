package com.smhrd.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.smhrd.board.entity.BoardEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Long>{
	
	// 게시글 역순으로 출력
	List<BoardEntity> findAllByOrderByIdDesc();
	
	// 제목으로 검색기능 구현
	List<BoardEntity> findByTitleContaining(String keyword);
	// select * from ~where title like %keyword%
	
	// 내용으로 검색 기능
	// sql문 작성법은 알겠으나 JPA 메소드로 만드릭 힘든 경우
	/* SQL문 삽입하는 법
	1. @Query 활용하여 작성(특이사항 * 사용이 안됨, 별칭 사용)
	2. 테이블명 작성 시 첫 글자와_ 글자는 대문자로 작성
	3. :keyword --> : 을 사용하면 변수로 쓰겠다는 의미
	4. 메소드명은 자유롭게 작성 */
	@Query("select b from BoardEntity b where b.content like %:keyword%")
	List<BoardEntity> searchContent(String keyword);

	// 작성자 기준으로 검색기능 만들기
	List<BoardEntity> findByWriter(String keyword);
	
}
