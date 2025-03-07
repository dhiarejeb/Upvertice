package com.dhia.Upvertise.handler;

public class DuplicateEntryException extends BusinessException {
    //Used when trying to create a record that already exists
    //(e.g., duplicate sponsorship, duplicate supplier offer).
    public DuplicateEntryException(String entityName, String field) {
        super(BusinessErrorCodes.DUPLICATE_ENTRY, "A " + entityName + " already exists with the same " + field + ".");
    }
}
