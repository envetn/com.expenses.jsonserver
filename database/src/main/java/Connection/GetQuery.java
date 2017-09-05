package Connection;

import jsonserver.common.datatype.ExpenseUser;
import jsonserver.common.datatype.Fetchperiod;
import jsonserver.common.datatype.Limit;
import jsonserver.common.datatype.Order;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static jsonserver.common.datatype.Fetchperiod.ALL;
import static jsonserver.common.datatype.Fetchperiod.DAY;
import static jsonserver.common.datatype.Fetchperiod.TIME_PERIOD;

/**
 * Created by eolochr on 12/24/16.
 */
public class GetQuery
{
    private final static Logger log  = Logger.getLogger(GetQuery.class);
    private final ThreadLocal<Order> myOrder;
    private final String myDatabase;
    private final ExpenseUser myUser;
    private final Limit myLimit;
    private final Connection myConnection;
    private PreparedStatement myStatement;
    private final String myId;

    private GetQuery(QueryBuilder builder) throws SQLException
    {
        myOrder = ThreadLocal.withInitial(() -> builder.myOrder);
        myUser = builder.myUser;
        myConnection = builder.myConnect;
        myDatabase = builder.myDatabase;
        myLimit = builder.myLimit;
        myId = builder.id;

        generate();
    }

    public PreparedStatement getStatement()
    {
        return myStatement;
    }

    private void generate() throws SQLException
    {
        String sqlString = "SELECT * FROM %s ";
        Integer pos = 0;
        final Fetchperiod fetchPeriod = myLimit.getFetchperiod();
        if (fetchPeriod.hasPeriod())
        {
            final String period = fetchPeriod.getPeriod();

            switch (period)
            {
                case ALL:
                    if (myId.equals("Expenses"))
                    {
                        sqlString += "WHERE username=? ORDER BY ? ";

                    }
                    else
                    {
                        sqlString += " ORDER BY ? ";
                    }
                    sqlString = String.format(sqlString, myDatabase);
                    myStatement = myConnection.prepareStatement(sqlString);

                    break;
                case DAY:
                    if (myId.equals("Expenses"))
                    {
                        sqlString += "WHERE %s=? AND username=? ORDER BY ? ";
                    }
                    else
                    {
                        sqlString += "WHERE %s=? ORDER BY ?";
                    }

                    sqlString = String.format(sqlString, myDatabase, myOrder.get()
                            .getOrderBy());
                    myStatement = myConnection.prepareStatement(sqlString);
                    myStatement.setString(++pos, fetchPeriod.getPeriodToFetch());
                    break;

                case TIME_PERIOD:

                    if (myId.equals("Expenses"))
                    {
                        sqlString += "WHERE %s BETWEEN ? AND ? AND username=? ORDER BY ? ";
                    }
                    else
                    {
                        sqlString += "WHERE %s BETWEEN ? AND ? ORDER BY ?";
                    }

                    sqlString = String.format(sqlString, myDatabase, myOrder.get()
                            .getOrderBy());
                    myStatement = myConnection.prepareStatement(sqlString);
                    myStatement.setString(++pos, fetchPeriod.getTimeStart());
                    myStatement.setString(++pos,  fetchPeriod.getTimeEnd());

                    break;
                default:
                    throw new SQLException("Did not find period to find");
            }
        }

        if(myUser != null && myId.equals("Expenses"))
        {
            myStatement.setString(++pos, myUser.getUsername());
        }
        myStatement.setString(++pos, myOrder.get()
                .getWhichOrder());
    }

    public static QueryBuilder getBuilder()
    {
        return new QueryBuilder();
    }

    public static class QueryBuilder
    {
        private Order myOrder;
        private ExpenseUser myUser;
        private Connection myConnect;
        private String myDatabase;
        private Limit myLimit;
        private String id;

        public QueryBuilder setDatabase(String database)
        {
            myDatabase = database;
            return this;
        }

        public QueryBuilder setOrder(Order order)
        {
            myOrder = order;
            return this;
        }

        public QueryBuilder setUser(ExpenseUser user)
        {
            myUser = user;
            return this;
        }

        public QueryBuilder setLimit(Limit limit)
        {
            myLimit = limit;
            return this;
        }

        public QueryBuilder setConnect(Connection connect)
        {
            this.myConnect = connect;
            return this;
        }

        public QueryBuilder setId(String id)
        {
            this.id = id;
            return this;
        }


        public GetQuery createQuery() throws SQLException
        {
            return new GetQuery(this);
        }

    }
}
