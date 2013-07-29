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

Handybot also uses a sandbox to evaluate code using clojail. You'll
need to set up a basic permissions file in `~/.java.policy`:

    grant {
      permission java.security.AllPermission;
    };

Since this is very open, you'll want to run HandyBot as its own user,
or this configuration may make Java applets run with very very open
permissions.
    
Then just run the code with Leiningen

    $ lein run
    
## Third party APIs

HandyBot uses [ideone.com](http://ideone.com) by
[Sphere Research Labs](http://sphere-research.com) for sandboxed code
execution. It also uses the Last.fm API for music recommendations.

## License

Copyright (C) 2012 Wilfred Hughes

Distributed under the GNU Public License v2 or later.

## Todo

* Add full sandbox execution using clojail, and allow users to define custom commands
* Fix the timestamp plugin which gets
* %seen to see when someone last logged in
* %reminder to set timers to remind users of things
* Respond to direct messages
