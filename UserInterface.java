package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UserInterface {
    private final Map<String, String> card;
    private final Map<String, Integer> cardMistakes;
    private final Scanner scanner;
    private StringBuilder logMessages;
    private final String importFile;
    private final String exportFile;

    public UserInterface(Scanner scanner, String importFile, String exportFile) {
        this.scanner = scanner;
        this.card = new LinkedHashMap<>();
        this.cardMistakes = new HashMap<>();
        this.logMessages = new StringBuilder();
        this.importFile = importFile;
        this.exportFile = exportFile;
    }

    public void start() {
        this.importCardsFromFile(this.importFile);

        while (true) {
            this.printMessage("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            String userAction = this.scanner.nextLine();
            this.addLogMessage("> " + userAction);

            if (userAction.equalsIgnoreCase("exit")) {
                this.exit();
                break;
            }

            this.processUserAction(userAction);
        }
    }

    private void processUserAction(String action) {
        switch (action.toLowerCase()) {
            case "add" -> {
                Card cd = this.getCard();
                if (cd != null) {
                    this.card.put(cd.term(), cd.definition());
                    this.printMessage("The pair (\"" + cd.term() + "\":\"" + cd.definition() + "\") has been added.");
                }
            }
            case "remove" -> this.removeCard();
            case "import" -> this.importCardsFromFile();
            case "export" -> this.exportCardsToFile();
            case "ask" -> this.askCards();
            case "log" -> this.createLogsFile();
            case "hardest card" -> this.displayHardestCard();
            case "reset stats" -> this.resetStats();
            default -> this.printMessage("Invalid action");
        }
    }

    private void exit() {
        this.printMessage("Bye bye!");
        this.exportCardsToFile(this.exportFile);
    }

    private Card getCard() {
        this.printMessage("The card:");
        String term = this.scanner.nextLine();
        this.addLogMessage("> " + term);
        if (this.card.containsKey(term)) {
            this.printMessage("The card \"" + term + "\" already exists.");
            return null;
        }

        this.printMessage("The definition of the card:");
        String definition = this.scanner.nextLine();
        this.addLogMessage("> " + definition);
        if (this.card.containsValue(definition)) {
            this.printMessage("The definition \"" + definition + "\" already exists.");
            return null;
        }

        return new Card(term, definition);
    }

    private void removeCard() {
        this.printMessage("Which card?");
        String card = this.scanner.nextLine();
        this.addLogMessage("> " + card);
        if (!this.card.containsKey(card)) {
            this.printMessage("Can't remove \"" + card + "\": there is no such card.");
            return;
        }

        this.card.remove(card);
        this.cardMistakes.remove(card);
        this.printMessage("The card has been removed.");
    }

    private void askCards() {
        this.printMessage("How many times to ask?");
        int numberOfTimesToAsk = this.scanner.nextInt();
        this.addLogMessage("> " + numberOfTimesToAsk);
        this.scanner.nextLine();
        this.addLogMessage("Consuming nextLine");

        if (numberOfTimesToAsk > this.card.size()) {
            numberOfTimesToAsk = this.card.size();
        }

        List<String> cardKeys = new ArrayList<>(this.card.keySet());
        for (int i = 0; i < numberOfTimesToAsk; i++) {
            String term = cardKeys.get(i);
            this.printMessage("Print the definition of \"" + term + "\":");
            String userDefinition = this.scanner.nextLine();
            this.addLogMessage("> " + userDefinition);

            if (this.card.get(cardKeys.get(i)).equalsIgnoreCase(userDefinition)) {
                this.printMessage("Correct!");
            } else if (this.card.containsValue(userDefinition)) {
                String correctTerm = getKeyByValue(userDefinition);
                this.printMessage("Wrong. The right answer is \"" + this.card.get(term) + "\", but your definition is correct for \"" + correctTerm + "\".");
                int mistakesCount = this.cardMistakes.getOrDefault(term, 0) + 1;
                this.cardMistakes.put(term, mistakesCount);
                this.addLogMessage("Add mistakes: " + mistakesCount + " to card: " + term);
            } else {
                this.printMessage("Wrong. The right answer is \"" + this.card.get(term) + "\".");
                int mistakesCount = this.cardMistakes.getOrDefault(term, 0) + 1;
                this.cardMistakes.put(term, mistakesCount);
                this.addLogMessage("Add mistakes: " + mistakesCount + " to card: " + term);
            }
        }
    }

    private void importCardsFromFile() {
        this.printMessage("File name:");
        String filename = this.scanner.nextLine();
        this.addLogMessage("> " + filename);

        File file = new File(filename);

        int importedCount = 0;
        try (Scanner fileData = new Scanner(file)) {
            while (fileData.hasNextLine()) {
                String fileLineData = fileData.nextLine();
                this.addLogMessage("> " + fileLineData);
                String[] fileLineDataParts = fileLineData.split(":");

                String term = fileLineDataParts[0];
                String definition = fileLineDataParts[1];
                int mistakesCount = Integer.parseInt(fileLineDataParts[2]);

                this.card.put(term, definition);
                this.addLogMessage("Add card " + term + " with definition: " + definition + " to memory.");
                this.cardMistakes.put(term, mistakesCount);
                this.addLogMessage("Add card " + term + " and mistakes: " + mistakesCount + " to memory.");

                importedCount++;
            }
            this.printMessage(importedCount + " cards have been loaded.");
        } catch (FileNotFoundException e) {
            this.printMessage("File not found.");
        }
    }

    private void importCardsFromFile(String importFile) {
        if (importFile == null) {
            return;
        }

        File file = new File(importFile);

        int importedCount = 0;
        try (Scanner fileData = new Scanner(file)) {
            while (fileData.hasNextLine()) {
                String fileLineData = fileData.nextLine();
                this.addLogMessage("> " + fileLineData);
                String[] fileLineDataParts = fileLineData.split(":");

                String term = fileLineDataParts[0];
                String definition = fileLineDataParts[1];
                int mistakesCount = Integer.parseInt(fileLineDataParts[2]);

                this.card.put(term, definition);
                this.addLogMessage("Add card " + term + " with definition: " + definition + " to memory.");
                this.cardMistakes.put(term, mistakesCount);
                this.addLogMessage("Add card " + term + " and mistakes: " + mistakesCount + " to memory.");

                importedCount++;
            }
            this.printMessage(importedCount + " cards have been loaded.");
        } catch (FileNotFoundException e) {
            this.printMessage("File not found.");
        }
    }

    private void exportCardsToFile() {
        this.printMessage("File name:");
        String filename = this.scanner.nextLine();
        this.addLogMessage("> " + filename);

        try (FileWriter writer = new FileWriter(filename)) {
            int exportedCount = 0;
            for (var entry : this.card.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                int mistakes = this.cardMistakes.getOrDefault(key, 0);

                writer.write(key + ":" + value + ":" + mistakes + "\n");
                this.addLogMessage("Write line " + key + ":" + value + ":" + mistakes + " to file: " + filename);
                exportedCount++;
            }

            this.printMessage(exportedCount + " cards have been saved.");
        } catch (IOException e) {
            this.printMessage("Couldn't export cards.");
        }
    }

    private void exportCardsToFile(String exportFile) {
        if (exportFile == null) {
            return;
        }

        try (FileWriter writer = new FileWriter(exportFile)) {
            int exportedCount = 0;
            for (var entry : this.card.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                int mistakes = this.cardMistakes.getOrDefault(key, 0);

                writer.write(key + ":" + value + ":" + mistakes + "\n");
                this.addLogMessage("Write line " + key + ":" + value + ":" + mistakes + " to file: " + exportFile);
                exportedCount++;
            }

            this.printMessage(exportedCount + " cards have been saved.");
        } catch (IOException e) {
            this.printMessage("Couldn't export cards.");
        }
    }

    private void displayHardestCard() {
        List<String> hardestCards = new ArrayList<>();
        int highestMistakesCount = 0;

        for (var entry : this.cardMistakes.entrySet()) {
            String cardName = entry.getKey();
            int mistakesCount = entry.getValue();

            if (mistakesCount > highestMistakesCount) {
                hardestCards.clear();
                hardestCards.add(cardName);
                highestMistakesCount = mistakesCount;
            } else if (mistakesCount == highestMistakesCount) {
                hardestCards.add(cardName);
            }
        }

        if (highestMistakesCount == 0) {
            this.printMessage("There are no cards with errors.");
        } else {
            String cardsText = "\"" + String.join("\", \"", hardestCards) + "\"";

            if (hardestCards.size() == 1) {
                this.printMessage("The hardest card is " + cardsText + ". You have " + highestMistakesCount + " errors answering it.");
            } else {
                this.printMessage("The hardest cards are " + cardsText + ". You have " + highestMistakesCount + " errors answering them.");
            }
        }
    }

    private void resetStats() {
        if (this.cardMistakes.isEmpty()) {
            this.printMessage("There are no cards with statistics.");
            return;
        }

        this.cardMistakes.clear();
        this.addLogMessage("Cleared card mistakes list.");
        this.printMessage("Card statistics have been reset.");
    }

    private void createLogsFile() {
        this.printMessage("File name:");
        String filename = this.scanner.nextLine();
        this.addLogMessage("> " + filename);

        try (FileWriter writer = new FileWriter(filename)) {
            writer.append(this.logMessages);
            System.out.println("The log has been saved.");
        } catch (IOException e) {
            this.printMessage(e.getMessage());
        }

        this.logMessages = new StringBuilder();
    }

    public String getKeyByValue(String value) {
        for (var entry : this.card.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String getCurrentTimestamp() {
        LocalDateTime timestamp = LocalDateTime.now();
        return "[" + timestamp.format(DateTimeFormatter.ISO_LOCAL_TIME) + "]";
    }

    private void addLogMessage(String message) {
        this.logMessages.append("[").append(this.getCurrentTimestamp()).append("]").append(message).append("\n");
    }

    private void printMessage(String message) {
        System.out.println(message);
        this.addLogMessage(message);
    }
}
