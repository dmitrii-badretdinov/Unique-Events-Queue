package org.company;

public class ResultPrinter {
    protected void printResults(Integer valueInGrams, String unitOfMeasurement) {
        Double valueToPrint = valueInGrams.doubleValue();
        String unitToPrint = "kg";
        if(unitOfMeasurement == "kg" || valueInGrams > 1000) {
            valueToPrint /= 1000.0;
        }
        else {
            unitToPrint = "g";
        }
        System.out.println("Your trip caused " + String.format("%,.2f", valueToPrint)
                + unitToPrint + " of CO2-equivalent.");
    }
}
