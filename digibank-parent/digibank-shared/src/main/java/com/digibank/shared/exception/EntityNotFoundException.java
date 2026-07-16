package com.digibank.shared.exception;

public class EntityNotFoundException extends RuntimeException {

    private final String entityName;
    private final Long entityId;

    public EntityNotFoundException(String entityName, Long entityId) {
        super(entityName + " with id " + entityId + " not found");
        this.entityName = entityName;
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public Long getEntityId() {
        return entityId;
    }
}
