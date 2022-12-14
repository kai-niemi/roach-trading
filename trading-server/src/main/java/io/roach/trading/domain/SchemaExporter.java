package io.roach.trading.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.EnumSet;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.PostgreSQL10Dialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

import io.roach.trading.domain.account.Account;
import io.roach.trading.domain.order.BookingOrder;
import io.roach.trading.domain.order.BookingOrderItem;
import io.roach.trading.domain.portfolio.Portfolio;
import io.roach.trading.domain.portfolio.PortfolioItem;
import io.roach.trading.domain.product.Product;
import io.roach.trading.util.CockroachDBDialect;

public class SchemaExporter {
    public static Class[] ENTITY_TYPES = {
            Account.class,
            Product.class,
            Portfolio.class,
            PortfolioItem.class,
            BookingOrder.class,
            BookingOrderItem.class
    };

    private SchemaExporter() {
    }

    public static void main(String[] args) throws IOException {
        boolean drop = true;
        Path outFile = null;
        String delimiter = ";";
        Class dialect = CockroachDBDialect.class;

        for (String arg : args) {
            if (arg.startsWith("--")) {
                if ("--drop".equals(arg)) {
                    drop = true;
                } else if (arg.startsWith("--output=")) {
                    outFile = Paths.get(arg.substring(9));
                } else if (arg.startsWith("--delimiter=")) {
                    delimiter = arg.substring(12);
                } else if (arg.equalsIgnoreCase("--psql")) {
                    dialect = PostgreSQL10Dialect.class;
                }
            } else {
                try {
                    dialect = Class.forName(arg);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        if (outFile == null) {
            outFile = Files.createTempFile("trading", "schema");
        }

        ServiceRegistry standardRegistry =
                new StandardServiceRegistryBuilder()
                        .applySetting("hibernate.dialect", dialect.getName())
                        .applySetting("hibernate.physical_naming_strategy",
                                "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy")
                        .build();

        MetadataSources metadataSources = new MetadataSources(standardRegistry);

        Arrays.stream(ENTITY_TYPES).sequential().forEach(clazz -> metadataSources.addAnnotatedClass(clazz));

        MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();
        metadataBuilder.applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE);
        Metadata metadata = metadataBuilder.build();

        // Exporter doesnt seem to export if file exists
        if (outFile.toFile().isFile() && !outFile.toFile().delete()) {
            System.err.println("WARN: Unable to delete file " + outFile);
        }

        SchemaExport schemaExport = new SchemaExport()
                .setOutputFile(outFile.toString())
                .setFormat(true)
                .setHaltOnError(true)
                .setDelimiter(delimiter);
        if (drop) {
            schemaExport.create(EnumSet.of(TargetType.SCRIPT), metadata);
        } else {
            schemaExport.createOnly(EnumSet.of(TargetType.SCRIPT), metadata);
        }

        Files.copy(outFile, System.out);
    }
}
