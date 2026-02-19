package com.miplan.routes

import com.miplan.models.requests.*
import com.miplan.models.responses.ApiResponse
import com.miplan.services.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.kanbanRoutes(
    columnService: ColumnService,
    cardService: CardService,
    checklistService: ChecklistService,
    attachmentService: AttachmentService,
    boardService: BoardService
) {
    
    authenticate("jwt") {
        
        // COLUMNS
        route("/api/columns") {
            post {
                try {
                    val request = call.receive<CreateColumnRequest>()
                    val column = columnService.createColumn(request)
                    call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "Columna creada", data = column))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            get("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@get
                    }
                    
                    val column = columnService.getColumnById(id)
                    if (column == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Columna no encontrada"))
                        return@get
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Columna obtenida", data = column))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            put("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val request = call.receive<UpdateColumnRequest>()
                    
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@put
                    }
                    
                    val column = columnService.updateColumn(id, request)
                    if (column == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Columna no encontrada"))
                        return@put
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Columna actualizada", data = column))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            delete("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@delete
                    }
                    
                    val deleted = columnService.deleteColumn(id)
                    if (!deleted) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Columna no encontrada"))
                        return@delete
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Columna eliminada", data = null))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            put("/{id}/move") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val request = call.receive<MoveColumnRequest>()
                    
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@put
                    }
                    
                    val column = columnService.moveColumn(id, request)
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
        
        // CARDS
        route("/api/cards") {
            post {
                try {
                    val request = call.receive<CreateCardRequest>()
                    val card = cardService.createCard(request)
                    call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "Tarjeta creada", data = card))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            get("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@get
                    }
                    
                    val card = cardService.getCardDetail(id)
                    if (card == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Tarjeta no encontrada"))
                        return@get
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Tarjeta obtenida", data = card))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            put("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val request = call.receive<UpdateCardRequest>()
                    
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@put
                    }
                    
                    val card = cardService.updateCard(id, request)
                    if (card == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Tarjeta no encontrada"))
                        return@put
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Tarjeta actualizada", data = card))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            delete("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@delete
                    }
                    
                    val deleted = cardService.deleteCard(id)
                    if (!deleted) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Tarjeta no encontrada"))
                        return@delete
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Tarjeta eliminada", data = null))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            put("/{id}/move") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val request = call.receive<MoveCardRequest>()
                    
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@put
                    }
                    
                    val card = cardService.moveCard(id, request)
                    if (card == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Tarjeta no encontrada"))
                        return@put
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Tarjeta movida", data = card))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            post("/{id}/copy") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val request = call.receive<CopyCardRequest>()
                    
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@post
                    }
                    
                    val card = cardService.copyCard(id, request)
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
        
        // CHECKLISTS
        route("/api/checklists") {
            post {
                try {
                    val request = call.receive<CreateChecklistRequest>()
                    val checklist = checklistService.createChecklist(request)
                    call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "Checklist creado", data = checklist))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            get("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@get
                    }
                    
                    val checklist = checklistService.getChecklistWithItems(id)
                    if (checklist == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Checklist no encontrado"))
                        return@get
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Checklist obtenido", data = checklist))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            put("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val request = call.receive<UpdateChecklistRequest>()
                    
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@put
                    }
                    
                    val checklist = checklistService.updateChecklist(id, request)
                    if (checklist == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Checklist no encontrado"))
                        return@put
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Checklist actualizado", data = checklist))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            delete("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@delete
                    }
                    
                    val deleted = checklistService.deleteChecklist(id)
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
        
        // CHECKLIST ITEMS
        route("/api/checklist-items") {
            post {
                try {
                    val request = call.receive<CreateChecklistItemRequest>()
                    val item = checklistService.createItem(request)
                    call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "Item creado", data = item))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            put("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    val request = call.receive<UpdateChecklistItemRequest>()
                    
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@put
                    }
                    
                    val item = checklistService.updateItem(id, request)
                    if (item == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Item no encontrado"))
                        return@put
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Item actualizado", data = item))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            put("/{id}/toggle") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@put
                    }
                    
                    val item = checklistService.toggleItemCompleted(id)
                    if (item == null) {
                        call.respond(HttpStatusCode.NotFound, ApiResponse<Unit>(success = false, message = "Item no encontrado"))
                        return@put
                    }
                    
                    call.respond(HttpStatusCode.OK, ApiResponse(success = true, message = "Item actualizado", data = item))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            delete("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@delete
                    }
                    
                    val deleted = checklistService.deleteItem(id)
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
        
        // ATTACHMENTS
        route("/api/attachments") {
            post {
                try {
                    val request = call.receive<CreateAttachmentRequest>()
                    val attachment = attachmentService.createAttachment(request)
                    call.respond(HttpStatusCode.Created, ApiResponse(success = true, message = "Archivo agregado", data = attachment))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ApiResponse<Unit>(success = false, message = "Error: ${e.message}"))
                }
            }
            
            delete("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, ApiResponse<Unit>(success = false, message = "ID inválido"))
                        return@delete
                    }
                    
                    val deleted = attachmentService.deleteAttachment(id)
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
