#!/usr/bin/env python3

''' Copyright 519seven 2020
    Input: private key in pkcs1 or 8 format
    Output: decimal representations of key data
            HEX if --hex is passed in           '''

import argparse
import sys
from Crypto.PublicKey import RSA

def get_args():
    ''' Build argument object '''

    parser = argparse.ArgumentParser(
        description='RSA key examiner'
        )

    parser.add_argument(
        '-k',
        '--key',
        help='Specify the location of your RSA key',
        required=True
        )
    parser.add_argument(
        '-p',
        '--passphrase',
        help='Supply your key passphrase',
        default=None
        )
    parser.add_argument(
        '--hex',
        dest='hex',
        help='Output in HEX? (default is decimal)',
        action='store_true'
        )
    return parser.parse_args()

if __name__ == '__main__':
    args = get_args()
    passphrase = args.passphrase
    public_key = RSA.importKey(open(args.key, 'r').read(), passphrase)
    if args.hex:
        print(f"n: {hex(public_key.n)}")
        print(f"e: {hex(public_key.e)}")
        print(f"d: {hex(public_key.d)}")
        print(f"p: {hex(public_key.p)}")
        print(f"u: {hex(public_key.u)}")
        sys.exit(0)
    print(f"n: {public_key.n}")
    print(f"e: {public_key.e}")
    print(f"d: {public_key.d}")
    print(f"p: {public_key.p}")
    print(f"u: {public_key.u}")
    sys.exit(0)
