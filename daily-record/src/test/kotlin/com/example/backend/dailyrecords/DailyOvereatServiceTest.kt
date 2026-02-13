package com.example.backend.dailyrecords

import com.example.backend.common.constant.ErrorCode
import com.example.backend.common.exception.CustomException
import com.example.backend.dailyrecords.dto.dummyDailyOvereatRequest
import com.example.backend.dailyrecords.entity.dummyDailyOvereat
import com.example.backend.user.UserRepository
import com.example.backend.user.entity.dummyUser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate

class DailyOvereatServiceTest :
    BehaviorSpec({
        val repository = mockk<DailyOvereatRepository>()
        val userRepository = mockk<UserRepository>()
        val service = DailyOvereatService(repository, userRepository)

        val user = dummyUser()
        val date = LocalDate.of(2026, 2, 1)

        Given("목록 조회 시") {
            When("정상 요청") {
                val from = LocalDate.of(2026, 2, 1)
                val to = LocalDate.of(2026, 2, 28)
                val overeat = dummyDailyOvereat(user = user)

                every { userRepository.findByUsername("testuser") } returns user
                every { repository.findAllByUserAndDateBetween(user, from, to) } returns listOf(overeat)

                val result = service.list("testuser", from, to)

                Then("DailyOvereatResponse 리스트 반환") {
                    result.size shouldBe 1
                    result[0].overeatLevel shouldBe OvereatLevel.MILD
                }
            }
        }

        Given("upsert 시") {
            When("기존 기록 없음, MILD") {
                val request = dummyDailyOvereatRequest()

                every { userRepository.findByUsername("testuser") } returns user
                every { repository.findByDateAndUser(date, user) } returns null
                every { repository.save(any()) } answers { firstArg() }

                service.upsert("testuser", request)

                Then("새 기록 저장") {
                    verify { repository.save(any()) }
                }
            }

            When("기존 기록 없음, NONE") {
                val request = dummyDailyOvereatRequest(overeatLevel = OvereatLevel.NONE)

                every { userRepository.findByUsername("testuser") } returns user
                every { repository.findByDateAndUser(date, user) } returns null

                service.upsert("testuser", request)

                Then("아무 동작 안 함") {
                    verify(exactly = 0) { repository.save(any()) }
                    verify(exactly = 0) { repository.delete(any()) }
                }
            }

            When("기존 기록 있음, 레벨 변경") {
                val existing = dummyDailyOvereat(user = user, overeatLevel = OvereatLevel.MILD)
                val request = dummyDailyOvereatRequest(overeatLevel = OvereatLevel.SEVERE)

                every { userRepository.findByUsername("testuser") } returns user
                every { repository.findByDateAndUser(date, user) } returns existing

                service.upsert("testuser", request)

                Then("레벨 업데이트") {
                    existing.overeatLevel shouldBe OvereatLevel.SEVERE
                }
            }

            When("기존 기록 있음, NONE으로 변경") {
                val existing = dummyDailyOvereat(user = user)
                val request = dummyDailyOvereatRequest(overeatLevel = OvereatLevel.NONE)

                every { userRepository.findByUsername("testuser") } returns user
                every { repository.findByDateAndUser(date, user) } returns existing
                justRun { repository.delete(existing) }

                service.upsert("testuser", request)

                Then("기존 기록 삭제") {
                    verify { repository.delete(existing) }
                }
            }
        }

        Given("유저 없음") {
            When("list 호출") {
                every { userRepository.findByUsername("unknown") } returns null

                Then("CustomException(RESOURCE_NOT_FOUND) 발생") {
                    val ex =
                        shouldThrow<CustomException> {
                            service.list("unknown", LocalDate.now(), LocalDate.now())
                        }
                    ex.errorCode shouldBe ErrorCode.RESOURCE_NOT_FOUND
                }
            }
        }
    })
