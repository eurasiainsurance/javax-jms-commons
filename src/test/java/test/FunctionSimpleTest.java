package test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.validation.ValidationException;

import org.junit.Test;

import ejb.resources.function.runtimeException.FunctionRuntimeExceptionDestination;
import ejb.resources.function.runtimeException.FunctionRuntimeExceptionEntity;
import ejb.resources.function.runtimeException.FunctionRuntimeExceptionResult;
import ejb.resources.function.simple.FunctionSimpleDestination;
import ejb.resources.function.simple.FunctionSimpleEntity;
import ejb.resources.function.simple.FunctionSimpleResult;
import ejb.resources.function.validation.FunctionValidationDestination;
import ejb.resources.function.validation.FunctionValidationEntity;
import ejb.resources.function.validation.FunctionValidationResult;
import tech.lapsa.javax.jms.MyJMSClient;
import tech.lapsa.javax.jms.MyJMSClient.MyJMSFunction;
import test.assertion.Assertions;

public class FunctionSimpleTest extends ArquillianBaseTestCase {

    @Inject
    private MyJMSClient jmsClient;

    @Inject
    private FunctionSimpleDestination functionSimpleDestination;

    @Test
    public void simpleTest_1() throws JMSException {
	final MyJMSFunction<FunctionSimpleEntity, FunctionSimpleResult> function = jmsClient.createFunction(
		functionSimpleDestination.getDestination(),
		FunctionSimpleResult.class);
	{
	    final String MESSAGE = "Hello JMS world!";
	    final FunctionSimpleEntity e = new FunctionSimpleEntity(MESSAGE);
	    final FunctionSimpleResult r = function.apply(e);
	    assertThat(r, not(nullValue()));
	    assertThat(r.message, allOf(not(nullValue()), is(equalTo(FunctionSimpleResult.PREFIX + e.message))));
	}
    }

    @Inject
    private FunctionValidationDestination functionValidationDestination;

    @Test
    public void validationTest_1() throws JMSException {
	final MyJMSFunction<FunctionValidationEntity, FunctionValidationResult> function = jmsClient.createFunction(
		functionValidationDestination.getDestination(),
		FunctionValidationResult.class);

	{
	    final String VALID_MESSAGE = "Hello JMS world!";
	    final FunctionValidationEntity e = new FunctionValidationEntity(VALID_MESSAGE);
	    final FunctionValidationResult r = function.apply(e);
	    assertThat(r, not(nullValue()));
	    assertThat(r.getMessage(), allOf(not(nullValue()), is(equalTo(FunctionSimpleResult.PREFIX + e.getMessage()))));
	}
    }

    @Test
    public void validationTest_2() {
	final MyJMSFunction<FunctionValidationEntity, FunctionValidationResult> function = jmsClient.createFunction(
		functionValidationDestination.getDestination(),
		FunctionValidationResult.class);

	{
	    final String NULL_MESSAGE = null;
	    final FunctionValidationEntity e = new FunctionValidationEntity(NULL_MESSAGE);
	    Assertions.expectException(() -> {
		try {
		    function.apply(e);
		} catch (JMSException e1) {
		    throw new RuntimeException(e1);
		}
	    }, ValidationException.class);
	}
    }

    @Inject
    private FunctionRuntimeExceptionDestination functionRuntimeExceptionDestination;

    @Test
    public void runtimeExceptionTest_1() throws JMSException {
	final MyJMSFunction<FunctionRuntimeExceptionEntity, FunctionRuntimeExceptionResult> function = jmsClient.createFunction(
		functionRuntimeExceptionDestination.getDestination(),
		FunctionRuntimeExceptionResult.class);

	{
	    final String MESSAGE = "Hello JMS world!";
	    final FunctionRuntimeExceptionEntity e = new FunctionRuntimeExceptionEntity(MESSAGE);
	    Assertions.expectException(() -> {
		try {
		    function.apply(e);
		} catch (JMSException e1) {
		    throw new RuntimeException(e1);
		}
	    }, NullPointerException.class);
	}
    }
}
