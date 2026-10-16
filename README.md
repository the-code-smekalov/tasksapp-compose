# Задачки

Простое Android-приложение для заметок-задач с вложениями (файлы, картинки, случайные котики из [TheCatAPI](https://thecatapi.com/)).

## Возможности
- Список задач (Room), переход к просмотру/редактированию по клику, сохранение прокрутки при возврате.
- Добавление задачи: текст (мин. 5 строк), до 5 вложений с превью, кнопка «Прикрепить котика».
- Редактирование: «Редактировать» и «Сохранить» в TopAppBar.
- Файлы копируются в `filesDir/attachments` (удаляются вместе с приложением).
- Material 3, светлая/тёмная тема по системе (Dynamic Color на Android 12+).

## Стек
Kotlin · Jetpack Compose · MVI (+Clean Architecture) · Room · Retrofit/OkHttp · Hilt · Compose Navigation · Coil · Secrets Gradle Plugin. minSdk 24, targetSdk 34.

## Структура
| Модуль | Содержимое |
|---|---|
| `:domain` | Чистый Kotlin: модели, интерфейсы репозиториев, use case'ы |
| `:data` | Room, Retrofit, реализации репозиториев, Hilt-модули |
| `:presentation` | Compose UI, ViewModel (MVI), навигация, тема |
| `:app` | `Application`, `MainActivity`, манифест |

## Запуск
1. Получите ключ на https://thecatapi.com/signup (работает и без ключа, но с лимитами).
2. Добавьте в `local.properties` (файл в `.gitignore`):
   ```
   CAT_API_KEY=ваш_ключ
   ```
3. Откройте проект в Android Studio (Koala+, JDK 17), дождитесь Gradle Sync, запустите `app`.
