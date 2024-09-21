package com.example.stock.facade;

import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockStockFacade {

    private final RedisLockRepository redisLockRepository;
    private final StockService stockService;

    public LettuceLockStockFacade(RedisLockRepository redisLockRepository, StockService stockService) {
        this.redisLockRepository = redisLockRepository;
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while (!redisLockRepository.lock(id)) {     // lock 획득 실패하는 경우 100ms의 텀을 주고 재시도
            Thread.sleep(100 );
        }

        try {   // lock 획득 성공 시 재고 감소
            stockService.decrease(id, quantity);
        } finally { // 모든 로직이 종료되면 lock 해제
            redisLockRepository.unlock(id);
        }
    }
}
