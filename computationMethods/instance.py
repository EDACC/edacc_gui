#!/usr/bin/python
import sys
def print_help():
  print "Instance Property Parser"
  print "------------------------"
  print ""
  print "Expects an cnf formula on stdin."
  print "Usage: instance.py [Option]"
  print " Option:"
  print "  --help         prints this help"
  print "  --variables    prints #variables"
  print "  --clauses      prints #clauses"
  print "  --min          prints min clause length"
  print "  --max          prints max clause length"
  print "  --c{1,2,3,4,5} prints #clauses of size {1,2,3,4,5}"
  print "  --cg6          prints #clauses of size >= 6"
  
def read_header():
  try:
    while 1:
      header = raw_input().split()
      if len(header) < 1 or header[0] != "c":
        break
    if len(header) != 4 or header[0] != "p" or header[1] != "cnf":
      return False
    return int(header[2]), int(header[3])
  except (EOFError):
    return False

def read_clause():
  try:
    clause = raw_input().split()
    if len(clause) == 0 or clause[len(clause)-1] != "0":
      return False
    return clause[:len(clause)-1]
  except (EOFError):
    return False
if len(sys.argv) > 2 or len(sys.argv) == 1:
  print_help()
  exit()

arg = sys.argv[1]

if arg == "--help":
  print_help()
elif arg == "--variables":
  print read_header()[0]
elif arg == "--clauses":
  print read_header()[1]
elif arg == "--min":
  read_header()
  res = 2147483647
  while (True):
    clause = read_clause()
    if clause == False:
      break
    if len(clause) < res:
      res = len(clause)
  print res
elif arg == "--max":
  read_header()
  res = 0
  while (True):
    clause = read_clause()
    if clause == False:
      break
    if len(clause) > res:
      res = len(clause)
  print res
elif arg == "--cg6":
  read_header()
  res = 0
  while (True):
    clause = read_clause()
    if clause == False:
      break
    if len(clause) >= 6:
      res += 1
  print res
else:
  if arg[:3] == "--c":
    c = int(arg[3:])
    read_header()
    res = 0
    while (True):
      clause = read_clause()
      if clause == False:
        break
      if len(clause) == c:
        res += 1
    print res   
  else:
    print_help()
