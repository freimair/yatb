Yet some other Terminate Buttons for Eclipse
====

## Why

Sometimes there are clean up tasks when shutting down certain processes.
Eclipse however doesn't signal a normal shutdown and hence doesn't trigger these tasks.
That's why this new button here. It will signal the process a normal shutdown request with `kill -s <pid>` internally for Linux-like and OSX platforms.

## Installation

1. Download the dist folder in this repository
2. in Eclipse open Help -> Install new Software... -> Add... -> Local...
3. Select the dist folder
4. Select the plugin and proceed like normal

Based on the original YaTB plugin by Nick Tan.
