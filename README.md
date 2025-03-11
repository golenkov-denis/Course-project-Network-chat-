# Консольный чат: сервер-клиент архитектура

Проект реализует многопользовательский текстовый чат с использованием Java-сокетов. Серверная часть обрабатывает подключения и маршрутизацию сообщений, клиенты предоставляют интерфейс для взаимодействия. Решение поддерживает настройку параметров подключения, детальное логирование событий и многопоточную обработку запросов.

## Архитектурные особенности

### Серверная компонента
Класс `Server` использует пул потоков (`ExecutorService`) для параллельного обслуживания клиентов. При получении нового соединения:
1. Создаётся экземпляр `ClientHandler` для управления коммуникацией
2. Поток из пула выполняет обработку входящих сообщений
3. Сообщения broadcast-рассылкой отправляются всем подключённым клиентам

Логирование реализовано через java.util.logging с кастомным форматом:
String.format("[%1$tF %1$tT] [%2$s] %3$s%n",
new Date(record.getMillis()),
record.getLevel(),
record.getMessage())


### Клиентская реализация
Клиентское приложение состоит из:
- Основного потока для отправки сообщений
- Фонового потока чтения ответов сервера
- Механизма логирования с аналогичным серверу форматом

Особенности обработки ввода:
Thread inputThread = new Thread(() -> {
while (true) {
System.out.print("You: ");
String message = scanner.nextLine();
if (message.equalsIgnoreCase("/exit")) break;
out.println(message);
}
});


## Конфигурация системы

### Файл настроек settings.txt
Располагается в корневой директории проекта:
server.port=8080
server.host=localhost


Парсинг параметров реализован методами:
private static String getHostFromSettings() {
// Логика чтения server.host
}

private static int getPortFromSettings() {
// Обработка server.port
}


## Сборка и запуск

### Требования
- Java Development Kit 11+
- Maven/Gradle для сборки
- Открытый порт 8080 (или указанный в настройках)
  


## Тестирование системы

### Unit-тесты
Пример теста подключения к серверу:
@Test
void testServerResponse() throws IOException {
try (Socket socket = new Socket("localhost", 8080);
BufferedReader in = new BufferedReader(
new InputStreamReader(socket.getInputStream()))) {
assertNotNull(in.readLine());
}
}



### Интеграционное тестирование
1. Проверка ответа через telnet:
telnet localhost 8080


2. Валидация логов:
[2025-03-12 09:15:22] [INFO] User 'TestUser' connected
[2025-03-12 09:15:30] [MESSAGE] TestUser: Hello World

Логирование ошибок реализовано через:
logger.severe("Error reading settings: " + e.getMessage());
