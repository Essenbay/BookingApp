package com.example.bookingapp.util

class UserNotSignedIn() : Exception("User not signed in")

class EstablishmentNotFound() : Exception("Establishment not found")
class CommentNotFound() : Exception("CommentNotFound")

class ReservationIsNotFree(): Exception("Reservation is not free")