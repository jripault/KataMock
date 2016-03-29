package org.codingdojo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@SuppressWarnings("Duplicates")
@RunWith(MockitoJUnitRunner.class)
public class MagicalNumberPrinterTest {

    @Mock
    private Printer printer;

    @Mock
    private NumberCalculator numberCalculator;

    @InjectMocks
    private NumberPrinter numberPrinter;

    @Test
    public void shouldPrintHundredsResults() {
        // GIVEN
        int limit = 100;
        when(numberCalculator.calculate(anyInt()))
            .thenReturn("0")  // first invocation returns "0"
            .thenReturn("1"); // other invocations return "1"

        // WHEN
        numberPrinter.printNumbers(limit);

        // THEN
        verify(numberCalculator, times(limit)).calculate(anyInt());
        verify(printer, times(1)).print("0");
        verify(printer, times(limit - 1)).print("1");

        verifyNoMoreInteractions(numberCalculator, printer);
    }

    @Test
    public void shouldContinuePrintingOnError() {
        // GIVEN
        when(numberCalculator.calculate(anyInt()))
            .thenReturn("1")
            .thenThrow(new RuntimeException())
            .thenReturn("3");

        doThrow(new RuntimeException()).when(printer).print("3");

        // WHEN
        numberPrinter.printNumbers(3);

        // THEN
        verify(numberCalculator, times(3)).calculate(anyInt());
        verify(printer).print("1");
        verify(printer).print("3");

        verifyNoMoreInteractions(numberCalculator, printer);
    }
}
