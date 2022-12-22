# GitHubUsersDemo
Тренировочное приложение для просмотра всех пользователей Git Hub.
## Состоит из трех экранов:
- список пользователей (реализован pagination на основе Jetpack Paging3, информация кешируется в sqlite)
- детализация по юзеру (переход на данный экран просходит при клике по элементу списка)
- экран входа в свой аккаунт (основной смысл авторизации в данном приложении - получение большего числа доступных запросов к GitHub Users API, 
после успешной авторизации будет доступен просмотр своего профиля).

## Используемые библиотеки/технологии:
- Архитектура MVVM
- Coroutines
- DataBinding
- Retrofit 2.9.0
- Jetpack Paging 3.1
- Jetpack Navigation 2.5.1
- Jetpack Room 2.4.3
- LiveData/ViewModel 2.5.1
- Picasso 2.8
- Firebase (для dynamic links)

## Что еще не реализовано:
- MVI на основе MVVM
- разделение на модули presentation, domain, data (clean architecture)
- dependency injection с помощью Dagger2
- unit-тестирование
