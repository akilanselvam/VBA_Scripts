| grep -o '"uri": "/[0-9.]\+"' | sed 's/"uri": "\(\/[^"]\+\)"/\1/' | sort -V | tail -n 1
