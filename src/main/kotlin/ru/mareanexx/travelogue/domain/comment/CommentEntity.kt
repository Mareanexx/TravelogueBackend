package ru.mareanexx.travelogue.domain.comment

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "comment")
data class CommentEntity(
    @Id
    val id: Int? = null,
    val text: String,
    val sendDate: LocalDateTime,
    val senderProfileId: Int, // FK на user's profile
    val mapPointId: Int // FK на map_point
)