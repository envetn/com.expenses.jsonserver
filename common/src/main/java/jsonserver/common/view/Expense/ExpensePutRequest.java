package jsonserver.common.view.Expense;

import jsonserver.common.datatype.Content;
import jsonserver.common.datatype.ExpenseUser;

import jsonserver.common.view.Request;
/**
 * Created by olof on 2016-07-09.
 */
public interface ExpensePutRequest extends Request
{
    Content getContent();

    ExpenseUser getUser();
}
