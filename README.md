Курсовой проект «Сетевой чат»

Проект реализует многопользовательский текстовый чат с использованием Java-сокетов. Серверная часть обрабатывает подключения и маршрутизацию сообщений,
клиенты предоставляют интерфейс для взаимодействия. 
Решение поддерживает настройку параметров подключения, детальное логирование событий и многопоточную обработку запросов.

Архитектурные особенности

Серверная компонента
Класс `Server` использует пул потоков (`ExecutorService`) для параллельного обслуживания клиентов. При получении нового соединения:
- Создаётся экземпляр `ClientHandler` для управления коммуникацией
- Поток из пула выполняет обработку входящих сообщений
- Сообщения broadcast-рассылкой отправляются всем подключённым клиентам

// Пример обработки подключения
while (true) {
Socket clientSocket = serverSocket.accept();
ClientHandler clientHandler = new ClientHandler(clientSocket, this);
clients.add(clientHandler);
new Thread(clientHandler).start();
}

Логирование реализовано через `java.util.logging` с кастомным форматом:
String.format("[%1$tF %1$tT] [%2$s] %3$s%n",
new Date(record.getMillis()),
record.getLevel(),
record.getMessage());

Клиентская реализация
Клиентское приложение состоит из:
- Основного потока для отправки сообщений
- Фонового потока чтения ответов сервера
- Механизма логирования с аналогичным серверу форматом

// Пример обработки ввода
private void startMessageSender() {
new Thread(() -> {
Scanner scanner = new Scanner(System.in);
while (true) {
String message = scanner.nextLine();
if ("/exit".equalsIgnoreCase(message)) {
out.println("/exit");
break;
}
out.println(message);
}
}).start();
}

Конфигурация системы

Файл настроек `settings.txt`
Располагается в корневой директории проекта:
server.port=8080
server.host=localhost

Парсинг параметров реализован методами:
private static String getHostFromSettings() {
try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
// Логика чтения server.host
}
}

private static int getPortFromSettings() {
try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
// Обработка server.port
}
}

Сборка и запуск

Требования
- Java Development Kit 17+
- Maven 3.6+ для сборки
- Открытый порт 8080 (или указанный в настройках)

Тестирование системы

Unit-тесты
@Test
void testServerStart() {
Server server = new Server();
assertDoesNotThrow(server::start);
}

Интеграционное тестирование
1. Проверка через telnet:
telnet localhost 8080

2. Валидация логов сервера:
[2025-03-25 14:30:05] [INFO] User 'TestUser' connected
[2025-03-25 14:30:15] [MESSAGE] [14:30:15] TestUser: Hello World

Логирование ошибок
logger.severe("Error reading settings: " + e.getMessage());
logger.severe("Connection error: " + e.getLocalizedMessage());

Особенности реализации
- Автоматическое создание файла настроек при первом запуске
- Потокобезопасная обработка подключений (`CopyOnWriteArrayList`)
- Форматирование сообщений с временными метками
- Корректное завершение работы по команде `/exit`
