# SCEventFishing 🎣

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue.svg)](https://developer.android.com/jetpack/compose)
[![Material3](https://img.shields.io/badge/Design-Material%203-red.svg)](https://m3.material.io/)
[![MinSDK](https://img.shields.io/badge/Min%20SDK-24-orange.svg)](https://developer.android.com/about/versions/nougat)

Утилита для инспекции, извлечения и экспорта ивентовых ассетов (`.sc`, `.jpg`, `.png`) из игр компании Supercell (Brawl Stars, Clash Royale) и их модификаций.

An Android application designed to inspect, extract, and manage event assets (`.sc`, `.jpg`, `.png`) from Supercell games (Brawl Stars, Clash Royale) and custom clients.

---

## 🌟 Основные возможности / Key Features

- ⚙️ **Три режима работы (Operation Modes)**:
  - **Regular Mode**: Требует Root-права (`su`). Прямой поиск файла событий в директории приложения `/data/data/<package_name>/cache/events/`.
  - **Compatibility Mode**: Работает без Root-прав через Storage Access Framework (SAF) для совместимости с модами (например, SCHunt).
  - **Demo Mode**: Ознакомительный режим для тестирования возможностей UI без подключения к играм.

- 📦 **Поддержка игр и модификаций**:
  - **Brawl Stars**:
    - Официальный клиент (`com.supercell.brawlstars`)
    - BSD Suitcase (`bsd.suitcase.release`)
    - Magic's Brawl (`com.magics.brawl`)
    - Tencent China (`com.tencent.tmgp.supercell.brawlstars`)
  - **Clash Royale**:
    - Официальный клиент (`com.supercell.clashroyale`)
    - Tencent China (`com.tencent.tmgp.supercell.clashroyale`)

- 📋 **Удобная работа с ассетами**:
  - Копирование ссылок на CDN ивентов (`https://event-assets.brawlstars.com/` и др.) или точных имён файлов в буфер обмена в 1 клик.
  - Пакетное копирование ("Copy All") и пакетный экспорт ("Extract All") всех найденных файлов.
  - Сохранение файлов в пользовательскую папку экспорта или по умолчанию в `Download/events/`.
  - Цветовая индикация обработки (красный — новый файл, синий — уже скопирован/извлечен).

- 📱 **Адаптивный и современный интерфейс**:
  - Написан на **Jetpack Compose** с использованием **Material Design 3**.
  - Полная адаптивность: на смартфонах и на планшетах (с двухпанельным разделением в ландшафтной ориентации при ширине экрана >= 600dp).
  - Поддержка светлой, тёмной и системной темы оформления.
  - Мультиязычность (Русский и English).

---

## 🛠️ Технологический стек / Tech Stack

- **Язык**: Kotlin
- **UI Фреймворк**: Jetpack Compose, Material 3, Navigation Drawer
- **Архитектурные компоненты**: AndroidX Lifecycle, ViewModel, SharedPreferences, SAF (Storage Access Framework)
- **Root Shell**: Собственный интерактивный контроллер `su` процесса (`RootShell.kt`)
- **Вспомогательные библиотеки**: Accompanist DrawablePainter, AndroidX AppCompat
- **Целевая версия**: SDK 34 (Android 14) / Compile SDK 36, Min SDK 24 (Android 7.0)

---

## 🚀 Требования и установка / Requirements & Installation

### Требования
- Устройство или эмулятор с **Android 7.0 (API 24)** или выше.
- Для **Regular Mode**: наличие Root-прав (Magisk / KernelSU / APatch / SuperSU).
- Для **Compatibility Mode**: Root не требуется (достаточно указать папку вывода SCHunt).

### Сборка из исходников
1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/NotCat40/SCEventFishing.git
   cd SCEventFishing
   ```
2. Откройте проект в **Android Studio** (recommended: Hedgehog / Iguana / Ladybug or newer).
3. Дождитесь завершения синхронизации Gradle.
4. Соберите проект через Gradle или запустите на подключенном устройстве:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 📂 Структура проекта / Project Structure

```
SCEventFishing/
├── app/
│   ├── src/main/
│   │   ├── java/com/sceventhunters/sceventfishing/
│   │   │   ├── MainActivity.kt                 # Главная Activity приложения
│   │   │   ├── SettingsActivity.kt             # Activity настроек и страницы "О программе"
│   │   │   ├── data/
│   │   │   │   ├── model/                      # Data-классы (AppInfo, AppMode)
│   │   │   │   └── repository/                 # Хранилище настроек (SharedPreferences)
│   │   │   ├── ui/
│   │   │   │   ├── main/                       # MainScreen (списки игр, детализация, пакетные действия)
│   │   │   │   ├── settings/                   # SettingsScreen (выбор темы, языка, папок экспорта)
│   │   │   │   ├── theme/                      # Тематизация Compose (SCEventFishingTheme)
│   │   │   │   └── util/                       # UI утилиты (LockScreenOrientation, preferences)
│   │   │   └── util/
│   │   │       ├── FileOperations.kt           # Логика копирования, поиска .sc файлов и экспорта
│   │   │       └── RootShell.kt                # Менеджер взаимодействия с Root shell (`su`)
│   │   └── res/                                # Локализованные ресурсы (values, values-ru)
│   └── build.gradle.kts
└── build.gradle.kts
```

---

## 🤝 Команда и авторство / Authors & Credits

Разработано командой **SCEventHunters team**.

- **Приложение**: SCEventFishing
- **Назначение**: Поиск, менеджмент и экспорт ивентовых ресурсов из игр Supercell.

---

## 📄 Лицензия / License

Проект распространяется для ознакомления и исследовательских целей. Все права на ассеты и игры принадлежат компаниям **Supercell** и **Tencent**.
