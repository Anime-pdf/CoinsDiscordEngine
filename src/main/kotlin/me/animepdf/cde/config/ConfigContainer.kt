package me.animepdf.cde.config

import org.spongepowered.configurate.ConfigurateException
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.objectmapping.ObjectMapper
import org.spongepowered.configurate.serialize.TypeSerializerCollection
import org.spongepowered.configurate.yaml.NodeStyle
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File
import java.nio.file.Files
import java.util.function.Consumer
import java.util.function.UnaryOperator

class ConfigContainer(val dataFolder: File) {
    init {
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    lateinit var generalConfig: GeneralConfig
    lateinit var languageConfig: LanguageConfig

    private fun <T> loadConfiguration(
        configClass: Class<T>,
        fileName: String,
        serializers: TypeSerializerCollection
    ): T {
        val configFile = File(dataFolder, fileName);
        val configPath = configFile.toPath()

        val loader = YamlConfigurationLoader.builder()
            .path(configPath)
            .defaultOptions { opts: ConfigurationOptions ->
                opts.serializers({ serializersInner ->
                    serializersInner.registerAll(
                        serializers
                    )
                })
            }
            .nodeStyle(NodeStyle.BLOCK)
            .indent(2)
            .build()

        try {
            if (Files.notExists(configPath)) {
                val newInstance: T = configClass.getDeclaredConstructor().newInstance()
                val newNode = loader.createNode()
                ObjectMapper.factory().get(configClass).save(newInstance, newNode)
                loader.save(newNode)
                return newInstance
            }

            val node = loader.load()
            val configInstance = ObjectMapper.factory().get(configClass).load(node)

            ObjectMapper.factory().get(configClass).save(configInstance, node)
            loader.save(node)

            return configInstance

        } catch (error: ConfigurateException) {
            System.err.println("Error loading configuration: $fileName")
            System.err.println("Error: ${error.message}")
            System.err.println("Error StackTrace: ${error.message}")
            error.printStackTrace()

            // Fallback: try to create a default instance if loading fails catastrophically
            try {
                System.err.println("Attempting to return a default instance for $fileName")
                return configClass.getDeclaredConstructor().newInstance()
            } catch (error: ReflectiveOperationException) {
                throw RuntimeException("Failed to instantiate default config for $fileName", error)
            }
        } catch (error: ReflectiveOperationException) {
            throw RuntimeException("Failed to create instance of config: $fileName", error);
        }
    }

    private fun <T> saveConfiguration(
        configInstance: T,
        configClass: Class<T>,
        fileName: String,
        serializers: TypeSerializerCollection
    ) {
        val configFile = File(dataFolder, fileName)
        val configPath = configFile.toPath()

        val loader = YamlConfigurationLoader.builder()
            .path(configPath)
            .defaultOptions(UnaryOperator { opts: ConfigurationOptions? ->
                opts!!.serializers(Consumer { serializersInner: TypeSerializerCollection.Builder? ->
                    serializersInner!!.registerAll(
                        serializers
                    )
                })
            })
            .nodeStyle(NodeStyle.BLOCK)
            .indent(2)
            .build()

        try {
            val node = loader.createNode()
            ObjectMapper.factory().get(configClass).save(configInstance, node)
            loader.save(node)
        } catch (error: ConfigurateException) {
            System.err.println("Error saving configuration: $fileName")
            error.printStackTrace()
        }
    }

    fun loadConfigs() {
        val generalSerializers = TypeSerializerCollection.builder()
            .build()
        generalConfig = loadConfiguration(GeneralConfig::class.java, "config.yml", generalSerializers)

        val languageSerializers = TypeSerializerCollection.builder()
            .build()

        generateLanguageTemplates(languageSerializers)
        languageConfig =
            loadConfiguration(LanguageConfig::class.java, "language.yml", languageSerializers)
    }

    private fun generateLanguageTemplates(serializers: TypeSerializerCollection) {
        val russianFile = File(dataFolder, "language.russian.yml")
        val englishFile = File(dataFolder, "language.yml")

        if (!russianFile.exists()) {
            val russianConfig = LanguageConfig.createRussian()
            saveConfiguration(
                russianConfig,
                LanguageConfig::class.java,
                russianFile.name,
                serializers
            )
        }

        if (!englishFile.exists()) {
            val englishConfig = LanguageConfig.createEnglish()
            saveConfiguration(
                englishConfig,
                LanguageConfig::class.java,
                englishFile.name,
                serializers
            )
        }
    }

    fun reloadConfigs() {
        loadConfigs()
    }
}