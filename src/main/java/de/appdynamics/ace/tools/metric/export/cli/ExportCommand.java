package de.appdynamics.ace.tools.metric.export.cli;

import com.appdynamics.ace.util.cli.api.api.CommandException;
import com.appdynamics.ace.util.cli.api.api.OptionWrapper;
import de.appdynamics.ace.metric.query.data.DataMap;
import de.appdynamics.ace.metric.query.parser.CompiledRestMetricQuery;
import de.appdynamics.ace.metric.query.parser.MetricParserException;
import de.appdynamics.ace.metric.query.parser.MetricQuery;
import de.appdynamics.ace.metric.query.parser.QueryException;
import de.appdynamics.ace.metric.query.rest.ControllerRestAccess;
import de.appdynamics.ace.reporting.printer.*;
import org.apache.commons.cli.Option;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stefan.marx on 20.08.14.
 */
public class ExportCommand extends RestCommand {

    private static final String ARG_CONTROLLER_FILE = "file";
    private static final String ARG_CONTROLLER_FORMAT = "format";


    private static Map<String,DataPrinter> printers = new HashMap<>();

    static {
        // Setup Formats
        //(xml,csv,table(*),pretty)
       printers.put("table",new TableDataPrinter());

        printers.put("csv",new CSVDataPrinter(",","\""));
        printers.put("pretty",new PrettyDataPrinter());
        printers.put("xml",new XMLDataPrinter(false));

    }
    @Override
    protected Collection<? extends Option> getAdditionalCLIOptions() {
        ArrayList<Option> opts = new ArrayList<Option>();

        // TODO: Add REST Options
        Option o;
        opts.add(o = new Option(ARG_CONTROLLER_FILE, true, "File to store the Data (defaults to stdout)"));
        opts.add(o = new Option(ARG_CONTROLLER_FORMAT, true, "The Format of Data (xml,csv,table(*),pretty) "));


        return opts;
    }

    @Override
    protected int executeRestCommand(OptionWrapper optionWrapper, ControllerRestAccess client) throws CommandException {

        int i = 0;
        for (String query : optionWrapper.getArgs()) {
            try {
                queryData(query,++i,optionWrapper,client);
            } catch (QueryException e) {
                e.printStackTrace();
                return 1;
            } catch (MetricParserException e) {
                e.printStackTrace();
                return 1;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return 1;
            }
        }


        return 0;
    }

    private void queryData(String query, int i, OptionWrapper optionWrapper, ControllerRestAccess client) throws QueryException, MetricParserException, FileNotFoundException, CommandException {

        String outputFormat = optionWrapper.getOptionValue(ARG_CONTROLLER_FORMAT,"table");
        DataPrinter printer = printers.get(outputFormat);

        if (printer == null) throw new CommandException("File Output Format ("+outputFormat+") not supported.");
        else {
            MetricQuery mq = new MetricQuery();


            CompiledRestMetricQuery erg = mq.parse( query);
            DataMap result = erg.execute(client,printer.isRequireSimplified());

            OutputStream os = System.out;

            if (optionWrapper.hasOption(ARG_CONTROLLER_FILE)) {
                String filename = optionWrapper.getOptionValue(ARG_CONTROLLER_FILE);
                if (i > 0) {
                    filename = filename+"."+i;
                }
                os = new FileOutputStream(new File(filename));
            }



            printer.printData(query,client,result,os);
        }


    }

    @Override
    public String getName() {
        return "export";
    }

    @Override
    public String getDescription() {
        return "Export Metrics based on metrics Query!";
    }

}
