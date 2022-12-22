package ru.drondron

class SearchParameters {
    String query, atLeast, ratio, pathToDirectory
    Integer numberOfPackages

    SearchParameters() {
        this.query = null
        this.atLeast = null
        this.ratio = null
        this.pathToDirectory = null
        this.numberOfPackages = null
    }

    String checkValid() {
        if (query == null || query.isBlank())
            return "query"
        if (atLeast == null || atLeast.isBlank())
            return "min resolution"
        if (ratio == null || ratio.isBlank())
            return "ratio"
        if (pathToDirectory == null || pathToDirectory.isBlank())
            return "path"
        if (numberOfPackages == null)
            return "number of packages"
        return null
    }

    @Override
    String toString() {
        return "query: " + query + '\n' +
                "atLeast: " + atLeast + '\n' +
                "ratio: " + ratio + '\n' +
                "pathToDirectory: " + pathToDirectory + '\n' +
                "numberOfPackages: " + numberOfPackages
    }
}
