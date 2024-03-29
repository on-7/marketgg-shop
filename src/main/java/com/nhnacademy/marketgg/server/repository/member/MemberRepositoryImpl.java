package com.nhnacademy.marketgg.server.repository.member;

import com.nhnacademy.marketgg.server.dto.info.MemberInfo;
import com.nhnacademy.marketgg.server.entity.Member;
import com.nhnacademy.marketgg.server.entity.QMember;
import com.nhnacademy.marketgg.server.entity.QOrder;
import com.querydsl.core.types.Projections;
import java.util.Optional;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class MemberRepositoryImpl extends QuerydslRepositorySupport implements MemberRepositoryCustom {

    public MemberRepositoryImpl() {
        super(Member.class);
    }

    @Override
    public Optional<MemberInfo> findMemberInfoByUuid(final String uuid) {
        QMember member = QMember.member;

        MemberInfo memberInfo = from(member)
                .innerJoin(member.cart)
                .where(member.uuid.eq(uuid))
                .select(Projections.constructor(MemberInfo.class,
                                                member.id, member.cart, member.memberGrade.grade, member.gender,
                                                member.birthDate))
                .fetchOne();

        return Optional.ofNullable(memberInfo);
    }

    @Override
    public String findUuidByOrderId(final Long orderId) {
        QMember member = QMember.member;
        QOrder order = QOrder.order;

        return from(member)
                .innerJoin(order).on(order.member.id.eq(member.id))
                .where(order.id.eq(orderId))
                .select(member.uuid)
                .fetchOne();
    }

    @Override
    public Optional<String> findUuidByMemberId(final Long memberId) {
        QMember member = QMember.member;

        return Optional.ofNullable(from(member)
                                           .where(member.id.eq(memberId))
                                           .select(member.uuid)
                                           .fetchOne());
    }

}
