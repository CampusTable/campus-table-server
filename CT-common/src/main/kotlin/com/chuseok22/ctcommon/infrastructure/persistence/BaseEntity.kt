package com.chuseok22.ctcommon.infrastructure.persistence

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

  /**
   * 생성일시 (UTC)
   */
  @field:CreatedDate
  @field:Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
  var createdAt: Instant? = null

  /**
   * 수정일시 (UTC)
   */
  @field:LastModifiedDate
  @field:Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
  var updatedAt: Instant? = null

  /**
   * 삭제 여부
   */
  @field:Column(name = "deleted", nullable = false)
  var deleted: Boolean = false

  /**
   * 삭제일시 (UTC)
   */
  @field:Column(name = "deleted_at", columnDefinition = "TIMESTAMPTZ")
  var deletedAt: Instant? = null

  /**
   * Soft Delete 실행
   */
  fun delete(now: Instant) {
    this.deleted = true
    deletedAt = now
  }
}