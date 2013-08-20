"""
This script takes a directory of harvested resources - stored as CSV files - as input,
splits them into files of 10,000 lines each, then uploads the split files to Google
Cloud Storage using gsutil. This script is invoked at the end of the Clojure harvesting
process.

It requires gsutil to be installed so it can be called with 'subprocess.check_call'.
"""

import sys
import os
import subprocess
import glob

LINE_COUNT = 10000 # split files after this many lines

def get_subdirs(base_path):
	"""Given a path, get list of sub-paths."""

	return [os.path.join(base_path, d) for d in os.listdir(base_path) 
			if os.path.isdir(os.path.join(base_path, d))]

def invoke_split(path, dry_run=True):
	"""Call split on all CSV files via subprocess."""

	print "\n____________________"
	print "Splitting CSV files in %s" % path

	cmd = "cat %s/*.csv | split -l %s - %s/" % (path, LINE_COUNT, path)

	if dry_run:
		print "\n<--Dry run-->"
		print "Command:"
		print cmd
	else:
		subprocess.check_call(cmd, shell=True) # shell=True for * expansion

	return

def delete_csv_files(path, dry_run=True):
	"""Delete all CSV files in directory."""

	csvs = glob.glob("%s/*.csv" % path)

	print "\n____________________"
	print "Deleting CSV files: \n%s" % "\n".join(csvs)

	if not dry_run:
		map(os.remove, csvs)
	else:
		print "\n<--Dry run-->"
		print "Command:"
		print "\n".join(["os.remove('%s')" % path for path in csvs])
	return

def copy_to_gs(local_base_dir, gs_base_path, dry_run=True):
	"""Invoke gsutil -m cp -R dir gs://my_bucket to recursively upload
	all directories w/multi-threading turned on."""

	print "\n____________________"
	print "Copying to Google Cloud Storage:"
	print local_base_dir

	cmd = "gsutil -m cp -R %s %s" % (local_base_dir, gs_base_path)

	if not dry_run:
		status = subprocess.check_call(cmd.split())
	else:
		print "\n<--Dry run-->"
		print "Command:"
		print cmd
		status = 0

	if status == 0:
		print
		print "Directory successfully copied to Google Cloud Storage:"
		print gs_base_path
	
	return status

def process_dir(path, dry_run=True):
	"""Split then delete the CSV files in a directory."""
	invoke_split(path, dry_run=dry_run)
	delete_csv_files(path, dry_run=dry_run)

	return

def main(base_path, gs_base_path, dry_run=True):
	if dry_run:
		print "This is a dry run. Nothing will be split, deleted, or uploaded."
		print "_______________________________________________________________"

        process_dir(base_path, dry_run=dry_run)

	copy_to_gs(base_path, gs_base_path, dry_run=dry_run)

	return

if __name__ == "__main__":
	
	base_path = sys.argv[1]
	gs_base_path = sys.argv[2]

	main(base_path, gs_base_path, dry_run=False)
