package com.miplan.routes

import com.miplan.models.requests.CreateBoardRequest
import com.miplan.models.requests.UpdateBoardRequest
import com.miplan.models.responses.ApiResponse
import com.miplan.services.BoardService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.boardRoutes(boardService: BoardService) {
    
    route("/api/boards") {
        
        authenticate("auth-jwt") {
            
            get {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                    
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(success = false, message = "Usuario no autenticado"))
                        return@get
                    }
                    
                    val boards = boardService.getBoardsByUserId(userId)
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Tableros obtenidos", data = boards))
                    
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            get("/{id}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                    val boardId = call.parameters["id"]?.toIntOrNull()
                    
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(success = false, message = "Usuario no autenticado"))
                        return@get
                    }
                    
                    if (boardId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@get
                    }
                    
                    if (!boardService.isBoardOwnedByUser(boardId, userId)) {
                        call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(success = false, message = "Sin permiso"))
                        return@get
                    }
                    
                    val board = boardService.getBoardDetail(boardId)
                    if (board == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Tablero no encontrado"))
                        return@get
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Tablero obtenido", data = board))
                    
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            post {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                    val request = call.receive<CreateBoardRequest>()
                    
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(success = false, message = "Usuario no autenticado"))
                        return@post
                    }
                    
                    val board = boardService.createBoard(request, userId)
                    if (board == null) {
                        call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error al crear tablero"))
                        return@post
                    }
                    
                    call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "Tablero creado", data = board))
                    
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            put("/{id}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                    val boardId = call.parameters["id"]?.toIntOrNull()
                    val request = call.receive<UpdateBoardRequest>()
                    
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(success = false, message = "Usuario no autenticado"))
                        return@put
                    }
                    
                    if (boardId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@put
                    }
                    
                    if (!boardService.isBoardOwnedByUser(boardId, userId)) {
                        call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(success = false, message = "Sin permiso"))
                        return@put
                    }
                    
                    val updated = boardService.updateBoard(boardId, request)
                    if (!updated) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Tablero no encontrado"))
                        return@put
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Tablero actualizado", data = null))
                    
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            delete("/{id}") {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                    val boardId = call.parameters["id"]?.toIntOrNull()
                    
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(success = false, message = "Usuario no autenticado"))
                        return@delete
                    }
                    
                    if (boardId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@delete
                    }
                    
                    if (!boardService.isBoardOwnedByUser(boardId, userId)) {
                        call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(success = false, message = "Sin permiso"))
                        return@delete
                    }
                    
                    val deleted = boardService.deleteBoard(boardId)
                    if (!deleted) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Tablero no encontrado"))
                        return@delete
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Tablero eliminado", data = null))
                    
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
        }
    }
}
