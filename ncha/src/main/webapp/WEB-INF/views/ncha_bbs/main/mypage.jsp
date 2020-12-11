<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<style type="text/css">
.body-container{
	display: flex;
	flex-direction: rows;
	flex-wrap:wrap;
	align-items: center;
	justify-content: center;
}
.post-list {
  display: grid;
  grid-template-columns: repeat(3, minmax(100px, 293px));
  grid-template-rowls: repeat(auto, minmax(100px, 293px));
  justify-content: center;
  gap: 28px;
}
.post {
  cursor: pointer;
  position: relative;
  display: block; /*Es un ancla por eso le cambio el displya*/
  background-repeat: no-repeat;
  background-position: center;
  background-size: cover;
}
.post-image {
  margin: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}
.image{
	width: 100%;
	height: 100%;
}
.post-image img {
  object-fit: cover;
  width:100%;
  height: 293px;
}
.post-overlay {
  background-color: rgb(0, 0, 0, 0.4);
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  top: 0;
  display: none;
  align-items: center;
  justify-content: center;
  color: white;
  text-align: center;
}
.post:hover .post-overlay {
  display: flex;
}
.post-likes,
.post-comments {
  width: 80px;
  margin: 5px;
  font-weight: bold;
  text-align: center;
  display: inline-block;
}

.profile-title{
	display: grid;
	grid-template-columns: repeat(3, minmax(100px, 293px));
	grid-template-rows: repeat(4,100px);
	grid-template-areas:
    "img name setting"
    "img follower following"
    "img intro intro"
    "tab tab tab";
}
.profile-img{
	grid-area:img;
	display: flex;
	justify-content: center;
	align-items: center;
}
.imgs{
	width: 200px; 
	height: 200px; 
	background-repeat: no-repeat;
	background-position: center;
	background-size: cover;
	border-radius: 50%;
	border: 1px solid silver;
}
.profile-introduce{
	grid-area:intro;
}
.profile-name{
	grid-area:name;
	display: flex;
	align-items: flex-end;
	align-content: center;
}
.profile-setting{
	grid-area:setting;
	display: flex;
	align-items: flex-end;
	align-content: center;
}
.profile-follower{
	grid-area:follower;
	display: flex;
	align-items: center;
	align-content: center;	
}
.profile-following{
	grid-area:following;
	display: flex;
	align-items: center;
	align-content: center;	
}
.profile-tab{
	grid-area:tab;
	display: flex;
	align-items: center;
	justify-content:center;
	border-top: 1px solid #ccc;
	font-size : 20px;
}
#profile-tabbox{
	width: 10%;
	height: 90px; 
	padding-top: 10px; 
	margin:10px; 
	border-top: 2px solid black;
	text-align: center;
}
@media screen and (max-width: 768px) {
	  .post-list {
	    gap: 3px;
	  }
	  .post-image img {
	  height: 100px;
	  }
}

</style>
<div class="body-container" style="width: 1080px;">
    <div class="profile-title">
    	<div class="profile-img">
    		<div class="imgs" style="background-image:url('${pageContext.request.contextPath}/resources/img/logo.png');">
    		
    		</div>
    	</div>
    	<div class="profile-introduce">안녕하세요. n차신상 개발자 이영헌입니다.</div>
    	<div class="profile-name">
    		<span><h1>USERID </h1></span>
    	</div>
    	<div class="profile-setting">
    		<span><a href=""><i class="fas fa-user-cog"></i>회원정보수정 </a></span>
    	</div>
    	<div class="profile-follower"><strong>팔로워 </strong>&nbsp;&nbsp;&nbsp;1000</div>
    	<div class="profile-following"><strong>팔로잉 </strong>&nbsp;&nbsp;&nbsp;1000</div>
    	<div class="profile-tab">
    		<div id="profile-tabbox" style="width: 10%; height: 90px; padding-top: 10px;">일상글</div>
    		<div id="profile-tabbox" style="width: 10%; height: 90px; padding-top: 10px;">중고글</div>
    		
    	</div>
    </div>
    
    
    
    
  <div class="post-list">
    <a href="" class="post" style="background-image: url('${pageContext.request.contextPath}/resources/img/logo.png');">
      <span class="post-overlay">
        <p>
          <span class="post-likes">150</span>
          <span class="post-comments">10</span>
        </p>
      </span>
    </a>
    <a href="" class="post" style="background-image: url('${pageContext.request.contextPath}/resources/img/logo.png');">
      <span class="post-overlay">
        <p>
          <span class="post-likes">150</span>
          <span class="post-comments">10</span>
        </p>
      </span>
    </a>

    <a href="" class="post">
      <div class="post-image">
      	<img alt="" src="${pageContext.request.contextPath}/resources/img/23627.jpg">
      </div>
      <span class="post-overlay">
        <p>
          <span class="post-likes">150</span>
          <span class="post-comments">10</span>
        </p>
      </span>
    </a>    <a href="" class="post">
      <div class="post-image">
        <img src="${pageContext.request.contextPath}/resources/img/logo.png" alt="">
      </div>
      <span class="post-overlay">
        <p>
          <span class="post-likes">150</span>
          <span class="post-comments">10</span>
        </p>
      </span>
    </a>    <a href="" class="post">
      <div class="post-image">
        <img src="${pageContext.request.contextPath}/resources/img/logo.png" alt="">
      </div>
      <span class="post-overlay">
        <p>
          <span class="post-likes">150</span>
          <span class="post-comments">10</span>
        </p>
      </span>
    </a>    <a href="" class="post">
      <div class="post-image">
        <img src="${pageContext.request.contextPath}/resources/img/logo.png" alt="">
      </div>
      <span class="post-overlay">
        <p>
          <span class="post-likes">150</span>
          <span class="post-comments">10</span>
        </p>
      </span>
    </a>    <a href="" class="post">
      <div class="post-image">
        <img src="https://image.freepik.com/foto-gratis/mujeres-sosteniendo-tazas-cafe-mesa-madera_23-2147935600.jpg" alt="">
      </div>
      <span class="post-overlay">
        <p>
          <span class="post-likes">150</span>
          <span class="post-comments">10</span>
        </p>
      </span>
    </a>    <a href="" class="post">
      <div class="post-image">
        <img src="https://image.freepik.com/foto-gratis/mujeres-sosteniendo-tazas-cafe-mesa-madera_23-2147935600.jpg" alt="">
      </div>
      <span class="post-overlay">
        <p>
          <span class="post-likes">150</span>
          <span class="post-comments">10</span>
        </p>
      </span>
    </a>   
  </div>
</div>
