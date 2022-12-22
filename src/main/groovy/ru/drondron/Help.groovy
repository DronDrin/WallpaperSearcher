package ru.drondron

class Help {
    private static String helpText = null

    static String getHelpText() {
        if (helpText == null) {
            helpText = ""
            def scanner = new Scanner(Help.getResourceAsStream("help.txt"))
            while (scanner.hasNextLine())
                helpText += scanner.nextLine()
        }
        helpText
    }
}
