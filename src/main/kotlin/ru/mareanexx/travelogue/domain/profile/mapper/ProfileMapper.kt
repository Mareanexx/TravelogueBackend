package ru.mareanexx.travelogue.domain.profile.mapper

import ru.mareanexx.travelogue.domain.profile.ProfileEntity
import ru.mareanexx.travelogue.domain.profile.dto.NewProfileRequest
import ru.mareanexx.travelogue.domain.profile.dto.UpdateProfileRequest

fun NewProfileRequest.mapToProfile(
    avatarPath: String?
) = ProfileEntity(
    username = username,
    fullName = fullName,
    bio = bio,
    coverPhoto = null,
    avatar = avatarPath,
    userUUID = userUUID
)

fun ProfileEntity.copyChangedProperties(
    updProfile: UpdateProfileRequest,
    newAvatarPath: String?,
    newCoverPath: String?
) = this.copy(
    username = updProfile.username ?: this.username,
    fullName = updProfile.fullName ?: this.fullName,
    bio = updProfile.bio ?: this.bio,
    avatar = newAvatarPath,
    coverPhoto = newCoverPath
)