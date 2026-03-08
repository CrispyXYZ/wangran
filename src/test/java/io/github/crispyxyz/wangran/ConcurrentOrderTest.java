package io.github.crispyxyz.wangran;

import io.github.crispyxyz.wangran.exception.BusinessException;
import io.github.crispyxyz.wangran.model.Event;
import io.github.crispyxyz.wangran.model.Merchant;
import io.github.crispyxyz.wangran.model.User;
import io.github.crispyxyz.wangran.model.UserEvent;
import io.github.crispyxyz.wangran.service.EventService;
import io.github.crispyxyz.wangran.service.MerchantService;
import io.github.crispyxyz.wangran.service.OrderService;
import io.github.crispyxyz.wangran.service.UserEventService;
import io.github.crispyxyz.wangran.service.UserService;
import io.github.crispyxyz.wangran.util.GenerationUtil;
import io.github.crispyxyz.wangran.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 声明：本集成测试类由AI生成，人工微调，用来检测业务逻辑在高并发下的问题
 */
 @Slf4j
@SpringBootTest
@ActiveProfiles("test") // 使用测试配置文件（如 H2 数据库）
public class ConcurrentOrderTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private UserEventService userEventService;

    private Integer testEventId;
    private Integer testMerchantId;
    private List<Integer> testUserIds;

    @BeforeEach
    void setUp() {
        // 创建测试商户（已审核）
        Merchant merchant = new Merchant();
        merchant.setPhoneNumber("1234567890");
        merchant.setPasswordSha256(SecurityUtil.computeSha256("password"));
        merchant.setApprovalStatus(Merchant.STATUS_APPROVED);
        merchant.setMerchantCode(GenerationUtil.generateUniqueSequence("mid_"));
        merchant.setUsername(GenerationUtil.generateUniqueUsername("merchant_"));
        merchantService.save(merchant);
        testMerchantId = merchant.getId();

        // 创建测试票务（已上架，库存10）
        Event event = new Event();
        event.setEventName("Test Event");
        event.setEventType("演出");
        event.setEventTime(Instant.now().plusSeconds(3600));
        event.setCity("威海");
        event.setPrice(BigDecimal.valueOf(100));
        event.setStock(10);
        event.setOnShelf(1);
        event.setSaleStartTime(Instant.now().minusSeconds(3600));
        event.setSaleEndTime(Instant.now().plusSeconds(3600));
        event.setMerchantId(testMerchantId);
        event.setEventCode(GenerationUtil.generateUniqueSequence("E"));
        eventService.save(event);
        testEventId = event.getId();

        // 创建20个测试用户
        testUserIds = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            User user = new User();
            user.setPhoneNumber(String.valueOf(i));
            user.setPasswordSha256(SecurityUtil.computeSha256("password"));
            user.setUsername(GenerationUtil.generateUniqueUsername("user_"));
            userService.save(user);
            testUserIds.add(user.getId());
        }
    }

    @AfterEach
    void tearDown() {
        // 清理订单（关联事件）
        if (testEventId != null) {
            userEventService.lambdaUpdate().eq(UserEvent::getEventId, testEventId).remove();
        }
        // 清理事件
        if (testEventId != null) {
            eventService.removeById(testEventId);
        }
        // 清理商户
        if (testMerchantId != null) {
            merchantService.removeById(testMerchantId);
        }
        // 清理用户
        if (testUserIds != null) {
            testUserIds.forEach(id -> userService.removeById(id));
        }
    }

    /**
     * 同一用户并发购买同一票务，预期仅成功一次（一人一票约束）
     */
    @Test
    void testConcurrentPurchase_SameUser() throws InterruptedException {
        int threadCount = 10;
        Integer userId = testUserIds.get(0);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    orderService.createOrder(userId, testEventId);
                    successCount.incrementAndGet();
                } catch (BusinessException e) {
                    // 预期重复购买异常
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证成功数 = 1，失败数 = threadCount-1
        assertEquals(1, successCount.get());
        assertEquals(threadCount - 1, failureCount.get());

        // 验证库存：初始10，成功1次 → 剩余9
        Event updatedEvent = eventService.getById(testEventId);
        assertEquals(9, updatedEvent.getStock());

        // 验证订单数 = 1
        long orderCount = userEventService.lambdaQuery()
                                          .eq(UserEvent::getEventId, testEventId)
                                          .count();
        assertEquals(1, orderCount);
    }

    /**
     * 不同用户并发购买同一票务，库存有限，预期成功数 = 初始库存
     */
    @Test
    void testConcurrentPurchase_DifferentUsers() throws InterruptedException {
        int threadCount = 10; // 用户数10
        int initialStock = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<Integer> userIds = testUserIds.subList(0, threadCount);

        for (int i = 0; i < threadCount; i++) {
            int index = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    orderService.createOrder(userIds.get(index), testEventId);
                    successCount.incrementAndGet();
                } catch (BusinessException e) {
                    // 预期库存不足异常
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // 成功数 = 库存，失败数 = threadCount - 库存
        assertEquals(initialStock, successCount.get());
        assertEquals(threadCount - initialStock, failureCount.get());

        // 验证库存为0
        Event updatedEvent = eventService.getById(testEventId);
        assertEquals(0, updatedEvent.getStock());

        // 验证订单数 = 成功数
        long orderCount = userEventService.lambdaQuery()
                                          .eq(UserEvent::getEventId, testEventId)
                                          .count();
        assertEquals(successCount.get(), orderCount);
    }

    /**
     * 同一订单并发退票，预期仅成功一次
     */
    @Test
    void testConcurrentRefund() throws InterruptedException {
        // 先让一个用户购买一张票
        Integer userId = testUserIds.get(0);
        UserEvent order = orderService.createOrder(userId, testEventId);
        Integer orderId = order.getId();

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    orderService.refundOrder(userId, orderId);
                    successCount.incrementAndGet();
                } catch (BusinessException e) {
                    // 预期已退票异常
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证成功数 = 1，失败数 = threadCount-1
        assertEquals(1, successCount.get());
        assertEquals(threadCount - 1, failureCount.get());

        // 验证订单已退票
        UserEvent updatedOrder = userEventService.getById(orderId);
        assertEquals(1, updatedOrder.getRefunded());

        // 验证库存恢复为初始10（之前购买扣1，退票加1）
        Event updatedEvent = eventService.getById(testEventId);
        assertEquals(10, updatedEvent.getStock());
    }

    @Test
    void testConcurrentRefundAndPurchase() throws InterruptedException {
        // 准备：用户A已购票
        Integer userA = testUserIds.get(0);
        Integer userB = testUserIds.get(1);
        UserEvent order = orderService.createOrder(userA, testEventId);
        // 库存此时为 9（初始10减1）

        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);
        AtomicInteger refundSuccess = new AtomicInteger(0);
        AtomicInteger purchaseSuccess = new AtomicInteger(0);

        executor.submit(() -> {
            try {
                startLatch.await();
                orderService.refundOrder(userA, order.getId());
                refundSuccess.incrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                endLatch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                startLatch.await();
                orderService.createOrder(userB, testEventId);
                purchaseSuccess.incrementAndGet();
            } catch (BusinessException e) {
                // 预期可能因库存不足失败
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                endLatch.countDown();
            }
        });

        startLatch.countDown();
        endLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // 最终库存 = 10 (退票+1，购票-1)
        Event event = eventService.getById(testEventId);
        assertEquals(10, event.getStock());

        // 订单数：原有订单已退票，新订单可能成功（若T2在T1之后执行）
        long orders = userEventService.lambdaQuery()
                                      .eq(UserEvent::getEventId, testEventId)
                                      .eq(UserEvent::getRefunded, 0)
                                      .count();
        // 可能为0或1，取决于执行顺序，但不会超卖
        assertTrue(orders == 0 || orders == 1);
    }
}