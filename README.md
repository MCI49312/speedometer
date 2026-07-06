# ⏱️ Speedometer App

A clean, minimalist, GPS-based Android application to accurately track your pace. Whether you are riding a mountain bike, commuting on an e-scooter, or just want to track your driving speed, this app records everything purely and simply.

## ✨ Features

* **Real-Time Tracking:** Highly accurate speed measurement using Google's Fused Location Provider.
* **Dual Units:** Seamlessly switch between Metric (km/h) and Imperial (mph) units.
* **Session & Total Odometer:** Tracks the distance of your current trip ("This session") as well as the all-time total distance.
* **Detailed Statistics:** Calculates your maximum speed and average speed for the current session.
* **Bilingual Support:** Fully supports English and Hungarian languages.
* **Modern UI:** Minimalist light/dark mode design with edge-to-edge navigation bar optimization.
* **No Ads, No Tracking:** 100% open-source, offline-capable, and free of advertisements.

## 🚀 Download & Installation

You can download the latest stable release directly from the GitHub repository.

1. Go to the [Releases](https://github.com/MCI49312/speedometer/releases/) page.
2. Download the `speedometer_vX.apk` file to your Android device.
3. Open the file and follow the on-screen instructions to install. *(Note: You may need to enable "Install from unknown sources" in your Android settings).*

## 🛠️ Development & Building

This application is built natively for Android using **Kotlin** and **Android Studio**.

* **Package Name:** `pixela16.space.speedometer`
* **Minimum SDK:** 24 (Android 7.0) 
* **Target SDK:** 34 (Android 14)

### How to build from source

1. Clone this repository:
   ```bash
   git clone https://github.com/MCI49312/speedometer.git
2. Open the project in **Android Studio**.
3. Allow Gradle to sync and download the necessary dependencies.
4. Connect your Android device or start an emulator.
5. Click **Run** to build and deploy the application.

## 🔒 Permissions Required

* `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`: Required to calculate your speed and distance using GPS hardware. 
* *The app does not collect, store, or share your location data externally. All data is saved locally on your device.*

## 📄 License

This project is open-source and available under the [MIT License](LICENSE). You are free to copy, modify, and use it in your own projects.
   
This is my first project. Gemini helped me with some parts of the code.
