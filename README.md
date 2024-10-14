---

# Restaurant Management System - DBMS Project

## Project Overview

This project is a **Restaurant Management System** built using SQL Server 2022. It provides an efficient way to manage restaurant operations such as customer reservations, staff assignments, orders, and menu items. The system is designed to handle various functionalities like booking tables, placing orders, managing staff, and tracking customer information.

## Group Members
- **Atharva Pingale**  
- **Nidhi Puthran**  
- **Varun Rahatgaonkar**  
- **Nikhil Sunchu**  

## Database Structure

The system consists of the following main tables:
1. **Customer**: Stores customer information like name, phone number, and email.
2. **Restaurant_Table**: Manages table details, including table number and capacity.
3. **Menu_Item**: Contains information about available menu items and their prices.
4. **Reservation**: Keeps track of table reservations made by customers.
5. **Staff**: Manages staff details, including self-referencing relationships for managers.
6. **Orders**: Stores information about customer orders and total amounts.
7. **Order_Item**: Tracks individual items ordered in each order and their quantities.

### Entity-Relationship Diagram (ERD)
An ER diagram was designed to model the relationships between different entities in the system. The relationships between customers, reservations, orders, and staff are properly normalized for efficient database operations.

## Key Features
- **Customer Management**: Stores customer information with unique IDs.
- **Table Reservations**: Handles table bookings with details like date and time.
- **Order Processing**: Allows customers to place orders, which are handled by staff.
- **Menu Management**: Tracks menu items and their prices.
- **Staff Management**: Manages the restaurant staff and their reporting structure.

## SQL Scripts

The following SQL scripts are included in the repository:
1. **Schema Creation**: Script to create all necessary tables for the database.
2. **Sample Data**: Script to populate the database with sample data, including customers, staff, and menu items.
3. **Queries**: Includes complex queries for reporting, such as retrieving total spending by customers, staff performance, and order summaries.
4. **Normalization**: Ensures the database follows 1NF, 2NF, 3NF, and BCNF principles.

## How to Run

1. **Clone the Repository**:  
   ```bash
   git clone https://github.com/your-repo-url/restaurant-dbms-project.git
   ```

2. **Set Up SQL Server**:  
   Install SQL Server 2022 and ensure the necessary drivers and management studio (SSMS) are available.

3. **Run the Schema Script**:  
   Open the `schema.sql` file and run it in SQL Server to create all the tables.

4. **Insert Sample Data**:  
   Run the `sample_data.sql` script to insert sample records into the database.

5. **Execute Queries**:  
   Run the queries in `queries.sql` to generate reports and retrieve specific information.

## Sample Query

A simple query to view customer orders along with their respective staff member:

```sql
SELECT O.Order_ID, C.Name AS Customer_Name, S.Name AS Staff_Name, O.Total_Amount
FROM Orders O
JOIN Customer C ON O.Customer_ID = C.Customer_ID
JOIN Staff S ON O.Staff_ID = S.Staff_ID;
```

## Team Contributions

Each team member contributed to different parts of the project:

- **Atharva Pingale**: Handled the table creation and schema design.
- **Nidhi Puthran**: Focused on sample data insertion and normalization.
- **Varun Rahatgaonkar**: Developed the complex queries and reports.
- **Nikhil Sunchu**: Worked on optimization and testing.

## Project Report

The project report provides detailed information about the design, implementation, and testing of the Restaurant Management System. 

You can view or download the report here: https://www.canva.com/design/DAGTVM5jicA/dLkGotzEZ4TprGfmG2JmhA/edit?utm_content=DAGTVM5jicA&utm_campaign=designshare&utm_medium=link2&utm_source=sharebutton

## Conclusion

This project demonstrates how a well-structured relational database can efficiently manage a restaurant’s day-to-day operations. By using SQL Server 2022, the system ensures data integrity, scalability, and ease of use.

---
