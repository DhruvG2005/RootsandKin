<?php
// Enable error reporting
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// Configuration for the XAMPP MySQL server (port 3307)
$hosts = ['192.168.155.37','192.168.0.104','192.168.167.219','192.168.30.219','192.168.0.103','192.168.255.219','192.168.0.102','192.168.178.42'];
$username1 = "root";  // Your MySQL username for XAMPP
$password1 = "dHRUV20@";  // Your MySQL password for XAMPP
$dbname1 = "genealogy";  // Your database name for XAMPP
$port1 = '3306';  // Port for XAMPP MySQL server

// // Configuration for the local MySQL server (port 3306)
// $servername2 = "DellVostro";
// $username2 = "root";  // Your MySQL username for local MySQL server
// $password2 = "dHRUV20@";  // Your MySQL password for local MySQL server
// $dbname2 = "genealogy";  // Your database name for local MySQL server
// $port2 = '3306';  // Port for local MySQL server

function connectToDatabase($hosts, $username1, $password1, $dbname1) {
    foreach ($hosts as $host) {
        // Try to establish a connection
        $conn = new mysqli($host, $username1, $password1, $dbname1);
        
        if ($conn->connect_error) {
            // Log connection failure and try the next IP address
            error_log("Connection failed to $host: " . $conn->connect_error);
        } else {
            // Successful connection
            return $conn;
        }
    }

    // If all hosts fail
    die("Connection failed to all database hosts.");
}
// Create connection to the first available host
$conn = connectToDatabase($hosts, $user, $password, $dbname);

// Get the data from the POST request
$first_name = $_POST['first_name'] ?? '';
$last_name = $_POST['last_name'] ?? '';
$father_name = $_POST['father_name'] ?? '';
$mother_name = $_POST['mother_name'] ?? '';
$birth_date = $_POST['birth_date'] ?? '';
$birth_place = $_POST['birth_place'] ?? '';
$gender = $_POST['gender'] ?? '';
$blood_group = $_POST['blood_group'] ?? '';

// Check if required fields are empty
if (empty($first_name) || empty($last_name) || empty($birth_date) || empty($birth_place) || empty($gender) || empty($blood_group)) {
    die("Error: Missing required fields.");
}

// Prepare and bind for XAMPP MySQL server
$stmt1 = $conn1->prepare("INSERT INTO persons (first_name, last_name, father_name, mother_name, birth_date, birth_place, gender, blood_group) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
if (!$stmt1) {
    die("Error preparing statement on XAMPP MySQL server: " . $conn1->error);
}
$stmt1->bind_param("ssssssss", $first_name, $last_name, $father_name, $mother_name, $birth_date, $birth_place, $gender, $blood_group);

// Execute the statement on XAMPP MySQL server
if (!$stmt1->execute()) {
    echo "Error inserting data into XAMPP MySQL server: " . $stmt1->error;
}

// // Prepare and bind for local MySQL server
// $stmt2 = $conn2->prepare("INSERT INTO persons (first_name, last_name, father_name, mother_name, birth_date, gender, blood_group) VALUES (?, ?, ?, ?, ?, ?, ?)");
// if (!$stmt2) {
//     die("Error preparing statement on local MySQL server: " . $conn2->error);
// }
// $stmt2->bind_param("sssssss", $first_name, $last_name, $father_name, $mother_name, $birth_date, $gender, $blood_group);

// // Execute the statement on local MySQL server
// if (!$stmt2->execute()) {
//     echo "Error inserting data into local MySQL server: " . $stmt2->error;
// }

// Close the connections
$stmt1->close();
$conn1->close();

// $stmt2->close();
// $conn2->close();

echo "New record created successfully on both servers";
?>
