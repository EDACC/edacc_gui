CC=gcc
CFLAGS=-O2 -W -Wall -Wextra

all: client

clean:
	rm -f *.o
	rm -f *~
	rm -f client

client: client.o log.o signals.o safeio.o mutex.o database.o configuration.o dbmgr.o md5sum.o
	$(CC) $(CFLAGS) *.o -o client -static -pthread `mysql_config --cflags` `mysql_config --libs`

client.o: client.c global.h log.h signals.h database.h configuration.h safeio.h mutex.h md5sum.h
	$(CC) $(CFLAGS) -c $<

log.o: log.c log.h
	$(CC) $(CFLAGS) -c $<

signals.o: signals.c signals.h global.h log.h
	$(CC) $(CFLAGS) -c $<

safeio.o: safeio.c safeio.h
	$(CC) $(CFLAGS) -c $<

mutex.o: mutex.c mutex.h
	$(CC) $(CFLAGS) -c $<

database.o: database.c database.h global.h log.h
	$(CC) $(CFLAGS) -c $<

configuration.o: configuration.c configuration.h
	$(CC) $(CFLAGS) -c $<

dbmrg.o: dbmrg.c dbmrg.h
	$(CC) $(CFLAGS) -c $<

md5sum.o: md5sum.c md5sum.h
	$(CC) $(CFLAGS) -c $<


.PHONY: all clean

