Подключение БД к проекту Java:

1)Установить IDE IntelliJ IDEA UE c официального сайта(есть бесплатные 30 дней, в теории можно плодить почты)
https://www.jetbrains.com/idea/download/#section=windows
![image](https://user-images.githubusercontent.com/71120846/153244277-4dcab93f-aa1e-4708-a88e-adf0b752bdd2.png)

2)Установить MySQL pack с официального сайта
https://dev.mysql.com/downloads/windows/installer/8.0.html
![image](https://user-images.githubusercontent.com/71120846/153244499-d8e45713-32c2-4556-8c6e-cf5e07cd49a3.png)

  Необходимые компоненты для установки
![image](https://user-images.githubusercontent.com/71120846/153245110-430548a5-d4a5-414a-825c-6a42fa70b1b7.png)

  На шаге создания пользователя root, придумываем пароль, например 1234. Можно также создать дополнительного пользователя.

  После установки всех компонентов, можно проверить работоспособность базы. Для этого необходимо открыть консоль под администратором, прописать путь для установленной папки сервера(у меня:cd C:\Program Files\MySQL\MySQL Server 8.0\bin). Далее прописываем команду mysql -u root -p, нажимаем enter, вводим пароль и мы должны были подключиться к серверу. Можно проверить, что все ок, написав команду SHOW DATABASES; будут выведены различные установленные базы. Для последующих шагов нужно выполнить команду CREATE DATABASE LIB;

Теперь, когда мы установили нашу базу данных, можно создать проект в нашей IDE. Ставим галочку SQL support.
![image](https://user-images.githubusercontent.com/71120846/153249304-ba5a259f-04ae-44d3-a972-6541ba218a21.png)
Когда создали проект, View->tool windows->database. В открывшейся понели, нажимаем на плюсик->data source->mysql.
![image](https://user-images.githubusercontent.com/71120846/153250724-c1e8d28d-6548-45c8-8e7e-88d4cd10d88b.png)
Также он напишет, что нехватает DB драйвера и предложит установить, нужно кликнуть. Потом можно нажать test connection для проверки подключения к базе.

Заходим в настройки проекта ctrl+alt+shift+S.
![image](https://user-images.githubusercontent.com/71120846/153249907-15f6a55e-a451-4cfa-bd23-c346dc060edf.png)
Далее нужно найти на своем компютере скачаный JDBC, у меня он лежит C:\Program Files (x86)\MySQL\Connector J 8.0 имя файла mysql-connector-java-8.0.28.jar.

Возвращаемся в нашу IDE, заходим во вкладку SDKs нажимаем на плюсик и добавляем ранее найденный файл JDBC
![image](https://user-images.githubusercontent.com/71120846/153251399-546e6904-db07-4b0d-868b-3953ff2f5c43.png)
