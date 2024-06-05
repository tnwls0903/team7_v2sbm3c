package dev.mvc.qna_contents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Qna_contentsDAOInter {

  /**
   * 질문글 등록
   * @param qna_contentsVO
   * @return
   */
  public int qna_create(Qna_contentsVO qna_contentsVO);
  
  /**
   * 질문글 전체 목록
   * @return
   */
  public ArrayList<Qna_contentsVO> qna_list_all();

  /**
   * 질문글별 등록된 글 목록
   * @param qcon_no
   * @return
   */
  public ArrayList<Qna_contentsVO> list_by_qcon_no(int qcon_no);
  
  /**
   * 질문글 조회
   * @param qcon_no
   * @return
   */
  public Qna_contentsVO qna_read(int qcon_no);
  
  /**
   * 질문글별 검색 목록
   * @param map
   * @return
   */
  public ArrayList<Qna_contentsVO> list_by_qna_search(HashMap<String, Object> hashMap);
  
  /**
   * 질문글별 검색된 레코드 개수
   * @param hashMap
   * @return
   */
  public int list_by_qna_search_count(HashMap<String, Object> hashMap);
  
  /**
   * 질문글별 검색 목록 + 페이징
   * @param hashMap
   * @return
   */
  public ArrayList<Qna_contentsVO> list_by_qna_search_paging(HashMap<String, Object> hashMap);
  
  /**
   * 질문글 비밀번호 검사
   * @param hashMap
   * @return
   */
  public int qna_password_check(HashMap<String, Object> hashMap);
  
  /**
   * 조회수 증가
   * @param qcon_no
   * @return
   */
  public int qna_update_view(int qcon_no);
  
  /**
   * 질문글 텍스트 수정
   * @param qna_contentsVO
   * @return
   */
  public int qna_update_text(Qna_contentsVO qna_contentsVO);
  
  /**
   * 질문글 삭제
   * @param qcon_no
   * @return
   */
  public int qna_delete(int qcon_no);
  
  // 카테고리 통합이었을 때
//  /**
//   * FK cate_no 값이 같은 레코드 갯수 산출
//   * @param cate_no
//   * @return
//   */
//  public int count_by_cate_no(int cate_no);
//  
//  /**
//   * FK cate_no 값이 같은 특정 카테고리에 속한 모든 레코드 삭제
//   * @param cate_no
//   * @return
//   */
//  public int delete_by_cate_no(int cate_no);
  
  
  /**
   * 질문글 이미지 등록
   * @param qna_imageVO
   * @return
   */
  public int qna_attach_create(Qna_imageVO qna_imageVO);
  
  /**
   * 질문글 이미지 전체 목록
   * @return
   */
  public ArrayList<Qna_imageVO> qna_list_all_image();
  
  /**
   * 질문글 이미지 조회
   * @param qcon_no
   * @return
   */
  public ArrayList<Qna_imageVO> qna_read_image(int qcon_no);
  
  /**
   * 질문글 이미지 수정
   * @param qna_imageVO
   * @return
   */
  public int qna_update_file(Qna_imageVO qna_imageVO);
  
  /**
   * 질문글 이미지 삭제
   * @param qcon_no
   * @return
   */
  public int qna_delete_image(int qcon_no);
  
  /**
   * 질문글 이미지 수
   * @param qcon_no
   * @return
   */
  public int qna_search_count_image(int qcon_no);
  
  /**
   * 질문글 댓글 등록
   * @param qna_commentVO
   * @return
   */
  public int qna_create_comment(Qna_commentVO qna_commentVO);
  
  /**
   * 질문글 댓글 전체 목록
   * @return
   */
  public ArrayList<Qna_commentVO> qna_list_all_comment();
  
  /**
   * 질문글에 따른 댓글 목록
   * @param qcon_no
   * @return
   */
  public List<Qna_Acc_commentVO> list_by_qcmt_no_join(int qcon_no);
  
  /**
   * 최신 댓글 500건
   * @param qcon_no
   * @return
   */
  public List<Qna_Acc_commentVO> list_by_qcmt_no_join_500(int qcon_no);
  
  /**
   * 질문글에 따른 댓글 조회
   * @param qcmt_no
   * @return
   */
  public Qna_commentVO qna_read_comment(int qcmt_no);
  
  /**
   * 질문글 댓글 수정
   * @param qna_commentVO
   * @return
   */
  public int qna_update_comment(Qna_commentVO qna_commentVO);
  
  /**
   * 질문글 댓글 삭제
   * @param qcmt_no
   * @return
   */
  public int qna_delete_comment(int qcmt_no);
  
  /**
   * 질문글 댓글 수
   * @param qcon_no
   * @return
   */
  public int qna_search_count_comment(int qcon_no);
}
