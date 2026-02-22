package de.smarthome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import de.smarthome.command.CommandHandler;
import org.springframework.boot.CommandLineRunner;
import java.util.Scanner;
import de.smarthome.command.PersonArrivedCommand;
import de.smarthome.command.PersonLeftCommand;
import de.smarthome.consumer.QueryService;


@SpringBootApplication
public class SmarthomeApplication implements CommandLineRunner {

    private CommandHandler commandHandler;
    private QueryService queryService;

    public SmarthomeApplication(CommandHandler commandHandler, QueryService queryService) {
        this.commandHandler = commandHandler;
        this.queryService = queryService;
    }

    public static void main(String[] args) {
        SpringApplication.run(SmarthomeApplication.class, args);
    }

    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("What happens in your smart home? (e.g. 'Alice arrives' or 'Bob leaves') Type 'exit' to quit.");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                scanner.close();
                System.exit(0);
                break;
            }
            if (input.equalsIgnoreCase("status")) {
                System.out.println(queryService.getStatus());
                continue;
            }

            processInput(input);
        }
        scanner.close();
    }

	//Methode um das Input zu verarbeiten und die passenden Commands zu erstellen
    private void processInput(String input) {
        String[] parts = input.split(" ");
        if (parts.length != 2) {
            System.out.println("Invalid input. Please use format: 'Name arrives' or 'Name leaves'");
            return;
        }

        String person = parts[0];
        String action = parts[1];

        if (action.equalsIgnoreCase("arrives")) {
            commandHandler.handlePersonArrivedCommand(new PersonArrivedCommand(person));
        } else if (action.equalsIgnoreCase("leaves")) {
            commandHandler.handlePersonLeftCommand(new PersonLeftCommand(person));
        } else {
            System.out.println("Unknown action: " + action);
        }
    }
}