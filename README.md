# Commands
Required parameters have &lt;angle brackets&gt;. Optional parameters have [square brackets].

|Command|Aliases|Usage|Description|
|---|---|---|---|
|/fastcraft|/fc|/fc &lt;craft&#124;toggle&gt;|FastCraft+ commands.|
|/fc toggle||/fc toggle [on&#124;off&#124;toggle] [player]|Enable or disable the FastCraft+ interface.|
|/fc craft|/craft<br>/cr<br>/workbench<br>/wb|/fc craft [workbench&#124;fastcraft]|Open the FastCraft+ interface, or a workbench.|
|/fastcraftadmin|/fcadmin<br>/fca|/fca &lt;reload&#124;debug&gt;|FastCraft+ admin commands.|
|/fca reload||/fca reload|Reload FastCraft+ configurations.|
|/fca debug||/fca debug|Output debug info. Should be used when reporting issues.|

# Permissions
|Default|Permission|Use|
|---|---|---|
|op|fastcraft.*|All FastCraft+ permissions|
|op|fastcraft.use|Permission to use FastCraft+ for crafting|
|false|fastcraft.command.*|Permission to use all non-admin commands|
|true|fastcraft.command.toggle|/fastcraft toggle [on/off/toggle]|
|op|fastcraft.command.toggle.other|/fastcraft toggle [on/off/toggle] [player]|
|false|fastcraft.command.craft|/fastcraft craft|
|op|fastcraft.admin.*|Permission to use all admin commands|
|op|fastcraft.admin.reload|/fastcraftadmin reload|
