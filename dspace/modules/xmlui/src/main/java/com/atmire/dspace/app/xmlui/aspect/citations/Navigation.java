/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package com.atmire.dspace.app.xmlui.aspect.citations;

import static org.apache.excalibur.source.impl.validity.NOPValidity.SHARED_INSTANCE;

import java.io.Serializable;

import org.apache.cocoon.caching.CacheableProcessingComponent;
import org.apache.excalibur.source.SourceValidity;
import org.dspace.app.xmlui.cocoon.AbstractDSpaceTransformer;
import org.dspace.app.xmlui.wing.Message;
import org.dspace.app.xmlui.wing.WingException;
import org.dspace.app.xmlui.wing.element.Options;

public class Navigation extends AbstractDSpaceTransformer implements CacheableProcessingComponent {
    protected static final Message T_citations =
            message("xmlui.Citations.Navigation");

    public Serializable getKey() {
        return 1;
    }

    public SourceValidity getValidity() {
        return SHARED_INSTANCE;
    }

    public void addOptions(Options options) throws WingException {

        options.addList("account").addItemXref(contextPath + "/citations", T_citations);
    }
}
