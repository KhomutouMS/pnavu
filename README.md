# FakeMailRu
Описание проекта

Данный проект представляет собой REST API для сервиса вопрос-ответ, разработанный с использованием Spring. На данный момент реализован базовый функционал без использования базы данных, который включает классические операции CRUD (Create, Read, Update, Delete).

Текущий функционал

GET - Получение списка всех пользователей

GET /{email} - Получение пользователя по email

POST /save_user - Создание нового пользователя

PUT /update_users - Обновление существующего пользователя

DELETE /delete_user/{email} - Удаление пользователя по email

Планы на будущее

Добавление изображений и файлов: Реализовать возможность прикрепления изображений и других файлов к заметкам.
Создание базы данных
Создание аккаунтов для пользователей
Возможность задавать вопросы и отвечать на них
Поиск уже заданного вопроса
