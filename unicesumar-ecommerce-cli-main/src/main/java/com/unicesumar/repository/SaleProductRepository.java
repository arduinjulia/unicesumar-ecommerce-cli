package com.unicesumar.repository;

import com.unicesumar.entities.SaleProduct;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SaleProductRepository implements EntityRepository<SaleProduct>{
    private final Connection connection;

    public SaleProductRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(SaleProduct entity) {
        String query = "INSERT INTO sale_products (sale_id, product_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, entity.getSaleId().toString());
            stmt.setString(2, entity.getProductId().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SaleProduct> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<SaleProduct> findAll() {
        String query = "SELECT * FROM sale_products";
        List<SaleProduct> saleProducts = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UUID saleId = UUID.fromString(rs.getString("sale_id"));
                UUID productId = UUID.fromString(rs.getString("product_id"));
                saleProducts.add(new SaleProduct(saleId, productId));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return saleProducts;
    }

    @Override
    public void deleteById(UUID id) {

    }

    public List<UUID> findProductIdsBySaleId(UUID saleId) {
        List<UUID> productIds = new ArrayList<>();
        String query = "SELECT productId FROM sale_products WHERE saleId = ?";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(query);
            stmt.setString(1, saleId.toString());
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                productIds.add(UUID.fromString(resultSet.getString("productId")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return productIds;
    }

}
