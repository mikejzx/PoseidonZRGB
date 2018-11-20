import StringIO
import re
import select
import socket
import sys
import threading

class IPC(threading.Thread):

    def __init__(self, line_filter = None):
        print ("\nInitialise")
        threading.Thread.__init__(self)
        self.daemon = True
        self.lock = threading.Lock()
        self.event = threading.Event()
        self.event.clear()
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.recv_buffer_size = 8192
        self.buffer = StringIO.StringIO()
        if(line_filter == None):
            self.line_filter = lambda x: x
        else:
            self.line_filter = line_filter


    def run(self):
        print ("\nRun")
        self.sock.connect(("localhost", 6969))
        data = True
        while data:
            try:
                data = self.sock.recv(self.recv_buffer_size)
            except socket.error, e:
                print e
                self.sock.close()
                break

            self.lock.acquire()
            self.buffer.write(data)
            self.lock.release()
            self.event.set()

    def readlines(self):
        print ("\nReadlines")
        self.lock.acquire()
        self.buffer.seek(0)
        raw_lines = self.buffer.readlines()
        self.buffer.truncate(0)
        self.lock.release()

        lines = map(self.line_filter, raw_lines)
        return lines

proc_control = IPC()
while True:
    proc_control.event.wait()
    data = proc_control.readlines()
    if(data):
        print("\nRECEIVED")

    proc_control.event.clear()