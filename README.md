# Minecraft Status Query
Get information from any unmodded Minecraft server with the built in query protocol.

## Usage
import query.*;
MCQuery mcQuery = new MCQuery("my.server.domain", 25565);
QueryResponse response = mcQuery.basicStat();
int players = response.getOnlinePlayers();
(...)

Examples can be found in src/examples/

### Note:
Remember that query must be enabled in your server.properties file (query=true).
