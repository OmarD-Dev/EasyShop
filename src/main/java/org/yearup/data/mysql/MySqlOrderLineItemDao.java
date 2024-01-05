package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderLineItemDao;
import org.yearup.models.Order;
import org.yearup.models.OrderLineItem;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlOrderLineItemDao extends MySqlDaoBase implements OrderLineItemDao {
    public MySqlOrderLineItemDao(DataSource dataSource) {
        super(dataSource);
    }
    @Override
    public List<OrderLineItem> getByOrderId(int orderId) {
        String sql = """
                SELECT * FROM order_line_items WHERE order_id = ?;
                """;
        List<OrderLineItem> orderLineItems = new ArrayList<>();

        try (Connection connection= getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1,orderId);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                orderLineItems.add(mapRow(resultSet));
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return orderLineItems;
    }

    @Override
    public void create(Order order, ShoppingCartItem shoppingCartItem) {
        String sql = """
                INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount)
                          VALUES (?, ?, ?, ?, ?);
                                """;

                        try (Connection connection = getConnection()) {
                            PreparedStatement statement = connection.prepareStatement(sql);
                            statement.setInt(1, order.getOrderId());
                            statement.setInt(2, shoppingCartItem.getProductId());
                            statement.setBigDecimal(3, shoppingCartItem.getLineTotal());
                            statement.setInt(4, shoppingCartItem.getQuantity());
                            statement.setBigDecimal(5, shoppingCartItem.getDiscountPercent());
                            statement.executeUpdate();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
    }

    private OrderLineItem mapRow(ResultSet row) throws SQLException{

        int orderLineItemId = row.getInt("order_line_item_id");
        int orderId = row.getInt("order_id");
        int productId = row.getInt("product_id");
        BigDecimal salesPrice = row.getBigDecimal("sales_price");
        int quantity = row.getInt("quantity");
        BigDecimal discount = row.getBigDecimal("discount");

        OrderLineItem orderLineItem = new OrderLineItem(){{
            setOrderLineItemId(orderLineItemId);
            setOrderId(orderId);
            setProductId(productId);
            setSalesPrice(salesPrice);
            setQuantity(quantity);
            setDiscount(discount);
        }};
        return orderLineItem;

    };

}
