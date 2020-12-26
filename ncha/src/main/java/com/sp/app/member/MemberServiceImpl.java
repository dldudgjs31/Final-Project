package com.sp.app.member;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sp.app.common.FileManager;
import com.sp.app.common.dao.CommonDAO;

@Service("member.memberService")
public class MemberServiceImpl implements MemberService {
	@Autowired
	private CommonDAO  dao;

	@Autowired
	private FileManager fileManager;
	
	@Override
	public Member loginMember(String userId) {
		Member dto=null;
		
		try {
			dto=dao.selectOne("member.loginMember", userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dto;
	}
	
	@Override
	public void insertMember(Member dto, String pathname) throws Exception {
		try {

			String serverFilename = fileManager.doFileUpload(dto.getUploadphoto(), pathname);
			if(serverFilename != null) {
				dto.setProfile_imageFilename(serverFilename);
			}

			if(dto.getEmail1().length()!=0 && dto.getEmail2().length()!=0) {
				dto.setEmail(dto.getEmail1() + "@" + dto.getEmail2());
			}
			
			if(dto.getTel1().length()!=0 && dto.getTel2().length()!=0 && dto.getTel3().length()!=0) {
				dto.setTel(dto.getTel1() + "-" + dto.getTel2() + "-" + dto.getTel3());
			}
			
			long memberSeq = dao.selectOne("member.memberSeq");
			dto.setMemberIdx(memberSeq);
			
			dao.updateData("member.insertMember12", dto); 
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Override
	public Member readMember(String userId) {
		Member dto=null;
		
		try {
			dto=dao.selectOne("member.readMember1", userId);
			
			if(dto!=null) {
				if(dto.getEmail()!=null) {
					String [] s=dto.getEmail().split("@");
					dto.setEmail1(s[0]);
					dto.setEmail2(s[1]);
				}

				if(dto.getTel()!=null) {
					String [] s=dto.getTel().split("-");
					dto.setTel1(s[0]);
					dto.setTel2(s[1]);
					dto.setTel3(s[2]);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dto;
	}
	
	@Override
	public Member readMember(long memberIdx) {
		Member dto=null;
		
		try {
			dto=dao.selectOne("member.readMember", memberIdx);
			
			if(dto!=null) {
				if(dto.getEmail()!=null) {
					String [] s=dto.getEmail().split("@");
					dto.setEmail1(s[0]);
					dto.setEmail2(s[1]);
				}

				if(dto.getTel()!=null) {
					String [] s=dto.getTel().split("-");
					dto.setTel1(s[0]);
					dto.setTel2(s[1]);
					dto.setTel3(s[2]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dto;
	}
	
	@Override
	public void updateMembership(Map<String, Object> map) throws Exception {
		try {
			dao.updateData("member.updateMembership", map);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Override
	public void updateLastLogin(String userId) throws Exception {
		try {
			dao.updateData("member.updateLastLogin", userId);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@Override
	public void updateMember(Member dto, String pathname) throws Exception {
		try {
			
			String serverFilename = fileManager.doFileUpload(dto.getUploadphoto(), pathname);
			if(serverFilename != null) {
				if(dto.getProfile_imageFilename().length()!=0) {
					fileManager.doFileDelete(dto.getProfile_imageFilename(), pathname);
				}
			}
			
			dto.setProfile_imageFilename(serverFilename);
			
			
			if(dto.getEmail1().length()!=0 && dto.getEmail2().length()!=0) {
				dto.setEmail(dto.getEmail1() + "@" + dto.getEmail2());
			}
			
			if(dto.getTel1().length()!=0 && dto.getTel2().length()!=0 && dto.getTel3().length()!=0) {
				dto.setTel(dto.getTel1() + "-" + dto.getTel2() + "-" + dto.getTel3());
			}
			
			dao.updateData("member.updateMember", dto);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void deleteMember(Map<String, Object> map) throws Exception {
		try {
			map.put("membership", 0);
			updateMembership(map);
			
			dao.deleteData("member.deleteMember", map);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public int dataCount(Map<String, Object> map) {
		int result=0;
		try {
			result=dao.selectOne("list.dataCountMember", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public List<Member> listMember(Map<String, Object> map) {
		List<Member> list=null;
		
		try {
			list=dao.selectList("list.listMember", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public Member readProfile(String userId) throws Exception {
		Member dto= null;
		try {
			dto=dao.selectOne("member.readProfile", userId);
			dto.setFollowerCount(dao.selectOne("member.followerCount",userId));
			dto.setFollowingCount(dao.selectOne("member.followingCount",userId));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return dto;
	}

	@Override
	public List<Member> listFollower(Map<String, Object> map) {
		List<Member> list=null;
		try {
			list=dao.selectList("member.followerList", map);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Member> listFollowing(Map<String, Object> map) {
		List<Member> list=null;
		try {		
			list=dao.selectList("member.followingList", map);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public int FollowerCount(String userId) {		
		int result = 0;	
		try {
			result = dao.selectOne("member.followerCount",userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int FollowingCount(String userId) {
		int result = 0 ;		
		try {
			result = dao.selectOne("member.followingCount",userId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public void deleteFollower(Map<String, Object> map) throws Exception {
		try {
			dao.deleteData("member.deleteFollower",map);	
		} catch (Exception e) {
			throw e;
		}

	}
	
	/*
	 * @Override public void deleteFollower(String userId1) throws Exception { try {
	 * dao.deleteData("member.deleteFollower",userId1); } catch (Exception e) {
	 * throw e; }
	 * 
	 * }
	 * 
	 * @Override public void deleteFollowing(String userId2) throws Exception { try
	 * { dao.deleteData("member.deleteFollowing",userId2); } catch (Exception e) {
	 * throw e; }
	 * 
	 * }
	 */
}
