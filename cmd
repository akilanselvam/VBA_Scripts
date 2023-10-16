curl -s "https://artifactory.global.standardchartered.com/artifactory/api/storage/maven-sc-release_local/rcb/atlas/build/rcb-atlas-codes-domain" | sed -nE '/"uri": "\/([0-9.]+)"/ s//\1/p' | awk -F'/' '{print $2}' | sort -V | tail -n 1

| grep -o '"uri": "/[0-9.]\+"' | sed 's/"uri": "\(\/[^"]\+\)"/\1/' | sort -V | tail -n 1
