package com.project.team.plice.controller;

import com.project.team.plice.dto.member.MemberDto;
import com.project.team.plice.service.interfaces.AdminService;
import com.project.team.plice.service.interfaces.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class JoinController {

    private final AdminService adminService;
    private final MemberService memberService;

    @GetMapping("/sign-up")
    public String signUpForm(HttpServletRequest request, Authentication authentication){
        adminService.logAccess(request, authentication);
        return "layout-content/join/sign-up";
    }

    @GetMapping("/join")
    public String joinForm(HttpServletRequest request, Authentication authentication){
        adminService.logAccess(request, authentication);
        return "layout-content/join/join";
    }

    @GetMapping("/join-success")
    public String joinSuccess(HttpServletRequest request, Authentication authentication) {
        adminService.logAccess(request, authentication);
        return "layout-content/join/join-success";
    }

    @PostMapping("/join")
    public String joinMember(@ModelAttribute MemberDto memberDto){
        memberService.join(memberDto);
        return "redirect:/login";
    }

    @GetMapping("/term-service")
    public String termServiceForm(HttpServletRequest request, Authentication authentication){
        adminService.logAccess(request, authentication);
        return "layout-content/join/term-service";
    }

    @GetMapping("/use-personal")
    public String usePersonalForm(HttpServletRequest request, Authentication authentication){
        adminService.logAccess(request, authentication);
        return "layout-content/join/use-personal";
    }

    @GetMapping("/marketing")
    public String marketingForm(HttpServletRequest request, Authentication authentication){
        adminService.logAccess(request, authentication);
        return "layout-content/join/marketing";
    }

    @GetMapping("/term-of-service")
    public String termOfServiceForm(HttpServletRequest request, Authentication authentication){
        adminService.logAccess(request, authentication);
        return "layout-content/join/term-of-service";
    }
}
