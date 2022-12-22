package ru.drondron

/**
 * Entry point
 */
class Main {
    static void main(String[] args) {
        List<String> arguments = new ArrayList<>(List.of(args))
        if (arguments.contains("-h") || arguments.contains("--help")) {
            println Help.getHelpText()
        } else {
            SearchParameters searchParameters = new SearchParameters()

            if (arguments.contains("-i") || arguments.contains("--interactive")) {
                UserInput.setAllByUserInput(searchParameters)
            } else {
                setAllByParameters(searchParameters, arguments)
            }
            // set parameters that user didn't entered from cache
            Cache.setNulls(searchParameters)

            // check if there are parameters that user didn't entered and cache doesn't contains
            def checkValid = searchParameters.checkValid()
            if (checkValid != null) {
                printNoCachedNoInput(checkValid)
                return
            }

            Cache.saveToCache(searchParameters)
            println "Downloading images with parameters:"
            println searchParameters.toString()

            // download
            (1..searchParameters.numberOfPackages).each {
                def images = WebFetcher.getImages(WebFetcher.wallpaperRequest(searchParameters.query, searchParameters.atLeast, searchParameters.ratio))
                WebFetcher.saveImagesToDirectory(searchParameters.pathToDirectory, images, searchParameters)
            }
        }
    }

    /**
     * prints message about user has never entered some parameter, and now
     * @param name parameter name
     */
    static void printNoCachedNoInput(String name) {
        printf "error! You used cached value for %s, but you never entered any value for this parameter!%n", name
    }

    /**
     * sets all fields in SearchParameters by app parameters
     * @param searchParameters object to set fields in
     * @param arguments program start arguments
     */
    static void setAllByParameters(SearchParameters searchParameters, List<String> arguments) {
        for (i in 0..<(arguments.size() - 1)) {
            switch (arguments[i]) {
                case "-q", "--query" -> {
                    searchParameters.query = arguments[i + 1]
                }
                case "-l", "--at-least" -> {
                    searchParameters.atLeast = arguments[i + 1]
                }
                case "-r", "--ratio" -> {
                    searchParameters.ratio = arguments[i + 1]
                }
                case "-p", "--path" -> {
                    searchParameters.pathToDirectory = arguments[i + 1]
                }
                case "-n", "-number-of-packages" -> {
                    searchParameters.numberOfPackages = arguments[i + 1].toInteger()
                }
            }
        }
    }
}
