# threesixfive

A simple Instagram client that generates my 365 days project gallery

## Installation

Download from https://github.com/credmp/threesixfive.

## Usage

Given the config.edn in the current directory you can run the application as follows:

    $ lein run

## Options

Options are set in a config file called config.edn. This file will be overwritten
each time the application runs in order to store the last id taken from Instagram.

    {
     :client_id "INSTAGRAM_CLIENT_ID",
     :userid INSTAGRAM_USER_ID
     :output {:images "IMAGE_DIR",
              :posts "JEKYLL_POST_DIR"}
    }


## Examples

My gallery: https://www.wiersma.org/threesixfive/
My instagram: https://www.instagram.com/credmp/

### Bugs

Probably plenty as it is very specific to my setup.

## License

Copyright © 2015 Arjen Wiersma

Distributed under the MIT License.
