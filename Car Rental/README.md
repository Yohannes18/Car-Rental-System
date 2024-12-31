# Car Rental System

This is a Java-based Car Rental System application. The system supports both admin and customer functionalities. Customers can sign up, log in, rent cars, and return cars. Admins can add, update, and remove cars, as well as view the list of cars and customers.

## Features

- **Admin**:
  - Add new cars to the system.
  - Remove cars from the system.
  - Update car details.
  - List all cars and customers.
- **Customer**:
  - Sign up and log in.
  - Rent available cars.
  - Return rented cars.
  - View the list of available cars.

## Prerequisites

Before running the application, ensure the following software is installed on your system:

1. [Java JDK 8 or later](https://www.oracle.com/java/technologies/javase-downloads.html).
2. [Apache Maven](https://maven.apache.org/install.html).
3. SQLite (optional, if you want to manage the database directly).

## Setup and Execution

Follow these steps to set up and run the application:

### 1. Clone the Repository

Clone the project repository to your local machine or download the source files.

```sh
cd /path/to/your/directory
git clone <https://github.com/Yohannes18/Car-Rental-System.git>
cd car-rental-system
```

### 2. Navigate to the Project Directory

Ensure you are in the directory containing the `pom.xml` file. For example:

```sh
cd /path/to/car-rental-system
```

### 3. Compile the Project

Use Maven to compile the project:

```sh
mvn compile
```

### 4. Run the Application

Execute the following command to run the application:

```sh
mvn exec:java -Dexec.mainClass="com.example.App"
```

### 5. Test the Application

Follow the prompts in the terminal to interact with the Car Rental System.

## Directory Structure

```
car-rental-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/
│   │   │       ├── App.java
│   │   │       └── CarRentalSystem.java
│   │   └── resources/
│   │       └── car_rental.db
├── pom.xml
└── README.md
```

## Notes

- The database is initialized automatically when the application is run for the first time.
- Admin password is set to `admin123` by default and can be changed in the `CarRentalSystem.java` file.
- Customer IDs are generated automatically during registration.

## License

This project is licensed under the MIT License.

