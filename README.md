# NASA APOD for Muzei

A Muzei art source that displays NASA's Astronomy Picture of the Day (APOD).

## Data source

The app loads the latest entries from NASA Science's APOD Basic JSON endpoint:

<https://science.nasa.gov/wp-json/wp/v2/apod-basic/>

Video entries are skipped because Muzei needs a still image. The artwork byline includes the APOD date and the image credit provided by NASA Science.

## Credits

- The original Muzei APOD source was written by Igor Almeida. Existing copyright notices and the GNU General Public License are retained.
- APOD content and image credits are provided by NASA and NASA Science. Image creators and other credit details are supplied per APOD entry.
- Muzei is the wallpaper platform this art source integrates with.

This project is not affiliated with or endorsed by NASA.

## Maintenance

The APOD integration has been updated to use NASA Science's APOD Basic JSON feed instead of scraping the legacy APOD webpage. The `api.nasa.gov` response currently returns a placeholder logo for the tested request, so this app does not use that endpoint.

## License

This project is distributed under the GNU General Public License, version 3 or (at your option) any later version. See [COPYING](./COPYING).
