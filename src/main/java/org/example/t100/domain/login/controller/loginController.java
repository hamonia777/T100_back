package org.example.t100.domain.login.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Iterator;

@Controller
@ResponseBody
public class loginController {

    @GetMapping("/")
    public String mainP() {

        String name = SecurityContextHolder.getContext().getAuthentication().getName();


//        JWT가 session을 stateless 한 채로 진행되긴 하지만 1회성에 한하여 과정 중에 잠시 세션이 생서되게 되는데 이때
//        JWTFilter를 통과한 뒤 세션을 확인하는 것이 가능하다.



        System.out.println(name);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority authority = iterator.next();
        String role = authority.getAuthority();
        System.out.println(role);


        return "Main Controller " + name +role;

    }
}
