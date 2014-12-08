__author__ = 'bingo4508'
from evaluate import *
import sys

if __name__ == '__main__':
    fn = sys.argv[-1]

    if '-d' in sys.argv:
        output_final_degree_hist(fn)
    elif '-n' in sys.argv:
        start = sys.argv.index('-n')+1
        output_final_attr_hist(fn, int(sys.argv[start]), int(sys.argv[start+1]))
