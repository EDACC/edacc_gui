CC=gcc
#CFLAGS=-O2 -W -Wall -Wextra
#for debugging
CFLAGS= -g3 -ggdb -W -Wall -Wextra
all: client

clean:
	rm -f *.o
	rm -f *~
	rm -f client
	rm -f log
	rm -f sparrow
	rm -f testinstance
	rm -f *.cnf

# Static build causes many problems. The most serious problem is, that the default glibc 
# can only be dynamical linked, so mysql may won't work.
client: client.o log.o signals.o safeio.o database.o configuration.o dbmgr.o md5sum.o
#uncomment next line for use on regular linux machine	
	#$(CC) $(CFLAGS) *.o -o client -pthread `mysql_config --cflags` `mysql_config --libs`
#uncomment next line to compile on the bwgrid
	$(CC) $(CFLAGS) *.o -o client -pthread -L ./mysql/lib -Xlinker -Bstatic -lmysqlclient -lm -lz -Xlinker -Bdynamic
	#$(CC) $(CFLAGS) *.o -o client -pthread -L./lib -lmysqlclient -lz -lcrypt -lnsl -lm -L./lib -lssl -lcrypto


client.o: client.c global.h log.h signals.h database.h configuration.h safeio.h md5sum.h
	$(CC) $(CFLAGS) -c $<

log.o: log.c log.h
	$(CC) $(CFLAGS) -c $<

signals.o: signals.c signals.h global.h log.h
	$(CC) $(CFLAGS) -c $<

safeio.o: safeio.c safeio.h
	$(CC) $(CFLAGS) -c $<


database.o: database.c database.h global.h log.h
	$(CC) $(CFLAGS) -c $<

configuration.o: configuration.c configuration.h log.h safeio.h
	$(CC) $(CFLAGS) -c $<

dbmgr.o: dbmgr.c dbmgr.h
	$(CC) $(CFLAGS) -c $<

md5sum.o: md5sum.c md5sum.h
	$(CC) $(CFLAGS) -c $<


.PHONY: all clean

