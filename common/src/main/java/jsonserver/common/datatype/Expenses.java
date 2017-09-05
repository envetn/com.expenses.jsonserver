package jsonserver.common.datatype;

import java.sql.Date;

/**
 * Created by lofie on 2017-05-21.
 */
public class Expenses
{
    private final String myCost;
    private final String myCostType;
    private final Date myBuyDate;
    private final String myComment;
    private final String myUuid;
    private boolean myExistsInDb;

    public Expenses(String cost, String costType, Date buyDate, String comment, String uuid, boolean existsInDb)
    {
        myCost = cost;
        myCostType = costType;
        myBuyDate = buyDate;
        myComment = comment;
        myUuid = uuid;
        myExistsInDb = existsInDb;
    }

    public String getCost()
    {
        return myCost;
    }

    public String getCostType()
    {
        return myCostType;
    }

    public Date getBuyDate()
    {
        return myBuyDate;
    }

    public String getComment()
    {
        return myComment;
    }

    public String getUuid()
    {
        return myUuid;
    }

    public boolean existsInDb()
    {
        return myExistsInDb;
    }
}
