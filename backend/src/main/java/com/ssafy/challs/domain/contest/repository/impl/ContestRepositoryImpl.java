package com.ssafy.challs.domain.contest.repository.impl;

import static com.ssafy.challs.domain.contest.entity.QContest.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.challs.domain.contest.dto.request.ContestSearchRequestDto;
import com.ssafy.challs.domain.contest.entity.Contest;
import com.ssafy.challs.domain.contest.entity.QContest;
import com.ssafy.challs.domain.contest.entity.QContestParticipants;
import com.ssafy.challs.domain.contest.repository.ContestRepositoryCustom;
import com.ssafy.challs.domain.team.dto.response.TeamContestResponseDto;
import com.ssafy.challs.domain.team.entity.QTeam;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ContestRepositoryImpl implements ContestRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Contest> searchContest(ContestSearchRequestDto searchRequestDto, Pageable pageable, Integer orderBy) {
		QContest contest = QContest.contest;
		QTeam team = QTeam.team;

		BooleanBuilder whereBuilder = new BooleanBuilder();

		// 검색 키워드 조건
		if (searchRequestDto.keyword() != null && !searchRequestDto.keyword().isEmpty()) {
			BooleanExpression keywordInTitle = contest.contestTitle.containsIgnoreCase(searchRequestDto.keyword());
			BooleanExpression keywordInTeamName = team.teamName.containsIgnoreCase(searchRequestDto.keyword());
			BooleanExpression keywordCondition = keywordInTitle.or(keywordInTeamName);
			whereBuilder.and(keywordCondition);
		}

		// 카테고리 조건
		if (searchRequestDto.category() != null) {
			BooleanExpression categoryCondition = contest.contestCategory.eq(searchRequestDto.category());
			whereBuilder.and(categoryCondition);
		}

		// 종료된 대회 조건
		if (Boolean.TRUE.equals(searchRequestDto.isEnd())) {
			BooleanExpression isEndCondition = contest.contestState.eq('E');
			whereBuilder.and(isEndCondition);
		}

		// 결과 쿼리 실행
		JPAQuery<Contest> query = queryFactory.selectFrom(contest)
			.innerJoin(contest.team, team)
			.where(whereBuilder);

		if (orderBy == 3) {
			query.orderBy(contest.createDate.desc());
		} else if (orderBy == 4) {
			query.orderBy(contest.contestTitle.asc());
		}

		List<Contest> results = query.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 효율적인 전체 개수 계산을 위해 JPAQuery<Long> 객체 생성
		JPAQuery<Long> countQuery = createCountQuery(contest, team, whereBuilder);

		// Page 객체 생성 후 반환
		return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
	}

	@Override
	public Page<Contest> searchContestOrderByRegistrationEnd(Pageable pageable) {
		QContest contest = QContest.contest;

		LocalDate today = LocalDate.now();

		// QueryDSL 쿼리 작성
		List<Contest> results = queryFactory
			.selectFrom(contest)
			.where(
				contest.contestState.eq('J'),
				contest.contestRegistrationEnd.goe(today)
			)
			.orderBy(contest.contestRegistrationEnd.asc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 카운트 쿼리 생성
		JPAQuery<Long> countQuery = createCountQuery(contest, today);

		return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
	}

	@Override
	public Page<Contest> searchContestOrderByRegistrationNum(Pageable pageable) {
		QContest contest = QContest.contest;
		QContestParticipants contestParticipants = QContestParticipants.contestParticipants;

		// 대회를 그룹화 & 참가신청 인원 수를 기준으로 정렬
		List<Contest> results = queryFactory
			.selectFrom(contest)
			.innerJoin(contestParticipants.contest, contest)
			.where(contest.contestState.eq('J'))
			.groupBy(contest)
			.orderBy(contestParticipants.count().desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 카운트 쿼리 생성
		JPAQuery<Long> countQuery = createCountQuery(contest);

		// PageableExecutionUtils를 사용하여 Page 객체 반환
		return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
	}

	private JPAQuery<Long> createCountQuery(QContest contest) {
		// JPAQuery<Long> 객체를 생성하여 반환
		return queryFactory
			.select(contest.count())
			.from(contest)
			.where(contest.contestState.eq('J'));
	}

	private JPAQuery<Long> createCountQuery(QContest contest, QTeam team, BooleanBuilder whereBuilder) {
		// JPAQuery<Long> 객체를 생성하여 반환
		return queryFactory
			.select(contest.count())
			.from(contest)
			.leftJoin(contest.team, team)
			.where(whereBuilder);
	}

	@Override
	public Page<TeamContestResponseDto> searchTeamContestList(Long teamId, Pageable pageable) {
		List<TeamContestResponseDto> response = queryFactory.select(
				Projections.constructor(
					TeamContestResponseDto.class, contest.id, contest.contestTitle, contest.contestImage))
			.from(contest)
			.where(contest.team.id.eq(teamId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> limit = queryFactory.select(contest.count())
			.from(contest)
			.where(contest.team.id.eq(teamId));
		return PageableExecutionUtils.getPage(response, pageable, limit::fetchOne);
	}

	private JPAQuery<Long> createCountQuery(QContest contest, LocalDate today) {
		// JPAQuery<Long> 객체를 생성하여 반환
		return queryFactory
			.select(contest.count())
			.from(contest)
			.where(
				contest.contestState.eq('J'),
				contest.contestRegistrationEnd.goe(today)
			);
	}

	@Override
	public Long findTeamIdByContestId(Long contestId) {
		QContest contest = QContest.contest;

		return queryFactory
			.select(contest.team.id)
			.from(contest)
			.where(contest.id.eq(contestId)).fetchOne();
	}

	@Override
	public void updateContestState(Long contestId, Character contestState) {
		QContest qContest = QContest.contest;

		queryFactory
			.update(qContest)
			.set(qContest.contestState, contestState)
			.where(qContest.id.eq(contestId))
			.execute();
	}
}
