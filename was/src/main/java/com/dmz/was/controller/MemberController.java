package com.dmz.was.controller;

import com.dmz.was.model.Member;
import com.dmz.was.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * 헬스 체크 엔드포인트
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "DMZ WAS - Private Member Service");
        return ResponseEntity.ok(response);
    }

    /**
     * 모든 회원 정보 조회
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMembers() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", memberService.getAllMembers().values());
        response.put("message", "회원 조회 성공");
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 회원 정보 조회 (ID 기반)
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<Map<String, Object>> getMemberById(@PathVariable Long memberId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Member> member = memberService.getMemberById(memberId);
        if (member.isPresent()) {
            response.put("success", true);
            response.put("data", member.get());
            response.put("message", "회원 조회 성공");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "해당 회원을 찾을 수 없습니다. (ID: " + memberId + ")");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 새 회원 추가
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addMember(@RequestBody Member member) {
        Map<String, Object> response = new HashMap<>();

        Member addedMember = memberService.addMember(member);
        response.put("success", true);
        response.put("data", addedMember);
        response.put("message", "회원 추가 성공");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 회원 정보 수정
     */
    @PutMapping("/{memberId}")
    public ResponseEntity<Map<String, Object>> updateMember(
            @PathVariable Long memberId,
            @RequestBody Member memberUpdate) {
        Map<String, Object> response = new HashMap<>();

        Optional<Member> updated = memberService.updateMember(memberId, memberUpdate);
        if (updated.isPresent()) {
            response.put("success", true);
            response.put("data", updated.get());
            response.put("message", "회원 정보 수정 성공");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "해당 회원을 찾을 수 없습니다. (ID: " + memberId + ")");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * 회원 정보 삭제
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Map<String, Object>> deleteMember(@PathVariable Long memberId) {
        Map<String, Object> response = new HashMap<>();

        boolean deleted = memberService.deleteMember(memberId);
        if (deleted) {
            response.put("success", true);
            response.put("message", "회원 삭제 성공 (ID: " + memberId + ")");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "해당 회원을 찾을 수 없습니다. (ID: " + memberId + ")");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
