package dev.mvc.reply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface ReplyDAOInter {

	/**
	 * 댓글 작성
	 * 
	 * @param share_commentsVO
	 * @return int
	 */
	public int create_comment(HashMap<String, Object> map);
	
	/**
	 * 댓글 전체 목록 조회
	 * @return
	 */
	public ArrayList<Share_commentsVO> list(); 
	
	/**
	 * 해당 게시글의 댓글 목록
	 * 
	 * @param scon_no
	 * @return int
	 */
	public ArrayList read_comment(int scon_no);
	
	/**
	 * 댓글 수정
	 * 
	 * @param map
	 * @return int
	 */
	public int update_comment(HashMap<String, Object> map);
	
	/**
	 * scon_no에 따른 comment 삭제 (여러개 삭제)
	 * 
	 * @param scon_no
	 * @return int
	 */
	public int sdelete_comment(List<Integer> scon_no);
	
	/**
	 * sctm_no에 따른 댓글 삭제
	 * 
	 * @param scmt_no
	 * @return
	 */
	public int delete_scmtno(int scmt_no);

	/**
	 * 해당하는 게시글 댓글 삭제
	 * 
	 * @param scon_no
	 * @return int
	 */
	public int delete_comments(int scon_no);

	/**
	 * 각 게시글의 댓글 개수
	 * 
	 * @param scon_no
	 * @return int count
	 */
	public int comment_search(int scon_no);
	
	/**
	 * scmt_no에 따른 scon_no 구하기
	 * @param scomt_no
	 * @return
	 */
	public int scon_comment(int scomt_no);



}
