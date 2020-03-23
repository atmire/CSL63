/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package com.atmire.csl.dao;

import java.sql.SQLException;
import java.util.List;

import com.atmire.csl.CslStyle;
import org.dspace.content.Site;
import org.dspace.core.Context;
import org.dspace.core.GenericDAO;
import org.dspace.eperson.EPerson;

public interface CslStyleDAO extends GenericDAO<CslStyle> {

    CslStyle find(Context context, String alias, Site site) throws SQLException;

    CslStyle find(Context context, String alias, EPerson eperson) throws SQLException;

    List<CslStyle> findBySite(Context context, Site site) throws SQLException;

    List<CslStyle> findByEPerson(Context context, EPerson eperson) throws SQLException;

    List<CslStyle> findBySiteOrEPerson(Context context, Site site, EPerson eperson) throws SQLException;
}
