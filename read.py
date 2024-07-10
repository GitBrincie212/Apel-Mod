#!/usr/bin/python3
import struct
import sys

def read_file(f):
    while True:
        ins = f.read(1)
        if not ins:
            break
        if ins == b'F':
            print(f"F {struct.unpack('>fff', f.read(12))}")
        if ins == b'T':
            print(f"T {struct.unpack('>B', f.read(1))}")
        if ins == b'P':
            print(f"P {struct.unpack('>fff', f.read(12))}")
        if ins == b'L':
            print(f"L {struct.unpack('>ffffffh', f.read(26))}")
    f.close()


if __name__ == "__main__":
    read_file(open(sys.argv[1], "rb"))
