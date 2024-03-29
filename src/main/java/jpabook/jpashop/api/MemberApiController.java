package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import java.util.List;
import java.util.stream.Collectors;

//@Controller
//@ResponseBody 두개를 합친 어노테이션이 바로 아래 어노테이션이다.
@RestController

//클라이언트에서 서버로 통신하는 메시지를 (request)메시지, 반대를 respense메시지라고한다.
//새로고침없이 하는 웹에서의 화면전환을 비동기통신이라고하는데 이때 서버로 요청메시지를 보낼때 본문에
//즉 body에 담아 보내야하고 이를 요청본문(requestBody),응답본문 respenseBody를 담아서 보내야한다.

@RequiredArgsConstructor
//final이 붙거나 @Notnull이 붙은 필드의 생성자를 자동 생성해주는 어노테이션
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1(){
        return memberService.findMembers();
    }

    //엔티티를 DTO로 변환하기
    //엔티티를 외부로 노출시키지 않는것이 핵심이다
    @GetMapping("/api/v2/members")
    public Result memberV2(){
        //엔티티를 dto로 변환하는것
        //엔티티를 변경해도 api스펙이 바뀌지 않음
        //Stream은 컬렉션에 저장되어 있는 원소들을 하나씩 순회하며 처리하는 것이다. 병렬처리를 할 수 있다는 장점이 있다.
        //array를 사용하면 배열로 시작하는것이 아니라서 count등 더 다양한 데이터형태를 출력할 수 있다.
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                //이후로 name을 다른명칭으로 바꿔도 데이터가 바뀌지 않는다.
                //필요한것만 노출할 수 있도록하여 엔티티를 유연하게 보여준다.
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }
    @Data
    @AllArgsConstructor
    static class Result<T>{

        //<T>는 데이터 형식에 의존하지 않고 하나의 값이 여러 다른 데이터 값을 가질 수 있는것.
        private int count;
        private T data;
    }
    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }
    @PostMapping("/api/v1/members")
    //RequestBody는 json에서 온 데이터를 매핑해서 멤버에 넣는다
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){
        Long id= memberService.join(member);
        return new CreateMemberResponse(id);
    }
    //아래 방법으로 해야 안정적으로 사용가능. 엔티티를 바꿔도 잘 작동한다. 그리고 위의 방법은 id인지 name인지 어떤 데이터가 넘어오는지 직관적으로 알기 어렵다.
    //엔티티를 파라미터로 쓰는것은 지양
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
    //DTO를 쓰는 이유
    //필요한 데이터만을 정의하여 가져다써서 불필요한 정보를 보내지 않아도 된. 또한 domain이 수정되지 않아야 하기 때문이다.
    //NotNull 과 같은 Data의 Validation이 DTO역할을 한다.
    @PutMapping("/api/v2/members/{id}")
    //요청,응답 dto를 별도로 만든다. 수정과 등록은 다르기 때문에
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){

            memberService.update(id,request.getName());
            Member findMember = memberService.findOne(id);
            return new UpdateMemberResponse(findMember.getId(),findMember.getName());
    }

    @Data
    static class UpdateMemberRequest{
        private String name;
    }
    @Data
    @AllArgsConstructor
    //모든 필드값을 파라미터로 받는 생성자를 만든다.
    //dto를 통해 필요한 값만 가져오기
    static class UpdateMemberResponse{
        private Long id;
        private String name;
    }
    @Data
    static class CreateMemberRequest{
        @NotEmpty
        private String name;
    }
    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id){
            this.id=id;
        }
    }
}
