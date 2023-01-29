package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
    public class MemberController {
    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {
        //화면에서 MemberForm객체에 접근할 수 있다.
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }


    @PostMapping("/members/new")
    //서브밋을 통해 포스트로 넘어온것 valid어노테이션 이용
            public String create(@Valid MemberForm form, BindingResult result) {
    //valid옆에 바인딩 리설트가 뜬다면 화이트 에러 대신에 오류가 담겨서 안의 코드가 실행된다.
        if(result.hasErrors()){
            //오류가 생겼으면 이동하도록
            return "members/createMemberForm";
        }
        //폼에서 가져온 시티,스트릿,..
        Address address = new Address(form.getCity(), form.getStreet(),form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        //저장이 끝나면 리다이렉트 즉 첫번째 페이지로 이동
        return "redirect:/";

    }
//회원목록 조회
    @GetMapping("/members")
    public String list(Model model){
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }
}
