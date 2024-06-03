package dev.mvc.reply;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dev.mvc.reply.ReplyProc")
public class ReplyProc implements ReplyProcInter {
  @Autowired
  private ReplyDAOInter replyDAO;

@Override
public int create_comment(HashMap<String, Object> map) {
	int cnt = this.replyDAO.create_comment(map);
	return cnt;
}

@Override
public ArrayList<Share_commentsVO> list() {
	ArrayList<Share_commentsVO> list = this.replyDAO.list();
	return list;
}

@Override
public ArrayList read_comment(int scon_no) {
	ArrayList<Share_commentsVO> list = this.replyDAO.read_comment(scon_no);
	return list;
}

@Override
public int update_comment(HashMap<String, Object> map) {
	int cnt = this.update_comment(map);
	return cnt;
}

@Override
public int sdelete_comment(List<Integer> scon_no) {
	int cnt = this.replyDAO.sdelete_comment(scon_no);
	return cnt;
}

@Override
public int delete_scmtno(int scmt_no) {
	int cnt = this.replyDAO.delete_scmtno(scmt_no);
	return cnt;
}

@Override
public int delete_comments(int scon_no) {
	int cnt = this.replyDAO.delete_comments(scon_no);
	return cnt;
}

@Override
public int comment_search(int scon_no) {
	int cnt = this.replyDAO.comment_search(scon_no);
	return cnt;
}


  


}