data=$(curl -s "")

# Extract the latest version
latest_version=""
while read -r line; do
  if [[ $line == *"\"uri\": \"/"* && $line != *"maven-metadata.xml"* ]]; then
    version="${line##*/}"
    if [[ -z $latest_version || "$version" > "$latest_version" ]]; then
      latest_version="$version"
    fi
  fi
done <<< "$data"

echo "Latest Version: $latest_version"

| tr -d '",' | awk -F ': ' '/uri/ {print $2}' | awk -F'/' '{print $2}' | sort -V | tail -n 1

| sed -nE '/"uri": "\/([0-9.]+)"/ s//\1/p' | awk -F'/' '{print $2}' | sort -V | tail -n 1

| grep -o '"uri": "/[0-9.]\+"' | sed 's/"uri": "\(\/[^"]\+\)"/\1/' | sort -V | tail -n 1
