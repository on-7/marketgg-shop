package com.nhnacademy.marketgg.server.repository.label;

import com.nhnacademy.marketgg.server.dto.response.label.LabelRetrieveResponse;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface LabelRepositoryCustom {

    /**
     * 전체 라벨 목록을 반환하는 메소드입니다.
     *
     * @return 라벨 전체 목록을 List 로 반환합니다.
     * @author 박세완
     * @since 1.0.0
     */
    List<LabelRetrieveResponse> findAllLabels();

}
