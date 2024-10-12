create database FusionFlavours;
use FusionFlavours;
-- Create Customer table
CREATE TABLE Customer (
    Customer_ID INT PRIMARY KEY IDENTITY,
    Name VARCHAR(100),
    Phone_Number VARCHAR(15),
    Email VARCHAR(100)
);

-- Create Restaurant_Table table
CREATE TABLE Restaurant_Table (
    Table_ID INT PRIMARY KEY IDENTITY,
    Table_Number INT NOT NULL,
    Capacity INT NOT NULL
);

-- Create Staff table
CREATE TABLE Staff (
    Staff_ID INT PRIMARY KEY IDENTITY,
    Name VARCHAR(100),
    Position VARCHAR(50),
    Phone_Number VARCHAR(15),
    Email VARCHAR(100),
    Manager_ID INT,
    FOREIGN KEY (Manager_ID) REFERENCES Staff(Staff_ID)
);

-- Create Reservation table
CREATE TABLE Reservation (
    Reservation_ID INT PRIMARY KEY IDENTITY,
    Customer_ID INT,
    Table_ID INT,
    Reservation_Date DATE,
    Reservation_Time TIME,
    FOREIGN KEY (Customer_ID) REFERENCES Customer(Customer_ID),
    FOREIGN KEY (Table_ID) REFERENCES Restaurant_Table(Table_ID)
);

-- Create Menu_Item table
CREATE TABLE Menu_Item (
    Menu_Item_ID INT PRIMARY KEY IDENTITY,
    Name VARCHAR(100),
    Price DECIMAL(5, 2)
);

-- Create Orders table
CREATE TABLE Orders (
    Order_ID INT PRIMARY KEY IDENTITY,
    Customer_ID INT,
    Staff_ID INT,
    Order_Date DATE,
    Total_Amount DECIMAL(7, 2),
    FOREIGN KEY (Customer_ID) REFERENCES Customer(Customer_ID),
    FOREIGN KEY (Staff_ID) REFERENCES Staff(Staff_ID)
);

-- Create Order_Item table
CREATE TABLE Order_Item (
    Order_Item_ID INT PRIMARY KEY IDENTITY,
    Order_ID INT,
    Menu_Item_ID INT,
    Quantity INT,
    Price DECIMAL(5, 2),
    FOREIGN KEY (Order_ID) REFERENCES Orders(Order_ID),
    FOREIGN KEY (Menu_Item_ID) REFERENCES Menu_Item(Menu_Item_ID)
);
-- Insert data into Customer table
INSERT INTO Customer (Name, Phone_Number, Email)
VALUES
('Rajesh Kumar', '9876543210', 'rajesh.kumar@example.com'),
('Priya Sharma', '9123456789', 'priya.sharma@example.com'),
('Anil Singh', '9998765432', 'anil.singh@example.com'),
('Sneha Reddy', '8881234567', 'sneha.reddy@example.com'),
('Amit Patel', '8876543210', 'amit.patel@example.com'),
('Neha Mehta', '8765432198', 'neha.mehta@example.com'),
('Vikram Desai', '8654321099', 'vikram.desai@example.com'),
('Pooja Nair', '8543219876', 'pooja.nair@example.com');

-- Insert data into Restaurant_Table
INSERT INTO Restaurant_Table (Table_Number, Capacity)
VALUES
(1, 4),
(2, 6),
(3, 2),
(4, 8),
(5, 4),
(6, 6),
(7, 2),
(8, 8);

-- Insert data into Staff table
INSERT INTO Staff (Name, Position, Phone_Number, Email, Manager_ID)
VALUES
('Aakash Verma', 'Manager', '7890123456', 'aakash.verma@example.com', NULL),
('Ravi Malhotra', 'Waiter', '7890654321', 'ravi.malhotra@example.com', 1),
('Suresh Rao', 'Chef', '7890987654', 'suresh.rao@example.com', 1),
('Leela Iyer', 'Manager', '7890789456', 'leela.iyer@example.com', NULL),
('Rahul Pillai', 'Waiter', '7890123789', 'rahul.pillai@example.com', 4),
('Rohini Gupta', 'Waiter', '7890564321', 'rohini.gupta@example.com', 4),
('Manoj Joshi', 'Chef', '7890432187', 'manoj.joshi@example.com', 4),
('Anjali Jain', 'Chef', '7890543276', 'anjali.jain@example.com', 1);

-- Insert data into Reservation table
INSERT INTO Reservation (Customer_ID, Table_ID, Reservation_Date, Reservation_Time)
VALUES
(1, 1, '2024-10-06', '19:00:00'),
(2, 3, '2024-10-07', '20:00:00'),
(3, 2, '2024-10-08', '18:30:00'),
(4, 5, '2024-10-09', '19:30:00'),
(5, 6, '2024-10-10', '21:00:00'),
(6, 7, '2024-10-11', '18:00:00'),
(7, 8, '2024-10-12', '20:30:00'),
(8, 4, '2024-10-13', '19:00:00');

-- Insert data into Menu_Item table
INSERT INTO Menu_Item (Name, Price)
VALUES
('Paneer Butter Masala', 250.00),
('Tandoori Roti', 40.00),
('Chicken Biryani', 300.00),
('Butter Naan', 60.00),
('Masala Dosa', 150.00),
('Gulab Jamun', 50.00),
('Pav Bhaji', 120.00),
('Rasgulla', 60.00);

-- Insert data into Orders table
INSERT INTO Orders (Customer_ID, Staff_ID, Order_Date, Total_Amount)
VALUES
(1, 2, '2024-10-06', 340.00),
(2, 2, '2024-10-07', 180.00),
(3, 3, '2024-10-08', 300.00),
(4, 5, '2024-10-09', 410.00),
(5, 6, '2024-10-10', 230.00),
(6, 6, '2024-10-11', 200.00),
(7, 7, '2024-10-12', 370.00),
(8, 8, '2024-10-13', 180.00);

-- Insert data into Order_Item table
INSERT INTO Order_Item (Order_ID, Menu_Item_ID, Quantity, Price)
VALUES
(1, 1, 1, 250.00),
(1, 2, 2, 80.00),
(2, 3, 1, 300.00),
(2, 5, 1, 150.00),
(3, 3, 1, 300.00),
(4, 6, 4, 200.00),
(5, 1, 1, 250.00),
(5, 4, 2, 120.00),
(6, 7, 1, 120.00),
(6, 6, 2, 100.00),
(7, 1, 2, 500.00),
(8, 5, 1, 150.00);
