# CatAddons

A Minecraft 1.21.10 Fabric mod to help debug plugins and servers.

## Requirements

- Java 21
- Minecraft 1.21.10
- Fabric Loader 0.18.4+

## Installation

1. Download the latest release from the releases page
2. Place the `.jar` file in your `mods` folder
3. Launch Minecraft with Fabric

## Building

```bash
./gradlew build
```

The built mod will be in `build/libs/`.

## Modules

### Render
- **Esp** - Highlight entities
- **FullBright** - Maximum gamma
- **Freecam** - Free camera movement
- **FreeLook** - Look around without moving
- **HidePlayers** - Hide other players
- **Watermark** - Display watermark overlay

### Movement
- **AutoSprint** - Automatically sprint

### Misc
- **Cape** - Equip custom capes

### Glitch/Utility
- **AnySign** - Enter any text into a sign
- **GuiUtils** - GUI buttons
- **MacroGuiSettings** - Macro GUI settings
- **Nick** - Hide your username
- **PacketLogger** - Log inventory packets
- **TpsCounter** - Display TPS

## Keybinds

- **Right Shift** - Toggle ClickGui
- **Z** - FreeLook
- **G** - Ghost Block
- **F7** - Packet Pause
- **F6** - Save GUI
- **V** - Restore GUI

## Commands

All commands are prefixed with `/cataddons`

- **dupe** - Method of the minute
- **macrogui** - Create macros for GUIS
- **module** - List all modules and settings
- **nbt** - Edit NBT data of an item
- **plugins** - List server plugins
- **reload** - Reload config
- **replaceblock** - Replace block the block you are looking at client side
- **restoreghosts** - Restore ghost blocks
- **sethand** - Set hand to an item clientside
- **toggle** - Toggle a module

## License

See COPYING file.


