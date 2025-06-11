rm -rf ./app/build/outputs/apk/debug/app-debug.apk 
./gradlew assembleDebug  
adb install ./app/build/outputs/apk/debug/app-debug.apk 
adb shell am start -n com.java.threathawk/com.java.threathawk.SplashActivity
