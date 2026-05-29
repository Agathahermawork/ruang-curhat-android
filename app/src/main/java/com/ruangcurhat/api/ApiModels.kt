package com.ruangcurhat.api

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)

data class ListResponse<T>(
    val success: Boolean,
    val data: List<T>?
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String?,
    val token_type: String?,
    val token: String?,
    val data: UserDto?
)

data class RegisterRequest(
    val email: String,
    val password: String?,
    val name: String,
    val pangkat: String,
    val nrp: String,
    val jabatan: String?,
    val kesatuan: String?,
    val telegram: String?,
    val role: String?
)

data class UpdateProfileRequest(
    val name: String,
    val pangkat: String,
    val nrp: String,
    val jabatan: String?,
    val kesatuan: String?,
    val telegram: String?,
    val password: String?
)

data class UserDto(
    val id: Int,
    val email: String,
    val name: String,
    val pangkat: String?,
    val nrp: String?,
    val jabatan: String?,
    val kesatuan: String?,
    val telegram: String?,
    val role: String?
)

data class CounselorRequest(
    val name: String,
    val pangkat: String,
    val nrp: String,
    val jabatan: String?,
    val kesatuan: String?,
    val telegram: String,
    val religion: String
)

data class CounselorDto(
    val id: Int,
    val name: String,
    val pangkat: String,
    val nrp: String,
    val jabatan: String?,
    val kesatuan: String?,
    val telegram: String,
    val religion: String,
    val emoji: String?
)
