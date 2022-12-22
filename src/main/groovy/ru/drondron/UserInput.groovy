package ru.drondron

import java.util.regex.Pattern

class UserInput {
    private static Scanner scanner = new Scanner(System.in)

    static String stringInput(String message) {
        print message
        scanner.nextLine()
    }

    static Integer intInput(String message) {
        def input = closureInput(message) { it.isInteger() }
        (input.isBlank() || input == null) ? null : input.toInteger()
    }

    static String closureInput(String message, Closure closure) {
        String input
        do input = stringInput(message)
        while (!closure(input))
        input
    }

    static String regexInput(String message, Pattern regex) {
        closureInput(message, { it =~ regex })
    }

    static void setAllByUserInput(SearchParameters searchParameters) {
        println "Enter all parameters:"
        println "(Enter nothing to use last entered value)"
        searchParameters.query = stringInput("query: ")
        searchParameters.atLeast = regexInput("min resolution: ", ~/(|(\d{1,5}x\d{1,5}))/)
        searchParameters.ratio = regexInput("ratio: ", ~/(|(\d{1,5}x\d{1,5}))/)
        searchParameters.pathToDirectory = closureInput("path to save images: ") {
            String path ->
                path == null || path.isBlank() || new File(String.join(File.separator, path.split(File.separator)[0..-2])).exists()
        }

        def numberOfPackagesInput = closureInput("number of packages: ", { String it -> it == null || it.isBlank() || it.isInteger() })
        searchParameters.numberOfPackages = numberOfPackagesInput.isBlank() ? null : numberOfPackagesInput.toInteger()
        if (searchParameters.numberOfPackages != null) {
            while (searchParameters.numberOfPackages < 1) {
                println "number of packages must be more or equals than one!"
                searchParameters.numberOfPackages = intInput("number of packages: ")
            }
        }
    }
}
