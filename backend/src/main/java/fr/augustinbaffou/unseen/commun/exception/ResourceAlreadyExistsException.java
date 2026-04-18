package fr.augustinbaffou.unseen.commun.exception;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String resourceName, String fieldName, Object fieldValue) {
        super(ExceptionMessages.RESOURCE_ALREADY_EXISTS.formatted(resourceName, fieldName, fieldValue));
    }
}
