package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {
    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Order create(Profile profile, ShoppingCart shoppingCart) {
        String sql = """
                INSERT INTO orders ( user_id, date, address, city, state, zip, shipping_amount)
                VALUES (?,?,?,?,?,?,?);
                """;
        try (Connection connection= getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement. RETURN_GENERATED_KEYS);

            statement.setInt(1, profile.getUserId());
            statement.setTimestamp(2,java.sql.Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(3, profile.getAddress());
            statement.setString(4, profile.getCity());
            statement.setString(5, profile.getState());
            statement.setString(6, profile.getZip());
            statement.setBigDecimal(7, shoppingCart.getTotal());
            statement.executeUpdate();

            ResultSet row = statement.getGeneratedKeys();
            if(row.next()){
                int generatedKey =row.getInt(1);
                return getByOrderId(generatedKey);
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Order getByOrderId(int orderId) {
        String sql = """
                SELECT * FROM  Orders WHERE order_id= ?;
                """;
        try(Connection connection= getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                return mapRow(resultSet);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }

    private Order mapRow(ResultSet row) throws SQLException{
        int orderId = row.getInt("order_id");
        int userId = row.getInt("user_id");
        LocalDateTime date= row.getTimestamp("date").toLocalDateTime();
        String address = row.getString("address");
        String city = row.getString("city");
        String state = row.getString("state");
        String zip = row.getString("zip");
        BigDecimal shippingAmount= row.getBigDecimal("shipping_amount");

        Order order = new Order(){{
            setOrderId(orderId);
            setUserId(userId);
            setDate(date);
            setAddress(address);
            setCity(city);
            setState(state);
            setZip(zip);
            setShippingAmount(shippingAmount);
        }};
        return order;

    }
}
