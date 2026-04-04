package com.dmz.was.service;

import com.dmz.was.model.Member;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MemberService {

    private final Map<Long, Member> memberDB = new HashMap<>();

    public MemberService() {
        // 초기 샘플 데이터
        initializeSampleData();
    }

    private void initializeSampleData() {
        memberDB.put(1L, new Member(1L, "김철수", "kim@company.com", "010-1234-5678", "개발팀", "2023-01-15"));
        memberDB.put(2L, new Member(2L, "이영희", "lee@company.com", "010-2345-6789", "마케팅팀", "2023-03-20"));
        memberDB.put(3L, new Member(3L, "박민준", "park@company.com", "010-3456-7890", "영업팀", "2023-06-10"));
        memberDB.put(4L, new Member(4L, "정수진", "jung@company.com", "010-4567-8901", "인사팀", "2023-09-05"));
    }

    /**
     * 회원 ID로 회원 정보 조회
     */
    public Optional<Member> getMemberById(Long memberId) {
        return Optional.ofNullable(memberDB.get(memberId));
    }

    /**
     * 모든 회원 정보 조회
     */
    public Map<Long, Member> getAllMembers() {
        return new HashMap<>(memberDB);
    }

    /**
     * 회원 정보 추가
     */
    public Member addMember(Member member) {
        long nextId = memberDB.keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L) + 1;
        member.setMemberId(nextId);
        memberDB.put(nextId, member);
        return member;
    }

    /**
     * 회원 정보 업데이트
     */
    public Optional<Member> updateMember(Long memberId, Member memberUpdate) {
        if (memberDB.containsKey(memberId)) {
            Member member = memberDB.get(memberId);
            member.setName(memberUpdate.getName());
            member.setEmail(memberUpdate.getEmail());
            member.setPhone(memberUpdate.getPhone());
            member.setDepartment(memberUpdate.getDepartment());
            return Optional.of(member);
        }
        return Optional.empty();
    }

    /**
     * 회원 정보 삭제
     */
    public boolean deleteMember(Long memberId) {
        return memberDB.remove(memberId) != null;
    }

    /**
     * 회원 존재 여부 확인
     */
    public boolean memberExists(Long memberId) {
        return memberDB.containsKey(memberId);
    }
}
