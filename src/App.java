import java.util.Scanner;

/**
 * Command line interface for the Playfair cipher.
 *
 *   java -cp bin App encrypt "<key>" "<text>"
 *   java -cp bin App decrypt "<key>" "<text>"
 *   java -cp bin App                          (interactive)
 */
public class App {

    private static final String USAGE = String.join("\n",
        "Usage:",
        "  java -cp bin App encrypt <key> <text>",
        "  java -cp bin App decrypt <key> <text>",
        "  java -cp bin App                       (interactive mode)");

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                interactive();
            } else if (args.length == 3 && (args[0].equals("encrypt") || args[0].equals("decrypt"))) {
                System.out.println(run(args[0], args[1], args[2]));
            } else {
                System.err.println(USAGE);
                System.exit(2);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    static String run(String mode, String key, String text) {
        if (Playfair.normalize(key).isEmpty()) {
            throw new IllegalArgumentException("The key must contain at least one letter A-Z.");
        }
        if (Playfair.normalize(text).isEmpty()) {
            throw new IllegalArgumentException("The text must contain at least one letter A-Z.");
        }

        Playfair cipher = new Playfair(key);
        return mode.equals("encrypt") ? cipher.encrypt(text) : cipher.decrypt(text);
    }

    private static void interactive() {
        // A single Scanner for System.in: a second one would lose buffered input.
        Scanner scanner = new Scanner(System.in);

        System.out.print("Key: ");
        String key = scanner.hasNextLine() ? scanner.nextLine() : "";
        System.out.print("Plaintext: ");
        String text = scanner.hasNextLine() ? scanner.nextLine() : "";
        scanner.close();
        System.out.println();

        String encrypted = run("encrypt", key, text);

        System.out.print(new Playfair(key).squareToString());
        System.out.println();
        System.out.println("Encrypted: " + encrypted);
        System.out.println("Decrypted: " + run("decrypt", key, encrypted));
    }
}
