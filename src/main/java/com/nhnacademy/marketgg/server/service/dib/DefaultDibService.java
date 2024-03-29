package com.nhnacademy.marketgg.server.service.dib;

import com.nhnacademy.marketgg.server.dto.response.dib.DibRetrieveResponse;
import com.nhnacademy.marketgg.server.entity.Dib;
import com.nhnacademy.marketgg.server.entity.Member;
import com.nhnacademy.marketgg.server.entity.Product;
import com.nhnacademy.marketgg.server.exception.dib.DibNotFoundException;
import com.nhnacademy.marketgg.server.exception.member.MemberNotFoundException;
import com.nhnacademy.marketgg.server.exception.product.ProductNotFoundException;
import com.nhnacademy.marketgg.server.repository.dib.DibRepository;
import com.nhnacademy.marketgg.server.repository.member.MemberRepository;
import com.nhnacademy.marketgg.server.repository.product.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultDibService implements DibService {

    private final DibRepository dibRepository;

    private final MemberRepository memberRepository;

    private final ProductRepository productRepository;

    @Transactional
    @Override
    public void createDib(final Long memberId, final Long productId) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(MemberNotFoundException::new);
        Product product = productRepository.findById(productId)
                                           .orElseThrow(ProductNotFoundException::new);

        Dib.Pk pk = new Dib.Pk(memberId, productId);

        Dib dib = new Dib(pk, member, product);

        dibRepository.save(dib);
    }

    @Transactional
    @Override
    public List<DibRetrieveResponse> retrieveDibs(final Long memberId) {
        return dibRepository.findAllDibs(memberId);
    }

    @Transactional
    @Override
    public void deleteDib(final Long memberId, final Long productId) {
        Dib dib = dibRepository.findById(new Dib.Pk(memberId, productId))
                               .orElseThrow(DibNotFoundException::new);

        dibRepository.delete(dib);
    }

}
