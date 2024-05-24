package dev.mvc.qna_contents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dev.mvc.account.AccountProc;
import dev.mvc.category.CategoryProcInter;
import dev.mvc.category.CategoryVO;
import dev.mvc.category.CategoryVOMenu;
import dev.mvc.share_contents.Contents;
import dev.mvc.tool.Tool;
import dev.mvc.tool.Upload;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@RequestMapping("/qcontents")
@Controller
public class Qna_contentsCont {

  @Autowired
  @Qualifier("dev.mvc.category.CategoryProc")
  private CategoryProcInter categoryProc;
  
  @Autowired
  @Qualifier("dev.mvc.qna_contents.Qna_contentsProc")
  private Qna_contentsProc qna_contentsProc;

  @Autowired
  @Qualifier("dev.mvc.account.AccountProc")
  private AccountProc accountProc;
  
  public Qna_contentsCont() {
    System.out.println("-> Qna_contentsCont created.");
  }
  
  
  /**
   * POST 요청시 새로고침 방지, POST 요청 처리완료 -> redirect -> url -> GET -> forward -> html
   * POST → url → GET → 데이터 전송
   * @return
   */
  @GetMapping(value="/msg")
  public String msg(Model model, String url){
    
    // 카테고리 전체 메뉴
    ArrayList<CategoryVOMenu> menu = new ArrayList<CategoryVOMenu>();
    model.addAttribute("menu", menu);

    return url; // /forward, /templates/...
  }
  
  
  /**
   * 질문글 등록 폼
   * http://localhost:9093/qcontents/qna_create?cate_no=2
   * @param model
   * @param cate_no
   * @return
   */
  @GetMapping(value="/qna_create")
  public String qna_create(Model model, int cate_no) {
    
    // 카테고리 가져오기
    CategoryVO categoryVO = this.categoryProc.cate_read(cate_no);
    model.addAttribute("categoryVO", categoryVO);
    
    return "qcontents/qna_create"; // /templates/qcontents/create.html
  }
  
  /**
   * 질문글 등록 처리
   * @param model
   * @param request
   * @param session
   * @param ra
   * @return
   */
  @PostMapping(value="/qna_create")
  public String qna_create(Model model,
                            HttpServletRequest request,
                            HttpSession session,
                            RedirectAttributes ra,
                            Qna_imageVO qna_imageVO,
                            Qna_contentsVO qna_contentsVO) {

    // 질문글 등록 전 출력
    System.out.println("-> [레코드 등록 전] qcon_no: " + qna_contentsVO.getQcon_no());
    System.out.println("-> [레코드 등록 전] file_no: " + qna_imageVO.getFile_no());
    
    // 카테고리 번호 가져오기
    int cate_no = qna_contentsVO.getCate_no(); // 부모글 번호
    
    // 질문글 등록 처리
    int cnt = this.qna_contentsProc.qna_create(qna_contentsVO);
    
    // 질문글 등록 성공 여부 확인
    if (cnt == 1) { // 질문글 등록 성공
      
      // 새로 등록된 질문글 번호 가져오기
      int qcon_no = qna_contentsVO.getQcon_no();
      System.out.println("-> [레코드 등록 후] qcon_no: " + qcon_no);
      
      // ---------------------------------------------------------------
      // 파일 전송 코드 시작
      // ---------------------------------------------------------------
      String file_origin_name = ""; // 원본 파일명
      String file_upload_name = ""; // 업로드된 파일명
      long file_size = 0;  // 파일 사이즈
      String file_thumb_name = ""; // Preview 이미지
      
      String upDir = Contents.getUploadDir(); // 파일을 업로드할 폴더 준비
      
      // 전송 파일이 없어서도 fnamesMF 객체가 생성됨.
      List<MultipartFile> fnamesMF = qna_imageVO.getFnamesMF();
      
      int count = fnamesMF.size(); // 전송 파일 갯수
      
      if (count > 0) {
        for (MultipartFile multipartFile:fnamesMF) { // 파일 추출, 1개이상 파일 처리
          file_size = multipartFile.getSize();  // 파일 크기
          if (file_size > 0) { // 파일 크기 체크
            file_origin_name = multipartFile.getOriginalFilename(); // 원본 파일명
            file_upload_name = Upload.saveFileSpring(multipartFile, upDir); // 파일 저장, 업로드된 파일명
            
            if (Tool.isImage(file_origin_name)) { // 이미지인지 검사
              file_thumb_name = Tool.preview(upDir, file_upload_name, 200, 150); // thumb 이미지 생성
            }
          }
          
          qna_imageVO.setQcon_no(qcon_no);
          qna_imageVO.setFile_origin_name(file_origin_name);
          qna_imageVO.setFile_upload_name(file_upload_name);
          qna_imageVO.setFile_thumb_name(file_thumb_name);
          qna_imageVO.setFile_size(file_size);
        }
      }    
      // -----------------------------------------------------
      // 파일 전송 코드 종료
      // -----------------------------------------------------
      
      // 이미지 파일 등록 처리
      this.qna_contentsProc.qna_attach_create(qna_imageVO);
      
      // 질문글 등록 성공했을 때
      ra.addAttribute("cate_no", cate_no);
      ra.addAttribute("qcon_no", qcon_no);   
      ra.addAttribute("file_no", qna_imageVO.getFile_no());

      return "redirect:/qcontents/qna_list_all";
    } else { // 질문글 등록 실패
      System.out.println("질문글 등록 실패");
      
      ra.addFlashAttribute("code", "qna_create_fail"); // 등록 실패
      ra.addFlashAttribute("cnt", 0); // cnt: 0, 질문글 등록 실패
      ra.addFlashAttribute("url", "/qcontents/msg"); // /templates/qcontents/msg.html
      
      return "#";
    }
  }

  /**
   * 질문글 목록 + 검색 + 페이징
   *  http://localhost:9093/qcontents/qna_list_all?cate_no=2
   * @param model
   * @param session
   * @param cate_no
   * @param file_no
   * @param word
   * @param now_page
   * @return
   */
  @GetMapping(value="/qna_list_all")
  public String list_by_qna_search_paging(Model model, HttpSession session, int cate_no,
                                                    @RequestParam(name="word", defaultValue = "") String word,
                                                    @RequestParam(name="file_no", defaultValue = "1") int file_no,
                                                    @RequestParam(name="now_page", defaultValue = "1") int now_page) {
    
    // 카테고리 가져오기
    CategoryVO categoryVO = this.categoryProc.cate_read(cate_no);
    model.addAttribute("categoryVO", categoryVO);
    
    word = Tool.checkNull(word).trim();
    
    HashMap<String, Object> map = new HashMap<>();
    map.put("cate_no", cate_no);
    map.put("word", word);
    map.put("now_page", now_page);
    
    ArrayList<Qna_contentsVO> list = this.qna_contentsProc.list_by_qna_search_paging(map);
    model.addAttribute("list", list);
    
    ArrayList<Qna_imageVO> qna_imageVO = this.qna_contentsProc.qna_list_all_image();
    System.out.println("-> list_all_image: " + qna_imageVO);
    model.addAttribute("qna_imageVO", qna_imageVO);
    
    model.addAttribute("word", word);
    
    // 페이징
    int search_count = this.qna_contentsProc.list_by_qna_search_count(map);
    String paging = this.qna_contentsProc.pagingBox(cate_no, now_page, word, "/qcontents/qna_list_all", 
        search_count, Contents.RECORD_PER_PAGE, Contents.PAGE_PER_BLOCK);
    model.addAttribute("paging", paging);
    model.addAttribute("now_page", now_page);
    model.addAttribute("search_count", search_count);
    
    // 일련 변호 생성: 레코드 갯수 - ((현재 페이지수 -1) * 페이지당 레코드 수)
    int no = search_count - ((now_page - 1) * Contents.RECORD_PER_PAGE);
    model.addAttribute("no", no);
    
    return "qcontents/list_by_qna_search_paging"; // /templates/qcontents/list_by_qna_search_paging.html
  }

}




///** 강사님 등록처리 성공한 코드
//* 질문글 등록 처리
//* @param model
//* @param request
//* @param session
//* @param ra
//* @return
//*/
//@PostMapping(value="/qna_create")
//public String qna_create(Model model,
//                       HttpServletRequest request,
//                       HttpSession session,
//                       RedirectAttributes ra,
//                       Qna_imageVO qna_imageVO,
//                       Qna_contentsVO qna_contentsVO) {
//
//// 질문글 등록 전 출력
//// System.out.println("-> [레코드 등록 전] qcon_no: " + qna_contentsVO.getQcon_no());
//
//// 카테고리 번호 가져오기
//int cate_no = qna_contentsVO.getCate_no(); // 부모글 번호
//
//// 질문글 등록 처리
//int cnt = this.qna_contentsProc.qna_create(qna_contentsVO);
//
//// 새로 등록된 질문글 번호 가져오기
//int qcon_no = qna_contentsVO.getQcon_no();
//System.out.println("-> [레코드 등록 후] qcon_no: " + qcon_no);
//
// // ---------------------------------------------------------------
// // 파일 전송 코드 시작
// // ---------------------------------------------------------------
// String file_origin_name = ""; // 원본 파일명
// String file_upload_name = ""; // 업로드된 파일명
// long file_size = 0;  // 파일 사이즈
// String file_thumb_name = ""; // Preview 이미지
// 
// String upDir = Contents.getUploadDir(); // 파일을 업로드할 폴더 준비
// 
// // 전송 파일이 없어서도 fnamesMF 객체가 생성됨.
// List<MultipartFile> fnamesMF = qna_imageVO.getFnamesMF();
// 
// int count = fnamesMF.size(); // 전송 파일 갯수
// 
// if (count > 0) {
//   for (MultipartFile multipartFile:fnamesMF) { // 파일 추출, 1개이상 파일 처리
//     file_size = multipartFile.getSize();  // 파일 크기
//     if (file_size > 0) { // 파일 크기 체크
//       file_origin_name = multipartFile.getOriginalFilename(); // 원본 파일명
//       file_upload_name = Upload.saveFileSpring(multipartFile, upDir); // 파일 저장, 업로드된 파일명
//       
//       if (Tool.isImage(file_origin_name)) { // 이미지인지 검사
//         file_thumb_name = Tool.preview(upDir, file_upload_name, 200, 150); // thumb 이미지 생성
//       }
//     }
//     
//     qna_imageVO.setQcon_no(qcon_no);
//     qna_imageVO.setFile_origin_name(file_origin_name);  // 실제 저장된 파일명
//     qna_imageVO.setFile_upload_name(file_upload_name);
//     qna_imageVO.setFile_thumb_name(file_thumb_name);
//     qna_imageVO.setFile_size(file_size);
//     
//     // 이미지 파일 등록 처리
//     int imageCnt = this.qna_contentsProc.qna_attach_create(qna_imageVO);
//
//   }
// }    
// // -----------------------------------------------------
// // 파일 전송 코드 종료
// // -----------------------------------------------------
//   
// ra.addAttribute("cate_no", cate_no);
// 
// return "redirect:/qcontents/qna_list_all";
//}
