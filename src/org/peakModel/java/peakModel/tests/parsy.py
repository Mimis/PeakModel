#!/usr/bin/env python

# Find terms that distinguish various novels by Charles Dickens.
# Note: if the w parameter is set wisely, no stop list is needed.

from nltk.tokenize import word_tokenize # Tokenizer
from weighwords import *
import logging
import numpy as np
import re
import os, sys

logger = logging.getLogger(__name__)
logging.basicConfig(level=logging.INFO)


top_k = 30  # How many terms per book to retrieve
#background = word_tokenize(sys.argv[1])
#foreground = word_tokenize(sys.argv[2])
background = word_tokenize(open(sys.argv[1]).read())
foreground = word_tokenize(open(sys.argv[1]).read())

print foreground

model = ParsimoniousLM([background], w=.01)

print("Top %d words:" % (top_k))
for term, p in model.top(top_k, foreground):
    print("    %s %.4f" % (term, np.exp(p)))
print("")




