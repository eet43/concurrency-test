package com.example.concurrency.aspect;

import com.example.concurrency.annotation.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {
    
    private final RedissonClient redissonClient;
    
    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        String lockKey = distributedLock.key();
        long waitTime = distributedLock.waitTime();
        long leaseTime = distributedLock.leaseTime();
        
        log.info("분산락 획득 시도 중... key: {}, 스레드: {}", lockKey, Thread.currentThread().getName());
        
        RLock lock = redissonClient.getLock(lockKey);
        
        boolean isLocked = false;
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("락 대기 중... key: {}, waitTime: {}ms", lockKey, waitTime);
            isLocked = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            
            long waitedTime = System.currentTimeMillis() - startTime;
            
            if (!isLocked) {
                log.warn("락 획득 실패! key: {}, 대기시간: {}ms", lockKey, waitedTime);
                throw new RuntimeException("분산락 획득에 실패했습니다");
            }
            
            log.info("락 획득 성공! key: {}, 대기시간: {}ms, 스레드: {}",
                    lockKey, waitedTime, Thread.currentThread().getName());
            
            Object result = joinPoint.proceed();
            
            log.info("비즈니스 로직 완료. key: {}", lockKey);
            return result;
            
        } catch (InterruptedException e) {
            log.error("락 획득 중 스레드가 중단되었습니다. key: {}", lockKey, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("스레드가 중단되었습니다", e);
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                long totalTime = System.currentTimeMillis() - startTime;
                lock.unlock();
                log.info("락 해제 완료! key: {}, 총 소요시간: {}ms, 스레드: {}",
                        lockKey, totalTime, Thread.currentThread().getName());
            }
        }
    }
}