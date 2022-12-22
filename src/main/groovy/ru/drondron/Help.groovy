package ru.drondron

/**
 * Utility class. Used to read help.txt resource file
 */
class Help {
    private static String helpText = null

    static String getHelpText() {
        if (helpText == null) {
            helpText = ""
            def scanner = new Scanner(Help.getResourceAsStream("help.txt"))
            while (scanner.hasNextLine())
                helpText += scanner.nextLine() + "\n"
        }
        helpText
    }
}
