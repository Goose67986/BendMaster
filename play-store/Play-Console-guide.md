# BendPro 360 Play Console testing handoff

Build: versionName 1.0, versionCode 18. Package: com.bendmasterpro.app.
The release bundle was built with bundleRelease and lintRelease. Its upload-key signature was verified locally. Device and engineering validation are still required before production.

1. In Play Console, create an app named BendPro 360, English (United States), App, Free. Use nngbusiness@gmail.com for support. The longer app title does not fit the 30-character listing limit.
2. Under Testing > Internal testing, create a release and enable Play App Signing. For a new app, choose a Google-generated app signing key. Upload BendPro360-v1.0.aab. If this package already exists in your account with another upload key, stop and use its registered key instead.
3. Use store-listing.txt for the listing. Category: Tools. Supply the real icon, a 1024×500 feature graphic and at least two actual phone screenshots. Screenshots are not included in this kit because no emulator/device capture was available.
4. Publish privacy-policy.md at a public web URL and enter that URL in Play Console. A public repository copy is also supplied. Confirm the privacy text before submission.
5. App access: all functionality available without login. Ads: No. In-app purchases: No for version 1.0. Data safety draft: no developer collection or sharing based on the current offline source. Review Android backup handling and the final bundled SDKs when completing the form. Complete content rating from the actual app; do not invent a rating. Suggested intended audience: adults using fabrication tools; confirm your intended audience.
6. Add internal testers and share Play Console's opt-in link after rolling out the test release. This preparation has not created or submitted a Play Console release.
7. Before production, verify tonnage against a known reliable reference and actual shop results, angle conventions, flat lengths, invalid inputs, material deletion, die limits, keyboard layout, and screen sizes. This is a testing build, not a certified engineering tool.
8. Personal accounts created after November 13, 2023 may need 12 opted-in closed testers for 14 continuous days before applying for production access. Follow your account dashboard's requirements.

Keep the PRIVATE signing backup private and retain it for future uploads. The Google Play app signing key will differ from the old debug APK key; a Play installation may require uninstalling the debug copy, which can remove local materials. Save custom material values before switching.

Official references:
https://developer.android.com/studio/publish/app-signing
https://support.google.com/googleplay/android-developer/answer/9859152
https://support.google.com/googleplay/android-developer/answer/14151465
https://support.google.com/googleplay/android-developer/answer/10787469
