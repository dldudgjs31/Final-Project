package com.sp.app.store;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sp.app.common.FileManager;
import com.sp.app.common.MyUtil;
import com.sp.app.customer.Customer;
import com.sp.app.customer.CustomerService;
import com.sp.app.review.ReviewService;
import com.sp.app.seller.Seller;
import com.sp.app.seller.SellerService;
import com.sp.app.seller.SessionInfo;

@Controller("store.storeController")
@RequestMapping("/store/*")
public class StoreController {

	@Autowired
	private StoreService service;

	@Autowired
	private CustomerService service3;
	@Autowired
	private MyUtil myUtil;

	@Autowired
	private ReviewService service2;
	
	@Autowired
	private SellerService service4;
	
	@Autowired
	private FileManager fileManager;
	/**
	 * 스토어 상품리스트
	 * @return
	 * @throws Exception
	 */
	
	@RequestMapping("list")
	public String list(
			@RequestParam(value = "page", defaultValue = "1") int current_page,
			@RequestParam(defaultValue = "all") String condition, 
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "") String categoryNum,
			HttpServletRequest req, Model model) throws Exception {

		int rows = 9;
		int total_page = 0;
		int dataCount = 0;

		if (req.getMethod().equalsIgnoreCase("GET")) {
			keyword = URLDecoder.decode(keyword, "utf-8");
		}
		Map<String, Object> map = new HashMap<>();
		map.put("condition", condition);
		map.put("keyword", keyword);
		map.put("categoryNum", categoryNum);
		String categoryName="";
		categoryName = service.readCategoryName(categoryNum);
		if(categoryName == "") {
			categoryName = "전체 상품";
		}
		dataCount = service.dataCount(map);
		if (dataCount != 0) {
			total_page = myUtil.pageCount(rows, dataCount);
		}
		if (total_page < current_page) {
			current_page = total_page;
		}
		int offset = (current_page - 1) * rows;
		if (offset < 0)
			offset = 0;
		map.put("offset", offset);
		map.put("rows", rows);
		
		List<Store> list = service.listProduct(map);
		int listNum=0; 
		int n = 0;
		for (Store dto : list) {
			listNum = dataCount - (offset + n);
			double score = service2.reviewScore(dto.getProductNum());
			int likeCount = service.dataProductLikeCount(dto.getProductNum());
			int storeFollow = service.dataStoreFollowCount(dto.getSellerId());
			dto.setLikeCount(likeCount);
			dto.setStoreFollowCount(storeFollow);
			if(score>4.5) {
				dto.setScore("★★★★★");
			}else if(score>3.5) {
				dto.setScore("★★★★");
			}else if(score>2.5) {
				dto.setScore("★★★");
			}else if(score>1.5) {
				dto.setScore("★★");
			}else if(score>0.5){
				dto.setScore("★");
			}else {
				dto.setScore("평가 없음");
			}
			dto.setListNum(listNum);
			n++;
		}

		String cp = req.getContextPath();
		String query = "";
		String listUrl = cp + "/store/list";
		String articleUrl = cp + "/store/article?page=" + current_page;
		if (categoryNum.length()!=0||keyword.length() != 0) {
			query = "condition=" + condition+"&categoryNum="+categoryNum + "&keyword" + URLEncoder.encode(keyword, "utf-8");
		}

		if (query.length() != 0) {
			listUrl += "?" + query;
			articleUrl += "&" + query;
		}
		String paging = myUtil.paging(current_page, total_page, listUrl);
		
		model.addAttribute("categoryName", categoryName);
		model.addAttribute("list", list);
		model.addAttribute("articleUrl", articleUrl);
		model.addAttribute("page", current_page);
		model.addAttribute("total_page", total_page);
		model.addAttribute("dataCount", dataCount);
		model.addAttribute("paging", paging);
		model.addAttribute("condition", condition);
		model.addAttribute("categoryNum", categoryNum); 
		model.addAttribute("keyword", keyword);
		return ".store.product.list";
	}

	/**
	 * 스토어 상품 올리기
	 * @return
	 * @throws Exception
	 */
	@GetMapping("created")
	public String createdForm(Model model) throws Exception{
		model.addAttribute("mode", "created");
		return ".store.product.created";
	}
	@PostMapping("created")
	public String creatdSubmit(
			Store dto,
			HttpSession session,
			Model model
			) throws Exception{
		SessionInfo info=(SessionInfo)session.getAttribute("seller");
		
		String root = session.getServletContext().getRealPath("/");
		String pathname = root+"uploads"+File.separator+"product";
		
		try {
			dto.setSellerId(info.getSellerId());
			service.insertProduct(dto, pathname);
			service.insertOption(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addAttribute("pathname",pathname);
		return "redirect:/store/list";
	}
	
	
	/**
	 * 스토어 상품 글보기
	 * @return
	 * @throws Exception
	 */
	@GetMapping("article")
	public String article(
			@RequestParam int num,
			@RequestParam String page,
			@RequestParam(defaultValue = "all") String condition, 
			@RequestParam(defaultValue="") String categoryNum,
			@RequestParam(defaultValue = "") String keyword,
			@RequestParam(defaultValue = "") String message,
			@RequestParam(defaultValue = "") String order,
			HttpSession session,
			Model model) throws Exception {
		keyword = URLDecoder.decode(keyword, "utf-8");
		message = URLDecoder.decode(message, "utf-8");

		String query = "page=" + page;
		if (categoryNum.length()!=0 ||keyword.length() != 0) {
			query += "&categoryNum="+categoryNum+"&condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8");
		}
		service.updateHitCount(num);

		Store dto = service.readProduct(num);
		if (dto == null) {
			return "redirect:/store/list?" + query;
		}
		int likeCount = service.dataProductLikeCount(num);
		dto.setLikeCount(likeCount);
		int storeFollowCount = service.dataStoreFollowCount(dto.getSellerId());
		dto.setStoreFollowCount(storeFollowCount);
		double score = service2.reviewScore(dto.getProductNum());
		if(score>4.5) {
			dto.setScore("★★★★★");
		}else if(score>3.5) {
			dto.setScore("★★★★");
		}else if(score>2.5) {
			dto.setScore("★★★");
		}else if(score>1.5) {
			dto.setScore("★★");
		}else if(score>0.5){
			dto.setScore("★");
		}else {
			dto.setScore("평가 없음");
		}
		List<Store> list1 = service.readProductFile(num);
		List<Store> listFile = service.listFile(num);
		List<Store> optionList = service.readOption(num);
		
		Map<String, Object> map = new HashMap<>();
		map.put("num", num);
		map.put("condition", condition);
		map.put("categoryNum", categoryNum);
		map.put("keyword", keyword);
		Store preReadDto = service.preReadProduct(map);
		Store nextReadDto = service.nextReadProduct(map);

		if(message.length()!=0) {
			model.addAttribute("message", message);			
		}
		model.addAttribute("productNum",num);
		model.addAttribute("order", order);
		model.addAttribute("dto", dto);
		model.addAttribute("list1", list1);
		model.addAttribute("listFile", listFile);
		model.addAttribute("optionList", optionList);
		model.addAttribute("preReadDto", preReadDto);
		model.addAttribute("nextReadDto", nextReadDto);
		model.addAttribute("page", page);
		model.addAttribute("query", query);

		return ".store.product.article";
	}
	
	@GetMapping("update")
	public String updateForm(
			@RequestParam int num,
			@RequestParam String page,
			HttpSession session,
			Model model
			)throws Exception{
		SessionInfo info =(SessionInfo)session.getAttribute("seller");
		Store dto = service.readProduct(num);
		List<Store> listFile = service.listFile(num);
		List<Store> list1 = service.readProductFile(num);
		List<Store> optionList = service.readOption(num);
		
		if(dto==null) {
			return "redirect:/store/list?page="+page;
		}
		
		if(!info.getSellerId().equals(dto.getSellerId())){
			return "redirect:/store/list?page="+page;
		}
		model.addAttribute("list1", list1);
		model.addAttribute("listFile", listFile);
		model.addAttribute("optionList", optionList);
		model.addAttribute("dto",dto);
		model.addAttribute("mode","update");
		model.addAttribute("page",page);
		
		return ".store.product.created";
	}
	
	@PostMapping("update")
	public String updateSubmit(
			Store dto,
			@RequestParam String page,
			HttpSession session
			) throws Exception{
		
		try {
			service.updateProduct(dto);
			service.deleteAllOption(dto.getProductNum());
			service.insertOption(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/store/list?page=" + page;
	}
	/**
	 * 글지우기
	 * @param page
	 * @param num
	 * @param condition
	 * @param keyword
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("delete")
	public String delete(
			@RequestParam String page,
			@RequestParam int num,
			@RequestParam(defaultValue = "all") String condition,
			@RequestParam(defaultValue = "") String keyword,
			HttpSession session
			) throws Exception {
		SessionInfo info = (SessionInfo)session.getAttribute("seller");
		
		keyword = URLDecoder.decode(keyword,"utf-8");
		String query = "page="+page;
		if(keyword.length()!=0) {
			query +="&condition="+condition+"&keyword="+URLEncoder.encode(keyword,"utf-8");
		}
			service.delete(num, info.getSellerId());
		return "redirect:/store/list?"+query;
	}
	
	//글 수정시 이미지 삭제
	@RequestMapping("deleteFile")
	@ResponseBody
	public void deleteImage(
			@RequestParam int main_imageFileNum
			)throws Exception {
		try {
			service.deleteImage(main_imageFileNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("deleteOption")
	@ResponseBody
	public void deleteOption(
			@RequestParam int optionNum
			)throws Exception{
		try {
			service.deleteOption(optionNum);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@RequestMapping("updateLike")
	@ResponseBody
	public Map<String, Object> updateLike(
			@RequestParam int productNum,
			HttpSession session
			)throws Exception{
		com.sp.app.member.SessionInfo info = (com.sp.app.member.SessionInfo)session.getAttribute("member");
		String state ="false";
		try {
			Map<String, Object> map = new HashedMap<>();
			map.put("productNum", productNum);
			map.put("userId", info.getUserId());
			int check = service.checkLike(map);
			if(check==0) {
				service.insertLike(map);
				state="true";
			}if(check==1) {
				service.deleteLike(map);
				state="deltrue";
			}
		} catch (Exception e) {
			e.printStackTrace();
			state ="false";
		}
		Map<String, Object> model=new HashedMap<>();
		model.put("state", state);
		return model;
	}
	@RequestMapping("updateLikepage")
	@ResponseBody
	public Map<String, Object> updateLikepage(
			@RequestParam int productNum,
			HttpSession session
			)throws Exception{
		com.sp.app.member.SessionInfo info = (com.sp.app.member.SessionInfo)session.getAttribute("member");
		int check = 0;
		try {
			Map<String, Object> map = new HashedMap<>();
			map.put("productNum", productNum);
			map.put("userId", info.getUserId());
			check=service.checkLike(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Object> model=new HashedMap<>();
		model.put("check", check);
		return model;
	}
	@RequestMapping("cartHeader")
	@ResponseBody
	public Map<String, Object> cartHeader(
			@RequestParam int page,
			HttpSession session
			)throws Exception{
		com.sp.app.member.SessionInfo info = (com.sp.app.member.SessionInfo)session.getAttribute("member");
		int check=0;
		try {
			Map<String, Object> map = new HashedMap<>();
			map.put("userId", info.getUserId());
			check=service3.dataCartCount(info.getUserId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Object> model=new HashedMap<>();
		model.put("check", check);
		return model;
	}
	@RequestMapping("updateStoreLike")
	@ResponseBody
	public Map<String, Object> updateStoreLike(
			@RequestParam String sellerId,
			HttpSession session
			)throws Exception{
		com.sp.app.member.SessionInfo info = (com.sp.app.member.SessionInfo)session.getAttribute("member");
		String state ="false";
		try {
			Map<String, Object> map = new HashedMap<>();
			map.put("sellerId", sellerId);
			map.put("userId", info.getUserId());
			int check = service.checkStoreLike(map);
			if(check==0) {
				service.insertStoreLike(map);
				state="true";
			}if(check==1) {
				service.deleteStoreLike(map);
				state="deltrue";
			}
		} catch (Exception e) {
			e.printStackTrace();
			state ="false";
		}
		Map<String, Object> model=new HashedMap<>();
		model.put("state", state);
		return model;
	}
	
	@RequestMapping("updateFollowpage")
	@ResponseBody
	public Map<String, Object> updateFollowpage(
			@RequestParam String sellerId,
			HttpSession session
			)throws Exception{
		com.sp.app.member.SessionInfo info = (com.sp.app.member.SessionInfo)session.getAttribute("member");
		int check = 0;
		try {
			Map<String, Object> map = new HashedMap<>();
			map.put("sellerId", sellerId);
			map.put("userId", info.getUserId());
			check=service.checkStoreLike(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Object> model=new HashedMap<>();
		model.put("check", check);
		return model;
	}
	
	@RequestMapping("myFollowStore")
	public String myFollowStore(
			@RequestParam(value = "page", defaultValue = "1") int current_page,
			@RequestParam String sellerId,
			HttpSession session,
			HttpServletRequest req,
			Model model
			)throws Exception{
		int rows = 10;
		int total_page = 0;
		int dataCount = 0;
		Map<String, Object> map = new HashMap<>();
		map.put("sellerId",sellerId);
		dataCount =service.dataMyProductCount(map);
		if (dataCount != 0) {
			total_page = myUtil.pageCount(rows, dataCount);
		}
		if (total_page < current_page) {
			current_page = total_page;
		}
		int offset = (current_page - 1) * rows;
		if (offset < 0)
			offset = 0;
		map.put("offset", offset);
		map.put("rows", rows);
		List<Store> list = service.listMyProduct(map);
		int listNum=0; 
		int n = 0;
		if(list !=null) {
			for (Store dto : list) {
				double score = service2.reviewScore(dto.getProductNum());
				int likeCount = service.dataProductLikeCount(dto.getProductNum());
				int storeFollow = service.dataStoreFollowCount(dto.getSellerId());
				dto.setLikeCount(likeCount);
				dto.setStoreFollowCount(storeFollow);
				if(score>4.5) {
					dto.setScore("★★★★★");
				}else if(score>3.5) {
					dto.setScore("★★★★");
				}else if(score>2.5) {
					dto.setScore("★★★");
				}else if(score>1.5) {
					dto.setScore("★★");
				}else if(score>0.5){
					dto.setScore("★");
				}else {
					dto.setScore("평가 없음");
				}
				listNum = dataCount - (offset + n);
				dto.setListNum(listNum);
				n++;
			}
		}
		int followCount = service.dataStoreFollowCount(sellerId);
		Seller sellerDto = service4.readSeller(sellerId);
		
		
		String cp = req.getContextPath();
		String listUrl = cp + "/store/customer/myFollowStore";
		String paging = myUtil.paging(current_page, total_page, listUrl);
		model.addAttribute("page", current_page);
		model.addAttribute("total_page", total_page);
		model.addAttribute("dataCount", dataCount);
		model.addAttribute("paging", paging);
		model.addAttribute("list", list);
		model.addAttribute("sellerId", sellerId);
		model.addAttribute("sellerDto", sellerDto);
		model.addAttribute("followCount", followCount);
		return ".store.mypage.mystore";
	}
}
