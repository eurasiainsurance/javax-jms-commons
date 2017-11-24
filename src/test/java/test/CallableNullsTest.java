package test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import javax.inject.Inject;
import javax.jms.JMSException;

import org.junit.Test;

import ejb.resources.callable.nulls.CallableNullsDestination;
import ejb.resources.callable.nulls.CallableNullsEntity;
import ejb.resources.callable.nulls.CallableNullsResult;
import tech.lapsa.javax.jms.JmsClientFactory;
import tech.lapsa.javax.jms.JmsClientFactory.JmsCallable;

public class CallableNullsTest extends ArquillianBaseTestCase {

    @Inject
    private JmsClientFactory jmsClientFactory;

    @Inject
    private CallableNullsDestination destination;

    @Test
    public void basic() throws JMSException {
	final JmsCallable<CallableNullsEntity, CallableNullsResult> callable //
		= jmsClientFactory.createCallable(destination.getDestination(), CallableNullsResult.class);
	{
	    final CallableNullsResult r = callable.call(null);
	    assertThat(r, nullValue());
	}
    }
}
