package com.dhia.Upvertise.handler;

public class EntityNotFoundException extends BusinessException {
    //Used when an entity (e.g., Sponsorship, SponsorOffer, SupplierOffer)
    //does not exist in the database.
    public EntityNotFoundException(String entityName, Integer id) {
        super(BusinessErrorCodes.ENTITY_NOT_FOUND, entityName + " with ID " + id + " not found.");
    }
}
