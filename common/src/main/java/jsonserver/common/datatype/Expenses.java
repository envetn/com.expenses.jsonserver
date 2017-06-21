package jsonserver.common.datatype;

import java.sql.Date;
import java.util.List;

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

    public Expenses(String cost, String costType, Date buyDate, String comment, String uuid)
    {
        this.myCost = cost;
        this.myCostType = costType;
        this.myBuyDate = buyDate;
        this.myComment = comment;
        this.myUuid = uuid;
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
}
