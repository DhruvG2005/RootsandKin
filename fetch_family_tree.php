<?php
header('Content-Type: application/json');

// Database configuration
$servername = "192.168.0.103";
$username = "root"; // Your database username
$password = "dHRUV20@"; // Your database password
$dbname = "genealogy"; // Your database name

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get first_name and last_name from the request
$first_name = $_GET['first_name'] ?? null;
$last_name = $_GET['last_name'] ?? null;

if ($first_name && $last_name) {
    // Query to get the person_id from the persons table
    $personQuery = "
        SELECT person_id 
        FROM persons 
        WHERE first_name = ? AND last_name = ?
    ";
    $stmt = $conn->prepare($personQuery);
    $stmt->bind_param("ss", $first_name, $last_name);
    $stmt->execute();
    $personResult = $stmt->get_result();

    if ($personResult->num_rows > 0) {
        $personRow = $personResult->fetch_assoc();
        $person_id = $personRow['person_id'];

        // Now that we have the person_id, fetch the family details
        $familyQuery = "
            SELECT f.family_id, f.family_name, fm.role
            FROM family_members fm
            JOIN families f ON fm.family_id = f.family_id
            WHERE fm.person_id = ?
        ";
        $stmt = $conn->prepare($familyQuery);
        $stmt->bind_param("i", $person_id);
        $stmt->execute();
        $familyResult = $stmt->get_result();

        $familyTree = [];
        while ($family = $familyResult->fetch_assoc()) {
            $familyId = $family['family_id'];

            // Query to get all family members of the family
            $membersQuery = "
                SELECT p.person_id, p.first_name, p.last_name, p.gender, fm.role
                FROM family_members fm
                JOIN persons p ON fm.person_id = p.person_id
                WHERE fm.family_id = ?
            ";
            $stmt2 = $conn->prepare($membersQuery);
            $stmt2->bind_param("i", $familyId);
            $stmt2->execute();
            $membersResult = $stmt2->get_result();

            $members = [];
            while ($member = $membersResult->fetch_assoc()) {
                // For each member, fetch their relationships
                $relationshipsQuery = "
                    SELECT r.relationship_type, p2.person_id, p2.first_name, p2.last_name, p2.gender
                    FROM relationships r
                    JOIN persons p1 ON r.person_id_1 = p1.person_id
                    JOIN persons p2 ON r.person_id_2 = p2.person_id
                    WHERE r.person_id_1 = ?
                ";
                $stmt3 = $conn->prepare($relationshipsQuery);
                $stmt3->bind_param("i", $member['person_id']);
                $stmt3->execute();
                $relationshipsResult = $stmt3->get_result();

                $relationships = [];
                while ($relationship = $relationshipsResult->fetch_assoc()) {
                    $relationships[] = [
                        "relationship_type" => $relationship['relationship_type'],
                        "related_person" => [
                            "person_id" => $relationship['person_id'],
                            "first_name" => $relationship['first_name'],
                            "last_name" => $relationship['last_name'],
                            "gender" => $relationship['gender'],
                        ]
                    ];
                }

                $members[] = [
                    "person_id" => $member['person_id'],
                    "first_name" => $member['first_name'],
                    "last_name" => $member['last_name'],
                    "gender" => $member['gender'],
                    "role" => $member['role'],
                    "relationships" => $relationships
                ];
            }

            $familyTree[] = [
                "family_id" => $family['family_id'],
                "family_name" => $family['family_name'],
                "role" => $family['role'],
                "members" => $members
            ];
        }

        echo json_encode($familyTree);
    } else {
        echo json_encode(["error" => "Person not found"]);
    }
} else {
    echo json_encode(["error" => "First name and last name not provided."]);
}

$conn->close();
?>
