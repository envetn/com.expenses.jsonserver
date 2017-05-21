package jsonserver.common.view;


import jsonserver.common.datatype.Limit;
import jsonserver.common.datatype.Order;

/**
 * Created by olof on 2016-07-09.
 */
public interface GetRequest extends Request
{
    Order getOrder();

    Limit getLimit();
}
