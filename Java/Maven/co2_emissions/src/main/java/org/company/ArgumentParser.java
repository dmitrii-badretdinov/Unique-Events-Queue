package org.company;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * The class that parses the arguments received from the Command Line Interface.
 * The help does not show possible values for the options, but within the 8-hour timeframe,
 * I haven't found a library that was in repo.maven.apache.org/maven2/org/ and had it.
 */
public class ArgumentParser {
    /**
     * The constructor is protected in case the class is extended.
     * Parser assumes that distance is an Integer.
     * I didn't risk to put common parts in a dummy function for the initialization
     * because I didn't have time to check if they are passed by value or reference.
     * In addition, the verbosity is compensated by the clarity of what happens.
     * @param CO2EmissionDictionary A dictionary of vehicle names and their emissions in grams.
     */
    protected ArgumentParser(Map<String, Integer> CO2EmissionDictionary) {
        _CO2EmissionDictionary = CO2EmissionDictionary;
        _options = new Options();
        _options.addOption(Option.builder("distance")
                        .hasArg(true)
                        .desc("Distance traveled. The unit of measurement is handled by the parameter --unit-of-distance.")
                        .required(true)
                        .build());
        _options.addOption(Option.builder("unit-of-distance")
                        .hasArg(true)
                        .desc("Unit in which the traveled distance is measured. Default value is kilometers.")
                        .required(false)
                        .build());
        _options.addOption(Option.builder("transportation-method")
                        .hasArg(true)
                        .desc("Method of transportation. Most of the time it's the type of the vehicle.")
                        .required(true)
                        .build());
        _options.addOption(Option.builder("output")
                        .hasArg(true)
                        .desc("The unit of measurement for the output.")
                        .required(false)
                        .build());
    }

    /**
     * Parses the arguments into a standardized dictionary that is later used
     * in calculations.
     * @param args A string of arguments given as the input to the program.
     * @return A dictionary (string, string) with the data for calculation.
     */
    protected Map<String, String> parse(String[] args){
        Map<String, String> dictionaryToReturn= new HashMap<>();

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(_options, args);
        }
        catch (ParseException exp) {
            formatter.printHelp("SAP_DevelopmentChallenge_Solution", _options);
            System.out.println("Unexpected exception:" + exp.getMessage());
        }

        if (StringUtils.isNumeric(cmd.getOptionValue("distance"))) {
            dictionaryToReturn.put("distance", cmd.getOptionValue("distance"));
        }
        else {
            System.out.println("Distance is not a number. Exiting.");
            System.exit(1);
        }

        if (_CO2EmissionDictionary.containsKey(cmd.getOptionValue("transportation-method"))) {
            dictionaryToReturn.put("transportation-method", cmd.getOptionValue("transportation-method"));
        }
        else {
            System.out.println("Unrecognized transportation method. Exiting.");
            System.exit(1);
        }

        dictionaryToReturn.put("unit-of-distance", "");
        if (cmd.hasOption("unit-of-distance")) {
            if ( EnumUtils.isValidEnum(
                    UnitsOfDistance.class,
                    cmd.getOptionValue("unit-of-distance").toUpperCase()) ) {

                dictionaryToReturn.put("unit-of-distance", cmd.getOptionValue("unit-of-distance").toUpperCase());
            }
            else {
                System.out.println("Unrecognized unit of measurement for distance. Using defaults.");
            }
        }

        dictionaryToReturn.put("output", "");
        if (cmd.hasOption("output")) {
            if ( EnumUtils.isValidEnum(
                    OutputWeightUnit.class,
                    cmd.getOptionValue("output").toUpperCase()) ) {

                dictionaryToReturn.put("output", cmd.getOptionValue("output").toUpperCase());
            }
            else {
                System.out.println("Unrecognized unit of measurement for output. Using defaults.");
            }

        }

        return dictionaryToReturn;
    }

    private final Options _options;
    private final Map<String, Integer> _CO2EmissionDictionary;
}
