<?php
// Database connection details
$hosts = ['192.168.155.37','192.168.0.104','192.168.167.219','192.168.30.219','192.168.0.103','192.168.255.219','192.168.0.102','192.168.178.42']; // Database host
$user = 'root'; // Database username
$password = 'dHRUV20@'; // Database password
$dbname = 'genealogy'; // Database name

function connectToDatabase($hosts, $user, $password, $dbname) {
    foreach ($hosts as $host) {
        // Try to establish a connection
        $conn = new mysqli($host, $user, $password, $dbname);
        
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

// Helper function to split a full name into first and last name
function splitName($name) {
    $name_parts = explode(" ", $name);
    return count($name_parts) > 1 ? ['first_name' => $name_parts[0], 'last_name' => $name_parts[1]] : ['first_name' => $name, 'last_name' => ''];
}

// Helper function to fetch family members (siblings, parents, spouses)
function getFamilyMembers($conn, $last_name) {
    // Fetch family members based on last name (Father, Mother, Son, Daughter, Spouse roles)
    $sql = "SELECT p.first_name, p.last_name, p.gender, fm.role, p.person_id 
            FROM family_members fm
            JOIN persons p ON fm.person_id = p.person_id
            WHERE fm.role IN ('Father', 'Mother', 'Son', 'Daughter', 'Spouse') 
            AND p.last_name = ?";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $last_name);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $familyMembers = [];
    while ($row = $result->fetch_assoc()) {
        $familyMembers[] = [
            'first_name' => $row['first_name'],
            'last_name' => $row['last_name'],
            'gender' => $row['gender'],
            'role' => $row['role'],
            'person_id' => $row['person_id'] // Add person_id to use later in relationships
        ];
    }

    return $familyMembers;
}

// Helper function to fetch parents (father and mother)
function getParents($conn, $first_name, $last_name) {
    $sql = "SELECT father_name, mother_name FROM persons WHERE first_name = ? AND last_name = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $first_name, $last_name);
    $stmt->execute();
    $result = $stmt->get_result();
    return $result->fetch_assoc();
}

// Recursive function to fetch grandparents by checking father_name and mother_name iteratively
function getGrandparents($conn, $father_name, $mother_name) {
    $grandparents = [];

    // Fetch paternal grandparents (father's side)
    if ($father_name) {
        $father_parts = splitName($father_name);
        $father_first_name = $father_parts['first_name'];
        $father_last_name = $father_parts['last_name'];

        $sql = "SELECT father_name, mother_name FROM persons WHERE first_name = ? AND last_name = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("ss", $father_first_name, $father_last_name);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($row = $result->fetch_assoc()) {
            if ($row['father_name']) {
                $father_grandparents = splitName($row['father_name']);
                $grandparents[] = ['first_name' => $father_grandparents['first_name'], 'last_name' => $father_grandparents['last_name'], 'relationship' => 'Paternal Grandfather'];
            }
            if ($row['mother_name']) {
                $mother_grandparents = splitName($row['mother_name']);
                $grandparents[] = ['first_name' => $mother_grandparents['first_name'], 'last_name' => $mother_grandparents['last_name'], 'relationship' => 'Paternal Grandmother'];
            }
        }
    }

    // Fetch maternal grandparents (mother's side)
    if ($mother_name) {
        $mother_parts = splitName($mother_name);
        $mother_first_name = $mother_parts['first_name'];
        $mother_last_name = $mother_parts['last_name'];

        $sql = "SELECT father_name, mother_name FROM persons WHERE first_name = ? AND last_name = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("ss", $mother_first_name, $mother_last_name);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($row = $result->fetch_assoc()) {
            if ($row['father_name']) {
                $father_grandparents = splitName($row['father_name']);
                $grandparents[] = ['first_name' => $father_grandparents['first_name'], 'last_name' => $father_grandparents['last_name'], 'relationship' => 'Maternal Grandfather'];
            }
            if ($row['mother_name']) {
                $mother_grandparents = splitName($row['mother_name']);
                $grandparents[] = ['first_name' => $mother_grandparents['first_name'], 'last_name' => $mother_grandparents['last_name'], 'relationship' => 'Maternal Grandmother'];
            }
        }
    }

    return $grandparents;
}

// Fetch relationship details from the relationships table
function getRelationshipDetails($conn, $person_id, $relative_id) {
    $sql = "SELECT relationship_type, other_details 
            FROM relationships 
            WHERE (person_id_1 = ? AND person_id_2 = ?) 
            OR (person_id_1 = ? AND person_id_2 = ?)";
    
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("iiii", $person_id, $relative_id, $relative_id, $person_id);
    $stmt->execute();
    $result = $stmt->get_result();
    
    $relationships = [];
    
    while ($row = $result->fetch_assoc()) {
        $relationships[] = [
            'relationship_type' => $row['relationship_type'],
            'other_details' => $row['other_details']
        ];
    }
    
    return $relationships;
}

// Get the first name and last name from the GET request
$first_name = $_GET['first_name'];
$last_name = $_GET['last_name'];

// Step 1: Fetch person_id from the persons table based on first_name and last_name
$sql = "SELECT person_id, father_name, mother_name FROM persons WHERE first_name = ? AND last_name = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $first_name, $last_name);
$stmt->execute();
$result = $stmt->get_result();
$row = $result->fetch_assoc();

if (!$row) {
    echo json_encode(["error" => "Person not found"]);
    exit();
}

$person_id = $row['person_id'];
$father_name = $row['father_name'];
$mother_name = $row['mother_name'];

// Step 2: Fetch family members (siblings, parents, spouses)
$familyMembers = getFamilyMembers($conn, $last_name);

// Step 3: Fetch parents (father and mother) for the given person
$parents = [];
if ($father_name) {
    // Split father's name into first and last name
    $father_parts = splitName($father_name);
    $father_first_name = $father_parts['first_name'];
    $father_last_name = $father_parts['last_name'];

    // Fetch father details
    $sql = "SELECT first_name, last_name FROM persons WHERE first_name = ? AND last_name = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $father_first_name, $father_last_name);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($row = $result->fetch_assoc()) {
        $parents[] = [
            'first_name' => $row['first_name'],
            'last_name' => $row['last_name'],
            'relationship' => 'Father'
        ];
    }
}

if ($mother_name) {
    // Split mother's name into first and last name
    $mother_parts = splitName($mother_name);
    $mother_first_name = $mother_parts['first_name'];
    $mother_last_name = $mother_parts['last_name'];

    // Fetch mother details
    $sql = "SELECT first_name, last_name FROM persons WHERE first_name = ? AND last_name = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $mother_first_name, $mother_last_name);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($row = $result->fetch_assoc()) {
        $parents[] = [
            'first_name' => $row['first_name'],
            'last_name' => $row['last_name'],
            'relationship' => 'Mother'
        ];
    }
}

// Step 4: Fetch grandparents (paternal and maternal)
$grandparents = getGrandparents($conn, $father_name, $mother_name);

// Step 5: Add relationships (siblings, parents, spouses) to the response
$relationships = [];
$familyMemberCount = count($familyMembers);

// Loop through all family members and check all combinations
for ($i = 0; $i < $familyMemberCount; $i++) {
    $familyMember = $familyMembers[$i];
    $relative_first_name = $familyMember['first_name'];
    $relative_last_name = $familyMember['last_name'];
    $relative_id = $familyMember['person_id'];

    // Fetch relationship details
    $relationshipDetails = getRelationshipDetails($conn, $person_id, $relative_id);
    
    // Add relationship to the list
    foreach ($relationshipDetails as $detail) {
        $relationships[] = [
            'person_1' => $first_name . ' ' . $last_name,
            'person_2' => $relative_first_name . ' ' . $relative_last_name,
            'relationship_type' => $detail['relationship_type'],
            'details' => $detail['other_details']
        ];
    }
}

// Final response construction
$response = [];
$response['person'] = [
    'first_name' => $first_name,
    'last_name' => $last_name
];

// Family Members
$response['family_members'] = $familyMembers;

// Parents
$response['parents'] = $parents;

// Grandparents
$response['grandparents'] = $grandparents;

// Relationships
$response['relationships'] = $relationships;

echo json_encode($response);

// Close the connection
$conn->close();
?>
