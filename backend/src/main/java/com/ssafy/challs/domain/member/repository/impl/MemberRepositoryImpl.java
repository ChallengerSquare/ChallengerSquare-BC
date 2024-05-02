package com.ssafy.challs.domain.member.repository.impl;

import static com.ssafy.challs.domain.member.entity.QMember.*;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.challs.domain.member.dto.request.MemberCreateRequestDto;
import com.ssafy.challs.domain.member.dto.request.MemberUpdateRequestDto;
import com.ssafy.challs.domain.member.repository.MemberRepositoryCustom;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public void createMember(MemberCreateRequestDto memberCreateRequestDto, Long memberId) {
		queryFactory.update(member)
			.set(member.memberName, memberCreateRequestDto.memberName())
			.set(member.memberAddress,
				memberCreateRequestDto.memberAddress())
			.set(member.memberPhone, memberCreateRequestDto.memberPhone())
			.set(member.memberBirth, memberCreateRequestDto.memberBirth())
			.set(member.isAgree, memberCreateRequestDto.isAgree())
			.where(member.id.eq(memberId))
			.execute();
	}

	@Override
	public void updateMember(MemberUpdateRequestDto memberUpdateRequestDto, Long memberId) {
		queryFactory.update(member)
			.set(member.memberName, memberUpdateRequestDto.memberName())
			.set(member.memberAddress,
				memberUpdateRequestDto.memberAddress())
			.set(member.memberPhone, memberUpdateRequestDto.memberPhone())
			.where(member.id.eq(memberId))
			.execute();
	}

	@Override
	public void deleteMember(Long memberId) {
		queryFactory.update(member)
			.set(member.isWithdraw, true)
			.where(member.id.eq(memberId))
			.execute();
	}

}
