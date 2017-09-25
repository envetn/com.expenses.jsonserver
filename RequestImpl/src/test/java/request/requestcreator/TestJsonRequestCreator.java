package request.requestcreator;

import com.google.gson.JsonObject;
import jsonserver.common.view.DeleteRequest;
import jsonserver.common.view.Expense.ExpensePutRequest;
import jsonserver.common.view.GetRequest;
import jsonserver.common.view.Request;
import jsonserver.common.view.Temperature.TemperaturePutRequest;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import request.impl.*;

import java.io.IOException;

import static junitparams.JUnitParamsRunner.$;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Created by Foten on 4/23/2017.
 */
@RunWith (JUnitParamsRunner.class)
public class TestJsonRequestCreator
{
    private static final String USER_PUT = "User-Put";
    private static final String EXPENSES_PUT = "Expenses-Put";
    private static final String EXPENSES_GET = "Expenses-Get";
    private static final String EXPENSES_DELETE = "Expenses-Delete";
    private static final String TEMPERATURE_PUT = "Temperature-Put";
    private static final String TEMPERATURE_GET = "Temperature-Get";
    private static final String TEMPERATURE_DELETE = "Temperature-Delete";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Parameters
    public Object[] validActions()
    {
        return $(
                $(USER_PUT, CreateUserRequest.class),
                $(EXPENSES_PUT, ExpensePutRequest.class),
                $(EXPENSES_GET, GetRequest.class),
                $(EXPENSES_DELETE, DeleteRequest.class),
                $(TEMPERATURE_PUT, TemperaturePutRequest.class),
                $(TEMPERATURE_GET, GetRequest.class)
        );
    }

    @Parameters
    public Object[] invalidActions()
    {
        return $($(TEMPERATURE_DELETE)
        );
    }

    @Test
    @Parameters (method = "validActions")
    public void testFetch(String type, Class clazz) throws IOException
    {
        JsonRequestCreator requestCreatorByName = JsonRequestCreator.getRequestCreatorByName(type);
        Request request = requestCreatorByName.generateRequest(getJsonString());
        assertThat(request).isInstanceOf(clazz);
    }

    @Test
    @Parameters (method = "invalidActions")
    public void testNotAbleToGenerateNotSupportedAction(String invalidAction) throws IOException
    {
        expectedException.expect(IllegalArgumentException.class);
        JsonRequestCreator requestCreatorByName = JsonRequestCreator.getRequestCreatorByName(invalidAction);
        requestCreatorByName.generateRequest(getJsonString());
    }

    @Test
    @Parameters (method = "invalidActions")
    public void testNotAbleToExecuteNotSupportedAction(String invalidAction) throws IOException
    {
        Request request = mock(Request.class);
        expectedException.expect(IllegalArgumentException.class);
        JsonRequestCreator requestCreatorByName = JsonRequestCreator.getRequestCreatorByName(invalidAction);
        requestCreatorByName.executeRequest(request);
    }

    @Test
    public void testUnknownType() throws IOException
    {
        JsonRequestCreator requestCreatorByName = JsonRequestCreator.getRequestCreatorByName("invalid");
        Request request = requestCreatorByName.generateRequest(getJsonString());

        JsonObject jsonObject = requestCreatorByName.executeRequest(request);

        assertThat(jsonObject)
                .as("JsonResponse from unknown operation type should not be null")
                .isNotNull();
        assertThat(request).isInstanceOf(UnknownOperationRequest.class);
    }

    private String getJsonString()
    {
        return "{" +
                " \"requestDate\": \"2016-02-01\"," +
                " \"requestType\": \"Put\"," +
                " \"id\": {" +
                "     \"requestId\": \"test\"" +
                " }" +
                "}";
    }
}
