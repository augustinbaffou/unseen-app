package fr.augustinbaffou.unseen.commun.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(ExceptionMessages.RESOURCE_NOT_FOUND.formatted(resourceName, fieldName, fieldValue));
    }
}
