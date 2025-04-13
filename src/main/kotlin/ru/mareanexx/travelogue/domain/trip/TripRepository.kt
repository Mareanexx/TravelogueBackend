package ru.mareanexx.travelogue.domain.trip

import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import ru.mareanexx.travelogue.domain.trip.dto.ActiveFollowingTrip
import ru.mareanexx.travelogue.domain.trip.dto.AuthorTrip
import ru.mareanexx.travelogue.domain.trip.dto.TripByTag
import ru.mareanexx.travelogue.domain.trip.dto.UserTrip

@Repository
interface TripRepository : CrudRepository<TripEntity, Int> {
    @Query("""
        SELECT id, name, description, start_date, end_date, steps_number,
        days_number, type, status, cover_photo
        FROM "trip"
        WHERE profile_id = :authorId
    """)
    fun findAllByAuthorProfileId(@Param("authorId") authorId: Int): List<AuthorTrip>

    @Query("""
        SELECT id, name, description, start_date, end_date, steps_number,
        days_number, status, cover_photo
        FROM "trip"
        WHERE profile_id = :profileId AND type = 'Public'
    """)
    fun findByProfileIdFilterByStatus(@Param("profileId") profileId: Int): List<UserTrip>

    @Query("""
        SELECT tr.id AS trip_id, tr.name, tr.description, tr.start_date, tr.end_date,
               tr.steps_number, tr.days_number, tr.status, tr.cover_photo,
               pr.id AS profile_id, pr.avatar, pr.username
        FROM trip tr
        JOIN profile pr ON tr.profile_id = pr.id
        JOIN tags ON tags.trip_id = tr.id
        WHERE tags.name = :tagName AND pr.id != :finderId
    """)
    fun findAllByTag(
        @Param("tagName") tagName: String,
        @Param("finderId") finderId: Int
    ): List<TripByTag>

    @Query("""
        SELECT tr.id AS trip_id, tr.name, tr.description, tr.start_date, tr.end_date,
               tr.steps_number, tr.days_number, tr.status, tr.cover_photo,
               pr.id AS profile_id, pr.avatar, pr.username
        FROM trip tr
        JOIN profile pr ON pr.id = tr.profile_id
        JOIN follows f ON f.follower_id = :followerId AND f.following_id = pr.id
        WHERE tr.status = 'Current' AND tr.type = 'Public'
    """)
    fun findAllByStatusAndFollowerId(@Param("followerId") followerId: Int): List<ActiveFollowingTrip>
}