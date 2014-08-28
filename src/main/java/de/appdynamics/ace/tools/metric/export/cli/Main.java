package de.appdynamics.ace.tools.metric.export.cli;

import com.appdynamics.ace.util.cli.api.api.CommandlineExecution;

/**
 * Created by stefan.marx on 20.08.14.
 */
public class Main {

    /** Main Command */
    public static void main(String[] args) {

        CommandlineExecution cle = new CommandlineExecution("MetricExportCLI");

        cle.setHelpVerboseEnabled(false);

        cle.addCommand(new ExportCommand());

        System.exit(cle.execute(args));
    }
}
