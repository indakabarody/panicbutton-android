Panic Button Application
=======
An application to get help from a hospital or health organization.

Build with Android Studio
-------------------------
Panic Button needs access to its API. To connect to the API, please make changes to
`public static final String URL` constant on each Java Activity to the IP or url
where the API is placed.

Where to get the API?
-------------------------
The API is bundled with an administrator web. The web source can be found here :
https://github.com/indakabarody/panicbutton-web

After get the web source, please install it to the desired location. You can also host it.
Then make sure the IP or url is correct, and make changes to `public static final String URL`
constant on each Java Activity.

Telegram Notifications
-------------------------
To make a telegram notifications, make sure you already have a Telegram Bot and Channel. Then
on the web API please make changes to `$url` property to your Telegram API and Telegram Channel ID.