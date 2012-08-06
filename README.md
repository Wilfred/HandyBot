# Handy

A simple IRC bot in Clojure.

Target features: remembers aliases, gets weather, tube status, execute
code in sandbox.

## Usage

First, create a private_settings.clj in src/Handy:

    $ cat src/Handy/private_settings.clj                                                                                                                                                                    [23:22:18]
    (ns Handy.private-settings)
    
    (def private-settings
      {:ideone-user "your-ideone-username"
       :ideone-password "your-ideone-password"})
    
Then just run the code with Leiningent

    $ lein run

## License

Copyright (C) 2012 Wilfred Hughes

Distributed under the GNU Public License v2 or later.
