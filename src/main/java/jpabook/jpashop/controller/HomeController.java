package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class HomeController {
    @RequestMapping("/") // 첫번째 화면에 잡히는것임 / 이기 때문에
    public String home(){
        log.info("home controller"); //홈 컨트롤러의 로그 보기
        return "home";

    }
}
