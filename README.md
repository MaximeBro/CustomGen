# CustomGen
CustomGen is a plugin which adds a custom ores generator represented by a dispenser.
The generator can be obtained with its crafting recipe and will produce the ores in its inventory.
You can create different generators with config files - see [Config tutorial](https://www.youtube.com) - for more information.
Your generators data is saved under json files in the config directory and is restored every time you restart your server.

## Commands

- *Displays the plugin menu (available commands)*
```properties
/customgen help
```
<br/>

- *Displays the available generators names*
```properties
/customgen list | gen | generators
```
<br/>

- *Displays the given generation's values*
```properties
/customgen generation <name>
```
<br/>

- *Gives the player the amount of chosen generator*
```properties
/customgen give <name> <amount>
```
<br/>

- *Gets the number of running tasks (placed and working generators on the server)*
```properties
/customgen tasks
```
<br/>

- *Gets the plugin's build's version*
```properties
/customgen version
```

# Permissions

You only need one permission to use all the plugin's commands : `customgen.use`


# Crafting Recipe
Coming soon...

## Download & Releases
Coming soon...

# Disclaimer
DO NOT UPDATE YOUR `generators.json` FILE MANUALLY !
<br/>If you encounter any bug just delete the file and reload your server plugins !

# License
All Rights Reserved unless otherwise explicitly stated.