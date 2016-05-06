# Commands
Required parameters have &lt;angle brackets&gt;. Optional parameters have [square brackets].

<table width=100%>
  <tr>
    <th>Command</th>
    <th>Aliases</th>
    <th width=260px>Usage</th>
    <th>Description</th>
  </tr>
  <tr>
    <td>/fastcraft</td>
    <td>/fc</td>
    <td>/fc &lt;craft|toggle&gt;</td>
    <td>FastCraft+ commands.</td>
  </tr>
  <tr>
    <td>/fc toggle</td>
    <td></td>
    <td>/fc toggle [on|off|toggle] [player]</td>
    <td>Enable or disable the FastCraft+ interface.</td>
  </tr>
  <tr>
    <td>/fc craft</td>
    <td>
      /craft<br>
      /cr<br>
      /workbench<br>
      /wb
    </td>
    <td>/craft [workbench|fastcraft]</td>
    <td>Open the FastCraft+ interface, or a workbench.</td>
  </tr>
  <tr>
    <td>/fastcraftadmin</td>
    <td>
      /fcadmin<br>
      /fca
    </td>
    <td>/fca &lt;reload|debug&gt;</td>
    <td>FastCraft+ admin commands.</td>
  </tr>
  <tr>
    <td>/fca reload</td>
    <td></td>
    <td>/fca reload</td>
    <td>Reload FastCraft+ config files.</td>
  </tr>
  <tr>
    <td>/fca debug</td>
    <td></td>
    <td>/fca debug</td>
    <td>Output debug info. Should be used when reporting issues. Can only be used from the console.</td>
  </tr>
</table>

# Permissions
<table width=100%>
  <tr>
    <th>Default</th>
    <th width=290px>Permission</th>
    <th>Use</th>
  </tr>
  <tr>
    <td>true</td>
    <td>fastcraft.use</td>
    <td>Permission to use FastCraft+ for crafting</td>
  </tr>
  <tr>
    <td>false</td>
    <td>fastcraft.command.*</td>
    <td>Permission to use all non-admin commands</td>
  </tr>
  <tr>
    <td>true</td>
    <td>fastcraft.command.toggle</td>
    <td>Use the toggle command</td>
  </tr>
  <tr>
    <td>op</td>
    <td>fastcraft.command.toggle.other</td>
    <td>Use the toggle command on another player</td>
  </tr>
  <tr>
    <td>false</td>
    <td>fastcraft.command.craft</td>
    <td>/fastcraft craft</td>
  </tr>
  <tr>
    <td>op</td>
    <td>fastcraft.admin.*</td>
    <td>Permission to use all admin commands</td>
  </tr>
  <tr>
    <td>op</td>
    <td>fastcraft.admin.reload</td>
    <td>/fastcraftadmin reload</td>
  </tr>
  <tr>
    <td>false</td>
    <td>fastcraft.blacklist.*</td>
    <td>All blacklist permissions.</td>
  </tr>
  <tr>
    <td>false</td>
    <td>
      fastcraft.blacklist.hash.&lt;entry&gt;<br>
      fastcraft.blacklist.result.&lt;entry&gt;<br>
      fastcraft.blacklist.ingredient.&lt;entry&gt;<br>
    </td>
    <td>
      Bypasses entries in the blacklist config. This permission will have
      different functionality, depending on whether blacklist.yml is being
      used as a blacklist or a whitelist:<br>
      <b>Blacklist</b>: Allow a player to craft blacklisted recipes.<br>
      <b>Whitelist</b>: Disallow a player from crafting whitelisted recipes.
    </td>
  </tr>
</table>
