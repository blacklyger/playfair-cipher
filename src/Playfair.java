import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Playfair cipher (classical digraph substitution cipher, 1854).
 *
 * The 5x5 square is built from the key followed by the remaining letters of
 * the alphabet, with I and J sharing one cell. Only the letters A-Z are used,
 * everything else in key or message is ignored.
 *
 * NOTE: Playfair is a historical cipher and trivially breakable today.
 * It must not be used to protect real data.
 */
public final class Playfair {

    private static final int SIZE = 5;
    private static final char FILLER = 'X';
    private static final char ALT_FILLER = 'Q';

    private final char[][] square = new char[SIZE][SIZE];
    // Row and column of every letter, indexed by letter - 'A'.
    private final int[] row = new int[26];
    private final int[] column = new int[26];

    public Playfair(String key) {
        String letters = normalize(key) + "ABCDEFGHIKLMNOPQRSTUVWXYZ";

        StringBuilder used = new StringBuilder();
        for (char ch : letters.toCharArray()) {
            if (used.indexOf(String.valueOf(ch)) < 0) {
                used.append(ch);
            }
        }

        for (int idx = 0; idx < SIZE * SIZE; idx++) {
            char ch = used.charAt(idx);
            square[idx / SIZE][idx % SIZE] = ch;
            row[ch - 'A'] = idx / SIZE;
            column[ch - 'A'] = idx % SIZE;
        }
        // J is written as I.
        row['J' - 'A'] = row['I' - 'A'];
        column['J' - 'A'] = column['I' - 'A'];
    }

    /** Uppercase, keep only A-Z and replace J with I. */
    static String normalize(String input) {
        StringBuilder ret = new StringBuilder();
        for (char ch : input.toUpperCase(Locale.ROOT).toCharArray()) {
            if (ch >= 'A' && ch <= 'Z') {
                ret.append(ch == 'J' ? 'I' : ch);
            }
        }
        return ret.toString();
    }

    /**
     * Split the plaintext into pairs. A pair of identical letters is split by
     * inserting X (Q if the letter itself is X), an odd length is padded the same way.
     */
    static List<String> groupTwo(String plaintext) {
        String text = normalize(plaintext);
        List<String> pairs = new ArrayList<>();

        int i = 0;
        while (i < text.length()) {
            char first = text.charAt(i);
            char filler = first == FILLER ? ALT_FILLER : FILLER;

            if (i + 1 < text.length() && text.charAt(i + 1) != first) {
                pairs.add("" + first + text.charAt(i + 1));
                i += 2;
            } else {
                pairs.add("" + first + filler);
                i += 1;
            }
        }

        return pairs;
    }

    public String encrypt(String plaintext) {
        return transform(groupTwo(plaintext), 1);
    }

    public String decrypt(String ciphertext) {
        String text = normalize(ciphertext);

        if (text.length() % 2 != 0) {
            throw new IllegalArgumentException("Ciphertext must consist of an even number of letters.");
        }

        List<String> pairs = new ArrayList<>();
        for (int i = 0; i < text.length(); i += 2) {
            if (text.charAt(i) == text.charAt(i + 1)) {
                throw new IllegalArgumentException("Invalid ciphertext: pair \"" + text.substring(i, i + 2) + "\" contains a letter twice.");
            }
            pairs.add(text.substring(i, i + 2));
        }

        return transform(pairs, SIZE - 1);
    }

    /**
     * Apply the three Playfair rules. shift = 1 encrypts (move right/down),
     * shift = 4 decrypts (equivalent to moving left/up by one).
     */
    private String transform(List<String> pairs, int shift) {
        StringBuilder ret = new StringBuilder();

        for (String pair : pairs) {
            int a = pair.charAt(0) - 'A';
            int b = pair.charAt(1) - 'A';

            if (row[a] == row[b]) {
                // Same row: take the letter to the right (left when decrypting).
                ret.append(square[row[a]][(column[a] + shift) % SIZE]);
                ret.append(square[row[b]][(column[b] + shift) % SIZE]);
            } else if (column[a] == column[b]) {
                // Same column: take the letter below (above when decrypting).
                ret.append(square[(row[a] + shift) % SIZE][column[a]]);
                ret.append(square[(row[b] + shift) % SIZE][column[b]]);
            } else {
                // Rectangle: swap the columns.
                ret.append(square[row[a]][column[b]]);
                ret.append(square[row[b]][column[a]]);
            }
        }

        return ret.toString();
    }

    /** The key square as printable 5x5 block. */
    public String squareToString() {
        StringBuilder ret = new StringBuilder();
        for (char[] line : square) {
            for (int j = 0; j < SIZE; j++) {
                ret.append(line[j]).append(j < SIZE - 1 ? " " : "\n");
            }
        }
        return ret.toString();
    }
}
