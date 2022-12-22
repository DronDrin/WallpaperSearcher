package ru.drondron

/**
 * This class saves and loads parameters from cache file. Used to store user's input.
 */
class Cache {
    private static Properties cache = new Properties()
    private static File cacheFile
    static {
        // get directory that the program is executing in
        def executing = new File(Cache.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent()
        cacheFile = new File(executing + "/" + Settings.getAppProperty("cacheFile"))
        if (!cacheFile.exists())
            cacheFile.createNewFile()
        cache.load(new FileInputStream(cacheFile))
    }

    /**
     * get property from cache
     * @param name name of property
     * @return property value
     */
    static String get(String name) {
        cache.getProperty(name)
    }

    /**
     * set property in cache
     * @param name property name
     * @param value new value of property
     */
    static void set(String name, String value) {
        cache.setProperty(name, value)
        cache.store(new FileOutputStream(cacheFile), "")
    }

    /**
     * set all parameters in object from cache
     * @param parameters object to set properties in
     */
    static void setAll(SearchParameters parameters) {
        parameters.query = get "defaultQuery"
        parameters.atLeast = get "defaultAtLeast"
        parameters.ratio = get "defaultRatios"
        parameters.pathToDirectory = get "pathToDirectory"
        parameters.numberOfPackages = get("numberOfPackages")?.toInteger()
    }

    /**
     * set all parameters in object from cache that is null or blank
     * @param parameters parameters object to set properties in
     */
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

    /**
     * save all parameters in object
     * @param searchParameters object
     */
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
