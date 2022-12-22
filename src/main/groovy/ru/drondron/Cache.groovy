package ru.drondron

class Cache {
    private static Properties cache = new Properties()
    private static File cacheFile
    static {
        def executing = new File(Cache.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent()
        cacheFile = new File(executing + "/" + Settings.getAppProperty("cacheFile"))
        if (!cacheFile.exists())
            cacheFile.createNewFile()
        cache.load(new FileInputStream(cacheFile))
    }

    static String get(String name) {
        cache.getProperty(name)
    }

    static void set(String name, String value) {
        cache.setProperty(name, value)
        cache.store(new FileOutputStream(cacheFile), "")
    }

    static void setAll(SearchParameters parameters) {
        parameters.query = get "defaultQuery"
        parameters.atLeast = get "defaultAtLeast"
        parameters.ratio = get "defaultRatios"
        parameters.pathToDirectory = get "pathToDirectory"
        parameters.numberOfPackages = get("numberOfPackages")?.toInteger()
    }

    static void setNulls(SearchParameters parameters) {
        if (parameters.query == null || parameters.query.isBlank())
            parameters.query = get "defaultQuery"
        if (parameters.atLeast == null || parameters.atLeast.isBlank())
            parameters.atLeast = get "defaultAtLeast"
        if (parameters.ratio == null || parameters.ratio.isBlank())
            parameters.ratio = get "defaultRatios"
        if (parameters.pathToDirectory == null || parameters.pathToDirectory.isBlank())
            parameters.pathToDirectory = get "defaultPathToDirectory"
        if (parameters.numberOfPackages == null || parameters.numberOfPackages < 1)
            parameters.numberOfPackages = get("defaultNumberOfPackages")?.toInteger()
    }

    static void saveToCache(SearchParameters searchParameters) {
        if (searchParameters.checkValid() != null)
            throw new RuntimeException("tried to cache null value")
        set "defaultQuery", searchParameters.query
        set "defaultAtLeast", searchParameters.atLeast
        set "defaultRatios", searchParameters.ratio
        set "defaultPathToDirectory", searchParameters.pathToDirectory
        set "defaultNumberOfPackages", searchParameters.numberOfPackages.toString()
    }
}
