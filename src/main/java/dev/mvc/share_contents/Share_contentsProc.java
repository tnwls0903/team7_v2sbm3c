package dev.mvc.share_contents;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		Share_contentsVO scontents=this.scontentsDAO.read(scon_no);
		return scontents;
	}

}