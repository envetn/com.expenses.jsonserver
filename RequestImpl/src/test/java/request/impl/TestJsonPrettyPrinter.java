package request.impl;

import jsonserver.common.Utils.Utilities;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Created by Foten on 2/25/2017.
 */
public class TestJsonPrettyPrinter
{
    private final static Logger log  = Logger.getLogger(TestJsonPrettyPrinter.class);

    @Test
    public void testPrettyPrinter()
    {
        JsonObject object = new JsonObject();
        JsonObject idObject = new JsonObject();
        idObject.addProperty("userId", "UserId");
        idObject.addProperty("ratingId", "ratingId");
        idObject.addProperty("testId", "testId");

        JsonObject computerObject = new JsonObject();
        computerObject.addProperty("CPU", "i5");
        computerObject.addProperty("HDD1", "1TB");
        computerObject.addProperty("HDD2", "2TB");
        computerObject.addProperty("MotherBoard", "Asus x21x+1");

        object.add("Id", idObject);
        object.add("Computer", computerObject);
        String prettyJsonString = Utilities.getPrettyJsonString(computerObject);

        log.info(prettyJsonString);
    }
}
