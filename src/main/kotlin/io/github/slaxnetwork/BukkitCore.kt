package io.github.slaxnetwork

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import com.github.shynixn.mccoroutine.bukkit.registerSuspendingEvents
import io.github.slaxnetwork.icon.IconRegistry
import io.github.slaxnetwork.icon.IconRegistryImpl
import io.github.slaxnetwork.kyouko.KyoukoAPI
import io.github.slaxnetwork.listeners.AsyncPlayerChatListener
import io.github.slaxnetwork.listeners.PlayerLoginListener
import io.github.slaxnetwork.listeners.PlayerQuitListener
import io.github.slaxnetwork.profile.ProfileRegistryImpl
import io.github.slaxnetwork.profile.ProfileRegistry
import io.github.slaxnetwork.rank.RankRegistry
import io.github.slaxnetwork.rank.RankRegistryImpl
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.plugin.ServicePriority

class BukkitCore : SuspendingJavaPlugin() {
    lateinit var kyouko: KyoukoAPI
        private set

    lateinit var profileRegistry: ProfileRegistry
        private set

    lateinit var rankRegistry: RankRegistry
        private set

    lateinit var iconRegistry: IconRegistry
        private set

    lateinit var testBukkitCoreImpl: BukkitCoreAPIImpl
        private set

    override suspend fun onLoadAsync() {
        kyouko = KyoukoAPI(System.getenv("API_SECRET") ?: "KYOUKO")
    }

    override suspend fun onEnableAsync() {
        rankRegistry = RankRegistryImpl(kyouko.ranks)
        profileRegistry = ProfileRegistryImpl()
        iconRegistry = IconRegistryImpl(kyouko.icons)

        try {
            rankRegistry.initialize()
            iconRegistry.initialize()
        } catch(ex: Exception) {
            logger.severe("Unable to initialize a service.")
            ex.printStackTrace()
            server.shutdown()
            return
        }

        mm = SlaxMiniMessageBuilder(iconRegistry)
            .createInstance()

        server.servicesManager.register(
            BukkitCoreAPI::class.java,
            BukkitCoreAPIImpl(profileRegistry, kyouko.servers),
            this,
            ServicePriority.Normal
        )

        setOf(
            AsyncPlayerChatListener(profileRegistry),
            PlayerLoginListener(profileRegistry, kyouko.profiles),
            PlayerQuitListener(profileRegistry)
        ).forEach { server.pluginManager.registerSuspendingEvents(it, this) }

        testBukkitCoreImpl.registerServer("127.0.0.1", 25565, "lobby")
    }

    override suspend fun onDisableAsync() {
    }
}

/**
 * Public [MiniMessage] instance.
 */
lateinit var mm: MiniMessage
    private set