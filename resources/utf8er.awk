#!/usr/bin/awk -f
# Prepends a three-byte (three bytes \xEF\xBB\xBF) 
# Byte Order Marker (BOM) to a file to explicitly
# show UTF8 encoding. 
# Assumptions:
#   Original file is actually UTF8-encoded.
# Example:
#   gawk -f utf8er.awk source.csv dest_with_bom.csv

BEGIN {
#out_file = "utf8out.csv"
#FS=","
x=0
}
{ if ( x==0 && $0 !~ /^\xEF\xBB\xBF/ ) { x=x+1; print "\xEF\xBB\xBF"$0 }
  else { x=x+1; print $0 }
} 
END {}
#{ print "Unicode BOM added to first line of file of " x " lines." }