Restaurant Management System - DBMS Project
Project Overview
This project is a Restaurant Management System built using SQL Server 2022. It provides an efficient way to manage restaurant operations such as customer reservations, staff assignments, orders, and menu items. The system is designed to handle various functionalities like booking tables, placing orders, managing staff, and tracking customer information.

Group Members
Atharva Pingale
Nidhi Puthran
Varun Rahatgaonkar
Nikhil Sunchu
Database Structure
The system consists of the following main tables:

Customer: Stores customer information like name, phone number, and email.
Restaurant_Table: Manages table details, including table number and capacity.
Menu_Item: Contains information about available menu items and their prices.
Reservation: Keeps track of table reservations made by customers.
Staff: Manages staff details, including self-referencing relationships for managers.
Orders: Stores information about customer orders and total amounts.
Order_Item: Tracks individual items ordered in each order and their quantities.
Entity-Relationship Diagram (ERD)
An ER diagram was designed to model the relationships between different entities in the system. The relationships between customers, reservations, orders, and staff are properly normalized for efficient database operations.

Key Features
Customer Management: Stores customer information with unique IDs.
Table Reservations: Handles table bookings with details like date and time.
Order Processing: Allows customers to place orders, which are handled by staff.
Menu Management: Tracks menu items and their prices.
Staff Management: Manages the restaurant staff and their reporting structure.
SQL Scripts
The following SQL scripts are included in the repository:

Schema Creation: Script to create all necessary tables for the database.
Sample Data: Script to populate the database with sample data, including customers, staff, and menu items.
Queries: Includes complex queries for reporting, such as retrieving total spending by customers, staff performance, and order summaries.
Normalization: Ensures the database follows 1NF, 2NF, 3NF, and BCNF principles.
How to Run
Clone the Repository:

bash
Copy code
git clone https://github.com/your-repo-url/restaurant-dbms-project.git
Set Up SQL Server:
Install SQL Server 2022 and ensure the necessary drivers and management studio (SSMS) are available.

Run the Schema Script:
Open the schema.sql file and run it in SQL Server to create all the tables.

Insert Sample Data:
Run the sample_data.sql script to insert sample records into the database.

Execute Queries:
Run the queries in queries.sql to generate reports and retrieve specific information.

Sample Query
A simple query to view customer orders along with their respective staff member:

sql
Copy code
SELECT O.Order_ID, C.Name AS Customer_Name, S.Name AS Staff_Name, O.Total_Amount
FROM Orders O
JOIN Customer C ON O.Customer_ID = C.Customer_ID
JOIN Staff S ON O.Staff_ID = S.Staff_ID;
Team Contributions
Each team member contributed to different parts of the project:

Atharva Pingale: Handled the table creation and schema design.
Nidhi Puthran: Focused on sample data insertion and normalization.
Varun Rahatgaonkar: Developed the complex queries and reports.
Nikhil Sunchu: Worked on optimization and testing.
Conclusion
This project demonstrates how a well-structured relational database can efficiently manage a restaurant’s day-to-day operations. By using SQL Server 2022, the system ensures data integrity, scalability, and ease of use.
