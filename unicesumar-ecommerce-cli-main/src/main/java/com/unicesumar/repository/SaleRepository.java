package com.unicesumar.repository;

import com.unicesumar.entities.Sale;
import com.unicesumar.entities.SaleProduct;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SaleRepository implements EntityRepository<Sale>{
    private final Connection connection;
    private SaleProductRepository saleProductRepository;

    public SaleRepository(Connection connection) {
        this.connection = connection;
        this.saleProductRepository = new SaleProductRepository(connection);
    }

    @Override
    public void save(Sale entity) {
        String query = "INSERT INTO sales VALUES (?, ?, ?, ?)";
        try {
            PreparedStatement stmt = this.connection.prepareStatement(query);
            stmt.setString(1, entity.getUuid().toString());
            stmt.setString(2, entity.getUserId().toString());
            stmt.setString(3, entity.getPaymentMethod());
            stmt.setDate(4, new java.sql.Date(entity.getSale_date().getTime()));
            stmt.executeUpdate();
            for (UUID productId : entity.getProducts()) {
                saleProductRepository.save(new SaleProduct(entity.getUuid(), productId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Sale> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Sale> findAll() {
        String query = "SELECT * FROM sales";
        ArrayList<Sale> sales = new ArrayList<>();

        try {
            PreparedStatement stmt = this.connection.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                UUID saleId = UUID.fromString(resultSet.getString("uuid"));
                sales.add(new Sale(
                        saleId,  // UUID da venda
                        resultSet.getString("paymentMethod"),
                        UUID.fromString(resultSet.getString("userId")),
                        new Date(resultSet.getDate("sale_date").getTime()),
                        saleProductRepository.findProductIdsBySaleId(saleId)
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return sales;
    }

    @Override
    public void deleteById(UUID id) {

    }
}
