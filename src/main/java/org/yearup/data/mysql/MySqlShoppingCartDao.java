package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        String sql= """
                SELECT s.quantity, p.* From shopping_cart as s
                Join products as p On s.product_id = p.product_id
                WHERE s.user_id= ?;
                """;
        ShoppingCart shoppingCart = new ShoppingCart();
        try (Connection connection = getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                ShoppingCartItem shoppingCartItem = mapRow(resultSet);
                shoppingCart.add(shoppingCartItem);
            }
            return shoppingCart;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public ShoppingCart addProductToCart(int userId, int productId) {
        // if product exists, update quantity
        // if it doesn't add it to database
        ShoppingCart shoppingCart= getByUserId(userId);
        int quantity = 1;
            if(shoppingCart.contains(productId)) {
                quantity = shoppingCart.get(productId).getQuantity() + 1;
                shoppingCart.get(productId).setQuantity(quantity);
                updateProductQuantity(productId, userId,quantity);
            }else {
                String insertSql= """
                INSERT INTO shopping_cart (quantity, user_id, product_id)
                VALUES (?,?,?);
                """;

                try (Connection connection = getConnection()) {
                    PreparedStatement statement = connection.prepareStatement(insertSql);
                    statement.setInt(1, quantity);
                    statement.setInt(2, userId);
                    statement.setInt(3, productId);
                    statement.executeUpdate();

                    return getByUserId(userId);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return getByUserId(userId);
    }

    @Override
    public void removeProductFromCart(int productId, int userId) {

    }

    @Override
    public void updateProductQuantity(int productId, int userId, int quantity) {
        String updateSql = """
            UPDATE shopping_cart
            SET quantity = ?
            WHERE user_id = ? AND product_id = ?;
            """;

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setInt(1, quantity);
            statement.setInt(2, userId);
            statement.setInt(3, productId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearCart(int userId) {
        String sql= """
                DELETE FROM shopping_cart WHERE user_id = ?
                """;

        try(Connection connection= getConnection()) {
            PreparedStatement statement= connection.prepareStatement(sql);
            statement.setInt(1,userId);
            statement.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    protected static ShoppingCartItem mapRow(ResultSet row) throws SQLException
    {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();

        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String color = row.getString("color");
        int stock = row.getInt("stock");
        boolean isFeatured = row.getBoolean("featured");
        String imageUrl = row.getString("image_url");
        int quantity = row.getInt("quantity");

        Product product= new Product(productId, name, price, categoryId, description, color, stock, isFeatured, imageUrl);
        shoppingCartItem.setProduct(product);
        shoppingCartItem.setQuantity(quantity);

        return shoppingCartItem;
    }
}
