import java.util.List;

/**
 * Dependency free tests, run with:
 *   javac -d bin src/*.java test/*.java && java -ea -cp bin PlayfairTest
 */
public class PlayfairTest {

    private static int failures = 0;

    private static void check(String name, Object actual, Object expected) {
        if (!actual.equals(expected)) {
            failures++;
            System.out.println("FAIL " + name + ": expected " + expected + ", got " + actual);
        } else {
            System.out.println("ok   " + name);
        }
    }

    private static void checkThrows(String name, Runnable code) {
        try {
            code.run();
            failures++;
            System.out.println("FAIL " + name + ": no exception");
        } catch (IllegalArgumentException e) {
            System.out.println("ok   " + name);
        }
    }

    public static void main(String[] args) {
        // Reference vector from the Wikipedia article on the Playfair cipher.
        Playfair wiki = new Playfair("playfair example");
        check("wikipedia encrypt", wiki.encrypt("Hide the gold in the tree stump"), "BMODZBXDNABEKUDMUIXMMOUVIF");
        check("wikipedia decrypt", wiki.decrypt("BMODZBXDNABEKUDMUIXMMOUVIF"), "HIDETHEGOLDINTHETREXESTUMP");

        // Classic textbook example (Stallings) with key MONARCHY.
        Playfair monarchy = new Playfair("MONARCHY");
        check("monarchy square", monarchy.squareToString(),
            "M O N A R\nC H Y B D\nE F G I K\nL P Q S T\nU V W X Z\n");
        check("monarchy balloon", monarchy.encrypt("balloon"), "IBSUPMNA");

        check("pairs double letters", Playfair.groupTwo("balloon"), List.of("BA", "LX", "LO", "ON"));
        check("pairs odd length", Playfair.groupTwo("abc"), List.of("AB", "CX"));
        check("pairs double X", Playfair.groupTwo("xx"), List.of("XQ", "XQ"));
        check("pairs single X", Playfair.groupTwo("x"), List.of("XQ"));
        check("J is I", Playfair.normalize("Jo-Jo 123!"), "IOIO");

        // Input that crashed the old version: odd length, digits, punctuation.
        Playfair key = new Playfair("Geheimer Schlüssel 2022");
        for (String text : new String[] {"a", "abc", "Hallo, Welt! 123", "xxxxx", "jjjj", "ZZ"}) {
            String encrypted = key.encrypt(text);
            check("roundtrip " + text, key.encrypt(key.decrypt(encrypted)), encrypted);
        }

        checkThrows("odd ciphertext rejected", () -> key.decrypt("ABC"));
        checkThrows("double letter ciphertext rejected", () -> key.decrypt("AABB"));
        checkThrows("empty key rejected", () -> App.run("encrypt", "123", "hello"));

        System.out.println(failures == 0 ? "\nAll tests passed." : "\n" + failures + " test(s) failed.");
        System.exit(failures == 0 ? 0 : 1);
    }
}
