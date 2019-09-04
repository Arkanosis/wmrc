<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/c8/Wmrc_logo.svg/1182px-Wmrc_logo.svg.png" align="right" width="120px">

# wmrc [![Version](https://img.shields.io/badge/version-v0.1.0--dev-orange.svg)](https://semver.org/spec/v2.0.0.html) [![License](https://img.shields.io/badge/license-ISC-blue.svg)](/LICENSE) [![Build status](https://travis-ci.org/Arkanosis/wmrc.svg?branch=master)](https://travis-ci.org/Arkanosis/wmrc)

**wmrc** is an Android application to follow recent changes on Wikimedia projects in real time. Think [LiveRC](https://fr.wikipedia.org/wiki/Wikip%C3%A9dia:LiveRC/Documentation/fr) or [Huggle](https://en.wikipedia.org/wiki/Wikipedia:Huggle) but for mobile phones and tablets, built on modern backends ([EventStreams](https://wikitech.wikimedia.org/wiki/EventStreams), the [MediaWiki Action API](https://www.mediawiki.org/wiki/API:Main_page)) with modern technology ([Kotlin](https://kotlinlang.org/), [OkHttp](http://square.github.io/okhttp/)).

## Current status

wmrc is currently under development after a first presentation during the [2018 French WikiConvention](https://meta.wikimedia.org/wiki/WikiConvention_francophone/2018), in Grenoble, on the 6th of October 2018. The next demo will happen during the [2019 French WikiConvention](https://meta.wikimedia.org/wiki/WikiConvention_francophone/2019), in Bruxelles, on the 7th of September 2019.

## Features

<img src="https://upload.wikimedia.org/wikipedia/commons/8/86/Wmrc_0.1.0-dev.1.png" align="right" width="250px">

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

## Installation

wmrc is not yet ready for mainstream usage. However, if you want to test it now and don't mind a minimal feature set a few occasional crashes here and there, you can install it through [F-Droid](https://f-droid.org/), by adding the following repository:

 * Address: https://apk.arkanosis.net
 * Fingerprint: 655955660F34A4DB7CC2B30D96B8B546759D6AEABC83D34AE682B73A7C24FE62
 
You can also add this F-Droid repository by scanning this QR code:

![wmrc F-Droid repository QR code](/images/fdroid-qr.png?raw=true)
 
Alternatively, you can [download the apk](https://apk.arkanosis.net/fdroid/repo/wmrc.apk) ([PGP signature](https://apk.arkanosis.net/fdroid/repo/wmrc.apk.asc)) and install it manually (but you will have to install updates manually as well).

Once ready for mainstream usage, wmrc will be distributed for free through the main [F-Droid](https://f-droid.org/) repository, and *maybe* through [Google Play](https://play.google.com/store) as well.

## Compiling

If you want to compile wmrc yourself, clone this repository and use:
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
