package org.example;

import org.example.config.LiquibaseMigration;
import org.example.controller.ViewingConsole;

public class Main {

    public static void main(String[] args) {

        LiquibaseMigration.runMigration();

        ViewingConsole viewingConsole = new ViewingConsole();
        viewingConsole.start();
    }


}