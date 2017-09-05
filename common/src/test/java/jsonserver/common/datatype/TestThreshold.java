package jsonserver.common.datatype;

import jsonserver.common.view.Expense.ExpensePutRequest;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by lofie on 2017-07-22.
 */
public class TestThreshold
{

    @Before
    public void init()
    {
    }

    @Test
    public void testFindValidThreshold()
    {
        Threshold threshold = new Threshold(200, 100, 4, "bill", "lofie");

        Expenses expensemock = mock(Expenses.class);
        when(expensemock.getBuyDate()).thenReturn(Date.valueOf("2017-05-02"));
        when(expensemock.getCostType()).thenReturn("bill");

        boolean valid = threshold.isCorrectThreshold("lofie", expensemock);
        assertThat(valid).isTrue();
    }
}
