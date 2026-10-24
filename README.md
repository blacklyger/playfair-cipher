# Playfair cipher in Java

A clean, dependency-free implementation of the **Playfair cipher**, the first practical
*digraph* substitution cipher (Charles Wheatstone, 1854). It was used in the Boer War and in
World War I and II. Instead of single letters it encrypts **pairs of letters**, which made
simple frequency analysis much harder.

The project is an exercise in classical cryptography: building the key square, handling the
edge cases of the algorithm correctly and validating input carefully.

> ⚠️ **Do not use Playfair to protect real data.** It is a historical cipher and can be broken
> within minutes using digraph frequency analysis or known-plaintext attacks. For real
> encryption, use modern, vetted algorithms (e.g. AES-GCM or ChaCha20-Poly1305) from an
> established library.

## How it works

1. **Key square:** the letters of the key (without duplicates), followed by the rest of the alphabet, fill a 5×5 grid. `I` and `J` share one cell.

   ```
   Key: MONARCHY     M O N A R
                     C H Y B D
                     E F G I K
                     L P Q S T
                     U V W X Z
   ```

2. **Prepare the plaintext:** keep only letters, replace `J` with `I` and split the text into pairs. If a pair would contain the same letter twice, an `X` is inserted (`Q` if the letter is `X`). An odd-length text is padded the same way: `balloon → BA LX LO ON`.
3. **Encrypt each pair:**
   - **Same row:** take the letter to the right of each (wrapping around).
   - **Same column:** take the letter below each (wrapping around).
   - **Rectangle:** take the letter in the same row but in the other letter's column.
4. **Decrypt** by applying the rules in reverse (left / up).

## Usage

Requirements: JDK 11+.

```bash
javac -d bin src/*.java

java -cp bin App encrypt "playfair example" "Hide the gold in the tree stump"
# BMODZBXDNABEKUDMUIXMMOUVIF

java -cp bin App decrypt "playfair example" BMODZBXDNABEKUDMUIXMMOUVIF
# HIDETHEGOLDINTHETREXESTUMP

java -cp bin App            # interactive: prints key square, ciphertext and decryption
```

The decrypted text still contains the filler letters (`X`/`Q`) and uses `I` for `J`, as
the original cipher does.

## Tests

```bash
javac -d bin src/*.java test/*.java && java -cp bin PlayfairTest
```

The tests check published reference vectors (Wikipedia, and Stallings' *Cryptography and
Network Security* with the key `MONARCHY`), all edge cases of pair building (double letters,
`XX`, odd lengths), round trips with digits, punctuation and umlauts, and that invalid
ciphertexts are rejected.

## Project structure

```
src/Playfair.java       The cipher: key square, normalization, pair building, rules
src/App.java            Command line interface (arguments or interactive mode)
test/PlayfairTest.java  Dependency-free tests
```

## Fixes in the current version

- The program crashed whenever input was piped in, because two `Scanner` objects were created on `System.in`. It now uses a single `Scanner` and also accepts command-line arguments.
- Building the letter pairs could throw `StringIndexOutOfBoundsException` (e.g. for `"abc"`), and input containing a pair like `XX` was handled incorrectly. Both cases were fixed.
- Digits, spaces and punctuation in the text or key used to crash the program (`Letter could not be found`). They are now ignored.
- A `J` in the key was dropped instead of being treated as `I`.
- Upper-casing is now locale-independent (`Locale.ROOT`), so the program behaves the same on systems with, for example, a Turkish locale.
