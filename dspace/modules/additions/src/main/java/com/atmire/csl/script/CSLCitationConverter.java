package com.atmire.csl.script;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.atmire.content.factory.CSLCitationServiceFactory;
import com.atmire.csl.service.CSLCitationService;
import com.google.common.io.Files;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.Charsets;
import org.apache.log4j.Logger;
import org.dspace.content.Item;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;

public class CSLCitationConverter {

    /* Log4j logger*/
    private static final Logger log = Logger.getLogger(CSLCitationConverter.class);

    private static final String HELP_OPTION = "h";
    private static final String UUID_LIST_OPTION = "i";
    private static final String FILE_OPTION = "f";

    private List<String> uuids = new LinkedList<>();
    private String fileLocation = null;
    private ItemService itemService = ContentServiceFactory.getInstance().getItemService();
    private CSLCitationService cslCitationService = CSLCitationServiceFactory.getInstance().getCslCitationService();
    public static void main(String... args) throws ParseException, IOException, SQLException, java.text.ParseException {
        CSLCitationConverter script = new CSLCitationConverter();
        script.mainImpl(args);
    }

    private CSLCitationConverter() {
    }

    private void mainImpl(String... args) throws ParseException, IOException, SQLException, java.text.ParseException {
        Context context = new Context();
        processArguments(args);

        List<Item> items = new LinkedList<>();
        for (String uuid : uuids) {
            Item item = itemService.find(context, UUID.fromString(uuid));
            if (item != null) {
                items.add(item);
            }
        }
        String fileContent = Files.toString(new File(fileLocation), Charsets.UTF_8);
        String citation = cslCitationService.getCitationEntry(items, fileContent, "html");
        System.out.println(citation);
    }

    private void processArguments(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        Options options = createCommandLineOptions();
        CommandLine line = parser.parse(options, args);
        parseCommandLineOptions(options, line);
    }

    private void parseCommandLineOptions(Options options, CommandLine line) {
        if (line.hasOption(HELP_OPTION)) {
            printHelp(options);
            System.exit(0);
        }
        if (line.hasOption(UUID_LIST_OPTION)) {
            uuids = Arrays.asList(line.getOptionValue(UUID_LIST_OPTION).split(","));
        } else {
            log.error("The CSLCitationConverter script needs a list of UUIDs");
            System.out.println("The CSLCitationConverter script needs a list of UUIDs");
            printHelp(options);
            System.exit(0);
        }

        if (line.hasOption(FILE_OPTION)) {
            fileLocation = line.getOptionValue(FILE_OPTION);
        } else {
            log.error("The CSLCitationConverter script needs a CSL file");
            System.out.println("The CSLCitationConverter script needs a CSL file");
            printHelp(options);
            System.exit(0);
        }


    }

    private Options createCommandLineOptions() {
        Options options = new Options();
        options.addOption(HELP_OPTION, "help", false, "Print the usage of the script");
        options.addOption(UUID_LIST_OPTION, "uuids", true, "The list of UUIDs of Items to be made into a citation");
        options.addOption(FILE_OPTION, "file", true, "The citation file");
        return options;
    }

    private void printHelp(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("dsrun " + getClass().getCanonicalName(), options);
    }

}
