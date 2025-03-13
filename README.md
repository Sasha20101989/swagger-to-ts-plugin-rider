# Auto API Plugin for IntelliJ IDEA

[Плагин на маркетплейсе](https://plugins.jetbrains.com/plugin/26666-auto-api)

## Описание проекта
Auto API — это плагин для IntelliJ IDEA, который генерирует TypeScript-клиентов для работы с API, основываясь на Swagger JSON файле. Плагин позволяет разработчикам автоматически преобразовывать OpenAPI спецификации в TypeScript клиентов с поддержкой axios и обработки параметров запросов. В будущем будет добавлена поддержка генерации C# клиентов.
## Начало работы
### Установка
1.	Установка плагина через Marketplace:
  - Откройте IntelliJ IDEA или Rider
  - Перейдите в IntelliJ IDEA | Rider > Settings > Plugins
  - В поле поиска введите Auto API
  - Установите плагин из Marketplace

2. Сборка плагина из исходников:
Для того чтобы собрать плагин из исходников, выполните следующие шаги:
  - Убедитесь, что у вас установлен [.NET8 SDK](https://dotnet.microsoft.com/en-us/download/dotnet/8.0)
  - Убедитесь, что у вас установлен [JDK 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
  - Откройте терминал (или командную строку)
  - Выполните следующую команду для установки NSwag глобально:
    ```shell
    dotnet tool install --global NSwag.ConsoleCore --version 14.2.0
    ```
  - Клонируйте репозиторий с исходным кодом:
    ```shell
    git clone https://tfs.rubius.com/RubiusGroupProjects/DevelopmentTemplates/_git/web.back.plugin.auto-api
    cd web.back.plugin.auto-api
    ```
  #### Эта команда установит глобально утилиту NSwag версии 14.2.0, которая позволяет работать с OpenAPI и генерировать TypeScript и C# клиентов.
  #### После установки можно проверить, что NSwag установлен корректно, выполнив команду
  ```shell
    nswag --version
  ```
  #### Вы должны увидеть вывод с версией 14.2.0, например:
  ```shell
    NSwag command line tool v14.2.0
  ```
  - Соберите проект с помощью Gradle:
    ```shell
    ./gradlew build
    ```

### Зависимости
  - JDK 21 для сборки проекта
  - Gradle для управления зависимостями и сборкой
  - IntelliJ IDEA для разработки плагина

## Сборка и тестирование
Для сборки плагина и упаковки его в .zip архив выполните следующие шаги:
  - Откройте терминал в корневой директории проекта
  - Выполните команду:
   ```shell
  ./gradlew buildPlugin
   ```
#### После выполнения команды архив с плагином будет доступен в директории build/distributions/ с расширением .zip
#### Чтобы протестировать плагин в своей IDE:
  - Перейдите в IntelliJ IDEA | Rider > Settings > Plugins > Install plugin from disk
  - Укажите путь к собранному .zip архиву и установите плагин

## Пример использования плагина
 - Перейдите в меню Tools > Services > Generate Api Clients
 - Введите URL Swagger JSON файла (например, https://example.com/swagger.json)
 - Выберите директорию для сохранения сгенерированного TypeScript файла
 - Укажите имя выходного файла (например, ApiClient.ts)
 - Нажмите “ОК”, чтобы сгенерировать файл
#### После успешной генерации плагин откроет сгенерированный файл в редакторе

## Последние релизы
  - Поддержка генерации TypeScript клиентов с использованием Swagger JSON для IntelliJ IDEA(Apple Silicon), Rider(Apple Silicon)
  [1.1.1](https://tfs.rubius.com/RubiusGroupProjects/DevelopmentTemplates/_git/web.back.plugin.auto-api?path=%2Freleases%2Fweb.back.plugin.auto-api_1.1.1.zip)
  - Добавлена поддержка IntelliJ IDEA(Windows), Rider(Windows)
  [1.1.2](https://tfs.rubius.com/RubiusGroupProjects/DevelopmentTemplates/_git/web.back.plugin.auto-api?path=%2Freleases%2Fweb.back.plugin.auto-api_1.1.2.zip)
  - Поддержка генерации CSharp клиентов с использованием Swagger JSON для IntelliJ IDEA(Apple Silicon), Rider(Apple Silicon), IntelliJ IDEA(Windows), Rider(Windows)
  [1.1.3](https://tfs.rubius.com/RubiusGroupProjects/DevelopmentTemplates/_git/web.back.plugin.auto-api?path=%2Freleases%2Fweb.back.plugin.auto-api_1.1.3.zip)