package org.example;

import org.example.config.LiquibaseMigration;
import org.example.context.ApplicationContext;
import org.example.controller.ViewingConsole;

public class ProductCatalogApplication {

    public static void main(String[] args) {

        LiquibaseMigration liquibaseMigration  = ApplicationContext.getInstance().getBean(LiquibaseMigration.class);
        liquibaseMigration.runMigration();

        ViewingConsole viewingConsole = ApplicationContext.getInstance().getBean(ViewingConsole.class);
        viewingConsole.start();
    }
}