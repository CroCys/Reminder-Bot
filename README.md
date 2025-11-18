# Telegram Bot — менеджер общих подписок и напоминаний

![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white) ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)

[![License](https://img.shields.io/badge/license-MIT-brightgreen)](LICENSE) [![Version](https://img.shields.io/badge/version-1.5-blue)](pom.xml) [![Last commit](https://img.shields.io/github/last-commit/CroCys/TelegramBot)](https://github.com/CroCys/TelegramBot)

> Spring Boot Telegram-бот для управления общими подписками, генерации платежей и напоминаний об оплате.

---

## Краткое описание

Этот проект реализует Telegram-бота, который помогает группам людей (семья, друзья, коллеги) совместно управлять
платными подписками: регистрировать пользователей, подписываться/отписываться от сервисов, автоматически генерировать
платежи, отправлять напоминания и фиксировать оплаты через интерфейс Telegram.

Ключевые возможности:

- Регистрация пользователей и хранение их данных в PostgreSQL.
- Подписка/отписка на шаблоны подписок (из `subscriptions.json`).
- Автоматическая генерация платежей и ежедневные/плановые напоминания (scheduler).
- Админ-инструменты: просмотр должников и пометка оплат (Доступно 2 админа).
- Интерактивные меню и кнопки (inline/keyboard) в Telegram.

---

## Стек и зависимости

- Java 21 (см. `pom.xml` property `<java.version>`)
- Spring Boot 3.5.5 (родительский POM)
- telegrambots-spring-boot-starter 6.9.7.1
- PostgreSQL (JDBC, runtime)
- Lombok (аннотации)
- Maven (с wrapper `./mvnw`)
- Docker и `docker-compose` (при запуске контейнеров)

Файл с зависимостями: `pom.xml`.

---

## Структура проекта (важные файлы)

- `src/main/java/com/vadim/telegrambot/TelegramBotApplication.java` — точка входа приложения.
- `src/main/java/com/vadim/telegrambot/service/Bot.java` — основной класс Telegram-бота, обработка сообщений и
  callback'ов.
- `src/main/java/com/vadim/telegrambot/service/*` — бизнес-логика: `SubscriptionService`, `UserService`,
  `PaymentService`, `ReminderService`.
- `src/main/java/com/vadim/telegrambot/model/*` — JPA-модели: `User`, `Subscription`, `Payment`.
- `src/main/java/com/vadim/telegrambot/repository/*` — Spring Data JPA репозитории.
- `src/main/java/com/vadim/telegrambot/config/*` — конфигурации: `SubscriptionJsonLoader`, `SubscriptionInitializer`,
  `BotRegistrationConfig`, `AsyncConfig`.
- `src/main/java/com/vadim/telegrambot/scheduler/ReminderScheduler.java` — задание планировщика (cron/планировщик
  напоминаний).
- `src/main/resources/application-dev.yml` — dev-конфигурация.
- `src/main/resources/application-prod.yml` — prod-конфигурация.
- `subscriptions.json` — список шаблонов подписок (код, название, цена, дата оплаты).
- `docker-compose-dev.yml`, `docker-compose.yml` — compose-конфиги (локальная БД / прод-окружение).

---

## Как запустить локально (рекомендуемый dev-процесс)

Требования:

- Java 21
- Maven (можно использовать `./mvnw` из репозитория)
- Docker & docker-compose (для быстрого поднятия PostgreSQL)

1) Поднять локальную БД PostgreSQL (рекомендуется через Docker):

```bash
# поднять БД для разработки (файл docker-compose-dev.yml расположен в корне проекта)
docker-compose -f docker-compose-dev.yml up -d
```

2) Убедиться, что в `application-dev.yml` указаны правильные параметры подключения к БД (по умолчанию совпадают с
   настройками из `docker-compose-dev.yml`) и боту:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/botdb
    username: botuser
    password: secret
```

```yaml
bot:
  username: YourBotUserName
  token: 123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11
  firstAdminId: 123456789
  secondAdminId: 987654321
```

3) Убедиться, что файл `subscriptions.json` находится в корне проекта (рядом с `pom.xml`), либо изменить путь к нему
   в `application-dev.yml` (свойство `subscription.jsonFilePath`). _см ниже раздел "Работа с subscriptions.json"._

4) Собрать и запустить приложение в профиле `dev` (использует `application-dev.yml`):

```bash
# собрать
./mvnw clean package

# запустить напрямую (использует dev-профиль через флаг):
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# либо запустить собранный jar:
java -jar target/TelegramBot-1.5.jar --spring.profiles.active=dev
```

---

## Docker / production

В `application-prod.yml` используются переменные окружения. Перед запуском в проде убедитесь, что заданы:

- DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
- APP_PORT (необязательно)
- TELEGRAM_BOT_USERNAME, TELEGRAM_BOT_TOKEN
- TELEGRAM_FIRST_ADMIN_ID, TELEGRAM_SECOND_ADMIN_ID

Пример `.env` для прод-приложения:

```ini
# .env
APP_PORT=8080

DB_HOST=postgres
DB_PORT=5432
DB_NAME=botdb
DB_USER=botuser
DB_PASSWORD=secret


TELEGRAM_BOT_USERNAME=YourBotUserName
TELEGRAM_BOT_TOKEN=123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11
TELEGRAM_FIRST_ADMIN_ID=123456789
TELEGRAM_SECOND_ADMIN_ID=987654321
```

Также проверьте, что `subscriptions.json` доступен в контейнере (см. volume в `docker-compose.yml`).

Запуск `docker-compose.yml` (Если скачан последний релиз с GitHub):

```bash
# поднять все сервисы (db + бот) через docker-compose
docker-compose up -d
```

Если приложение разворачивается вручную на сервере, собрать jar и запустить с активным `prod` профилем:

```bash
./mvnw clean package
java -jar target/TelegramBot-1.5.jar --spring.profiles.active=prod
```

---

## Конфигурация Telegram бота

Параметры бота находятся в `application-*.yml`:

- `bot.username` — username бота
- `bot.token` — токен
- `bot.firstAdminId`, `bot.secondAdminId` — идентификаторы админов (Telegram ID)

В `application-dev.yml` эти значения заданы напрямую (для удобства разработки).

---

## Работа с subscriptions.json

Файл `subscriptions.json` содержит шаблоны подписок, которые приложение читает при старте (через
`SubscriptionJsonLoader` и `SubscriptionInitializer`). Формат:

```json
[
  {
    "uniqueCode": "1",
    "name": "VPN",
    "price": 100,
    "dayOfMonth": 22
  },
  {
    "uniqueCode": "2",
    "name": "Apple",
    "price": 172,
    "dayOfMonth": 7
  }
]
```

Правила и поведение:

- `uniqueCode` — уникальный код подписки; используется для поиска/сопоставления при инициализации.
- При старте приложение добавляет новые шаблоны и обновляет существующие (по `uniqueCode`).
- Если из JSON удалена подписка — `SubscriptionInitializer` удалит её из БД и отвяжет пользователей.

Чтобы добавить/обновить подписку: отредактируйте `subscriptions.json` и перезапустите приложение (или обновите
монтируемый файл в контейнере).

---

## Диагностика и распространённые проблемы

- Приложение аварийно завершает запуск с сообщением о `Subscription JSON file not found` — проверьте свойство
  `subscription.jsonFilePath` в `application-*.yml` и наличие файла (`subscriptions.json`) в указанном пути.
- Бот не регистрируется / нет активности — проверьте `bot.token` и логи при старте (`BotRegistrationConfig` регистрирует
  бота в `TelegramBotsApi`).
- Ошибки подключения к БД — проверьте URL/cred в `application-*.yml` (или переменные окружения), доступность Postgres (
  `docker-compose -f docker-compose-dev.yml logs -f db`).
- SQL-логи (в `dev`) включены: `spring.jpa.show-sql: true` в `application-dev.yml`.

Полезные команды для отладки:

```bash
# просмотреть логи БД (docker)
docker-compose -f docker-compose-dev.yml logs -f db

# просмотреть логи приложения (если запускали через docker-compose)
docker-compose logs -f <app-service-name>

# запустить приложение в консоли и смотреть логи
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Как вносить изменения / developer guide

1. Форк/клонируйте репозиторий.
2. Поднимите локальную БД через `docker-compose-dev.yml`.
3. Соберите и запустите приложение локально (`./mvnw clean package` / `./mvnw spring-boot:run`).
4. Для тестирования Telegram-бота используйте тестовый токен и тестовый чат/аккаунт.

Точки входа для изучения кода:

- `TelegramBotApplication` — запуск приложения.
- `service/Bot.java` — обработка команд и callback'ов.
- `config/SubscriptionJsonLoader.java` и `config/SubscriptionInitializer.java` — загрузка и синхронизация шаблонов
  подписок.
- `scheduler/ReminderScheduler.java` и `service/ReminderService.java` — планирование и отправка напоминаний.
- `service/*`, `repository/*`, `model/*` — бизнес-логика и сущности.

---

## Лицензия и контакты

Проект лицензирован под MIT — см. файл `LICENSE`.

Автор: Вадим
Email: vadikderkach2003@gmail.com
GitHub: https://github.com/CroCys/TelegramBot

---

Спасибо за использование и вклад в проект!