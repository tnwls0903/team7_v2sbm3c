package dev.mvc.share_contents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.mvc.account.AccountVO;
import dev.mvc.bookmark.Share_markVO;
import dev.mvc.recommend.HashtagVO;
import dev.mvc.share_contents.Contents;
import dev.mvc.share_contentsdto.Contents_tagVO;
import dev.mvc.share_contentsdto.Contents_urlVO;
import dev.mvc.reply.Share_commentVO;
import dev.mvc.share_contentsdto.Share_contentsVO;
import dev.mvc.share_contentsdto.Share_imageVO;

@Service("dev.mvc.share_contents.Share_contentsProc")
public class Share_contentsProc implements Share_contentsProcInter {
	@Autowired
	private Share_contentsDAOInter scontentsDAO;

	@Override
	public ArrayList list_all() {
		ArrayList<Share_contentsVO> list = this.scontentsDAO.list_all();
		return list;
	}

	@Override
	public ArrayList list_all_image() {
		ArrayList<Share_imageVO> list = this.scontentsDAO.list_all_image();
		return list;
	}

	@Override
	public Share_contentsVO read(int scon_no) {
		Share_contentsVO scontents = this.scontentsDAO.read(scon_no);
		return scontents;
	}

	@Override
	public int update_view(int scon_no) {
		int cnt = this.scontentsDAO.update_view(scon_no);
		return cnt;
	}

	@Override
	public int update_text(Share_contentsVO share_contentsVO) {
		int cnt = this.scontentsDAO.update_text(share_contentsVO);
		return cnt;
	}

	@Override
	public int create(Share_contentsVO share_contentsVO) {
		int cnt = this.scontentsDAO.create(share_contentsVO);
		return cnt;
	}

	@Override
	public int delete(int scon_no) {
		int cnt = this.scontentsDAO.delete(scon_no);
		return cnt;
	}

	@Override
	public int create_image(Share_imageVO share_imageVO) {
		int cnt = this.scontentsDAO.create_image(share_imageVO);
		return cnt;
	}

	@Override
	public ArrayList<Share_contentsVO> list_by_contents_search_paging(HashMap<String, Object> map) {
		/*
		 * 예) 페이지당 10개의 레코드 출력 1 page: WHERE r >= 1 AND r <= 10 2 page: WHERE r >= 11
		 * AND r <= 20 3 page: WHERE r >= 21 AND r <= 30
		 * 
		 * 페이지에서 출력할 시작 레코드 번호 계산 기준값, nowPage는 1부터 시작 1 페이지 시작 rownum: now_page = 1, (1
		 * - 1) * 10 --> 0 2 페이지 시작 rownum: now_page = 2, (2 - 1) * 10 --> 10 3 페이지 시작
		 * rownum: now_page = 3, (3 - 1) * 10 --> 20
		 */
		int begin_of_page = ((int) map.get("now_page") - 1) * Contents.RECORD_PER_PAGE;

		// 시작 rownum 결정
		// 1 페이지 = 0 + 1: 1
		// 2 페이지 = 10 + 1: 11
		// 3 페이지 = 20 + 1: 21
		int start_num = begin_of_page + 1;

		// 종료 rownum
		// 1 페이지 = 0 + 10: 10
		// 2 페이지 = 10 + 10: 20
		// 3 페이지 = 20 + 10: 30
		int end_num = begin_of_page + Contents.RECORD_PER_PAGE;
		/*
		 * 1 페이지: WHERE r >= 1 AND r <= 10 2 페이지: WHERE r >= 11 AND r <= 20 3 페이지: WHERE
		 * r >= 21 AND r <= 30
		 */

		System.out.println("begin_of_page: " + begin_of_page);
		System.out.println("WHERE r >= " + start_num + " AND r <= " + end_num);

		map.put("start_num", start_num);
		map.put("end_num", end_num);

		ArrayList<Share_contentsVO> list = this.scontentsDAO.list_by_contents_search_paging(map);

		return list;
	}

	@Override
	public ArrayList contents_tag_search_paging(HashMap<String, Object> map) {

		ArrayList<Contents_tagVO> list = this.scontentsDAO.contents_tag_search_paging(map);
		return list;

	}

	@Override
	public String pagingBox(int now_page, String word, String list_file, int search_count, int record_per_page,
			int cate_no, int page_per_block) {
		// 전체 페이지 수: (double)1/10 -> 0.1 -> 1 페이지, (double)12/10 -> 1.2 페이지 -> 2 페이지
		int total_page = (int) (Math.ceil((double) search_count / record_per_page));
		// 전체 그룹 수: (double)1/10 -> 0.1 -> 1 그룹, (double)12/10 -> 1.2 그룹-> 2 그룹
		int total_grp = (int) (Math.ceil((double) total_page / page_per_block));
		// 현재 그룹 번호: (double)13/10 -> 1.3 -> 2 그룹
		int now_grp = (int) (Math.ceil((double) now_page / page_per_block));

		// 1 group: 1, 2, 3 ... 9, 10
		// 2 group: 11, 12 ... 19, 20
		// 3 group: 21, 22 ... 29, 30
		int start_page = ((now_grp - 1) * page_per_block) + 1; // 특정 그룹의 시작 페이지
		int end_page = (now_grp * page_per_block); // 특정 그룹의 마지막 페이지

		StringBuffer str = new StringBuffer(); // String class 보다 문자열 추가등의 편집시 속도가 빠름

		// style이 java 파일에 명시되는 경우는 로직에 따라 css가 영향을 많이 받는 경우에 사용하는 방법
		str.append("<style type='text/css'>");
		str.append("  #paging {text-align: center; margin-top: 5px; font-size: 1em;}");
		str.append("  #paging A:link {text-decoration:none; color:black; font-size: 1em;}");
		str.append("  #paging A:hover{text-decoration:none; background-color: #FFFFFF; color:black; font-size: 1em;}");
		str.append("  #paging A:visited {text-decoration:none;color:black; font-size: 1em;}");
		str.append("  .span_box_1{");
		str.append("    text-align: center;");
		str.append("    font-size: 1em;");
		str.append("    border: 1px;");
		str.append("    border-style: solid;");
		str.append("    border-color: #cccccc;");
		str.append("    padding:1px 6px 1px 6px; /*위, 오른쪽, 아래, 왼쪽*/");
		str.append("    margin:1px 2px 1px 2px; /*위, 오른쪽, 아래, 왼쪽*/");
		str.append("  }");
		str.append("  .span_box_2{");
		str.append("    text-align: center;");
		str.append("    background-color: #668db4;");
		str.append("    color: #FFFFFF;");
		str.append("    font-size: 1em;");
		str.append("    border: 1px;");
		str.append("    border-style: solid;");
		str.append("    border-color: #cccccc;");
		str.append("    padding:1px 6px 1px 6px; /*위, 오른쪽, 아래, 왼쪽*/");
		str.append("    margin:1px 2px 1px 2px; /*위, 오른쪽, 아래, 왼쪽*/");
		str.append("  }");
		str.append("</style>");
		str.append("<DIV id='paging'>");
//    str.append("현재 페이지: " + nowPage + " / " + totalPage + "  "); 

		// 이전 10개 페이지로 이동
		// now_grp: 1 (1 ~ 10 page)
		// now_grp: 2 (11 ~ 20 page)
		// now_grp: 3 (21 ~ 30 page)
		// 현재 2그룹일 경우: (2 - 1) * 10 = 1그룹의 마지막 페이지 10
		// 현재 3그룹일 경우: (3 - 1) * 10 = 2그룹의 마지막 페이지 20
		int _now_page = (now_grp - 1) * page_per_block;
		if (now_grp >= 2) { // 현재 그룹번호가 2이상이면 페이지수가 11페이지 이상임으로 이전 그룹으로 갈수 있는 링크 생성
			str.append("<span class='span_box_1'><A href='" + list_file + "?cate_no=" + cate_no + "&word=" + word
					+ "&now_page=" + _now_page + "'>이전</A></span>");
		}

		// 중앙의 페이지 목록
		for (int i = start_page; i <= end_page; i++) {
			if (i > total_page) { // 마지막 페이지를 넘어갔다면 페이 출력 종료
				break;
			}

			if (now_page == i) { // 목록에 출력하는 페이지가 현재페이지와 같다면 CSS 강조(차별을 둠)
				str.append("<span class='span_box_2'>" + i + "</span>"); // 현재 페이지, 강조
			} else {
				// 현재 페이지가 아닌 페이지는 이동이 가능하도록 링크를 설정
				str.append("<span class='span_box_1'><A href='" + list_file + "?cate_no=" + cate_no + "&word=" + word
						+ "&now_page=" + i + "'>" + i + "</A></span>");
			}
		}

		// 10개 다음 페이지로 이동
		// nowGrp: 1 (1 ~ 10 page), nowGrp: 2 (11 ~ 20 page), nowGrp: 3 (21 ~ 30 page)
		// 현재 페이지 5일경우 -> 현재 1그룹: (1 * 10) + 1 = 2그룹의 시작페이지 11
		// 현재 페이지 15일경우 -> 현재 2그룹: (2 * 10) + 1 = 3그룹의 시작페이지 21
		// 현재 페이지 25일경우 -> 현재 3그룹: (3 * 10) + 1 = 4그룹의 시작페이지 31
		_now_page = (now_grp * page_per_block) + 1; // 최대 페이지수 + 1
		if (now_grp < total_grp) {
			str.append("<span class='span_box_1'><A href='" + list_file + "?cate_no=" + cate_no + "&word=" + word
					+ "&now_page=" + _now_page + "'>다음</A></span>");
		}
		str.append("</DIV>");

		return str.toString();
	}

	@Override
	public int list_by_cateno_search_count(HashMap<String, Object> map) {
		int cnt = this.scontentsDAO.list_by_cateno_search_count(map);
		return cnt;
	}

	@Override
	public int create_url(HashMap<String, Object> map) {
		int cnt = this.scontentsDAO.create_url(map);
		return cnt;
	}

	@Override
	public ArrayList url_read(int scon_no) {
		ArrayList<Contents_urlVO> list = this.scontentsDAO.url_read(scon_no);
		return list;
	}

	@Override
	public ArrayList only_url(int scon_no) {
		ArrayList<Contents_urlVO> list = this.scontentsDAO.only_url(scon_no);
		return list;
	}

	@Override
	public int update_url(HashMap<String, Object> map) {
		int cnt = this.scontentsDAO.update_url(map);
		return cnt;
	}

	@Override
	public int delete_url(int scon_no) {
		int cnt = this.scontentsDAO.delete_url(scon_no);
		return cnt;
	}

	@Override
	public int insert_tag(HashMap<String, Object> map) {
		int cnt = this.scontentsDAO.insert_tag(map);
		return cnt;
	}

	@Override
	public ArrayList read_contents_tag(int scon_no) {
		ArrayList<Contents_tagVO> list = this.scontentsDAO.read_contents_tag(scon_no);
		return list;
	}

	@Override
	public int attach_create(Share_imageVO share_imageVO) {
		int cnt = this.scontentsDAO.attach_create(share_imageVO);
		return cnt;
	}

	@Override
	public ArrayList select_hashtag() {
		ArrayList<HashtagVO> list = this.scontentsDAO.select_hashtag();
		return list;
	}


	@Override
	public ArrayList select_sconno(int tag_no) {
		ArrayList<Contents_tagVO> list = this.scontentsDAO.select_sconno(tag_no);
		return list;
	}

	@Override
	public ArrayList list_by_sconno(int scon_no) {
		ArrayList<Share_contentsVO> list = this.scontentsDAO.list_by_sconno(scon_no);
		return list;
	}

	@Override
	public ArrayList list_image() {
		ArrayList<Share_imageVO> list = this.scontentsDAO.list_image();
		return list;
	}

	@Override
	public ArrayList read_image(int scon_no) {
		ArrayList<Share_imageVO> share_imageVO = this.scontentsDAO.read_image(scon_no);
		return share_imageVO;
	}

	@Override
	public int delete_image(int scon_no) {
		int cnt = this.scontentsDAO.delete_image(scon_no);
		return cnt;
	}

	@Override
	public int update_file(Share_imageVO share_imageVO) {
		int cnt = this.scontentsDAO.update_file(share_imageVO);
		return cnt;
	}

	@Override
	public int count_image(int scon_no) {
		int cnt = this.scontentsDAO.count_image(scon_no);
		return cnt;
	}

	@Override
	public int up_priority(int scon_no) {
		int cnt = this.scontentsDAO.up_priority(scon_no);
		return cnt;
	}

	@Override
	public int down_priority(int scon_no) {
		int cnt = this.scontentsDAO.down_priority(scon_no);
		return cnt;
	}

	@Override
	public int y_mark(int scon_no) {
		int cnt = this.scontentsDAO.y_mark(scon_no);
		return cnt;
	}

	@Override
	public int n_mark(int scon_no) {
		int cnt = this.scontentsDAO.n_mark(scon_no);
		return cnt;
	}

	@Override
	public int bookmark_create(HashMap<String, Object> map) {
		int cnt = this.scontentsDAO.bookmark_create(map);
		return cnt;
	}

	@Override
	public int bookmark_delete(HashMap<String, Object> map) {
		int cnt = this.scontentsDAO.bookmark_delete(map);
		return cnt;
	}

	@Override
	public int bookmarall_delete(int scon_no) {
		int cnt = this.scontentsDAO.bookmarall_delete(scon_no);
		return cnt;
	}

	@Override
	public int delete_tag(int scon_no) {
		int cnt = this.scontentsDAO.delete_tag(scon_no);
		return cnt;
	}

	@Override
	public int tag_count(int tag_no) {
		int cnt = this.scontentsDAO.tag_count(tag_no);
		return cnt;
	}

	@Override
	public int delete_sconno(List<Integer> scon_no) {
		int cnt = this.scontentsDAO.delete_sconno(scon_no);
		return cnt;
	}

	@Override
	public int sdelete_image(List<Integer> scon_no) {
		int cnt = this.scontentsDAO.sdelete_image(scon_no);
		return cnt;
	}
	@Override
	public int sdelete_bookmark(List<Integer> scon_no) {
		int cnt = this.scontentsDAO.sdelete_bookmark(scon_no);
		return cnt;
	}

	@Override
	public int sdelete_url(List<Integer> scon_no) {
		int cnt = this.scontentsDAO.sdelete_url(scon_no);
		return cnt;
	}

	@Override
	public int sdelete_tag(List<Integer> scon_no) {
		int cnt = this.scontentsDAO.sdelete_tag(scon_no);
		return cnt;
	}

	@Override
	public ArrayList<Share_imageVO> distinct_sconno() {
		ArrayList<Share_imageVO> list = this.scontentsDAO.distinct_sconno();
		return list;
	}

	@Override
	public HashtagVO read_hashtag_name(int tag_no) {
		HashtagVO hash= this.scontentsDAO.read_hashtag_name(tag_no);
		return hash;
	}

	@Override
	public ArrayList<Share_imageVO> distinct_image(int scon_no) {
		ArrayList<Share_imageVO> share_imageVO = this.scontentsDAO.distinct_image(scon_no);
		return share_imageVO;
	}

	@Override
	public ArrayList<HashtagVO> all_hashtag_name() {
		ArrayList<HashtagVO> list = this.scontentsDAO.all_hashtag_name();
		return list;
	}

	@Override
	public ArrayList<HashtagVO> sconno_hashtag(int scon_no) {
		ArrayList<HashtagVO> list = this.scontentsDAO.sconno_hashtag(scon_no);
		return list;
	}

	@Override
	public ArrayList<Share_markVO> mark_check(HashMap<String,Object>map) {
		ArrayList<Share_markVO> list = this.scontentsDAO.mark_check(map);
		return list;
	}

	@Override
	public String writer(HashMap<String, Object> map) {
		String writer = this.scontentsDAO.writer(map);
		return writer;
	}

	@Override
	public int bookmark_count(int scon_no) {
		int cnt = this.scontentsDAO.bookmark_count(scon_no);
		return cnt;
	}

	@Override
	public ArrayList<Share_imageVO> related_image(int tag_no) {
		ArrayList<Share_imageVO> list = this.scontentsDAO.related_image(tag_no);
		return list;
	}

	@Override
	public int related_image_count() {
		int cnt = this.scontentsDAO.related_image_count();
		return cnt;
	}

	@Override
	public AccountVO read_img(int scon_no) {
		AccountVO accountVO = this.scontentsDAO.read_img(scon_no);
		return accountVO;
	}



}
