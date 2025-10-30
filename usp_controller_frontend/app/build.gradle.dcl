androidApplication {
    namespace = "org.example.app"

    dependencies {
        implementation("org.apache.commons:commons-text:1.11.0")
        implementation(project(":utilities"))

        // AndroidX UI
        implementation("androidx.appcompat:appcompat:1.7.0")
        implementation("com.google.android.material:material:1.12.0")
        implementation("androidx.constraintlayout:constraintlayout:2.2.0")
        implementation("androidx.recyclerview:recyclerview:1.3.2")
        implementation("androidx.viewpager2:viewpager2:1.1.0")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")

        // Networking
        implementation("com.squareup.okhttp3:okhttp:4.12.0")

        // MQTT (Eclipse Paho)
        implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    }
}
