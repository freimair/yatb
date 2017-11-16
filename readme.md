Yet another Terminate Button (YaTB) for Eclipse
====

## Why

Sometimes there are clean up tasks when shutting down certain processes.
Eclipse however doesn't signal a normal shutdown and hence doesn't trigger these tasks.
That's why this new button here. It will signal the process a normal shutdown request with `kill -s <pid>` internally for Linux-like and OSX platforms.

## Installation

Update site: https://github.com/missedone/yatb/raw/master/dist
