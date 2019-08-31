<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/c8/Wmrc_logo.svg/1182px-Wmrc_logo.svg.png" align="right" width="120">

# wmrc [![Version](https://img.shields.io/badge/version-v0.1.0--dev-orange.svg)](https://semver.org/spec/v2.0.0.html) [![License](https://img.shields.io/badge/license-ISC-blue.svg)](/LICENSE) [![Build status](https://travis-ci.org/Arkanosis/wmrc.svg?branch=master)](https://travis-ci.org/Arkanosis/wmrc)

**wmrc** is an Android application to follow recent changes on Wikimedia projects in real time. Think [LiveRC](https://fr.wikipedia.org/wiki/Wikip%C3%A9dia:LiveRC/Documentation/fr) or [Huggle](https://en.wikipedia.org/wiki/Wikipedia:Huggle) but for mobile phones and tablets, built on modern backends ([EventStreams](https://wikitech.wikimedia.org/wiki/EventStreams), the [MediaWiki Action API](https://www.mediawiki.org/wiki/API:Main_page)) with modern technology ([Kotlin](https://kotlinlang.org/), [OkHttp](http://square.github.io/okhttp/)).

## Current status

wmrc is currently under development after a first presentation during the [2018 French WikiConvention](https://meta.wikimedia.org/wiki/WikiConvention_francophone/2018), in Grenoble, on the 6th of October 2018. The next demo will happen during the [2019 French WikiConvention](https://meta.wikimedia.org/wiki/WikiConvention_francophone/2019), in Bruxelles, on the 7th of September 2019.

It's not ready for use and not packaged at all for the moment.

## Features

wmrc features:
 * real time monitoring of recent changes (much faster than LiveRC);
 * patrol and revert buttons;
 * filtering based on the user group;
 * filtering based on the wiki
 
Additionaly, wmrc is designed to take into account the following properties of mobile phones and tablets:
 * small screen (too little to use traditional tools);
 * tactile screen (less precise and versatile than the mouse + keyboard combo);
 * mobile internet connection (with frequent loss of signal and occasional change of IP address).
 
wmrc is designed with internationalization in mind and already available in the following languages:
 * English,
 * French.

## Compiling and installing

Once ready for mainstream usage, wmrc will be distributed for free through [F-Droid](https://f-droid.org/), and *maybe* through [Google Play](https://play.google.com/store) as well.

Until then, clone this repository and use:
 * `make build` to compile;
 * `make sign` to sign the apk (mandatory to use on non-developer devices);
 * `make install` to deploy on the mobile device;
 * `make run` to run the previously installed app on the mobile device;
 * `make experiment` to run experiments (ie. not the Android application, but a commandline program) on the development device.

## Contributing and reporting bugs

Contributions (including translations) are welcome through [GitHub pull requests](https://github.com/Arkanosis/wmrc/pulls).

Please report bugs and request new features on [GitHub issues](https://github.com/Arkanosis/wmrc/issues).

## License

wmrc is copyright (C) 2018-2019 Jérémie Roquet <jroquet@arkanosis.net> and licensed under the ISC license.
