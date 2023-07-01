# LibIS
## Постановка задачи
Необходимо реализовать классическое приложение с использованием клиент-серверной архитектуры. Клиент должен иметь интуитивно понятный графический интерфейс для просмотра данных предметной области. Также необходимо разработать механизм для передачи запроса от клиента к серверу и получения ответа. На стороне сервера должно происходить получение и обработка запросов от клиента и последующее взаимодействие с базой данных, расположенной на машине с сервером. Для выполнения данной задачи, я решил воспользоваться возможностями языка программирования java для написания и клиентской и серверной части. Для создания графического интерфейса использована библиотека Java.Swing. Используемая БД - MY SQL SERVER.

## Описание предметной области
Предметной областью для моего проекта может служить любая общественная библиотека. В качестве пользователей могут выступать сотрудники библиотеки, которые смогут вести учет в целом за работой учреждения. В качестве объектов учета могут служить следующие сущности:
1. Клиенты - Обычные посетители библиотеки, приходящие за понравившейся книгой.
2. Читательский билет - Документ, необходимый для пользования услугами библиотеки.
3. Книга - Основной предмет, за которым посетители и приходят в библиотеку.
4. Автор - Маэстро, написавший ту или иную книгу.
5. Издательство - Организация, выпустившая книгу.

## Описание программы
При разработке данного приложения передо мной стояла задача выбрать СУБД, с которой будет взаимодействовать мой сервер. Выбор пал на MYSQL SERVER, так как на первый взгляд мне показалось, что раз java от Oracle, то и СУБД должна быть от них. На самом деле это не совсем так и возможно стоило было выбрать для небольшого проекта менее крупную СУБД. Однако были и свои плюсы в выборе данной СУБД, например механизм автоматического логирования, который позволил бы администраторам БД отслеживать всевозможные изменения данных, что помогло бы в решении различных инцидентов. При разработке структуры БД, я ровнялся на 3НФ, так как она заключает в себе и правильность хранения данных, так и удобство доступа и чтения данных для обычных разработчиков/администраторов.  
В итоге общую структуру БД можно описать с помощью данной модели:
![БД](https://github.com/cevazrem/LibIS/assets/71120846/c2c4097b-01ec-4277-abdb-135775f6f9b5)
Описание полей:
- Таблица Publishers:
  -	Id - Уникальный идентификатор. Первичный ключ.
  -	name - Название организации.
  -	address - Адрес организации.
Таблица Authors:
⦁	Id - Уникальный идентификатор. Первичный ключ.
⦁	Fio - ФИО автора.
⦁	date_birth - Дата рождения автора.
⦁	Country_birth - Страна рождения автора.
Таблица Books:
⦁	Id - Уникальный идентификатор. Первичный ключ.
⦁	Name - Название книги.
⦁	author - Автор. Внешний ключ на таблицу Authors.
⦁	publisher - Издатель. Внешний ключ на таблицу Publishers.
⦁	pub_year - Год издания.
Таблица Clients:
⦁	Id - Уникальный идентификатор. Первичный ключ.
⦁	Fio - ФИО клиента.
⦁	Date_birth - Дата рождения клиента.
⦁	Date_reg - Дата регистрации клиента в системе.
⦁	Date_end - Дата прекращения действия клиента.
⦁	Phone - Телефон клиента.
Таблица Tickets:
⦁	Id - Уникальный идентификатор. Первичный ключ.
⦁	Client - Клиент, владелец билета. Внешний ключ на таблицу Clients.
⦁	Date_start - Дата начала действия билета.
⦁	Date_end - Дата окончания действия билета.
Таблица Reserves:
⦁	Id - Уникальный идентификатор. Первичный ключ.
⦁	Ticket - Билет. Внешний ключ на таблицу Tickets.
⦁	Book - Книга. Внешний ключ на таблицу Books.
Для взаимодействия с БД также используется соответствующий драйвер JDBC. Он позваляет нашему приложению на java отправлять запросы SQL к SQL серверу и получать ответ по выполнению операции.
