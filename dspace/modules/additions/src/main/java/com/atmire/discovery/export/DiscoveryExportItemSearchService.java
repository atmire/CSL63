package com.atmire.discovery.export;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dspace.authorize.factory.AuthorizeServiceFactory;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.discovery.DiscoverFacetField;
import org.dspace.discovery.DiscoverHitHighlightingField;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverResult;
import org.dspace.discovery.SearchServiceException;
import org.dspace.discovery.SearchUtils;
import org.dspace.discovery.configuration.DiscoveryConfiguration;
import org.dspace.discovery.configuration.DiscoveryConfigurationParameters;
import org.dspace.discovery.configuration.DiscoveryHitHighlightFieldConfiguration;
import org.dspace.discovery.configuration.DiscoverySortConfiguration;
import org.dspace.discovery.configuration.DiscoverySortFieldConfiguration;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.services.factory.DSpaceServicesFactory;

public class DiscoveryExportItemSearchService {

    public static Logger log = Logger.getLogger(DiscoveryExportItemSearchService.class);
    

    public List<Item> getItems(Context context, Map<String, String> parameters, String[] filterQueries) {
        return getItems(context, parameters, filterQueries, -1);
    }

    public List<Item> getItems(Context context, Map<String, String> parameters, String[] filterQueries,
                               boolean findAll) {
        if (findAll) {
            return getItems(context, parameters, filterQueries, Integer.MAX_VALUE);
        }
        return getItems(context, parameters, filterQueries, -1);
    }


    public List<Item> getItems(Context context, Map<String, String> parameters, String[] filterQueries,
                               int maxSearchResults) {
        List<Item> items = new ArrayList<>();

        try {
            String query = parameters.get("query");

            String scopeString = parameters.get("scope");
            if (StringUtils.isEmpty(scopeString)) {
                scopeString = parameters.get("handle");
            }
            DSpaceObject scope = null;
            if (StringUtils.isNotBlank(scopeString)) {
                scope = HandleServiceFactory.getInstance().getHandleService().resolveToObject(context, scopeString);
            }

            String sortBy = parameters.get("sort_by");

            DiscoverResult queryResults = null;

            int page = 1;
                queryResults = performSearch(context, scope, query, filterQueries, sortBy, page, parameters,
                                             maxSearchResults);

                for (DSpaceObject resultDso : queryResults.getDspaceObjects()) {
                    if (resultDso.getType() == Constants.ITEM) {
                        items.add((Item) resultDso);
                    }
                }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return items;
    }

    private DiscoverResult performSearch(Context context, DSpaceObject scope, String query, String[] fqs, String sortBy,
                                         int page, Map<String, String> parameters, int maxSearchResults)
            throws SearchServiceException, SQLException {

        List<String> filterQueries = new ArrayList<String>();

        if (fqs != null) {
            filterQueries.addAll(Arrays.asList(fqs));
        }

        DiscoverQuery queryArgs = new DiscoverQuery();

        //Add the configured default filter queries
        DiscoveryConfiguration discoveryConfiguration = SearchUtils.getDiscoveryConfiguration(scope);
        List<String> defaultFilterQueries = discoveryConfiguration.getDefaultFilterQueries();
        queryArgs.addFilterQueries(defaultFilterQueries.toArray(new String[defaultFilterQueries.size()]));

        if (filterQueries.size() > 0) {
            queryArgs.addFilterQueries(filterQueries.toArray(new String[filterQueries.size()]));
        }

        if (maxSearchResults != -1) {
            queryArgs.setMaxResults(maxSearchResults);
        } else {
            int maxResults = DSpaceServicesFactory.getInstance().getConfigurationService().getIntProperty(
                    "discovery.export.maxResults", 500);
            if (AuthorizeServiceFactory.getInstance().getAuthorizeService().isAdmin(context)) {
                maxResults = DSpaceServicesFactory.getInstance().getConfigurationService()
                                                  .getIntProperty("discovery.export.maxResults.admin", maxResults);
            }
            queryArgs.setMaxResults(maxResults);
        }

        DiscoverySortConfiguration searchSortConfiguration = discoveryConfiguration.getSearchSortConfiguration();
        if (sortBy == null) {
            //Attempt to find the default one, if none found we use SCORE
            sortBy = "score";
            if (searchSortConfiguration != null) {
                for (DiscoverySortFieldConfiguration sortFieldConfiguration : searchSortConfiguration.getSortFields()) {
                    if (sortFieldConfiguration.equals(searchSortConfiguration.getDefaultSort())) {
                        sortBy = SearchUtils.getSearchService()
                                            .toSortFieldIndex(sortFieldConfiguration.getMetadataField(),
                                                              sortFieldConfiguration.getType());
                    }
                }
            }
        }
        String sortOrder = parameters.get("order");
        if (sortOrder == null && searchSortConfiguration != null) {
            sortOrder = searchSortConfiguration.getDefaultSortOrder().toString();
        }

        if (sortOrder == null || sortOrder.equalsIgnoreCase("DESC")) {
            queryArgs.setSortField(sortBy, DiscoverQuery.SORT_ORDER.desc);
        } else {
            queryArgs.setSortField(sortBy, DiscoverQuery.SORT_ORDER.asc);
        }


        String groupBy = parameters.get("group_by");


        // Enable groupBy collapsing if designated
        if (groupBy != null && !groupBy.equalsIgnoreCase("none")) {
            /** Construct a Collapse Field Query */
            queryArgs.addProperty("collapse.field", groupBy);
            queryArgs.addProperty("collapse.threshold", "1");
            queryArgs.addProperty("collapse.includeCollapsedDocs.fl", "handle");
            queryArgs.addProperty("collapse.facet", "before");

            queryArgs.setSortField("dc.type", DiscoverQuery.SORT_ORDER.asc);

        }

        queryArgs.setQuery(query != null && !query.trim().equals("") ? query : null);

        if (page > 1) {
            queryArgs.setStart((page - 1) * queryArgs.getMaxResults());
        } else {
            queryArgs.setStart(0);
        }

        if (discoveryConfiguration.getHitHighlightingConfiguration() != null) {
            List<DiscoveryHitHighlightFieldConfiguration> metadataFields = discoveryConfiguration
                    .getHitHighlightingConfiguration().getMetadataFields();
            for (DiscoveryHitHighlightFieldConfiguration fieldConfiguration : metadataFields) {
                queryArgs.addHitHighlightingField(
                        new DiscoverHitHighlightingField(fieldConfiguration.getField(), fieldConfiguration.getMaxSize(),
                                                         fieldConfiguration.getSnippets()));
            }
        }

        new DiscoverFacetField("search.resourceid", "custom", Integer.MAX_VALUE,
                               DiscoveryConfigurationParameters.SORT.VALUE);

        return SearchUtils.getSearchService().search(context, scope, queryArgs);
    }
}
