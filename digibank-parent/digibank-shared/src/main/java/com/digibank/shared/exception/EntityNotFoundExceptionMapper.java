package com.digibank.shared.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class EntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {

    @Override
    public Response toResponse(EntityNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of(
                        "error", "Not found",
                        "message", exception.getMessage(),
                        "entity", exception.getEntityName(),
                        "id", exception.getEntityId()
                ))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
