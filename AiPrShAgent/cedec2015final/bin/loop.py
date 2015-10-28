#!/usr/bin/python
import sys
import time
import datetime
import locale
from datetime import date
import subprocess

argvs = sys.argv
if(len(argvs) < 3):
    print "Usage: python %s command interval" % argvs[0]
    quit()
command = argvs[1]
interval = int(argvs[2])


limit = datetime.datetime.strptime("2015-08-21 12:00:00", '%Y-%m-%d %H:%M:%S')
print limit
while datetime.datetime.now() < limit:
    print datetime.datetime.now()
    print command
    subprocess.call(command, shell=True)
    print datetime.datetime.now()
    print "Finish"
    time.sleep(interval)
        

