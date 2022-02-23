import java.util.*;

public class App {

    public static char[][] generate_playfair_square(String key_input) {

        char[][] square = new char[5][5];

        key_input = key_input.toUpperCase();

        String key = new String();
        for (int i = 0; i < key_input.length(); i++) {
            if (key_input.charAt(i) == 'J')
                continue;

            if (key.contains("" + key_input.charAt(i)))
                continue;

            key += key_input.charAt(i);
        }

        String tempkey = new String(key);
        for (int i = 0; i < 26; i++) {
            char ch = (char)(i + 65);

            if (ch == 'J')
                continue;

            if (key.contains("" + ch))
                continue;
            
            tempkey += ch;
        }

        for (int i = 0, idx = 0; i < square.length; i++) {
            for (int j = 0; j < square[i].length; j++) {
                square[i][j] = tempkey.charAt(idx++);
            }
        }

        return square;
    }

    public static String[] group_two(String input) {

        input = input.replaceAll("\\s+", "").toUpperCase();

        String ret = "";

        int len = input.length();
        for (int i = 0; i < len; i++) {
            if (input.charAt(i) == 'J') {
                ret += 'I';
            } else {
                ret += input.charAt(i);
            }
        }

        len = ret.length();
        for (int i = 0; i < len; i = i + 2) {
            if (ret.charAt(i + 1) == ret.charAt(i)) {
                ret = ret.substring(0, i + 1) + 'X' + ret.substring(i + 1);
            }
        }

        if (ret.length() % 2 != 0) {
            ret += "X";
        }

        return ret.split("(?<=\\G.{2})");
    }

    public static HashMap<String, Integer> search_letter(char[][] playfair_square, char letter) {

        for (int i = 0; i < playfair_square.length; i++) {
            for (int j = 0; j < playfair_square[i].length; j++) {
                if (letter == playfair_square[i][j]) {
                    HashMap<String, Integer> map = new HashMap<String, Integer>();
                    map.put("i", i);
                    map.put("j", j);
                    return map;
                }
            }
        }

        throw new Error("Letter could not be found!");
    }

    public static List<HashMap<String, Integer>> get_letter_indices(char[][] playfair_square, String plaintext) {

        List<HashMap<String, Integer>> ret = new ArrayList<HashMap<String, Integer>>();

        for (int i = 0; i < plaintext.length(); i++) {
            char letter = plaintext.charAt(i);
            ret.add(search_letter(playfair_square, letter));
        }

        return ret;
    }

    public static char letter_vertical(char[][] playfair_square, int i, int j, int dst, boolean up) {
        return playfair_square[up ? (((i - dst) % 5) + 5) % 5 : (i + dst) % 5][j];
    }

    public static char letter_horizontal(char[][] playfair_square, int i, int j, int dst, boolean left) {
        return playfair_square[i][left ? (((j - dst) % 5) + 5) % 5 : (j + dst) % 5];
    }

    public static String first_rule(char[][] playfair_square, HashMap<String, Integer> first_char, HashMap<String, Integer> second_char) {
        String ret = "";
        ret += letter_vertical(playfair_square, first_char.get("i"), first_char.get("j"), 1, false);
        ret += letter_vertical(playfair_square, second_char.get("i"), second_char.get("j"), 1, false);
        return ret;
    }

    public static String second_rule(char[][] playfair_square, HashMap<String, Integer> first_char, HashMap<String, Integer> second_char) {
        String ret = "";
        ret += letter_horizontal(playfair_square, first_char.get("i"), first_char.get("j"), 1, false);
        ret += letter_horizontal(playfair_square, second_char.get("i"), second_char.get("j"), 1, false);
        return ret;
    }

    public static String third_rule(char[][] playfair_square, HashMap<String, Integer> first_char, HashMap<String, Integer> second_char) {
        String ret = "";
        ret += playfair_square[first_char.get("i")][second_char.get("j")];
        ret += playfair_square[second_char.get("i")][first_char.get("j")];
        return ret;
    }

    public static String determine_rule(String message, char[][] playfair_square) {

        String[] blocks = group_two(message);

        String ret = new String();

        for (int i = 0; i < blocks.length; i++) {
            List<HashMap<String, Integer>> map = get_letter_indices(playfair_square, blocks[i]);

            HashMap<String, Integer> first_char = map.get(0);
            HashMap<String, Integer> second_char = map.get(1);

            if (first_char.get("j").equals(second_char.get("j"))) {
                ret += first_rule(playfair_square, first_char, second_char);
            } else if (first_char.get("i").equals(second_char.get("i"))) {
                ret += second_rule(playfair_square, first_char, second_char);
            } else {
                ret += third_rule(playfair_square, first_char, second_char);
            }
        }

        return ret;
    }

    public static String encrypt(String message, String square_key) {
        char[][] playfair_square = generate_playfair_square(square_key);
        return determine_rule(message, playfair_square);
    }

    public static void main(String[] args) throws Exception {
        Scanner input_scanner = new Scanner(System.in);
        Scanner square_key_scanner = new Scanner(System.in);
        String input = input_scanner.nextLine();
        String square_key = square_key_scanner.nextLine();
        input_scanner.close();
        square_key_scanner.close();

        String encrypted = encrypt(input, square_key);      
        System.out.println(encrypted);
    }
}
