package com.nhnacademy.marketgg.server.exception.transaction;

/**
 * 트랜잭션을 찾을 수 없을 때 예외처리입니다.
 *
 * @version 1.0.0
 */
public class TransactionNotFoundException extends IllegalArgumentException {

    private static final String ERROR = "트랜잭션을 찾을 수 없습니다.";

    public TransactionNotFoundException() {
        super(ERROR);
    }

}