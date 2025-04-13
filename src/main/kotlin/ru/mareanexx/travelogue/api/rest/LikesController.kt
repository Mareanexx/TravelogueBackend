package ru.mareanexx.travelogue.api.rest

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import ru.mareanexx.travelogue.domain.likes.LikesService
import ru.mareanexx.travelogue.domain.likes.dto.LikeRequest
import ru.mareanexx.travelogue.domain.likes.type.LikeStatusCode.*

@RestController
@RequestMapping("/api/v1/likes")
class LikesController(
    private val likesService: LikesService
) {
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    fun addNew(@RequestBody likeRequest: LikeRequest): ResponseEntity<Map<String, String>> {
        return try {
            val statusCode = likesService.addNew(likeRequest)
            val response = when (statusCode) {
                SUCCESS -> mapOf("success" to "You successfully likes map_point")
                ERROR -> mapOf("error" to "Something went wrong, can't add new like")
                UNKNOWN -> mapOf("error" to "Unknown error on server")
            }
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to "Can't add new like"))
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    fun deleteExisting(@RequestBody likeRequest: LikeRequest): ResponseEntity<Map<String, String>> {
        return try {
            val statusCode = likesService.deleteExisted(likeRequest)
            val response = when (statusCode) {
                SUCCESS -> mapOf("success" to "You deleted like successfully")
                ERROR -> mapOf("error" to "Can't delete like")
                UNKNOWN -> mapOf("error" to "Unknown error on server")
            }
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to "Can't delete like on map_point"))
        }
    }
}