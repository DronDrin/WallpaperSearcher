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

class WebFetcher {
    private static ObjectMapper objectMapper = new ObjectMapper()
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    static String wallpaperRequest(String query, String atLeast, String ratios) {
        Settings.getAppProperty("searchApiUrl", query, atLeast, ratios).toURL().text
    }

    static Image[] getImages(String jsonResponse) {
        def value = objectMapper.readValue(jsonResponse, Data.class)
        value.getData()
    }

    static void saveImagesToDirectory(String pathToDirectory, Image[] images, SearchParameters searchParameters) {
        new File(pathToDirectory).mkdirs()
        ImageIO.setUseCache(false)
        AtomicInteger finished = new AtomicInteger(0)
        AtomicInteger finishedWithError = new AtomicInteger(0)
        AtomicReference<String> lastMessage = new AtomicReference<>("")
        AtomicBoolean lastMessageIsFinished = new AtomicBoolean(false)
        Arrays.stream(images).parallel().forEach {
            if (it.purity == "sfw") {
                def scale = Math.pow(10, 2)
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
                    File imageFile = null
                    try {
                        def format = it.fileType.split("/")[-1]
                        imageFile = new File("%s/%s.%s".formatted(pathToDirectory, it.id, format))
                        print("loading file %s to %s".formatted(imageFile.name, imageFile.absolutePath), lastMessage, lastMessageIsFinished)
                        if (imageFile.exists()) {
                            exist = true
                            throw new IOException("File %s already exist!".formatted(imageFile.name))
                        }
                        imageFile.createNewFile()

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

    static synchronized void print(String message, AtomicReference<String> lastMessage, AtomicBoolean lastMessageIsFinished, boolean err = false) {
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

    static synchronized void printFinished(String message, AtomicReference<String> lastMessage, AtomicBoolean lastMessageIsFinished) {
        if (lastMessageIsFinished.get()) {
            eraseLine()
        }
        println message
        lastMessage.set(message)
        lastMessageIsFinished.set(true)
    }

    static void eraseLine() {
        System.out.printf("\033[%dA", 1)
        System.out.print("\033[2K")
    }
}