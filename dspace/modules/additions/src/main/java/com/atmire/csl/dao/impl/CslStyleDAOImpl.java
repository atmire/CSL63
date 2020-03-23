/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 * <p>
 * http://www.dspace.org/license/
 */
package com.atmire.csl.dao.impl;

import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.or;

import java.sql.SQLException;
import java.util.List;

import com.atmire.csl.CslStyle;
import com.atmire.csl.dao.CslStyleDAO;
import org.dspace.content.Site;
import org.dspace.core.AbstractHibernateDAO;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.hibernate.Criteria;

public class CslStyleDAOImpl extends AbstractHibernateDAO<CslStyle> implements CslStyleDAO {

    @Override
    public CslStyle find(Context context, String alias, Site site) throws SQLException {

        Criteria criteria = createCriteria(context, CslStyle.class);

        criteria.add(eq("alias", alias));
        criteria.add(eq("site", site));

        return uniqueResult(criteria);
    }

    @Override
    public CslStyle find(Context context, String alias, EPerson eperson) throws SQLException {

        Criteria criteria = createCriteria(context, CslStyle.class);

        criteria.add(eq("alias", alias));
        criteria.add(eq("eperson", eperson));

        return uniqueResult(criteria);
    }

    @Override
    public List<CslStyle> findBySite(Context context, Site site) throws SQLException {

        Criteria criteria = createCriteria(context, CslStyle.class);

        criteria.add(eq("site", site));

        return list(criteria);
    }

    @Override
    public List<CslStyle> findByEPerson(Context context, EPerson eperson) throws SQLException {

        Criteria criteria = createCriteria(context, CslStyle.class);

        criteria.add(eq("eperson", eperson));

        return list(criteria);
    }

    @Override
    public List<CslStyle> findBySiteOrEPerson(Context context, Site site, EPerson eperson) throws SQLException {

        Criteria criteria = createCriteria(context, CslStyle.class);

        criteria.add(
                or(
                        eq("site", site),
                        eq("eperson", eperson)
                )
        );

        return list(criteria);
    }
}
