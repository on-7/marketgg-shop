package com.nhnacademy.marketgg.server.elastic.request.searchutil;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 검색어로 여러 필드를 조회할 떄 사용할 수 있습니다.
 *
 * @author 박세완
 * @version 1.0.0
 */
@AllArgsConstructor
@Getter
public class MultiMatch {

    /**
     * 검색어입니다.
     *
     * @since 1.0.0
     */
    private String query;

    /**
     * 검색 시 오타 교정 장치입니다.
     *
     * @since 1.0.0
     */
    private String fuzziness;

    /**
     * 검색을 할 필드들입니다.
     *
     * @since 1.0.0
     */
    private List<String> fields;


}
