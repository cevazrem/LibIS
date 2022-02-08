CREATE DATABASE LIB;

USE LIB;

CREATE TABLE Authors (
	id INT PRIMARY KEY AUTO_INCREMENT,
    fio VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE Publishers (
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) UNIQUE NOT NULL,
    adress VARCHAR(255) NOT NULL
);

CREATE TABLE Books (
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    author INT NOT NULL,
    publisher INT NOT NULL,
    pub_year INT NOT NULL,
    FOREIGN KEY(author) REFERENCES Authors(id),
    FOREIGN KEY(publisher) REFERENCES Publishers(id)
);

CREATE TABLE Clients (
	id INT PRIMARY KEY AUTO_INCREMENT,
    fio VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    reg_date DATE NOT NULL,
    end_date DATE,
    phone VARCHAR(20) UNIQUE NOT NULL,
    CONSTRAINT chk_birth_date CHECK (birth_date > '1900-01-01'),
    CONSTRAINT chk_reg_date CHECK (reg_date > '2021-01-01'),
    CONSTRAINT chk_end_date CHECK (end_date > '2021-01-01'),
    CONSTRAINT chk_phone CHECK (phone REGEXP '[+]?[0-9]{1,3} ?\\(?[0-9]{3}\\)? ?[0-9]{2}[0-9 -]+[0-9]{2}')
);

CREATE TABLE Tickets (
	id INT PRIMARY KEY AUTO_INCREMENT,
    client INT NOT NULL,
    date_start DATE NOT NULL,
    date_end DATE,
    CONSTRAINT chk_date_start CHECK (date_start > '2021-01-01'),
    CONSTRAINT chk_date_end CHECK (date_end > '2021-01-01'),
    FOREIGN KEY(client) REFERENCES Clients(id)
);

CREATE TABLE Reserves (
	id INT PRIMARY KEY AUTO_INCREMENT,
    ticket INT NOT NULL,
    book INT NOT NULL,
    FOREIGN KEY(ticket) REFERENCES Tickets(id),
    FOREIGN KEY(book) REFERENCES Books(id)
);
INSERT INTO Authors (fio) values ('John Ronald Reuel Tolkien'), ('Joanne Rowling'), ('Stephen Edwin King'), ('Александр Сергеевич Пушкин'), ('Лев Николаевич Толстой'), ('Фёдор Михайлович Достоевский');

INSERT INTO Publishers (name, adress) values 
		('Эксмо — АСТ', '123308, город Москва, ул. Зорге, д. 1 стр. 1, этаж 20 каб 2013'),
        ('Феникс', '125009, город Москва, Тверская ул., д.6 стр.1'),
        ('ОЛМА Медиа Групп/ИД Просвещение', '129085, город Москва, Звездный б-р, д. 21 стр. 3, пом I комн 5');
        
INSERT INTO Books (name, author, publisher, pub_year) values
		('Гарри Поттер и философский камень', 2, 1, 1997),
        ('Гарри Поттер и тайная комната', 2, 1, 1998),
        ('Гарри Поттер и узник Азкабана', 2, 1, 1999),
        ('Гарри Поттер и кубок огня', 2, 3, 2000),
        ('The Fellowship of the Ring', 1, 1, 1954),
        ('The Two Towers', 1, 3, 1954),
		('The Two Towers', 1, 1, 1954),
		('The Return of the King', 1, 3, 1955),
        ('The Shining', 3, 2, 1980),
        ('The Stand', 3, 2, 1994),
        ('It', 3, 2, 1990),
        ('It', 3, 2, 2017),
        ('It', 3, 2, 2019),
        ('Евгений Онегин', 4, 2, 2015),
        ('Барышня-Крестьянка', 4, 2, 2020),
        ('Пиковая дама', 4, 3, 2013),
        ('Капитанская дочка', 4, 3, 2021),
        ('Анна Каренина', 5, 1, 2021),
        ('Война и мир. Том 1', 5, 1, 2020),
        ('Война и мир. Том 1', 5, 1, 2022),
        ('Война и мир. Том 2', 5, 2, 2021),
        ('Война и мир. Том 3', 5, 3, 2018),
		('Война и мир. Том 4', 5, 2, 2022),
		('Идиот', 6, 1, 2006),
        ('Преступление и наказание', 6, 3, 2012),
        ('Белый ночи', 6, 2, 2001),
        ('Игрок', 6, 1, 2016),
        ('Бесы', 6, 1, 2022);
	
INSERT INTO Clients (fio, birth_date, reg_date, end_date, phone) values
		('Иванов Иван Иванович', '2000-05-12', '2021-01-02', null, '+79999999999'),
		('Петров Петр Петрович', '1990-09-09', '2021-01-02', null, '+78888888888'),
		('Сергеев Сергей Сергеевич', '1991-10-21', '2021-01-02', null, '+77777777777'),
		('Владов Владислав Владиславович', '2006-03-12', '2021-05-05', '2021-06-05', '+79969342312');
        
INSERT INTO Tickets (date_start, date_end, client) values
		('2021-01-02', null, 1),
		('2021-01-02', '2023-01-01', 2),
		('2021-01-02', '2023-01-01', 3),
		('2021-05-05', '2021-06-05', 2);

INSERT INTO Reserves (ticket, book) values 
		(1, 1),
        (1, 2),
        (1, 3),
        (1, 4),
        (2, 5),
        (2, 6),
        (2, 8),
        (3, 7),
        (3, 14),
        (3, 15),
        (3, 18),
        (3, 20),
        (4, 12);
