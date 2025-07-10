# SkillLink 🎯

SkillLink is a modern Android application built with Jetpack Compose and Firebase that connects users based on the skills they want to **learn** and **teach**. 
It includes features such as real-time chat, secure video calls using WebRTC, profile management, and location-based skill matching.

## 🚀 Features

- 🔐 **Authentication** with Firebase (Email/Password)
- 👤 **User Profiles** with editable fields: name, role, location, availability, skills to teach and learn
- 🔍 **Skill Matching**: Find people nearby or globally to learn from or teach
- 💬 **Real-time Chat** between users using Firebase Firestore
- 📹 **Video Calling** with WebRTC and Firebase signaling (ICE Candidates, SDP, Call sessions)
- 🗺️ **Location Sharing** (for localized skill-based discovery)
- 📁 **Firestore Rules** to protect chat, user, and call data
- ✨ **Modern UI** built using Jetpack Compose
- 🔄 **Role-Based Navigation** for Learners and Providers

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Firebase Services**:
  - Authentication
  - Firestore
  - Firebase Storage
- **Video Call**: WebRTC integrated with Firebase
- **Other Libraries**:
  - Accompanist (for permissions and loading animations)
  - Coil (for image loading)
  - Coroutines & Flow (for async operations)
