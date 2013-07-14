# HandyBot

A simple IRC bot in Clojure. HandyBot is inspired by Emacs, so aims to
by maximally self-documenting and introspectable.

Target features: remembers aliases, gets weather, tube status, execute
code in sandbox.

## Usage

First, create a private_settings.clj in src/Handy:

    $ cat src/Handy/private_settings.clj                                                                                                                                                                    [23:22:18]
    (ns Handy.private-settings)
    
    (def private-settings
      {:ideone-user "your-ideone-username"
       :ideone-password "your-ideone-password"})
    
Then just run the code with Leiningen

    $ lein run
    
## Third party APIs

HandyBot uses [ideone.com](http://ideone.com) by
[Sphere Research Labs](http://sphere-research.com) for sandboxed code
execution. It also uses the Last.fm API for music recommendations.

## License

Copyright (C) 2012 Wilfred Hughes

Distributed under the GNU Public License v2 or later.
