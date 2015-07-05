**TreeGrowth** Is a minecraft mod for tree reproduction. When a chunk is processed, TreeGrowth looks for existing trees. 
Every tree is then processed on its own. Based on its surroundings and characteristics new trees of the same type are spawned around it.

## Authors

- **xeedness** - Creator

## How it works

### Scheduling
The scheduling is based on ingame ticks. TreeGrowth manages a list of loaded chunks. Every loaded chunk should be processed at a specific rate. The main scheduling routine looks for the next chunk, that needs processing. If the processing is done fast, another chunk can be processed until a maximum processing time is met or no more chunks need processing. 

### Tree Configuration
A tree type can be identified by its trunk size, wood log block id and leaves block id. To work with different mods these characteristics have to be known to **TreeGrowth**. Additionally different characteristics can be configured. For example fertility is a value between 0 and 1 and is used to determined if a tree is randomly planted. 

### Tree Detection
Trees are detected by matching its structure to a stencil. Different stencils can be implemented to not only fit vanilla trees, but also every possible modded tree structure. 

### Tree Processing
Every detected tree gets processed for growth calculations. In a variable area around the tree optimal positions for spawning trees are getting calculated. This is done by counting the fertile positions around the viewed position. If a random number exceeds the fertility of the detected tree a new sapling is spawned.



 