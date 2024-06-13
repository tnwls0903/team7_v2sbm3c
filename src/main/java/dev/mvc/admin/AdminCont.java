package dev.mvc.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import dev.mvc.category.CategoryProcInter;
import dev.mvc.category.CategoryVO;
import dev.mvc.tool.MailTool;
import dev.mvc.tool.Security;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RequestMapping("/admin")
@Controller
public class AdminCont {
	@Autowired
	@Qualifier("dev.mvc.admin.AdminProc")
	private AdminProcInter adminProc;

	@Autowired
	@Qualifier("dev.mvc.category.CategoryProc")
	private CategoryProcInter categoryProc;

	@Autowired
	Security security;

	public AdminCont() {
		System.out.println("-> AdminCont created.");
	}

	/**
	 * 메인 화면
	 * 
	 * @param model
	 * @return
	 */
//	@GetMapping(value = "") // http://localhost:9093/admin
//	public String admin_main(Model model) {
//
//		return "admin/index";
//
//	}
	@GetMapping(value = "") // http://localhost:9093/admin
	public String admin_main(HttpSession session, Model model) {

		Integer adm_no = (Integer) session.getAttribute("adm_no");
		System.out.println("admin_main session ==> adm_no: " + adm_no);

		if (adm_no == null) {
			return "admin/login";
		} else {
			return "admin/index";
		}
		
	}

	/**
	 * 아이디 중복 확인
	 * 
	 * @param adm_id
	 * @return
	 */
	@GetMapping(value = "/checkID") // http://localhost:9093/admin/checkID?adm_id=admin2
	@ResponseBody
	public String checkID(String adm_id) {
		System.out.println("---> checkID adm_id: " + adm_id);

		int cnt = this.adminProc.checkID(adm_id);

		JSONObject obj = new JSONObject();
		obj.put("cnt", cnt);

		return obj.toString();
	}

	/**
	 * 이름 중복 확인
	 * 
	 * @param adm_name
	 * @return
	 */
	@GetMapping(value = "/checkName") // http://localhost:9093/admin/checkName?adm_name=admin2
	@ResponseBody
	public String checkName(String adm_name) {
		System.out.println("---> checkName adm_name: " + adm_name);

		int cnt = this.adminProc.checkName(adm_name);

		JSONObject obj = new JSONObject();
		obj.put("cnt", cnt);

		System.out.println("---> checkName cnt: " + cnt);

		return obj.toString();
	}

	/**
	 * 이메일 중복 확인
	 * 
	 * @param adm_email
	 * @return
	 */
	@GetMapping(value = "/checkEmail") // http://localhost:9093/admin/checkEmail?adm_email=ua@desk.tour
	@ResponseBody
	public String checkEmail(String adm_email) {
		System.out.println("---> checkEmail adm_email: " + adm_email);

		int cnt = this.adminProc.checkEmail(adm_email);

		JSONObject obj = new JSONObject();
		obj.put("cnt", cnt);

		return obj.toString();
	}

	/** 인증 번호 저장 변수 */
	private String verify_code;

	/**
	 * 인증 메일 전송
	 * 
	 * @return
	 */
	@PostMapping(value = "/mail")
	@ResponseBody
	public Map<String, Object> mail(String receiver) {
		System.out.println("---> AdminCont RECEIVER : " + receiver);

		Map<String, Object> response = new HashMap<>();

		try {
			/* 인증 번호 생성 */
			Random random = new Random();
			int code = random.nextInt(999999);
			verify_code = String.format("%06d", code);
			System.out.println("---> verify code : " + code);

			MailTool mailTool = new MailTool();
			mailTool.send(receiver, "-", "[DESK TOUR] 이메일 인증입니다.", "Verify Code : " + code); // 메일 전송
			response.put("status", "success");
		} catch (Exception e) {
			response.put("status", "error");
			response.put("msg", e.getMessage());
		}

		return response;
	}

	/**
	 * 인증 번호 확인
	 * 
	 * @param code
	 * @return
	 */
	@PostMapping(value = "/verifyCode")
	@ResponseBody
	public Map<String, Object> verifyCode(@RequestParam String code, @RequestParam String adm_id) {
		Map<String, Object> response = new HashMap<>();

		if (verify_code != null && verify_code.equals(code)) {
			response.put("status", "success");
			response.put("acc_id", adm_id);
		} else {
			response.put("status", "error");
			response.put("msg", "인증번호가 일치하지 않습니다.");
		}

		return response;
	}

	/**
	 * 회원 가입 폼
	 * 
	 * @param model
	 * @param adminVO
	 * @return
	 */
	@GetMapping(value = "/create") // http://localhost:9093/admin/create
	public String create(Model model, AdminVO adminVO) {
		List<CategoryVO> cate_list = this.categoryProc.cate_list_all();
		for (int i = 0; i < cate_list.size(); i++) {
			System.out.println("category: " + cate_list.get(i).getCate_name());
		}
		model.addAttribute("cate_list", cate_list);

		// String[] cateList = this.adminProc.cateList().split(",");
		// List<String> cate_names = Arrays.asList(cateList);
		// System.out.println("--> cate_names: " + cate_names);
		// model.addAttribute("cate_names", cate_names);

		return "admin/create";
	}

	/**
	 * 회원 가입 처리
	 * 
	 * @param adminVO
	 * @return
	 */
	@PostMapping(value = "/create")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> create_proc(AdminVO adminVO) {
		Map<String, Object> response = new HashMap<>();

		int checkID_cnt = this.adminProc.checkID(adminVO.getAdm_id());
		int checkName_cnt = this.adminProc.checkName(adminVO.getAdm_name());
		int checkEmail_cnt = this.adminProc.checkEmail(adminVO.getAdm_email());

		if ((checkID_cnt == 0) && (checkName_cnt == 0)) {
			int cnt = this.adminProc.create(adminVO);

			if (cnt != 0) { // 회원 가입 성공
				response.put("code", "create_success");
//				response.put("acc_id", accountVO.getAcc_id());
//				response.put("acc_name", accountVO.getAcc_name());

			} else {
				response.put("code", "create_fail");
				response.put("cnt", 0);
			}
			response.put("cnt", 1);
		} else if (checkID_cnt != 0) { // 아이디 중복
			response.put("code", "duplicate_id");
			response.put("cnt", 0);
		} else if (checkName_cnt != 0) {
			response.put("code", "duplicate_name");
			response.put("cnt", 0);

		} else if (checkEmail_cnt != 0) {
			response.put("code", "duplicate_email");
			response.put("cnt", 0);
		}

		return ResponseEntity.ok(response);
	}

	/**
	 * 회원 가입 메시지
	 * 
	 * @param model
	 * @param adminVO
	 * @param code
	 * @param cnt
	 * @return
	 */
	@GetMapping(value = "/msg")
	public String msg(Model model, AdminVO adminVO,
					@RequestParam(value = "code") String code,
					@RequestParam(value = "cnt") int cnt) {
		System.out.println("--> cnt: " + cnt);

		model.addAttribute("code", code);
		model.addAttribute("cnt", cnt);

		return "admin/msg";
	}
	
	/**
	 * 로그인 폼 (쿠키 기반)
	 * 
	 * @param request
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/login")
	public String login_form(Model model, HttpServletRequest request, AdmLogVO admLogVO, String url)  {

		/* Cookie */
		Cookie[] cookies = request.getCookies();
		Cookie cookie = null;

		String ck_adm_id = ""; // adm_id 저장
		String ck_id_save = ""; // adm_id 저장 여부 확인

		if (cookie != null) { // 쿠키 존재
			for (int i = 0; i < cookies.length; i++) {
				System.out.println("--> cookies[" + i + "]" + cookies[i]);
				cookie = cookies[i]; // 쿠키 객체 추출

				if (cookie.getName().equals("ck_adm_id")) {
					ck_adm_id = cookie.getValue(); // id
				} else if (cookie.getName().equals("ck_id_save")) {
					ck_id_save = cookie.getValue(); // Y, N
				}
			}
		}

		model.addAttribute("ck_id", ck_adm_id);
		model.addAttribute("ck_id_save", ck_id_save);
		model.addAttribute("url", url);
		
		return "admin/login";
	}
	
	
	/**
	 * 로그인 처리 (쿠키 기반)
	 * 
	 * @param model
	 * @return
	 */
	@PostMapping(value = "/login")
	public String login_proc(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model,
			AdmLogVO admLogVO, String url, String adm_id, String adm_pw,
			@RequestParam(value = "id_save", defaultValue = "") String id_save) {
		String ip = request.getRemoteAddr(); // IP
		System.out.println("---> 접속한 IP: " + ip);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("adm_id", adm_id);
		map.put("adm_pw", this.security.aesEncode(adm_pw));

		int cnt = this.adminProc.login(map);
		System.out.println("---> admin login_proc cnt: " + cnt);

		model.addAttribute("cnt", cnt);

		if (id_save.equals("Y")) { // Checkbox 체크, acc_id 저장
			Cookie ck_adm_id = new Cookie("adm_id", adm_id);
			ck_adm_id.setPath("/"); // root 폴더에 쿠키를 기록 -> 모든 경로에서 쿠키 접근 가능
			ck_adm_id.setMaxAge(60 * 60 * 24 * 30); // 30 days, 초단위
			response.addCookie(ck_adm_id); // acc_id 저장
		} else { // N, Checkbox 해제, acc_id 미저장
			Cookie ck_adm_id = new Cookie("ck_id", "");
			ck_adm_id.setPath("/");
			ck_adm_id.setMaxAge(0);
			response.addCookie(ck_adm_id);
		}

		/* Cookie - Checkbox 체크 확인 */
		Cookie ck_id_save = new Cookie("ck_id_save", id_save);
		ck_id_save.setPath("/");
		ck_id_save.setMaxAge(60 * 60 * 24 * 30); // 30 days
		response.addCookie(ck_id_save);
		if (url.length() > 0) {
			return "redirect:" + url;
		}

		if (cnt == 1) { // 로그인 성공
			// id를 이용한 회원 정보 조회
			AdminVO adminVO = this.adminProc.readById(adm_id);
			session.setAttribute("adm_no", adminVO.getAdm_no());
			// int adm_no = (int)session.getAttribute("adm_no"); // Session에서 가져오기

			session.setAttribute("adm_id", adminVO.getAdm_id());
			session.setAttribute("adm_name", adminVO.getAdm_name());

			if (adminVO.getAdm_grade() >= 1 && adminVO.getAdm_grade() <= 10) {
				session.setAttribute("adm_grade", "admin"); // 관리자
			} else if (adminVO.getAdm_grade() >= 30 && adminVO.getAdm_grade() <= 39) {
				session.setAttribute("adm_grade", "suspended"); // 정지 상태
			} else if (adminVO.getAdm_grade() == 99) {
				session.setAttribute("adm_grade", "withdrawn"); // 탈퇴 상태
			} else {
				session.setAttribute("adm_grade", "member");
			}

			/* 관리자 로그 저장 */
			admLogVO.setAdm_no(adminVO.getAdm_no());
			admLogVO.setAdm_log_ip(ip);
			int log_cnt = this.adminProc.recordLog(admLogVO);
			System.out.println("---> admin Record_log_cnt: " + log_cnt);

			/* Cookie - adm_id 관련 쿠키 저장 */
			Cookie ck_adm_id = new Cookie("adm_id", adm_id);
			ck_adm_id.setPath("/"); // root 폴더에 쿠키를 기록 -> 모든 경로에서 쿠키 접근 가능
			ck_adm_id.setMaxAge(60 * 60 * 24 * 30); // 30 days, 초단위
			response.addCookie(ck_adm_id); // acc_id 저장

			return "redirect:/admin";
		} else {
			model.addAttribute("code", "login_fail");
			model.addAttribute("cnt", 0);
			return "admin/msg";
		}

	}

	/**
	 * 로그아웃
	 * 
	 * @param session
	 * @param model
	 * @return
	 */
	@GetMapping(value = "/logout")
	public String logout(HttpSession session, Model model) {
		session.invalidate(); // 모든 세션 변수 삭제
		
		System.out.println("---> 관리자 로그아웃");
		return "redirect:/admin";
	}

}
