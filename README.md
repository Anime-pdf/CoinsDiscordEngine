<div align="center">

# CoinsDiscordEngine

[![Java Version](https://img.shields.io/badge/Java-21-orange?style=flat-square)](https://adoptium.net/)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21+-green?style=flat-square)](https://papermc.io/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)](LICENSE)

**Seamlessly integrate CoinsEngine economy with Discord via DiscordSRV.**
<br>
A simple and efficient bridge for transaction logging and notification management.

</div>

---

## 💎 Description
**CoinsDiscordEngine (CDE)** is an addon that connects the [CoinsEngine](https://www.spigotmc.org/resources/coinsengine.84121/) economy plugin with [DiscordSRV](https://www.spigotmc.org/resources/discordsrv.18494/).

The main goal of this plugin is to send notifications to a specified Discord channel whenever financial operations occur on your server. This is perfect for public transaction logs or economy administration auditing.

### ✨ Key Features
* **Transaction Logs:** Logs `/pay` commands (player -> player) directly to Discord.
* **Admin Monitoring:** Notifications when administrators give (`add`) or take (`remove`) currency.
* **Reason Support:** Displays the reason for the transfer or balance change if one was provided.
* **Flexible Configuration:** Fully customizable messages and command aliases.
* **Modern Core:** Written in Kotlin, supports Paper and it's forks.

---

## 🛠 Requirements
To run this plugin, ensure you have the following components:

| Component | Requirement                  |
| :--- |:-----------------------------|
| **Java** | Version **21** or higher     |
| **Server Core** | Paper, Purpur, Folia (1.21+) |
| **CoinsEngine** | Version 2.6.0                |
| **DiscordSRV** | Version 1.30.3+              |

---

## 🚀 Installation
1.  Install and configure **CoinsEngine** and **DiscordSRV**. Ensure they are working correctly.
2.  Download the **CoinsDiscordEngine** `.jar` file.
3.  Place it into your server's `/plugins/` folder.
4.  Restart the server.
5.  Open `plugins/CoinsDiscordEngine/config.yml` and set the Discord Channel ID (`channelId`).
6.  Run `/cde reload` or restart the server.

---

## 💻 Commands & Permissions
The plugin uses its own command system that wraps CoinsEngine functionality to trigger notifications.

> ℹ️ **Note:** The main command is `/cde` (or your custom prefix defined in the config).

| Command | Aliases (Configurable) | Description | Permission Required          |
| :--- | :--- | :--- |:-----------------------------|
| `/cde pay <player> <amount> [reason]` | `pay`, `send` | Transfer currency to a player | None                         |
| `/cde add <player> <amount> [reason]` | `add`, `deposit` | Give currency to a player | `coinsengine.command.give`   |
| `/cde remove <player> <amount> [reason]` | `remove`, `withdraw` | Take currency from a player | `coinsengine.command.take`   |
| `/cde reload` | `reload` | Reload configuration | `coinsengine.command.reload` |

---

## ⚙️ Configuration

### `config.conf`
Main plugin settings. Define your channel ID, currency, and command aliases here.

<details>
<summary>📄 Show config.conf</summary>

```hocon
# Prefix for custom commands
prefix=mycustomprefix

# Whether to enable prefixed commands (e.g. /mycustomprefix pay)
prefixed-commands=false

# Whether to enable non-prefixed commands (e.g. /pay, /add, etc.)
non-prefixed-commands=true

# Currency ID from CoinsEngine (default is 'coins')
currency-id=coins

# Discord Text Channel ID where logs will be sent (CHANGE THIS!)
channel-id=0

# Command aliases
alias {
  pay=[
    pay,
    send
  ]
  add=[
    add,
    deposit
  ]
  remove=[
    remove,
    withdraw
  ]
}

# Since CoinsEngine 2.6.0 I need to log operations myself, so these are options
log {
  log-pay-to-file=true # 'operations.log' near config file
  log-pay-to-console=false
}

# Toggle notifications for specific actions
notification {
  pay=true
  add=true
  remove=true
}
```

</details>

### `language.conf`

Message configuration. You can use placeholders like `{from}`, `{to}`, `{target}`, `{amount}`, `{reason}`, and `{purpose}`.

<details>
<summary>📄 Show language.conf (Default English)</summary>

```hocon
something-went-wrong="Something went wrong, transaction cancelled"

# Player-to-player transaction messages
# Placeholders: {from}, {to}, {amount}, {currency}, {purpose}
transaction-message="`{from}` sent `{to}` {amount} {currency}"
transaction-message-purpose="`{from}` sent `{to}` {amount} {currency}. Purpose: {purpose}"

# Admin 'add' messages
# Placeholders: {source}, {target}, {amount}, {currency}, {reason}
add-message="`{target}` account was replenished with {amount} {currency}"
add-message-reason="`{target}` account was replenished with {amount} {currency}. Reason: {reason}"

# Admin 'remove' messages
# Placeholders: {source}, {target}, {amount}, {currency}, {reason}
remove-message="{amount} {currency} were withdrawn from `{target}`'s account"
remove-message-reason="`{amount} {currency} were withdrawn from `{target}`'s account. Reason: {reason}"

# Currency noun forms (mostly for slavic languge users)
# ONE: 1
# FEW: 2-4
# MANY: 11-14 and everything else
currency-forms {
  one=coin
  few=coins
  many=coins
}

```

</details>

---

<div align="center">
<p>Developed with ❤️ by <b>Animepdf</b></p>
</div>
