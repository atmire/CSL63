package com.atmire.content.factory.impl;

import com.atmire.content.factory.CSLCitationServiceFactory;
import com.atmire.csl.service.CSLCitationService;
import org.springframework.beans.factory.annotation.Autowired;

public class CSLCitationServiceFactoryImpl extends CSLCitationServiceFactory {

    @Autowired(required = true)
    private CSLCitationService cslCitationService;

    public CSLCitationService getCslCitationService() {
        return cslCitationService;
    }
}
