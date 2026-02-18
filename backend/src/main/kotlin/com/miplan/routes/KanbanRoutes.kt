package com.miplan.routes

import com.miplan.models.requests.*
import com.miplan.models.responses.ApiResponse
import com.miplan.services.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Rutas para gestión de columnas, tarjetas, checklists y attachments
 */
fun Route.kanbanRoutes(
    columnService: ColumnService,
    cardService: CardService,
    checklistService: ChecklistService,
    attachmentService: AttachmentService,
    boardService: BoardService
) {
    
    authenticate {
        
        // ==================== COLUMNS ====================
        route("/api/columns") {
            
            // POST /api/columns - Crear columna
            post {
                try {
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal?.payload?.getClaim("userId")?.asInt()
                    val request = call.receive<CreateColumnRequest>()
                    
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized, ApiResponse<Unit>(success = false, message = "Usuario no autenticado"))
                        return@post
                    }
                    
                    // Verificar propiedad del tablero
                    if (!boardService.isBoardOwnedByUser(request.boardId, userId)) {
                        call.respond(HttpStatusCode.Forbidden, ApiResponse<Unit>(success = false, message = "No tienes permiso"))
                        return@post
                    }
                    
                    val column = columnService.createColumn(request)
                    call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "Columna creada", data = column))
                    
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // GET /api/columns/{id} - Obtener columna
            get("/{id}") {
                try {
                    val columnId = call.parameters["id"]?.toIntOrNull()
                    if (columnId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@get
                    }
                    
                    val column = columnService.getColumnById(columnId)
                    if (column == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Columna no encontrada"))
                        return@get
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Columna obtenida", data = column))
                    
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // PUT /api/columns/{id} - Actualizar columna
            put("/{id}") {
                try {
                    val columnId = call.parameters["id"]?.toIntOrNull()
                    val request = call.receive<UpdateColumnRequest>()
                    
                    if (columnId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@put
                    }
                    
                    val column = columnService.updateColumn(columnId, request)
                    if (column == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Columna no encontrada"))
                        return@put
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Columna actualizada", data = column))
                    
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // DELETE /api/columns/{id} - Eliminar columna
            delete("/{id}") {
                try {
                    val columnId = call.parameters["id"]?.toIntOrNull()
                    if (columnId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@delete
                    }
                    
                    val deleted = columnService.deleteColumn(columnId)
                    if (!deleted) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Columna no encontrada"))
                        return@delete
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Columna eliminada", data = null))
                    
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // PUT /api/columns/{id}/move - Mover columna
            put("/{id}/move") {
                try {
                    val columnId = call.parameters["id"]?.toIntOrNull()
                    val request = call.receive<MoveColumnRequest>()
                    
                    if (columnId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@put
                    }
                    
                    val column = columnService.moveColumn(columnId, request)
                    if (column == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Columna no encontrada"))
                        return@put
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Columna movida", data = column))
                    
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
        }
        
        // ==================== CARDS ====================
        route("/api/cards") {
            
            // POST /api/cards - Crear tarjeta
            post {
                try {
                    val request = call.receive<CreateCardRequest>()
                    val card = cardService.createCard(request)
                    call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "Tarjeta creada", data = card))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // GET /api/cards/{id} - Obtener tarjeta con detalles
            get("/{id}") {
                try {
                    val cardId = call.parameters["id"]?.toIntOrNull()
                    if (cardId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@get
                    }
                    
                    val card = cardService.getCardDetail(cardId)
                    if (card == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Tarjeta no encontrada"))
                        return@get
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Tarjeta obtenida", data = card))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // PUT /api/cards/{id} - Actualizar tarjeta
            put("/{id}") {
                try {
                    val cardId = call.parameters["id"]?.toIntOrNull()
                    val request = call.receive<UpdateCardRequest>()
                    
                    if (cardId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@put
                    }
                    
                    val card = cardService.updateCard(cardId, request)
                    if (card == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Tarjeta no encontrada"))
                        return@put
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Tarjeta actualizada", data = card))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // DELETE /api/cards/{id} - Eliminar tarjeta
            delete("/{id}") {
                try {
                    val cardId = call.parameters["id"]?.toIntOrNull()
                    if (cardId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@delete
                    }
                    
                    val deleted = cardService.deleteCard(cardId)
                    if (!deleted) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Tarjeta no encontrada"))
                        return@delete
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Tarjeta eliminada", data = null))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // PUT /api/cards/{id}/move - Mover tarjeta
            put("/{id}/move") {
                try {
                    val cardId = call.parameters["id"]?.toIntOrNull()
                    val request = call.receive<MoveCardRequest>()
                    
                    if (cardId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@put
                    }
                    
                    val card = cardService.moveCard(cardId, request)
                    if (card == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Tarjeta no encontrada"))
                        return@put
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Tarjeta movida", data = card))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // POST /api/cards/{id}/copy - Copiar tarjeta
            post("/{id}/copy") {
                try {
                    val cardId = call.parameters["id"]?.toIntOrNull()
                    val request = call.receive<CopyCardRequest>()
                    
                    if (cardId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@post
                    }
                    
                    val card = cardService.copyCard(cardId, request)
                    if (card == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Tarjeta no encontrada"))
                        return@post
                    }
                    
                    call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "Tarjeta copiada", data = card))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
        }
        
        // ==================== CHECKLISTS ====================
        route("/api/checklists") {
            
            // POST /api/checklists - Crear checklist
            post {
                try {
                    val request = call.receive<CreateChecklistRequest>()
                    val checklist = checklistService.createChecklist(request)
                    call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "Checklist creado", data = checklist))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // GET /api/checklists/{id} - Obtener checklist con items
            get("/{id}") {
                try {
                    val checklistId = call.parameters["id"]?.toIntOrNull()
                    if (checklistId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@get
                    }
                    
                    val checklist = checklistService.getChecklistWithItems(checklistId)
                    if (checklist == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Checklist no encontrado"))
                        return@get
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Checklist obtenido", data = checklist))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // PUT /api/checklists/{id} - Actualizar checklist
            put("/{id}") {
                try {
                    val checklistId = call.parameters["id"]?.toIntOrNull()
                    val request = call.receive<UpdateChecklistRequest>()
                    
                    if (checklistId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@put
                    }
                    
                    val checklist = checklistService.updateChecklist(checklistId, request)
                    if (checklist == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Checklist no encontrado"))
                        return@put
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Checklist actualizado", data = checklist))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // DELETE /api/checklists/{id} - Eliminar checklist
            delete("/{id}") {
                try {
                    val checklistId = call.parameters["id"]?.toIntOrNull()
                    if (checklistId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@delete
                    }
                    
                    val deleted = checklistService.deleteChecklist(checklistId)
                    if (!deleted) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Checklist no encontrado"))
                        return@delete
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Checklist eliminado", data = null))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
        }
        
        // ==================== CHECKLIST ITEMS ====================
        route("/api/checklist-items") {
            
            // POST /api/checklist-items - Crear item
            post {
                try {
                    val request = call.receive<CreateChecklistItemRequest>()
                    val item = checklistService.createItem(request)
                    call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "Item creado", data = item))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // PUT /api/checklist-items/{id} - Actualizar item
            put("/{id}") {
                try {
                    val itemId = call.parameters["id"]?.toIntOrNull()
                    val request = call.receive<UpdateChecklistItemRequest>()
                    
                    if (itemId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@put
                    }
                    
                    val item = checklistService.updateItem(itemId, request)
                    if (item == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Item no encontrado"))
                        return@put
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Item actualizado", data = item))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // PUT /api/checklist-items/{id}/toggle - Toggle completado
            put("/{id}/toggle") {
                try {
                    val itemId = call.parameters["id"]?.toIntOrNull()
                    
                    if (itemId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@put
                    }
                    
                    val item = checklistService.toggleItemCompleted(itemId)
                    if (item == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Item no encontrado"))
                        return@put
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Item actualizado", data = item))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // DELETE /api/checklist-items/{id} - Eliminar item
            delete("/{id}") {
                try {
                    val itemId = call.parameters["id"]?.toIntOrNull()
                    if (itemId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@delete
                    }
                    
                    val deleted = checklistService.deleteItem(itemId)
                    if (!deleted) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Item no encontrado"))
                        return@delete
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Item eliminado", data = null))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
        }
        
        // ==================== ATTACHMENTS ====================
        route("/api/attachments") {
            
            // POST /api/attachments - Crear attachment
            post {
                try {
                    val request = call.receive<CreateAttachmentRequest>()
                    val attachment = attachmentService.createAttachment(request)
                    call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "Archivo agregado", data = attachment))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            // DELETE /api/attachments/{id} - Eliminar attachment
            delete("/{id}") {
                try {
                    val attachmentId = call.parameters["id"]?.toIntOrNull()
                    if (attachmentId == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@delete
                    }
                    
                    val deleted = attachmentService.deleteAttachment(attachmentId)
                    if (!deleted) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Archivo no encontrado"))
                        return@delete
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Archivo eliminado", data = null))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
        }
    }
}
