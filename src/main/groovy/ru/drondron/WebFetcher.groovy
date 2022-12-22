package ru.drondron

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import ru.drondron.json.Data
import ru.drondron.json.Image

import javax.imageio.ImageIO
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Wallpapers loader
 */
class WebFetcher {
    private static ObjectMapper objectMapper = new ObjectMapper()
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    /**
     * Requests list of wallpapers
     * @return JSON response from server
     */
    static String wallpaperRequest(String query, String atLeast, String ratios) {
        Settings.getAppProperty("searchApiUrl", query, atLeast, ratios).toURL().text
    }

    /**
     * parse images list from JSON
     * @param jsonResponse
     * @return
     */
    static Image[] getImages(String jsonResponse) {
        def value = objectMapper.readValue(jsonResponse, Data.class)
        value.getData()
    }

    /**
     * saves images list to directory
     * @param pathToDirectory directory to save images
     * @param images images to save
     * @param searchParameters parameters for validation
     */
    static void saveImagesToDirectory(String pathToDirectory, Image[] images, SearchParameters searchParameters) {
        new File(pathToDirectory).mkdirs()
        AtomicInteger finished = new AtomicInteger(0)
        AtomicInteger finishedWithError = new AtomicInteger(0)
        AtomicReference<String> lastMessage = new AtomicReference<>("")
        AtomicBoolean lastMessageIsFinished = new AtomicBoolean(false)
        Arrays.stream(images).parallel().forEach {
            if (it.purity == "sfw") {
                def scale = Math.pow(10, 2)
                // check if image resolution is bigger or equals min resolution, and whether it has right ratio
                if (it.dimensionX < (searchParameters.atLeast.split("x")[0].toInteger()) ||
                        it.dimensionY < (searchParameters.atLeast.split("x")[1].toInteger()) ||
                        (Math.round(((double) it.dimensionX / it.dimensionY) * scale) / scale !=
                                Math.round(scale * searchParameters.ratio.split("x")[0].toDouble() / searchParameters.ratio.split("x")[1].toDouble()) / scale)) {
                    print("image %s has wrong resolution(%dx%d)! It will not be downloaded%n".formatted it.id, it.dimensionX, it.dimensionY, lastMessage, lastMessageIsFinished, true)
                    finishedWithError.incrementAndGet()
                    return
                }
                boolean stop = false
                boolean susses = false
                boolean exist = false
                while (!(stop || susses)) {
                    // file to save image to
                    File imageFile = null
                    try {
                        // image format is like image/png, so it should be converted to 'png'
                        def format = it.fileType.split("/")[-1]
                        imageFile = new File("%s/%s.%s".formatted(pathToDirectory, it.id, format))
                        print("loading file %s to %s".formatted(imageFile.name, imageFile.absolutePath), lastMessage, lastMessageIsFinished)
                        if (imageFile.exists()) {
                            exist = true
                            throw new IOException("File %s already exist!".formatted(imageFile.name))
                        }
                        imageFile.createNewFile()

                        // downloading file
                        def inputStream = it.path.toURL().openConnection().getInputStream()
                        Files.copy(inputStream, imageFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                        susses = true
                        finished.incrementAndGet()
                    } catch (Exception ex) {
                        if (ex.message.contains("429")) {
                            print("429 error code! i'll try to download that wallpaper again in two seconds...", lastMessage, lastMessageIsFinished, true)
                            Thread.sleep(2000)
                        } else {
                            stop = true
                            print(ex.toString(), lastMessage, lastMessageIsFinished, true)
                            finishedWithError.incrementAndGet()
                        }
                        try {
                            // exception! File must be removed (only if it didn't exists before, of course)
                            if (imageFile != null && imageFile.exists() && !exist)
                                imageFile.delete()
                        } catch (Exception exIn) {
                            print(exIn.toString(), lastMessage, lastMessageIsFinished, true)
                        }
                    }
                }
            } else
                print("sfw error", lastMessage, lastMessageIsFinished, true)
            def message = "finished %d/24%s".formatted(finished.get() + finishedWithError.get(), finishedWithError.get() == 0 ? "" :
                    (" (" + finishedWithError.get() + " error" + (finishedWithError.get() > 1 ? "s" : "") + ")"))
            printFinished(message, lastMessage, lastMessageIsFinished)
        }
    }

    /**
     * prints message to console, and eases previous line if it's needed
     * @param message text to print
     * @param lastMessage last printed message
     * @param lastMessageIsFinished is last printed message matches finished%d/24...
     * @param err should message be printed as error
     */
    private static synchronized void print(String message, AtomicReference<String> lastMessage, AtomicBoolean lastMessageIsFinished, boolean err = false) {
        if (lastMessageIsFinished.get()) {
            eraseLine()
        }
        if (err)
            System.err.println(message)
        else
            println message
        if (lastMessageIsFinished.get()) {
            println lastMessage.get()
        } else {
            lastMessageIsFinished.set(false)
            lastMessage.set(message)
        }
    }

    /**
     * print 'finished' message, and eases previous line if it's needed
     * @param message message to print
     * @param lastMessage last printed message
     * @param lastMessageIsFinished is last printed message matches finished%d/24...
     */
    private static synchronized void printFinished(String message, AtomicReference<String> lastMessage, AtomicBoolean lastMessageIsFinished) {
        if (lastMessageIsFinished.get()) {
            eraseLine()
        }
        println message
        lastMessage.set(message)
        lastMessageIsFinished.set(true)
    }

    /**
     * erases previous line
     */
    private static void eraseLine() {
        System.out.printf("\033[%dA", 1)
        System.out.print("\033[2K")
    }
}