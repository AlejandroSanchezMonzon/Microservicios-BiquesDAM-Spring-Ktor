package biques.dam.es.routes

import biques.dam.es.dto.UserLoginDTO
import biques.dam.es.dto.UserRegisterDTO
import biques.dam.es.dto.UserUpdateDTO
import biques.dam.es.exceptions.UserBadRequestException
import biques.dam.es.exceptions.UserNotFoundException
import biques.dam.es.repositories.users.KtorFitRepositoryUsers
import biques.dam.es.services.token.TokensService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import mu.KotlinLogging
import org.koin.core.qualifier.named
import org.koin.ktor.ext.inject

/**
 * Represents the users routes of the application.
 * @author BiquesDAM-Team
 */
private val logger = KotlinLogging.logger {}
private const val ENDPOINT = "users"

fun Application.usersRoutes() {
    val userRepository by inject<KtorFitRepositoryUsers>(named("KtorFitRepositoryUsers"))
    //val userRepository = KtorFitRepositoryUsers()
    val tokenService by inject<TokensService>()

    routing {
        route("/$ENDPOINT") {
            /**
             * Login with username and password.
             * @throws UserBadRequestException if the user is not found.
             */
            post("/login") {
                logger.debug { "API Gateway -> /login" }

                try {
                    val login = call.receive<UserLoginDTO>()

                    val user = async {
                        userRepository.login(login)
                    }

                    call.respond(HttpStatusCode.OK, user.await())

                } catch (e: UserBadRequestException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                }
            }

            /**
             * Register a new user.
             * @throws UserBadRequestException if the user doesn't create.
             */
            post("/register") {
                logger.debug { "API Gateway -> /register" }

                try {
                    val register = call.receive<UserRegisterDTO>()

                    val user = async {
                        userRepository.register(register)
                    }

                    call.respond(HttpStatusCode.Created, user.await())

                } catch (e: UserBadRequestException) {
                    call.respond(HttpStatusCode.BadRequest, e.message.toString())
                }
            }

            authenticate {
                /**
                 * Retrieves all users.
                 * @throws UserNotFoundException if the users are not found.
                 */
                get {
                    logger.debug { "API Gateway -> /users" }
                    try {
                        val token = tokenService.generateToken(call.principal()!!)

                        val res = async {
                            userRepository.findAll("Bearer $token")
                        }
                        val users = res.await()

                        call.respond(HttpStatusCode.OK, users)

                    } catch (e: UserNotFoundException) {
                        call.respond(HttpStatusCode.NotFound, e.message.toString())
                    }
                }

                /**
                 * Retrieves a user by id.
                 * @throws UserNotFoundException if the user is not found.
                 */
                get("/{id}") {
                    logger.debug { "API Gateway -> /users/id" }

                    try {
                        val token = tokenService.generateToken(call.principal()!!)
                        val id = call.parameters["id"]

                        val user = async {
                            userRepository.findById("Bearer $token", id!!.toLong())
                        }

                        call.respond(HttpStatusCode.OK, user.await())

                    } catch (e: UserNotFoundException) {
                        call.respond(HttpStatusCode.NotFound, e.message.toString())
                    }
                }

                /**
                 * Updates a user by id.
                 * @throws UserNotFoundException if the user is not found.
                 * @throws UserBadRequestException if the user doesn't update.
                 */
                put("/{id}") {
                    logger.debug { "API Gateway -> /users/id" }

                    try {
                        val token = tokenService.generateToken(call.principal()!!)
                        val id = call.parameters["id"]
                        val user = call.receive<UserUpdateDTO>()

                        val updatedUser = async {
                            userRepository.update("Bearer $token", id!!.toLong(), user)
                        }

                        call.respond(HttpStatusCode.OK, updatedUser.await())

                    } catch (e: UserNotFoundException) {
                        call.respond(HttpStatusCode.NotFound, e.message.toString())
                    } catch (e: UserBadRequestException) {
                        call.respond(HttpStatusCode.BadRequest, e.message.toString())
                    }
                }

                /**
                 * Deletes a user by id.
                 * @throws UserNotFoundException if the user is not found.
                 */
                delete("/{id}") {
                    logger.debug { "API Gateway -> /users/id" }

                    try {
                        val token = tokenService.generateToken(call.principal()!!)
                        val id = call.parameters["id"]

                        userRepository.delete("Bearer $token", id!!.toLong())

                        call.respond(HttpStatusCode.NoContent)
                    } catch (e: UserNotFoundException) {
                        call.respond(HttpStatusCode.NotFound, e.message.toString())
                    }
                }
            }
        }
    }
}