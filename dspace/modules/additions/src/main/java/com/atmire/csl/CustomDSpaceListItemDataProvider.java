package com.atmire.csl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.undercouch.citeproc.csl.CSLItemDataBuilder;
import de.undercouch.citeproc.csl.CSLName;
import org.apache.commons.lang3.StringUtils;
import org.dspace.content.DCPersonName;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;


public class CustomDSpaceListItemDataProvider extends DSpaceListItemDataProvider {

    protected CSLItemDataBuilder handleCslNameFields(Item item, CSLItemDataBuilder cslItemDataBuilder)
            throws ParseException {

        cslItemDataBuilder = super.handleCslNameFields(item, cslItemDataBuilder);

        List<MetadataValue> metadataAuthors = itemService.getMetadataByMetadataString(item, "dc.contributor.author");
        List<CSLName> illustrators = new ArrayList<>();
        List<CSLName> editorialDirectors = new ArrayList<>();
        List<CSLName> authors = new ArrayList<>();

        for (MetadataValue author : metadataAuthors) {
            boolean hasRoleMetadata = false;
            DCPersonName dcPersonName = new DCPersonName(author.getValue());

            if (!hasRoleMetadata) {
                authors.add(new CSLName(dcPersonName.getLastName(), dcPersonName.getFirstNames(), null, null, null, null, null, null, null, null, null, null));
            }
        }
        cslItemDataBuilder.illustrator(illustrators.toArray(new CSLName[0]));
        cslItemDataBuilder.editorialDirector(editorialDirectors.toArray(new CSLName[0]));
        cslItemDataBuilder.author(authors.toArray(new CSLName[0]));


        return cslItemDataBuilder;
    }

    @Override
    protected String getMetadataFirstValueFromItem(Item item, String metadataField) throws ParseException {
        String value = super.getMetadataFirstValueFromItem(item, metadataField);
        if(StringUtils.isEmpty(value))
        {
            if(metadataField.equals("dc.title.en"))
            {
                value = super.getMetadataFirstValueFromItem(item, "dc.title");
            }
            else if(metadataField.equals("dc.description.abstractEn"))
            {
                value = super.getMetadataFirstValueFromItem(item, "dc.description.abstract");
            }
        }
        return value;
    }
}
