package de.appdynamics.ace.tools.metric.export.cli;

import com.appdynamics.ace.util.cli.api.api.AbstractCommand;
import de.appdynamics.ace.metric.query.rest.ControllerRestAccess;
import org.apache.commons.cli.Option;

import com.appdynamics.ace.util.cli.api.api.CommandException;
import com.appdynamics.ace.util.cli.api.api.OptionWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: stefan.marx
 * Date: 12.11.13
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
public abstract class RestCommand extends AbstractCommand {
    public static final String ARG_CONTROLLER_HOST = "controllerHost";
    public static final String ARG_CONTROLLER_PORT = "controllerPort";
    public static final String ARG_CONTROLLER_SSL = "ssl";
    public static final String ARG_USERNAME = "username";
    public static final String ARG_ACCOUNT = "account";
    public static final String ARG_PASSWORD = "password";

    @Override
    protected List<Option> getCLIOptionsImpl() {
        ArrayList<Option> opts = new ArrayList<Option>();

        // TODO: Add REST Options
        Option o;
        opts.add(o = new Option(ARG_CONTROLLER_HOST, true, "The Host where the Controller can be reached "));
        o.setRequired(true);
        opts.add(o = new Option(ARG_CONTROLLER_PORT, true, "The Port where the Controller can be reached (defaults to 8090)"));
        opts.add(o = new Option(ARG_CONTROLLER_SSL, false, "Declare if need to use ssl to connect!"));


        opts.add(o = new Option(ARG_USERNAME, true, "Username on Controller used to create synced users"));
        o.setRequired(true);
        opts.add(o = new Option(ARG_ACCOUNT, true, "Account on Controller used to create synced users"));
        o.setRequired(false);
        opts.add(o = new Option(ARG_PASSWORD, true, "Password on Controller used to create synced users"));
        o.setRequired(true);

        Collection<? extends Option> additional = getAdditionalCLIOptions();
        if (additional != null)
            opts.addAll(additional);

        return opts;

    }

    protected abstract Collection<? extends Option> getAdditionalCLIOptions();

    @Override
    protected int executeImpl(OptionWrapper optionWrapper) throws CommandException {
        ControllerRestAccess client = new ControllerRestAccess(optionWrapper.getOptionValue(ARG_CONTROLLER_HOST),
                optionWrapper.getOptionValue(ARG_CONTROLLER_PORT, "8090"),
                optionWrapper.hasOption(ARG_CONTROLLER_SSL),
                optionWrapper.getOptionValue(ARG_USERNAME),
                optionWrapper.getOptionValue(ARG_PASSWORD),
                optionWrapper.getOptionValue(ARG_ACCOUNT, "customer1"));

        return executeRestCommand(optionWrapper,client);


    }

    protected abstract int executeRestCommand(OptionWrapper optionWrapper, ControllerRestAccess client) throws CommandException;




}