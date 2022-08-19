package com.nhnacademy.marketgg.server.entity.event;

import com.nhnacademy.marketgg.server.dto.request.point.PointHistoryRequest;
import com.nhnacademy.marketgg.server.entity.Member;
import com.nhnacademy.marketgg.server.entity.PointHistory;
import com.nhnacademy.marketgg.server.repository.pointhistory.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

import static com.nhnacademy.marketgg.server.constant.PointContent.ORDER;
import static com.nhnacademy.marketgg.server.constant.PointContent.ORDER_CANCEL;

@Component
@RequiredArgsConstructor
public class OrderPointCanceledEventHandler {

    private final PointHistoryRepository pointRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void restorePoint(OrderPointCanceledEvent event) {
        Member member = event.getOrder().getMember();
        List<PointHistory> pointHistoryList = pointRepository.findByOrderId(event.getOrder().getId());
        Integer point;

        for (PointHistory pointHistory : pointHistoryList) {
            point = pointHistory.getPoint();
            Integer totalPoint = pointHistory.getContent().equals(ORDER.getContent())
                    ? pointHistory.getTotalPoint() - point
                    : pointHistory.getTotalPoint() + point;

            PointHistoryRequest pointRequest = new PointHistoryRequest(point, ORDER_CANCEL.getContent());

            PointHistory saveHistory = new PointHistory(member, event.getOrder(), totalPoint, pointRequest);
            pointRepository.save(saveHistory);
        }
    }

}