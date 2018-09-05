# wmrc [![Version](https://img.shields.io/badge/version-v0.1.0--dev-orange.svg)](https://semver.org/spec/v2.0.0.html) [![License](https://img.shields.io/badge/license-ISC-blue.svg)](/LICENSE) [![Build status](https://travis-ci.org/Arkanosis/wmrc.svg?branch=master)](https://travis-ci.org/Arkanosis/wmrc)

**wmrc** is a an Android application to follow recent changes on Wikimedia projects in real time. Think [LiveRC](https://fr.wikipedia.org/wiki/Wikip%C3%A9dia:LiveRC/Documentation/fr), or [Huggle](https://en.wikipedia.org/wiki/Wikipedia:Huggle) but for mobile phones and tablets, built on modern backends ([EventStreams](https://wikitech.wikimedia.org/wiki/EventStreams), the [MediaWiki Action API](https://www.mediawiki.org/wiki/API:Main_page)) with modern technology ([Kotlin](https://kotlinlang.org/)).

## Current status

wmrc is currently under development for a first demo during the [2018 French WikiConvention](https://meta.wikimedia.org/wiki/WikiConvention_francophone/2018), which will take place in Grenoble, France, from the 5th to the 7th of October 2018.

It's not ready for use and not packaged at all for the moment.

## Features

wmrc features:
 * real time monitoring of recent changes (much faster than LiveRC);
 * patrol and revert buttons;
 * filtering based on the user group;
 * filtering based on the wiki.

## Compiling and installing

Once ready for mainstream usage, wmrc will be distributed for free through [f-droid](https://f-droid.org/), and *maybe* through [Google Play](https://play.google.com/store) as well.

Until then, clone this repository and use:
 * `make build` to compile;
 * `make install` to deploy on the mobile device,
 * `make run` to run the previously installed app on the mobile device.
 * `make experiment` to run experiments (ie. not the Android application, but a commandline program) on the development device.

## Contributing and reporting bugs

Contributions are welcome through [GitHub pull requests](https://github.com/Arkanosis/wmrc/pulls).

Please report bugs and feature requests on [GitHub issues](https://github.com/Arkanosis/wmrc/issues).

## License

wmrc is copyright (C) 2018 Jérémie Roquet <jroquet@arkanosis.net> and licensed under the ISC license.
