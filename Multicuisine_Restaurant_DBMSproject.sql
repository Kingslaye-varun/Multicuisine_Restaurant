-- Create Customer table
CREATE TABLE Customer (
    Customer_ID INT PRIMARY KEY IDENTITY(1,1),
    Name VARCHAR(100) NOT NULL,
    Phone_Number VARCHAR(15) NOT NULL,
    Email VARCHAR(100) NOT NULL
);

-- Create Restaurant_Table table
CREATE TABLE Restaurant_Table (
    Table_ID INT PRIMARY KEY IDENTITY(1,1),
    Table_Number INT NOT NULL,
    Capacity INT NOT NULL
);

-- Create Menu_Item table
CREATE TABLE Menu_Item (
    Menu_Item_ID INT PRIMARY KEY IDENTITY(1,1),
    Name VARCHAR(100) NOT NULL,
    Price DECIMAL(7, 2) NOT NULL,
    Category VARCHAR(50) NOT NULL
);

-- Create Reservation table
CREATE TABLE Reservation (
    Reservation_ID INT PRIMARY KEY IDENTITY(1,1),
    Customer_ID INT NOT NULL,
    Table_ID INT NOT NULL,
    Reservation_Date DATE NOT NULL,
    Reservation_Time TIME NOT NULL,
    FOREIGN KEY (Customer_ID) REFERENCES Customer(Customer_ID),
    FOREIGN KEY (Table_ID) REFERENCES Restaurant_Table(Table_ID)
);

-- Create Staff table
CREATE TABLE Staff (
    Staff_ID INT PRIMARY KEY IDENTITY(1,1),
    Name VARCHAR(100) NOT NULL,
    Position VARCHAR(50) NOT NULL,
    Phone_Number VARCHAR(15) NOT NULL,
    Email VARCHAR(100) NOT NULL,
    Manager_ID INT,
    Password VARCHAR(60) NOT NULL,
    FOREIGN KEY (Manager_ID) REFERENCES Staff(Staff_ID)
);

-- Create Orders table
CREATE TABLE Orders (
    Order_ID INT PRIMARY KEY IDENTITY(1,1),
    Customer_ID INT NOT NULL,
    Staff_ID INT NOT NULL,
    Order_Date DATE NOT NULL,
    Total_Amount DECIMAL(7, 2) NOT NULL,
    FOREIGN KEY (Customer_ID) REFERENCES Customer(Customer_ID),
    FOREIGN KEY (Staff_ID) REFERENCES Staff(Staff_ID)
);

-- Create Order_Item table
CREATE TABLE Order_Item (
    Order_Item_ID INT PRIMARY KEY IDENTITY(1,1),
    Order_ID INT NOT NULL,
    Menu_Item_ID INT NOT NULL,
    Quantity INT NOT NULL,
    Price DECIMAL(7, 2) NOT NULL,
    FOREIGN KEY (Order_ID) REFERENCES Orders(Order_ID),
    FOREIGN KEY (Menu_Item_ID) REFERENCES Menu_Item(Menu_Item_ID)
);

-- Sample Data for Customer table
INSERT INTO Customer (Name, Phone_Number, Email) VALUES
('Rohit Sharma', '9876543210', 'rohit.sharma@example.com'),
('Anjali Patil', '9876509876', 'anjali.patil@example.com'),
('Rahul Mehta', '9807654321', 'rahul.mehta@example.com'),
('Sunita Desai', '9876005432', 'sunita.desai@example.com'),
('Kiran Rao', '9876000001', 'kiran.rao@example.com');
select *from Customer;

-- Sample Data for Restaurant_Table table
INSERT INTO Restaurant_Table (Table_Number, Capacity) VALUES
(1, 4),
(2, 6),
(3, 2),
(4, 4),
(5, 8);
select *from Restaurant_Table;
-- Sample Data for Menu_Item table
INSERT INTO Menu_Item (Name, Price, Category) VALUES
('Paneer Butter Masala', 250.00, 'Main Course'),
('Butter Naan', 50.00, 'Bread'),
('Gulab Jamun', 40.00, 'Dessert'),
('Masala Dosa', 150.00, 'Snacks'),
('Biryani', 200.00, 'Main Course');
select*from Menu_Item;

-- Sample Data for Staff table
INSERT INTO Staff (Name, Position, Phone_Number, Email, Manager_ID, Password) VALUES
('Vijay Kumar', 'Manager', '9898989898', 'vijay.kumar@example.com', NULL, 'password123'),
('Rajesh Gupta', 'Waiter', '9898765432', 'rajesh.gupta@example.com', 1, 'password456'),
('Pooja Verma', 'Chef', '9876543456', 'pooja.verma@example.com', 1, 'password789'),
('Kartik Joshi', 'Waiter', '9876541234', 'kartik.joshi@example.com', 1, 'password012');
select *from Staff;
-- Sample Data for Reservation table
INSERT INTO Reservation (Customer_ID, Table_ID, Reservation_Date, Reservation_Time) VALUES
(1, 1, '2024-10-15', '19:00'),
(2, 2, '2024-10-15', '20:00'),
(3, 3, '2024-10-15', '19:30');
select *from Reservation;

-- Sample Data for Orders table
INSERT INTO Orders (Customer_ID, Staff_ID, Order_Date, Total_Amount) VALUES
(1, 2, '2024-10-15', 300.00),
(2, 3, '2024-10-15', 500.00),
(3, 2, '2024-10-15', 450.00);
select *from Orders;
-- Sample Data for Order_Item table
INSERT INTO Order_Item (Order_ID, Menu_Item_ID, Quantity, Price) VALUES
(1, 1, 1, 250.00),
(1, 2, 2, 50.00),
(2, 4, 3, 150.00),
(2, 5, 1, 200.00),
(3, 1, 1, 250.00),
(3, 3, 2, 40.00);
select *from Order_Item;

-- Simple Query
SELECT * FROM Customer;

-- Nested Query
SELECT * FROM Orders
WHERE Total_Amount > (SELECT AVG(Total_Amount) FROM Orders);

-- Complex Query
SELECT C.Name AS Customer_Name, SUM(O.Total_Amount) AS Total_Spent
FROM Customer C
JOIN Orders O ON C.Customer_ID = O.Customer_ID
GROUP BY C.Name;

-- Correlated Query
SELECT Name, Phone_Number
FROM Customer C
WHERE EXISTS (
    SELECT 1 FROM Reservation R 
    WHERE R.Customer_ID = C.Customer_ID 
    GROUP BY R.Customer_ID 
    HAVING COUNT(*) > 1
);

-- End the previous batch
GO

-- View Creation
CREATE VIEW OrderDetails AS
SELECT O.Order_ID, C.Name AS Customer_Name, S.Name AS Staff_Name, O.Order_Date, O.Total_Amount
FROM Orders O
JOIN Customer C ON O.Customer_ID = C.Customer_ID
JOIN Staff S ON O.Staff_ID = S.Staff_ID;

-- Query the View
SELECT * FROM OrderDetails;

--Joins
SELECT O.Order_ID, C.Name AS Customer_Name, S.Name AS Staff_Name, O.Total_Amount
FROM Orders O
JOIN Customer C ON O.Customer_ID = C.Customer_ID
JOIN Staff S ON O.Staff_ID = S.Staff_ID;


-- Normalization
-- 1NF: Each column contains atomic values and each row is unique.
-- 2NF: All non-key attributes are fully dependent on the primary key (achieved as no partial dependencies exist).
-- 3NF: No transitive dependencies (all non-key attributes are independent of each other).
-- BCNF: Every determinant is a candidate key (achieved as no further normalization is needed based on current structure).
