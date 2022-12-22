package ru.drondron

class Main {
    static void main(String[] args) {
        List<String> arguments = new ArrayList<>(List.of(args))
        if (arguments.contains("-h") || arguments.contains("--help")) {
            println Help.getHelpText()
        } else {
            SearchParameters searchParameters = new SearchParameters()
            Cache.setAll(searchParameters)

            if (arguments.contains("-i") || arguments.contains("--interactive")) {
                UserInput.setAllByUserInput(searchParameters)
            } else {
                setAllByParameters(searchParameters, arguments)
            }
            Cache.setNulls(searchParameters)

            def checkValid = searchParameters.checkValid()
            if (checkValid != null) {
                printNoCachedNoInput(checkValid)
                return
            }

            Cache.saveToCache(searchParameters)
            println "Downloading images with parameters:"
            println searchParameters.toString()

            (1..searchParameters.numberOfPackages).each {
                def images = WebFetcher.getImages(WebFetcher.wallpaperRequest(searchParameters.query, searchParameters.atLeast, searchParameters.ratio))
                WebFetcher.saveImagesToDirectory(searchParameters.pathToDirectory, images, searchParameters)
            }
        }
    }

    static void printNoCachedNoInput(String name) {
        printf "error! You used cached value for %s, but you never entered any value for this parameter!%n", name
    }

    static void setAllByParameters(SearchParameters searchParameters, List<String> arguments) {
        for (i in 0..<arguments.size()) {
            switch (arguments[i]) {
                case "-q", "--query" -> {
                    searchParameters.query = arguments[i + 1]
                    Cache.set("defaultQuery", searchParameters.query)
                }
                case "-l", "--at-least" -> {
                    searchParameters.atLeast = arguments[i + 1]
                    Cache.set("defaultAtLeast", searchParameters.atLeast)
                }
                case "-r", "--ratio" -> {
                    searchParameters.ratio = arguments[i + 1]
                    Cache.set("defaultQuery", searchParameters.ratio)
                }
                case "-p", "--path" -> {
                    searchParameters.pathToDirectory = arguments[i + 1]
                    Cache.set("defaultPath", searchParameters.pathToDirectory)
                }
                case "-n", "-number-of-packages" -> {
                    searchParameters.numberOfPackages = arguments[i + 1].toInteger()
                    Cache.set("defaultQuery", searchParameters.numberOfPackages.toString())
                }
            }
        }
    }
}
