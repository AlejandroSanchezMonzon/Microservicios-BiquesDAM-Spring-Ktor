package es.dam.bique.microservicioproductoservicios.mappers

import es.dam.bique.microservicioproductoservicios.dto.AppointmentCreateDTO
import es.dam.bique.microservicioproductoservicios.dto.AppointmentDTO
import es.dam.bique.microservicioproductoservicios.models.Appointment
import es.dam.bique.microservicioproductoservicios.models.AssistanceType

import java.time.LocalDateTime
import java.util.*

fun AppointmentDTO.toEntity(): Appointment{
    return Appointment(
        id = id,
        uuid = UUID.fromString(this.uuid),
        user = this.user,
        assistance = AssistanceType.from(assistance),
        date = LocalDateTime.parse(date),
        description = this.description
    )
}

fun Appointment.toDTO(): AppointmentDTO{
    return AppointmentDTO(
        id = id.toString().toLong(),
        uuid = uuid.toString(),
        user = user,
        assistance = assistance.value,
        date = date.toString(),
        description = description
    )
}

fun AppointmentCreateDTO.toModel(): Appointment{
    return Appointment(
        user = this.user,
        assistance = AssistanceType.from(assistance),
        date = LocalDateTime.parse(date),
        description = this.description
    )
}