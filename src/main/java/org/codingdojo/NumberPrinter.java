package org.codingdojo;

/**
 * User: JRI <julien.ripault@atos.net>
 * Date: 29/03/2016
 */
public class NumberPrinter {

    private NumberCalculator numberCalculator;
    private Printer printer;

    public NumberPrinter(NumberCalculator numberCalculator, Printer printer) {
        this.numberCalculator = numberCalculator;
        this.printer = printer;
    }

    public void printNumbers(int limit) {
        if (limit < 1) {
            throw new RuntimeException("limit must be >= 1");
        }
        for (int i = 1; i <= limit; i++) {
            try {
                printer.print(numberCalculator.calculate(i));
            } catch (Exception e) {
                // noop
            }
        }
    }
}
