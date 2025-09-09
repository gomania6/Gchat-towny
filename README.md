# gChat-Towny

**gChat-Towny** is an addon for the [gChat](https://modrinth.com/plugin/gchat) plugin that integrates support for [Towny](https://www.spigotmc.org/resources/towny-advanced.72606/) and displays player town and nation tags directly in chat. The plugin is also compatible with [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for flexible placeholder usage.

---

## Features

* Displays **town** and **nation tags** in chat.
* Supports **HEX colors** for tags (e.g., `&#FF0000` → red).
* Integration with **PlaceholderAPI**:

  * `%gchat_towny%` — full tag: `[Nation | Town]`
  * `%gchat_townytown%` — town tag: `[Town]`
  * `%gchat_townynation%` — nation tag: `[Nation]`
* Fully compatible with **gChat**: tags are automatically applied in chat format.
* Checks for Towny and gChat — disables itself safely if dependencies are missing.
* **Debug mode** for checking tags and configuration.
* Configurable via `towny-gChat.yml`.

---

## Installation

1. Download [gChat-Towny.jar](https://github.com/YourRepo/gChat-Towny/releases/latest).
2. Place the file in your server's `plugins` folder.
3. Make sure you have installed:

   * [gChat](https://modrinth.com/plugin/gchat)
   * [Towny](https://www.spigotmc.org/resources/towny-advanced.72606/)
   * (optional) [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
4. Start the server. The plugin will create a configuration file at `plugins/gChat/towny-gChat.yml`.

---

## Configuration

Example `towny-gChat.yml`:

```yaml
tag_formats:
  town: '&f[&a%s&f] '
  nation: '&f[&#FF5656%s&f] '
  both: '&f[&c%s&f | &a%s&f] '
use_hex_colors: true
debug: false
```

* **tag\_formats.town** — format for town tag.
* **tag\_formats.nation** — format for nation tag.
* **tag\_formats.both** — format for both town and nation.
* **use\_hex\_colors** — enables HEX colors in tags.
* **debug** — enables console error output for debugging.

---

## PlaceholderAPI Placeholders

| Placeholder           | Description                    |
| --------------------- | ------------------------------ |
| `%gchat_towny%`       | Full tag: `[Nation / Town]`    |
| `%gchat_townytown%`   | Town tag `[Town]`              |
| `%gchat_townynation%` | Nation tag `[Nation]`          |

---

## Compatibility

* **gChat 1.x**
* **Towny 0.101+**
* **Minecraft 1.21+**
* **PlaceholderAPI (optional)**
