Simple Android App that is stores logs of baby feeds.
If you have a newborn and are not allowed much coding - it is a perfect project to fork.
Just tell your wife it is all for a baby!

This project implements following Android elements:

Data/persistence-perspective:
 - ContentProvider and ContentResolver for decoupling
 - SQLiteDb in the ContentProvider
 - CurosrLoader for ContentProvider
 - Shared Prerences
 - Saved state
 - (no cloud as of yet)

Layout-wise the project is uses:
 - RecyclerView with different ViewHolders for entry, dates and summaries.
 - Fragments
 - uses Settings AppCompat

From task perspectives:
 - Uses multiple Intents for various activities

** This app is currently missing: **
 - Getting data from the web.
 - Services (reminders about next feds)
 - Support for cloud.


