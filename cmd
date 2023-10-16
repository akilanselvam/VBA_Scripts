# Retrieve the JSON data using Invoke-WebRequest
$jsonResponse = (Invoke-WebRequest -Uri "").Content

# Parse JSON using ConvertFrom-Json
$jsonData = $jsonResponse | ConvertFrom-Json

# Extract the latest version
$latestVersion = $null
foreach ($child in $jsonData.children) {
    if ($child.uri -match '/\d+\.\d+\.\d+/') {
        $version = $child.uri -replace '/', ''
        if ($latestVersion -eq $null -or [Version]::new($version) -gt [Version]::new($latestVersion)) {
            $latestVersion = $version
        }
    }
}

# Output the latest version
Write-Host "Latest Version: $latestVersion"

| tr -d '",' | awk -F ': ' '/uri/ {print $2}' | awk -F'/' '{print $2}' | sort -V | tail -n 1

| sed -nE '/"uri": "\/([0-9.]+)"/ s//\1/p' | awk -F'/' '{print $2}' | sort -V | tail -n 1

| grep -o '"uri": "/[0-9.]\+"' | sed 's/"uri": "\(\/[^"]\+\)"/\1/' | sort -V | tail -n 1
