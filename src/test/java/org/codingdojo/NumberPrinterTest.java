package org.codingdojo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NumberPrinterTest {

    @Mock
    private Printer printer;

    @Mock
    private NumberCalculator numberCalculator;

    @InjectMocks
    private NumberPrinter numberPrinter;

    @Test
    public void printsCalculatorResultsHundredTimes() {
        // arrange
        int limit = 100;
        when(numberCalculator.calculate(anyInt()))
            .thenReturn("0")  // first invocation returns "0"
            .thenReturn("1"); // other invocations return "1"
        // act
        numberPrinter.printNumbers(limit);
        // assert
        verify(numberCalculator, times(limit)).calculate(anyInt());
        verify(printer, times(1)).print("0");
        verify(printer, times(limit - 1)).print("1");
        verifyNoMoreInteractions(numberCalculator, printer);
    }

    @Test
    public void continuesOnCalculatorOrPrinterError() {
        // arrange
        when(numberCalculator.calculate(anyInt()))
            .thenReturn("1")
            .thenThrow(new RuntimeException())
            .thenReturn("3");
        // stub the void method with an exception
        doThrow(new RuntimeException()).when(printer).print("3");
        // act
        numberPrinter.printNumbers(3);
        // assert
        verify(numberCalculator, times(3)).calculate(anyInt());
        verify(printer).print("1");
        verify(printer).print("3");

        verifyNoMoreInteractions(numberCalculator, printer);
    }
}
