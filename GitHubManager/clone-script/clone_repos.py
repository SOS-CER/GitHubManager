import os

def my_range(start, end, step):
    while start <= end:
        yield start
        start += step

        
#Modify these as appropriate for the org, class, and section info
#for a given semester
SECTION = "002"
CLASS = "csc316-"
ORG = "engr-csc316-fall2017"
ASSIGNMENT = "P2"
NUMSTUDENTS = 80

#put in the starting and (ending +1) numbers of the repos
#note there is an if statement for handling repos 1-9 by
#adding a leading 0, but it is untested
for x in my_range(1, NUMSTUDENTS,1):
   if x < 10:
      print "git clone git@github.ncsu.edu:%s/%s%s-%s-0%d.git" %(ORG, CLASS, SECTION, ASSIGNMENT, x)
      os.system("git clone git@github.ncsu.edu:%s/%s%s-%s-0%d.git" %(ORG, CLASS, SECTION, ASSIGNMENT, x))
   elif 10 <= x < 100:
      os.system("git clone git@github.ncsu.edu:%s/%s%s-%s-%d.git" %(ORG, CLASS, SECTION, ASSIGNMENT, x))
   else:
      os.system("git clone git@github.ncsu.edu:%s/%s%s-%s-%d.git" %(ORG, CLASS, SECTION, ASSIGNMENT, x))


