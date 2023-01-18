// JavaScript
function onlyNumber(obj) {
  obj.value = obj.value.replace(/[^0-9]/g, "");
}

// Jquery
$(function () {
  $(".login_repwd_btn").click(function () {
    $(".pwd_find_container").show().addClass("bg_white");
    $(".background").addClass("bg_black");
  });

  $(".fa-times").click(function () {
      $(".pwd_find_container").hide().removeClass("bg_white");
      $(".pwd_find_check_input").val("").css("border","1px solid #c9d5ff").attr("readonly", false);
      $(".accept_opt").fadeOut(300); // 0.3초
      $(".hide_text").hide();
      $(".background").removeClass("bg_black");
      $(".pwd_find_btn").css("background-color", "#dfdfdf").css("color", "whte").html("인증번호 받기");
  });

  $(".accept_btn").click(function () {
    $(".pwd_find_container").hide().removeClass("bg_white");
    $(".background").removeClass("bg_black");
  });

/*
 $(".background").click(function (e) {
    console.log($(e.target) == $(".background"));
    if($(e.target) != $(".background")) {
      $(".pwd_find_container").hide().removeClass("bg_white");
      $(".pwd_find_check_input").val("").css("border","1px solid #c9d5ff");
      $(".accept_opt").fadeOut(300); // 0.3초
      $(".hide_text").hide();
      $(".background").removeClass("bg_black");

    }
 });
*/


 $(".pwd_find_container .pwd_find_check_input").on("input", function () {
  (async () => {
     const idRgx = /01[016789][0-9]{4}[0-9]{4}/;
     const idInput = document.querySelector(".pwd_find_container .pwd_find_check_input").value;
     const result = await fetch("/login/check?idInput=" + idInput).then(res => res.text());
     console.log(result);
     if(result != "ok") {    // 일치하는 번호가 없음
         $(".accept_opt").fadeOut(300); // 0.3초
         $(".pwd_find_check_input").css("border", "1px solid red");
         $(".pwd_find_btn").css("background-color", "red").css("color", "white").html("가입된 번호가 없습니다.");
     }else{     // 일치함
         fetch("/login/send-message?phone=" + idInput).then(res => console.log(res.text()));
         $(".accept_opt").fadeIn(300); // 0.3초
         $(".pwd_find_check_input").css("border", "1px solid blue").attr("readonly", true);
         $(".pwd_find_btn").css("background-color", "blue").css("color", "white").html("문자인증을 진행해주세요.");
     }
   })();
 });



});
