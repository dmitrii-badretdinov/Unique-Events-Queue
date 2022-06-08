package org.company;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * The solution for the SAP Development Challenge.
 * @author Dmitrii Badretdinov
 * @since 2022-05-15
 */
public class Main {
    public static void main(String[] args) {

        final Map<String, Integer> CO2EmissionDictionary =
                Collections.unmodifiableMap(GetCO2EmissionsDictionary());

        ArgumentParser argumentParser = new ArgumentParser(CO2EmissionDictionary);

        final Map<String, String> dictionaryForCalculation = argumentParser.parse(args);
        Integer distance = Integer.parseInt(dictionaryForCalculation.get("distance"));
        if(dictionaryForCalculation.get("unit-of-distance").equals(UnitsOfDistance.M.name())) {
            distance *= 1000;
        }
        Integer transportationMethodCO2Emission =  CO2EmissionDictionary.get(dictionaryForCalculation.get("transportation-method"));
        Integer result = transportationMethodCO2Emission * distance;
        ResultPrinter resultPrinter = new ResultPrinter();
        resultPrinter.printResults(result, dictionaryForCalculation.get("output"));
    }

    /**
     * The values were entered manually because parsing an external file,
     * for instance CSV, did not fit into an 8-hour limit.
     * TreeMap is used to rule out null keys and values.
     * @return A TreeMap of the CO2 emission values for different vehicles.
     * Key - String; The vehicle name from the specification.
     * Value - Integer; The CO2 emission in grams.
     */
    private static TreeMap<String, Integer> GetCO2EmissionsDictionary() {
        TreeMap<String, Integer> loader = new TreeMap<>();

        loader.put("diesel-car-small", 152);
        loader.put("petrol-car-small", 164);
        loader.put("plugin-hybrid-car-small", 83);
        loader.put("electric-car-small", 60);
        loader.put("diesel-car-medium", 181);
        loader.put("petrol-car-medium", 102);
        loader.put("plugin-hybrid-car-medium", 120);
        loader.put("electric-car-medium", 68);
        loader.put("diesel-car-large", 219);
        loader.put("petrol-car-large", 282);
        loader.put("plugin-hybrid-car-large", 136);
        loader.put("electric-car-large", 83);
        loader.put("bus-default", 28);
        loader.put("train-default", 7);

        return loader;
    }
}