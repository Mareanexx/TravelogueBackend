package ru.mareanexx.travelogue.api.rest

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.mareanexx.travelogue.domain.notifications.NotificationsService
import ru.mareanexx.travelogue.domain.notifications.dto.trip.NewTripNotification
import ru.mareanexx.travelogue.domain.tags.TagService
import ru.mareanexx.travelogue.domain.trip.TripService
import ru.mareanexx.travelogue.domain.trip.dto.*

@RestController
@RequestMapping("/api/v1/trips")
class TripController(
    private val tripService: TripService,
    private val tagService: TagService,
    private val notificationsService: NotificationsService
) {
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('USER')")
    fun uploadNewTrip(
        @RequestPart("data") data: NewTripRequest,
        @RequestPart("cover") coverPhoto: MultipartFile
    ): ResponseEntity<TripResponse?> {
        return try {
            val newTrip = tripService.createNewTrip(data, coverPhoto)
            data.tagList?.let {
                tagService.addNew(it, newTrip.id)
                newTrip.tagList = it
            }

            notificationsService.notifyAllFollowersAboutNewTrip(
                NewTripNotification(
                    creatorId = data.profileId,
                    tripId = newTrip.id
                )
            )

            ResponseEntity.ok(newTrip)
        } catch(e: Exception) {
            println(e.message)
            ResponseEntity.badRequest().body(null)
        }
    }

    @DeleteMapping("/{tripId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
    fun deleteTrip(@PathVariable tripId: Int): ResponseEntity<String> {
        return try {
            tripService.deleteTrip(tripId)
            ResponseEntity.ok("Successfully deleted trip")
        } catch (e: Exception) {
            println(e.message)
            ResponseEntity.badRequest().body("Can't delete this trip")
        }
    }

    @PatchMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('USER')")
    fun editTrip(
        @RequestPart("data") data: EditTripRequest,
        @RequestPart("cover") coverPhoto: MultipartFile?
    ): ResponseEntity<TripResponse?> {
        return try {
            val editedTrip = tripService.editTrip(data, coverPhoto)
            data.tagList?.let {
                tagService.editTags(it, editedTrip.id)
                editedTrip.tagList = it
            }
            ResponseEntity.ok(editedTrip)
        } catch(e: Exception) {
            println(e.message)
            ResponseEntity.badRequest().body(null)
        }
    }

    @GetMapping("/tagged")
    @PreAuthorize("hasRole('USER')")
    fun getAllByTag(@RequestBody tripByTag: TripByTagRequest): ResponseEntity<List<TripByTag>> {
        return try {
            val trips = tripService.getAllByTag(tripByTag)
            ResponseEntity.ok(trips)
        } catch (e: Exception) {
            println(e.message)
            ResponseEntity.badRequest().body(emptyList())
        }
    }

    @GetMapping("/activity")
    @PreAuthorize("hasRole('USER')")
    fun getAllFollowingsCurrent(@RequestParam authorId: Int): ResponseEntity<List<ActiveFollowingTrip>> {
        return try {
            val currentTrips = tripService.getAllFollowingsCurrentTrips(authorId)
            ResponseEntity.ok(currentTrips)
        } catch (e: Exception) {
            println(e.message)
            ResponseEntity.badRequest().body(emptyList())
        }
    }
}