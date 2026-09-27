# CodeAI by Theshan - Android App

## 🤖 Project Overview

This is a complete Android AI Assistant app built with:
- **Language:** Kotlin
- **AI Backend:** Google Gemini 1.5 Flash API
- **UI:** Material Design 3 with dark purple theme
- **Architecture:** MVVM (Model-View-ViewModel)

---

## 📁 Project Structure

```
CodeAI-by-Theshan/
├── app/
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/theshan/codeai/
│       │   ├── data/
│       │   │   ├── api/
│       │   │   │   ├── GeminiApiService.kt   ← API interface
│       │   │   │   └── GeminiModels.kt       ← Request/Response models
│       │   │   ├── network/
│       │   │   │   └── NetworkModule.kt      ← Retrofit setup
│       │   │   └── repository/
│       │   │       └── GeminiRepository.kt   ← API calls + prompts
│       │   └── ui/
│       │       ├── MainActivity.kt           ← Main screen
│       │       ├── MainViewModel.kt          ← Business logic
│       │       ├── ApiKeyActivity.kt         ← API key setup
│       │       ├── ResultActivity.kt         ← Full code viewer
│       │       └── adapter/
│       │           └── ChatAdapter.kt        ← Chat RecyclerView
│       └── res/
│           ├── layout/
│           │   ├── activity_main.xml
│           │   ├── activity_api_key.xml
│           │   ├── activity_result.xml
│           │   └── item_chat_message.xml
│           ├── values/
│           │   ├── strings.xml
│           │   └── themes.xml
│           ├── drawable/
│           │   ├── bubble_user.xml
│           │   ├── bubble_ai.xml
│           │   └── btn_icon_bg.xml
│           └── color/
│               └── chip_bg_selector.xml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
    └── libs.versions.toml
```

---

## 🚀 How to Open in Android Studio

1. Open **Android Studio**
2. Click **"Open"**
3. Navigate to: `C:\Users\thesh\.gemini\antigravity\scratch\CodeAI-by-Theshan`
4. Click **OK**
5. Wait for Gradle sync to finish

---

## 🔑 Getting Your Free Gemini API Key

1. Go to: **https://aistudio.google.com/app/apikey**
2. Sign in with Google
3. Click **"Create API Key"**
4. Copy the key
5. Paste it in the app when it asks!

> **Free tier:** 1,500 requests/day - plenty for personal use!

---

## ✨ App Features

| Feature | Description |
|---------|------------|
| 🤖 AI Chat | Chat with Gemini AI |
| 📱 Android Mode | Generate Android Kotlin code |
| 🌐 Web Mode | Generate HTML/CSS/JS |
| 🐍 Python Mode | Generate Python scripts |
| 💬 Chat Mode | General AI conversation |
| 📋 Copy Code | One-tap code copying |
| 📤 Share Code | Share generated code |
| ⚙️ Settings | Change API key, clear chat |

---

## 🎨 App Appearance

- **Dark theme** with purple gradient (#7B68EE)
- Modern chat bubbles (user = purple, AI = dark)
- Material Design 3 components
- Smooth mode switching chips

---

## ⚠️ Need to Add (Launcher Icon)

You need to add `ic_launcher.png` to the `drawable` folder.
Or replace `@drawable/ic_launcher` in AndroidManifest with `@mipmap/ic_launcher` 
(Android Studio will generate this automatically when you create a new project and copy code).

**Quick fix:** In Android Studio, right-click `res` → New → Image Asset → configure your icon.
