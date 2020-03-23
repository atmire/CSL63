package com.atmire.content.factory;

import com.atmire.csl.service.CSLCitationService;
import org.dspace.services.factory.DSpaceServicesFactory;

public abstract class CSLCitationServiceFactory {

    public abstract CSLCitationService getCslCitationService();

    public static CSLCitationServiceFactory getInstance() {
        return DSpaceServicesFactory.getInstance().getServiceManager()
                                    .getServiceByName("cslCitationServiceFactory", CSLCitationServiceFactory.class);
    }
}
