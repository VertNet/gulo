#!/usr/bin/awk -f
BEGIN {
x=0
FS="\x09"
}
$0 ~ "\xEF" { x=x+1; print $0 }
END { print x " lines have a Unicode encoding problem (an exposed \xEF character)." }
