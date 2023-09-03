# Introduction

Voipfuture management has just decided to diversify our business and enter the gaming industry which is soaring to new heights lately.

It was decided to start out with a rather basic but popular game that is guaranteed to rake in big $$$ once we start adding micro-transactions and vanity items.
The game will be called... JMinesweeper !

# General Requirements

- client-server architecture
- ~~to thwart competitors~~ for performance reasons a proprietary binary network protocol will be used for communication between clients and the server
- we're kind of low on development resources so instead of the creating a VR experience using the latest version of Unity we took inspiration from Dwarf Fortress  and decided to build a ANSI terminal version instead.

# Networking Protocol

The network protocol is TCP-based and uses an 8-bit packet type ID and (assuming the packet type supports additional payload data)
, a 32-bit payload length field in big-endian byte order followed by the payload bytes.

| Offset | Length in bytes | Description                   |
|--------|-----------------|-------------------------------|
| 0      | 1               | Packet type ID                |
| 1      | 4               | (optional) payload byte count |
| 5      | variable        | (optional) payload data       |

The following packet types are being used:

| Type ID | Description                                            | Has payload? |
|---------|--------------------------------------------------------|--------------|
| 1       | Move cursor left by one cell                           | No           |
| 2       | Move cursor right by one cell                          | No           |
| 3       | Move cursor up by one cell                             | No           |
| 4       | Move cursor down by one cell                           | No           |
| 5       | Reveal the cell below the current cursor position      | No           |
| 6       | Toggle marking the cell at the current cursor position | No           |
| 7       | Start a new game                                       | Yes          |
| 8       | Quit                                                   | No           |
| 9       | Current screen content                                 | Yes          |
| 10      | Toggle debug mode                                      | No           |

The protocol uses TCP port 9999. When receiving a valid packet, the server will execute the associated action and then respond with a type 9 (Current Screen Content) packet that holds the screen content.
This does **not** apply to packet type 9 (Quit) though as the server will immediately close the connection after receiving it.
If the server receives a command that is not compatible with the current game state (like trying to move the cursor although the player has lost the game), it
the action will be ignored and the server will simply respond with the current screen content.

```mermaid
graph TD;
    A-->B;
    A-->C;
    B-->D;
    C-->D;
```

# Client Requirements

- needs to use the 

# Server Requirements
