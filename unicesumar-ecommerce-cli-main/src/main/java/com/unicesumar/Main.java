package com.unicesumar;

import com.unicesumar.entities.Product;
import com.unicesumar.entities.Sale;
import com.unicesumar.entities.User;
import com.unicesumar.paymentMethods.PaymentMethod;
import com.unicesumar.paymentMethods.PaymentType;
import com.unicesumar.repository.ProductRepository;
import com.unicesumar.repository.SaleRepository;
import com.unicesumar.repository.UserRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        ProductRepository listaDeProdutos = null;
        UserRepository listaDeUsuarios = null;
        SaleRepository listaDeVendas = null;

        Connection conn = null;
        
        // Parâmetros de conexão
        String url = "jdbc:sqlite:database.sqlite";

        // Tentativa de conexão
        try {
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                listaDeProdutos = new ProductRepository(conn);
                listaDeUsuarios = new UserRepository(conn);
                listaDeVendas = new SaleRepository(conn);
            } else {
                System.out.println("Falha na conexão.");
                System.exit(1);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            System.exit(1);
        }

        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("\n---MENU---");
            System.out.println("1 - Cadastrar Produto");
            System.out.println("2 - Listas Produtos");
            System.out.println("3 - Cadastrar Usuário");
            System.out.println("4 - Listar Usuários");
            System.out.println("5 - Realizar venda");
            System.out.println("6 - Sair");
            System.out.println("Escolha uma opção: ");
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1:
                    System.out.println("Cadastrar Produto");
                    listaDeProdutos.save(new Product("Teste", 10));
                    listaDeProdutos.save(new Product("Computador", 3000));
                    break;
                case 2:
                    System.out.println("Listar Produtos");
                    List<Product> products = listaDeProdutos.findAll();
                    products.forEach(System.out::println);
                    break;
                case 3:
                    System.out.println("Cadastrar Usuário");
                    listaDeUsuarios.save(new User("Júlia Arduin", "julia@example", "1234"));
                    break;
                case 4:
                    System.out.println("Listar Usuários");
                    List<User> users = listaDeUsuarios.findAll();
                    users.forEach(System.out::println);
                    break;
                case 5:
                    System.out.println("Digite o Email do usuário: ");
                    String email = scanner.nextLine();

                    Optional<User> userOptional = listaDeUsuarios.findByEmail(email);

                    if(userOptional.isPresent()){
                        User user = userOptional.get();
                        System.out.println("Usuário encontrado: " + user.getName());

                        System.out.println("Digite os IDs dos produtos (separados por vírgula): ");
                        String idsInput = scanner.nextLine();
                        String[] idStrings = idsInput.split(",");

                        List<UUID> productsIds = new ArrayList<>();
                        double totalValor = 0;

                        System.out.println("Produtos encontrado:");
                        for (String idStr : idStrings) {
                            try {
                                UUID id = UUID.fromString(idStr.trim());
                                Optional<Product> productOptional = listaDeProdutos.findById(id);

                                if (productOptional.isPresent()) {
                                    Product product = productOptional.get();
                                    System.out.println("- " + product.getName() + " | Preço: R$ " + product.getPrice());
                                    productsIds.add(product.getUuid());
                                    totalValor += product.getPrice();
                                } else {
                                    System.out.println("- Produto com ID " + idStr.trim() + " não encontrado.");
                                }
                            } catch (IllegalArgumentException e) {
                                System.out.println("- ID inválido: " + idStr.trim());
                            }
                        }

                        if(productsIds.isEmpty()){
                            System.out.println("Não foi encontrado nenhum produto válido.");
                        } else{
                            System.out.println("Escolha a forma de pagamento: ");
                            System.out.println("1 - Cartão de crédito");
                            System.out.println("2 - Boleto");
                            System.out.println("3 - Pix");
                            int escolha = scanner.nextInt();
                            scanner.nextLine();

                            PaymentType tipoSelecionado = null;

                            switch (escolha) {
                                case 1:
                                    tipoSelecionado = PaymentType.CARTAO;
                                    break;
                                case 2:
                                    tipoSelecionado = PaymentType.BOLETO;
                                    break;
                                case 3:
                                    tipoSelecionado = PaymentType.PIX;
                                    break;
                                default:
                                    System.out.println("Opção inválida. Tente novamente");
                            }

                            try {
                                // Criando a estratégia de pagamento via Factory Method
                                PaymentMethod paymentMethod = PaymentMethodFactory.create(tipoSelecionado);

                                // Processando o pagamento utilizando a estratégia escolhida
                                PaymentManager Manager = new PaymentManager(paymentMethod);
                                Manager.pay(totalValor);
                                listaDeVendas.save(new Sale(user.getUuid(), tipoSelecionado.name(), new Date(), productsIds));

                                System.out.println("Resumo da venda: ");
                                System.out.println("Cliente: " + user.getName());
                                System.out.println("Produtos:");

                                for (UUID productId : productsIds) {
                                    Optional<Product> productOptional = listaDeProdutos.findById(productId);
                                    if (productOptional.isPresent()) {
                                        Product product = productOptional.get();
                                        System.out.printf("- Produto: %s, Preço: R$ %.2f\n", product.getName(), product.getPrice());
                                    }
                                }

                                System.out.printf("Valor total: R$ %.2f\n", totalValor);
                                System.out.println("Pagamento: " + tipoSelecionado.name());
                                System.out.println("Venda registra da com sucesso!");

                            } catch (IllegalArgumentException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    } else {
                        System.out.println("Usuário não encontrado.");
                    }
                case 6:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente");
                    ;
            }

        } while (option != 6);


        scanner.close();
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
