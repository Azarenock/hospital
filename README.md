## hospital
1. Разработать базу данных "Поликлиника" (Упрощенная форма). на PostgreSql
   Структура базы данных должна удовлетворять следующим условиям:
   - Все пациенты распределены по участкам, в зависимости от места жительства (адреса);
   - Врачи разделены на две группы: (специалисты, терапевты);
   - Терапевты закреплены за участком;
   - Удин участок могут обслуживать один или несколько терапевтов;
   - Один терапевт  может обслуживать  пациентов со своего или с другого участка( в случае отсутствия коллеги);
   - Специалисты принимают пациентов независимо от участка.
   - Врачи работают 5 дней в неделю по 4 часа либо в утро(8-12) либо в вечер (13-17) через день;
   - Время приёма одного пациента 30 минут;
 2. Написать web-приложение на java, которое обеспечивает:
   - ввод в базу данных новых пациентов;
   - вывод расписания приёма врачей на месяц (с записанными пациентами);
   - запись пациента к конкретному врачу на конкретное время;
     
## Для установки клонируйте себе репозиторий 
https://github.com/Azarenock/hospital.git

## БД создана через pgAdmin
![image](![image](https://github.com/user-attachments/assets/fc8c7ffe-284f-4fd5-9692-2c28ae0e2ddc))
![image](![image](https://github.com/user-attachments/assets/39d00e3c-a687-4e5b-800c-0e0c6efa00f7))
![image](![image](https://github.com/user-attachments/assets/a2fa6508-0296-4973-aae5-0e7487a0a7ab))
![image](![image](https://github.com/user-attachments/assets/12f24262-4486-43b2-9488-b8b09ddb0636))

##Основные интерфейсы приложения
![image](![image](https://github.com/user-attachments/assets/1c3605d8-743c-4d26-9548-979d45f67895))
![image](![image](https://github.com/user-attachments/assets/8f40125a-ed32-4f3e-bdce-d2917a454dfb))
![image](![image](https://github.com/user-attachments/assets/8e67d965-32c9-4046-b38b-14f75ad3c505))
![image](![image](https://github.com/user-attachments/assets/53b7c028-2a62-4513-8fb4-f070e0de252f))
![image](https://github.com/user-attachments/assets/e93d07de-c88e-453f-a09b-46f5d6106487)
![image](https://github.com/user-attachments/assets/7f4aa446-f88a-4a47-a106-40a4cab363a2)

