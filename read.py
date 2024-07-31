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
        if ins == b'E':
            print(f"E {struct.unpack('>ffffffffh', f.read(34))}")
        if ins == b'S':
            print(f"S {struct.unpack('>fffffffffh', f.read(38))}")
        if ins == b'B':
            count = f.read(1)
            print(f"B{count} {struct.unpack('>ffffff', f.read(24))} [{len(f.read((count - 2) * 12))}...] {struct.unpack('>ffffffh', f.read(26))}")
        if ins == b'C':
            print(f"C {struct.unpack('>ffffffffh', f.read(34))}")
        if ins == b'Y':
            print(f"Y {struct.unpack('>ffffffffh', f.read(34))}")

    f.close()


if __name__ == "__main__":
    read_file(open(sys.argv[1], "rb"))
