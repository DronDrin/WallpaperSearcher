package ru.drondron

class Settings {
    public static final String PROPERTIES_FILE = "application"
    private static Properties properties
    static {
        properties = new Properties()
        properties.load(Settings.getResourceAsStream(PROPERTIES_FILE + ".properties"))
    }

    static String getAppProperty(String key, String... parameters) {
        def value = properties.getProperty key
        parameters.eachWithIndex{ entry, i -> value = value.replaceAll('\\{' + i + '}', entry) }
        value
    }
}
