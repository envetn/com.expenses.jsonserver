package Connection.internal;

import jsonserver.common.containers.TemperatureContainer;
import jsonserver.common.containers.UserContainer;
import jsonserver.common.datatype.Temperature;
import jsonserver.common.view.Request;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * asdasd
 */
public class TemperatureConnection
{
    private static final Logger LOGGER = Logger.getLogger(TemperatureConnection.class);
    private final Connection myConnection;
    private final Statement myStatement;
    private Date myDate;

    public TemperatureConnection(Connection connect, Date date, Statement statement)
    {
        myConnection = connect;
        myDate = date;
        myStatement = statement;
    }

    public int updateTemperature(UserContainer container) throws SQLException
    {
        int sqlResult = 0;
        TemperatureContainer temperatureContainer = container.getTemperatureContainer();
        Temperature temperatureToUpdate = temperatureContainer.getSpeculatedTemperature();
        if (temperatureToUpdate != null)
        {
            sqlResult = insertTemperatureValues(temperatureToUpdate);
            boolean success = sqlResult != 0;
            LOGGER.info("Trying to update Temperature. Result:  " + success);
        }
        else
        {
            LOGGER.info("No temperature to update");
        }
        return sqlResult;
    }

    public List<Temperature> readTemperatureValue(Request request) throws SQLException
    {
        //Todo: only read temperature connected to username
        String query = "SELECT * FROM test.temperature";

        PreparedStatement preparedStatement = myConnection.prepareStatement(query);

        LOGGER.info("Statement: " + preparedStatement.toString());
        ResultSet resultSet = preparedStatement.executeQuery();

        List<Temperature> listOfTemperature = new ArrayList<>();

        while (resultSet.next())
        {
            Date date = Date.valueOf(resultSet.getString("date"));
            String unsortedTemperature = resultSet.getString("temperatur");
            String unsortedTimeStamp = resultSet.getString("time");
            Temperature temperature = new Temperature(date, unsortedTemperature, unsortedTimeStamp, false);

            listOfTemperature.add(temperature);
        }
        return listOfTemperature;
    }

    private int insertTemperatureValues(Temperature temperature) throws SQLException
    {
        if (myDate == null)
        {
            fetchLastTemperatureDate();
        }

        Date incomingDate = temperature.getDate();

        PreparedStatement preparedStatement;
        if (myDate.toString().equals(incomingDate.toString()))
        {
            preparedStatement = appendData(temperature);
        }
        else
        {
            myDate = incomingDate;
            preparedStatement = insertData(temperature);
        }
        LOGGER.info("Statement: " + preparedStatement.toString());
        return preparedStatement.executeUpdate();
    }

    private void fetchLastTemperatureDate() throws SQLException
    {
        ResultSet resultSet = myStatement.executeQuery("SELECT id, date FROM test.temperature ORDER BY id DESC LIMIT 1 ");
        if (resultSet.next())
        {
            myDate = resultSet.getDate("date");
        }
    }

    private PreparedStatement appendData(Temperature temperature) throws SQLException
    {
        String time = temperature.getTime();
        String temp = temperature.getTemperature();
        Date temperatureDate = temperature.getDate();

        String sql = "UPDATE test.temperature SET time=CONCAT(time,?), temperatur=CONCAT(temperatur,?)  WHERE date=?  LIMIT 1";
        PreparedStatement preparedStatement = myConnection.prepareStatement(sql);

        preparedStatement.setString(1, time + "@");
        preparedStatement.setString(2, temp + "@");
        preparedStatement.setDate(3, temperatureDate);

        return preparedStatement;
    }

    private PreparedStatement insertData(Temperature temperature) throws SQLException
    {
        String time = temperature.getTime();
        String temp = temperature.getTemperature();
        Date temperatureDate = temperature.getDate();
        String sql = "INSERT INTO test.temperature (date, temperatur, time) VALUES (?,?,?)";
        PreparedStatement preparedStatement = myConnection.prepareStatement(sql);

        preparedStatement.setDate(1, temperatureDate);
        preparedStatement.setString(2, temp + "@");
        preparedStatement.setString(3, time + "@");

        return preparedStatement;
    }
}
