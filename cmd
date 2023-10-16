| tr -d '",' | awk -F ': ' '/uri/ {print $2}' | awk -F'/' '{print $2}' | sort -V | tail -n 1

| sed -nE '/"uri": "\/([0-9.]+)"/ s//\1/p' | awk -F'/' '{print $2}' | sort -V | tail -n 1

| grep -o '"uri": "/[0-9.]\+"' | sed 's/"uri": "\(\/[^"]\+\)"/\1/' | sort -V | tail -n 1
